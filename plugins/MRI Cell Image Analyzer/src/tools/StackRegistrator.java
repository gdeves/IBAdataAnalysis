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
package tools;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.ImageWindow;
import ij.measure.ResultsTable;
import ij.plugin.Duplicator;
import ij.process.ImageProcessor;
import java.awt.Rectangle;
import java.util.Observable;
import java.util.Observer;
import operations.analysis.ComputeDifferenceOperation;
import operations.channel.MergeChannelsOperation;
import operations.channel.SplitChannelsOperation;
import operations.image.ResizeToRotateOperation;
import rgbRegistration.Align_RGB_planes;
import utils.Zoom;

/**
 * A tool to manually align the slices of a stack. The align RGB plugin is used
 * to align 2 consecutive slices. The stack and an overlay of the current to
 * slices in red and green is shown. A quality of the alignment is calculated
 * based on the difference of the pixel intensities in the overlapping region
 * of the two images.
 * 
 * @author	Volker Baecker
 **/
public class StackRegistrator extends Observable implements Observer {
	
	protected StackRegistratorView view;

	protected ImagePlus inputImage;
	protected ImagePlus resultImage;
	protected ImagePlus rgbImage;
	boolean useInMemoryMode = true;
	protected int firstSliceInWork;
	protected int secondSliceInWork;
	protected Align_RGB_planes aligner;
	protected boolean updateQuality;
	
	public void show() {
		this.view().setVisible(true);
	}

	protected StackRegistratorView view() {
		if (this.view == null) {
			this.view = new StackRegistratorView(this);
		}
		return this.view;
	}

	protected void changed(String string) {
		this.setChanged();
		this.notifyObservers(string);
		this.clearChanged();
	}

	public void openStack() {
		IJ.run("Open...");
		this.getActiveStack();
	}

	public void getActiveStack() {
		this.setInputImage(IJ.getImage());
		this.createResultImage();
		this.setCurrentSlice(1);
		this.getInputImage().hide();
		this.getResultImage().show();
	}
	
	protected void createResultImage() {
		Duplicator copyAction = new Duplicator();
		resultImage = copyAction.run(inputImage);
		resultImage.setTitle( inputImage.getTitle() + " aligned");
	}

	public int getCurrentSliceNumber() {
		return this.getResultImage().getCurrentSlice();
	}

	public void setCurrentSlice(int currentSlice) {
		this.getResultImage().setSlice(currentSlice);
		this.createRGBOverlay();
		this.changed("currentSlice");
	}

	protected void createRGBOverlay() {
		if (rgbImage!=null) {
			rgbImage.getWindow().close();
		}
		if (this.getCurrentSliceNumber()>=(this.getResultImage().getStackSize()-2)) return;
		this.firstSliceInWork = this.getCurrentSliceNumber();
		this.secondSliceInWork = this.getCurrentSliceNumber()+1;
		MergeChannelsOperation merge = new MergeChannelsOperation();
		merge.setBlueChannel("none");
		merge.setInputImageGreen(this.getSliceNumber(this.getCurrentSliceNumber()));
		merge.setInputImageRed(this.getSliceNumber(this.getCurrentSliceNumber()+1));
		merge.setInputImageBlue(this.getSliceNumber(this.getCurrentSliceNumber()+1));
		merge.doIt();
		merge.getResult().setTitle("register slices " + this.firstSliceInWork + " - " + this.secondSliceInWork);
		this.setRgbImage(merge.getResult());
	}

	public ImagePlus getSliceNumber(int i) {
		ImageStack stack = this.resultImage.getImageStack();
		ImagePlus slice = resultImage.createImagePlus();
		ImageProcessor ip = stack.getProcessor(i).crop();
		slice.setProcessor(inputImage.getTitle() + " slice " + i, ip);
		return slice;
	}
	
	public ImagePlus getOriginalSliceNumber(int i) {
		ImageStack stack = this.inputImage.getImageStack();
		ImagePlus slice = inputImage.createImagePlus();
		ImageProcessor ip = stack.getProcessor(i).crop();
		slice.setProcessor(inputImage.getTitle() + " slice " + i, ip);
		return slice;
	}
	
	public ImagePlus getInputImage() {
		return inputImage;
	}

	public void setInputImage(ImagePlus inputImage) {
		this.inputImage = inputImage;
		this.changed("inputImage");
	}

	public ImagePlus getResultImage() {
		if (this.resultImage==null) {
			this.createResultImage();
		}
		return resultImage;
	}

	public void setResultImage(ImagePlus resultImage) {
		this.resultImage = resultImage;
	}

	public ImagePlus getRgbImage() {
		if (rgbImage==null) {
			this.createRGBOverlay();
		}
		return rgbImage;
	}

	public void setRgbImage(ImagePlus newRgbImage) {
		ImagePlus oldRGBImage = this.rgbImage;
		this.rgbImage = newRgbImage;
		this.changed("rgbImage");
		if (oldRGBImage!=null) {
			ImageWindow window = oldRGBImage.getWindow();
			Rectangle lastBounds = window.getBounds();
			double lastZoom = window.getCanvas().getMagnification();
			ImageWindow newWin = rgbImage.getWindow();
			newWin.setVisible(false);
			newWin.setBounds(lastBounds);
			Zoom.zoomTo(newWin, lastZoom);
			newWin.setVisible(true);
		}
	}

	public boolean isUseInMemoryMode() {
		return useInMemoryMode;
	}

	public void setUseInMemoryMode(boolean useInMemoryMode) {
		this.useInMemoryMode = useInMemoryMode;
	}

	public String getInputImagePath() {
		String result = this.getInputImage().getOriginalFileInfo().directory + 
							this.getInputImage().getOriginalFileInfo().fileName;
		return result;
	}

	public void setAligner(Align_RGB_planes aligner) {
		if (this.aligner!=null) {
			this.aligner.getBroadcaster().deleteObserver(this);
		}
		this.aligner = aligner;
		this.aligner.getBroadcaster().addObserver(this);
	}

	public void padImage() {
		ResizeToRotateOperation pad = new ResizeToRotateOperation();
		pad.setInputImage(this.resultImage);
		pad.setKeepSource(false);
		pad.doIt();
		this.setResultImage(pad.getResult());
		pad.setInputImage(this.inputImage);
		pad.setShowResult(false);
		pad.setKeepSource(false);
		pad.doIt();
		this.createRGBOverlay();
	}

	public Align_RGB_planes getAligner() {
		return aligner;
	}

	public void acceptCurrentRegistration() {
		SplitChannelsOperation split = new SplitChannelsOperation();
		split.setInputImage(this.getRgbImage());
		split.setShowResult(false);
		split.run();
		int sliceNumber = this.firstSliceInWork;
		ImagePlus newCurrentSlice = split.getResultGreen();
		ImagePlus newNextSlice = split.getResultRed();
		ImagePlus currentSlice = this.getSliceNumber(sliceNumber);
		ImagePlus nextSlice = this.getSliceNumber(sliceNumber + 1);
		this.getResultImage().getStack().addSlice(currentSlice.getTitle(), newCurrentSlice.getProcessor(), sliceNumber-1);
		this.getResultImage().getStack().deleteSlice(sliceNumber+1);
		this.getResultImage().getStack().addSlice(nextSlice.getTitle(), newNextSlice.getProcessor(), sliceNumber);
		this.getResultImage().getStack().deleteSlice(sliceNumber+2);
		if (this.getCurrentSliceNumber()==sliceNumber)
			this.setCurrentSlice(sliceNumber + 1);
		else
			this.setCurrentSlice(this.getCurrentSliceNumber());
	}

	public void revertCurrentSlice() {
		int sliceNumber = this.getCurrentSliceNumber();
		ImagePlus currentSlice = this.getSliceNumber(this.getCurrentSliceNumber());
		ImagePlus newCurrentSlice = this.getOriginalSliceNumber(this.getCurrentSliceNumber());
		this.getResultImage().getStack().addSlice(currentSlice.getTitle(), newCurrentSlice.getProcessor(), sliceNumber-1);
		this.getResultImage().getStack().deleteSlice(sliceNumber+1);
		this.setCurrentSlice(this.getCurrentSliceNumber());
	}
	
	public double computeCurrentQuality() {
		SplitChannelsOperation split = new SplitChannelsOperation();
		split.setInputImage(this.getRgbImage());
		split.setShowResult(false);
		split.run();
		ImagePlus currentSlice = split.getResultGreen();
		ImagePlus nextSlice = split.getResultRed();
		currentSlice.setImage(currentSlice.getProcessor().createImage());
		nextSlice.setImage(nextSlice.getProcessor().createImage());
		ComputeDifferenceOperation difference = new ComputeDifferenceOperation();
		difference.setShowResult(false);
		difference.setFirstImage(currentSlice);
		difference.setSecondImage(nextSlice);
		difference.run();
		ResultsTable resultTable = difference.getDifference();
		double result = resultTable.getValue("average difference", 0);
		return result;
	}

	public void setUpdateQuality(boolean b) {
		this.updateQuality = b;
	}

	public void update(Observable arg0, Object arg1) {
		System.out.println(arg0.toString() + "-" + arg1.toString());
		if (this.isUpdateQuality()) {
			this.changed("slicePosition");
		}
	}

	public boolean isUpdateQuality() {
		return updateQuality;
	}
}
