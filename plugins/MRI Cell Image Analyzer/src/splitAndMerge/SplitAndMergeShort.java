package splitAndMerge;

import ij.ImagePlus;

public class SplitAndMergeShort extends SplitAndMerge {
	
	public SplitAndMergeShort(ImagePlus img) {
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
		return (((short[])(data))[pos])&0xffff;
	}

}
