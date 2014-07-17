/*
 * Created on 18.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.roi;

import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.gui.Wand;
import ij.process.ImageProcessor;
import operations.Operation;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SelectWhitePixelOperation extends Operation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2645341863444897388L;
	
	static public int raster = 10;
	
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
		result = this.getInputImage();
		result.killRoi();
		ShapeRoi roi = 
		this.selectWhitePixel();
		roi = this.unselectBlackPixel(roi);
		result.getProcessor().setRoi(roi);
		result.setProcessor(null, result.getProcessor());
		result.setRoi(roi);
	}

	/**
	 * @param roi
	 */
	private ShapeRoi unselectBlackPixel(ShapeRoi roi) {
		if (roi==null) return roi;
		ImagePlus img = result;
		ImageProcessor ip = img.getProcessor();
		Wand w = new Wand(ip);
		int width = result.getWidth();
		int height = result.getHeight(); 
		for (int x=0; x<width; x+=raster) {
			for (int y=0; y<height; y+=raster) {
				if (ip.getPixel(x,y)==0) {
					if (roi.contains(x,y)) {
						w.autoOutline(x,y,0,0);
						Roi tmpRoi = new PolygonRoi(w.xpoints, w.ypoints, w.npoints,
								Roi.FREEROI);
						ShapeRoi newShapeRoi = new ShapeRoi(tmpRoi); 
						roi = roi.not(newShapeRoi);
					}
				}
			}
			Thread.yield();
		}	
		return roi;
	}
	
	/**
	 * @param roi
	 */
	private ShapeRoi selectWhitePixel() {
		ShapeRoi roi = null;
		ImagePlus img = result;
		ImageProcessor ip = img.getProcessor();
		Wand w = new Wand(ip);
		int width = result.getWidth();
		int height = result.getHeight(); 
		for (int x=0; x<width; x+=raster) {
			for (int y=0; y<height; y+=raster) {
				if (ip.getPixel(x,y)==255) {
					if (roi == null || !roi.contains(x,y)) {
							w.autoOutline(x, y, 255, 255);
						Roi tmpRoi = new PolygonRoi(w.xpoints, w.ypoints, w.npoints,
								Roi.FREEROI);
						ShapeRoi newShapeRoi = new ShapeRoi(tmpRoi); 
						if (roi == null)
							roi = newShapeRoi;
						else
							roi = roi.or(newShapeRoi);
					}
				}
			}
			Thread.yield();
		}
		return roi;
	}
	
}
