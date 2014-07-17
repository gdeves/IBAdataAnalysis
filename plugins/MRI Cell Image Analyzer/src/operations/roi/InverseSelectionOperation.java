/*
 * Created on 11.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.roi;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import operations.Operation;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InverseSelectionOperation extends Operation {
	private static final long serialVersionUID = 5707475408845006552L;
	
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
	
	void invertRoi(ImagePlus imp) {
		if (!IJ.isJava2())
			{IJ.error("Inverse", "Java 1.2 or later required"); return;}
		Roi roi = imp.getRoi();
		if (roi==null || !roi.isArea())
			{return;}
		ShapeRoi s1, s2;
		if (roi instanceof ShapeRoi)
			s1 = (ShapeRoi)roi;
		else
			s1 = new ShapeRoi(roi);
		
		s2 = new ShapeRoi(new Roi(0,0, imp.getWidth(), imp.getHeight()));
		imp.setRoi(s1.xor(s2));
	}
	
	public void doIt() {
		result = this.getInputImage();
		if (keepSource) {
			Roi inputRoi = inputImage.getRoi();
			inputImage.killRoi();
			result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
			inputImage.setRoi(inputRoi);
			if (inputRoi!=null) result.setRoi((Roi)inputRoi.clone());
		} 
		this.invertRoi(result);
		result.updateImage();
	}
}
