package splitAndMerge;

public class Link {

	private Shape start;
	private Shape end;
	private int neighbors;
	
	public static int TOPLEFT = 1;
	public static int TOP = 2;
	public static int TOPRIGHT = 3;
	public static int RIGHT = 4;
	public static int BOTTOMRIGHT = 5;
	public static int BOTTOM = 6;
	public static int BOTTOMLEFT = 7;
	public static int LEFT = 8;
	
	/**
	 * Create a new link between the two specified shapes
	 * @param begin / first shape
	 * @param finish / second shape
	 */
	public Link(Shape begin, Shape finish)
	{
		start = begin;
		end = finish;
	}
	
	public Link()
	{
	}

	public Shape getEnd() {
		return end;
	}

	public void setEnd(Shape end) {
		this.end = end;
	}

	public Shape getStart() {
		return start;
	}

	public void setStart(Shape start) {
		this.start = start;
	}

	public int getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(int direction) {
		this.neighbors = direction;
	}
	
	public boolean equals(Object otherLink)
	{
		boolean result=false;

		if(this.getClass()!=otherLink.getClass()) return false;
		Link link = (Link) otherLink;
		if((this.start == link.start) && (this.end == link.end)) return true;
		return result;
	}
	
	public int hashCode()
	{
		int i;
		i = 10*start.hashCode() + end.hashCode();
		return i;
	}
	
}
