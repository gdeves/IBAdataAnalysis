/*
 * Created on 11.07.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.processing;

import operations.Operation;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.process.ImageProcessor;
import ij.text.TextWindow;

public class SubtractMeanOperation extends Operation {
	
	private static final long serialVersionUID = -8767495960888550255L;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		// Set parameterTypes, parameterNames and resultType
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
		this.processSlices();
		result.setSlice(1);
		TextWindow textWin =((TextWindow)(IJ.getTextPanel().getParent()));
		textWin.dispose();
	}
	
	protected void doItForSlice(int sliceNumber, ImageStack stack) {
		ImageProcessor ip = stack.getProcessor(sliceNumber);
		result.setSlice(sliceNumber);
		ResultsTable measurements = new ResultsTable();
		Analyzer analyzer = new Analyzer(result, Measurements.MEAN, measurements);
		analyzer.run(ip);
		double mean = measurements.getValueAsDouble(1,0);
		ip.add(-mean);
	}
}
