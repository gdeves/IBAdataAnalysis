package splitAndMerge;

import java.awt.Rectangle;

public class Region extends Rectangle {
	private static final long serialVersionUID = 4289772789221538120L;
	private int level;
	private float variance;
	private Region region1;
	private Region region2;
	private Region region3;
	private Region region4;
	private Region father;	
	private boolean visited;	
	
	public Region()
	{
	}
	
	public Region(int positionX, int positionY, int lvl, Region node, float regionWidth, float regionHeight)
	{
		super(positionX, positionY, (int)regionWidth, (int)regionHeight);
		region1 = null;
		region2 = null;
		region3 = null;
		region4 = null;
		level = lvl;
		father = node;
		width = (int) regionWidth;
		height = (int) regionHeight;
		visited = false;
	}
	
	public float GetVariance()
	{
		return variance;
	}

	public Region getRegion1() {
		return region1;
	}

	public Region getRegion2() {
		return region2;
	}

	public Region getRegion3() {
		return region3;
	}

	public Region getRegion4() {
		return region4;
	}

	public int getLevel() {
		return level;
	}

	public void setRegion1(Region region1) {
		this.region1 = region1;
	}

	public void setRegion2(Region region2) {
		this.region2 = region2;
	}

	public void setRegion3(Region region3) {
		this.region3 = region3;
	}

	public void setRegion4(Region region4) {
		this.region4 = region4;
	}

	public Region getFather() {
		return father;
	}
	
	public boolean touches4Connected(Region aRegion)
	{
		boolean result = false;
		Region testRegion = (Region) aRegion.clone();
		testRegion.translate(-1, 0);
		if(this.intersects(testRegion)) return true;
		testRegion.translate(2, 0);
		if(this.intersects(testRegion)) return true;
		testRegion.translate(-1, -1);
		if(this.intersects(testRegion)) return true;
		testRegion.translate(0, 2);
		if(this.intersects(testRegion)) return true;
		
		return result;
	}
	
	public boolean touches8Connected(Region aRegion)
	{
		boolean result = false;
		Region testRegion = (Region) aRegion.clone();
		
		if(this.touches4Connected(aRegion)) return true;
		
		testRegion.translate(-1, -1);
		if(this.intersects(testRegion)) return true;
		testRegion.translate(2, 2);
		if(this.intersects(testRegion)) return true;
		testRegion.translate(0, -2);
		if(this.intersects(testRegion)) return true;
		testRegion.translate(-2, 2);
		if(this.intersects(testRegion)) return true;
		
		return result;
	}
	
	public int getXAsInt()
	{
		return (int)this.x;
	}
	
	public int getYAsInt()
	{
		return (int)this.y;
	}

	public boolean getVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
}
