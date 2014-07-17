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

import gui.Options;
import gui.options.ChoiceOption;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.RGBStackMerge;
import operations.Operation;
import operations.control.WaitForUserOperation;

/**
 * The operation uses the class RGBStackMerge from imagej to merge 2 or 3 greyscale images into an RGB image. 
 * 
 * @author Volker Baecker
 */
public class MergeChannelsOperation extends Operation {
	private static final long serialVersionUID = -8554655272266991314L;
	protected ImagePlus inputImageRed;
	protected ImagePlus inputImageGreen;
	protected ImagePlus inputImageBlue;
	protected ChoiceOption redChannel;
	protected ChoiceOption greenChannel;
	protected ChoiceOption blueChannel;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[3];
		optionsNames[0] = "red channel";
		optionsNames[1] = "green channel";
		optionsNames[2] = "blue channel";
		parameterTypes = new Class[3];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.ImagePlus");
		parameterTypes[2] = Class.forName("ij.ImagePlus");
		parameterNames = new String[3];
		parameterNames[0] = "InputImageRed";
		parameterNames[1] = "InputImageGreen";
		parameterNames[2] = "InputImageBlue";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}

	public void doIt() {
		ImagePlus[] channels = new ImagePlus[3];;
		this.setupChannels(channels);
		RGBStackMerge merger = new RGBStackMerge();
		ImageStack redStack = null;
		ImageStack greenStack = null;
		ImageStack blueStack = null;
		int width = 0; 
		int height = 0;
		int stackSize = 0;
		if (channels[0]!=null) {
			redStack = channels[0].getStack();
			width = channels[0].getWidth();
			height = channels[0].getHeight();
			stackSize = channels[0].getStackSize();
		}
		if (channels[1]!=null) {
			greenStack = channels[1].getStack();
			width = channels[1].getWidth();
			height = channels[1].getHeight();
			stackSize = channels[1].getStackSize();
		}
		if (channels[2]!=null) {
			blueStack = channels[2].getStack();
			width = channels[2].getWidth();
			height = channels[2].getHeight();
			stackSize = channels[2].getStackSize();
		}
		ImageStack mergedStacks = merger.mergeStacks(width,
												     height,
													 stackSize,
													 redStack,
													 greenStack,
													 blueStack,
													 this.keepSource);
		result = new ImagePlus("RGB", mergedStacks);
	}

	protected void setupChannels(ImagePlus[] channels) {
		channels[0] = null;
		channels[1] = null;
		channels[2] = null;
		if (this.getRedChannel().equals(this.choices()[0])) {
			channels[0] = this.getInputImageRed();
		}
		if (this.getRedChannel().equals(this.choices()[1])) {
			channels[0] = this.getInputImageGreen();
		}
		if (this.getRedChannel().equals(this.choices()[2])) {
			channels[0] = this.getInputImageBlue();
		}
		if (!getRedChannel().equals("none") && channels[0]==null) channels[0] = IJ.getImage();
		if (this.getGreenChannel().equals(this.choices()[0])) {
			channels[1] = this.getInputImageRed();
		}
		if (this.getGreenChannel().equals(this.choices()[1])) {
			channels[1] = this.getInputImageGreen();
		}
		if (this.getGreenChannel().equals(this.choices()[2])) {
			channels[1] = this.getInputImageBlue();
		}
		if (!getGreenChannel().equals("none") && channels[1]==null) {
			if (channels[0]==null) channels[1] = IJ.getImage(); 
			else {
				WaitForUserOperation op = new WaitForUserOperation();
				op.run();
				channels[1] = IJ.getImage();
			}
		}
		if (this.getBlueChannel().equals(this.choices()[0])) {
			channels[2] = this.getInputImageRed();
		}
		if (this.getBlueChannel().equals(this.choices()[1])) {
			channels[2] = this.getInputImageGreen();
		}
		if (this.getBlueChannel().equals(this.choices()[2])) {
			channels[2] = this.getInputImageBlue();
		}
		if (!getBlueChannel().equals("none") && channels[2]==null) {
			if (channels[0]==null && channels[1]==null) channels[2] = IJ.getImage();
			else {
				WaitForUserOperation op = new WaitForUserOperation();
				op.run();
				channels[2] = IJ.getImage();
			}
		}
	}

	public ImagePlus getInputImageBlue() {
		return inputImageBlue;
	}

	public void setInputImageBlue(ImagePlus inputImageBlue) {
		this.inputImageBlue = inputImageBlue;
	}

	public ImagePlus getInputImageGreen() {
		return inputImageGreen;
	}

	public void setInputImageGreen(ImagePlus inputImageGreen) {
		this.inputImageGreen = inputImageGreen;
	}

	public ImagePlus getInputImageRed() {
		return inputImageRed;
	}

	public void setInputImageRed(ImagePlus inputImageRed) {
		this.inputImageRed = inputImageRed;
	}

	public String getBlueChannel() {
		return blueChannel.getValue();
	}

	public void setBlueChannel(String blueChannel) {
		this.blueChannel.setValue(blueChannel);
	}

	public String getGreenChannel() {
		return greenChannel.getValue();
	}

	public void setGreenChannel(String greenChannel) {
		this.greenChannel.setValue(greenChannel);
	}

	public String getRedChannel() {
		return redChannel.getValue();
	}

	public void setRedChannel(String redChannel) {
		this.redChannel.setValue(redChannel);
	}
	
	public String[] choices() {
		String[] choices = {"red input", "green input", "blue input", "none"};
		return choices;
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.redChannel = new ChoiceOption(this.choices());
		this.redChannel.setValue(this.choices()[0]);
		this.redChannel.setName(optionsNames[0]);
		options.add(this.redChannel);
		this.greenChannel = new ChoiceOption(this.choices());
		this.greenChannel.setValue(this.choices()[1]);
		this.greenChannel.setName(optionsNames[1]);
		options.add(this.greenChannel);
		this.blueChannel = new ChoiceOption(this.choices());
		this.blueChannel.setValue(this.choices()[2]);
		this.blueChannel.setName(optionsNames[2]);
		options.add(this.blueChannel);
	}
	
	public void connectOptions() {
		redChannel = (ChoiceOption) options.getOptions().get(0);
		greenChannel = (ChoiceOption) options.getOptions().get(1);
		blueChannel = (ChoiceOption) options.getOptions().get(2);
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setInputImageRed(null);
		this.setInputImageGreen(null);
		this.setInputImageBlue(null);
	}
}
