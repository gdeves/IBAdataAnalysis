/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker B�cker
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * www.mri.cnrs.fr
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package objectModelingWorkbench;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import operations.file.SaveImageOperation;
import operations.image.ScaleImageOperation;
import applications.Application;
import tools.magicWand.MagicWand;
import utils.ImageDuplicator;
import utils.ImageTransformer;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.NewImage;
import ij.process.ImageProcessor;
import imagejProxies.MRIInterpreter;

/**
 * A slice of an image stack. The slice knows how to align another slice
 * to itself.
 * 
 * @author Volker B�cker
 */
public class AlignmentSlice extends DisplaySlice {
	protected int xOffset = 0;
	protected int yOffset = 0;
	protected int angle = 0;
	
	protected double score = 0.0d;
	protected Application application;
	
	public AlignmentSlice(ImagePlus anImage) {
		super(anImage);
	}
	
	public AlignmentSlice(String path) {
		super(path);
	}

	public AlignmentSlice() {
		// TODO Auto-generated constructor stub
	}

	public int getXOffset() {
		return xOffset;
	}
	
	public void setXOffset(int offset) {
		xOffset = offset;
	}
	
	public void setXOffset(Integer offset) {
		this.setXOffset(offset.intValue());
	}
	
	public void setYOffset(Integer offset) {
		this.setYOffset(offset.intValue());
	}
	
	public void setScore(Double score) {
		this.score = score.doubleValue();
	}
	
	public int getYOffset() {
		return yOffset;
	}
	
	public void setYOffset(int offset) {
		yOffset = offset;
	}
	
	public int getPixel(int x, int y) {
		return image.getPixel(x-xOffset, y-yOffset)[0];
	}
	
	public double alignSlice(AlignmentSlice theOtherSlice) {
		int x0 = xOffset-(width/2);
		int y0 = yOffset-(height/2);
		int bestX = x0;
		int bestY = y0;
		theOtherSlice.moveTo(x0, y0);
		double bestScore = this.calculateDifference(theOtherSlice);
		int nrOfPixels = width*height;
		for (int i=1; i<width; i++) {
			for (int j=1; j<height; j++) {
				theOtherSlice.xOffset=x0+i; theOtherSlice.yOffset=y0+j;
				double newScore = this.calculateDifference(theOtherSlice);
				if (newScore<bestScore) {
					bestScore = newScore;
					bestX = x0+i;
					bestY = y0+j;
				}
				IJ.showProgress((height)*(i-1)+j, nrOfPixels);
			}
		}
		theOtherSlice.moveTo(bestX, bestY);
		theOtherSlice.setScore(bestScore);
		return bestScore;
	}

	public double fastAlignSlice(AlignmentSlice theOtherSlice, int stepWidth) {
		int x0 = xOffset-(width/2);
		int y0 = yOffset-(height/2);
		int bestX = x0;
		int bestY = y0;
		int nrOfPixels = width*height;
		theOtherSlice.moveTo(x0, y0);
		double bestScore = this.calculateDifference(theOtherSlice);
		for (int i=stepWidth; i<width; i+=stepWidth) {
			for (int j=stepWidth; j<height; j+=stepWidth) {
				theOtherSlice.xOffset=x0+i; theOtherSlice.yOffset=y0+j;
				double newScore = this.calculateDifference(theOtherSlice);
				if (newScore<bestScore) {
					bestScore = newScore;
					bestX = x0+i;
					bestY = y0+j;
				}
			}
			IJ.showProgress(i, width);
		}
		bestX = bestX-stepWidth;
		bestY = bestY-stepWidth;
		x0 = bestX;
		y0 = bestY;
		theOtherSlice.moveTo(x0, y0);
		nrOfPixels = 2*stepWidth;
		for (int i=1; i<nrOfPixels; i++) {
			for (int j=1; j<nrOfPixels; j++) {
				theOtherSlice.xOffset=x0+i; theOtherSlice.yOffset=y0+j;
				double newScore = this.calculateDifference(theOtherSlice);
				if (newScore<bestScore) {
					bestScore = newScore;
					bestX = x0+i;
					bestY = y0+j;
				}
			}
			IJ.showProgress(i, nrOfPixels);
		}
		theOtherSlice.moveTo(bestX, bestY);
		theOtherSlice.setScore(bestScore);
		return bestScore;
	}
	
	public ImageProcessor getProcessor() {
		return image.getProcessor();
	}
	public double calculateDifference(AlignmentSlice theOtherSlice) {
		final Rectangle image1Region = new Rectangle(this.xOffset, this.yOffset, this.width, this.height);
		final Rectangle image2Region = new Rectangle(theOtherSlice.xOffset, theOtherSlice.yOffset, this.width, this.height);
		final Rectangle2D overlapRegion = image1Region.createIntersection(image2Region);
		final int startX = (int) Math.round(overlapRegion.getX());
		final int startY = (int) Math.round(overlapRegion.getY());
		final int endX = (int) Math.round(overlapRegion.getX() + overlapRegion.getWidth());
		final int endY = (int) Math.round(overlapRegion.getY() + overlapRegion.getHeight());
		float result = 0;
		final ImageProcessor p1 = image.getProcessor();
		final ImageProcessor p2 = theOtherSlice.getProcessor();
		final byte[] pixels = (byte[]) p1.getPixels();
		final byte[] pixels2 = (byte[]) p2.getPixels();
		for (int i=startX; i<endX; i++) {
			for (int j=startY; j<endY; j++) {
				
				int x = i-xOffset;
				int y = j-yOffset;
				int val1 = (x<0 || x>=width || y<0 || y>=height) ? 0 : pixels[y*width+x]&0xff;
				
				x = i-theOtherSlice.xOffset;
				y = j-theOtherSlice.yOffset;
				int val2 = (x<0 || x>=theOtherSlice.width || y<0 || y>=theOtherSlice.height) ? 0 : pixels2[y*width+x]&0xff;
				
				result += Math.abs(val1 - val2);
			}
		}
		result /= ((endX-startX) * (endY-startY));
		return result;
	}

	public void moveTo(int i, int j) {
		this.xOffset = i;
		this.yOffset = j;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String getTranslationString() {
		String result = "";
		result = result + this.getXOffset() + ",\t " + this.getYOffset() + " -\t " + this.getScore();
		return result;
	}

	public void saveWithSize(File targetFile, Rectangle zeroAndimageSize, boolean applyTranslations) {
		ImagePlus image = null;
		if (image==null) {
			image = this.loadImage();
		} else {
			image = this.getImage();
		}
		ImagePlus newImage;
		if (applyTranslations) {
			newImage = NewImage.createImage(image.getTitle(), zeroAndimageSize.width, zeroAndimageSize.height, 
												   image.getNSlices(), image.getBitDepth(), NewImage.FILL_BLACK);
		} else {
			newImage = ImageDuplicator.copyImage(image, image.getTitle());
		}
		MRIInterpreter interpreter = new MRIInterpreter();
		interpreter.setBatchMode(true);
		image.setImage(image.getProcessor().createImage());
		WindowManager.setTempCurrentImage(image);
		image.setRoi(this.getRoi());
		Application app = this.getApplication();
		if (app!=null) {
			app.run();
		}
		image.killRoi();
		ImagePlus result = image;
		WindowManager.setTempCurrentImage(image);
  	    interpreter.setBatchMode(true);
		if (applyTranslations) {
			IJ.run("Copy");
			newImage.setImage(newImage.getProcessor().createImage());
			WindowManager.setTempCurrentImage(newImage);
			IJ.run("Paste");
			IJ.run("Select None");
			IJ.run("Arbitrarily...", "angle=" + this.getAngle() + " interpolate fill");
			IJ.run("Select None");
			result = ImageTransformer.shift(IJ.getImage(), this.getXOffset(), 
					this.getYOffset());
		}
		SaveImageOperation save = new SaveImageOperation();
		save.setInputImage(result);
		save.setPath(targetFile.getParent() + File.separator + image.getOriginalFileInfo().fileName);
		save.run();
		newImage.changes = false;
		image.changes = false;
		this.setApplication(null);
		interpreter.setBatchMode(false);
 	}

	public void printOn(BufferedWriter out) {
		try {
			out.write(this.getXOffset() + "\t" + this.getYOffset() + "\t" + this.getAngle() + "\t" + this.getScore() + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setApplication(Application application) {
		this.application = application;
	}
	
	protected Application getApplication() {
		return application;
	}

	public void autoSelect(int i, int j) {
		MagicWand wand = MagicWand.getInstance();
		this.getImage().killRoi(); // test
		wand.doMagicWandAt(this.getImage(), i, j);
		this.setRoi(this.getImage().getRoi());
	}

	public void scale(File targetFile) {
		ScaleImageOperation operation = ((ScaleImageOperation)application.getOperations().get(0));
		operation.setKeepSource(true);
		ImagePlus image = null;
		if (image==null) {
			image = this.loadImage();
		} else {
			image = this.getImage();
		}
		ImagePlus newImage = ImageDuplicator.copyImage(image, image.getTitle());
		MRIInterpreter interpreter = new MRIInterpreter();
		interpreter.setBatchMode(true);
		image.setImage(image.getProcessor().createImage());
		operation.setInputImage(newImage);
		Application app = this.getApplication();
		if (app!=null) {
			app.run();
		}
		ImagePlus result = operation.getResult();
		SaveImageOperation save = new SaveImageOperation();
		save.setInputImage(result);
		save.setPath(targetFile.getParent() + File.separator + image.getOriginalFileInfo().fileName);
		save.run();
		newImage.changes = false;
		image.changes = false;
		this.setApplication(null);
		interpreter.setBatchMode(false);
		double xScale = operation.getScaleFactorX();
		this.setXOffset((int)Math.round(this.getXOffset() * (xScale * 1.0)));
		double yScale = operation.getScaleFactorY();
		this.setYOffset((int)Math.round(this.getYOffset() * (yScale * 1.0)));
		this.setPath(targetFile.getParent() + File.separator + image.getOriginalFileInfo().fileName);
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}
	
	public void setAngle(Integer angle) {
		this.angle = angle.intValue();
	}
	
	public Rectangle bounds() {
		double angleInRadians = angle * (Math.PI / 180.0);
		Polygon aPolygon = new Polygon();
		double x1 = -width / 2.0;
		double y1 = -height / 2.0;
		double x2= -x1;
		double y2= -y1;
		double sinAngle = Math.sin(angleInRadians);
		double cosAngle = Math.cos(angleInRadians);
		int xLeftUp = (int)Math.round(x1 * cosAngle - y1 * sinAngle);
		int yLeftUp = (int)Math.round(x1 * sinAngle + y1*cosAngle);
		int xRightUp = (int)Math.round(x2 * cosAngle - y1 * sinAngle);
		int yRightUp = (int)Math.round(x2*sinAngle + y1*cosAngle);
		int xRightDown = (int)Math.round(x2 * cosAngle - y2 * sinAngle);
		int yRightDown = (int)Math.round(x2*sinAngle + y2*cosAngle);
		int xLeftDown = (int)Math.round(x1 * cosAngle - y2 * sinAngle);
		int yLeftDown = (int)Math.round(x1*sinAngle + y2*cosAngle);
		aPolygon.addPoint(xLeftUp, yLeftUp);
		aPolygon.addPoint(xRightUp, yRightUp);
		aPolygon.addPoint(xRightDown, yRightDown);
		aPolygon.addPoint(xLeftDown, yLeftDown);
		aPolygon.translate(this.xOffset, this.yOffset);
		Rectangle boundingBox = aPolygon.getBounds();
		return boundingBox;
	}
}
