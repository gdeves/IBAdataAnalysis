/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2008 INSERM and CNRS
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging, 
 * by Volker B�cker
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
package objectModelingWorkbench;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.gui.Roi;
import ij.gui.Toolbar;
import ij.measure.ResultsTable;
import ij.text.TextWindow;
import ij3d.Image3DUniverse;
import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;
import operations.Operation;
import operations.analysis.MeasureOperation;
import operations.file.SaveImageOperation;
import operations.roi.FillOperation;
import tools.magicWand.MagicWand;
import utils.NumberUtil;

/**
 * The object 3D manager allows to create and manage objects. Selections on slices can be added to an object.
 * A 3D scence containing the objetcs can be create or the objects can be exported as image stacks.
 * 
 * @author Volker B�cker
 */
public class Object3DManager extends Observable implements KeyListener {
	protected ArrayList<Object3D> objects;
	protected ObjectModelingWorkbench model;
	protected boolean isShowObjects;
	protected int currentColorIndex = 0;
	protected ArrayList<Color> colors;
	protected int selectObjectIndex = 0;
	protected int autoObjectCounter = 0;
	private Object3DManagerView view;
	private Operation measureOperation;
	
	public Object3DManager(ObjectModelingWorkbench registrator) {
		super();
		this.model = registrator;
	}

	public void addObject(String name) {
		Object3D object = new Object3D(name);
		if (this.getObjects().contains(object)) return;
		object.setColor(this.nextColor());
		objects.add(object);
		Collections.sort(objects);
		this.changed("objects");
	}

	protected Color nextColor() {
		Color result = this.getColors().get(this.getCurrentColorIndex());
		currentColorIndex = (currentColorIndex + 1) % (this.getColors().size());
		return result;
	}

	public ArrayList<Color> getColors() {
		if (this.colors==null) this.setColors(this.getDefaultColors());
		return this.colors;
	}

	protected void setColors(ArrayList<Color> colors) {
		this.colors = colors;
		
	}

	protected ArrayList<Color> getDefaultColors() {
		ArrayList<Color> result = new ArrayList<Color>();
		result.add(Color.RED);
		result.add(Color.GREEN);
		result.add(Color.BLUE);
		result.add(Color.YELLOW);
		result.add(Color.MAGENTA);
		result.add(Color.CYAN);
		result.add(Color.ORANGE);
		result.add(Color.PINK);
		return result;
	}

	protected int getCurrentColorIndex() { 
		return currentColorIndex ;
	}

	public ArrayList<Object3D> getObjects() {
		if (objects==null) objects = new ArrayList<Object3D>();
		return objects;
	}

	public ArrayList<String> getObjectNames() {
		ArrayList<String> result = new ArrayList<String>();
		for (Object3D object : this.getObjects()) {
			result.add(object.getName());
		}
		return result;
	}
	
	public Vector<String> getObjectNamesWithIndications() {
		Vector<String> result = new Vector<String>();
		for (Object3D object : this.getObjects()) {
			String indication = "";
			if (!object.isVisible) indication = " (hidden)";
			result.add(object.getName() + indication);
		}
		return result;
	}
	
	public void setObjects(ArrayList<Object3D> objects) {
		this.objects = objects;
	}
	
	public void changed(String string) {
		this.setChanged();
		this.notifyObservers(string);
		this.clearChanged();
	}

	public void show() {
		this.getView().setVisible(true);
	}

	protected Object3DManagerView getView() {
		if (this.view==null) {
			view = new Object3DManagerView(this);
		}
		return view;
	}

	public void addSelectionsToObjects(int[] indices) {
		model.getView().updateRoi();
		int[] selectedSlices = model.getView().getSelectionIndices();
		for (int i=0; i<indices.length; i++) {
			Object3D object = (Object3D) this.getObjects().get(indices[i]);
			for (int j=0; j<selectedSlices.length; j++) {
				AlignmentSlice slice = (AlignmentSlice) model.getAlignments().getAlignments().get(selectedSlices[j]);
				Roi roi = slice.getRoi();
				if (roi == null) continue;
				object.addSelection(selectedSlices[j], roi);
			}
		}
		this.changed("selections");
		model.changed("showObjects");
		model.changed("display");
	}

	public void setSelectionToActiveImage(int objectIndex, int selectionIndex) {
		Object3D object = (Object3D) this.getObjects().get(objectIndex);
		Roi originalSelection = (Roi) object.getSelections().get(new Integer(selectionIndex));
		Roi selection = (Roi) originalSelection.clone();
		int offsetX = model.getFirstImageOffsetX();
		int offsetY = model.getFirstImageOffsetY();
		if (model.getSecondImage()!=null) {
			offsetX = model.getSecondImageOffsetX();
			offsetY = model.getSecondImageOffsetY();
		}
		if (IJ.getImage()!=model.getFirstImage() && IJ.getImage()!=model.getSecondImage()) {
			offsetX = 0;
			offsetY = 0;
		}
		AlignmentSlice slice = getCurrentSlice();
		AlignmentSlice selectionSlice = (AlignmentSlice) model.getAlignments().getAlignments().get(selectionIndex);
		offsetX -= selectionSlice.getXOffset();
		offsetY -= selectionSlice.getYOffset();
		if (selectionSlice==slice) {
			offsetX = 0;
			offsetY = 0;
		}
		selection.setLocation(selection.getBounds().x - offsetX, selection.getBounds().y - offsetY);
		IJ.getImage().setRoi(selection);
	}

	protected AlignmentSlice getCurrentSlice() {
		AlignmentSlice slice = model.firstImageSlice;
		if (model.getSecondImage()!=null) {
			slice = model.secondImageSlice;
		}
		return slice;
	}

	public void deleteSelections(int objectIndex, int[] selectionIndices) {
		if (objectIndex==-1) return;
		if (selectionIndices.length==0) return;
		((Object3D) this.getObjects().get(objectIndex)).deleteSelections(selectionIndices);
		this.changed("selections");
		model.changed("showObjects");
		model.changed("display");
	}

	public void renameObject(int index, String name) {
		Object3D object = this.getObjects().get(index);
		object.setName(name);
		Collections.sort(objects);
		this.changed("objects");
	}

	public void deleteObjects(int[] selectedIndices) {
		ArrayList<Object3D> objectsToDelete = new ArrayList<Object3D>();
		for (int i=0; i<selectedIndices.length; i++) {
			objectsToDelete.add(this.getObjects().get(selectedIndices[i]));
		}
		Iterator<Object3D> it = objectsToDelete.iterator();
		while (it.hasNext()) {
			Object3D object = it.next();
			this.getObjects().remove(object);
		}
		this.changed("objects");
		model.changed("showObjects");
		model.changed("display");
	}

	public boolean isShowObjects() {
		return isShowObjects;
	}

	public void setShowObjects(boolean isShowObjects) {
		this.isShowObjects = isShowObjects;
		model.changed("showObjects");
		model.changed("display");
	}

	public ImagePlus[] getImagesForFirstSlice() {
		int sliceNumber = model.getView().getSelectionIndices()[0];
		ImagePlus[] result = this.getImagesForSlice(sliceNumber);
		return result;
	}
	public ArrayList<Object3D> getObjectsForFirstSlice() {
		int sliceNumber = model.getView().getSelectionIndices()[0];
		ArrayList<Object3D> result = this.getObjectsForSlice(sliceNumber);
		return result;
	}
	
	public ArrayList<Object3D> getObjectsForSecondSlice() {
		int sliceNumber = model.getView().getSelectionIndices()[model.getView().getSelectionIndices().length-1];
		ArrayList<Object3D> result = this.getObjectsForSlice(sliceNumber);
		return result;
	}
	
	public ImagePlus[] getImagesForSecondSlice() {
		int sliceNumber = model.getView().getSelectionIndices()[model.getView().getSelectionIndices().length-1];
		ImagePlus[] result = this.getImagesForSlice(sliceNumber);
		return result;
	}
	
	protected ImagePlus[] getImagesForSlice(int sliceNumber) {
		FillOperation op = new FillOperation();
		op.setKeepSource(false);
		ArrayList<Object3D> objects = getObjectsForSlice(sliceNumber);
		ImagePlus[] result = new ImagePlus[objects.size()];
		int counter = 0;
		Iterator<Object3D> it;
		it = objects.iterator();
		while (it.hasNext()) {
			Object3D object = it.next();
			ImagePlus firstImage = model.getFirstImage();
			ImagePlus image = NewImage.createRGBImage(firstImage.getTitle()+"-object" + sliceNumber, 
					firstImage.getWidth(), firstImage.getHeight(), firstImage.getNSlices(), NewImage.FILL_BLACK);
			image.setImage(image.getProcessor().createImage());
			image.setRoi((Roi) object.getSelections().get(new Integer(sliceNumber)));
			Color oldColor = Toolbar.getForegroundColor();
			image.getProcessor().setColor(object.getColor());
			op.setInputImage(image);
			op.setShowResult(false);
			op.run();
			image.setColor(oldColor);	
			image.killRoi();
			result[counter] = image;
			counter++;
		}
		return result;
	}

	protected ArrayList<Object3D> getObjectsForSlice(int sliceNumber) {
		ArrayList<Object3D> allObjects = this.getObjects();
		ArrayList<Object3D> objects = new ArrayList<Object3D>();
		for (Object3D object : allObjects) {
			if (object.getSelections().containsKey(new Integer(sliceNumber))) {
				objects.add(object);
			}
		}
		return objects;
	}

	public void setObjectColor(int[] objectIndices, Color objectColor) {
		for (int i=0; i<objectIndices.length; i++) {
			Object3D object = (Object3D) this.getObjects().get(objectIndices[i]);
			object.setColor(objectColor);
		}
		
	}

	public void save(File aFile) {
		this.writeObjectNamesFile(aFile);
		ArrayList<Object3D> allObjects = this.getObjects();
		for(Object3D object : allObjects) {
			object.save(aFile.getParentFile());
		}
	}

	public void open(File aFile) {
		if (!aFile.exists()) return;
		this.setObjects(null);
		this.readObjectNamesFile(aFile);
		ArrayList<Object3D> allObjects = this.getObjects();
		for(Object3D object : allObjects) {
			object.load(aFile.getParentFile());
		}
		this.setAutoObjectCounter();
		this.changed("objects");
	}
	
	protected void setAutoObjectCounter() {
		int counter = 0;
		ArrayList<String> allObjectNames = this.getObjectNames();
		for(String name : allObjectNames) {
			int number = 0;
			try {
				number = Integer.parseInt(name);
			} catch (java.lang.NumberFormatException e) {
				// ignore
			}
			if (number>counter) counter = number;
		}
		this.autoObjectCounter = counter;	
	}

	protected void writeObjectNamesFile(File aFile) {
		FileWriter fileWriter;
		BufferedWriter out = null;
		try {
			fileWriter = new FileWriter(aFile);
			out = new BufferedWriter(fileWriter);
			this.printOn(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e1) {
					// ignore
				}
			}
		}
	}

	protected void readObjectNamesFile(File aFile) {
		FileReader fileReader;
		BufferedReader in = null;
		try {
			fileReader = new FileReader(aFile);
			in = new BufferedReader(fileReader);
			this.readFrom(in);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in!=null) {
				try {
					in.close();
				} catch (IOException e1) {
					// ignore
				}
			}
		}
	}
	
	protected void printOn(BufferedWriter out) throws IOException {
		ArrayList<String> allNames = this.getObjectNames();
		for(String name : allNames) {
			out.write(name + "\n");
		}
	}

	protected void readFrom(BufferedReader in) throws IOException {
		String line;
		while((line=in.readLine())!=null) {
			this.getObjects().add(new Object3D(line.trim()));
		}
	}

	public ImagePlus createStack(int[] selectedObjects, int[] selectedSlices) {
		Alignments alignments = new Alignments();
		ArrayList<AlignmentSlice> slices = new ArrayList<AlignmentSlice>();
		int numberOfSlices = 0;
		for (int i=selectedSlices[0]; i<=selectedSlices[selectedSlices.length-1]; i++) {
			slices.add(model.getAlignments().getAlignments().get(i));
			numberOfSlices++;
		}
		alignments.setAlignments(slices);
		Rectangle zeroAndImageSize = alignments.calculateImageSize();
		String name = getNameForStack(selectedObjects, selectedSlices);
		ImagePlus newImage = NewImage.createRGBImage(name, zeroAndImageSize.width, zeroAndImageSize.height, 
												   numberOfSlices, NewImage.FILL_BLACK);
		for (int i=0; i<selectedObjects.length; i++) {
			Object3D object = (Object3D) this.getObjects().get(selectedObjects[i]);
			object.paintSelectedSlicesOn(newImage, selectedSlices, slices, zeroAndImageSize);
		}
		return newImage;
	}

	protected String getNameForStack(int[] selectedObjects, int[] selectedSlices) {
		String name = "";
		for (int i=0; i<selectedObjects.length; i++) {
			name += this.getObjectNames().get(i);
			if (i<selectedObjects.length-1) name += "+";
		}
		name += "(" + (selectedSlices[0] + 1);
		if (selectedSlices.length>1) name += "-" + (selectedSlices[selectedSlices.length-1] + 1);
		name +=  ")";
		return name;
	}

	public void saveObjectsToSeries(File targetFile, int[] selectedObjects, int[] selectedSlices) {
		Alignments alignments = new Alignments();
		ArrayList<AlignmentSlice> slices = new ArrayList<AlignmentSlice>();
		SaveImageOperation save = new SaveImageOperation();
		int numberOfSlices = 0;
		for (int i=selectedSlices[0]; i<=selectedSlices[selectedSlices.length-1]; i++) {
			slices.add(model.getAlignments().getAlignments().get(i));
			numberOfSlices++;
		}
		HashSet<Integer> selectedSlicesMap = new HashSet<Integer>();
		for (int i=0; i<selectedSlices.length; i++) {
			selectedSlicesMap.add(new Integer(selectedSlices[i]));
		}
		alignments.setAlignments(slices);
		Rectangle zeroAndImageSize = alignments.calculateImageSize();
		for (int counter=0; counter<numberOfSlices; counter++) {
			int currentSlice=selectedSlices[counter];
			ImagePlus newImage = NewImage.createRGBImage(Integer.toString(currentSlice), zeroAndImageSize.width, zeroAndImageSize.height, 
					1, NewImage.FILL_BLACK);
			for (int currentObject=0; currentObject<selectedObjects.length; currentObject++) {
				Object3D object = (Object3D) this.getObjects().get(selectedObjects[currentObject]);
				if (!object.getSelections().keySet().contains(new Integer(currentSlice))) continue; 
				AlignmentSlice slice = (AlignmentSlice) model.getAlignments().getAlignments().get(currentSlice);
				object.paintSliceOnImage(slice, currentSlice, newImage, zeroAndImageSize);
			}
			String nameIndex = "" + (new Integer(currentSlice));
			String originalNameIndex = nameIndex + "";
			for (int i=0; i<5-(originalNameIndex.length()); i++) {
				nameIndex = "0" + nameIndex;
			}
			save.setInputImage(newImage);
			String fileName = targetFile.getName().split("\\.")[0];
			save.setPath(targetFile.getParent() + File.separator + fileName + nameIndex + ".tif");
			save.run();
		}
	}

	public void saveObjectsTo8BitSeries(File targetFile, int[] selectedObjects, int[] selectedSlices) {
		Alignments alignments = new Alignments();
		ArrayList<AlignmentSlice> slices = new ArrayList<AlignmentSlice>();
		SaveImageOperation save = new SaveImageOperation();
		int numberOfSlices = 0;
		for (int i=selectedSlices[0]; i<=selectedSlices[selectedSlices.length-1]; i++) {
			slices.add(model.getAlignments().getAlignments().get(i));
			numberOfSlices++;
		}
		HashSet<Integer> selectedSlicesMap = new HashSet<Integer>();
		for (int i=0; i<selectedSlices.length; i++) {
			selectedSlicesMap.add(new Integer(selectedSlices[i]));
		}
		alignments.setAlignments(slices);
		Rectangle zeroAndImageSize = alignments.calculateImageSize();
		for (int counter=0; counter<numberOfSlices; counter++) {
			int currentSlice=selectedSlices[counter];
			ImagePlus newImage = NewImage.createByteImage(Integer.toString(currentSlice), zeroAndImageSize.width, zeroAndImageSize.height, 
					1, NewImage.FILL_BLACK);
			int greyDelta = 255 / selectedObjects.length;
			for (int currentObject=0; currentObject<selectedObjects.length; currentObject++) {
				int greyvalue = (currentObject + 1) * greyDelta;
				Object3D object = (Object3D) this.getObjects().get(selectedObjects[currentObject]);
				if (!object.getSelections().keySet().contains(new Integer(currentSlice))) continue; 
				AlignmentSlice slice = (AlignmentSlice) model.getAlignments().getAlignments().get(currentSlice);
				object.paintSliceOn8BitImage(slice, currentSlice, newImage, zeroAndImageSize, greyvalue);
			}
			String nameIndex = "" + (new Integer(currentSlice));
			String originalNameIndex = nameIndex + "";
			for (int i=0; i<5-(originalNameIndex.length()); i++) {
				nameIndex = "0" + nameIndex;
			}
			save.setInputImage(newImage);
			String fileName = targetFile.getName().split("\\.")[0];
			save.setPath(targetFile.getParent() + File.separator + fileName + nameIndex + ".tif");
			save.run();
		}
	}
	
	public void showObjects(int[] selectedIndices) {
		for (int i=0; i<selectedIndices.length; i++) {
			int index = selectedIndices[i];
			((Object3D)this.getObjects().get(index)).setIsVisible(true);
		}
		model.changed("showObjects");
		model.changed("display");
		this.changed("objects");
	}

	public void hideObjects(int[] selectedIndices) {
		for (int i=0; i<selectedIndices.length; i++) {
			int index = selectedIndices[i];
			((Object3D)this.getObjects().get(index)).setIsVisible(false);
		}
		model.changed("showObjects");
		model.changed("display");
		this.changed("objects");
	}

	public void findAndCreateObjects(String name) {
		int[] selectedSlices = model.getView().getSelectionIndices();
		Roi points = null;
		int sliceIndex = -1;
		int sliceIndexInSelection = -1;
		for (int i=0; i<selectedSlices.length; i++) {
			AlignmentSlice currentSlice = (AlignmentSlice)model.getAlignments().alignments.get(selectedSlices[i]);
			if (currentSlice.getRoi()!=null && currentSlice.getRoi().getType()==Roi.POINT) {
				sliceIndexInSelection = i;
				sliceIndex = selectedSlices[i];
				points = currentSlice.getRoi();
			}
		}
		if (points==null) return;
		int[] xCoords = points.getPolygon().xpoints;
		int[] yCoords = points.getPolygon().ypoints;
		int number = points.getPolygon().npoints;
		int offset = this.getLastNumberForObjectName(name);
		for (int j=0; j<number; j++) {
			this.addObject(name+NumberUtil.intToStringWithNDigits(j+1+offset, 5));
		}
		AlignmentSlice startSlice = (AlignmentSlice)model.getAlignments().alignments.get(sliceIndex);
		AlignmentSlice currentSlice = startSlice;
		// do it for slice with point selection
		if (currentSlice.getImage()==null) currentSlice.setImage(currentSlice.loadImage());
		if (MagicWand.getInstance().getImage()!=currentSlice.getImage()) {
			MagicWand.getInstance().setImage(currentSlice.getImage());
		}
		for (int objectNumber=0; objectNumber<number; objectNumber++) {
			doAutoSelectForSlice(currentSlice, null, name, xCoords[objectNumber], yCoords[objectNumber], objectNumber+offset, sliceIndex);
		}
		// from slice with point selection forwards
		AlignmentSlice lastSlice = startSlice;
		for (int i=sliceIndexInSelection + 1; i<selectedSlices.length; i++) {
			currentSlice = (AlignmentSlice)model.getAlignments().alignments.get(selectedSlices[i]);
			currentSlice.setImage(currentSlice.loadImage());
			MagicWand.getInstance().setImage(currentSlice.getImage());
			for (int objectNumber=0; objectNumber<number; objectNumber++) {
				doAutoSelectForSlice(currentSlice, lastSlice, name, xCoords[objectNumber], yCoords[objectNumber], objectNumber+offset, selectedSlices[i]);
			}
			lastSlice = currentSlice;
		}
		// from slice with point selection backwards
		lastSlice = startSlice;
		for (int i=sliceIndexInSelection - 1; i>-1; i--) {
			currentSlice = (AlignmentSlice)model.getAlignments().alignments.get(selectedSlices[i]);
			currentSlice.setImage(currentSlice.loadImage());
			MagicWand.getInstance().setImage(currentSlice.getImage());
			for (int objectNumber=0; objectNumber<number; objectNumber++) {
				doAutoSelectForSlice(currentSlice, lastSlice, name, xCoords[objectNumber], yCoords[objectNumber], objectNumber+offset, selectedSlices[i]);
			}
			lastSlice = currentSlice;
		}
		this.changed("selections");
		model.changed("showObjects");
		model.changed("display");
	}

	private int getLastNumberForObjectName(String aName) {
		int counter = 0;
		ArrayList<String> allNames = this.getObjectNames();
		for(String name : allNames) {
			if (name.contains(aName)) {
				int number = 0;
				try {
					number = Integer.parseInt(name.substring(aName.length(), name.length()));
				} catch (java.lang.NumberFormatException e) {
					// ignore
				}
				if (number>counter) counter = number;
			}
		}	
		return counter;
	}

	protected int getIndexIn(int[] array, int value) {
		for (int i=0; i<array.length; i++) {
			if (array[i]==value) return i;
		}
		return -1;
	}

	protected void doAutoSelectForSlice(AlignmentSlice slice, AlignmentSlice lastSlice, String objectName, int x, int y, int objectNumber, int sliceNumber) {
		int xCoord = x;
		int yCoord = y;
		int index = this.getObjectNames().indexOf(objectName+NumberUtil.intToStringWithNDigits(objectNumber+1, 5));
		Object3D object = (Object3D) this.getObjects().get(index);
		if (lastSlice!=null) {
			Integer key = new Integer(model.getAlignments().getAlignments().indexOf(lastSlice));
			Polygon polygon = ((Roi)object.getSelections().get(key)).getPolygon();
			Point point = MagicWand.getInstance().getPointInsidePolygon(polygon);
			xCoord = point.x;
			yCoord = point.y;
		}
		slice.autoSelect(xCoord, yCoord);
		object.addSelection(sliceNumber, slice.getRoi());
	}

	public void saveOneSeriesPerObject(File targetFile, int[] selectedObjects, int[] selectedSlices) {
		for (int i=0; i< selectedObjects.length; i++) {
			String filename = targetFile.getAbsolutePath().split("\\.")[0] + NumberUtil.intToStringWithNDigits(i, 5) + "-" + ".tif";
			File aFile = new File(filename);
			int[] theObject = new int[1];
			theObject[0] = selectedObjects[i];
			this.saveObjectsToSeries(aFile, theObject, selectedSlices);
		}
	}

	public void selectObjectAt(int x, int y, int z) {
		ArrayList<Object3D> allObjects = this.getObjects();
		int counter=0;
		boolean found = false;
		for (Object3D object : allObjects) {
			found = object.getSelections().containsKey(new Integer(z));
			if (found) {
				Roi aRoi = (Roi) object.getSelections().get(new Integer(z));
				found = aRoi.contains(x,y);
			}
			if (found) break;
			counter++;
		}
		if (!found) return;
		selectObjectIndex = counter;
		this.changed("selectedObject");
	}

	public void createObjectFromSelection() {
		ImagePlus image = IJ.getImage();
		if (image.getRoi()==null) return;
		MagicWand wand = MagicWand.getInstance();
		if (wand.getImage()==image) {
			wand.closeSelection();
			autoObjectCounter++;
			String name = NumberUtil.intToStringWithNDigits(autoObjectCounter, 5);
			this.addObject(name);
			int[] indices = new int[1];
			indices[0] = this.getObjectNames().lastIndexOf(name);
			this.addSelectionsToObjects(indices);
			this.changed("objects");
			this.getView().getObjectsList().setSelectedValue(name, true);
			this.changed("selections");
			model.changed("display");
		}
		
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_SPACE && e.isControlDown()) {
			int[] indices = this.getView().getObjectsList().getSelectedIndices();
			this.addSelectionsToObjects(indices);
			return;
		}
		if (e.getKeyCode()==KeyEvent.VK_SPACE) {
			this.createObjectFromSelection();
		}
	}

	public void measureObjects(int[] selectedObjects, int[] selectedSlices) {
		boolean firstrun = true;
		int counter = 1;
		TextWindow table = new TextWindow("object measurements", "", 400, 300);
		for (int sliceNr = 0; sliceNr<selectedSlices.length; sliceNr++) {
			AlignmentSlice currentSlice = (AlignmentSlice)model.getAlignments().alignments.get(selectedSlices[sliceNr]);
			ImagePlus image = currentSlice.loadImage();
			for (int objectNr = 0; objectNr<selectedObjects.length; objectNr++) {
				Object3D object = (Object3D) this.getObjects().get(selectedObjects[objectNr]);
				Roi roi = (Roi) object.getSelections().get(new Integer(selectedSlices[sliceNr]));
				String data = "";
				if (roi!=null) {
					ResultsTable measurement = object.measureSliceOnImage(selectedSlices[sliceNr], image, this.getMeasureOperation());
					if (firstrun) {
						firstrun = false;
						String headingsString = measurement.getColumnHeadings();
						headingsString = "Nr.\tObject\tSlice" + headingsString;
						table.getTextPanel().setColumnHeadings(headingsString);
					}
					data = measurement.getRowAsString(0);
					data = data.substring(data.indexOf("\t")+1, data.length());
				}
				String line = counter + "\t" + object.getName() + "\t" + (selectedSlices[sliceNr] + 1) + "\t" + data; 
				table.getTextPanel().appendLine(line);
				counter++;
			}
		}
		table.setVisible(true);
	}

	public void measureObjects3D(int[] selectedIndices, int[] selectionIndices) {
		// TODO Auto-generated method stub
		
	}

	public Operation getMeasureOperation() {
		if (this.measureOperation==null) this.measureOperation = new MeasureOperation();
		return measureOperation;
	}

	public Image3DUniverse create3DSurfaceScene(int[] selectedObjects, int[] selectedSlices, int resamplingFactor) {
		Image3DUniverse scene = new Image3DUniverse();
		Create3DSurfaceSceneTask createScene = new Create3DSurfaceSceneTask(selectedObjects, selectedSlices, resamplingFactor, model, this, scene);
		Thread task = new Thread(createScene);
		task.start();
		return scene;
	}
}
