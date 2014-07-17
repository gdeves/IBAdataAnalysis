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
package operations.analysis;

import ij.ImagePlus;
import ij.gui.NewImage;

/**
 * Stores the eigenvalues and eigenvectors from the hessian operation and creates
 * images of the eigenvalues or eigenvectors.
 * 
 * @author	Volker Baecker 
 */
public class HessianImage  {
	float[][][] hessian;

	public float[][][] getHessian() {
		return hessian;
	}

	public void setHessian(float[][][] hessian) {
		this.hessian = hessian;
	}
	
	public ImagePlus getImage() {
		ImagePlus result = NewImage.createByteImage("costs", hessian[0][0].length, hessian[0].length, 1, NewImage.GRAY8);
		for (int xIndex = 0; xIndex < hessian[0][0].length; xIndex++) {
			for (int yIndex = 0; yIndex < hessian[0].length; yIndex++) {
				result.getProcessor().putPixel(xIndex, yIndex, (int)Math.round(hessian[0][yIndex][xIndex]));
			}
		}
		return result;
	}
	
	public ImagePlus getVectorImage(int raster) {
		ImagePlus result = NewImage.createByteImage("costs", hessian[0][0].length, hessian[0].length, 1, NewImage.GRAY8);
		for (int xIndex = 0; xIndex < hessian[1][0].length; xIndex+=raster) {
			for (int yIndex = 0; yIndex < hessian[1].length; yIndex+=raster) {
				float xValue =(hessian[1][yIndex][xIndex]);
				float yValue = (hessian[2][yIndex][xIndex]);
				float length = ((hessian[0][yIndex][xIndex]) / 254.0f) * (raster/2.0f);
				result.getProcessor().drawLine(xIndex, yIndex, Math.round(xIndex+(xValue*length)), Math.round(yIndex+(yValue*length)));
			}
		}
		return result;
	}
}
