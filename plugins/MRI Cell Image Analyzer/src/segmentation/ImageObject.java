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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import operations.image.CopyImageOperation;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.measure.ResultsTable;

/**
 * An image object represents a meaningful part of a scene. 
 * It has sub-objects and a parent object. It can access its 
 * neighbors. 
 * 
 * @author Volker Bäcker
 */
public class ImageObject {
	
	protected Roi roi;
	protected ArrayList<Node<ImageObject>> neighbors;
	protected ImagePlus objectImage;
	protected float lastMaxDistance = -1;
	protected Node<ImageObject> node;
	protected Scene scene;
	protected String name;
	protected Measurements measurements;
	
	public ImageObject(Scene aScene, Roi roi) {
		this.roi = roi;
		this.node = new Node<ImageObject>(this);
		scene = aScene;
		neighbors = new ArrayList<Node<ImageObject>>();
		name = "unnamed";
	}

	public ImageObject(ImagePlus anImage) {
		this.roi = new Roi(0,0,anImage.getWidth(), anImage.getHeight());
		this.node = new Node<ImageObject>(this);
		scene = new Scene();
		scene.setImage(anImage);
		scene.setRoot(this);
		neighbors = new ArrayList<Node<ImageObject>>();
		name = "unnamed";
	}
	
	public ImagePlus getObjectImage() {
		if (this.objectImage==null) createObjectImage();
		return objectImage;
	}

	public void createObjectImage() {
		ImagePlus image = scene.getImage();
		Roi oldRoi = image.getRoi();
		image.setRoi(roi);
		CopyImageOperation copy = new CopyImageOperation();
		copy.setShowResult(false);
		copy.setInputImage(image);
		copy.run();
		objectImage = copy.getResult();
		image.setRoi(oldRoi);
	}

	public Rectangle bounds() {
		return roi.getBounds();
	}
	
	public ArrayList<Node<ImageObject>> getNeighborsWithMaxDistance(int maxDistance) {
		if (maxDistance!=lastMaxDistance) {
			lastMaxDistance = maxDistance;
			findNeighborsWithMaxDistance(maxDistance);
		}
		return neighbors; 
	}

	public void findNeighborsWithMaxDistance(int maxDistance) {
		 neighbors = node.getParent().getContent().findNeighborsTo(this, maxDistance);
	}
	
	public void findNeighborsForAllCildren(int maxDistance) {
		Iterator<Node<ImageObject>> it = node.getChildren().iterator();
		while(it.hasNext()) {
			ImageObject child = it.next().getContent();
			child.findNeighborsWithMaxDistance(maxDistance);
		}
	}

	protected ArrayList<Node<ImageObject>> findNeighborsTo(ImageObject object, int maxDistance) {
		Roi objectRoi = (Roi) object.getRoi().clone();
		ImagePlus image = getImage();
		Roi oldRoi = image.getRoi();
		image.setRoi(objectRoi);
		WindowManager.setTempCurrentImage(image);
		IJ.run("Enlarge...", "enlarge=" + maxDistance);
		objectRoi = image.getRoi();
		ShapeRoi objectShape = new ShapeRoi(objectRoi);
		ArrayList<Node<ImageObject>> neighbors = new ArrayList<Node<ImageObject>>();
		Iterator<Node<ImageObject>> it = node.getChildren().iterator();
		while(it.hasNext()) {
			Node<ImageObject> childNode = it.next();
			if (childNode.getContent()==object) continue;
			Roi childRoi = (Roi) childNode.getContent().getRoi().clone();
			ShapeRoi childShape = new ShapeRoi(childRoi);
			childShape.and(objectShape);
			if (childShape.getLength()>=maxDistance) {
				neighbors.add(childNode);
			}
			
		}
		WindowManager.setTempCurrentImage(null);
		image.setRoi(oldRoi);
		return neighbors;
	}

	public Node<ImageObject> getNode() {
		return node;
	}
	
	public void createSubObjects(ImagePlus mask, ResultsTable measurements) {
		int x, y;
		for (int i=0; i<measurements.getCounter(); i++) {
			x = (int)measurements.getValue("XStart", i);
			y = (int)measurements.getValue("YStart", i);
			if (!this.roi.getPolygon().contains(x, y)) continue;
			WindowManager.setTempCurrentImage(mask);
			IJ.doWand(x, y);
			Roi roi = mask.getRoi();
			ImageObject imageObject = new ImageObject(scene, roi);
			imageObject.setName(imageObject.getName() + " - " + (i+1));
			node.addChild(imageObject.getNode());
		}
	}

	public int numberOfObjects() {
		return node.getChildren().size();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		String result = name;
		if (measurements!=null) result += " " + measurements.getMeasurementsString();
		return result;
	}
	
	public Roi getRoi() {
		return roi;
	}
	
	public ImageObject getRoot() {
		return scene.getRoot();
	}
	
	public ImagePlus getImage() {
		return scene.getImage();
	}

	public void measure() {
		this.measurements = new Measurements(this);
		measurements.run();
	}

	public Measurements getMeasurements() {
		if (measurements==null) measure();
		return measurements;
	}
}
