package utils;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class ImageAccessorRGB extends ImageAccessor {

	public ImageAccessorRGB(ImagePlus img) {
		super(img);
	}
	
	public ImageAccessorRGB(ImageProcessor ip) {
		super(ip);
	}

	public float getPixel(int x, int y) {
		int pos = y*width+x;
		int c = (((int[])(this.data))[pos]);
		int[] iArray = new int[3];
		iArray[0] = (c&0xff0000)>>16;
		iArray[1] = (c&0xff00)>>8;
		iArray[2] = c&0xff;
		return iArray[0] + iArray[1] + iArray[2];
	}
}
