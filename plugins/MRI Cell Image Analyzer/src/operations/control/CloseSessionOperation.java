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

import gui.Options;
import gui.options.BooleanOption;
import gui.options.Option;
import os.OperatingSystemProxy;
import operations.Operation;

/**
 * Closes the session of the user. On window systems the name of a command or script 
 * that closes the session can  be configured in mri_cia_config.txt under the key 
 * "logoff command". If the key doesn't exist the command shutdown -l -f is used. On
 * unix the command /bin/bash logoff is used. On Mac a script with the name logout.scpt
 * is called.
 * 
 * @author Volker Baecker
 */
public class CloseSessionOperation extends Operation {
	private static final long serialVersionUID = -7817169092048923999L;
	protected Option deactivate;
	
	public void doIt() {
		if (this.getDeactivate()) {
			return;
		}
		OperatingSystemProxy.current().logout();
	}
	
	protected void initialize() throws ClassNotFoundException {
		super.initialize();
		parameterTypes = new Class[0];
		parameterNames = new String[0];
		optionsNames = new String[1];
		optionsNames[0] = "deactivate";
		resultTypes = new Class[0];
		resultNames = new String[0];
	}

	public boolean getDeactivate() {
		return deactivate.getBooleanValue();
	}

	public void setDeactivate(boolean deactivate) {
		this.deactivate.setValue(Boolean.toString(deactivate));
	}
	
	protected void setupOptions() {
		options = new Options();
		options.setName(this.name() + " options");
		String[] optionsNames = this.getOptionsNames();
		this.deactivate = new BooleanOption();
		deactivate.setName(optionsNames[0]);
		deactivate.setShortHelpText("Check to skip the operation.");
		options.add(this.deactivate);
	}
	
	public void connectOptions() {
		this.deactivate = (BooleanOption) this.options.getOptions().get(0);
	}
}
