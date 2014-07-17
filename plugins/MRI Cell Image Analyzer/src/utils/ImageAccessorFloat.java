package utils;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class ImageAccessorFloat extends ImageAccessor {

	public ImageAccessorFloat(ImagePlus img) {
		super(img);
	}
	
	public ImageAccessorFloat(ImageProcessor ip) {
		super(ip);
	}

	public float getPixel(int x, int y) {
		int pos = y*width+x;
		return (((float[])(this.data))[pos]);
	}
}
