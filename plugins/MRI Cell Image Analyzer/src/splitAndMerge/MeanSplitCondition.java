package splitAndMerge;

import ij.ImagePlus;
import ij.measure.Measurements;
import ij.process.ImageStatistics;

public class MeanSplitCondition extends SplitCondition {
	
	public MeanSplitCondition(ImagePlus img) {
		super(img);
	}

	public boolean conditionVerification(Object cond) {
		
		//int threashold = ((Integer)cond).intValue();
		ImageStatistics stats = image.getStatistics(Measurements.MEAN+Measurements.STD_DEV);
		double mean = stats.mean;
		if(mean!=6 && mean!=255) return true;
		else return false;
	}

	boolean conditionVerification(Object cond, double variance) {
		// TODO Auto-generated method stub
		return false;
	}

}
