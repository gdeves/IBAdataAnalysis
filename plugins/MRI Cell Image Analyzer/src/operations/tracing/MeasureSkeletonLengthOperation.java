/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2008 INSERM and CNRS
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging, 
 * by Volker Bäcker
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
package operations.tracing;

import ij.ImagePlus;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import operations.Operation;

/**
 * Measure the length of a skeleton using the algorithm from 
 * "Robust Quantification of In Vitro Angiogenesis Through Image Analysis" 
 * published in IEEE Transactions on Medical Imaging Vol24, No.4, April 2005
 * as described in the ImageJ mailing list by Michael Miller. The aspect ratio of 
 * a pixel is considered to be one. Lengths are calculated from the middle of one
 * pixel to the middle of another pixel.
 * 
 * @author Volker Bäcker
 */
public class MeasureSkeletonLengthOperation extends Operation {
	private static final long serialVersionUID = 5478576220569670645L;
	protected ResultsTable length;
	protected float resultLength;
	private double sqrtOfTwo = Math.sqrt(2);
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[1];
		resultNames[0] = "Length";
	}

	public void doIt() {
		resultLength = calculateLength();
		length = new ResultsTable();
		length.incrementCounter();
		length.addValue("length", resultLength);
	}
	
	public float calculateLength() {
		ImagePlus inputImage = this.getInputImage();
		ImageProcessor ip = inputImage.getProcessor();
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		int value,ul,um,ur,l,r,ll,lm,lr;
		float straightCounter = 0;
		float diagonalCounter = 0;
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				value = ip.get(x, y);
				if (value==0) continue;
				ul = ip.getPixel(x-1, y-1);
				um = ip.getPixel(x, y-1);
				ur = ip.getPixel(x+1, y-1);
				l = ip.getPixel(x-1, y);
				r = ip.getPixel(x+1, y);
				ll = ip.getPixel(x-1, y+1);
				lm = ip.getPixel(x, y+1);
				lr = ip.getPixel(x+1, y+1);
				if (um>0) straightCounter++; 
				if (l>0) straightCounter++;
				if (r>0) straightCounter++;
				if (lm>0) straightCounter++;
				if (ul>0 && l==0 && um==0) diagonalCounter++;
				if (ur>0 && r==0 && um==0) diagonalCounter++;
				if (ll>0 && l==0 && lm==0) diagonalCounter++;
				if (lr>0 && r==0 && lm==0) diagonalCounter++;
			}
		}
		straightCounter = straightCounter / 2.0f;
		diagonalCounter = diagonalCounter / 2.0f;
		float result = straightCounter + (float)(diagonalCounter * sqrtOfTwo);
		result = result * (float)inputImage.getCalibration().pixelWidth;
		return result;
	}

	public ResultsTable getLength() {
		return length;
	}

	public void setLength(ResultsTable length) {
		this.length = length;
	}

	protected void showResult() {
		this.getLength().show("skeleton length of " + inputImage.getTitle());
	}
}
