package tools.sparkMaster2D;

import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.WindowManager;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import operations.Operation;
import operations.analysis.AnalyzeParticlesOperation;
import operations.control.WaitForUserOperation;
import operations.file.SaveImageOperation;
import operations.image.InvertImageOperation;
import operations.reporting.ReportMeasurementsOperation;
import operations.roi.CreateSelectionOperation;
import operations.roi.InverseSelectionOperation;
import operations.segmentation.ThresholdOperation;
import operations.stack.DeltaFUpOperation;
import operations.stack.ZProjectionOperation;
import statistics.BasicStatistics;
import statistics.BasicStatisticsDouble;
import statistics.studentTTest.StudentTTest;

public abstract class SparkMaster2DTask implements Runnable {
	protected SparkMaster2D model;
	protected ImagePlus normalizedImage;
	protected ImagePlus deltaImage;
	protected ImagePlus deltaMask;
	protected ResultsTable candidates;
	protected ResultsTable sparks;
	protected WaitForUserOperation waitOperation;
	
	public SparkMaster2DTask(SparkMaster2D model) {
		this.model = model;
		waitOperation = new WaitForUserOperation();	
	}
	
	abstract public void run();
	
	protected void closeImages() {
		model.setMessage("closing images...");
		model.currentImage.changes = false;
		model.currentImage.flush();
		model.currentImage.close();
		model.currentImage = null;
		deltaMask.changes = false;
		deltaMask.close();
		deltaMask.flush();
		deltaMask = null;
		System.gc();
		Thread.yield();
		model.setProgress(model.progress + 1);
	}

	protected void saveControlImage(int index) {
		model.setMessage("saving the control image...");
		WindowManager.setTempCurrentImage(model.currentImage);
		IJ.run("RGB Color");
		WindowManager.setTempCurrentImage(deltaMask);
		drawSparksAndCandidates();
		File imageFile = new File((String)(model.getImages().getElementAt(index)) + ".tif");
		String path = imageFile.getParent() + File.separator + "results";
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern("yyyyMMddHHmmss");
		String timestampString = df.format(timestamp);
		SaveImageOperation save = new SaveImageOperation();
		save.setInputImage(model.currentImage);
		save.setPath(imageFile.getAbsolutePath());
		save.setOutputFolder(path);
		save.setNameAddition("-" + timestampString);
		save.run();
		model.setProgress(model.progress + 1);
	}

	private void drawSparksAndCandidates() {
		int sparksCounter = 0;
		for (int i=0; i<candidates.getCounter(); i++) {
			int currentSparkX = 0;
			int currentSparkY = 0;
			int currentSparkZ = 0;
			if (sparksCounter<sparks.getCounter()) {
				currentSparkX = (int)(sparks.getValue("XStart", sparksCounter));
				currentSparkY = (int)(sparks.getValue("YStart", sparksCounter));
				currentSparkZ = (int)(sparks.getValue("Slice", sparksCounter));
			} 
			int x = (int)(candidates.getValue("XStart", i));
			int y = (int)(candidates.getValue("YStart", i));
			int z = (int)(candidates.getValue("Slice", i));
			boolean isSpark = false;
			Color foregroundColor = Color.YELLOW;
			if (x==currentSparkX && y==currentSparkY && z==currentSparkZ) {
				isSpark = true;
				foregroundColor = Color.GREEN;
				sparksCounter++;
			}
			if (!isSpark && !model.showSparkCandidates) continue;
			deltaMask.setSlice(z);
			WindowManager.setTempCurrentImage(deltaMask);
			IJ.doWand(x, y);
			model.currentImage.setSlice(z);
			model.currentImage.setRoi(deltaMask.getRoi());
			IJ.setForegroundColor(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue());
			WindowManager.setTempCurrentImage(model.currentImage);
			IJ.run("Draw");
			if (isSpark) {
				model.currentImage.getProcessor().setFont(new Font("SansSerif", Font.PLAIN, 10));
				Rectangle bounds = model.currentImage.getRoi().getBounds();
				model.currentImage.getProcessor().drawString(Integer.toString(sparksCounter), bounds.x, bounds.y - 5);
			}
		}
		Color oldForegroundColor = Prefs.getColor(Prefs.FCOLOR,Color.black);
		IJ.setForegroundColor(oldForegroundColor.getRed(), 
							  oldForegroundColor.getGreen(), 
							  oldForegroundColor.getBlue());
		model.currentImage.killRoi();
	}

	protected void saveMeasurements(int i) {
		model.setMessage("saving the measurements...");
		String imagePath = (String)(model.getImages().getElementAt(i));
		ReportMeasurementsOperation report = new ReportMeasurementsOperation();
		report.setMeasurements(sparks);
		report.setImageName(imagePath);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern("yyyyMMddHHmmss");
		String timestampString = df.format(timestamp);
		File imageFile = new File((String)(model.getImages().getElementAt(i)));
		String path = imageFile.getParent() + File.separator + "results";
		File pathFile = new File(path);
		if (!pathFile.exists()) pathFile.mkdir();
		path += File.separator + "sparks-" + imageFile.getName();
		path += "-" + timestampString + ".xls";
		report.setOutputPath(path);
		report.setInteractive(false);
		if (model.isBatchMode()) report.setShowResult(false);
		report.run();
		model.setProgress(model.progress + 1);
	}

	protected void applyStatisticalFilter() {
		model.setMessage("applying the statistical filter...");
		sparks = new ResultsTable();
		
		StudentTTest test = new StudentTTest();
		for (int i=0; i<candidates.getCounter(); i++) {
			int x = (int) Math.round(candidates.getValue("X", i));
			int y = (int) Math.round(candidates.getValue("Y", i));
			int z = (int)(candidates.getValue("Slice", i));
			if (z<7) continue;
			if (z>=model.currentImage.getNSlices()-1) continue;
			
			double[] samples1 = getKSamplesFor(x,y,z);
			double mean1 = getSampleAt(x, y, z);
			test.studentTTest(samples1, samples1.length, mean1);
			if (test.getBothtails()>=model.alphaConfidenceThreshold) continue;
			
			double[] samples2 = getKSamplesFor(x,y,z+1);
			double mean2 = getSampleAt(x, y, z+1);
			test.studentTTest(samples2, samples2.length, mean2);
			if (test.getBothtails()>=model.betaConfidenceThreshold) continue;
			
			BasicStatistics stats = new BasicStatisticsDouble(samples1);
			double mean = stats.getMean();
			double amplitude = (mean1 - mean) / mean;
			sparks.incrementCounter();
			sparks.addValue("X", candidates.getValue("X", i));
			sparks.addValue("Y", candidates.getValue("Y", i));
			sparks.addValue("Slice", candidates.getValue("Slice", i));
			sparks.addValue("XStart", candidates.getValue("XStart", i));
			sparks.addValue("YStart", candidates.getValue("YStart", i));
			sparks.addValue("Amplitude", amplitude);
		}
		model.setProgress(model.progress + 1);
	}

	private double[] getKSamplesFor(int x, int y, int z) {
		double[] samples = new double[5];
		int index = 0;
		for (int i=z-6; i<z-1;i++) {
			samples[index] = getSampleAt(x, y, i);
			index++;
		}
		return samples;
	}

	private double getSampleAt(int x, int y, int z) {
		ImagePlus theImage = model.currentImage;
		ImageProcessor ip = theImage.getStack().getProcessor(z);
		int radius = model.statisticalFilterRadius;
		double average = 0;
		int count = 0;
		for (int i=x-radius; i<=x+radius; i++) {
			for (int j=y-radius; j<=y+radius; j++) {
				average += ip.getPixelValue(i, j);
				count++;
			}
		}
		average /= (count * 1.0d);
		return average;
	}

	protected void measureCandidates() {
		model.setMessage("find candidate sparks...");
		AnalyzeParticlesOperation find = new AnalyzeParticlesOperation();
		find.setInputImage(deltaMask);
		if (model.isBatchMode()) find.setShowResult(false);
		find.setMeasureCentroids(true);
		find.setDisplaySliceNumber(true);
		find.setRecordStarts(true);
		find.setCalculateMask(false);
		find.run();
		candidates = find.getMeasurements();
		model.setProgress(model.progress + 1);
	}

	protected void applyLiveOrDie() {
		model.setMessage("applying live or die...");
		Operation op = model.liveOrDieOperation;
		op.setInputImage(deltaMask);
		if (model.isBatchMode()) op.setShowResult(false);
		op.setKeepSource(false);
		op.run();
		model.setProgress(model.progress + 1);
	}

	protected void thresholdDeltaImage() {
		model.setMessage("Thresholding sparks...");
		WindowManager.setTempCurrentImage(deltaImage);
		deltaImage.setTitle("deltas-mask");
		deltaMask = deltaImage;
		deltaImage = null;
		float threshold = model.sparksThreshold;
		((FloatProcessor)deltaMask.getProcessor()).setMinAndMax(-1, 1);
		deltaMask.getProcessor().setThreshold(-1, threshold, ImageProcessor.NO_LUT_UPDATE);
		WindowManager.setTempCurrentImage(deltaMask);
		IJ.run("NaN Background", "stack");
		IJ.run("8-bit", "stack");
		deltaMask.getProcessor().setThreshold(0, 0, ImageProcessor.RED_LUT);
		IJ.run("Convert to Mask", "stack");
		model.setProgress(model.progress + 1);
	}

	protected void calculateDeltaImage() {
		model.setMessage("Calculating difference-stack...");
		DeltaFUpOperation op = new DeltaFUpOperation();
		if (model.isBatchMode()) op.setShowResult(false);
		op.setInputImage(normalizedImage);
		op.run();
		deltaImage = op.getResult();
		deltaImage.setTitle("deltas");
		deltaImage.show();
		IJ.run("Duplicate...", "title=one-more-slice");
		IJ.run("8-bit");
		IJ.run("Select All");
		IJ.run("Fill");
		IJ.run("32-bit");
		ImagePlus oneMoreSlice = IJ.getImage();
		oneMoreSlice.killRoi();
		deltaImage.getStack().addSlice("delta F up 0", oneMoreSlice.getProcessor(), 0);
		deltaImage.setStack("deltas+", deltaImage.getStack());
		oneMoreSlice.changes = false;
		oneMoreSlice.close();
		oneMoreSlice.flush();
		normalizedImage.changes = false;
		normalizedImage.close();
		normalizedImage.flush();
		normalizedImage = null;
		System.gc();
		Thread.yield();
		model.setProgress(model.progress + 1);
	}

	protected Roi maskCell() {
		model.setMessage("Masking the cell...");
		
		Operation thresholdOperation = (Operation)model.thresholdOperations.getSelectedItem();
		thresholdOperation.setInputImage(model.currentImage);
		if (model.isBatchMode()) thresholdOperation.setShowResult(false);
		thresholdOperation.run();
		ImagePlus mask = thresholdOperation.getResult();
		
		Operation projectionOperation = new ZProjectionOperation();
		projectionOperation.setInputImage(mask);
		if (model.isBatchMode()) projectionOperation.setShowResult(false);
		projectionOperation.run();
		ImagePlus projection = projectionOperation.getResult();
		
		ThresholdOperation threshold = new ThresholdOperation();
		threshold.setThreshold(1);
		threshold.setInputImage(projection);
		threshold.setKeepSource(false);
		if (model.isBatchMode()) threshold.setShowResult(false);
		threshold.run();
		
		InvertImageOperation invert = new InvertImageOperation();
		invert.setInputImage(projection);
		invert.setKeepSource(false);
		if (model.isBatchMode()) invert.setShowResult(false);
		invert.run();
		
		Operation createSelectionOperation = new CreateSelectionOperation();
		createSelectionOperation.setInputImage(projection);
		createSelectionOperation.setKeepSource(false);
		if (model.isBatchMode()) createSelectionOperation.setShowResult(false);
		createSelectionOperation.run();
		
		Roi roi = createSelectionOperation.getResult().getRoi();
		normalizedImage.setRoi(roi);
		InverseSelectionOperation inverse = new InverseSelectionOperation();
		inverse.setInputImage(normalizedImage);
		inverse.setKeepSource(false);
		if (model.isBatchMode()) inverse.setShowResult(false);
		inverse.run();
		WindowManager.setTempCurrentImage(normalizedImage);
		IJ.run("Fill", "stack");
		normalizedImage.killRoi();
		
		mask.changes = false;
		mask.flush();
		mask.close();
		
		projection.changes = false;
		projection.flush();
		projection.close();
		
		model.setProgress(model.progress + 1);
		return roi;
	}

	protected void smooth() {
		if (model.isSmoothImage()) {
			model.setMessage("Smoothing image...");
			Operation op = (Operation)model.smoothingOperations.getSelectedItem();
			op.setInputImage(this.normalizedImage);
			op.setKeepSource(false);
			if (model.isBatchMode()) op.setShowResult(false);
			op.run();
		}
		model.setProgress(model.progress + 1);
	}
	
	protected void normalize() {
		model.setMessage("Normalizing image...");
		ImagePlus image = model.currentImage;
		ImagePlus normalizationSlice = null;
		if (model.useProjection) normalizationSlice = this.getAverageZProjection();
		else normalizationSlice = this.getNormalizationSlice();
		IJ.run("Add...", "value=1");
		IJ.run("Image Calculator...", "image1=" + image.getTitle() + 
					" operation=Divide image2=" + normalizationSlice.getTitle() + " create 32-bit stack");
		normalizedImage = IJ.getImage();
		normalizationSlice.changes = false;
		normalizationSlice.close();
		normalizationSlice.flush();
		normalizationSlice = null;
		System.gc();
		Thread.yield();
		model.setProgress(model.progress + 1);
	}
	
	private ImagePlus getAverageZProjection() {
		ZProjectionOperation projection = new ZProjectionOperation();
		projection.setInputImage(model.currentImage);
		projection.setKeepSource(true);
		if (model.isBatchMode()) projection.setShowResult(false);
		projection.setMethod("Average Intensity");
		projection.run();
		ImagePlus normalizationSlice = projection.getResult();
		normalizationSlice.show();
		return normalizationSlice;
	}
	
	private ImagePlus getNormalizationSlice() {
		int index = ((Integer)model.normalizationFrame.getValue()).intValue();
		ImagePlus image = model.currentImage;
		image.setSlice(index);
		IJ.run("Duplicate...", "title=normalization-slice");
		ImagePlus normalizationSlice = IJ.getImage();
		return normalizationSlice;
	}
	
	protected void subtractBackground() {
		model.setMessage("Subtracting background...");
		for (int i=1; i<=model.currentImage.getNSlices(); i++) {
			model.currentImage.setSlice(i);
			model.currentImage.setRoi(model.getBackground());
			double value = model.currentImage.getStatistics(ImageStatistics.MEAN).mean;
			WindowManager.setTempCurrentImage(model.currentImage);
			model.currentImage.killRoi();
			IJ.run("Subtract...", "slice value=" + value);
		}
		model.currentImage.killRoi();
	}
	
	protected void removeFrames() {
		model.setMessage("Removing frames...");
		int beforeFirst = ((Integer)(model.startFrame.getValue())).intValue() - 1;
		int afterLast = ((Integer)(model.endFrame.getValue())).intValue() + 1;
		if (beforeFirst>0) {
			IJ.run("Slice Remover", "first="+ 1 +" last=" + beforeFirst + " increment=1");
			afterLast -= beforeFirst;
		}
		if (afterLast<=model.currentImage.getNSlices()) 
			IJ.run("Slice Remover", "first="+ afterLast +" last=" + model.currentImage.getNSlices() + " increment=1");
		model.setProgress(model.progress + 1);
	}
	
	protected void crop() {
		model.setMessage("Cropping image...");
		if (!model.roi.isEmpty()) {
			model.currentImage.setRoi((Roi)model.roi.getElementAt(0));
			IJ.run("Crop");
		}
		model.setProgress(model.progress + 1);	
	}
	
	protected void excludeRegions() {
		model.setMessage("Excluding regions...");
		Enumeration<?> excludedRegions = (Enumeration<?>) model.excludedRegions.elements();
		while (excludedRegions.hasMoreElements()) {
			Roi current = (Roi)excludedRegions.nextElement();
			model.currentImage.setRoi(current);
			IJ.run("Fill", "stack");
		}
		model.currentImage.killRoi();
		model.setProgress(model.progress + 1);
	}
	
	protected void loadImage(int i) {
		model.setMessage("Loading image " + i+1 + "/" + model.getImages().getSize());
		String imagePath = (String) model.getImages().getElementAt(i);
		model.openImage(imagePath);
		model.currentImage.show();
		model.setProgress(model.progress + 1);
	}
}
