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
package operations.tracing;

import java.awt.Color;
import gui.options.Option;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Toolbar;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import operations.Operation;
import operations.control.WaitForUserOperation;

/**
 * Given a mask and a skeleton of the mask, estimate the average thickness of 
 * the filament like structures in the mask. 
 * 
 * @author Volker Bäcker
 */
public class MeasureMeanDiameterOperation extends Operation {
	private static final long serialVersionUID = -5191206248799743343L;
	protected ImagePlus skeleton;
	protected ResultsTable meanThickness;
	protected Option maxJointRadius;
	protected ImagePlus disconnectedSkeleton;
	final public int[] xInc = {1, 1, 0, -1, -1, -1,  0,  1};
	final public int[] yInc = {0, 1, 1,  1,  0, -1, -1, -1};
	private double sqrtOfTwo = Math.sqrt(2);

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "max joint radius";
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.ImagePlus");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "Skeleton";
		resultTypes = new Class[2];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultTypes[1] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[2];
		resultNames[0] = "Result";
		resultNames[1] = "MeanThickness";
	}

	public void doIt() {
		this.getInputImage();
		this.getSkeleton();
		disconnectedSkeleton = this.copyImage(getSkeleton(), "disconnected " + skeleton.getTitle());
		this.disconnectSkeleton();
		result = disconnectedSkeleton;
		float thickness = calculateMeanThickness();
		meanThickness = new ResultsTable();
		meanThickness.incrementCounter();
		meanThickness.addValue("mean thickness", thickness);
	}
	
	private void disconnectSkeleton() {
		Color foreground = Toolbar.getForegroundColor();
		Toolbar.setForegroundColor(Color.black);
		ImageProcessor ipDisconnected = disconnectedSkeleton.getProcessor();
		ImageProcessor ipSkeleton = skeleton.getProcessor();
		int width = disconnectedSkeleton.getWidth();
		int height = disconnectedSkeleton.getHeight();
		int value, count;
		int radius = getMaxJointRadius();
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				value = ipSkeleton.get(x, y);
				if (value==0) continue;
				count = 0;
				if (ipSkeleton.getPixel(x-1, y-1)>0) count++;
				if (ipSkeleton.getPixel(x,   y-1)>0) count++;
				if (ipSkeleton.getPixel(x+1, y-1)>0) count++;
				if (ipSkeleton.getPixel(x-1, y  )>0) count++;
				if (ipSkeleton.getPixel(x+1, y  )>0) count++;
				if (ipSkeleton.getPixel(x-1, y+1)>0) count++;
				if (ipSkeleton.getPixel(x,   y+1)>0) count++;
				if (ipSkeleton.getPixel(x+1, y+1)>0) count++;
				if (count<=2) continue;
				ipDisconnected.fillOval(x-radius, y-radius, 2*radius+1, 2*radius+1);
			}
		}
	   Toolbar.setForegroundColor(foreground);
	}

	public float calculateMeanThickness() {
		ImageProcessor ipDisconnected = disconnectedSkeleton.getProcessor();
		ImageProcessor ipMask = getInputImage().getProcessor();
		float sum = 0;
		int samples = 0;
		int value;
		int width = disconnectedSkeleton.getWidth();
		int height = disconnectedSkeleton.getHeight();
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				value = ipDisconnected.get(x, y);
				if (value==0) continue;
				int direction = -1;
				if (ipDisconnected.getPixel(x+1, y-1)>0) direction=1;
				if (ipDisconnected.getPixel(x  , y-1)>0) direction=0;
				if (ipDisconnected.getPixel(x-1, y-1)>0) direction=7;
				if (ipDisconnected.getPixel(x-1, y  )>0) direction=6;
				if (ipDisconnected.getPixel(x-1, y+1)>0) direction=5;
				if (ipDisconnected.getPixel(x  , y+1)>0) direction=4;
				if (ipDisconnected.getPixel(x+1, y+1)>0) direction=3;
				if (ipDisconnected.getPixel(x+1, y  )>0) direction=2;
				if (direction==-1) continue;
				boolean foundRightBorder = false;
				int rightStep = 0;
				while (!foundRightBorder) {
					int xCurrent = x + xInc[direction]*(rightStep+1);
					int yCurrent = y + yInc[direction]*(rightStep+1);
					if (ipMask.getPixel(xCurrent, yCurrent)==0) {
						foundRightBorder=true;
					} else rightStep++;
					
				}
				boolean foundLeftBorder = false;
				int leftStep = 0;
				while (!foundLeftBorder) {
					int xCurrent = x - xInc[direction]*(leftStep+1);
					int yCurrent = y - yInc[direction]*(leftStep+1);
					if (ipMask.getPixel(xCurrent, yCurrent)==0) {
						foundLeftBorder=true;
					} else leftStep++;
				}
				samples++;
				if (direction==1 || direction==3 || direction==5 || direction==7) {
					sum = sum + ((rightStep+leftStep+1) * (float)sqrtOfTwo);
				} else {
					sum = sum + (rightStep+leftStep+1);
				}
			}
		}
		float result = (sum / samples);
		result = result * (float)inputImage.getCalibration().pixelWidth;
		return result;
	}

	protected void setupOptions() {
		super.setupOptions();
		this.setMaxJointRadius(20);
		maxJointRadius.setMin(0);
		maxJointRadius.setShortHelpText("Set the maximum radius of a branching area.");
	}
	
	public void connectOptions() {
		maxJointRadius = (Option) options.getOptions().get(0);
	}
	
	public ImagePlus getSkeleton() {
		if (skeleton==null && this.getApplication()==null) {
			WaitForUserOperation op = new WaitForUserOperation();
			op.run();
			skeleton = WindowManager.getCurrentImage();
		}
		return skeleton;
	}

	public void setSkeleton(ImagePlus skeleton) {
		this.skeleton = skeleton;
	}

	public ResultsTable getMeanThickness() {
		return meanThickness;
	}

	public void setMeanThickness(ResultsTable meanThickness) {
		this.meanThickness = meanThickness;
	}

	public int getMaxJointRadius() {
		return maxJointRadius.getIntegerValue();
	}

	public void setMaxJointRadius(int maxJointRadius) {
		this.maxJointRadius.setIntegerValue(maxJointRadius);
	}
	
	protected void showResult() {
		this.getMeanThickness().show("mean thickness of " + inputImage.getTitle());
		this.disconnectedSkeleton.show();
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setSkeleton(null);
	}
}
