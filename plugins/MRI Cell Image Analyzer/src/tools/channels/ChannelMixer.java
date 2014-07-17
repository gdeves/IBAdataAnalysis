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
package tools.channels;

import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.NewImage;
import ij.gui.Roi;
import ij.plugin.RGBStackMerge;
import ij.plugin.Duplicator;
import ij.plugin.filter.RGBStackSplitter;
import ij.process.ImageProcessor;
import ij.process.StackProcessor;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Observable;
import java.util.Observer;
import tools.PixelSpy;
import broadcaster.WindowManagerBroadcaster;

/**
 * Select the channles to be displayed and set the min and max display of the 
 * three channels. In contrast to the color balance adjuster from ImageJ the display
 * of all three channels can be manipulated in the same time. 
 *
 * @author	Volker Baecker
 **/
public class ChannelMixer extends Observable implements Observer {
	ImagePlus inputImageRGB;
	ImagePlus redOriginal;
	ImagePlus greenOriginal;
	ImagePlus blueOriginal;
	ImagePlus redTemp;
	ImagePlus greenTemp;
	ImagePlus blueTemp;
	protected double redMin;
	protected double redMax;
	protected double greenMin;
	protected double greenMax;
	protected double blueMin;
	protected double blueMax;
	protected boolean showRed;
	protected boolean showGreen;
	protected boolean showBlue;
	protected String modus;
	protected boolean keepAdjustment;
	RGBStackSplitter splitter = new RGBStackSplitter();
	Duplicator duplicater = new Duplicator();
	RGBStackMerge merger = new RGBStackMerge();
	protected ChannelMixerView view;
	
	public ChannelMixer(ImagePlus image) throws Exception {
		super();
		this.blockEvents();
		inputImageRGB = image;
		if (image == null || image.getType()!=ImagePlus.COLOR_RGB) {
			inputImageRGB = NewImage.createRGBImage("RGB", 80, 80, 1, NewImage.RGB);
			inputImageRGB.show();
		}
		this.setShowRed(true);
		this.setShowGreen(true);
		this.setShowBlue(true);
		this.setModus(this.modi()[1]);
		this.setKeepAdjustment(false);
		WindowManagerBroadcaster.getInstance().addObserver(this);
		this.initialize();
	}

	private void initialize() {
		splitter.split(inputImageRGB.getStack(), true);
		redOriginal = new ImagePlus("red");
		redOriginal.setStack("red", splitter.red);
		greenOriginal = new ImagePlus("green");
		greenOriginal.setStack("green", splitter.green);
		blueOriginal = new ImagePlus("blue");
		blueOriginal.setStack("blue", splitter.blue);
		if (this.isKeepAdjustment()) {
			this.setRedMin(this.getRedMin());
			this.setRedMax(this.getRedMax());
			this.setGreenMin(this.getGreenMin());
			this.setGreenMax(this.getGreenMax());
			this.setBlueMin(this.getBlueMin());
			this.setBlueMax(this.getBlueMax());
		} else {
			this.setRedMin(redOriginal.getProcessor().getMin());
			this.setRedMax(redOriginal.getProcessor().getMax());
			this.setGreenMin(greenOriginal.getProcessor().getMin());
			this.setGreenMax(greenOriginal.getProcessor().getMax());
			this.setBlueMin(redOriginal.getProcessor().getMin());
			this.setBlueMax(redOriginal.getProcessor().getMax());
		}
		this.changed("minMax");
		this.changed("channelSelection");
		this.changed("modus");
	}
	
	private void changed(String string) {
		this.setChanged();
		this.notifyObservers(string);
		this.clearChanged();
	}

	protected String[] modi() {
		String[] modi = {"min", "max"};
		return modi;
	}

	public double getBlueMax() {
		return blueMax;
	}

	public void setBlueMax(double blueMax) {
		this.blueMax = blueMax;
		this.updateImages();
	}

	public double getBlueMin() {
		return blueMin;
	}

	public void setBlueMin(double blueMin) {
		this.blueMin = blueMin;
		this.updateImages();
	}

	public double getGreenMax() {
		return greenMax;
	}

	public void setGreenMax(double greenMax) {
		this.greenMax = greenMax;
		this.updateImages();
	}

	public double getGreenMin() {
		return greenMin;
	}

	public void setGreenMin(double greenMin) {
		this.greenMin = greenMin;
		this.updateImages();
	}

	public void updateImages() {
		redTemp = duplicater.run(redOriginal);
		redTemp.setTitle(inputImageRGB.getTitle());
		greenTemp = duplicater.run(greenOriginal);
		greenTemp.setTitle(inputImageRGB.getTitle());
		blueTemp = duplicater.run(blueOriginal);
		blueTemp.setTitle(inputImageRGB.getTitle());
		redTemp.getProcessor().setMinAndMax(this.getRedMin(), this.getRedMax());
		this.apply(redTemp, redTemp.getProcessor());
		ImageStack red = null;
		if (isShowRed()) {
			red = redTemp.getStack();
		}
		greenTemp.getProcessor().setMinAndMax(this.getGreenMin(), this.getGreenMax());
		this.apply(greenTemp, greenTemp.getProcessor());
		ImageStack green = null;
		if (isShowGreen()) {
			green = greenTemp.getStack();
		}
		blueTemp.getProcessor().setMinAndMax(this.getBlueMin(), this.getBlueMax());
		this.apply(blueTemp, blueTemp.getProcessor());
		ImageStack blue = null;
		if (isShowBlue()) {
			blue = blueTemp.getStack();
		}
		ImageStack merged = merger.mergeStacks(inputImageRGB.getWidth(), inputImageRGB.getHeight(), inputImageRGB.getStackSize(),
							red, green, blue, true);
		Rectangle oldBounds = inputImageRGB.getWindow().getBounds();
		Roi oldRoi = inputImageRGB.getRoi();
		Rectangle position = inputImageRGB.getWindow().getCanvas().getSrcRect();
		double lastZoom = inputImageRGB.getWindow().getCanvas().getMagnification();
		inputImageRGB.setStack(inputImageRGB.getTitle(), merged);
		inputImageRGB.getWindow().setVisible(false);
		inputImageRGB.getWindow().setBounds(oldBounds);
		if (inputImageRGB.getWindow().getCanvas().getMagnification()<lastZoom) this.zoomIn(inputImageRGB.getWindow(), lastZoom);
		if (inputImageRGB.getWindow().getCanvas().getMagnification()>lastZoom) this.zoomOut(inputImageRGB.getWindow(), lastZoom);
		inputImageRGB.getWindow().getCanvas().getSrcRect().setBounds(position);
		if (oldRoi!=null) inputImageRGB.setRoi(oldRoi);
		inputImageRGB.getWindow().setVisible(true);
	}

	public double getRedMax() {
		return redMax;
	}

	public void setRedMax(double redMax) {
		this.redMax = redMax;
		this.updateImages();
	}

	public double getRedMin() {
		return redMin;
	}

	public void setRedMin(double redMin) {
		this.redMin = redMin;
		this.updateImages();
	}

	public boolean isShowBlue() {
		return showBlue;
	}

	public void setShowBlue(boolean showBlue) {
		this.showBlue = showBlue;
	}

	public boolean isShowGreen() {
		return showGreen;
	}

	public void setShowGreen(boolean showGreen) {
		this.showGreen = showGreen;
	}

	public boolean isShowRed() {
		return showRed;
	}

	public void setShowRed(boolean showRed) {
		this.showRed = showRed;
	}

	public String getModus() {
		return modus;
	}

	public void setModus(String modus) {
		this.modus = modus;
		this.changed("minMax");
	}
	
	public void show() {
		this.getView().setVisible(true);
		this.changed("minMax");
		this.changed("channelSelection");
		this.changed("modus");
		this.unblockEvents();
	}

	private ChannelMixerView getView() {
		if (view==null) {
			view = new ChannelMixerView(this);
		}
		return view;
	}

	public void update(Observable arg0, Object anAspect) {
		ImagePlus newInputImage = WindowManager.getCurrentImage();
		if (newInputImage.getTitle().contains("Pixel Spy")) return;
		if (newInputImage==null || newInputImage.getWindow()==null ) return;
		if (newInputImage==inputImageRGB) return;
		if (anAspect.equals("currentImage")) {
			inputImageRGB.getWindow().close();
			inputImageRGB = newInputImage;
			inputImageRGB.show();
			this.initialize();
			this.unblockEvents();
		}
	}

	public void close() {
		WindowManagerBroadcaster.getInstance().deleteObserver(this);
	}
	
	public void apply(ImagePlus imp, ImageProcessor ip) {
		int[] table = new int[256];
		int min = (int)ip.getMin();
		int max = (int)ip.getMax();
		for (int i=0; i<256; i++) {
			if (i<=min)
				table[i] = 0;
			else if (i>=max)
				table[i] = 255;
			else
				table[i] = (int)(((double)(i-min)/(max-min))*255);
		}
		ip.setRoi(imp.getRoi());
		if (imp.getStackSize()>1) {
			ImageStack stack = imp.getStack();
				new StackProcessor(stack, ip).applyTable(table);
		} else {
			if (ip.getMask()!=null)	 ip.snapshot();
			ip.applyTable(table);
			ip.reset(ip.getMask());
		}
		imp.changes = true;
		imp.unlock();
	}

	public void blockEvents() {
		if (PixelSpy.isActive()) {
			WindowManagerBroadcaster.getInstance().deleteObserver(PixelSpy.getCurrent());
		}
	}

	public void unblockEvents() {
		if (PixelSpy.isActive()) {
			WindowManagerBroadcaster.getInstance().addObserver(PixelSpy.getCurrent());
			PixelSpy.getCurrent().updateAnywaw();
		}
	}
	
	private void zoomOut(ImageWindow win, double lastZoom) {
		while(win.getCanvas().getMagnification()>lastZoom) {
			if (win==null) return;
			ImageCanvas ic = win.getCanvas();
			Point loc = ic.getCursorLoc();
			int x = ic.screenX(loc.x);
			int y = ic.screenY(loc.y);
			ic.zoomOut(x, y);
			}
		}
		
	private void zoomIn(ImageWindow win, double lastZoom) {
		while(win.getCanvas().getMagnification()<lastZoom) {
			if (win==null) return;
			ImageCanvas ic = win.getCanvas();
			Point loc = ic.getCursorLoc();
			int x = ic.screenX(loc.x);
			int y = ic.screenY(loc.y);
			ic.zoomIn(x, y);			
			}
	}

	public void toggleKeepAdjustment() {
		this.setKeepAdjustment(!this.isKeepAdjustment());
		
	}

	public boolean isKeepAdjustment() {
		return keepAdjustment;
	}
	
	public void setKeepAdjustment(boolean keepAdjustment) {
		this.keepAdjustment = keepAdjustment;
		this.changed("keepAdjustment");
	}
}
