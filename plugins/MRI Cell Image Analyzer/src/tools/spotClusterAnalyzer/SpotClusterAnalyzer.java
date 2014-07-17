package tools.spotClusterAnalyzer;

import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.NewImage;
import ij.measure.ResultsTable;
import ij.process.ImageStatistics;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;

import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.knowceans.mcl.MarkovClustering;
import org.knowceans.mcl.SparseMatrix;

import operations.analysis.Find3dObjectsOperation;
import operations.channel.CreateCompositeOperation;

public class SpotClusterAnalyzer extends Observable {
	protected SpotClusterAnalyzerView view;
	protected ImagePlus blueImage;
	protected ImagePlus redImage;
	protected ImagePlus greenImage;
	protected ImagePlus composite;
	protected ResultsTable redPoints;
	protected ResultsTable greenPoints;
	protected ArrayList<SpotNode> graph;
	protected SpinnerNumberModel maxDistance;
	protected SpinnerNumberModel threshold;

	public void show() {
		this.getView().setVisible(true);
	}

	public SpotClusterAnalyzerView getView() {
		if (this.view==null) {
			this.view = new SpotClusterAnalyzerView(this);
		}
		return this.view;
	}
	
	public static void openNew() {
		(new SpotClusterAnalyzer()).show();
	}

	public void setBlueImage(ImagePlus image) {
		this.blueImage = image;
		composite = null;
		this.changed("blueImage");
	}
	
	public void changed(String string) {
		this.setChanged();
		this.notifyObservers(string);
		this.clearChanged();
	}

	public ImagePlus getBlueImage() {
		return blueImage;
	}

	public void toggleShowImage(ImagePlus image) {
		if (image==null) return;
		if (image.isVisible()) 
			image.hide(); 
		else 
			image.show();
	}

	public void setRedImage(ImagePlus image) {
		this.redImage = image;
		composite = null;
		this.changed("redImage");
	}
	
	public void setGreenImage(ImagePlus image) {
		this.greenImage = image;
		composite = null;
		this.changed("greenImage");
	}

	public ImagePlus getGreenImage() {
		return greenImage;
	}

	public ImagePlus getRedImage() {
		return redImage;
	}

	public ImagePlus getComposite() {
		if (composite==null) this.createComposite();
		return composite;
	}

	private void createComposite() {
		CreateCompositeOperation createComposite = new CreateCompositeOperation();
		createComposite.setShowResult(false);
		createComposite.setInputImageBlue(getBlueImage());
		createComposite.setInputImageGreen(getGreenImage());
		createComposite.setInputImageRed(getRedImage());
		createComposite.setInputImageGray(getRedImage());
		if (redImage==null) createComposite.setRedChannel("none");
		if (greenImage==null) createComposite.setGreenChannel("none");
		if (blueImage==null) createComposite.setBlueChannel("none");
		createComposite.setGrayChannel("none");
		createComposite.run();
		composite = createComposite.getResult();
	}

	public void calculateRedPoints(ImagePlus mask) {
		this.redPoints = this.calculatePoints(mask);	
		this.changed("red points");
	}
	
	public void calculateGreenPoints(ImagePlus mask) {
		this.greenPoints = this.calculatePoints(mask);	
		this.changed("green points");
	}

	protected ResultsTable calculatePoints(ImagePlus mask) {
		Find3dObjectsOperation find = new Find3dObjectsOperation();
		find.setShowResult(false);
		find.setInputImage(mask);
		find.run();
		ResultsTable result = find.getMeasurements();
		return result;
	}

	public void toggleShowRedPoints() {
		if (redPoints==null) return;
		Frame redPointsFrame = WindowManager.getFrame("red points");
		if (redPointsFrame==null) 
			this.redPoints.show("red points");
		else 
			redPointsFrame.setVisible(false);
	}

	public void toggleShowGreenPoints() {
		if (greenPoints==null) return;
		Frame greenPointsFrame = WindowManager.getFrame("green points");
		if (greenPointsFrame==null) 
			this.greenPoints.show("green points");
		else 
			greenPointsFrame.setVisible(false);
	}

	public int getNumberOfGreenPoints() {
		return greenPoints.getCounter();
	}

	public int getNumberOfRedPoints() {
		return redPoints.getCounter();
	}

	public void combineRedAndGreenPoints() {
		// TODO Auto-generated method stub
		
	}

	public void calculateGraph() {
		createNodes();
		this.connectNodes();
		double[][] matrix = getMatrix();
		matrix = this.runMarkovClustering(matrix);
		this.createClusters(matrix);
	}

	private void createClusters(double[][] matrix) {
		int numberOfEdges = 0;
		int numberOfAddedEdges = 0;
		ArrayList<SpotNode> newGraph = new ArrayList<SpotNode>();
		for (SpotNode node : graph) {
			ArrayList<SpotEdge> edges = node.getEdges();
			for (SpotEdge edge : edges) {
				numberOfEdges++;
				int startIndex = edge.startNode.getId();
				int endIndex = edge.endNode.getId();
				SpotNode startNode = graph.get(startIndex);
				SpotNode endNode = graph.get(endIndex);
				SpotNode newStartNode = new SpotNode(startNode.id, startNode.x, startNode.y, startNode.z, startNode.image, startNode.maxIntensity);
				SpotNode newEndNode = new SpotNode(endNode.id, endNode.x, endNode.y, endNode.z, endNode.image, endNode.maxIntensity);
				if (!newGraph.contains(startNode)) newGraph.add(newStartNode);
				if (!newGraph.contains(endNode)) newGraph.add(newEndNode);
				if (matrix[startIndex][endIndex]>0 || matrix[endIndex][startIndex]>0) {
					newStartNode.connectWith(newEndNode);
					numberOfAddedEdges++;
				}
			}
		}
		System.out.println("removed " + (numberOfEdges - numberOfAddedEdges) + " edges");
		graph = newGraph;
	}

	private void createNodes() {
		double max = blueImage.getStatistics(ImageStatistics.MIN_MAX).max;
		int nrOfGreen = getNumberOfGreenPoints();
		int nrOfRed = getNumberOfRedPoints();
		graph = new ArrayList<SpotNode>(nrOfRed+nrOfGreen);
		int counter = 0;
		for (int i=0; i<nrOfRed; i++) {
			SpotNode node = new SpotNode(counter,(float)redPoints.getValue("Centre X", i),
					     				 (float)redPoints.getValue("Centre Y", i),
					     				 (float)redPoints.getValue("Centre Z", i),
					     				 blueImage, max);
			counter++;
			node.beRed();
			graph.add(node);
		}
		for (int i=0; i<nrOfGreen; i++) {
			SpotNode node = new SpotNode(counter,(float)greenPoints.getValue("Centre X", i),
					     				 (float)greenPoints.getValue("Centre Y", i),
					     				 (float)greenPoints.getValue("Centre Z", i),
					     				 blueImage, max);
			counter++;
			node.beGreen();
			graph.add(node);
		}
	}

	private void connectNodes() {
		float maxDistance = getMaxDistanceValue();
		for (int i=0; i<graph.size()-1; i++) {
			for (int j=i+1; j<graph.size(); j++) {
				SpotNode first = graph.get(i);
				SpotNode second = graph.get(j);
				if (first.distanceTo(second)<maxDistance ) {
					first.connectWith(second);
				}
			}
		}	
		removeEdges();
	}

	private float getMaxDistanceValue() {
		return ((Integer)this.maxDistance.getValue()).floatValue();
	}

	private void removeEdges() {
		for (int i=0; i<graph.size()-1; i++) {
			SpotNode aNode = graph.get(i);
			aNode.deleteEdgesWithMinSmallerThan(this.getThresholdValue());
		}
		
	}

	private int getThresholdValue() {
		return ((Integer)this.threshold.getValue()).intValue();
	}

	public void drawGraph(ImagePlus stack) {
		if (graph==null) return;
		Iterator<SpotNode> it = graph.iterator();
		while(it.hasNext()) {
			SpotNode node = it.next();
			node.drawWithEdgesOn(stack);
		}
	}

	ImagePlus newStack() {
		ImagePlus stack = NewImage.createImage(blueImage.getTitle() + " graph", 
							 blueImage.getWidth(), 
							 blueImage.getHeight(), 
							 blueImage.getNSlices(), 
							 8, 
							 NewImage.FILL_BLACK);
		return stack;
	}

	public SpinnerModel getThreshold() {
		if (threshold==null) 
			threshold = new SpinnerNumberModel(0,0,65535,1);
		return threshold;
	}

	public SpinnerModel getMaxDistance() {
		if (maxDistance==null)
			maxDistance = new SpinnerNumberModel(15,0,Integer.MAX_VALUE,1);
		return maxDistance;
	}
	
	public double[][] getMatrix() {
		double[][] matrix = new double[graph.size()][graph.size()];
		for (int i=0; i<graph.size(); i++) {
			SpotNode node = graph.get(i);
			ArrayList<SpotEdge> edges = node.getEdges();
			for (int j=0; j<edges.size();j++) {
				SpotEdge edge = edges.get(j);
				int startID = edge.startNode.getId();
				int endID = edge.endNode.getId();
				matrix[startID][endID] = edge.cost();
			}
		}
		return matrix;
	}
	
	public double[][] runMarkovClustering(double[][] matrix) {
		SparseMatrix aa = new SparseMatrix(matrix);
        aa = aa.transpose();
        double maxResidual = 0.001;
        double gammaExp = 2.0;
        double loopGain = 0.;
        double zeroMax = 0.001;
        MarkovClustering mcl = new MarkovClustering();
        aa = mcl.run(aa, maxResidual, gammaExp, loopGain, zeroMax);
        return aa.getDense();
	}
}
