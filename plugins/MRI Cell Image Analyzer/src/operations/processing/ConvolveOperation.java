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

import gui.Options;
import gui.options.BooleanOption;
import gui.options.MatrixOption;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.Convolver;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import operations.Operation;

/**
 * Convolves an image with a given kernel.
 * 
 * @author Volker Bäcker
 */

public class ConvolveOperation extends Operation {
	private static final long serialVersionUID = -484925125891190161L;
	MatrixOption kernel;
	BooleanOption normalize;
	private Convolver convolver;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		optionsNames = new String[2];
		optionsNames[0] = "kernel";
		optionsNames[1] = "normalize";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}

	public float[] getKernel() {
		return kernel.getMatrixValue();
	}

	public void setKernel(String aKernel) {
		this.kernel.setValue(aKernel);
	}
	
	public void doIt() {
		convolver = null;
		ImagePlus inputImage = this.getInputImage();
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		this.processSlices();
	}

	private int getKernelHeight() {
		return kernel.height();
	}

	private int getKernelWidth() {
		return kernel.width();
	}
	
	protected void setupOptions() {
		options = new Options(); 
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.kernel = new MatrixOption();
		this.setKernel("-1, -1, -1, -1, -1;	-1, -1, -1, -1, -1; -1, -1, 24, -1, -1; -1, -1, -1, -1, -1; -1, -1, -1, -1, -1");
		kernel.setShortHelpText("enter a matrix. Use ';' to separate rows and ',' to separate values in a row");
		this.kernel.setName(optionsNames[0]);
		options.add(this.kernel);
		this.normalize = new BooleanOption();
		normalize.setValue("false");
		normalize.setName(optionsNames[1]);
		normalize.setShortHelpText("normalize the values to preserve the image brightness");
		options.add(normalize);
	}
	
	public void connectOptions() {
		this.kernel= (MatrixOption) this.options.getOptions().get(0);
		this.normalize = (BooleanOption) this.options.getOptions().get(1);
	}
	
	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		ImageProcessor ip = stack.getProcessor(sliceNumber);
		if (ip instanceof FloatProcessor) {
			this.getConvolver().convolve(ip, this.getKernel(), this.getKernelWidth(), this.getKernelHeight());
			return;
		} 
		for (int i=0; i<ip.getNChannels(); i++) {
			FloatProcessor fp = null;
			fp = ip.toFloat(i, fp);
			this.getConvolver().convolve(fp, this.getKernel(), this.getKernelWidth(), this.getKernelHeight());
			ip.setPixels(i, fp);
		}
	}

	protected Convolver getConvolver() {
		if (convolver == null) {
			convolver = new Convolver();
			convolver.setNormalize(normalize.getBooleanValue());
		}
		return convolver;
	}
}
