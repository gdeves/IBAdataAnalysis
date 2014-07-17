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
package operations.stack;

import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.process.ImageProcessor;
import operations.Operation;
import operations.control.WaitForUserOperation;

/**
 * Creates a stack with the slices of the first input image followed by the slices of the
 * second input image. 
 * 
 * @author Volker Baecker
 */
public class MergeStacksOperation extends Operation {
	private static final long serialVersionUID = 5717986238338192760L;
	protected ImagePlus secondInputImage;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
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
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		ImagePlus secondInputImage = this.getSecondInputImage();
		result = new ImagePlus();
		ImageStack stack = new ImageStack(inputImage.getWidth(), inputImage.getHeight());
		for (int i=0; i<inputImage.getNSlices(); i++) {
			ImageProcessor ip = inputImage.getStack().getProcessor(i+1);
			stack.addSlice(inputImage.getStack().getSliceLabel(i+1), ip);
		}
		for (int i=0; i<secondInputImage.getNSlices(); i++) {
			ImageProcessor ip = secondInputImage.getStack().getProcessor(i+1);
			stack.addSlice(secondInputImage.getStack().getSliceLabel(i+1), ip);
		}
		result.setStack(inputImage.getTitle() + secondInputImage.getTitle(), stack);
	}
	
	public ImagePlus getSecondInputImage() {
		if (application==null) {
			WaitForUserOperation wait = new WaitForUserOperation();
			wait.run();
			secondInputImage = WindowManager.getCurrentImage();
		}
		return secondInputImage;
	}
	
	public void setSecondInputImage(ImagePlus secondInputImage) {
		this.secondInputImage = secondInputImage;
	}
}
