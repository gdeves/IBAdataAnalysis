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
package operations.control;
import operations.Operation;
import ij.ImagePlus;
import ij.gui.NewImage;

/** 
 * If the input image is null an empty image with the same size and type as the reference
 * image is created, otherwise the result is a reference to the input image.
 *
 * @author Volker Baecker
 */
public class ReplaceNullWithEmptyImageOperation extends Operation {
	private static final long serialVersionUID = -5361379145012667340L;
	protected ImagePlus referenceImage;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.ImagePlus");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "ReferenceImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.inputImage;
		if (application==null) {
			inputImage = this.getInputImage();
			if (inputImage==null) return;
			referenceImage = inputImage;
			inputImage = null;
		}
		result = inputImage;
		if (result == null) {
			result = NewImage.createImage("empty", this.getReferenceImage().getWidth(), 
												       this.getReferenceImage().getHeight(), 
													   this.getReferenceImage().getNSlices(), 
													   this.getReferenceImage().getBitDepth(),
													   NewImage.FILL_WHITE);
		}
	}

	public ImagePlus getReferenceImage() {
		return referenceImage;
	}
	
	public void setReferenceImage(ImagePlus referenceImage) {
		this.referenceImage = referenceImage;
	}
}
