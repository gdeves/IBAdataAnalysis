package tools.resolutionTestChart;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Plot;
import ij.gui.ProfilePlot;
import ij.gui.Roi;
import ij.measure.Calibration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import javax.swing.JFrame;

import utils.CurveMaximaFinder;

public class ResolutionTester extends Observable {
		public double[][] linesPerMillimeter;
		public double[] distances;
		private JFrame view;
		private int calibrationPixel = 130;
		private double calibrationDistance = 1;
		private ImagePlus image;
		private String [] groupsOfBars = {
			"-2, 1", "-2, 2", "-2, 3", "-2, 4", "-2, 5", "-2, 6",
			"-1, 1", "-1, 2", "-1, 3", "-1, 4", "-1, 5", "-1, 6",
			" 0, 1", " 0, 2", " 0, 3", " 0, 4", " 0, 5", " 0, 6",
			" 1, 1", " 1, 2", " 1, 3", " 1, 4", " 1, 5", " 1, 6",
			" 2, 1", " 2, 2", " 2, 3", " 2, 4", " 2, 5", " 2, 6",
			" 3, 1", " 3, 2", " 3, 3", " 3, 4", " 3, 5", " 3, 6",
			" 4, 1", " 4, 2", " 4, 3", " 4, 4", " 4, 5", " 4, 6",
			" 5, 1", " 5, 2", " 5, 3", " 5, 4", " 5, 5", " 5, 6",
			" 6, 1", " 6, 2", " 6, 3", " 6, 4", " 6, 5", " 6, 6",
			" 7, 1", " 7, 2", " 7, 3", " 7, 4", " 7, 5", " 7, 6",
			" 8, 1", " 8, 2", " 8, 3", " 8, 4", " 8, 5", " 8, 6",
			" 9, 1", " 9, 2", " 9, 3"
		};
		private int selectedGroupOfBarsIndex;
		private ArrayList<ResolutionMeasurement> horizontalMeasurements = new ArrayList<ResolutionMeasurement>();
		private ArrayList<ResolutionMeasurement> verticalMeasurements = new ArrayList<ResolutionMeasurement>();
		private double minBlackAreaIntensity;
		private double maxWhiteAreaIntensity;
		
		public ResolutionTester() {
			image = IJ.getImage();
			if (image==null) return;
			this.setupLinesPerMillimeter();
			this.setupDistances();
			selectedGroupOfBarsIndex = new ArrayList<String>(Arrays.asList(groupsOfBars)).indexOf(" 4, 1");
			this.updateBlackAreaIntensity();
			this.updateWhiteAreaIntensity();
		}

		public JFrame getView() {
			if (this.view==null) this.view = new ResolutionTesterView(this);
			return view;
		}
		
		public void show() {
			this.getView().setVisible(true);
		}
		
		protected void changed(String anAspect) {
			this.setChanged();
			this.notifyObservers(anAspect);
			this.clearChanged();
		}
		
		private void setupDistances() {
			distances = new double[6];		
			distances[0] = this.getBarLength(-2, 2);
			distances[1] = this.getBarLength(0, 2);
			distances[2] = this.getBarLength(2, 2);
			distances[3] = this.getBarLength(4, 2);
			distances[4] = this.getBarLength(6, 2);
			distances[5] = this.getBarLength(8, 2);
			this.changed("distances");
		}

		private void setupLinesPerMillimeter() {
			linesPerMillimeter = new double[12][6];
			
			linesPerMillimeter[0][0]=0.25;
			linesPerMillimeter[0][1]=0.28;
			linesPerMillimeter[0][2]=0.315;
			linesPerMillimeter[0][3]=0.353;
			linesPerMillimeter[0][4]=0.397;
			linesPerMillimeter[0][5]=0.445;
			
			linesPerMillimeter[1][0]=0.5;
			linesPerMillimeter[1][1]=0.561;
			linesPerMillimeter[1][2]=0.630;
			linesPerMillimeter[1][3]=0.707;
			linesPerMillimeter[1][4]=0.793;
			linesPerMillimeter[1][5]=0.891;
			
			linesPerMillimeter[2][0]=1;
			linesPerMillimeter[2][1]=1.12;
			linesPerMillimeter[2][2]=1.26;
			linesPerMillimeter[2][3]=1.41;
			linesPerMillimeter[2][4]=1.59;
			linesPerMillimeter[2][5]=1.78;
			
			linesPerMillimeter[3][0]=2;
			linesPerMillimeter[3][1]=2.24;
			linesPerMillimeter[3][2]=2.52;
			linesPerMillimeter[3][3]=2.83;
			linesPerMillimeter[3][4]=3.17;
			linesPerMillimeter[3][5]=3.56;
			
			linesPerMillimeter[4][0]=4;
			linesPerMillimeter[4][1]=4.49;
			linesPerMillimeter[4][2]=5.04;
			linesPerMillimeter[4][3]=5.66;
			linesPerMillimeter[4][4]=6.35;
			linesPerMillimeter[4][5]=7.13;
			
			linesPerMillimeter[5][0]=8;
			linesPerMillimeter[5][1]=8.98;
			linesPerMillimeter[5][2]=10.1;
			linesPerMillimeter[5][3]=11.3;
			linesPerMillimeter[5][4]=12.7;
			linesPerMillimeter[5][5]=14.3;
			
			linesPerMillimeter[6][0]=16.00;
			linesPerMillimeter[6][1]=17.95;
			linesPerMillimeter[6][2]=20.16;
			linesPerMillimeter[6][3]=22.62;
			linesPerMillimeter[6][4]=25.39;
			linesPerMillimeter[6][5]=28.50;
			
			linesPerMillimeter[7][0]=32;
			linesPerMillimeter[7][1]=36;
			linesPerMillimeter[7][2]=40.3;
			linesPerMillimeter[7][3]=45.3;
			linesPerMillimeter[7][4]=50.8;
			linesPerMillimeter[7][5]=57.0;
			
			linesPerMillimeter[8][0]=64;
			linesPerMillimeter[8][1]=71.8;
			linesPerMillimeter[8][2]=80.6;
			linesPerMillimeter[8][3]=90.5;
			linesPerMillimeter[8][4]=102.0;
			linesPerMillimeter[8][5]=114;
			
			linesPerMillimeter[9][0]=128;
			linesPerMillimeter[9][1]=143.69;
			linesPerMillimeter[9][2]=161.27;
			linesPerMillimeter[9][3]=181.02;
			linesPerMillimeter[9][4]=203.19;
			linesPerMillimeter[9][5]=228.07;
			
			linesPerMillimeter[10][0]=256;
			linesPerMillimeter[10][1]=287.35;
			linesPerMillimeter[10][2]=322.54;
			linesPerMillimeter[10][3]=362.04;
			linesPerMillimeter[10][4]=406.37;
			linesPerMillimeter[10][5]=456.14;
			
			linesPerMillimeter[11][0]=512;
			linesPerMillimeter[11][1]=574.7;
			linesPerMillimeter[11][2]=645.08;
			linesPerMillimeter[11][3]=Double.NaN;
			linesPerMillimeter[11][4]=Double.NaN;
			linesPerMillimeter[11][5]=Double.NaN;
		}
		
		public double getLinesPerMillimeter(int group, int element) {
			int groupIndex = group + 2;
			int elementIndex = element - 1;
			return linesPerMillimeter[groupIndex][elementIndex];
		}
		
		public double getBarLength(int group, int element) {
			double result = 2.5d / this.getLinesPerMillimeter(group, element);
			return result;
		}
		
		public double[] getDistances() {
			return distances;
		}

		public void setCalibrationPixel() {
			if (image==null) return;
			Roi aRoi = image.getRoi();
			if (aRoi==null) return;
			image.setCalibration(null);
			if (aRoi.isArea()) setCalibrationPixel(aRoi.getBounds().width);
			else setCalibrationPixel((int)aRoi.getLength());
		}

		private void setScale() {
			Calibration calibration = new Calibration();
			calibration.setImage(image);
			calibration.setUnit("mm");
			calibration.pixelWidth=calibrationDistance / calibrationPixel;
			calibration.pixelHeight = calibration.pixelWidth;
			image.setCalibration(calibration);
			image.updateAndRepaintWindow();
		}

		public void setCalibrationDistance(double dist) {
			calibrationDistance = dist;
			setScale();
		} 
		
		public static void createAndShow() {
			try {
				(new ResolutionTester()).show();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}

		public int getCalibrationPixel() {
			return calibrationPixel;
		}

		public void setCalibrationPixel(int calibrationPixel) {
			this.calibrationPixel = calibrationPixel;
			setScale();
		}

		public String[] getGroupsOfBars() {
			return groupsOfBars;
		}

		public int getSelectedGroupOfBarsIndex() {
			return selectedGroupOfBarsIndex;
		}

		public void setSelectedGroupOfBarsIndex(int selectedGroupOfBarsIndex) {
			this.selectedGroupOfBarsIndex = selectedGroupOfBarsIndex;
		}

		public void addHorizontalMeasurement() {
			this.addMeasurement(true);
		}
		
		public void addVerticalMeasurement() {
			this.addMeasurement(false);
		}

		public ArrayList<ResolutionMeasurement> getHorizontalMeasurements() {
			return horizontalMeasurements;
		}

		protected void addMeasurement(boolean horizontal) {
			ArrayList<ResolutionMeasurement> measurements = verticalMeasurements;
			String message = "verticalMeasurements";
			if (horizontal) {
				measurements = horizontalMeasurements;
				message = "horizontalMeasurements";
			}			
			Roi aRoi = image.getRoi();
			if (aRoi==null) return;
			double value = getHeightOfMaxima(horizontal);
			addElementToMeasurements(value, measurements);
			this.changed(message);
		}

		protected void addElementToMeasurements(double value, ArrayList<ResolutionMeasurement> measurements) {
			int index = selectedGroupOfBarsIndex + measurements.size();
			String element = groupsOfBars[index];
			String[] components = element.split(",");
			int groupIndex = Integer.parseInt(components[0].trim());
			int elementIndex = Integer.parseInt(components[1].trim());
			double linesPerMillimeter = getLinesPerMillimeter(groupIndex, elementIndex);
			ResolutionMeasurement measurement = new ResolutionMeasurement(value, element, linesPerMillimeter);
			measurements.add(measurement);
		}

		protected double getHeightOfMaxima(boolean horizontal) {
			CurveMaximaFinder maxFinder = getMaxFinder(horizontal);
			double value = 0;
			if (maxFinder.getNumberOfMaxima() < 3) return value;
			maxFinder.removeLeftmostMaximum();
			ArrayList<Double> heights = maxFinder.getHeights();
			ArrayList<Double> yValues = maxFinder.getYMaxima();
			double vWhite = yValues.get(0);
			double vBlack = yValues.get(0)- heights.get(0);
			double value0 = (vWhite - vBlack) / (vWhite + vBlack);  
			vWhite = yValues.get(1);
			vBlack = yValues.get(1)- heights.get(1);
			double value1 = (vWhite - vBlack) / (vWhite + vBlack);
			value = (value0 + value1) / 2.0d;
			double c0 = (this.maxWhiteAreaIntensity - this.minBlackAreaIntensity) / (this.maxWhiteAreaIntensity + this.minBlackAreaIntensity); 
			value = 100 * (value / c0);
			return value;
		}

		private CurveMaximaFinder getMaxFinder(boolean horizontal) {
			ProfilePlot profile = new ProfilePlot(image, horizontal);
			CurveMaximaFinder maxFinder = new CurveMaximaFinder(profile.getProfile());
			maxFinder.setMinHeight(0.1);
			maxFinder.setMinDistance(1);
			return maxFinder;
		}
		
		public ArrayList<ResolutionMeasurement> getVerticalMeasurements() {
			return verticalMeasurements;
		}

		protected Plot getPlot(ArrayList<ResolutionMeasurement> measurements) {
			double[] xValues = new double[measurements.size()+1];
			double[] yValues = new double[measurements.size()+1];
			double max = 100;
			xValues[0] = 0;
			yValues[0] = 100;
			int counter = 1;
			for (ResolutionMeasurement measurement : measurements) {
				xValues[counter] = measurement.getLinesPerMillimeter();
				yValues[counter] = measurement.getValue();
				counter++;
			}
			String zeroLabel = getZeroLabel(xValues, yValues);
			String fiftyPercentLabel = getFiftyPercentLabel(xValues, yValues, max);
			Plot plot = new Plot("resolution test", "lines/mm " + zeroLabel + ", "+ fiftyPercentLabel , "I", xValues, yValues, Plot.LINE + Plot.Y_GRID + Plot.X_GRID + Plot.X_NUMBERS);
			return plot;
		}

		private String getFiftyPercentLabel(double[] xValues, double[] yValues, double max) {
			String label = ""; 
			double x50 = this.getLinesPerMMAtFiftyPercent(xValues, yValues, max);
			label = "r[50%] undef.";
			if (!Double.isNaN(x50)) 
				label = "r[50%]="+Double.toString(x50);
			return label;
		}

		protected double getLinesPerMMAtFiftyPercent(double[] xValues, double[] yValues, double max) {
			double result = 0;
			double y50 = max / 2.0d;
			double x1=0,y1=0,x2=0,y2=0,m=0;
			boolean found = false;
			for (int rightIndex= yValues.length-1;  rightIndex>0; rightIndex--) {
				if ((yValues[rightIndex]<=y50 && yValues[rightIndex-1]>=y50) || (yValues[rightIndex]>=y50 && yValues[rightIndex-1]<=y50)) {
					x2 = xValues[rightIndex];
					y2 = yValues[rightIndex];
					x1 = xValues[rightIndex-1];
					y1 = yValues[rightIndex-1];
					m = (y2-y1) / (x2-x1);
					found = true;
					break;
				}
			}
			if (!found) return Double.NaN;
			result = x1 + (y50/m) - (y1/m);
			return result;
		}
		
		private String getZeroLabel(double[] xValues, double[] yValues) {
			int index=0;
			for (double val : yValues) {
				if (val==0)	break;
				index++;
			}
			String zeroLabel = "r[0] undef.";
			if (index<xValues.length) zeroLabel = "r[0]="+Double.toString(xValues[index]);
			return zeroLabel;
		}
		
		
		public Plot getHorizontalPlot() {
			return this.getPlot(this.getHorizontalMeasurements());
		}
		
		public Plot getVerticalPlot() {
			return this.getPlot(this.getVerticalMeasurements());
		}

		public double getMaxWhiteAreaIntensity() {
			return maxWhiteAreaIntensity;
		}

		public void setMaxWhiteAreaIntensity(double maxWhiteAreaIntensity) {
			this.maxWhiteAreaIntensity = maxWhiteAreaIntensity;
			this.changed("whiteAreaIntensity");
		}

		public double getMinBlackAreaIntensity() {
			return minBlackAreaIntensity;
		}

		public void setMinBlackAreaIntensity(double minBlackAreaIntensity) {
			this.minBlackAreaIntensity = minBlackAreaIntensity;
			this.changed("blackAreaIntensity");
		}

		public void updateBlackAreaIntensity() {
			this.setMinBlackAreaIntensity(image.getStatistics().min);		
		}

		public void updateWhiteAreaIntensity() {
			this.setMaxWhiteAreaIntensity(image.getStatistics().max);		
		}
}
