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

import javax.vecmath.Point3d;

import utils.LineProcessor;

import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * A molecule consists of a green and a red spot in the 3d space. The length 
 * of the molecule is the distance between the two spots.
 *  
 * @author baecker
 *
 */
public class Molecule {
	private Spot greenSpot;
	private Spot redSpot;
	private double length;
	
	public Molecule(Spot green, Spot red, double length) {
		this.greenSpot = green;
		this.redSpot = red;
		this.length = length;
	}
	
	public Spot getGreenSpot() {
		return greenSpot;
	}
	
	public Spot getRedSpot() {
		return redSpot;
	}

	public double getLength() {
		return length;
	}
	
	@Override
	public String toString() {
		String result = "Molecule[" + this.getGreenSpot().toString() + " - " + this.redSpot.toString() + " - " + this.getLength() + "]";
		return result;
	}

	public void drawOn(ImagePlus image, ImageInfo info) {
		Point3d startPoint = greenSpot.getPointInPixelCoords(info);
		Point3d endPoint = redSpot.getPointInPixelCoords(info);
		Point3d currentPoint = greenSpot.getPointInPixelCoords(info);
		ImageProcessor ip = image.getStack().getProcessor((int)currentPoint.z);
		ip.setValue(255);
		ip.drawPixel((int)Math.round(currentPoint.x), (int)Math.round(currentPoint.y));
		if ((int)startPoint.x == (int)endPoint.x && (int)startPoint.y == (int)endPoint.y && (int)startPoint.z == (int)endPoint.z)
			return;
		while (LineProcessor.movePointAlongLine(currentPoint, startPoint, endPoint)) {
			ip = image.getStack().getProcessor((int)currentPoint.z);
			ip.setValue(255);
			ip.drawPixel((int)Math.round(currentPoint.x), 
					 (int)Math.round(currentPoint.y));
		}
	}

}
