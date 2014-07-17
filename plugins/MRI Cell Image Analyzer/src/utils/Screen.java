package utils;

import ij.IJ;
import ij.ImageJ;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

public class Screen {
	static public Rectangle getMaxWindow() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle maxWindow = ge.getMaximumWindowBounds();
		ImageJ ij = IJ.getInstance();
		Dimension ijSize = ij!=null?ij.getSize():new Dimension(0,0);
		maxWindow.y += ijSize.height;
		maxWindow.height -= ijSize.height;
		return maxWindow;
	}
}
