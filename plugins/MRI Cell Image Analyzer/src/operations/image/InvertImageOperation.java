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
package operations.image;

import operations.Operation;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

/**
 * Invert the intensity values in the image. 
 * 
 * @author Volker
 */
public class InvertImageOperation extends Operation {
	private static final long serialVersionUID = -5373906802041264782L;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
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
		super.doIt();
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		this.processSlices();
	}
	
	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		ImageProcessor ip = stack.getProcessor(sliceNumber);
		ip.invert();
	}
}
