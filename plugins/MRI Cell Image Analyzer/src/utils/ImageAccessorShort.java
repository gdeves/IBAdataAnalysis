package utils;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class ImageAccessorShort extends ImageAccessor {

	public ImageAccessorShort(ImagePlus img) {
		super(img);
	}
	
	public ImageAccessorShort(ImageProcessor ip) {
		super(ip);
	}

	public float getPixel(int x, int y) {
		int pos = y*width+x;
		return (((short[])(this.data))[pos])&0xffff;
	}
}
