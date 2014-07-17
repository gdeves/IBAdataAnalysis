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
import ij.process.ImageProcessor;
import operations.Operation;

/**
 * Sets the display min and max values to 0 and 255 for 8 bit images and to
 * 0 and 65535 for 16 and 32 bit images. This can be useful to visually compare
 * the intensities in 2 images.
 * 
 * @author Volker Bäcker
 */
public class SetMinAndMaxDisplayOperation extends Operation {
	private static final long serialVersionUID = -3681353796941618064L;

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
		
	public SetMinAndMaxDisplayOperation() {
		super();
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		ImageProcessor imp = inputImage.getProcessor();
		if (inputImage.getType()==ImagePlus.GRAY16 || inputImage.getType()==ImagePlus.GRAY32) imp.setMinAndMax(0, 65535);
		else imp.setMinAndMax(0, 255);
		this.getInputImage().updateAndDraw();
	}
}
