/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2008 Volker B�cker
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
package tools.grid;

import java.awt.geom.Point2D;

/**
 * A grid point knows its coordinates in the image and its coordinates on the grid.
 * 
 * @author	Volker Baecker
 **/
public class GridPoint {
	public Point2D point;
	public int i;
	public int j;
	
	public GridPoint(double x, double y, int i, int j) {
		point = new Point2D.Double(x,y);
		this.i=i;
		this.j=j;
	}
	
	public double distance(Point2D aPoint) {
		return point.distance(aPoint);
	}

}
