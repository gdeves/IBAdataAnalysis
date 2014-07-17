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

import ij.ImageStack;
import imagejProxies.SilentAnalyzer;

/**
 * Measure all slices of the stack. The operation measures either each slice as a whole,
 * the content of the selection on each slice or the pixels between min and max threshold
 * on each slice, in case the limit threshold option is checked and a threshold is set
 * using the threshold adjuster.
 * 
 * @author	Volker Baecker 
 */
public class MeasureAllSlicesOperation extends MeasureOperation {
	private static final long serialVersionUID = 1L;

	public void doIt() {
		measurements = null;
		result = this.getCopyOfOrReferenceTo(this.getInputImage(), inputImage.getTitle() + " - measure all slices");
		measurements = this.getMeasurements();
		measurements.reset();
		this.processSlices();
	}
	
	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		SilentAnalyzer analyzer = new SilentAnalyzer(this.getInputImage(), 
				 this.getMeasurementsWord(), 
				 measurements);
		this.getInputImage().setSlice(sliceNumber);
		analyzer.setup("", this.getInputImage());
		analyzer.run(this.getInputImage().getProcessor());
	}

	protected void showResult() {
		this.getMeasurements().show("measurements on " + this.getInputImage().getTitle());
	}
}
