package tools.sparkMaster2D;

import java.util.ArrayList;
import java.util.Iterator;

import utils.CurveMaximaFinder;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.NewImage;
import ij.gui.Roi;
import ij.plugin.filter.MRIZAxisProfiler;
import ij.process.ImageProcessor;

public class SparkMaster2DAptel extends SparkMaster2DTask {

	public SparkMaster2DAptel(SparkMaster2D model) {
		super(model);
	}

	@Override
	public void run() {
		if (model.useBatchMode()) model.setBatchMode(true);
		model.setMessage("start processing");
		if (model.stopped) return;
		model.setProgressMin(0);
		model.setProgressMax(6*model.getImages().getSize());
		model.setProgress(0);
		for (int i=0; i<model.getImages().getSize(); i++) {
			/* 01 */	loadImage(i);							if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 02 */	subtractBackground();					if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 03 */	excludeRegions();						if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 04 */	crop();									if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 05 */	removeFrames();							if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
						normalizedImage = model.currentImage;
			/* 06 */ 	if (model.maskCell) maskCell();			if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			
			/* 07 */	findCandidateSparks();					if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			// propose garbage collection
			System.gc();
			Thread.yield();
		}
		model.setMessage("finished processing");
		model.setProgress(model.progressMax);
		model.setBatchMode(false);
		model = null;
		System.gc();
	}

	private void findCandidateSparks() {
		model.setMessage("Detecting candidate sparks...");
		ImagePlus image = normalizedImage;
		ImagePlus candidates = NewImage.createByteImage("candidates", image.getWidth(), image.getHeight(), image.getNSlices(), NewImage.FILL_BLACK);
		ImageProcessor ip = image.getProcessor();
		int radius = 1;
		Roi roi = new Roi(0,0,2*radius+1, 2*radius+1);
		MRIZAxisProfiler profiler = new MRIZAxisProfiler();
		profiler.setup(null, image);
		double minThreshold = ip.getMinThreshold();
		double maxThreshold = ip.getMaxThreshold();
		for (int x=0; x<image.getWidth(); x++) {
			for (int y=0; y<image.getHeight(); y++) {
				if (ip.get(x, y)==0) continue;
				roi.setLocation(x, y);
				float[] profile = profiler.getZAxisProfile(roi, minThreshold, maxThreshold);
				CurveMaximaFinder finder = new CurveMaximaFinder(profile);
				finder.setMinHeight(0.5);
				drawMaximaOn(candidates, x, y, finder.getMaxima(), finder.getHeights());
			}
		}
		candidates.show();
	}

	private void drawMaximaOn(ImagePlus candidates, int x, int y, ArrayList<Integer> maxima, ArrayList<Double> heights) {
		Iterator<Integer> maximaIterator = maxima.iterator();
		Iterator<Double> heightsIterator = heights.iterator();
		ImageStack stack = candidates.getStack();
		while(maximaIterator.hasNext()) {
			ImageProcessor ip = stack.getProcessor(maximaIterator.next().intValue()+1);
			ip.set(x,y,heightsIterator.next().intValue());
		}
	}

}
