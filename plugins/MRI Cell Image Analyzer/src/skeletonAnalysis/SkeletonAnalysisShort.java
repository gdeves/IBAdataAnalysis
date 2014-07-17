package skeletonAnalysis;

import ij.ImagePlus;

public class SkeletonAnalysisShort extends SkeletonAnalysis {

	public SkeletonAnalysisShort(ImagePlus img) {
		super(img);
		// TODO Auto-generated constructor stub
	}

	public float getPixel(int x, int y) {
		int pos = y*width+x;
		return (((short[])(this.data))[pos])&0xffff;
	}


}
