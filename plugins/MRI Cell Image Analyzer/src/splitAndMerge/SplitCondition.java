package splitAndMerge;

import ij.ImagePlus;

public abstract class SplitCondition {
	static public int VARIANCE = 0;
	static public int MEAN = 1;
	
	protected ImagePlus image;
	
	public SplitCondition(ImagePlus img)
	{
		image = img;
	}
	
	public static SplitCondition newFor(ImagePlus img, int options)
	{
		SplitCondition result = null;
		
		if(options == MEAN) result = new MeanSplitCondition(img);
		if(options == VARIANCE) result = new VarianceSplitCondition(img);
		
		return result;
	}
	
	abstract boolean conditionVerification(Object cond);
	abstract boolean conditionVerification(Object cond, double variance);
}
