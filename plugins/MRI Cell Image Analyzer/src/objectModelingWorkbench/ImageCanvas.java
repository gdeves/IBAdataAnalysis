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
package objectModelingWorkbench;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import ij.util.Java2;
import imagejProxies.ImageJAccessProxy;
import imagejProxies.Proxy;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import utils.Broadcaster;

/**
 * The image canvas that is used in the ObjectModelingWorkbench. It can display the transparent overlay
 * of two images. It takes into account the translation and rotation and displays objects in the ObjectManger.
 * 
 * @author Volker B�cker
 */
public class ImageCanvas extends ij.gui.ImageCanvas implements Observer {
	private static final long serialVersionUID = 3911048278762863753L;
	protected ImagePlus image;
	protected ImagePlus secondImage;
	protected int offsetX;
	protected int offsetY;
	protected int angle;
	protected int secondImageOffsetX;
	protected int secondImageOffsetY;
	protected int secondImageAngle;
	public int tmpSecondImageOffsetX;
	public int tmpSecondImageOffsetY;
	public int tmpSecondImageAngle;
	public int distance = 1;
	public int deltaAngle = 1;
	protected int scrollX = 0;
	protected int scrollY = 0;
	protected ImageDisplayOptions imageOptions;
	protected ImageDisplayOptions secondImageOptions;
	protected boolean bufferStrategySet = false;
	protected int width;
	protected int height;

	protected Broadcaster broadcaster;

	protected Object3DManager objectManager;

	protected ImageJAccessProxy proxy = new ImageJAccessProxy();
	protected Object[] params = new Object[0];
	private int eventX;
	private int eventY;
	
	public ImageCanvas() {
		super(new ImagePlus());
	}

	public void setImage(ImagePlus anImage) {
		image = anImage;
		updateImage(anImage);
	}

	public void update(Graphics graphics) {
		BufferStrategy strategy = this.getBufferStrategy();
		Graphics bufferGraphics = strategy.getDrawGraphics();
		bufferGraphics.clearRect(0,0,this.getWidth()-1,this.getHeight()-1);
		super.update(bufferGraphics);
		this.repaint();
	}

	public void paint(Graphics graphics) {
		if (!bufferStrategySet) {
			bufferStrategySet = true;
			this.createBufferStrategy(2);
		}
		BufferStrategy strategy = this.getBufferStrategy();
		Graphics bufferGraphics = strategy.getDrawGraphics();
		bufferGraphics.translate(-scrollX, -scrollY);
		Roi roi = imp.getRoi();
		if (roi != null) {
			Proxy.setField(imp.getRoi(), "ic", this );
			proxy.execute(null, roi, roi.getClass(), "updatePaste", params);
		}
		try {
			if (imageUpdated) {
				imageUpdated = false;
				imp.updateImage();
			}
			if (IJ.isJava2()) {
				if (magnification < 1.0)
					Java2.setBilinearInterpolation(graphics, true);
				else if (IJ.isMacOSX())
					Java2.setBilinearInterpolation(graphics, false);
			}
			drawImages(bufferGraphics);
			if (roi != null) {
				if (roi.getClass()== ShapeRoi.class) fakeSourceRectForShapeRoi(roi);
				drawRoi(bufferGraphics, roi);
			}
			if (objectManager != null) drawObjects(bufferGraphics);
		} catch (OutOfMemoryError e) {
			IJ.outOfMemory("Paint");
		}
		bufferGraphics.dispose();
		strategy.show();
	}

	public void validateWindow() {
		JFrame win = (JFrame) (((JPanel) (this.getParent()))
				.getTopLevelAncestor());
		win.validate();
	}

	public void zoomIn(int x, int y) {
		super.zoomIn(x, y);
		polygonRoiWorkaround();
		this.validateWindow();
	}

	public void zoomOut(int x, int y) {
		super.zoomOut(x, y);
		polygonRoiWorkaround();
		this.validateWindow();
	}

	public void setXOffset(int offsetX) {
		this.offsetX = offsetX;
	}

	public void setYOffset(int offsetY) {
		this.offsetY = offsetY;
	}

	public int getScrollX() {
		return scrollX;
	}

	public void setScrollX(int scrollX) {
		this.scrollX = scrollX;
	}

	public int getScrollY() {
		return scrollY;
	}

	public void setScrollY(int scrollY) {
		this.scrollY = scrollY;
	}

	public ImagePlus getSecondImage() {
		return secondImage;
	}

	public void setSecondImage(ImagePlus secondImage) {
		this.secondImage = secondImage;
		tmpSecondImageOffsetX = 0;
		tmpSecondImageOffsetY = 0;
		if (secondImage==null) 
			this.updateImage(image); 
		else
			this.updateImage(secondImage);
	}

	public int getSecondImageOffsetX() {
		return secondImageOffsetX;
	}

	public void setSecondImageOffsetX(int secondImageOffsetX) {
		this.secondImageOffsetX = secondImageOffsetX;
	}

	public int getSecondImageOffsetY() {
		return secondImageOffsetY;
	}

	public void setSecondImageOffsetY(int secondImageOffsetY) {
		this.secondImageOffsetY = secondImageOffsetY;
	}

	public ImageDisplayOptions getImageOptions() {
		return imageOptions;
	}

	public void setImageOptions(ImageDisplayOptions imageOptions) {
		this.imageOptions = imageOptions;
		imageOptions.addObserver(this);
	}

	public ImageDisplayOptions getSecondImageOptions() {
		return secondImageOptions;
	}

	public void setSecondImageOptions(ImageDisplayOptions secondImageOptions) {
		this.secondImageOptions = secondImageOptions;
		secondImageOptions.addObserver(this);
	}

	public void update(Observable sender, Object aspect) {
		this.update(this.getGraphics());
	}

	/**Converts a screen x-coordinate to an offscreen x-coordinate.*/
	public int offScreenX(int x) {
		double resultX = offScreenXD(x);
		double resultY = offScreenYD(eventY);
		double zeroX = imp.getWidth() / 2.0;
		double zeroY = imp.getHeight() / 2.0;
		double theAngle = this.angle;
		if (secondImage!=null) {
			theAngle = this.secondImageAngle + tmpSecondImageAngle;
		}
		Point2D result = rotate(resultX, resultY, theAngle, zeroX, zeroY);
		return (int)result.getX();
	}

	/**Converts a screen y-coordinate to an offscreen y-coordinate.*/
	public int offScreenY(int y) {
		double resultX = offScreenXD(eventX);
		double resultY = offScreenYD(y);
		double zeroX = imp.getWidth() / 2.0;
		double zeroY = imp.getHeight() / 2.0;
		double theAngle = this.angle;
		if (secondImage!=null) {
			theAngle = this.secondImageAngle + tmpSecondImageAngle;
		}
		Point2D result = rotate(resultX, resultY, theAngle, zeroX, zeroY);
		return (int)result.getY();
	}
	
	/**Converts a screen x-coordinate to a floating-point offscreen x-coordinate.*/
	public double offScreenXD(int sx) {
		double offset = this.offsetX;
		if (secondImage!=null) {
			offset = this.secondImageOffsetX + tmpSecondImageOffsetX;
		}
		double zeroX = this.getParent().getWidth() / 2 - imp.getWidth() / 2;
		return offScreen(zeroX, offset, scrollX, sx); 
	}
	
	public double offScreenYD(int sy) {
		double offset = this.offsetY;
		if (secondImage!=null) {
			offset = this.secondImageOffsetY + tmpSecondImageOffsetY;
		}
		double zeroY = this.getParent().getHeight() / 2 - imp.getHeight()/ 2;
		return offScreen(zeroY, offset, scrollY, sy); 
	}
	
	public double offScreen(double zero, double offset, double scroll, int coord) {
		double result = (scroll + coord - (zero + offset)) / magnification;
		return result;
	}
	
	public Point2D rotate(double x, double y, double angle, double axisX, double axisY) {
		double angleInRadians = angle * (Math.PI / 180.0);
		AffineTransform rotation = AffineTransform.getRotateInstance(angleInRadians, axisX, axisY);
		Point2D newPoint = new Point2D.Double();
		rotation.transform(new Point2D.Double(x, y), newPoint);
		return newPoint;
	}
	
	public int screenX(int x) {
		return screenXD(x); 
	}

	public int screenY(int y) {
		return screenYD(y);
	}
	
	/**Converts a floating-point offscreen x-coordinate to a screen x-coordinate.*/
	public int screenXD(double ox) {
		double offset = this.offsetX;
		if (secondImage!=null) {
			offset = this.secondImageOffsetX + tmpSecondImageOffsetX;
		}
		double zeroX = (this.getParent().getWidth() / 2  - imp.getWidth() / 2);
		return screen(zeroX, offset, ox); 
	}

	/**Converts a floating-point offscreen x-coordinate to a screen x-coordinate.*/
	public int screenYD(double oy) {
		double offset = this.offsetY;
		if (secondImage!=null) {
			offset = this.secondImageOffsetY + tmpSecondImageOffsetY;
		}
		double zeroY = (this.getParent().getHeight() / 2 - imp.getHeight() / 2);
		return screen(zeroY, offset, oy); 
	}
	
	public int screen(double zero, double offset, double coord) {
		int result = (int)((coord * magnification) + zero + offset);
		return result;
	}
	
	public void mouseReleased(MouseEvent e) {
		eventX = e.getX();
		eventY = e.getY();
		if (this.offScreenX(e.getX())<0 || this.offScreenY(e.getY())<0) return;
		super.mouseReleased(e);
		getBroadcaster().changed("", "roi");
		if (e.isControlDown() && objectManager!=null) {
			objectManager.selectObjectAt(this.offScreenX(e.getX()), this.offScreenY(e.getY()), 
					objectManager.model.getView().getSelectionIndices()[objectManager.model.getView().getSelectionIndices().length-1]);
		}
		this.repaint();
	}

	public void mouseMoved(MouseEvent e) {
		eventX = e.getX();
		eventY = e.getY();
		super.mouseMoved(e);
		this.repaintRoi();
	}

	public void mouseDragged(MouseEvent e) {
		eventX = e.getX();
		eventY = e.getY();
		super.mouseDragged(e);
		this.repaintRoi();
	}

	public void mousePressed(MouseEvent e) {
		eventX = e.getX();
		eventY = e.getY();
		if (this.offScreenX(e.getX())<0 || this.offScreenY(e.getY())<0) return;
		super.mousePressed(e);
		this.repaintRoi();
	}
	
	public void mouseExited(MouseEvent e) {
		eventX = e.getX();
		eventY = e.getY();
		super.mouseExited(e);
	}
	
	public int getHeight() {
		if (height==0) {
			height = this.getDefaultHeight();
		}
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
		this.getBroadcaster().changed("height", Integer.toString(height));
		ImagePlus image = this.image;
		if (this.secondImage!=null) image = this.secondImage;
		this.updateImage(image);
		this.repaint();
		this.validateWindow();
	}

	public int getWidth() {
		if (width==0) {
			width = this.getDefaultWidth();
		}
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		this.getBroadcaster().changed("width", Integer.toString(width));
		ImagePlus image = this.image;
		if (this.secondImage!=null) image = this.secondImage;
		this.updateImage(image);
		this.repaint();
		this.validateWindow();
	}

	public Broadcaster getBroadcaster() {
		if (this.broadcaster==null) {
			this.broadcaster = new Broadcaster();
		}
		return broadcaster;
	}

	public void setObjectManager(Object3DManager objectManager) {
		this.objectManager = objectManager;
	}

	public int getSecondImageAngle() {
		return secondImageAngle;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	public void setSecondImageAngle(int secondImageAngle) {
		this.secondImageAngle = secondImageAngle;
	}

	protected void repaintRoi() {
		if (imp==null) return;
		if (imp.getRoi()!=null) {
			Roi aRoi = imp.getRoi();
			if (aRoi.getState()==Roi.NORMAL) return;
			this.repaint();
		}
	}

	void updateImage(ImagePlus imp) {
		this.imp = imp;
		int width = imp.getWidth();
		int height = imp.getHeight();
		imageWidth = width;
		imageHeight = height;
		srcRect = new Rectangle(0, 0, this.getWidth(), this.getHeight());
		setDrawingSize(this.getWidth(), this.getHeight());
		removeMouseListener(this);
		removeMouseMotionListener(this);
		removeKeyListener(IJ.getInstance());
		addMouseListener(this);
 		addMouseMotionListener(this);
 		addKeyListener(IJ.getInstance());
 		polygonRoiWorkaround();
 		if (imp!=null && imp.getWindow()!=null) Proxy.setField(imp.getWindow(), "ic", this);
	}

	private void polygonRoiWorkaround() {
		srcRect.x = 1;
	}

	protected void fakeSourceRectForShapeRoi(Roi roi) {
		srcRect.x = (int)((offScreenX(-roi.getBounds().x)  + roi.getBounds().x / magnification)  - scrollX/magnification);
		srcRect.y = (int)((offScreenY(-roi.getBounds().y)  + roi.getBounds().y / magnification) - scrollY/magnification);
		srcRect.width = imp.getWidth();
		srcRect.height = imp.getHeight();
	}

	protected void drawImages(Graphics graphics) {
		if (image != null && imageOptions != null) {
			int zeroX = this.getParent().getWidth() / 2 - image.getWidth() / 2;
			int zeroY = this.getParent().getHeight() / 2 - image.getHeight() / 2;
			graphics.clearRect(0, 0, this.getWidth(), this.getHeight());
			drawOpaqueOrTransparent(graphics, image, zeroX + offsetX, zeroY +offsetY, angle, imageOptions.getAlpha());
		}
		if (secondImage != null && secondImageOptions != null) {
			int zeroX = this.getParent().getWidth() / 2 - secondImage.getWidth() / 2;
			int zeroY = this.getParent().getHeight() / 2 - secondImage.getHeight() / 2;
			drawOpaqueOrTransparent(graphics, secondImage, 
										zeroX + secondImageOffsetX + tmpSecondImageOffsetX, 
										zeroY + secondImageOffsetY + tmpSecondImageOffsetY, 
										secondImageAngle + tmpSecondImageAngle,
										secondImageOptions.getAlpha());
		}
	}

	protected void drawObjects(Graphics graphics) {
		if (!objectManager.isShowObjects()) return;
		int[] slices = objectManager.model.getView().getSelectionIndices();
		if (slices == null || slices.length==0) return;
		if (image != null && imageOptions != null) {
			int zeroX = this.getParent().getWidth() / 2 - image.getWidth() / 2;
			int zeroY = this.getParent().getHeight() / 2 - image.getHeight() / 2;
			int slice = slices[0];
			ArrayList<Object3D> objects = objectManager.getObjectsForFirstSlice();
			for (Object3D object : objects) {
				if (!object.isVisible()) continue;
				this.drawObjectsOpaqueOrTransparent(graphics, object, zeroX + offsetX, zeroY +offsetY, angle, slice);
			}
		}
		if (secondImage != null && secondImageOptions != null) {
			int zeroX = this.getParent().getWidth() / 2 - secondImage.getWidth() / 2;
			int zeroY = this.getParent().getHeight() / 2 - secondImage.getHeight() / 2;
			int slice = slices[slices.length-1];
			ArrayList<Object3D> objects = objectManager.getObjectsForSecondSlice();
			for (Object3D object : objects) {
				if (!object.isVisible()) continue;
				this.drawObjectsOpaqueOrTransparent(graphics, object, 
						zeroX + secondImageOffsetX + tmpSecondImageOffsetX, 
						zeroY + secondImageOffsetY + tmpSecondImageOffsetY, secondImageAngle + tmpSecondImageAngle, slice);
			}
		}
	}
	
	protected void drawObjectsOpaqueOrTransparent(Graphics graphics, Object3D anObject, int x, int y, int angle, int slice) {
		Image theImage;
		Graphics theGraphics = graphics;
		theImage = createARGBObjectImageFor(anObject, slice);
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, anObject.getAlpha()));
		g2d.rotate(-angle * (Math.PI / 180.0), x+image.getWidth() * magnification/ 2, y+image.getHeight() * magnification / 2);
		drawImage(theImage, theGraphics, x, y, image.getWidth(), image.getHeight());
		g2d.rotate(angle * (Math.PI / 180.0), x+image.getWidth() * magnification/ 2, y+image.getHeight() * magnification / 2);
	}

	private void drawRoi(Graphics graphics, Roi roi) {
		double theAngle = this.angle;
		if (secondImage!=null) {
			theAngle = this.secondImageAngle + tmpSecondImageAngle;
		}
		int zeroX = this.getParent().getWidth() / 2 - image.getWidth() / 2;
		int zeroY = this.getParent().getHeight() / 2 - image.getHeight() / 2;
		Graphics2D g2d = (Graphics2D) graphics;
		g2d.rotate(-theAngle * (Math.PI / 180.0), zeroX + offsetX+image.getWidth() * magnification/ 2, zeroY + offsetY +image.getHeight() * magnification / 2);
		roi.draw(g2d);
		g2d.rotate(theAngle * (Math.PI / 180.0), zeroX + offsetX+image.getWidth() * magnification/ 2, zeroY + offsetY +image.getHeight() * magnification / 2);
	}
	
	protected void drawOpaqueOrTransparent(Graphics graphics, ImagePlus anImage, int x, int y, int angle, float alpha) {
		Image theImage = anImage.getImage();
		Graphics theGraphics = graphics;
		Graphics2D g2d = (Graphics2D) graphics;
		if (alpha < 1) {
			theImage = createARGBImageFor(anImage);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		}
		g2d.rotate(-angle * (Math.PI / 180.0), x+image.getWidth() * magnification/ 2, y+image.getHeight() * magnification / 2);
		drawImage(theImage, theGraphics, x, y, anImage.getWidth(), anImage.getHeight());
		g2d.rotate(angle * (Math.PI / 180.0), x+image.getWidth() * magnification/ 2, y+image.getHeight() * magnification / 2);
	}

	protected BufferedImage createARGBImageFor(ImagePlus anImage) {
		BufferedImage bi = new BufferedImage(
				anImage.getWidth(), anImage.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();
		g.drawImage(anImage.getImage(), 0, 0, (int) (anImage.getWidth()), (int) (anImage.getHeight()), this);
		g.dispose();
		return bi;
	}

	protected Image createARGBObjectImageFor(Object3D anObject, int slice) {
		BufferedImage bi = new BufferedImage(
				image.getWidth(), image.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
		           RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setComposite(AlphaComposite.getInstance(
				 AlphaComposite.CLEAR, 0.0f));
		Roi aRoi = (Roi) anObject.getSelections().get(new Integer(slice));
		g.setComposite(AlphaComposite.SrcOver);
		g.setColor(anObject.getColor());
		Polygon aPolygon = aRoi.getPolygon();
		g.fillPolygon(aPolygon);
		g.dispose();
		return bi;
	}
	
	protected void drawImage(Image anImage, Graphics graphics, int x, int y, int width, int height) {
		graphics.drawImage(anImage, 
							x, y, (int) (x + (width * magnification)), (int) (y + (height * magnification)), 
							0, 0, width, height, 
						   this);
	}

	protected int getDefaultWidth() {
		return 2000;
	}

	protected int getDefaultHeight() {
		return 2000;
	}
}
