/*
 * Created on 10.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package operations.processing;

import ij.plugin.filter.RankFilters;

/**
 * @author Volker
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class VarianceFilterOperation extends RankFilterOperation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3679140220768099592L;
	
	public int filterType() {
		return RankFilters.VARIANCE;
	}
}
