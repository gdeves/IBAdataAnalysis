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
package utils;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.vecmath.Point3d;

/**
 * A utility class to handle line segments. 
 * 
 * @author	Volker Baecker
 **/
public class LineProcessor {
	public static Line2D copyRotated(Line2D aLine, Point2D aPoint, double angle) {
		AffineTransform at = AffineTransform.getRotateInstance((Math.PI/180)*angle,aPoint.getX(),aPoint.getY());
		Point2D start = new Point2D.Float();
		Point2D end = new Point2D.Float();
		at.transform(aLine.getP1(), start);
		at.transform(aLine.getP2(), end);
		Line2D result = new Line2D.Float(start, end);
		return result;
	}
	
	public static void rotate(Line2D aLine, Point2D aPoint, double angle) {
		AffineTransform at = AffineTransform.getRotateInstance((Math.PI/180)*angle,aPoint.getX(),aPoint.getY());
		Point2D start = aLine.getP1();
		Point2D end = aLine.getP2();
		at.transform(aLine.getP1(), start);
		at.transform(aLine.getP2(), end);
		aLine.setLine(start, end);
	}
	
	public static Line2D copyFirstSegment(Line2D aLine, double length) {
		double lineLength = aLine.getP1().distance(aLine.getP2());
		double factor = length / lineLength;
		AffineTransform at = AffineTransform.getScaleInstance(factor, factor);
		Point2D delta = new Point2D.Double(aLine.getX2()-aLine.getX1(), aLine.getY2()-aLine.getY1());
		Point2D newEndPoint = new Point2D.Float();
		at.transform(delta, newEndPoint);
		at = AffineTransform.getTranslateInstance(aLine.getX1(), aLine.getY1());
		Point2D resultPoint = new Point2D.Float();
		at.transform(newEndPoint, resultPoint);
		Line2D result = new Line2D.Float(aLine.getP1(), resultPoint);
		return result;
	}
	
	public static void reduceToFirstSegment(Line2D aLine, double length) {
		double lineLength = aLine.getP1().distance(aLine.getP2());
		double factor = length / lineLength;
		AffineTransform at = AffineTransform.getScaleInstance(factor, factor);
		Point2D delta = new Point2D.Double(aLine.getX2()-aLine.getX1(), aLine.getY2()-aLine.getY1());
		Point2D newEndPoint = new Point2D.Float();
		at.transform(delta, newEndPoint);
		at = AffineTransform.getTranslateInstance(aLine.getX1(), aLine.getY1());
		Point2D resultPoint = new Point2D.Float();
		at.transform(newEndPoint, resultPoint);
		aLine.setLine(aLine.getP1(), resultPoint);
	}
	
	public static Line2D perpendicularLine(Line2D baseLine, double length) {
		Line2D part1 = copyRotated(baseLine, baseLine.getP1(), -90);
		reduceToFirstSegment(part1, length);
		Line2D part2 = copyRotated(baseLine, baseLine.getP1(), 90);
		reduceToFirstSegment(part2, length);
		Line2D result = new Line2D.Float(part1.getP2(), part2.getP2());
		return result;
	}
	
	public static boolean movePointAlongLine(Point2D currentPoint, Line2D aLine) {
		double dx = aLine.getX2()-aLine.getX1();
		double dy = aLine.getY2()-aLine.getY1();
		double n = Math.sqrt(dx*dx + dy*dy);
		if (n<=Double.MIN_VALUE) return false;
		double xinc = dx/n;
		double yinc = dy/n;
		double rx = currentPoint.getX();
		double ry = currentPoint.getY();
		rx += xinc;
		ry += yinc;
		if (((xinc>=0 && aLine.getP2().getX()<rx) || (xinc<0 && aLine.getP2().getX()>rx)) || 
			((yinc>=0 && aLine.getP2().getY()<ry) || (yinc<0 && aLine.getP2().getY()>ry))) 
			return false;
		currentPoint.setLocation(rx,ry);
		return true;
	}
	
	public static boolean movePointAlongLine(Point3d currentPoint, Point3d startPoint, Point3d endPoint) {
		double dx = endPoint.getX()-startPoint.getX();
		double dy = endPoint.getY()-startPoint.getY();
		double dz = endPoint.getZ()-startPoint.getZ();
		double n = Math.sqrt(dx*dx + dy*dy + dz*dz);
		if (n<=Double.MIN_VALUE) return false;
		double xInc = dx/n;
		double yInc = dy/n;
		double zInc = dz/n;
		double rx = currentPoint.getX();
		double ry = currentPoint.getY();
		double rz = currentPoint.getZ();
		rx += xInc;
		ry += yInc;
		rz += zInc;
		if (((xInc>=0 && endPoint.getX()<rx) || (xInc<0 && endPoint.getX()>rx)) || 
			((yInc>=0 && endPoint.getY()<ry) || (yInc<0 && endPoint.getY()>ry)) || 
			((zInc>=0 && endPoint.getZ()<rz) || (zInc<0 && endPoint.getZ()>rz))) 
			return false;
		currentPoint.set(rx,ry,rz);
		return true;
	}
	
	public static boolean movePointIntoDirectionOf(Point2D currentPoint, Line2D aLine) {
		double dx = aLine.getX2()-aLine.getX1();
		double dy = aLine.getY2()-aLine.getY1();
		double n = Math.sqrt(dx*dx + dy*dy);
		double xinc = dx/n;
		double yinc = dy/n;
		double rx = currentPoint.getX();
		double ry = currentPoint.getY();
		rx += xinc;
		ry += yinc;
		currentPoint.setLocation(rx,ry);
		return true;
	}
	
	public static Point2D middlePointOf(Line2D aLine) {
		double xMiddle = aLine.getX1() + ((aLine.getX2() - aLine.getX1()) /2.0);
		double m = (aLine.getY2() - aLine.getY1()) / (aLine.getX2() - aLine.getX1());
		double yMiddle = (m * (xMiddle - aLine.getX1())) + aLine.getY1();
		return new Point2D.Float((float)xMiddle, (float)yMiddle);
	}
	
	public static boolean movePointIntoDirectionOfBy(Point2D currentPoint, Line2D aLine, float delta) {
		double dx = aLine.getX2()-aLine.getX1();
		double dy = aLine.getY2()-aLine.getY1();
		double n = Math.sqrt(dx*dx + dy*dy);
		double xinc = dx/n;
		double yinc = dy/n;
		double rx = currentPoint.getX();
		double ry = currentPoint.getY();
		rx += delta*xinc;
		ry += delta*yinc;
		currentPoint.setLocation(rx,ry);
		return true;
	}

	public static Line2D inverseLine(Line2D line) {
		return new Line2D.Double(line.getX2(), line.getY2(), line.getX1(), line.getY1());
	}
} 
