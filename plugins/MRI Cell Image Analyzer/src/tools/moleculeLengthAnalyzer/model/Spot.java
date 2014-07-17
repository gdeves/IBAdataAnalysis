/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2009 INSERM 
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * (www.mri.cnrs.fr) by Volker Bäcker
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
package tools.moleculeLengthAnalyzer.model;

import java.text.NumberFormat;

import javax.vecmath.Point3d;

/**
 * A spot is a 3D point that besides of the coordinates has a unique id.
 * 
 * @author baecker
 *
 */
public class Spot {
	/**
	 * The unique id of the spot
	 */
	private int id;
	/**
	 * The coordinates of the spot
	 */
	private Point3d point;
	
	public Spot(int id, double x, double y, double z) {
		this.id = id;
		this.point = new Point3d(x,y,z);
	}
	
	public int getId() {
		return id;
	}
	
	public double getX() {
		return point.x;
	}
	
	public double getY() {
		return point.y;
	}
	
	public double getZ() {
		return point.z;
	}
	
	public double distance(Spot aSpot) {
		return this.point.distance(aSpot.point);
	}
	
	public String toString() {
		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(2);
		String xText = format.format(this.getX());
		String yText = format.format(this.getY());
		String zText = format.format(this.getZ());
		String result = this.getClass().getSimpleName() + "[" + this.getId() + ", " + xText + ", " + yText + ", " + zText + "]";
		return result;
	}

	public Point3d getPointInPixelCoords(ImageInfo info) {
		int x = (int)Math.round(this.getX() / info.getXSize());
		int y = (int)Math.round(this.getY() / info.getYSize());
		int z = (int)Math.round(this.getZ() / info.getZSize());
		Point3d point = new Point3d(x, y, z);
		return point;
	}
}
