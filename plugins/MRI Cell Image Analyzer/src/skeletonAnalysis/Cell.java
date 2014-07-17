package skeletonAnalysis;

import java.util.ArrayList;

import ij.gui.Roi;
import ij.gui.ShapeRoi;

public class Cell extends ShapeRoi {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float intensity;
	private int pixels;
	private float circularity;
	private boolean notNormal;
	private double xCenterOfMass;
	private double yCenterOfMass;
	private ArrayList<PointOfSkeleton> skeleton;
	
	public Cell(Roi r) {
		super(r);
		notNormal = false;
		skeleton = new ArrayList<PointOfSkeleton>();
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public int getPixels() {
		return pixels;
	}

	public void setPixels(int pixels) {
		this.pixels = pixels;
	}

	public float getCircularity() {
		return circularity;
	}

	public void setCircularity(float circularity) {
		this.circularity = circularity;
	}

	public boolean isNotNormal() {
		return notNormal;
	}

	public void setNotNormal(boolean notNormal) {
		this.notNormal = notNormal;
	}

	public double getXCenterOfMass() {
		return xCenterOfMass;
	}

	public void setXCenterOfMass(double centerOfMass) {
		xCenterOfMass = centerOfMass;
	}

	public double getYCenterOfMass() {
		return yCenterOfMass;
	}

	public void setYCenterOfMass(double centerOfMass) {
		yCenterOfMass = centerOfMass;
	}

	public ArrayList<PointOfSkeleton> getSkeleton() {
		return skeleton;
	}

	public void setSkeleton(ArrayList<PointOfSkeleton> skeleton) {
		this.skeleton = skeleton;
	}

}
