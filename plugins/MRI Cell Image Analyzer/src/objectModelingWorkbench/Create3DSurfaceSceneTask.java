package objectModelingWorkbench;

import ij.ImagePlus;
import ij.gui.NewImage;
import ij3d.Image3DUniverse;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.vecmath.Color3f;

public class Create3DSurfaceSceneTask implements Runnable {

	private int[] selectedSlices;
	private int[] selectedObjects;
	private ObjectModelingWorkbench bench;
	private Object3DManager manager;
	private int resamplingFactor;
	private Image3DUniverse scene;

	public Create3DSurfaceSceneTask(int[] selectedObjects, int[] selectedSlices, int resamplingFactor, ObjectModelingWorkbench bench, Object3DManager manager, Image3DUniverse scene) {
		this.selectedObjects = selectedObjects;
		this.selectedSlices = selectedSlices;
		this.resamplingFactor = resamplingFactor;
		this.bench = bench;
		this.manager = manager;
		this.scene = scene;
	}

	@Override
	public void run() {
		Alignments alignments = new Alignments();
		ArrayList<AlignmentSlice> slices = new ArrayList<AlignmentSlice>();
		int numberOfSlices = 0;
		for (int i=selectedSlices[0]; i<=selectedSlices[selectedSlices.length-1]; i++) {
			slices.add(bench.getAlignments().getAlignments().get(i));
			numberOfSlices++;
		}
		alignments.setAlignments(slices);
		Rectangle zeroAndImageSize = alignments.calculateImageSize();
		String name = manager.getNameForStack(selectedObjects, selectedSlices);
		boolean[] channels = {true, true, true};
		int factor = Math.min(resamplingFactor, numberOfSlices / 2);
		for (int i=0; i<selectedObjects.length; i++) {
			ImagePlus newImage = NewImage.createByteImage(name, zeroAndImageSize.width, zeroAndImageSize.height, 
					   numberOfSlices, NewImage.FILL_BLACK);
			Object3D object = (Object3D) manager.getObjects().get(selectedObjects[i]);
			if (object.countObjectSlicesAmong(selectedSlices)==0) continue;
			object.paintSelectedSlicesOn(newImage, selectedSlices, slices, zeroAndImageSize);
			scene.addVoltex(newImage, new Color3f(object.getColor()), object.getName(), 1, channels, factor);
			newImage.close();
		}
	}
	
}
