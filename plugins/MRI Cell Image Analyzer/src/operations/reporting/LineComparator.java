/*
 * Created on 24.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.reporting;

import java.awt.geom.Line2D;
import java.util.Comparator;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LineComparator extends Object implements Comparator<Line2D> {
	public int compare(Line2D arg0, Line2D arg1) {
		Line2D line1 = arg0;
		Line2D line2 = arg1;
		if (line1.getX1()<line2.getX2()) {
			return -1;
		}
		if (line1.getX1()==line2.getX2()) {
			return 0;
		}
		return 1;
	}
}
