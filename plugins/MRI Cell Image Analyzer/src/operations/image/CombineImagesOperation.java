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
package operations.image;

import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.NewImage;
import gui.Options;
import gui.options.BooleanOption;
import operations.Operation;
import operations.control.WaitForUserOperation;
import stack.Stack_Combiner;

/**
 * Create a new image containing the two input images either one above the other or one next to the other.
 * 
 * @author Volker
 */
public class CombineImagesOperation extends Operation {
	private static final long serialVersionUID = 3628029706683937964L;
	protected BooleanOption combineVertically;
	protected ImagePlus secondInputImage;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "combine vertically";
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.ImagePlus");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "SecondInputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.combineVertically = new BooleanOption();
		this.combineVertically.setValue("true");
		this.combineVertically.setName(optionsNames[0]);
		this.combineVertically.setShortHelpText("choose the arrangement of the images");
		options.add(this.combineVertically);
	}
	
	public void connectOptions() {
		combineVertically = (BooleanOption) options.getOptions().get(0);
	}

	public boolean getCombineVertically() {
		return combineVertically.getBooleanValue();
	}

	public void setCombineVertically(boolean combineVertically) {
		this.combineVertically.setValue(Boolean.toString(combineVertically)); 
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		ImagePlus secondInputImage = this.getSecondInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		if (secondInputImage==null) secondInputImage = 
			NewImage.createImage("empty", inputImage.getWidth(), 
										  inputImage.getHeight(), 
										  inputImage.getNSlices(), 
										  inputImage.getBitDepth(),
										  NewImage.FILL_WHITE);
		ImagePlus tmp = 
			this.getCopyOfOrReferenceTo(secondInputImage, secondInputImage.getTitle());
		Stack_Combiner combiner = new Stack_Combiner();
		ImageStack combined;
		if (this.getCombineVertically()) 	
			combined = combiner.combineVertically(result.getStack(), tmp.getStack()); 
		else
			combined = combiner.combineHorizontally(result.getStack(), tmp.getStack());
		result = new ImagePlus("Combined Images", combined);
	}

	public ImagePlus getSecondInputImage() {
		if (secondInputImage==null) {
			if (application==null) {
				WaitForUserOperation wait = new WaitForUserOperation();
				wait.run();
			}
			secondInputImage = WindowManager.getCurrentImage();
		}
		return secondInputImage;
	}

	public void setSecondInputImage(ImagePlus secondInputImage) {
		this.secondInputImage = secondInputImage;
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setSecondInputImage(null);	
	}
}
