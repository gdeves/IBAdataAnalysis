/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker Bäcker
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * www.mri.cnrs.fr
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package utils;

import ij.ImagePlus;
import ij.gui.Plot;

import java.util.ArrayList;
import java.util.Iterator;

import operations.channel.MergeChannelsOperation;

/**
 * Given a function f[t] as a list of values, answer a list of local maxima of the 
 * curve that lie above a given level. For each maximum the timepoint and the height is calculated.
 * The height is the distance between the maximum and the closest minimum to the left. The minimum
 * is the smallest value between the maximum and the closest maximum to the left. 
 * 
 * @author Volker Bäcker
 *
 */
public class CurveMaximaFinder {

	protected double minHeight = 0;			// Only maxima above minHeight will be taken into account. Min height is a value between 0 and 1.
	protected int minDistance = 10;			// The minimal distance between two maxima.
	protected double[] curve = new double[0];	// A list of values representing the values of the function f[t], t=1,..,N.
	protected ArrayList<Integer> maxima = null;
	protected ArrayList<Double> heights = null;
	protected ArrayList<Double> yMaxima = null; 
	protected double globalMax;				// the highest y-value of the whole curve
	protected double[] xValues;
	protected Plot maximaPlot;
	protected Plot curvePlot;
	

	/**
	 * Create a new CurveMaximaFinder for the the given curve.
	 * @param aCurve	A list of values representing a curve f[t] for t=1,..,N
	 */
	public CurveMaximaFinder(double[] aCurve) {
		curve = aCurve;
		findGlobalMax();
	}
	
	/**
	 * Create a new CurveMaximaFinder for the the given curve.
	 * @param aCurve	A list of values representing a curve f[t] for t=1,..,N
	 */
	public CurveMaximaFinder(float[] aCurve) {
		curve = new double[aCurve.length];
		for (int i=0; i<aCurve.length;i++) {
			curve[i] = aCurve[i];
		}
		findGlobalMax();
	}

	/**
	 * Answer a list of x-coordinates of the maxima.
	 */
	public ArrayList<Integer> getMaxima() {
		if (maxima==null) findMaxima();
		return maxima;
	}

	/**
	 * Answer a list of y-coordinates of the maxima.
	 */
	public ArrayList<Double> getHeights() {
		if (maxima==null) findMaxima();
		return heights;
	}

	/**
	 * A plot og the maxima of the curve. The maxima are shown with their height compared
	 * to lowest point between the maximum and the next maximum to the left. 
	 * 
	 * @return	Plot 	A plot of the maxima
	 */
	public Plot getMaximaPlot() {
		if (maximaPlot == null) createMaximaPlot();
		return maximaPlot;
	}

	/**
	 * Answer a plot of the curve.
	 */
	public Plot getCurvePlot() {
		if (curvePlot==null) createCurvePlot();
		return curvePlot;		
	}

	/**
	 * Get an image of the overlay of the curve and the maxima.
	 */
	public ImagePlus getCurveAndMaximaImage() {
		ImagePlus curveImage = this.getCurvePlot().getImagePlus();
		curveImage.getProcessor().invert();
		ImagePlus maximaImage = this.getMaximaPlot().getImagePlus();
		maximaImage.getProcessor().invert();
		MergeChannelsOperation op = new MergeChannelsOperation();
		op.setInputImageRed(maximaImage);
		op.setInputImageGreen(curveImage);
		op.setInputImageBlue(maximaImage);
		op.setShowResult(false);
		op.setBlueChannel("none");
		op.setRedChannel("red input");
		op.setGreenChannel("green input");
		op.run();
		return op.getResult();
	}

	/**
	 * Answer the minimum height. Only maxima above that value will be taken into account.
	 * The value refers to the normalized height and is between 0 and 1. 
	 */
	public double getMinHeight() {
		return minHeight;
	}

	/**
	 * Set the minium height. Only maxima above that value will be taken into account.
	 * The value refers to the normalized height and is between 0 and 1.  
	 */
	public void setMinHeight(double d) {
		minHeight = d;
		if (minHeight>1) minHeight = 1;
		if (minHeight<0) minHeight = 0;
	}
	
	public int getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(int minDistance) {
		this.minDistance = minDistance;
	}

	protected void findGlobalMax() {
		globalMax = -Float.MAX_VALUE;
		for (int i=0; i<curve.length; i++) {
			if (curve[i]>globalMax) globalMax = curve[i];
		}
	}

	/**
	 * Find all local maxima without taking into account the minHeight.
	 */
	protected void findMaxima() {
		int dist = getMinDistance();
		maxima = new ArrayList<Integer>();
		heights = new ArrayList<Double>();
		yMaxima = new ArrayList<Double>();
		for (int i=1; i<curve.length-1; i++) {
			if (isBiggestIn(i-dist,i+dist,curve[i])) maxima.add(new Integer(i));
		}
		calculateHeightsOfMaxima();
		if (getMinHeight()>0) removeSmallMaxima();
	}

	protected void removeSmallMaxima() {
		ArrayList<Integer> filteredMaxima = new ArrayList<Integer>();
		ArrayList<Double> filteredHeights = new ArrayList<Double>();
		ArrayList<Double> filteredYMaxima = new ArrayList<Double>();
		for(int i=0; i<maxima.size(); i++) {
			Double value = heights.get(i);
			if (value.floatValue()/globalMax>=minHeight) {
				filteredMaxima.add(maxima.get(i));
				filteredHeights.add(value);
				filteredYMaxima.add(yMaxima.get(i));
			}
		}
		maxima = filteredMaxima;
		heights = filteredHeights;
		yMaxima = filteredYMaxima;
	}

	protected boolean isBiggestIn(int start, int end, double max) {
		if (start<0) return false;
		if (end>curve.length-1) return false;
		boolean result = true;
		for (int i=start; i<=end; i++) {
			if (curve[i]>max) result = false;
		}
		return result;
	}

	/**
	 * Calculate the heights of the maxima. The height of a maximum is defined as the vertical
	 * distance between the maximum and the lowest local minimum between this maximum and the
	 * next local maximum to the left.
	 *
	 */
	protected void calculateHeightsOfMaxima() {
		Iterator<Integer> it = maxima.iterator();
		int index = 0;
		while(it.hasNext()) {
			int maximumX = it.next().intValue();
			int firstX = 0;
			if (index>0) {
				firstX = maxima.get(index-1).intValue()+1;
			}
			int xLowest = lowestPointBetween(firstX, maximumX);
			double height = curve[maximumX] - curve[xLowest];
			heights.add(new Double(height));
			yMaxima.add(new Double(curve[maximumX]));
			index++;
		}
	}

	/**
	 * Find the lowest point in the curve between firstX and maximumX.
	 * 
	 * @param firstX
	 * @param maximumX
	 * @return	The x coordinate of the lowest point between firstX and maximumX.
	 */
	protected int lowestPointBetween(int firstX, int maximumX) {
		double value = Integer.MAX_VALUE;
		int x = firstX;
		for (int i=firstX; i<maximumX; i++) {
			if (curve[i]<value) {
				value = curve[i];
				x = i;
			}
		}
		return x;
	}

	protected void createCurvePlot() {
		curvePlot = new Plot("curve", "t", "F", getXValues(), curve, Plot.LINE);
		curvePlot.setLimits(0, curve.length-1, 0, globalMax);
	}
	
	/**
	 *  Answer a float array of the x-coordinates 0,...,N of the curve.
	 */
	protected double[] getXValues() {
		if (xValues == null) {
			this.calculateXValues();
		}
		return xValues;
	}

	/**
	 * Construct a float array of the x-coordinates 0,...,N of the curve.
	 *
	 */
	protected void calculateXValues() {
		xValues = new double[curve.length];
		for (int i=0; i<curve.length; i++) {
			xValues[i]=i;
		}
	}

	protected void createMaximaPlot() {
		double[] yValues = new double[curve.length];
		int index = 0;
		for (int i=0; i<curve.length; i++) {
			float value = 0;
			if (index < maxima.size() && i==maxima.get(index).intValue()) {
				value = heights.get(index).floatValue();
				index++;
			}
			yValues[i] = value;
		}
		maximaPlot = new Plot("maxima", "t", "F", xValues, yValues, Plot.LINE);
		maximaPlot.setLimits(0, curve.length-1, 0, globalMax);
	}
	
	public void removeLeftmostMaximum() {
		if (this.maxima==null) this.findMaxima();
		maxima.remove(0);
		heights.remove(0);
		yMaxima.remove(0);
	}
	
	public int getNumberOfMaxima() {
		if (this.maxima==null) this.findMaxima();
		return this.maxima.size();
	}

	public ArrayList<Double> getYMaxima() {
		if (maxima==null) findMaxima();
		return yMaxima;
	}
}
