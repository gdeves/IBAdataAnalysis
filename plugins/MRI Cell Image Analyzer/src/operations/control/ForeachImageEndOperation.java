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
package operations.control;

import operations.Operation;

/**
 * The operation marks the end of a loop.
 * In each cycle the result of "foreach image do" is the current filename. When "foreach image end" is 
 * reached the current filename is set to the next in the list and the execution continues after "foreach image do". 
 * When the last filename in the list is reached execution continues after "foreach image end".
 * 
 * @author Volker
 */
public class ForeachImageEndOperation extends Operation {
	private static final long serialVersionUID = 7879804150200312407L;
	ForeachImageDoOperation doOperation;

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[1];
		parameterTypes[0] = Class.forName("operations.control.ForeachImageDoOperation");
		parameterNames = new String[1];
		parameterNames[0] = "DoOperation";
		resultTypes = new Class[0];
		resultNames = new String[0];
	}
	
	public void doIt() {
		if (this.application==null) return;
		if (!this.getDoOperation().getAtEnd().booleanValue()) {
			this.getApplication().backToOperationNumber(this.getApplication().getOperations().indexOf(doOperation)-1);
			this.getDoOperation().next();
		} else {
			this.getDoOperation().reset();
		}
		
	}

	public ForeachImageDoOperation getDoOperation() {
		return doOperation;
	}

	public void setDoOperation(ForeachImageDoOperation doOperation) {
		this.doOperation = doOperation;
	}
	
	public boolean isLoopEnd() {
		return true;
	}
}
