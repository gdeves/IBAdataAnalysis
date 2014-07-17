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
package tools;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Observable;
import java.util.Observer;

import broadcaster.WindowManagerBroadcaster;


import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.NewImage;

/**
 * Opens a window that shows a magnified view of the region under the mouse-pointer of the current image.
 * The region is updated as the mouse pointer is moved along the image.
 * 
 * @author	Volker Baecker
 **/
public class PixelSpy implements MouseMotionListener, WindowListener, Observer {
	protected ImagePlus inputImage;
	static protected int size = 80;
	protected ImageWindow imageWindow;
	static protected PixelSpy current;
	
	public PixelSpy(ImagePlus imp) {
		super();
		int type = NewImage.GRAY8;
		int depth = 8;
		if (imp != null) {
			type = imp.getType()==4?3:imp.getType();
			depth = imp.getBitDepth();
		}
		imageWindow = new ImageWindow(NewImage.createImage("Pixel Spy", size, size, 1, depth, type));
		if (type!=3 && imp !=null) {
			imageWindow.getImagePlus().getProcessor().setMinAndMax(imp.getProcessor().getMin(), imp.getProcessor().getMax());
		}
		imageWindow.addWindowListener(this);
		WindowManagerBroadcaster.getInstance().addObserver(this);
		IJ.getInstance().addWindowListener(this);
		inputImage = imp;
		spyOnImage(imp);
	}

	protected void spyOnImage(ImagePlus imp) {
		if (imp == null) return;
		ImageWindow oldImageWindow = imageWindow;
		oldImageWindow.setVisible(false);	// added
		imageWindow.setVisible(false);	// added
		imageWindow = new ImageWindow(NewImage.createImage("Pixel Spy", size, size, 1, imp.getBitDepth(), (imp.getType()==4?3:imp.getType())));
		if (imp.getType()!=ImagePlus.COLOR_RGB) {
			imageWindow.getImagePlus().getProcessor().setMinAndMax(imp.getProcessor().getMin(), imp.getProcessor().getMax());	
		}
		if (imageWindow.getCanvas().getMagnification()<oldImageWindow.getCanvas().getMagnification()) this.zoomIn(imageWindow, oldImageWindow);
		if (imageWindow.getCanvas().getMagnification()>oldImageWindow.getCanvas().getMagnification()) this.zoomOut(imageWindow, oldImageWindow);
		imageWindow.setBounds(oldImageWindow.getBounds());
		imageWindow.setExtendedState(ImageWindow.MAXIMIZED_BOTH);
		imageWindow.setExtendedState(ImageWindow.NORMAL);
		oldImageWindow.close();
		imageWindow.addWindowListener(this);
		imp.getWindow().getCanvas().addMouseMotionListener(this);
		inputImage = imp;
		this.copyImagePlus(imp);
		imageWindow.setVisible(true);	// added
	}
	
	private void zoomOut(ImageWindow win, ImageWindow oldWin) {
		while(win.getCanvas().getMagnification()>oldWin.getCanvas().getMagnification()) {
			if (win==null) return;
			ImageCanvas ic = win.getCanvas();
			int x1 = size/2;
			int y1 = size/2;
			ic.zoomOut(x1, y1);
			}
	}

	private void zoomIn(ImageWindow win, ImageWindow oldWin) {
		while(win.getCanvas().getMagnification()<oldWin.getCanvas().getMagnification()) {
			if (win==null) return;
			ImageCanvas ic = win.getCanvas();
			int x1 = size/2;
			int y1 = size/2;
			ic.zoomIn(x1, y1);			
		}
	}
	
	public void copyImagePlus(int xIn, int yIn) {
		if (IJ.spaceBarDown()) return;
		int xSource = inputImage.getWindow().getCanvas().offScreenX(xIn);
		int ySource = inputImage.getWindow().getCanvas().offScreenY(yIn);
		if (inputImage.getType()!=ImagePlus.COLOR_RGB) {
			imageWindow.getImagePlus().getProcessor().setColorModel(inputImage.getProcessor().getColorModel());
		}
		if (size % 2 !=0) size++;
		for (int x=0; x<size; x++) {
			for (int y=0; y<size; y++) {
				imageWindow.getImagePlus().getProcessor().putPixel(x,y,inputImage.getPixel(xSource-(size/2)+x,
						  ySource-(size/2)+y));
			}
		}
	 	imageWindow.getImagePlus().updateAndRepaintWindow();
	 	imageWindow.getImagePlus().getProcessor().setColor(Color.YELLOW);
	 	imageWindow.getImagePlus().getProcessor().drawDot(size/2, size/2);
	}
	
	public void copyImagePlus(ImagePlus imp) {
		this.copyImagePlus(0, 0);
	}

	public void mouseDragged(MouseEvent event) {
		if (inputImage.getWindow()!=this.imageWindow) {
			this.copyImagePlus(event.getX(), event.getY());
		}
	}

	public void mouseMoved(MouseEvent event) {
		if (inputImage.getWindow()!=this.imageWindow) {
			this.copyImagePlus(event.getX(), event.getY());
		}
	}
	
	public void windowClosing(WindowEvent e) {
	 	imageWindow.removeWindowListener(this);
	 	if (inputImage != null) {
	 		inputImage.getWindow().getCanvas().removeMouseMotionListener(this);
	 	}
	 	WindowManagerBroadcaster.getInstance().deleteObserver(this);
	 	imageWindow.close();
	 	current = null;
	 }
	 
	 public void windowActivated(WindowEvent e) {
		 // noop
	}

	public void update(Observable arg0, Object anAspect) {
		ImageWindow old = WindowManager.getCurrentWindow();
		ImagePlus newInputImage = WindowManager.getCurrentImage();
		if (newInputImage==null || newInputImage.getWindow()==null ) return;
		if (imageWindow.getImagePlus()==newInputImage) return;
		if (newInputImage==inputImage) return;
		if (anAspect.equals("currentImage")) {
			if (inputImage != null && inputImage.getWindow()!=null) {
				inputImage.getWindow().getCanvas().removeMouseMotionListener(this);
			}
			this.spyOnImage(newInputImage);
		}
		WindowManager.setWindow(old);
	}

	public void updateAnywaw() {
		ImageWindow old = WindowManager.getCurrentWindow();
		ImagePlus newInputImage = WindowManager.getCurrentImage();
		if (inputImage != null && inputImage.getWindow()!=null) {
			inputImage.getWindow().getCanvas().removeMouseMotionListener(this);
		}
		this.spyOnImage(newInputImage);
		WindowManager.setWindow(old);
	}

	public void windowOpened(WindowEvent arg0) {
		// noop
	}

	public void windowClosed(WindowEvent arg0) {
		// noop
	}

	public void windowIconified(WindowEvent arg0) {
		// noop
	}

	public void windowDeiconified(WindowEvent arg0) {
		// noop
	}

	public void windowDeactivated(WindowEvent arg0) {
		// noop
	}

	public void setVisible(boolean b) {
		imageWindow.setVisible(b);
	}
	
	public static PixelSpy getCurrent(ImagePlus imp) {
		if (current==null) current = new PixelSpy(imp);
		return current;
	}
	
	public static boolean isActive() {
		return current != null;
	}
	
	public static PixelSpy getCurrent() {
		return current;
	}
}
