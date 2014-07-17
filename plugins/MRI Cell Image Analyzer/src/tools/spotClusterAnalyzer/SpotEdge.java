package tools.spotClusterAnalyzer;

import javax.vecmath.Point3d;
import tools.lineProfile3D.LineProfile3D;
import utils.LineProcessor;
import ij.ImagePlus;
import ij.process.ImageProcessor;

public class SpotEdge {
	protected SpotNode startNode;
	protected SpotNode endNode;
	protected float[] profil;

	public SpotEdge(SpotNode from, SpotNode to) {
		this.startNode = from;
		this.endNode = to;
		this.measure(from, to);
	}

	private void measure(SpotNode from, SpotNode to) {
		ImagePlus image = from.getImage();
		LineProfile3D profil = new LineProfile3D(from.x, from.y, from.z, to.x, to.y, to.z, image);
		this.profil = profil.getProfile();
	}

	public void drawOn(ImagePlus stack) {
		Point3d startPoint = startNode.getPoint();
		Point3d endPoint = endNode.getPoint();
		Point3d currentPoint = startNode.getPoint();
		ImageProcessor ip = stack.getStack().getProcessor((int)Math.round(currentPoint.z));
		ip.setValue(255);
		ip.drawPixel((int)Math.round(currentPoint.x), 
					 (int)Math.round(currentPoint.y));
		while (LineProcessor.movePointAlongLine(currentPoint, startPoint, endPoint)) {
			ip = stack.getStack().getProcessor((int)Math.round(currentPoint.z));
			ip.setValue(255);
			ip.drawPixel((int)Math.round(currentPoint.x), 
						 (int)Math.round(currentPoint.y));
		}
	}

	public boolean equals(Object anObject) {
		if (anObject.getClass()!=this.getClass()) return false;
		SpotEdge otherEdge = (SpotEdge) anObject;
		if (this.startNode.equals(otherEdge.startNode) && this.endNode.equals(otherEdge.endNode)) return true;
		if (this.startNode.equals(otherEdge.endNode) && this.endNode.equals(otherEdge.startNode)) return true;
		return false;
	}
	
	public int hashCode() {
		return startNode.hashCode() ^ endNode.hashCode(); 
	}
	
	public float getMin() {
		float min = Integer.MAX_VALUE;
		for (int i=0; i<profil.length; i++) {
			float value = profil[i];
			if (value<min) min = value;
		}
		return min;
	}
	
	public double cost() {
		double max = startNode.getMaxIntensity();
		double result = 0;
		for (int i=0; i<profil.length; i++) {
			float value = profil[i];
			result += value;
		}
		result = result / (max * profil.length);
		return result;
	}
}
