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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 * A list of spots.
 * 
 * @author baecker
 *
 */
public class Spots extends ArrayList<Spot> implements ListModel {
	private static final long serialVersionUID = -4653266458878639148L;
	
	public Spots() {
		super();
	}
	
	public Spots getSpotsWithMinDistance(double minDistance) {
		Spots result = new Spots();
		Set<Spot> removeList = calculateRemoveList(minDistance);
		for (Spot spot : this) {
			if (removeList.contains(spot)) continue; 
			else result.add(spot);	
		}
		return result;
	}

	private Set<Spot> calculateRemoveList(double minDistance) {
		Set<Spot> removeList = new HashSet<Spot>();
		int numberOfSpots = this.size();
		for(int i = 0; i<numberOfSpots; i++) {
			for(int j = i+1; j<numberOfSpots; j++) {
				Spot spotA =  this.get(i);
				Spot spotB =  this.get(j);
				double distance = spotA.distance(spotB);
				if (distance<minDistance) {
					removeList.add(spotA);
					removeList.add(spotB);
				}
			}
		}
		return removeList;
	}
	
	public Molecules findMolecules(Spots spots) {
		Molecules result = new Molecules();
		for(int i=0; i<this.size(); i++) {
			double minDist = Double.POSITIVE_INFINITY;
			int partnerIndex = -1;
			Spot greenSpot = this.get(i);
			for(int j=0; j<spots.size(); j++) {
				Spot redSpot = spots.get(j);
				double distance = greenSpot.distance(redSpot);
				if (distance<minDist) {
					minDist = distance;
					partnerIndex = j;
				}
			}
			if (partnerIndex==-1) continue;
			Spot partnerSpot = spots.get(partnerIndex);
			Molecule aMolecule = new Molecule(greenSpot, partnerSpot, minDist);
			result.add(aMolecule);
		}
		return result;
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
	public void addListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
		
	}
}
