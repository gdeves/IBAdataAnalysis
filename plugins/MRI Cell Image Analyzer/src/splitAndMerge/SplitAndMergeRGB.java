package splitAndMerge;

import ij.ImagePlus;

public class SplitAndMergeRGB extends SplitAndMerge {
	
	public SplitAndMergeRGB(ImagePlus img) {
		super(img);
	}
	
	public float getPixel(int x, int y) {
		return getValueAt(x, y, data);
	}
	
	public float getGradient(int x, int y) {
		return getValueAt(x, y, gradient);
	}

	private float getValueAt(int x, int y, Object data) {
		int pos = y*width+x;
		int c = (((int[])(data))[pos]);
		int[] iArray = new int[3];
		iArray[0] = (c&0xff0000)>>16;
		iArray[1] = (c&0xff00)>>8;
		iArray[2] = c&0xff;
		return iArray[0] + iArray[1] + iArray[2];
	}
	
	
}
