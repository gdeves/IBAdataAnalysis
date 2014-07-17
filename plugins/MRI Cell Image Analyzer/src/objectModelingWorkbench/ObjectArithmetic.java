/* This file is part of MRI Cell Image Analyzer.
 * (c) 2005-2007 Volker Bäcker
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

import java.util.Observable;
import java.util.Vector;

/**
 * Add two Object3D objects or subtract one from the other. The operation is performed
 * on the selections that make up the objects slice by slice.
 * 
 * @author Volker Bäcker
 */
public class ObjectArithmetic extends Observable {
	protected Object3D object1;
	protected Object3D object2;
	protected String operand;
	protected int[] slices;
	protected Object3DManager model;
	
	public void show() {
		(new ObjectArithmeticView(this)).setVisible(true);
	}
	
	public Object3DManager getModel() {
		return model;
	}
	
	public void setModel(Object3DManager model) {
		this.model = model;
	}
	
	public Object3D getObject1() {
		return object1;
	}
	
	public void setObject1(Object3D object1) {
		this.object1 = object1;
	}
	
	public Object3D getObject2() {
		return object2;
	}
	
	public void setObject2(Object3D object2) {
		this.object2 = object2;
	}
	
	public String getOperand() {
		return operand;
	}
	
	public void setOperand(String operand) {
		this.operand = operand;
	}
	
	public int[] getSlices() {
		slices = model.model.view.getSelectionIndices();
		return slices;
	}
	
	public void setSlices(int[] slices) {
		this.slices = slices;
	}
	
	public String[] getOperators() {
		String[] operators = {"add (a or b)", "subtract (a and not(b))"};
		return operators;
	}
	
	public Vector<String> getOperands() {
		return new Vector<String>(model.getObjectNames());
	}
	
	public void applyOperation(int operand1Index, String operator, int operand2Index) {
		Object3D object1 = (Object3D) model.getObjects().get(operand1Index);
		Object3D object2 = (Object3D) model.getObjects().get(operand2Index);
		if (operator.contains("subtract")) {
			object1.subtract(object2, this.getSlices());
		}
		if (operator.contains("add")) {
			object1.add(object2, this.getSlices());
		}
		model.changed("selections");
		model.model.changed("showObjects");
		model.model.changed("display");
	}
}
