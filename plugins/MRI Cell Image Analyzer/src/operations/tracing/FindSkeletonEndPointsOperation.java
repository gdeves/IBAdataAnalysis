package operations.tracing;

import java.awt.Point;
import java.util.Vector;

import ij.ImagePlus;
import ij.gui.PointRoi;
import ij.gui.Roi;
import operations.Operation;
import tracing.SkeletonTreeTracer;

public class FindSkeletonEndPointsOperation extends Operation {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8191239061733423898L;
	protected Vector<Point> resultPoints;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("ij.ImagePlus");
		parameterNames = new String[1];
		parameterNames[0] = "InputImage";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("java.util.Vector");
		resultNames = new String[1];
		resultNames[0] = "ResultPoints";
	}
	
	public void doIt() {
		ImagePlus inputImage = this.getInputImage();
		SkeletonTreeTracer tracer = new SkeletonTreeTracer(inputImage);
		tracer.run();
		resultPoints = tracer.getEndPoints();
	}

	protected void showResult() {
		if (resultPoints==null) return;
		int[] x = new int[resultPoints.size()];
		int[] y = new int[resultPoints.size()];
		for (int i=0; i<resultPoints.size(); i++) {
			x[i] = resultPoints.elementAt(i).x;
			y[i] = resultPoints.elementAt(i).y;
		}
		Roi roi = new PointRoi(x,y,x.length);
		inputImage.setRoi(roi);
	}
	
	public Vector<Point> getResultPoints() {
		return resultPoints;
	}

	public void setResultPoints(Vector<Point> resultPoints) {
		this.resultPoints = resultPoints;
	}
	
}
