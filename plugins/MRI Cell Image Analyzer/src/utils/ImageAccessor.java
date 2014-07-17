package utils;

import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import regionGrowing.ImageTypeDoesntExist;

public abstract class ImageAccessor {
	
	protected int width;
	protected int height;
	protected Object data;
	
	public ImageAccessor(ImagePlus img)
	{
		width = img.getWidth();
		height = img.getHeight();
		data = img.getProcessor().getPixels();
	}
	
	public ImageAccessor(ImageProcessor ip)
	{
		width = ip.getWidth();
		height = ip.getHeight();
		data = ip.getPixels();
	}
	
	public static ImageAccessor newFor(ImagePlus img) throws ImageTypeDoesntExist
	{
		ImageAccessor result = null;
		if(img.getType()==ImagePlus.GRAY8 || img.getType()==ImagePlus.COLOR_256) result = new ImageAccessorByte(img);
		if(img.getType()==ImagePlus.GRAY16)	result = new ImageAccessorShort(img);
		if(img.getType()==ImagePlus.GRAY32) result = new ImageAccessorFloat(img);
		if(img.getType()==ImagePlus.COLOR_RGB) result = new ImageAccessorRGB(img);
		if (result == null) throw(new ImageTypeDoesntExist());
		return result;
	}
	
	public static ImageAccessor newFor(ImageProcessor ip) throws ImageTypeDoesntExist
	{
		ImageAccessor result = null;
		if(ip.getClass().equals(ByteProcessor.class)) result = new ImageAccessorByte(ip);
		if(ip.getClass().equals(ShortProcessor.class))	result = new ImageAccessorShort(ip);
		if(ip.getClass().equals(FloatProcessor.class)) result = new ImageAccessorFloat(ip);
		if(ip.getClass().equals(ColorProcessor.class)) result = new ImageAccessorRGB(ip);
		if (result == null) throw(new ImageTypeDoesntExist());
		return result;
	}
	
	abstract public float getPixel(int x, int y);

}
