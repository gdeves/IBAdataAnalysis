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

import ij.gui.Grid;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;

/**
 * Provide a static method to run the grid tool from a toolset macro. 
 * 
 * @author	Volker Baecker
 **/
public class RunGrid {
	public static void run() {
		Grid.openNew();
	}
	
	public static void applyGrid() {
		Grid current = Grid.getCurrent();
		ResultsTable table = Analyzer.getResultsTable();
		ResultsTable newTable = current.applyGridTo(table);
		newTable.show(current.getResultsTableTitle());
		if (!current.isShowOutlines()) return;
		current.getOutlinesImage().show();
	}
}
