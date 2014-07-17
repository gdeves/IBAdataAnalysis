package splitAndMerge;
import analysis.signalToNoise.MedianThresholdSignalToNoiseEstimator;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.gui.ShapeRoi;

public class Shape extends ShapeRoi {
	private float intensity;
	private float gradient;
	private float area;
	private double entropy;
	private double signalToNoiseRatio;
	private static final long serialVersionUID = 1L;
	private boolean border;
	private ImagePlus image;
	private SplitAndMerge splitAndMerge;
	private int[] histogram = null;
	private int meanIntensity;
	private int maxIntensity;
	
	public int[] getHistogram() {
		return histogram;
	}

	public void setHistogram(int[] histogram) {
		this.histogram = histogram;
	}
	
	public Shape(Roi r, ImagePlus img, SplitAndMerge splitMerge) {
		super(r);

		border = false;
		image = img;
		splitAndMerge = splitMerge;
		this.area = (float) (this.getBounds().getWidth()*this.getBounds().getHeight());
	}
	
	public void merge(Shape shape) {
		this.or(shape);
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	public float getArea() {
		return area;
	}

	public void setArea(float area) {
		this.area = area;
	}

	public boolean getBorder() {
		return border;
	}

	public void setBorder(boolean border) {
		this.border = border;
	}

	public float getGradient() {
		return gradient;
	}

	public void setGradient(float gradient) {
		this.gradient = gradient;
	}

	public double getEntropy() {
		return entropy;
	}

	public void setEntropy(double entropy) {
		this.entropy = entropy;
	}

	public double getSignalToNoiseRatio() {
		return signalToNoiseRatio;
	}

	public void setSignalToNoiseRatio(float signalToNoiseRatio) {
		this.signalToNoiseRatio = signalToNoiseRatio;
	}

public void calculateHistrogram() {
		histogram = new int[256];
		for(int i=0;i<histogram.length;i++) histogram[i]=0;
		int x = this.getBounds().x;
		int y = this.getBounds().y;
		int width = this.getBounds().width;
		int height = this.getBounds().height;
		
		for(int i=0;i<width+x;i++) {
			for(int j=0;j<height+y;j++) {
				if(this.contains(i, j)){
					int index = (int) splitAndMerge.getPixel(i, j);
					histogram[index]++;
				}
			}
		}
	}
	
	public void calculateEntropy() {
		double sum = 0;
		int i = 0;
		for(i=0;i<histogram.length;i++) {
			if(histogram[i]==0) continue;
			double ratio = histogram[i]/((double)area);
			sum += (ratio*(Math.log(ratio)/Math.log(2)));
		}
		entropy = -sum;
	}
	
	public void addHistogram(Shape shape) {
		for(int i=0;i<histogram.length;i++) {
			int[] hist = shape.getHistogram();
			histogram[i] += hist[i];
		}
	}
	
	public void calculateSignalToNoiseRatio() {
		MedianThresholdSignalToNoiseEstimator sne = MedianThresholdSignalToNoiseEstimator.newFor(image, 6, 6);
		sne.run();
		signalToNoiseRatio = sne.getMedianSNR();
	}
	
	public void calculateMeanGradient() {
		float gradientMean = 0;
		for(int i=(int) this.getBounds().getX();i<this.getBounds().getX()+this.getBounds().getWidth();i++) {
			for(int j=(int) this.getBounds().getY();j<this.getBounds().getY()+this.getBounds().getHeight();j++) {
				gradientMean += splitAndMerge.getGradient(i,j);
			}
		}
		this.gradient = (float) (gradientMean / (this.getBounds().getWidth()*this.getBounds().getHeight()));
	}
	
	public void calculateIntensity() {
		float totalIntensity = 0;
		for(int i=(int) this.getBounds().getX();i<(this.getBounds().getX()+this.getBounds().getWidth());i++) {
			for(int j=(int) this.getBounds().getY();j<(this.getBounds().getY()+this.getBounds().getHeight());j++) {
				totalIntensity = totalIntensity + splitAndMerge.getPixel(i, j);
			}
		}
		this.intensity = totalIntensity / (float)(this.getBounds().getWidth()*this.getBounds().getHeight());
	}
	
	public void calculateMeanIntensity() {
		for(int i=0;i<histogram.length;i++) {
			if(histogram[i]!=0) {
				meanIntensity = i;
				break;
			}
		}
	}
	
	public void calculateMaxIntensity() {
		for(int i=(histogram.length)-1;i>=0;i--) {
			if(histogram[i]!=0) {
				maxIntensity = i;
				break;
			}
		}
	}

	public int getMaxIntensity() {
		return maxIntensity;
	}

	public int getMeanIntensity() {
		return meanIntensity;
	}

}
