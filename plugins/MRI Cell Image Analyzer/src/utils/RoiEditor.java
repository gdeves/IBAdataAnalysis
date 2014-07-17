/*
 * Created on 10.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package utils;

import java.awt.Polygon;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RoiEditor {
	

	protected ImagePlus inputImage;

	/**
	 * @param currentImage
	 */
	public RoiEditor(ImagePlus currentImage) {
		inputImage = currentImage;
	}

	/**
	 * 
	 */
	public void deleteLastPolygonSegment() {
		Roi roi = inputImage.getRoi();
		if (roi.getType()!=Roi.POLYLINE && roi.getType()!=Roi.POLYGON) return;
		PolygonRoi polygonRoi = (PolygonRoi) roi;
		Polygon polygon = polygonRoi.getPolygon();
		int[] xPoints = new int[polygon.npoints-1];
		int[] yPoints = new int[polygon.npoints-1];
		for (int i=0; i<polygon.npoints-1; i++) {
			xPoints[i] = polygon.xpoints[i];
			yPoints[i] = polygon.ypoints[i];
		} 
		PolygonRoi newRoi = new PolygonRoi(xPoints, yPoints, polygon.npoints-1, roi.getType());
		inputImage.setRoi(newRoi);
	}
}
