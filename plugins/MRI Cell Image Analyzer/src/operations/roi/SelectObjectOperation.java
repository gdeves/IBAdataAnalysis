/*
 * Created on 10.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.roi;

import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.Wand;
import ij.process.ImageProcessor;

import java.awt.geom.Point2D;

import operations.Operation;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SelectObjectOperation extends Operation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5662508206249428924L;
	
	protected Point2D position;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		// Set parameterTypes, parameterNames and resultType
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("java.awt.geom.Point2D");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "Position";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	/**
	 * @return Returns the position.
	 */
	public Point2D getPosition() {
		return position;
	}
	/**
	 * @param position The position to set.
	 */
	public void setPosition(Point2D position) {
		this.position = position;
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setPosition(null);	
	}
	
	public void doIt() {
		result = this.getCopyOfOrReferenceTo(inputImage, inputImage.getTitle());
		result.killRoi();
		this.doWand();
	}

	/**
	 * 
	 */
	private void doWand() {
		ImagePlus img = result;
		ImageProcessor ip = img.getProcessor();
		Wand w = new Wand(ip);
		double t1 = ip.getMinThreshold();
		int x = (int)Math.round(position.getX());
		int y = (int)Math.round(position.getY());
		if (t1==ImageProcessor.NO_THRESHOLD)
			w.autoOutline(x, y);
		else
			w.autoOutline(x, y, (int)t1, (int)ip.getMaxThreshold());
		if (w.npoints>0) {
			Roi roi = new PolygonRoi(w.xpoints, w.ypoints, w.npoints, Roi.TRACED_ROI);
			img.setRoi(roi); 
		}
	}
	
}
