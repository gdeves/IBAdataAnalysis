package skeletonAnalysis;

import java.util.ArrayList;
import java.util.Iterator;
import regionGrowing.ImageTypeDoesntExist;
import regionGrowing.MagicWand;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.NewImage;
import ij.gui.Roi;
import ij.measure.Measurements;
import ij.plugin.ImageCalculator;
import ij.process.ImageStatistics;

public abstract class SkeletonAnalysis {

	static int meanSize = 3000;
	protected ImagePlus image;
	protected ImagePlus mask2;
	protected int width;
	protected int height;
	protected Object data;
	protected ArrayList<Cell> cellList;
	static byte ACCEPTED = 1;
	protected byte[][] markedPixel;
	
	abstract public float getPixel(int x, int y);
	
	public static SkeletonAnalysis newFor(ImagePlus img) throws ImageTypeDoesntExist
	{
		SkeletonAnalysis result = null;
		if(img.getType()==ImagePlus.GRAY8 || img.getType()==ImagePlus.COLOR_256) result = new SkeletonAnalysisByte(img);
		if(img.getType()==ImagePlus.GRAY16)	result = new SkeletonAnalysisShort(img);
		if(img.getType()==ImagePlus.GRAY32) result = new SkeletonAnalysisFloat(img);
		if(img.getType()==ImagePlus.COLOR_RGB) result = new SkeletonAnalysisRGB(img);
		if (result == null) throw(new ImageTypeDoesntExist());
		return result;
	}
	
	public SkeletonAnalysis(ImagePlus img) {
		image = img;
		data = img.getProcessor().getPixels();
		width = image.getWidth();
		height = image.getHeight();
		cellList = new ArrayList<Cell>();
	}
	
	public void makeTreshold() {
		mask2 = NewImage.createByteImage("mask2", width, height, 1, NewImage.FILL_WHITE);
		for(int i=0;i<height;i++) {
			for(int j=0;j<width;j++) {
				if(this.getPixel(j, i)>40) {
					mask2.getProcessor().set(j, i, 0);
				}
			}
		}
	}
	
	public void pickCells() throws ImageTypeDoesntExist {
		
		MagicWand magicWand = MagicWand.newFor(mask2);
		magicWand.setThreshold(10);
		
		data = mask2.getProcessor().getPixels();
		
		markedPixel = new byte[width][height];

		for(int i=0;i<height;i+=5) {
			for(int j=0;j<width;j++) {
				if(markedPixel[j][i]==SkeletonAnalysis.ACCEPTED) continue;
				if(this.getPixel(j, i)<10) {
					WindowManager.setTempCurrentImage(mask2);
					Roi roi = magicWand.createRoi(j, i);
					Cell cell = new Cell(roi);
					image.setRoi(roi);
					mask2.setRoi(roi);
					cellList.add(cell);
					markedPixelsOfRoi(cell);
					WindowManager.setTempCurrentImage(null);
				}
			}
		}
		image.killRoi();
		mask2.killRoi();
	}
	
	public void markedPixelsOfRoi(Cell cell) {

		int x = cell.getBounds().x;
		int y = cell.getBounds().y;
		int width = cell.getBounds().width;
		int height = cell.getBounds().height;
		int pixels = 0;
		
		for(int i=0;i<width+x;i++) {
			for(int j=0;j<height+y;j++) {
				if(cell.contains(i, j)){
					pixels++;
					markedPixel[i][j]=SkeletonAnalysis.ACCEPTED;
				}
			}
		}
		makeStats(cell, pixels);
	}
	
	public void selectDifferentCell() {
		for(int i=0;i<cellList.size();i++) {
			Cell cell = cellList.get(i);
			image.setRoi(cell);
			mask2.setRoi(cell);
			WindowManager.setTempCurrentImage(mask2);
			if(!cell.contains((int)cell.getXCenterOfMass(), (int)cell.getYCenterOfMass()) || cell.getPixels()>8000){
				cell.setNotNormal(true);
				this.extractSkeletonOfCell(cell);
				System.out.println("bingo");
			}
		}
	}
	
	public void makeStats(Cell cell, int pixels) {
		
		cell.setPixels(pixels);
		
		WindowManager.setTempCurrentImage(mask2);
		
		ImageStatistics stats = mask2.getStatistics(Measurements.CENTER_OF_MASS,Measurements.CIRCULARITY);

		cell.setXCenterOfMass(stats.xCenterOfMass);
		cell.setYCenterOfMass(stats.yCenterOfMass);
		
		WindowManager.setTempCurrentImage(null);
		
		double perimeter = cell.getLength();
		
		double circularity = perimeter==0.0?0.0:4.0*Math.PI*(pixels/(perimeter*perimeter));
		if (circularity>1.0) circularity = 1.0;

		cell.setCircularity((float)circularity);
		System.out.println("pixels : "+pixels+" length : "+cell.getLength()+" circularity : "+cell.getCircularity());
		
	}
	
	public void extractSkeletonOfCell(Cell cell) {
		WindowManager.setTempCurrentImage(mask2);
		mask2.setRoi(cell);
		WindowManager.setTempCurrentImage(mask2);
		IJ.run("Scale...", "x=1 y=1 width="+cell.getBounds().width+" height="+cell.getBounds().height+" interpolate create title=extractedCell.tif");
		IJ.run("Duplicate...", "title=duplicate.tif");
		WindowManager.setTempCurrentImage(WindowManager.getImage("duplicate.tif"));
		IJ.run("Fill Holes");
		
		ImageCalculator calculator = new ImageCalculator();
		calculator.run("Multiply create", WindowManager.getImage("extractedCell.tif"),WindowManager.getImage("duplicate.tif"));
		WindowManager.getImage("extractedCell.tif").changes = false;
		WindowManager.getImage("extractedCell.tif").close();
		WindowManager.getImage("duplicate.tif").changes = false;
		WindowManager.getImage("duplicate.tif").close();
		
		WindowManager.setTempCurrentImage(WindowManager.getImage("Result of extractedCell"));
		IJ.run("Skeletonize");
		
		getPixelsOfSkeleton(WindowManager.getCurrentImage(), cell);
		
		WindowManager.getImage("Result of extractedCell").changes = false;
		WindowManager.getImage("Result of extractedCell").close();
		WindowManager.setTempCurrentImage(null);
	}
	
	public void getPixelsOfSkeleton(ImagePlus mask, Cell cell) {
		//skeleton = new ArrayList();
		WindowManager.setTempCurrentImage(mask);
		for(int i=0; i<mask.getWidth(); i++) {
			for(int j=0; j<mask.getHeight(); j++) {
				mask.setRoi(i, j, 1, 1);
				ImageStatistics stats = mask.getStatistics(Measurements.MEAN);
				if(stats.mean<10) {
					PointOfSkeleton point = new PointOfSkeleton(i,j);
					cell.getSkeleton().add(point);
				}
			}
		}
		WindowManager.setTempCurrentImage(null);
		this.getSkeleton(mask, cell);
	}
	
	public void getSkeleton(ImagePlus mask, Cell cell) {
		
		ArrayList<PointOfSkeleton> skeleton = cell.getSkeleton();
		Iterator<PointOfSkeleton> it = skeleton.iterator();
		
		WindowManager.setTempCurrentImage(mask);
		
		while(it.hasNext()) {
			PointOfSkeleton point = it.next();
			int x = (int) point.getX();
			int y = (int) point.getY();
			mask.setRoi(x-1, y-1, 1, 1);
			ImageStatistics stats = mask.getStatistics(Measurements.MEAN);
			if(stats.mean<10 && x!=0 && y!=0) {
				PointOfSkeleton neighborPoint = new PointOfSkeleton(x-1, y-1);
				point.getNeighbors().add(neighborPoint);
			}
			mask.setRoi(x, y-1, 1, 1);
			stats = mask.getStatistics(Measurements.MEAN);
			if(stats.mean<10 && y!=0) {
				PointOfSkeleton neighborPoint = new PointOfSkeleton(x, y-1);
				point.getNeighbors().add(neighborPoint);
			}
			mask.setRoi(x+1, y-1, 1, 1);
			stats = mask.getStatistics(Measurements.MEAN);
			if(stats.mean<10 && x!=mask.getWidth() && y!=0) {
				PointOfSkeleton neighborPoint = new PointOfSkeleton(x+1, y-1);
				point.getNeighbors().add(neighborPoint);
			}
			mask.setRoi(x+1, y, 1, 1);
			stats = mask.getStatistics(Measurements.MEAN);
			if(stats.mean<10 && x!=mask.getWidth()) {
				PointOfSkeleton neighborPoint = new PointOfSkeleton(x+1, y);
				point.getNeighbors().add(neighborPoint);
			}
			mask.setRoi(x+1, y+1, 1, 1);
			stats = mask.getStatistics(Measurements.MEAN);
			if(stats.mean<10 && x!=mask.getWidth() && y!=mask.getHeight()) {
				PointOfSkeleton neighborPoint = new PointOfSkeleton(x+1, y+1);
				point.getNeighbors().add(neighborPoint);
			}
			mask.setRoi(x, y+1, 1, 1);
			stats = mask.getStatistics(Measurements.MEAN);
			if(stats.mean<10 && y!=mask.getHeight()) {
				PointOfSkeleton neighborPoint = new PointOfSkeleton(x, y+1);
				point.getNeighbors().add(neighborPoint);
			}
			mask.setRoi(x-1, y+1, 1, 1);
			stats = mask.getStatistics(Measurements.MEAN);
			if(stats.mean<10 && x!=0 && y!=mask.getHeight()) {
				PointOfSkeleton neighborPoint = new PointOfSkeleton(x-1, y+1);
				point.getNeighbors().add(neighborPoint);
			}
			mask.setRoi(x-1, y, 1, 1);
			stats = mask.getStatistics(Measurements.MEAN);
			if(stats.mean<10 && x!=0) {
				PointOfSkeleton neighborPoint = new PointOfSkeleton(x-1, y);
				point.getNeighbors().add(neighborPoint);
			}
		}
		
		it = skeleton.iterator();
		while(it.hasNext()) {
			PointOfSkeleton point = it.next();
			if(point.getNeighbors().size()>2) {
				System.out.println("possible segmentation");
				break;
			}	
		}
	}

	public ArrayList<Cell> getCellList() {
		return cellList;
	}

}
