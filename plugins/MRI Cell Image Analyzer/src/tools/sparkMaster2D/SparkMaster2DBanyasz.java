package tools.sparkMaster2D;

public class SparkMaster2DBanyasz extends SparkMaster2DTask {

	public SparkMaster2DBanyasz(SparkMaster2D model) {
		super(model);
	}

	@Override
	public void run() {
		if (model.useBatchMode()) model.setBatchMode(true);
		model.setMessage("start processing");
		if (model.stopped) return;
		model.setProgressMin(0);
		model.setProgressMax(15*model.getImages().getSize());
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
			/* 06 */	normalize();							if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 07 */	smooth();								if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 08 */ 	if (model.maskCell) maskCell();			if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 09 */	calculateDeltaImage();					if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 10 */ 	thresholdDeltaImage();					if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 11 */	applyLiveOrDie();						if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 12 */ 	measureCandidates(); 					if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 13 */ 	applyStatisticalFilter(); 				if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 14 */	saveMeasurements(i); 					if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 15 */ 	saveControlImage(i);					if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 16 */ 	if (model.closeImages()) closeImages();	if (model.stopped) return;
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

}
