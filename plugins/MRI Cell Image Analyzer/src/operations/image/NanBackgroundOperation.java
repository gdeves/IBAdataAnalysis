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
import gui.options.Option;
import ij.IJ;
import ij.process.ImageProcessor;
import operations.FilterOperation;

/**
 * Set intensities below min and above max to the special floating point value
 * NaN (Not a Number)
 * 
 * @author Volker Bäcker
 */
public class NanBackgroundOperation extends FilterOperation {
	private static final long serialVersionUID = 582920378700486689L;
	protected Option min;
	protected Option max;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		optionsNames = new String[2];
		optionsNames[0] = "min";
		optionsNames[1] = "max";
	}
	
	public void runFilter() {
		IJ.run("32-bit");
		result.getProcessor().setThreshold(this.getMin(), this.getMax(), ImageProcessor.RED_LUT);
		IJ.run("NaN Background");
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setMin(0);
		min.setShortHelpText("Values below min will be set to NaN.");
		this.setMax(1);
		max.setShortHelpText("Values above max will be set to NaN.");
	}
	
	public void connectOptions() {
		this.min = (Option) this.options.getOptions().get(0);
		this.max = (Option) this.options.getOptions().get(1);
	}

	public float getMax() {
		return max.getFloatValue();
	}

	public void setMax(float max) {
		this.max.setFloatValue(max);
	}

	public float getMin() {
		return min.getFloatValue();
	}

	public void setMin(float min) {
		this.min.setFloatValue(min);
	}
}
