package splitAndMerge;

import ij.ImagePlus;

public class VarianceSplitCondition extends SplitCondition {

	public VarianceSplitCondition(ImagePlus img) {
		super(img);
	}

	public boolean conditionVerification(Object cond) {
		return false;
	}

	boolean conditionVerification(Object cond, double variance) {
		int threshold = ((Integer)cond).intValue();
		if(variance>threshold) return true;
		else return false;
	}

}
