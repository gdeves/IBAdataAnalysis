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
package operations.processing;

import operations.Operation;
import gui.options.Option;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;


/**
 * Replaces each pixel with the maximum (brightest) value in a 3x3 neighborhood, thus enlarging bright regions. 
 * This is the same as using the maximum operation with a radius 1. The operation can be applied repeatedly on the
 * same image for a given number of times.
 * 
 * @author Volker Bäcker
 */
public class ErodeOperation extends Operation {
	private static final long serialVersionUID = 1577451507283585363L;
	Option numberOfTimes;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "number of times";
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		this.processSlices();
	}
	
	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		ImageProcessor ip = stack.getProcessor(sliceNumber);
		for (int i=0; i< this.getNumberOfTimes(); i++) ip.erode();
	}

	public int getNumberOfTimes() {
		return numberOfTimes.getIntegerValue();
	}

	public void setNumberOfTimes(int numberOfTimes) {
		this.numberOfTimes.setValue(Integer.toString(numberOfTimes));
	}
	
	public void connectOptions() {
		this.numberOfTimes = (Option) this.options.getOptions().get(0);
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setNumberOfTimes(1);
		this.numberOfTimes.setMin(1);
		this.numberOfTimes.setShortHelpText("enter the number of times the operation is applied");
	}
}
