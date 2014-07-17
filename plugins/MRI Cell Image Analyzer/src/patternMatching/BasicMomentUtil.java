package patternMatching;

import java.awt.Polygon;
import java.awt.Rectangle;

public class BasicMomentUtil {
	
	static public float moment(int m, int n, Polygon form) {
		float result = 0;
		Rectangle bounds = form.getBounds();
		for (int y=bounds.y; y<(bounds.y+bounds.height); y++) {
		      for (int x=bounds.x; x<(bounds.x+bounds.width); x++) {
		    	  if(form.contains(x,y)) result+=  1 * Math.pow(x,m) * Math.pow(y,n);		     
		    }
		}
		return result;
	}
}
