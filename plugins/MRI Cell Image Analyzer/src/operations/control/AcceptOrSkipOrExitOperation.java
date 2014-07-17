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

import gui.options.Option;
import operations.Operation;

/**
 * Pause the application until the user presses one of the buttons accept, skip or
 * exit. If accept is pressed the application continues with the next operation. If
 * skip is pressed the application skips a configurable number of operations. If
 * exit is pressed the application is stopped.
 * 
 * @author Volker Baecker
 */
public class AcceptOrSkipOrExitOperation extends Operation {
	private static final long serialVersionUID = 7935725580910923066L;
	protected Option numberOfOperations;
	protected boolean wait = true;
	protected boolean doAccept;
	protected boolean doSkip;
	protected boolean doExit;
	protected AcceptOrSkipOrExitOperationView acceptOrskipOrExitView;

	public boolean isDoExit() {
		return doExit;
	}

	public void setDoExit(boolean doExit) {
		this.doExit = doExit;
		if (doExit) {
			doSkip = false;
			doAccept = false;
		}
	}

	public boolean isDoSkip() {
		return doSkip;
	}

	public void setDoSkip(boolean doSkip) {
		this.doSkip = doSkip;
		if (doSkip) {
			doExit = false;
			doAccept = false;
		}
	}

	public boolean isDoAccept() {
		return doAccept;
	}
	
	public void setDoAccept(boolean doWait) {
		this.doAccept = doWait;
		if (doAccept) {
			doExit = false;
			doSkip = false;
		}
	}
	
	public AcceptOrSkipOrExitOperation() {
		super();
	}
	
	protected void initialize() throws ClassNotFoundException {
		this.acceptOrskipOrExitView = new AcceptOrSkipOrExitOperationView(this);
		parameterTypes = new Class[0];
		parameterNames = new String[0];
		optionsNames = new String[1];
		optionsNames[0] = "number of operations";
		resultTypes = new Class[0];
		resultNames = new String[0];
	}
	
	public void doIt() {
		this.wait = true; 
		this.acceptOrskipOrExitView.setVisible(true);
		while(wait) {
			try {
				Thread.yield();
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.acceptOrskipOrExitView.setVisible(false);
		if (this.isDoSkip()) {
			application.setProgramCounter(application.getProgramCounter()+this.getNumberOfOperations());
		}
		if (this.isDoExit()) {
			this.stopApplication();
		}
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setNumberOfOperations(1);
		numberOfOperations.setShortHelpText("enter the number of operations to skip");
	}
	
	public void connectOptions() {
		this.numberOfOperations = (Option) this.options.getOptions().get(0);
	}
	
	public int getNumberOfOperations() {
		return numberOfOperations.getIntegerValue();
	}

	public void setNumberOfOperations(int numberOfOperations) {
		this.numberOfOperations.setValue(Integer.toString(numberOfOperations));
	}
	
	protected boolean isBatchOperation() {
		return false;
	}
}
