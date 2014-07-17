package skeletonAnalysis;

import ij.ImagePlus;

public class SkeletonAnalysisFloat extends SkeletonAnalysis {

	public SkeletonAnalysisFloat(ImagePlus img) {
		super(img);
		// TODO Auto-generated constructor stub
	}

	public float getPixel(int x, int y) {
		int pos = y*width+x;
		return (((float[])(this.data))[pos]);
	}

}
