package tools.sparkMaster2D;

import utils.ZProfiler;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.process.ImageProcessor;

public class ZProfile {
	protected float[] values;
	private float max = -1;
	private float min;
	
	public ZProfile(float[] values) {
		this.values = values;
	}

	public static ZProfile newFor(ImagePlus anImage, Roi aRoi) {
		Roi oldRoi = anImage.getRoi();
		ImageProcessor ip = anImage.getProcessor();
		float[] values = ZProfiler.getZAxisProfile(anImage, aRoi, ip.getMinThreshold(), ip.getMaxThreshold());
		ZProfile profile = new ZProfile(values);
		anImage.setRoi(oldRoi);
		return profile;
	}

	public void normalize() {
		float min = this.getMin();
		float max = this.getMax();
		float delta = max - min;
		for (int i=0; i<values.length; i++) {
			values[i] = (values[i] - min) / delta;
		}
	}

	private float getMax() {
		if (max==-1) max = calculateMax();
		return max;
	}
	
	private float getMin() {
		if (min==-1) min = calculateMin();
		return min;
	}

	private float calculateMin() {
		float min = Float.MAX_VALUE;
		for (int i=0; i<values.length; i++) {
			float value = values[i];
			if (value<min) min = value;
		}
		return min;
	}

	private float calculateMax() {
		float max = -Float.MAX_VALUE;
		for (int i=0; i<values.length; i++) {
			float value = values[i];
			if (value>max) max = value;
		}
		return max;
	}

	public void subtract(ZProfile cellProfile) {
		float[] otherValues = cellProfile.getValues();
		for (int i=0; i<values.length; i++) {
			values[i] = values[i] - otherValues[i];
		}
		
	}

	public float[] getValues() {
		return values;
	}

}
