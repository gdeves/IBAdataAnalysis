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
import operations.control.WaitForUserOperation;
import ij.plugin.PlugIn;

/**
 * Run this plugin from a macro (run("Wait For User","");) to pause the 
 * macro and let the user make changes to a selection or an image. The 
 * macro opens a button "continue". The execution of the macro continues
 * when the user presses the button.
 * 
 * @author	Volker Baecker
 */
public class Wait_For_User implements PlugIn {

	public void run(String arg) {
		WaitForUserOperation op = new WaitForUserOperation();
		op.doIt();
	}

}
