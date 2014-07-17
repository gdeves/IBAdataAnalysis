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
package operations.file;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;

/**
 * Save each slice of a stack as a single tif-image.
 * 
 * @author Volker Baecker
 */
public class SaveAsTiffSequenceOperation extends SaveImageOperation {
	private static final long serialVersionUID = -3493546943159700224L;
	
	public void doIt() {
		ImagePlus inputImage = this.inputImage;
		if (application==null && inputImage==null) inputImage = this.getInputImage();
		if (inputImage==null) return;
		path = this.getPath();
		this.addPathAdditions();
		if (inputImage.getStackSize()==1)
			return;
		else {
			int extIndex = path.lastIndexOf(".");
			String newPath = path + ".tif";
			if (extIndex!=-1) newPath = path.substring(0, extIndex) + ".tif";
			this.setSuccess(new Boolean(this.saveAsTiffSeries(newPath)));
		}
	}

	protected boolean saveAsTiffSeries(String path) {
		int number = 0;
		boolean result = true;
		String digits = getDigits(number);
		ImageStack stack = inputImage.getStack();
		ImagePlus tmp = new ImagePlus();
		tmp.setTitle(inputImage.getTitle());
		int nSlices = stack.getSize();
		for (int i=1; i<=nSlices; i++) {
			IJ.showStatus("writing: "+i+"/"+nSlices);
			IJ.showProgress((double)i/nSlices);
			tmp.setProcessor(null, stack.getProcessor(i));
			digits = getDigits(number++);
			int extIndex = path.lastIndexOf(".");
			String aPath = path.substring(0, extIndex) + digits + ".tif";
			if (!(new FileSaver(tmp).saveAsTiff(aPath))) {
				result = false;
				break;
			}
		}
		return result;
	}
	
	String getDigits(int n) {
		String digits = "00000000"+(1+n);
		return digits.substring(digits.length()-4);
	}
}
