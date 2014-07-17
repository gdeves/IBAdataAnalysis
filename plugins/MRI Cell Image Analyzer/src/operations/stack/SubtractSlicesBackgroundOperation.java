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
package operations.stack;

import ij.IJ;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.Roi;
import ij.process.ImageStatistics;
import operations.FilterOperation;

/**
 * On each slice of the stack measure the mean intensity within the roi and subtract it from
 * the slice.  
 * 
 * @author Volker Baecker
 *
 */
public class SubtractSlicesBackgroundOperation extends FilterOperation {
	private static final long serialVersionUID = -4842005372507050040L;
	private Roi roi;

	public void runFilter() {
		roi = inputImage.getRoi();
		inputImage.killRoi();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle() + " - subtract slices background");
		this.processSlices();
		inputImage.setRoi(roi);
	}
	
	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		result.setSlice(sliceNumber);
		result.setRoi(roi);
		double value = result.getStatistics(ImageStatistics.MEAN).mean;
		WindowManager.setTempCurrentImage(result);
		result.killRoi();
		IJ.run("Subtract...", "slice value=" + value);
	}

}
