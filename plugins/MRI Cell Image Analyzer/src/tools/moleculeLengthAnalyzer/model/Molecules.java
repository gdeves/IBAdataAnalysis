/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2009 INSERM 
 * MRI Cell Image Analyzer has been created at Montpellier RIO Imaging
 * (www.mri.cnrs.fr) by Volker Bäcker
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
package tools.moleculeLengthAnalyzer.model;

import ij.ImagePlus;

import java.util.ArrayList;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 * A list of molecules.
 * 
 * @author baecker
 *
 */
public class Molecules extends ArrayList<Molecule> implements ListModel {
	private static final long serialVersionUID = -5095667873503590997L;

	@Override
	public void addListDataListener(ListDataListener arg0) {
		// do nothing	
	}

	@Override
	public Object getElementAt(int index) {
		return this.get(index);
	}

	@Override
	public int getSize() {
		return this.size();
	}

	@Override
	public void removeListDataListener(ListDataListener arg0) {
		// do nothing
	}

	public void drawOn(ImagePlus image, ImageInfo info) {
		for (Molecule molecule : this) molecule.drawOn(image, info);
	}

}
