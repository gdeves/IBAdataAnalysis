package tools.sparkMaster2D;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import operations.file.SaveAsTiffSequenceOperation;
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.gui.Roi;
import ij.process.FloatProcessor;

public class SparkMaster2DBaecker extends SparkMaster2DTask {

	private Roi cellRoi;
	private ZProfile cellProfile;
	private ImagePlus profilesImage;
	private int radius = 0;

	public SparkMaster2DBaecker(SparkMaster2D model) {
		super(model);
	}

	@Override
	public void run() {
		if (model.useBatchMode()) model.setBatchMode(true);
		model.setMessage("start processing");
		if (model.stopped) return;
		model.setProgressMin(0);
		model.setProgressMax(9*model.getImages().getSize());
		model.setProgress(0);
		
		for (int i=0; i<model.getImages().getSize(); i++) {
			/* 01 */	loadImage(i);							if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 02 */	excludeRegions();						if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 03 */	crop();									if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 04 */	removeFrames();							if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			normalizedImage = model.currentImage;
			
			/* 05 */	cellRoi = maskCell();					if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
																														
			/* 06 */ 	cellProfile = calculateProfile(cellRoi);if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 07 */    profilesImage = calculateProfilesImage();if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 08 */	saveProfilesImageAsSequence(i);			if (model.stopped) return;
																if (model.stopAfterEachStep()) waitOperation.run();
			/* 09 */ 	if (model.closeImages()) closeImages();	if (model.stopped) return;
			// propose garbage collection
			System.gc();
			Thread.yield();
		}
		
		model.setMessage("finished processing");
		model.setProgress(model.progressMax);
		model.setBatchMode(false);
		model = null;
		profilesImage.show();
		System.gc();
	}

	private void saveProfilesImageAsSequence(int index) {
		model.setMessage("saving the result image...");
		SaveAsTiffSequenceOperation op = new SaveAsTiffSequenceOperation();
		op.setInputImage(profilesImage);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern("yyyyMMddHHmmss");
		String timestampString = df.format(timestamp);
		op.setOutputFolder("seq-" + timestampString);
		op.setCreateInSourceFolder(true);
		File imageFile = new File((String)(model.getImages().getElementAt(index)) + ".tif");
		op.setPath(imageFile.getAbsolutePath());
		op.run();
		model.setProgress(model.progress + 1);
		
	}

	private ImagePlus calculateProfilesImage() {
		model.setMessage("calculating the profile images...");
		ImagePlus image = normalizedImage;
		ImagePlus profilesImage = 
			NewImage.createFloatImage("profiles", 
									  image.getWidth(), 
									  image.getHeight(), 
									  image.getNSlices(), 
									  NewImage.FILL_BLACK);
		Roi roi = new Roi(-radius,-radius,2*radius+1,2*radius+1);
		for (int x=0; x<image.getWidth(); x++) {
			for (int y=0; y<image.getHeight(); y++) {
				int xM = x-radius;
				int yM = y-radius;
				roi.setLocation(xM, yM);
				ZProfile profile = ZProfile.newFor(image, roi);
				profile.normalize();
				profile.subtract(cellProfile);
				float[] values = profile.getValues();
				for (int i=0; i<values.length; i++) {
					FloatProcessor ip = (FloatProcessor)profilesImage.getStack().getProcessor(i+1);
					ip.setf(x, y, values[i]);
				}
			}	
		}
		model.setProgress(model.progress + 1);
		return profilesImage;
	}

	private ZProfile calculateProfile(Roi aRoi) {
		model.setMessage("calculating the profile of the cell...");
		ZProfile cellProfile = ZProfile.newFor(normalizedImage, new Roi(aRoi.getBounds()));
		cellProfile.normalize();
		model.setProgress(model.progress + 1);
		return cellProfile;
	}
}
