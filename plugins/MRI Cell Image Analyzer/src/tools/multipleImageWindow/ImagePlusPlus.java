package tools.multipleImageWindow;

import ij.ImagePlus;
import ij.gui.Roi;
import ij.process.ImageProcessor;

public class ImagePlusPlus extends ImagePlus {

	public ImagePlusPlus(String title, ImageProcessor processor) {
		super(title, processor);
	}
	
	public void restoreRoi() {
		super.restoreRoi();
		notifyListeners(UPDATED);
	}
	
	public void setRoi(Roi newRoi) {
		super.setRoi(newRoi);
		notifyListeners(UPDATED);
	}
	
	public void killRoi() {
		super.killRoi();
		notifyListeners(UPDATED);
	}
}
