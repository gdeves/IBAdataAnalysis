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
package operations.analysis;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import operations.Operation;
import operations.control.WaitForUserOperation;
import operations.image.ConvertImageTypeOperation;

/**
 * Calculate the average difference between two images. The two images must have
 * the same size. The images are conerted to 8-bit images before the computation 
 * is done. Pixels that are zero in one of the images are skipped.
 *
 * @author	Volker Baecker
 **/
public class ComputeDifferenceOperation extends Operation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7770582616755118107L;
	protected ImagePlus firstImage;
	protected ImagePlus secondImage;
	protected ResultsTable difference;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.ImagePlus");
		parameterNames = new String[2];
		parameterNames[0] = "FirstImage";
		parameterNames[1] = "SecondImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.measure.ResultsTable");
		resultNames = new String[1];
		resultNames[0] = "Difference";
	}
	
	public ResultsTable getDifference() {
		if (difference==null) {
			difference = new ResultsTable();
		}
		return difference;
	}

	public void setDifference(ResultsTable difference) {
		this.difference = difference;
	}

	public ImagePlus getFirstImage() {
		return firstImage;
	}

	public void setFirstImage(ImagePlus firstImage) {
		this.firstImage = firstImage;
	}

	public ImagePlus getSecondImage() {
		return secondImage;
	}

	public void setSecondImage(ImagePlus secondImage) {
		this.secondImage = secondImage;
	}
	
	public void doIt() {
		if (firstImage==null) firstImage = IJ.getImage();
		if (secondImage==null) {
			WaitForUserOperation op = new WaitForUserOperation();
			op.run();
			secondImage = IJ.getImage();
		}
		ConvertImageTypeOperation convert = new ConvertImageTypeOperation();
		convert.setShowResult(false);
		if (firstImage.getType()!=ImagePlus.GRAY8) {
			convert.setInputImage(firstImage);
			convert.run();
			firstImage = convert.getResult();
		}
		if (secondImage.getType()!=ImagePlus.GRAY8) {
			convert.setInputImage(secondImage);
			convert.run();
			secondImage = convert.getResult();
		}
		if (firstImage.getImage()==null) firstImage.setImage(firstImage.getProcessor().createImage());
		if (secondImage.getImage()==null) secondImage.setImage(secondImage.getProcessor().createImage());
		float result = 0;
		int counter = 0;
		int width = firstImage.getWidth();
		int height = firstImage.getHeight();
		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				float value1 = firstImage.getPixel(x,y)[0];
				if (value1==0) continue;
				float value2 = secondImage.getPixel(x,y)[0];
				if (value2==0) continue;
				counter++;
				result = result + Math.abs(value1-value2);
			}
		}
		result = result / (counter * 1.0f);
		difference = this.getDifference();
		difference.reset();
		difference.incrementCounter();
		difference.addValue("average difference", result);
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setFirstImage(null);
		this.setSecondImage(null);
	}

	protected void showResult() {
		this.getDifference().show("result of compute difference");
	}
}
