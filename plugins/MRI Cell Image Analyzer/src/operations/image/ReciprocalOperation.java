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

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.process.ImageProcessor;

/**
 * Creates a float image in which each pixel has the reciprocal value 1/f of the 
 * pixel value f in the original image. 
 * 
 * @author Volker Baecker
 */
public class ReciprocalOperation extends ImageMathOperation {
	private static final long serialVersionUID = -1355569478799307839L;

	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		WindowManager.setTempCurrentImage(result);
		IJ.run("32-bit");
		ImageProcessor ip = this.getResult().getProcessor();
		if (result.getType()!=ImagePlus.GRAY32) {
			WindowManager.setTempCurrentImage(result);
			IJ.run("32-bit");
		}
		float[] pixels = (float[])ip.getPixels();
		for (int i=0; i<ip.getWidth()*ip.getHeight(); i++) {
			if (pixels[i]==0f)
				pixels[i] = Float.NaN;
			else
				pixels[i] = 1f/pixels[i];
		}
		ip.resetMinAndMax();
	}
}
