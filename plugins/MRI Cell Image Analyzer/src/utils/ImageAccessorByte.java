package utils;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class ImageAccessorByte extends ImageAccessor {

	public ImageAccessorByte(ImagePlus img) {
		super(img);
	}
	
	public ImageAccessorByte(ImageProcessor ip) {
		super(ip);
	}

	public float getPixel(int x, int y) {
		int pos = y*width+x;
		return (((byte[])(this.data))[pos])&0xff;
	}
}
