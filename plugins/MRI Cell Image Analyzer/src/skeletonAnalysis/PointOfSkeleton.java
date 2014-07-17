package skeletonAnalysis;

import java.awt.Point;
import java.util.ArrayList;

public class PointOfSkeleton extends Point {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<PointOfSkeleton> neighbors;
	
	public PointOfSkeleton(int i, int j) {
		super(i,j);
		neighbors = new ArrayList<PointOfSkeleton>();
	}

	public ArrayList<PointOfSkeleton> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(ArrayList<PointOfSkeleton> neighbors) {
		this.neighbors = neighbors;
	}

}
