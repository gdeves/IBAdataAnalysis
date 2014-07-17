/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker B�cker
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
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
package tools.cellCounter;

import ij.ImagePlus;
import ij.Prefs;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.Wand;
import java.awt.Color;
import java.awt.Polygon;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Vector;
import operations.Operation;
import operations.analysis.FindObjectsOperation;
import operations.file.OpenImageOperation;
import patternMatching.CentralMomentFeatureCalculator;
import patternMatching.FeatureCalculator;
import patternMatching.FeatureVector;
import applications.Application;

/**
 * A class of objects detected in an image. This is used by the CellCounter.
 * 
 * @author	Volker Baecker
 **/
public class ObjectClass {
	PointRoi selection = new PointRoi(new int[0], new int[0], 0);
	String fileName = null;
	String name;
	Color color;
	ImagePlus mask;
	ImagePlus inputImage;
	private FeatureCalculator featureCalculator;
	FeatureVector[] features;
	
	public ObjectClass(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Roi getSelection() {
		return selection;
	}
	
	public void setSelection(PointRoi roi, String fileName) {
		this.fileName = fileName;
		selection = roi;
		// to do, each point has to be added only once
	}

	public Color getColor() {
		if (color==null) {
			color = Prefs.getColor(Prefs.ROICOLOR,Color.yellow);
		}
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public boolean isEmpty() {
		return (this.getSelection().getPolygon().npoints==0);
	}

	public void putOn(Writer output) throws IOException {
		output.write(this.getName() + "\n");
		output.write(this.getSelection().getPolygon().npoints + "\n");
		float[] rgb = color.getRGBColorComponents(null);
		output.write(rgb[0] + ", ");
		output.write(rgb[1] + ", ");
		output.write(rgb[2] + "\n");
		output.write(this.fileName + "\n");
		for (int i=0; i<selection.getPolygon().npoints; i++) {
			output.write(selection.getPolygon().xpoints[i] + "," + selection.getPolygon().ypoints[i] + "\n");
		}
		for (int i=0; i<this.getFeatures().length; i++) {
			FeatureVector current = this.getFeatures()[i];
			current.putOn(output);
		}
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void calculateFeatures() {
		Polygon points = selection.getPolygon();
		ImagePlus aMask = this.getMask();
		Wand wand = new Wand(aMask.getProcessor());
		FeatureCalculator featureCalc = this.getFeatureCalculator();
		this.features = new FeatureVector[points.npoints];
		for(int i=0; i<points.npoints; i++) {
			int x = points.xpoints[i];
			int y = points.ypoints[i];
			wand.autoOutline(x,y,255,255);
			if (wand.npoints>0) {
				Roi roi = new PolygonRoi(wand.xpoints, wand.ypoints, wand.npoints, Roi.TRACED_ROI);
				this.getInputImage().setRoi(roi); 
			}
			featureCalc.setInputImage(this.getInputImage());
			featureCalc.run();
			features[i] = featureCalc.getResult();
		}
	}

	private FeatureCalculator getFeatureCalculator() {
		if (this.featureCalculator==null) {
			this.setFeatureCalculator(this.getDefaultFeatureCalculator()); 
		}
		return featureCalculator;
	}

	private void setFeatureCalculator(FeatureCalculator aFeatureCalculator) {
		this.featureCalculator = aFeatureCalculator;
		
	}

	private FeatureCalculator getDefaultFeatureCalculator() {
		FeatureCalculator result = new CentralMomentFeatureCalculator();
		return result;
	}

	private void createMask() {
		Application app = Application.load("./_applications/particle counting/select all cells preprocessing.cia");
		ArrayList<Operation> operations = app.getOperations();
		app.removeOperation((Operation)operations.get(operations.size()-1));
		Operation first = (Operation) app.getOperations().get(0);
		first.setInputImage(this.getInputImage());
		app.run();
		FindObjectsOperation lastOp = (FindObjectsOperation)(operations.get(operations.size()-1));
		mask = lastOp.getMask();
	}

	public ImagePlus getInputImage() {
		if (inputImage==null) {
			loadInputImage();
		}
		return inputImage;
	}
	
	private void loadInputImage() {
		OpenImageOperation op = new OpenImageOperation();
		op.setPath(this.getFileName());
		op.setShowResult(false);
		op.run();
		this.inputImage = op.getResult();
	}

	public void setInputImage(ImagePlus inputImage) {
		this.inputImage = inputImage;
	}

	public ImagePlus getMask() {
		if (mask==null) {
			createMask();
		}
		return mask;
	}

	public void setMask(ImagePlus mask) {
		this.mask = mask;
	}

	public FeatureVector[] getFeatures() {
		if (features==null) {
			features = new FeatureVector[0];
		}
		return features;
	}

	public ObjectClass newClassWithCellsSimilarTo(Vector<ObjectClass> comparisonObjectClasses, double distance, String newName) {
		ObjectClass newClass = new ObjectClass(newName);
		PointRoi newRoi = null;
		FeatureVector[] allObjectFeatures = this.getFeatures();
		for (int i=0; i<allObjectFeatures.length; i++) {
			FeatureVector currentFeatureVector = allObjectFeatures[i];
			for (ObjectClass currentObjectClass : comparisonObjectClasses) {
				double currentDistance = currentObjectClass.distanceTo(currentFeatureVector);
				if (currentDistance <= distance) {
					int x = selection.getPolygon().xpoints[i];
					int y = selection.getPolygon().ypoints[i];
					if (newRoi!=null) 
						newRoi = newRoi.addPoint(x,y);
					else {
						int[] xComp = new int[1];
						xComp[0] = x;
						int[] yComp = new int[1];
						yComp[0] = y;
						newRoi = new PointRoi(xComp, yComp, 1);
					}
				}
			}
		}
		newClass.setSelection(newRoi, this.getFileName());
		return newClass;
	}

	public double[] distancesTo(Vector<ObjectClass> comparisonObjectClasses) {
		FeatureVector[] allObjectFeatures = this.getFeatures();
		double[] distances = new double[allObjectFeatures.length];
		for (int i=0; i<allObjectFeatures.length; i++) {
			FeatureVector currentFeatureVector = allObjectFeatures[i];
			double minDistance = Double.MAX_VALUE;
			for (ObjectClass currentObjectClass : comparisonObjectClasses) {
				double currentDistance = currentObjectClass.distanceTo(currentFeatureVector);
				if (currentDistance<minDistance) minDistance = currentDistance;
				}
			distances[i] = minDistance;
			}
		return distances;
	}

	private double distanceTo(FeatureVector featureVector) {
		double minDistance = Double.MAX_VALUE;
		FeatureVector[] allObjectFeatures = this.getFeatures();
		for (int i=0; i<allObjectFeatures.length; i++) {
			FeatureVector currentFeatureVector = allObjectFeatures[i];
			double currentDistance = currentFeatureVector.distanceTo(featureVector);
			if (currentDistance<minDistance) minDistance = currentDistance;
		}
		return minDistance;
	}
}
