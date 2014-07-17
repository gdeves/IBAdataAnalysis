/*
 * Created on 29.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.roi;

import ij.gui.PointRoi;
import ij.measure.ResultsTable;
import operations.Operation;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ObjectsToPointSelectionOperation extends Operation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7336301727416552202L;
	
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
	
	public void doIt() {
		result = this.getInputImage();
		result.killRoi();
		int max = this.getObjects().getCounter();
		int[] xCoord = new int[max];
		int[] yCoord = new int[max];
		for (int currentIndex = 0; currentIndex < max; currentIndex++) {
			int x = (int) Math.round(this.getObjects().getColumn(
			ResultsTable.X_CENTROID)[currentIndex]);
			int y = (int) Math.round(this.getObjects().getColumn(
			ResultsTable.Y_CENTROID)[currentIndex]);
			xCoord[currentIndex] = x;
			yCoord[currentIndex] = y;
		}
		PointRoi roi = new PointRoi(xCoord, yCoord, max);
		result.setRoi(roi);
		result.updateAndDraw();
	}
	
	protected void cleanUpInput() {
		super.cleanUpInput();
		this.setObjects(null);	
	}
}
