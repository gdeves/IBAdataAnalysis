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
package operations.processing;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import operations.Operation;
import operations.analysis.FindObjectsOperation;

/**
 * Erodes a binary image until the first object totally disappears.
 * 
 * @author Volker Bäcker
 */
public class AdaptiveErodeOperation extends Operation {
	private static final long serialVersionUID = -6603131749230528452L;	
	protected ImagePlus tmpImage = null;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		ImageStatistics stats = result.getStatistics();
		if (result.getType()!=ImagePlus.GRAY8 || (stats.histogram[0]+stats.histogram[255]!=stats.pixelCount)) {
			IJ.error("adaptive erode operation",
				"A thresholded image or an 8-bit binary image is\n"
				+"required. Refer to Image->Adjust->Threshold\n"
				+"or to Process->Binary->Threshold.");
			this.stopApplication();
			return;
		}
		this.processSlices();
	}
	
	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		ImageProcessor ip = stack.getProcessor(sliceNumber);
		int lastCount=0;
		int currentCount=0;
		int iterations = 0;
		 while (lastCount<=currentCount) {
		 	tmpImage = this.copyImage(inputImage, inputImage.getTitle());
			lastCount = this.countObjects();
			ip.erode();
			currentCount = this.countObjects();
			iterations++;
		}
		if (this.isKeepSource()) {result = tmpImage;} else {
			result.setImage(tmpImage.getImage());
			result.updateAndDraw();
		};
	}

	protected int countObjects() {
		FindObjectsOperation findObjectsOperation = new FindObjectsOperation();
		findObjectsOperation.setInputImage(result);
		findObjectsOperation.setMinSize(1);
		findObjectsOperation.setShowResult(false);
		findObjectsOperation.setExcludeEdgeObjects(false);
		findObjectsOperation.run();
		ResultsTable measurements = findObjectsOperation.getMeasurements();
		return measurements.getCounter();
	}
}
