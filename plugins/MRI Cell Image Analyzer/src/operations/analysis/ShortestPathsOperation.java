/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker B�cker
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
package operations.analysis;

import java.awt.Point;

import nj.Dijkstra;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.NewImage;
import ij.measure.ResultsTable;
import operations.Operation;

/**
 * Calculate the shortest paths from each of the points passed in to all other points
 * in its neighborhood based on the cost values and vectors from the hessian image.
 * This operation uses the class Dijkstra from NeuronJ (by Erik Meijering).
 * 
 * @author Volker Baecker
 */
public class ShortestPathsOperation extends Operation {
	private static final long serialVersionUID = 625126369337009255L;
	protected HessianImage hessianImage;
	protected ResultsTable points;
	protected ImagePlus resultStack;
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[2];
		parameterTypes[0] = Class.forName("operations.analysis.HessianImage");
		parameterTypes[1] = Class.forName("ij.measure.ResultsTable");
		parameterNames = new String[2];
		parameterNames[0] = "HessianImage";
		parameterNames[1] = "Points";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("ij.ImagePlus");
		resultNames = new String[1];
		resultNames[0] = "ResultStack";
	}
	
	public void doIt() {
		 Dijkstra dijkstra = new Dijkstra();
		 ImageStack stack = new ImageStack(hessianImage.getHessian()[0][0].length, 
		 								   hessianImage.getHessian()[0].length);
		 for (int i=0; i<points.getCounter(); i++) {
		 	ImagePlus result = NewImage.createByteImage("costs", hessianImage.getHessian()[0][0].length, hessianImage.getHessian()[0].length, 1, NewImage.GRAY8);
		 	float x = (float) points.getValueAsDouble(ResultsTable.X_CENTROID, i);
		 	float y = (float) points.getValueAsDouble(ResultsTable.Y_CENTROID, i);
		 	Point p = new Point(Math.round(x),Math.round(y));
		 	byte[][] dirs = dijkstra.run(hessianImage.getHessian(), p);
		 	for (int xIndex = 0; xIndex < dirs[0].length; xIndex++) {
				for (int yIndex = 0; yIndex < dirs.length; yIndex++) {
					int value = (int)Math.round(dirs[yIndex][xIndex]);
					if (value==4 || value==5 || value == 0) {
						value = 1;
					} else {
						value = 0;
					}
					int cost = (int)Math.round(hessianImage.getHessian()[0][yIndex][xIndex]);
					cost = 254 - cost;
					result.getProcessor().putPixel(xIndex, yIndex, cost*value);
				}
			}
		 	stack.addSlice("dirs " + (new Integer(i)).toString(), result.getProcessor());
		 }
		 if (points.getCounter()>0) {
		 	resultStack = new ImagePlus("dirs", stack);
		 } else {
		 	resultStack = new ImagePlus();
		 }
	}
	
	public HessianImage getHessianImage() {
		return hessianImage;
	}

	public void setHessianImage(HessianImage hessianImage) {
		this.hessianImage = hessianImage;
	}

	public ResultsTable getPoints() {
		return points;
	}

	public void setPoints(ResultsTable points) {
		this.points = points;
	}

	public ImagePlus getResultStack() {
		return resultStack;
	}

	public void setResultStack(ImagePlus resultStack) {
		this.resultStack = resultStack;
	}
	
	protected void cleanUpInput() {
		this.setHessianImage(null);
		this.setPoints(null);
	}
}
