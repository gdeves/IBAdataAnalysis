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
package operations.roi;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.Roi;
import gui.options.Option;
import operations.FilterOperation;
/**
 * Enlarge or shrink a selection by a given number of pixels.
 * 
 * @author Volker Bäcker
 */
public class EnlargeSelectionOperation extends FilterOperation {
	private static final long serialVersionUID = -2772969587868259692L;
	protected Option pixels; 
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[1];
		optionsNames[0] = "pixels";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		Roi aRoi = inputImage.getRoi();
		if (aRoi==null) return;
		inputImage.killRoi();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		inputImage.setRoi(aRoi);
		ImageStack stack = result.getStack();
		if (stack!=null) {
			result.setSlice(inputImage.getCurrentSlice());
		}
		WindowManager.setTempCurrentImage(result);
		this.runFilter();
		result = IJ.getImage();
		WindowManager.setTempCurrentImage(null);
	}
	
	@Override
	public void runFilter() {
		result.setRoi(inputImage.getRoi());
		IJ.run("Enlarge...", "enlarge=" + this.getPixels());
	}
	
	protected void setupOptions() {
		super.setupOptions();
		setPixels(-4);
		pixels.setShortHelpText("Use a negative value to shrink");
	}
	public void connectOptions() {
		this.pixels = (Option) this.options.getOptions().get(0);
	}

	public int getPixels() {
		return pixels.getIntegerValue();
	}

	public void setPixels(int pixels) {
		this.pixels.setIntegerValue(pixels);
	}
}
