package tools.multipleImageWindow;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

import ij.ImagePlus;
import ij.gui.ImageCanvas;


public class ImageCanvasPlus extends ImageCanvas {

	private static final long serialVersionUID = 4620196174966156635L;
	private FourImageWindowView window;
	private int position;

	public ImageCanvasPlus(ImagePlus imp, FourImageWindowView window, int position) {
		super(imp);
		this.window = window;
		this.position = position;
	}

	public void zoomIn(int x, int y) {
		if (magnification>=32)
			return;
		double newMag = getHigherZoomLevel(magnification);
		int newWidth = (int)(imageWidth*newMag);
		int newHeight = (int)(imageHeight*newMag);
		Dimension newSize = new Dimension(newWidth, newHeight);
		if (newSize!=null) {
			setDrawingSize(newSize.width, newSize.height);
			setMagnification(newMag);
			imp.getWindow().pack();
		}
		repaint();
		updateDividerPositions();
	}
	
	public void zoomOut(int x, int y) {
		super.zoomOut(x, y);
		updateDividerPositions();
	}
	
	public void unzoom() {
		super.unzoom();
		updateDividerPositions();
	}
	
	public void updateDividerPositions() {
		if (position == FourImageWindow.UPPERLEFT) {
			window.setVerticalDividerPosition((int)Math.round(dstWidth));
			window.setHorizontalDividerPosition((int)Math.round(dstHeight));
		}
		if (position == FourImageWindow.UPPERRIGHT) {
			window.setVerticalDividerPosition((int)Math.round(window.getContentWidth()-dstWidth));
			window.setHorizontalDividerPosition((int)Math.round(dstHeight));
		}
		if (position == FourImageWindow.LOWERLEFT) {
			window.setVerticalDividerPosition((int)Math.round(dstWidth));
			window.setHorizontalDividerPosition((int)Math.round(window.getContentHeight()-dstHeight));
		}
		if (position == FourImageWindow.LOWERRIGHT) {
			window.setVerticalDividerPosition((int)Math.round(window.getContentWidth()-dstWidth));
			window.setHorizontalDividerPosition((int)Math.round(window.getContentHeight()-dstHeight));
		}
	}
	
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int ox = offScreenX(x);
		int oy = offScreenY(y);
		if (ox>imp.getWidth() || ox<0) return;
		if (oy>imp.getHeight() || oy<0) return;
		super.mousePressed(e);
	}
}
