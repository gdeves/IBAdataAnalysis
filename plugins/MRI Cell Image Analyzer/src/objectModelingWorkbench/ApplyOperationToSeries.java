/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker B�cker
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
package objectModelingWorkbench;
import gui.CellImageAnalyzer;
import ij.ImagePlus;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Vector;

import operations.Operation;

/**
 * Apply an operation to a slice or to the whole stack, slice by slice.
 * 
 * @author Volker Baecker
 */
public class ApplyOperationToSeries extends Observable {
	protected ApplyOperationToSeriesView view;
	protected ArrayList<Operation> operations;
	protected ObjectModelingWorkbench registrator;
	protected boolean applyTranslations;
	
	public ApplyOperationToSeries(ObjectModelingWorkbench registrator) {
		super();
		this.registrator = registrator;
	}
	
	public void show() {
		this.getView().setVisible(true);
	}

	public ApplyOperationToSeriesView getView() {
		if (this.view==null) this.view = new ApplyOperationToSeriesView(this);
		return this.view;
	}

	public ArrayList<Operation> getOperations() {
		if (this.operations==null) {
			this.setupOperations();
		}
		return operations;
	}

	public Vector<String> getOperationNames() {
		Vector<String> names = new Vector<String>();
		ArrayList<Operation> operations = this.getOperations();
		Iterator<Operation> it = operations.iterator();
		while (it.hasNext()) {
			Operation currentOperation = it.next();
				names.add(currentOperation.name());
		}
		return names;
	}
	
	protected void setupOperations() {
		ArrayList<Operation> allOperations = CellImageAnalyzer.getCurrent().getOperationsFor("all").getOperations();
		operations = new ArrayList<Operation>();
		for (Operation currentOperation : allOperations) {
			Class<?>[] parameterTypes = currentOperation.getParameterTypes();
			if (currentOperation.name().equals("close image")) continue;
			if (parameterTypes.length==1 && parameterTypes[0]==ImagePlus.class) {
				operations.add(currentOperation);
			}
		}
	}

	public void setOperations(ArrayList<Operation> operations) {
		this.operations = operations;
	}

	public void applyOperationToSeries(Operation op, File targetFile) {
		registrator.applyOperationToSeries(op, targetFile, this.isApplyTranslations());
	}

	public boolean isApplyTranslations() {
		return applyTranslations;
	}

	public void setApplyTranslations(boolean b) {
		applyTranslations = b;
	}

	public void applyOperationToSlice(Operation op) {
		op.setKeepSource(false);
		op.run();
	}
}
