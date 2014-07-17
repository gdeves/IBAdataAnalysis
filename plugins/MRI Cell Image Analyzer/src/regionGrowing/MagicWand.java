package regionGrowing;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.NewImage;
import ij.gui.Roi;

public abstract class MagicWand {
	
	protected int width;
	protected int height;
	protected Object data;
	protected ImagePlus image;
	protected float threshold;
	static byte UNUSED = 0;
	static byte ACCEPTED = 1;
	static byte REJECTED = 2;

	public MagicWand(ImagePlus img)
	{
		width = img.getWidth();
		height = img.getHeight();
		data = img.getProcessor().getPixels();
		image = img;
		threshold = 50;
	}
	
	public static MagicWand newFor(ImagePlus img) throws ImageTypeDoesntExist
	{
		MagicWand result = null;
		if(img.getType()==ImagePlus.GRAY8 || img.getType()==ImagePlus.COLOR_256) result = new MagicWandByte(img);
		if(img.getType()==ImagePlus.GRAY16)	result = new MagicWandShort(img);
		if(img.getType()==ImagePlus.GRAY32) result = new MagicWandFloat(img);
		if(img.getType()==ImagePlus.COLOR_RGB) result = new MagicWandRGB(img);
		if (result == null) throw(new ImageTypeDoesntExist());
		return result;
	}
	
	abstract public float getPixel(int x, int y);
	
	public Roi createRoi(int x, int y)
	{
		Roi roi = null;
		ImagePlus mask = NewImage.createByteImage("mask", width, height, 1, NewImage.FILL_WHITE);
		byte[][] markedPixel = new byte[width][height];
		ArrayList<Point> queue = new ArrayList<Point>();
		int numberOfPoints = 1;
		Point firstPoint = new Point(x,y);
		queue.add(firstPoint);
		float totalIntensity = getPixel(x, y);
		float average = getPixel(x, y);
		mask.getProcessor().set(firstPoint.x, firstPoint.y, 0);
		while(!queue.isEmpty())
		{
			Point currentPoint = queue.remove(0);
			if(numberOfPoints%100==0){
				IJ.showStatus(""+numberOfPoints);
				IJ.showProgress(numberOfPoints, width*height);
			}
			if(currentPoint.x>=width || currentPoint.x<0  || currentPoint.y>=height  || currentPoint.y<0) continue;
			if(markedPixel[currentPoint.x][currentPoint.y]!=MagicWand.UNUSED) continue;
			
			float currentIntensity = this.getPixel(currentPoint.x, currentPoint.y);
			
			if(currentIntensity <= (average+threshold) && currentIntensity >= (average-threshold))
			{
				mask.getProcessor().set(currentPoint.x, currentPoint.y, 0);
				markedPixel[currentPoint.x][currentPoint.y]=MagicWand.ACCEPTED;
				queue.addAll(this.getNeighbors(currentPoint));
				totalIntensity += currentIntensity;
				numberOfPoints++;
				average = totalIntensity / numberOfPoints;
			}
			else
			{
				markedPixel[currentPoint.x][currentPoint.y]=MagicWand.REJECTED;
			}
		}
		WindowManager.setTempCurrentImage(mask);
		IJ.run("Create Selection");
		WindowManager.setTempCurrentImage(null);
		roi = mask.getRoi();
		
		return roi;
	}

	protected Collection<Point> getNeighbors(Point currentPoint)
	{
		int x = currentPoint.x;
		int y = currentPoint.y;
		
		ArrayList<Point> listNeighbors = new ArrayList<Point>();
		
		Point top = new Point(x,y-1);
		Point right = new Point(x+1,y);
		Point bottom = new Point(x,y+1);
		Point left = new Point(x-1,y);
		
		listNeighbors.add(top);
		listNeighbors.add(right);
		listNeighbors.add(bottom);
		listNeighbors.add(left);
		
		return listNeighbors;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
}
