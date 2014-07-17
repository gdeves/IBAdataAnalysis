package skeletonAnalysis;

import ij.ImagePlus;

public class SkeletonAnalysisByte extends SkeletonAnalysis {

	public SkeletonAnalysisByte(ImagePlus img) {
		super(img);
		// TODO Auto-generated constructor stub
	}

	public float getPixel(int x, int y) {
		int pos = y*width+x;
		return (((byte[])(this.data))[pos])&0xff;
	}

}
