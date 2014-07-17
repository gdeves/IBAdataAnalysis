/*
 * Created on 17.11.2005
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
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;

import operations.Operation;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SelectAllObjectsOperation extends Operation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3534411207539876432L;
	
	protected ResultsTable objects;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterTypes[1] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[2];
		parameterNames[0] = "InputImage";
		parameterNames[1] = "Objects";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "Result";
	}
	/**
	 * @return Returns the objects.
	 */
	public ResultsTable getObjects() {
		return objects;
	}
	/**
	 * @param objects The objects to set.
	 */
	public void setObjects(ResultsTable objects) {
		this.objects = objects;
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setObjects(null);	
	}
	
	public void doIt() {
		result = this.getCopyOfOrReferenceTo(inputImage,  inputImage.getTitle());
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
		PolygonRoi roi = null;
		ShapeRoi totalRoi = null;
		for (int currentIndex = 0; currentIndex < this.getObjects()
				.getCounter(); currentIndex++) {
			int x = (int) Math.round(this.getObjects().getColumn(
					ResultsTable.X_CENTROID)[currentIndex]);
			int y = (int) Math.round(this.getObjects().getColumn(
					ResultsTable.Y_CENTROID)[currentIndex]);
			double t1 = ip.getMinThreshold();
			if (t1 == ImageProcessor.NO_THRESHOLD)
				w.autoOutline(x, y);
			else
				w.autoOutline(x, y, (int) t1, (int) ip.getMaxThreshold());
			roi = new PolygonRoi(w.xpoints, w.ypoints, w.npoints,
					Roi.TRACED_ROI);
			if (w.npoints > 0) {
				if (totalRoi == null) {
					totalRoi = new ShapeRoi(roi);
				} else {
					totalRoi.or(new ShapeRoi(roi));
				}
			}
		}
		img.getProcessor().setRoi(totalRoi);
		img.setRoi(totalRoi);
		img.setProcessor(null, img.getProcessor());
	}
}
