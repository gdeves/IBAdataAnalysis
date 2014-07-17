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

/**
* Intensity values smaller than a constant value c will be set to c. 
* 
* @author Volker
*/
public class MinOperation  extends ImageMathWithParameterOperation {
	private static final long serialVersionUID = -6815542791371339186L;

	public MinOperation() throws ClassNotFoundException {
		super();
		this.value.setName("value");
		this.value.setShortHelpText("Enter the minimum.");
		this.setValue(127);
	}

	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		result.getProcessor().min(this.getValue());
	}
}
