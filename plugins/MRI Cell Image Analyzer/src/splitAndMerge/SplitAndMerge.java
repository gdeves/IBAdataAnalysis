package splitAndMerge;

import java.util.ArrayList;
import java.util.HashSet;
import regionGrowing.ImageTypeDoesntExist;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.NewImage;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import imagejProxies.MRIInterpreter;

public abstract class SplitAndMerge {
	
	protected int width;
	protected int height;
	protected Object data;
	protected Object gradient = null;
	protected ImagePlus image;
	protected ArrayList<Region> regionList;
	protected ArrayList<ShapeRoi> shapeList;
	protected ArrayList<Region> leafList;
	protected int threshold;
	protected int split=0;
	protected boolean conditionVerif;
	protected ImagePlus mask;
	protected Graph graph;
	private ImagePlus mask2;
	protected ArrayList<MergeCondition> conditionMergeList;
	
	public SplitAndMerge(ImagePlus img)
	{
		width = img.getWidth();
		height = img.getHeight();
		data = img.getProcessor().getPixels();
		image = img;
		threshold = 25;
		leafList = new ArrayList<Region>();
		shapeList = new ArrayList<ShapeRoi>();
		graph = new Graph();
	}
	
	abstract public float getPixel(int x, int y);
	abstract public float getGradient(int x, int y);
	
	/**
	 * 
	 * @param img (ImagePlus)
	 * @return splitAndMergeByte or SplitAndMergeShort or SplitAndMergeFloat or SplitAndMergeRGB
	 * @throws ImageTypeDoesntExist
	 */
	public static SplitAndMerge newFor(ImagePlus img) throws ImageTypeDoesntExist
	{
		SplitAndMerge result = null;
		if(img.getType()==ImagePlus.GRAY8 || img.getType()==ImagePlus.COLOR_256) result = new SplitAndMergeByte(img);
		if(img.getType()==ImagePlus.GRAY16)	result = new SplitAndMergeShort(img);
		if(img.getType()==ImagePlus.GRAY32) result = new SplitAndMergeFloat(img);
		if(img.getType()==ImagePlus.COLOR_RGB) result = new SplitAndMergeRGB(img);
		if (result == null) throw(new ImageTypeDoesntExist());
		return result;
	}
	
	/**
	 * First function to start the split
	 * @param localThreshold
	 * @return
	 */
	public ArrayList<Region> startSplit(int localThreshold)
	{
		threshold = localThreshold;
		regionList = new ArrayList<Region>();
		Region firstRegion = new Region(0, 0, 0, null, width, height);
		this.split(firstRegion);
		return regionList;
	}
	
	/**
	 * Split method. Call startSplit(int localThreshold) to split
	 * @param region 
	 * @return 
	 */
	public Region split(Region region)
	{
		conditionVerif = false;
		
		float localWidth = (float)region.getWidth();
		float localHeight = (float)region.getHeight();
		
		int numberOfPixels = (int)region.getWidth()*(int)region.getHeight();
		double[] intensity = this.getSumOfIntensity(region); 
		double variance = this.calculateStdDev(numberOfPixels, intensity[0], intensity[1]);

		SplitCondition cond = SplitCondition.newFor(image, SplitCondition.VARIANCE);
		Object object1 = new Integer(threshold);
		conditionVerif = cond.conditionVerification(object1,variance);
		if(conditionVerif && (localWidth>1 || localHeight>1))
		{
			regionList.add(region);
			int width1 = (int)region.getWidth()/2;
			int width2 = (int) (region.getWidth()/2)+1;
			int height1 = (int) region.getHeight()/2;
			int height2 = (int) (region.getHeight()/2)+1;
			
			if(region.getWidth()>1 && region.getHeight()>1) {
			
				region.setRegion1(split(new Region(region.getXAsInt(), region.getYAsInt(), (region.getLevel())+1, region, width1, height1)));
				
				if(region.getWidth()%2==0)
					region.setRegion2(split(new Region(region.getXAsInt() + (int)localWidth/2, region.getYAsInt(), (region.getLevel())+1, region, width1, height1)));
				else
					region.setRegion2(split(new Region(region.getXAsInt() + (int)localWidth/2, region.getYAsInt(), (region.getLevel())+1, region, width2, height1)));
				
				if(region.getHeight()%2==0)
					region.setRegion3(split(new Region(region.getXAsInt(), region.getYAsInt() + (int)localHeight/2, (region.getLevel())+1, region, width1, height1)));
				else
					region.setRegion3(split(new Region(region.getXAsInt(), region.getYAsInt() + (int)localHeight/2, (region.getLevel())+1, region, width1, height2)));
				
				if(region.getWidth()%2==0 && region.getHeight()%2==0)
					region.setRegion4(split(new Region(region.getXAsInt() + (int)localWidth/2, region.getYAsInt() + (int)localHeight/2, (region.getLevel())+1, region, width1, height1)));
				else if(region.getWidth()%2==1 && region.getHeight()%2==0)
					region.setRegion4(split(new Region(region.getXAsInt() + (int)localWidth/2, region.getYAsInt() + (int)localHeight/2, (region.getLevel())+1, region, width2, height1)));
				else if(region.getWidth()%2==0 && region.getHeight()%2==1)
					region.setRegion4(split(new Region(region.getXAsInt() + (int)localWidth/2, region.getYAsInt() + (int)localHeight/2, (region.getLevel())+1, region, width1, height2)));
				else
					region.setRegion4(split(new Region(region.getXAsInt() + (int)localWidth/2, region.getYAsInt() + (int)localHeight/2, (region.getLevel())+1, region, width2, height2)));
			}
			else if(region.getWidth()<=1 && region.getHeight()>1) {
				if(region.getHeight()%2==0) {
					region.setRegion1(split(new Region(region.getXAsInt(), region.getYAsInt(), (region.getLevel())+1, region, width2, height1)));
					region.setRegion3(split(new Region(region.getXAsInt(), (region.getYAsInt())+1, (region.getLevel())+1, region, width2, height1)));
				}
				else{
					region.setRegion1(split(new Region(region.getXAsInt(), region.getYAsInt(), (region.getLevel())+1, region, width2, height1)));
					region.setRegion3(split(new Region(region.getXAsInt(), (region.getYAsInt())+1, (region.getLevel())+1, region, width2, height2)));
				}
			}
			else if(region.getWidth()>1 && region.getHeight()<=1) {
				if(region.getWidth()%2==0) {
					region.setRegion1(split(new Region(region.getXAsInt(), region.getYAsInt(), (region.getLevel())+1, region, width1, height2)));
					region.setRegion2(split(new Region((region.getXAsInt())+1, region.getYAsInt(), (region.getLevel())+1, region, width1, height2)));
				}
				else{
					region.setRegion1(split(new Region(region.getXAsInt(), region.getYAsInt(), (region.getLevel())+1, region, width1, height2)));
					region.setRegion2(split(new Region((region.getXAsInt())+1, region.getYAsInt(), (region.getLevel())+1, region, width2, height2)));
				}
			}
		}
		else
		{
			addShapeList(region);
		}
		return(region);
	}
	
	private double[] getSumOfIntensity(Region region) {
		int width = (int) region.getWidth();
		int height = (int) region.getHeight();
		int x = region.getXAsInt();
		int y = region.getYAsInt();
		double[] totalIntensity = new double[2];
		for(int i=x;i<(x+width);i++) {
			for(int j=y;j<(y+height);j++) {
				float pixel = this.getPixel(i, j);
				totalIntensity[0] = totalIntensity[0] + pixel;
				totalIntensity[1] = totalIntensity[1] + (float)Math.pow(pixel,2);
			}
		}
		return totalIntensity;
	}

	private void addShapeList(Region region) {
		leafList.add(region);
		Shape lastShape = new Shape(new Roi(region), image, this);
		shapeList.add(lastShape.or(lastShape));		
		for(MergeCondition condition : this.conditionMergeList) {
			if(condition.getClass().getName().equalsIgnoreCase("splitAndMerge.IntensityMergeCondition")) {
				lastShape.calculateIntensity();
			}
			else if(condition.getClass().getName().equalsIgnoreCase("splitAndMerge.GradientMergeCondition")) {
				if(gradient==null) this.calculateGradient();
				lastShape.calculateMeanGradient();
			}
			else if(condition.getClass().getName().equalsIgnoreCase("splitAndMerge.EntropyMergeCondition")) {
				lastShape.calculateHistrogram();
				lastShape.calculateEntropy();
			}
			else if(condition.getClass().getName().equalsIgnoreCase("splitAndMerge.SignalToNoiseRatioMergeCondition")) {
				lastShape.calculateHistrogram();
				lastShape.calculateSignalToNoiseRatio();
			}
		}
		
		Node node = new Node(new NodeShape(lastShape));
		graph.addNode(node);
	}

	/**
	 * Construct the adjacency graph with links
	 * @param first chape
	 */
	public Graph makeAdjacencyGraph() {
		for(int i=0;i<shapeList.size();i++) {
			Region leaf = (Region)leafList.get(i);
			leaf.setVisited(true);
			Node node = (Node) graph.getNodes().get(i);
			for(int j=0;j<shapeList.size();j++) {
				if((((Region)leafList.get(j)).getVisited()) || (leafList.get(i) == leafList.get(j))) continue;
				Node neighbor = (Node) graph.getNodes().get(j);
				Region leafToCompare = (Region)leafList.get(j);
				if(leaf.touches8Connected(leafToCompare)) {
					node.addNeighbor(neighbor);
					neighbor.addNeighbor(node);
				}
			}
		}
		return graph;
	}

	public ArrayList<ShapeRoi> getShapeList() {
		return shapeList;
	}

	/**
	 * draw all nodes contains in the nodes list
	 */
	public ImagePlus drawShapes() {
		MRIInterpreter interpreter = new MRIInterpreter();
		interpreter.setBatchMode(true);
		mask2 = NewImage.createByteImage("mask2", width, height, 1, NewImage.FILL_WHITE);
		int color = 0;
		for(int m=0;m<graph.getNodes().size();m++) {
			Node node = (Node) graph.getNodes().get(m);
			if(node.getRemove()) continue;
			Shape shape = (Shape) node.getData().getValue();
			WindowManager.setTempCurrentImage(mask2);
			mask2.setRoi(shape);
			IJ.setForegroundColor(color, color, color);
			IJ.run("Fill");
			mask2.updateAndDraw();
			WindowManager.setTempCurrentImage(null);
			color = color + 20;
			if(color>240) color=0;
		}
		interpreter.setBatchMode(false);
		return mask2;
	}

	/**
	 * get the standard deviation of a region
	 * @param n // number of pixels
	 * @param sum // sum of intensity (getSumOfIntensity)
	 * @param sum2 // (sum of intensity)� (getSumOfIntensity)
	 * @return standard deviation
	 */
	public double calculateStdDev(int n, double sum, double sum2) {
		double stdDev;
		if(n>0) {
			stdDev = (n*sum2-sum*sum)/n;
			if (stdDev>0.0) stdDev = Math.sqrt(stdDev/(n-1.0));
			else stdDev = 0.0;
		}
		else stdDev = 0.0;
		
		return stdDev;
	}
	
	/**
	 * get the gradient of a region
	 * @return gradient
	 */
	public void calculateGradient() {
		MRIInterpreter interpreter = new MRIInterpreter();
		interpreter.setBatchMode(true);
		IJ.run("Duplicate...", "title=gradient.tif");
		WindowManager.setTempCurrentImage(WindowManager.getImage("gradient.tif"));
		IJ.run("Find Edges");
		gradient = WindowManager.getImage("gradient.tif").getProcessor().getPixels();
		WindowManager.getImage("gradient.tif").changes = false;
		WindowManager.getImage("gradient.tif").close();
		WindowManager.setTempCurrentImage(null);
		interpreter.setBatchMode(false);
	}

	/**
	 * get small shape
	 * @param thresholdSmallShapen
	 * @return Node
	 */
	public Node getSmallShape(int thresholdSmallShape) {
		ArrayList<Node> nodes = graph.getNodes();
		for(Node node : nodes) {
			if(node.getRemove() || node==null) continue;
			Shape shape = (Shape) ((NodeData)node.getData()).getValue();
			if(shape.getArea()<thresholdSmallShape) {
				return node;		
			}
		}
		return null;
	}
	
	public Node getNeighborToMergeWith(Node node) {
		double maxArea = 0;
		Node nodeMax = null;
		HashSet<Node> neighbors = node.getNeighbors();
		for(Node aNode : neighbors) {
			if(node.getRemove()) continue;
			Shape shape = (Shape) ((NodeData)aNode.getData()).getValue();
			if(shape.getArea()>maxArea) {
				nodeMax = aNode;
				maxArea = shape.getArea();
			}
		}
		return nodeMax;
	}
	
	public void mergeSmallShape(int thresholdSmallShape) {
		boolean merged = true;
		while(merged) {
			Node node = getSmallShape(thresholdSmallShape);
			if(node == null) {
				merged = false;
				continue;
			}
			Node aNode = getNeighborToMergeWith(node);
			if(aNode==null) continue;
			aNode.mergeWith(node);
			node.setRemove(true);
		}
	}
	
	public void mergeIsolatedShape(int thresholdBackground) {
		boolean merged = true;
		while(merged) {
			ArrayList<Node> shapes = getIsolatedShape(thresholdBackground);
			if(shapes==null) {
				merged = false;
				continue;
			}
			Node node = shapes.get(0);
			Node aNode = shapes.get(1);
			aNode.mergeWith(node);
			node.setRemove(true);
		}
	}
	
	public ArrayList<Node> getIsolatedShape(int thresholdBackground) {
		ArrayList<Node> result = new ArrayList<Node>();
		ArrayList<Node> nodes = graph.getNodes();
		for(Node node : nodes) {
			if(((Shape)node.getData().getValue()).getIntensity()<thresholdBackground || node.getRemove()) continue;
			if(node.getNeighbors().size()==1) {
				HashSet<Node> neighbors = node.getNeighbors();
				for (Node aNode : neighbors) {
					if(((Shape)aNode.getData().getValue()).getIntensity()<thresholdBackground) continue;
					result.add(node);
					result.add(aNode);
					return result;				
				}
			}
		}
		return null;
	}
	
	public void mergeToImproveCircularity(int thresholdBackground) {
		ArrayList<Node> nodes = graph.getNodes();
		for(Node node: nodes) {
			if(node.getRemove() || ((Shape)node.getData().getValue()).getIntensity()<thresholdBackground) continue;
			Node aNode = getNodeToMergeToImproveCircularity(node,thresholdBackground);
			if(aNode==null) continue;
			node.mergeWith(aNode);
			aNode.setRemove(true);
		}
	}
	
	public Node getNodeToMergeToImproveCircularity(Node node, int thresholdBackground) {
		Shape shape = (Shape) node.getData().getValue();
		WindowManager.setTempCurrentImage(mask2);
		mask2.setRoi(shape);
		double perimeter = mask2.getRoi().getLength();
		double circularityBefore = perimeter==0.0?0.0:4.0*Math.PI*(shape.getArea()/(perimeter*perimeter));
		if (circularityBefore>1.0) circularityBefore = 1.0;
		HashSet<Node> neighbors = node.getNeighbors();
		for(Node aNode : neighbors) {
			if(aNode.getRemove() || ((Shape)aNode.getData().getValue()).getIntensity()<thresholdBackground) continue;
			Shape aShape = (Shape) aNode.getData().getValue();
			ShapeRoi merge = new ShapeRoi(shape);
			merge.or(aShape);
			mask2.setRoi(merge);
			double perimeterAfter = mask2.getRoi().getLength();
			double circularityAfter = perimeterAfter==0.0?0.0:4.0*Math.PI*((shape.getArea()+aShape.getArea())/(perimeterAfter*perimeterAfter));
			if (circularityAfter>1.0) circularityAfter = 1.0;
			if(circularityBefore<circularityAfter) {
				return aNode;
			}
		}
		return null;
	}

	public void cleanList() {
		ArrayList<Node> copy = new ArrayList<Node>(graph.getNodes());
		for(Node node : copy) {
			if(node.getRemove()) graph.getNodes().remove(node);
		}
	}

public void setConditionMergeList(ArrayList<MergeCondition> conditionMergeList) {
		this.conditionMergeList = new ArrayList<MergeCondition>(conditionMergeList);
	}
}
