package splitAndMerge;

import ij.ImagePlus;
import java.util.ArrayList;
import java.util.Iterator;
import regionGrowing.ImageTypeDoesntExist;

public class SplitAndMergeThread implements Runnable{

	private ImagePlus image;
	static public int thresholdSplit = 9;
	static public int thresholdSmallShape = 26;
	static public int thresholdBackground = 10;
	static public boolean isRunning = false;
	public ArrayList<SplitAndMergeCondition> listMergeCondition;
	private SplitAndMergeTool model;
	private ArrayList<MergeCondition> mergeConditionList;
	private boolean isStopped;
	
	public SplitAndMergeThread(ImagePlus img, SplitAndMergeTool localModel, int localThresholdSplit, int localThresholdSmallShape, int localThresholdBackground, ArrayList<SplitAndMergeCondition> listCondition)
	{
		image = img;
		thresholdSplit = localThresholdSplit;
		thresholdSmallShape = localThresholdSmallShape;
		thresholdBackground = localThresholdBackground;
		model = localModel;
		listMergeCondition = new ArrayList<SplitAndMergeCondition>(listCondition);
		mergeConditionList = new ArrayList<MergeCondition>();
	}
	
	public void run() {
		isStopped = false;
		try {
			Iterator<SplitAndMergeCondition> it = listMergeCondition.iterator();
			while(it.hasNext()) {
				if (this.isStopped) return;
				SplitAndMergeCondition condition = it.next();
				if(condition.getCondition().equalsIgnoreCase("Gradient")) {
					int thresholdGradient = condition.getValue();
					GradientMergeCondition mergeCondition = new GradientMergeCondition();
					mergeCondition.setThreshold(thresholdGradient);
					mergeConditionList.add(mergeCondition);
					if(condition.getOperator()==null) mergeCondition.setOperator("and");
					else mergeCondition.setOperator(condition.getOperator());
					mergeCondition.setNot(condition.getNot());
				}
				if(condition.getCondition().equalsIgnoreCase("Intensity")) {
					int thresholdMerge = condition.getValue();
					IntensityMergeCondition mergeCondition = new IntensityMergeCondition();
					mergeCondition.setThreshold(thresholdMerge);
					mergeConditionList.add(mergeCondition);
					if(condition.getOperator()==null) mergeCondition.setOperator("and");
					else mergeCondition.setOperator(condition.getOperator());
					mergeCondition.setNot(condition.getNot());
				}
				if(condition.getCondition().equalsIgnoreCase("Entropy")) {
					int thresholdEntropy = condition.getValue();
					EntropyMergeCondition mergeCondition = new EntropyMergeCondition();
					mergeCondition.setThreshold(thresholdEntropy);
					mergeConditionList.add(mergeCondition);
					if(condition.getOperator()==null) mergeCondition.setOperator("and");
					else mergeCondition.setOperator(condition.getOperator());
					mergeCondition.setNot(condition.getNot());
				}
				if(condition.getCondition().equalsIgnoreCase("SNR")) {
					int thresholdSignalToNoiseRatio = condition.getValue();
					SignalToNoiseRatioMergeCondition mergeCondition = new SignalToNoiseRatioMergeCondition();
					mergeCondition.setThreshold(thresholdSignalToNoiseRatio);
					mergeConditionList.add(mergeCondition);
					if(condition.getOperator()==null) mergeCondition.setOperator("and");
					else mergeCondition.setOperator(condition.getOperator());
					mergeCondition.setNot(condition.getNot());
				}
			}

			if (this.isStopped) return;
			
			model.showWorking(true);
			
			double begin = System.currentTimeMillis();
			System.out.println("start split : "+begin);
			
			SplitAndMerge data = SplitAndMerge.newFor(image);
			data.setConditionMergeList(mergeConditionList);
			data.startSplit(thresholdSplit);
			System.out.println("end split start rag : "+(System.currentTimeMillis()-begin));	
			Graph graph = data.makeAdjacencyGraph();
			System.out.println("end rag start merge : "+(System.currentTimeMillis()-begin));

			if (this.isStopped) return;
			
			if(mergeConditionList.size()!=0) {	
				graph.mergeAllNodes(mergeConditionList);
				System.out.println("end merge : "+(System.currentTimeMillis()-begin));
				data.drawShapes();
				data.cleanList();
				data.mergeSmallShape(thresholdSmallShape);
				data.mergeIsolatedShape(thresholdBackground);
				data.mergeToImproveCircularity(thresholdBackground);
				System.out.println("end : "+(System.currentTimeMillis()-begin));
			}
			
			if (this.isStopped) return;
			
			ImagePlus mask = data.drawShapes();
			image.killRoi();
			mask.killRoi();
			mask.show();
			SplitAndMergeToolView.isRunning=false;
			model.showWorking(false);
			
		} catch (ImageTypeDoesntExist f) {
			f.printStackTrace();
		}
	}

	public void stop() {
		this.isStopped = true;
		SplitAndMergeToolView.isRunning=false;
		model.showWorking(false);
	}
}
