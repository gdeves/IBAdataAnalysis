package tools.lineProfile3D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import javax.vecmath.Point3d;
import utils.LineProcessor;
import ij.ImagePlus;
import ij.gui.Plot;
import ij.process.ImageProcessor;

public class LineProfile3D {
	protected float x1, y1, z1;
	protected float x2, y2, z2;
	protected ImagePlus image;
	protected float length, delta;
	protected float[] profile = null;
	protected int numberOfSegments;
	protected float length2D;
	
	public LineProfile3D(float x1, float y1, float z1, float x2, float y2, float z2, ImagePlus image) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.image = image;
		if (z2<z1) this.swapPoints();
		this.length = this.calculateLength();
		this.length2D = (float)(new Point2D.Float(x1,y1)).distance(new Point2D.Float(x2,y2));
		this.numberOfSegments = Math.round(z2) - Math.round(z1);
		this.delta = (z2==z1 ? length2D : length2D / (z2-z1));
	}

	protected void calculateProfile() {
		Point3d currentPoint = new Point3d(x1,y1,z1);
		Point3d startPoint = new Point3d(x1,y1,z1);
		Point3d endPoint = new Point3d(x2,y2,z2);
		ArrayList<Float> profile = new ArrayList<Float>();
		ImageProcessor ip = image.getStack().getProcessor((int)Math.round(currentPoint.z));
		float value = ip.getPixelValue((int)Math.round(currentPoint.x), (int)Math.round(currentPoint.y));
		profile.add(value);
		while (LineProcessor.movePointAlongLine(currentPoint, startPoint, endPoint)) {
			ip = image.getStack().getProcessor((int)Math.round(currentPoint.z));
			value = ip.getPixelValue((int)Math.round(currentPoint.x), (int)Math.round(currentPoint.y));
			profile.add(value);
		}
		Iterator<Float> it = profile.iterator();
		this.profile = new float[profile.size()];
		int index = 0;
		while(it.hasNext()) {
			float aValue = it.next();
			this.profile[index] = aValue;
			index++;
		}
	}

	protected float calculateLength() {
		float length = 
			(float)Math.sqrt(((x2-x1)*(x2-x1)) + 
						     ((y2-y1)*(y2-y1)) +
						     ((z2-z1)*(z2-z1)));
		return length;
	}

	protected void swapPoints() {
		float tx, ty, tz;
		tx = x1;
		ty = y1;
		tz = z1;
		x1 = x2;
		y1 = y2;
		z1 = z2;
		x2 = tx;
		y2 = ty;
		z2 = tz;
	}
	
	public Point3d getStartPoint() {
		return new Point3d(x1,y1,z1);
	}
	
	public Point3d getEndPoint() {
		return new Point3d(x2,y2,z2);
	}

	public float getLength() {
		return length;
	}

	public float[] getProfile() {
		if (this.profile==null) this.calculateProfile();
		return profile;
	}
	
	public void show() {
		float[] indices = new float[getProfile().length];
		for (int i=0; i<profile.length; i++) {
			indices[i] = i;
		}
		Plot plot = new Plot("3d line profile", "Distance", "Gray Value", indices, profile);	
		plot.show();
	}
}
