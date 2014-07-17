/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2008 Volker Bäcker
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * www.mri.cnrs.fr
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package tools.grid;

import java.util.Comparator;
/**
 * Compare strings of the form ixj that represent positions on a grid.
 * 
 * @author	Volker Baecker
 **/
public class GridPositionComparator implements Comparator<String> {

	public int compare(String first, String second) {
		String[] elementsFirst = first.split("x");
		float iFirst = Float.parseFloat(elementsFirst[0]);
		float jFirst = Float.parseFloat(elementsFirst[1]);
		String[] elementsSecond = second.split("x");
		float iSecond = Float.parseFloat(elementsSecond[0]);
		float jSecond = Float.parseFloat(elementsSecond[1]);
		
		if (jFirst<jSecond) return -1;
		if (jFirst==jSecond && iFirst==iSecond) return 0;
		if (jFirst==jSecond) {
			if (iFirst<iSecond) return -1;
			else return 1;
		}	
		return 1;
	}

}
