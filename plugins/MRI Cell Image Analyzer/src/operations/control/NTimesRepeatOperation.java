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
 * Marks the start of a n-times repeat loop. The operations between the start and the end
 * of the loop are repeated n times.
 *  
 * @author Volker Baecker
 */
public class NTimesRepeatOperation extends Operation {

	private static final long serialVersionUID = 1L;
	private Option n;
	protected Boolean atEnd = new Boolean(false);
	protected int counter;
	
	public int getN() {
		return n.getIntegerValue();
	}
	
	public void setN(int n) {
		this.n.setValue(Integer.toString(n));
	}

	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[0];
		parameterNames = new String[0];
		optionsNames = new String[1];
		optionsNames[0] = "number of repetitions";
		resultTypes = new Class[1];
		resultTypes[0] = Class.forName("operations.control.NTimesRepeatOperation");
		resultNames = new String[1];
		resultNames[0] = "NTimesRepeatOperation";
	}

	public NTimesRepeatOperation getNTimesRepeatOperation() {
		return this;
	}
	
	public void setNTimesRepeatOperation(NTimesRepeatOperation op) {
		// noop
	}
	
	public Boolean getAtEnd() {
		return atEnd;
	}

	public void setAtEnd(Boolean atEnd) {
		this.atEnd = atEnd;
	}
	
	public void doIt() {
		counter += 1;
		if (counter==this.getN()) {
			this.setAtEnd(new Boolean(true));
			counter = 0;
		}
	}
	
	protected void setupOptions() {
		super.setupOptions();
		this.setN(2);
		n.setMin(1);
		n.setShortHelpText("the number of times to repeat the body of the loop");
	}
	
	public void connectOptions() {
		this.n = (Option) this.options.getOptions().get(0);
	}

	public boolean isLoop() {
		return true;
	}
}
