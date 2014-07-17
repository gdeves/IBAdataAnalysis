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

/**
 * The image information consists of the size of the image in voxels and
 * the size of a voxel in an arbitrary length unit. 
 * 
 * @author baecker
 *
 */
public class ImageInfo {
	private int width;
	private int height;
	private int slices;
	private double xSize;
	private double ySize;
	private double zSize;
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getSlices() {
		return slices;
	}
	
	public void setSlices(int slices) {
		this.slices = slices;
	}
	
	public double getXSize() {
		return xSize;
	}
	
	public void setXSize(double xSize) {
		this.xSize = xSize;
	}
	
	public double getYSize() {
		return ySize;
	}
	
	public void setYSize(double ySize) {
		this.ySize = ySize;
	}
	
	public double getZSize() {
		return zSize;
	}
	
	public void setZSize(double zSize) {
		this.zSize = zSize;
	}
}
