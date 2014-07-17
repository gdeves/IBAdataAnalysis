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
package gui;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Vector;

import operations.Operation;
import applications.Application;

/**
 * The parameter and results of an operation. Parameters have a name and a type.
 * An input parameter of one operation can be connected to a result of another
 * operation with the same type.
 *
 * @author	Volker Baecker
 **/
public class Parameter extends Observable {
	protected Application application;
	protected Operation operation;
	protected int operationIndex;
	protected String[] parameterNames;
	protected Class<?>[] parameterTypes;
	protected ArrayList<ArrayList<int[]>> possibleInputMaps;
	protected ParameterView view;
	
	
	public Parameter(Operation op, Application app) {
		this.operation = op;
		this.application = app;
		this.initialize();
	}

	private void initialize() {
		possibleInputMaps = new ArrayList<ArrayList<int[]>>();
		operationIndex = application.getOperations().indexOf(operation);
		this.parameterNames = operation.getHumanReadableParameterNames();
		this.parameterTypes = operation.getParameterTypes();
		for (int i=0; i<parameterNames.length; i++) {
			this.setupPossibleInputMapFor(i);
		}
	}

	private void setupPossibleInputMapFor(int parameterIndex) {
		ArrayList<int[]> maps = new ArrayList<int[]>();
		Class<?> parameterType = parameterTypes[parameterIndex];
		for(int i=0; i<operationIndex; i++) {
			Operation inputOperation = (Operation) application.getOperations().get(i);
			Class<?>[] resultTypes = inputOperation.getResultTypes();
			for (int j=0; j<resultTypes.length; j++) {
				Class<?> resultType = resultTypes[j];
				if (resultType==parameterType) {
					int[] map = new int[2];
					map[0] = i;
					map[1] = j;
					maps.add(map);
				}
			}
		}
		possibleInputMaps.add(maps);
	}
	
	public String[] getParameterNames() {
		return parameterNames;
	}
	
	public String[] getParameterTypes() {
		String[] typeNames = new String[parameterTypes.length]; 
		for (int i=0; i<parameterTypes.length; i++) {
			typeNames[i] = parameterTypes[i].getSimpleName();
		}
		return typeNames;
	}
	
	public Vector<String> getInputOperationsForParameter(int i) {
		Vector<String> result = new Vector<String>();
		ArrayList<int[]> maps = possibleInputMaps.get(i);
		int index = 0;
		for (int[] map : maps) {
			String name = ((Operation)application.getOperations().get(map[0])).name();
			if ((index>0) && (map[0]==((int[])(maps.get(index-1)))[0])) continue;
			result.add(name);
			index++;
		}
		return result;
	}
	
	public Vector<String> getResultNamesFor(int parameter, int inputOperation) {
		Vector<String> result = new Vector<String>();
		if (inputOperation<0) return result;
		ArrayList<int[]> maps = possibleInputMaps.get(parameter);
		int startIndex = this.getStartIndexFor(inputOperation, maps);
		int endIndex = this.getEndIndexFor(inputOperation, maps, startIndex); 
		for(int i=startIndex; i<=endIndex; i++) {
			int[] map = (int[])maps.get(i);	
			Operation inputOp = ((Operation)application.getOperations().get(map[0]));
			String[] resultNames = inputOp.getResultNames();
			result.add(Operation.humanReadableName(resultNames[map[1]]));	
		}
		return result;
	}

	/**
	 * @param inputOperation
	 * @param maps
	 * @return
	 */
	private int getEndIndexFor(int inputOperation, ArrayList<int[]> maps, int startIndex) {
		if (startIndex==maps.size()-1) {
			return startIndex;
		}
		for (int i=startIndex+1; i<maps.size(); i++) {
			int[] map = maps.get(i);
			if (map[0]!=(maps.get(i-1))[0]) return i-1;
		}
		return maps.size()-1;
	}

	/**
	 * @param inputOperation
	 * @param maps
	 */
	private int getStartIndexFor(int inputOperation, ArrayList<int[]> maps) {
		if (inputOperation==0) {
			return 0;
		}
		int counter = 0;
		for (int i=1; i<maps.size(); i++) {
			int[] map = maps.get(i);
			if (map[0]!=(maps.get(i-1))[0]) counter++;
			if (counter==inputOperation) {
				counter = i;
				break;
			}
		}
		return counter;
	}

	/**
	 * 
	 */
	public void show() {
		if (view == null) {
			view = new ParameterView(this);
		}
		view.setVisible(true);
	}
	/**
	 * @return Returns the application.
	 */
	public Application getApplication() {
		return application;
	}
	/**
	 * @return Returns the operation.
	 */
	public Operation getOperation() {
		return operation;
	}
	
	public String getMappingTextForParameters() {
		String result = "";
		for (int i=0; i<this.parameterNames.length; i++) {
			result = result + this.getMappingTextForParameter(i);
			if (i<parameterNames.length-1) {
				result = result + "\n";
			}
		}
		return result;
	}
	public String getMappingTextForParameter(int i) {
		int[] map = (int[]) this.operation.getInputMap().get(i);
		String result = "(" + parameterTypes[i].getSimpleName() + ") " + parameterNames[i] + " := ";
		if (map[0]==-1 || map[1]==-1) {
			result = result + " null";
		} else {
			Operation inputOperation = (Operation) application.getOperations().get(map[0]);
			String resultName = inputOperation.getResultNames()[map[1]];
			result = result + inputOperation.name() + " : " + resultName;
		}
		return result;
	}

	/**
	 * @param parameterIndex
	 * @param operationIndex2
	 * @param resultIndex
	 */
	public void mapOperationInput(int parameterIndex, int sourceOperationIndex, int resultIndex) {
		ArrayList<int[]> map = possibleInputMaps.get(parameterIndex);
		int[] mapping = findMappingFor(map, sourceOperationIndex, resultIndex);
		this.operation.setInputForParameterTo(parameterIndex, mapping[0], mapping[1]);
	}

	/**
	 * @param map
	 * @param sourceOperationIndex
	 * @param resultIndex
	 * @return
	 */
	private int[] findMappingFor(ArrayList<int[]> map, int sourceOperationIndex, int resultIndex) {
		int startIndex = this.getStartIndexFor(sourceOperationIndex, map);
		return ((int[])map.get(startIndex+resultIndex));
	}

	
}
