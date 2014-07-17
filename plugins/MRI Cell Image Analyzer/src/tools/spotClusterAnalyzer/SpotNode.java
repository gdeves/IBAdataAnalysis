package tools.spotClusterAnalyzer;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.Iterator;

import javax.vecmath.Point3d;

public class SpotNode {
	static public int RED=0; 
	static public int GREEN=1; 
	protected ArrayList<SpotEdge> edges;
	protected ImagePlus image;
	public float x,y,z;
	protected int color = SpotNode.RED;
	protected double maxIntensity;
	protected int id;
	
	public SpotNode(int id, float x, float y, float z, ImagePlus image, double max) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.z = z;
		this.image = image;
		edges = new ArrayList<SpotEdge>();
		this.maxIntensity = max; 
	}

	public int numberOfEdges() {
		return edges.size();
	}
	
	public ImagePlus getImage() {
		return image;
	}
	
	public void beRed() {
		color = RED;
	}
	
	public void beGreen() {
		color = GREEN;
	}
	
	public boolean isRed() {
		return color == RED;
	}
	
	public boolean isGreen() {
		return color == GREEN;
	}
	
	public float distanceTo(SpotNode anOtherNode) {
		float distance = 
			(float)Math.sqrt(((this.x - anOtherNode.x) * (this.x - anOtherNode.x)) +
			((this.y - anOtherNode.y) * (this.y - anOtherNode.y)) +
			((this.z - anOtherNode.z) * (this.z - anOtherNode.z)));
		return distance;
	}

	public void connectWith(SpotNode anOtherNode) {
		SpotEdge edge = new SpotEdge(this, anOtherNode);
		if (!this.edges.contains(edge)) this.edges.add(edge);
		if (!anOtherNode.edges.contains(edge)) anOtherNode.edges.add(edge);
	}
	
	public void removeEdge(SpotEdge anEdge) {
		if (!anEdge.startNode.equals(this) && !anEdge.endNode.equals(this)) return;
		anEdge.startNode.edges.remove(anEdge);
		anEdge.endNode.edges.remove(anEdge);
	}
	
	public void drawWithEdgesOn(ImagePlus stack) {
		this.drawOn(stack);
		Iterator<SpotEdge> it = edges.iterator();
		while(it.hasNext()) {
			SpotEdge edge = it.next();
			edge.drawOn(stack);
		}
	}

	public void drawOn(ImagePlus stack) {
		ImageProcessor ip = stack.getStack().getProcessor(Math.round(z));
		ip.setValue(255);
		ip.drawDot(Math.round(x), Math.round(y));
	}

	public Point3d getPoint() {
		return new Point3d(x,y,z);
	}
	
	public boolean equals(Object anObject) {
		if (anObject.getClass() != this.getClass()) return false;
		SpotNode otherNode = (SpotNode) anObject;
		return (otherNode.x == this.x && otherNode.y==this.y&&otherNode.z==this.z);
	}
	
	public int hashCode() {
		return new Float(this.x).hashCode() ^ 
					new Float(this.y).hashCode() ^
					 new Float(this.z).hashCode();
	}
	
	public ArrayList<SpotEdge> getEdges() {
		return edges;
	}

	public void deleteEdgesWithMinSmallerThan(int thresholdValue) {
		ArrayList<SpotEdge> newEdges = new ArrayList<SpotEdge>();
		Iterator<SpotEdge> it = edges.iterator();
		while(it.hasNext()) {
			SpotEdge edge = it.next();
			if (edge.getMin()>=thresholdValue) 
				newEdges.add(edge);
		}
		this.edges = newEdges;
	}

	public double getMaxIntensity() {
		return this.maxIntensity;
	}

	public int getId() {
		return id;
	}
}
