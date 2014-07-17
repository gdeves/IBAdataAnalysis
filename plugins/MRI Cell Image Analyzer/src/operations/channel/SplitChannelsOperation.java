/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker Bäcker
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
package operations.channel;

import operations.Operation;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.macro.Interpreter;
import ij.plugin.HyperStackReducer;
import ij.plugin.filter.RGBStackSplitter;

/**
 * The operation uses the class RGBStackSplitter from imagej to split an RGB image into
 * its color components. 
 * 
 * @author Volker Baecker
 */
public class SplitChannelsOperation extends Operation {
	private static final long serialVersionUID = -3534936471252579600L;
	protected ImagePlus resultRed;
	protected ImagePlus resultGreen;
	protected ImagePlus resultBlue;
	protected boolean keepGreenChannel;
	protected boolean keepRedChannel;
	protected boolean keepBlueChannel;
	protected Thread workingThread;
	
	public SplitChannelsOperation() {
		super();
		keepRedChannel = true;
		keepGreenChannel = true;
		keepBlueChannel = true;
	}

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[3];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultTypes[1] = Class.forName("ij.ImagePlus");
		resultTypes[2] = Class.forName("ij.ImagePlus");
		resultNames = new String[3];
		resultNames[0] = "ResultRed";
		resultNames[1] = "ResultGreen";
		resultNames[2] = "ResultBlue";
	}

	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		ImagePlus result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		boolean batchMode = Interpreter.isBatchMode();
		this.getInterpreter().setBatchMode(true);
		RGBStackSplitter splitter = new RGBStackSplitter();
		workingThread = Thread.currentThread();
		 if (inputImage.isComposite()) {
			 	this.splitChannels(result);
			 	this.getInterpreter().setBatchMode(batchMode);
			 	return;
	        }
		splitter.split(result);
		resultRed = WindowManager.getImage(result.getTitle() + " (red)");
		resultBlue = WindowManager.getImage(result.getTitle() + " (blue)");
		resultGreen = WindowManager.getImage(result.getTitle() + " (green)");
		this.getInterpreter().setBatchMode(batchMode);
	}
	
	protected void showResult() {
		if (this.getShowResult()) {
			if (this.keepRedChannel) {
				this.getResultRed().show();
			}
			if (this.keepGreenChannel) {
				this.getResultGreen().show();
			}
			if (this.keepBlueChannel) {
				this.getResultBlue().show();
			}
		}
	}
	
	public ImagePlus getResultBlue() {
		return resultBlue;
	}

	public void setResultBlue(ImagePlus resultBlue) {
		if (resultBlue==null && this.resultBlue!=null) {
			this.resultBlue.close();
			WindowManager.setTempCurrentImage(workingThread, null);
		}
		this.resultBlue = resultBlue;
	}

	public ImagePlus getResultGreen() {
		return resultGreen;
	}

	public void setResultGreen(ImagePlus resultGreen) {
		if (resultGreen==null && this.resultGreen!=null) {
			this.resultGreen.close();
			WindowManager.setTempCurrentImage(workingThread, null);
		}
		this.resultGreen = resultGreen;
	}

	public ImagePlus getResultRed() {
		return resultRed;
	}

	public void setResultRed(ImagePlus resultRed) {
		if (resultRed==null && this.resultRed!=null) {
			this.resultRed.close();
			WindowManager.setTempCurrentImage(workingThread, null);
		}
		this.resultRed = resultRed;
	}
	
	public boolean isKeepBlueChannel() {
		return keepBlueChannel;
	}

	public void setKeepBlueChannel(boolean keepBlueChannel) {
		this.keepBlueChannel = keepBlueChannel;
	}

	public boolean isKeepGreenChannel() {
		return keepGreenChannel;
	}

	public void setKeepGreenChannel(boolean keepGreenChannel) {
		this.keepGreenChannel = keepGreenChannel;
	}

	public boolean isKeepRedChannel() {
		return keepRedChannel;
	}

	public void setKeepRedChannel(boolean keepRedChannel) {
		this.keepRedChannel = keepRedChannel;
	}
	
    void splitChannels(ImagePlus imp) {
		int width = imp.getWidth();
		int height = imp.getHeight();
		int channels = imp.getNChannels();
		int slices = imp.getNSlices();
		int frames = imp.getNFrames();
		int size = slices*frames;
		HyperStackReducer reducer = new HyperStackReducer(imp);
		for (int c=1; c<=channels; c++) {
			ImageStack stack2 = new ImageStack(width, height, size); // create empty stack
			stack2.setPixels(imp.getProcessor().getPixels(), 1); // can't create ImagePlus will null 1st image
			ImagePlus imp2 = new ImagePlus("C"+c+"-"+imp.getTitle(), stack2);
			stack2.setPixels(null, 1);
			imp.setPosition(c, 1, 1);
			imp2.setDimensions(1, slices, frames);
			reducer.reduce(imp2);
			imp2.setOpenAsHyperStack(true);
			imp2.show();
		}
		imp.changes = false;
		imp.close();
		resultRed = WindowManager.getImage("C3-"+imp.getTitle());
		resultBlue = WindowManager.getImage("C1-"+imp.getTitle());
		resultGreen = WindowManager.getImage("C2-"+imp.getTitle());
    }
}