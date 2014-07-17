/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2008 INSERM and CNRS
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging, 
 * by Volker Bäcker
 * www.mri.cnrs.fr
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package segmentation;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.macro.Interpreter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Observable;

import operations.analysis.AnalyzeParticlesOperation;

/**
 * The image object explorer allows to create and manage a number of image objects.
 * 
 * @author Volker Bäcker
 *
 */
public class ImageObjectExplorer extends Observable implements Comparator<Node<ImageObject>> {
	protected ImageObject root;
	protected ImageObjectExplorerView view;
	protected boolean hasChanges = false;
	protected ImageObjectExplorerOptions options;

	public ImageObjectExplorer() {
		options = new ImageObjectExplorerOptions();
	}
	
	public void show() {
		this.getView().setVisible(true);
	}
	public ImageObjectExplorerView getView() {
		if (view == null) view = new ImageObjectExplorerView(this);
		return view;
	}
	public ImageObject getRoot() {
		return root;
	}

	public void setRoot(ImageObject root) {
		this.root = root;
	}
	
	public void setView(ImageObjectExplorerView view) {
		this.view = view;
	}

	static public void openNew() {
		ImageObjectExplorer explorer = new ImageObjectExplorer();
		explorer.show();
	}
	
	public Node<ImageObject> getRootNode() {
		if (root!=null) 
			return root.getNode(); 
		else
			return null;
	}
	
	public boolean isEmpty() {
		return root==null;
	}
	
	public void createNewObjectTree(ImagePlus image) {
		Scene aScene = new Scene();
		Roi aRoi = new Roi(0, 0, image.getWidth(), image.getHeight());
		root = new ImageObject(aScene, aRoi);
		aScene.setImage(image);
		aScene.setRoot(root);
		root.setName(image.getTitle());
		this.setHasChanges(true);
		this.changed("object tree");
	}
	
	public void setHasChanges(boolean value) {
		this.hasChanges = value;
	}
	
	public boolean hasChanges() {
		return hasChanges ;
	}
	public void save() {
		if (!hasChanges()) return;
		// TODO
	}
	
	public void changed(String string) {
		this.setChanged();
		this.notifyObservers(string);
		this.clearChanged();
	}
	
	public void createSubObjectsFromMask(ImagePlus mask, ImageObject object) {
		AnalyzeParticlesOperation op = new AnalyzeParticlesOperation();
		boolean wasBatchMode = Interpreter.isBatchMode();
		op.setShowResult(false);
		op.setRecordStarts(true);
		op.setInputImage(mask);
		boolean wasVisible = mask.isVisible();
		op.getInterpreter().setBatchMode(true);
		mask.show();
		op.run();
		if (!wasVisible) mask.hide();
		op.getInterpreter().setBatchMode(wasBatchMode);
		object.createSubObjects(mask, op.getMeasurements());
		this.setHasChanges(true);
	}
	
	public void findNeighbors(ArrayList<Node<ImageObject>> selectedNodes, int maxDist) {
		Iterator<Node<ImageObject>> it = selectedNodes.iterator();
		while (it.hasNext()) {
			Node<ImageObject> node = it.next();
			node.getContent().findNeighborsWithMaxDistance(maxDist);
		}
	}
	
	public void measureChildren(Node<ImageObject> node) {
		Iterator<Node<ImageObject>> it = node.getChildren().iterator();
		while(it.hasNext()) {
			Node<ImageObject> child = it.next();
			child.getContent().measure();
		}
		this.changed("object tree");
		this.setHasChanges(true);
	}
	
	public void selectObjects(ArrayList<Node<ImageObject>> nodes) {
		root.getImage().killRoi();
		Iterator<Node<ImageObject>> it = nodes.iterator();
		ShapeRoi combinedRoi = new ShapeRoi(nodes.get(0).getContent().getRoi());
		boolean first = true;
		while(it.hasNext()) {
			if (!first) {
				Roi aRoi = it.next().getContent().getRoi();
				combinedRoi.or(new ShapeRoi(aRoi));
			}
			first = false;
		}
		root.getImage().setRoi(combinedRoi);
	}

	public ImageObjectExplorerOptions getOptions() {
		return this.options;
	}

	public void sortChildren(Node<ImageObject> node) {
		Collections.sort(node.getChildren(), this);
		this.changed("object tree");
		this.setHasChanges(true);
	}

	public int compare(Node<ImageObject> aNode, Node<ImageObject> anOtherNode) {
		int result = 0;
		ArrayList<String> sortByMeasurements = this.getOptions().getSortByMeasurements();
		Iterator<String> it = sortByMeasurements.iterator();
		ImageObject anObject = aNode.getContent();
		ImageObject anOtherObject = anOtherNode.getContent();
		Measurements measurements = anObject.getMeasurements();
		Measurements otherMeasurements = anOtherObject.getMeasurements();
		int smaller = -1;
		if (this.getOptions().isSortDescending()) smaller = 1;
 		while(it.hasNext()) {
			String measurement = it.next();
			if (measurements.getMeasure(measurement) < otherMeasurements.getMeasure(measurement)) return smaller;
			if (measurements.getMeasure(measurement) > otherMeasurements.getMeasure(measurement)) return -1 * smaller;
		}
		return result;
	}	
}
