import ij.plugin.filter.*;
import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.*;
import ij.util.*;
import ij.plugin.filter.Analyzer;
import java.awt.Rectangle;
import ij.measure.CurveFitter;
import java.awt.*;
import java.text.DecimalFormat; 
import java.awt.image.*;
import ij.plugin.frame.RoiManager;

import java.util.*;


public class FRAP_Profiler implements PlugInFilter, Measurements  {

	ImagePlus imp;
	ImageWindow myWindow;
	Roi roi, full;
	DecimalFormat df3 = new DecimalFormat("##0.000");
	DecimalFormat df2 = new DecimalFormat("##0.00");
	DecimalFormat df1 = new DecimalFormat("##0.0");
	RoiManager rm;

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL+NO_CHANGES+ROI_REQUIRED;
	}

	public void run(ImageProcessor ip) {
		if (imp.getStackSize()<2) {
			IJ.showMessage("FRAP Profiler", "This command requires a stack.");
			return;
		}

		myWindow = imp.getWindow();
		rm = RoiManager.getInstance();
		if (rm==null)
			{IJ.error("Roi Manager is not open"); return;}
		rm.select(0);	
		roi = imp.getRoi();

		Hashtable table = rm.getROIs();
		java.awt.List list = rm.getList();
		int roiCount = list.getItemCount();
		Roi[] rois = new Roi[roiCount];
		for (int i=0; i<roiCount; i++) {
			String label = list.getItem(i);
			Roi roi2 = (Roi)table.get(label);
			if (roi2==null) continue;
			rois[i] = roi2;
		}



		//Roi full = new Roi (0,0,imp.getWidth(),imp.getHeight());
		roi = rois[roiCount-2];
		full = rois [roiCount-1];


		if ((full.getBounds().width*full.getBounds().width)<(roi.getBounds().width*roi.getBounds().width)) 
			{
			roi = rois[roiCount-1];
			full = rois [roiCount-2];	
			}
	
		Rectangle r = roi.getBoundingRect();
		
		if (roi.getType()>=Roi.LINE) {
			IJ.showMessage("FRAP Profiler", "This command does not work with line selections.");
			return;
		}
		double minThreshold = ip.getMinThreshold();
	        	double maxThreshold = ip.getMaxThreshold();

		float[] yF = getZAxisProfile(roi, full, minThreshold, maxThreshold);

//even values in yF are the ROI, odd values the fill roi

		Calibration cal = imp.getCalibration();
		String timeUnit = cal.getTimeUnit();

//create array for full cell values
		float[]y2 = new float[(yF.length/2)]; 

//create array for ROI values
		float[]y3 = new float[(yF.length/2)]; 

		float[]y = new float[(yF.length/2)]; 
//get max	
		float yFmax=0;
		float yMax=0;
		float yFmin=65500;
		float yMin=65500;

		for (int v = 0; v<yF.length; v++)
			{
			if (v%2!=0) y2[v/2]=yF[v];
			if (v%2==0) y3[v/2]=yF[v];

			if((yFmax<yF[v])&&(v%2!= 0)) 	yFmax=yF[v];	
			if((yMax<yF[v]) &&(v%2== 0))	 yMax=yF[v];	

			if((yFmin>yF[v]) &&(v%2!= 0))	yFmin=yF[v];	
			if((yMin>yF[v]) &&(v%2== 0))	yMin=yF[v];	

			}


//normalise
	


		yMin=0; 
		yFmin=0;

		for (int u = 0; u<y.length; u++)
			{
		
			y[u] = (y2[u]/yFmax) / (y3[u]/yMax);
			

			}

//Now stretch to a range of 0 to 1...
		float yMin2 = y[0];
		float yMax2 = y[0];

		for (int u = 0; u<y.length; u++)
			{
		
			if (y[u] < yMin2) {yMin2 = y[u];}
			if (y[u] > yMax) {yMax = y[u];}
			

			}
		//IJ.showMessage("yMin = " + yMin2 + " new yMax = " + yMax2);

		for (int u = 0; u<y.length; u++)
			{
		
			y[u] = (y[u]-yMin2)/(yMax2-yMin2);			

			}

		
		float timescale = 1;
		if (y!=null)
			{float[] x = new float[y.length];
			
		
		
//Sets the time interval for consecutive X values.
//Also asks for single or double exponential curve fit.

		String[] methodList = {"Single exponential recovery", "Double exponential recovery"}; 
		int fitMethod = 0; 
		boolean drawROIs = true;      
		GenericDialog gd = new GenericDialog("FRAP Profiler Settings");
		gd.addChoice("Curve fitting method:", methodList, methodList[fitMethod]);
		gd.addNumericField("Time interval (sec): ",1,1); 
		gd.addCheckbox("Generate image with ROIs", drawROIs);
		gd.showDialog();
		if (gd.wasCanceled()) {return;}
		fitMethod = gd.getNextChoiceIndex();
		timescale = (float) gd.getNextNumber();
		IJ.log("Time interval: "+ timescale + " sec");
		drawROIs = gd.getNextBoolean();

			for (int i=0; i<x.length; i++) 
				{x[i] = i*timescale;}
						
			IJ.run("Line Width...", "line=1");

			PlotWindow pwF = new PlotWindow("rawFRAP: "+ imp.getTitle()+"-x"+r.x+".y"+r.y+".w"+r.width+".h"+r.height, timeUnit, "Mean", x, y2);
			pwF.addPoints(x, y3,PlotWindow.LINE);
			
			PlotWindow pwN = new PlotWindow("normFRAP: "+ imp.getTitle()+"-x"+r.x+".y"+r.y+".w"+r.width+".h"+r.height, timeUnit, "Norm. intensity", x, y);

			PlotWindow pw = new PlotWindow("procFRAP: "+ imp.getTitle()+"-x"+r.x+".y"+r.y+".w"+r.width+".h"+r.height, timeUnit, "Norm. intensity", x, y);
			double [] a = Tools.getMinMax(x);
            		double xmin=a[0], xmax=a[1];
			pwF.setLimits(xmin,xmax,yFmin,yFmax);
			pwF.draw();
            		float [] values2 = new float [y.length];
           			int valsize = values2.length;
			//for (int j=0; j<valsize; j++) values2[j] = y[j];

            		a = Tools.getMinMax(y);
           			double ymin=a[0], ymax=a[1];
		            pw.setLimits(xmin,xmax,ymin,ymax);
				pwN.setLimits(xmin,xmax,ymin,ymax);
				//pwN.addPoints(x,y,PlotWindow.CIRCLE);
				pwN.draw();

		//fit curve
			int sliceMin=0;
			for (int i=0;i<y.length; i++)
				{
				//We'll find the Y minimum, and assume that this is the first post-bleach point...
				if(y[i]==ymin) sliceMin=i+1;	
				}
			sliceMin=sliceMin - 1;
			//IJ.showMessage("Slice offset (sliceMin): " + sliceMin);
			float[] f = new float[y.length-sliceMin];
			float[] x2 = new float[y.length-sliceMin];
			float[] x3 = new float[y.length-sliceMin];
			//IJ.showMessage("Length of new array: " + x3.length);
			double[] fd = new double[y.length-sliceMin];
			double[] x2d = new double[y.length-sliceMin];
			for (int i=0; i<y.length-sliceMin; i++)
				{f[i]=y[i+sliceMin];	
				fd[i]=(double)f[i];	
				x2[i] = x[i];//+(float)((float)sliceMin*timescale);
				x3[i]= x[i] +(float)((float)sliceMin*timescale);
				//IJ.log(x2[i] + "," + f[i]);
				x2d [i] = (double)x2[i];	
				//IJ.write(i + "\t"+ f[i]);
				}

			CurveFitter cf = new CurveFitter(x2d, fd) ;

//This constrains the single exponential recovery curve to go through the origin. 
//Use the built-in EXP_RECOVERY if this constraint is not needed.
				String eqn = null;
				if (fitMethod == 0) {eqn = "y = a*(1-exp(-b*x))";} else {eqn = "y = a*(1-exp(-b*x)) +c*(1-exp(-d*x)) + e";}
				int params = cf.doCustomFit(eqn, null, false);
				if (params==0) return;

//The line below uses the built-in				
			//cf.doFit(CurveFitter.EXP_RECOVERY);

			double[] p = cf.getParams();
			
			if (fitMethod == 0) {
				//p[0]*(1-Math.exp(-p[1]*x))+p[2])
				IJ.log("p[0]*(1-exp(-p[1]*x):  "+ df2.format(p[0])+";  "+ df2.format(p[1]));
				//IJ.log("p[0]*(1-exp(-p[1]*x)+p[2]:  "+ df2.format(p[0])+";  "+ df2.format(p[1]) +";  "+df2.format(p[2]));
				double halflife = 0.69/p[1];
				IJ.log("t 1/2: " + IJ.d2s(halflife,4)  + " sec");
				//IJ.log("% mobile: " + IJ.d2s((p[0]*100 + p[2]*100),2));
				IJ.log("% mobile: " +IJ.d2s((p[0]*100),2));
				double tmp=0;
				float[] fit = new float[y.length];
				float fitY=0;
				for (int z=0; z<x2.length; z++)
					{
					//What is this for??
					tmp = x2[z]-p[2];
       				     	if (tmp<0.001) tmp = 0.001;
					fit[z]=(float)(p[0]*(1-Math.exp(-p[1]*x2[z])));//+p[2]); [new!]
				}
			
				pw.setColor(Color.red);
				pw.addPoints(x3, fit, PlotWindow.LINE);
				pw.setColor(Color.black);
				pw.draw();

				PlotWindow pwO = new PlotWindow("offsetFRAP: "+ imp.getTitle()+"-x"+r.x+".y"+r.y+".w"+r.width+".h"+r.height, timeUnit, "Norm. intensity", x2,f);
		      		pwO.setLimits(xmin,xmax,ymin,ymax);
				pwO.draw();


				PlotWindow pwC = new PlotWindow("offsetFRAP-fit: "+ imp.getTitle()+"-x"+r.x+".y"+r.y+".w"+r.width+".h"+r.height, timeUnit, "Norm. intensity", x2,fit);
		      		pwC.setLimits(xmin,xmax,ymin,ymax);
				pwC.setColor(Color.blue);
				pwC.addPoints(x2, f, PlotWindow.CIRCLE);
				pwC.setColor(Color.black);
				pwC.draw();
				} //fitMethod = 0 (single exp)
			else {
				//p[0]*(1-Math.exp(-p[1]*x))+p[2])
				IJ.log("p[0]*(1-exp(-p[1]*x)) + p[2]*(1-exp(-p[3]*x)) + p[4]:  ");
				IJ.log(df2.format(p[0])+";  "+ df2.format(p[1]) + ";  " + df2.format(p[2])+";  "+ df2.format(p[3]) +";  "+ df2.format(p[4]));
				double halflife = 0.69/p[1];
				double halflife2 = 0.69/p[3];
				IJ.log("t 1/2 #1: " + IJ.d2s(halflife,4)  + " sec");
				IJ.log("t 1/2 #2: " + IJ.d2s(halflife2,4)  + " sec");
				//IJ.log("% mobile: " + IJ.d2s((p[0]*100 + p[2]*100),2));
				IJ.log("% mobile: " +IJ.d2s((p[0]*100 + p[2]*100),2));
				double tmp=0;
				float[] fit = new float[y.length];
				float fitY=0;
				for (int z=0; z<x2.length; z++)
					{
					//What is this for??
					tmp = x2[z]-p[2];
       				     	if (tmp<0.001) tmp = 0.001;
					fit[z]=(float)(p[0]*(1-Math.exp(-p[1]*x2[z])) + p[2]*(1-Math.exp(-p[3]*x2[z])) + p[4]);

					}
			
				pw.setColor(Color.red);
				pw.addPoints(x3, fit, PlotWindow.LINE);
				pw.setColor(Color.black);
				pw.draw();

				PlotWindow pwO = new PlotWindow("offsetFRAP: "+ imp.getTitle()+"-x"+r.x+".y"+r.y+".w"+r.width+".h"+r.height, timeUnit, "Norm. intensity", x2,f);
		     		pwO.setLimits(xmin,xmax,ymin,ymax);
				pwO.draw();


				PlotWindow pwC = new PlotWindow("offsetFRAP-fit: "+ imp.getTitle()+"-x"+r.x+".y"+r.y+".w"+r.width+".h"+r.height, timeUnit, "Norm. intensity", x2,fit);
		      		pwC.setLimits(xmin,xmax,ymin,ymax);
				pwC.setColor(Color.blue);
				pwC.addPoints(x2, f, PlotWindow.CIRCLE);
				pwC.setColor(Color.black);
				pwC.draw();
					} //fitMethod = 1 (double exp)

			if (drawROIs) drawRegions(sliceMin);
			}
		}

//draw duplicate image and color ROIs
		void drawRegions(int sliceMin){
			WindowManager.setCurrentWindow(myWindow);
			imp.setSlice(sliceMin+1);
			imp.unlock();
			IJ.run("Select All");
			IJ.run("Duplicate...", "title=ROIs");
			IJ.run("RGB Color");
			ImageWindow dupWindow = WindowManager.getCurrentWindow();
			ImagePlus dupImp = dupWindow.getImagePlus();
			dupImp.setRoi(roi);
			IJ.run("Colors...", "foreground=cyan");
			//IJ.run("Line Width...", "line=2");
			IJ.run("Draw");
			dupImp.setRoi(full);
			IJ.run("Colors...", "foreground=red");
			//IJ.run("Line Width...", "line=2");
			IJ.run("Draw");
			IJ.run("Line Width...", "line=1");
			IJ.run("Colors...", "foreground=black");
			dupImp.setRoi(0,0,0,0);
			WindowManager.setCurrentWindow(myWindow);
			imp.setRoi(0,0,0,0);
			imp.lock();
		}

	float[] getZAxisProfile(Roi roi, Roi full, double minThreshold, double maxThreshold) {

		ImageStack stack = imp.getStack();
		int size = stack.getSize();
		float[] values = new float[(size*2)];
		imp.setRoi(roi);
		ImageProcessor mask = imp.getMask();
		//int[] mask = imp.getMask();
		Rectangle r = roi.getBoundingRect();
	
		imp.setRoi(full);
		ImageProcessor mask2 = imp.getMask();
		Rectangle rFull = full.getBoundingRect();

		Calibration cal = imp.getCalibration();
		Analyzer analyzer = new Analyzer(imp);
		
		int measurements = analyzer.getMeasurements();
		boolean showResults = measurements!=0 && measurements!=LIMIT;
        		boolean showingLabels = (measurements&LABELS)!=0 || (measurements&SLICE)!=0;

		measurements |= MEAN;
		if (showResults) {
		if (!analyzer.resetCounter())
				return null;
		float avg=0;
		
		}
		int k=0;
		for (int i=1; i<=size; i++) {
			ImageProcessor ip = stack.getProcessor(i);
			if (minThreshold!=ImageProcessor.NO_THRESHOLD)
		            ip.setThreshold(minThreshold,maxThreshold,ImageProcessor.NO_LUT_UPDATE);
			
			ip.setRoi(full);
			ip.setMask(mask2);
			ImageStatistics stats2 = ImageStatistics.getStatistics(ip, measurements, cal);
			values[k] = (float)stats2.mean; k++;
            		
			ip.setRoi(r);
			ip.setMask(mask);
			ImageStatistics stats = ImageStatistics.getStatistics(ip, measurements, cal);
			analyzer.saveResults(stats, roi);
			values[k] = (float)stats.mean;k++;


			}
		return values;
			}

}


