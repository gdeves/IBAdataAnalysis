/*
 * Created on 29.11.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package patternMatching;

import java.awt.Rectangle;
import ij.ImagePlus;
import ij.process.ImageProcessor;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CentralMomentCalculator {
	ImagePlus inputImage;
	public double zero = 0.0;
	
	public double m00 = zero;
    public double m10 = zero, m01 = zero;
    
    public double xc = zero, yc = zero;
    
	public double cm00 = zero;
	public double cm10 = zero, cm01 = zero;
	public double cm20 = zero, cm02 = zero, cm11 = zero;
	public double cm30 = zero, cm03 = zero, cm21 = zero, cm12 = zero;
	
	public double[] feature;
	
	private ImageProcessor ip;
	private Rectangle r;
	
	public CentralMomentCalculator() {
		super();
	}
	
	public void setImage(ImagePlus inputImage) {
		this.inputImage = inputImage;
	}
	
	public void calculate() {
		 ip = inputImage.getProcessor();
		 r = ip.getRoi();
		 this.calculateBasicMoments();
		 this.calculateCenterOfGravity();
		 this.calculateCentralMoments();
		 this.calculateFeatures();
	}

	/**
	 * 
	 */
	private void calculateFeatures() {
		feature = new double[7];
		feature[0] = cm20 + cm02;
		feature[1] = ((cm20 - cm02) * (cm20 - cm02)) + (4*(cm11*cm11));
		feature[2] = ((cm30 - (3 * cm12)) * (cm30 - (3 * cm12))) + 
					  (((3*cm21) - cm03) * ((3*cm21) - cm03));
		feature[3] = ((cm30 + cm12) * (cm30 + cm12)) + ((cm21+cm03)*((cm21+cm03)));
		feature[4] = ((cm30 - (3 * cm12)) * 
					  (cm30 + cm12) * 
					  (((cm30 + cm12) * (cm30 + cm12))  - 3*((cm21 - cm03)*(cm21 - cm03))) ) 
					  	+ 
					  ((3*cm21-cm03) *
					   (cm21+cm03) *
					   ( 3 * ((cm30 + cm12)*(cm30 + cm12)) -  ((cm21 + cm03) * (cm21 + cm03)))
					  		);
		feature[5] = ((cm20-cm02) * 
					 (((cm30+cm12)*(cm30+cm12))-((cm21-cm03)*(cm21-cm03)))) + ( 
					 4 * cm11 * (cm30+cm12) * (cm21 + cm03));
		feature[6] = ((3*cm21-cm03) * (cm30+cm12) * (((cm30+cm12)*(cm30+cm12))-3*((cm21+cm03)*(cm21+cm03)))) + 
		             ((3*cm12-cm30) * (cm21+cm03) * (3*((cm30+cm12)*(cm30+cm12))-((cm21+cm03)*(cm21+cm03))));
	}

	/**
	 * 
	 */
	private void calculateCentralMoments() {
		double currentPixel;
		double xCoord;
		double yCoord;
		
		for (int y=r.y; y<(r.y+r.height); y++) {
	     /*0*/cm00 = m00;
		      for (int x=r.x; x<(r.x+r.width); x++) {
		      	xCoord = x - xc;
		      	yCoord = y - yc;
		        currentPixel=ip.getPixelValue(x,y);
		        if (currentPixel < 0) currentPixel = zero; //gets rid of negative pixel values
		 /*1*/    cm10+=currentPixel*xCoord;
		          cm01+=currentPixel*yCoord;
		          
		 /*2*/    cm20+=currentPixel*(xCoord)*(xCoord);
		          cm02+=currentPixel*(yCoord)*(yCoord);
		          cm11+=currentPixel*(xCoord)*(yCoord);
		        
		 /*3*/    cm30+=currentPixel*(xCoord)*(xCoord)*(xCoord);
		          cm03+=currentPixel*(yCoord)*(yCoord)*(yCoord);
		          cm21+=currentPixel*(xCoord)*(xCoord)*(yCoord);
		          cm12+=currentPixel*(xCoord)*(yCoord)*(yCoord);
		      }
		}
	}

	/**
	 * 
	 */
	private void calculateCenterOfGravity() {
		xc = m10 / (m00 * 1.0);
		yc = m01 / (m00 * 1.0);
	}

	/**
	 * 
	 */
	private void calculateBasicMoments() {
		double currentPixel;
		for (int y=r.y; y<(r.y+r.height); y++) {
		      for (int x=r.x; x<(r.x+r.width); x++) {
		        currentPixel=ip.getPixelValue(x,y);
		        if (currentPixel < 0) currentPixel = zero; //gets rid of negative pixel values
		 /*0*/    m00+=currentPixel;
		 /*1*/    m10+=currentPixel*x;
		          m01+=currentPixel*y;
		      }
		    }
	}
}
