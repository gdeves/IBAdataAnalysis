package splitAndMerge;

import ij.IJ;
import ij.ImagePlus;

import java.util.ArrayList;
import java.util.Observable;

public class SplitAndMergeTool extends Observable {

	protected  boolean thresholdSplits;
	protected int thresholdSplit = 9;
	protected int thresholdSmallShape = 26;
	protected int thresholdBackground = 10;
	protected SplitAndMergeToolView view;
	private SplitAndMergeThread thread;
	private ArrayList<SplitAndMergeCondition> listMergeCondition = new ArrayList<SplitAndMergeCondition>();
	
	public ArrayList<SplitAndMergeCondition> getListMergeCondition() {
		return listMergeCondition;
	}

	public void setListMergeCondition(ArrayList<SplitAndMergeCondition> listMergeCondition) {
		this.listMergeCondition = listMergeCondition;
	}

	public SplitAndMergeToolView getView() {
		if(view==null)
			view = new SplitAndMergeToolView(this);
		return view;
	}

	private void changed(String anAspect) {
		this.setChanged();
		this.notifyObservers(anAspect);
	}

	public void showWorking(boolean b) {
		if(b)
			this.changed("start working");
		else
			this.changed("stop working");
	}

	public int getThresholdBackground() {
		return thresholdBackground;
	}

	public void setThresholdBackground(int thresholdBackground) {
		this.thresholdBackground = thresholdBackground;
	}

	public int getThresholdSmallShape() {
		return thresholdSmallShape;
	}

	public void setThresholdSmallShape(int thresholdSmallShape) {
		this.thresholdSmallShape = thresholdSmallShape;
	}

	public int getThresholdSplit() {
		return thresholdSplit;
	}

	public void setThresholdSplit(int thresholdSplit) {
		this.thresholdSplit = thresholdSplit;
	}

	public void run() {
		ImagePlus image = IJ.getImage();
		thread = new SplitAndMergeThread(image, this, thresholdSplit, thresholdSmallShape, thresholdBackground, listMergeCondition);
		new Thread(thread).start();
	}

	public void stopThread() {
		thread.stop();
	}

}
