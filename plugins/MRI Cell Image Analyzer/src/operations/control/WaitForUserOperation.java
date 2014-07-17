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
 * The operation pauses the application until the user presses the continue-button.
 *
 * @author Volker Baecker
 */
public class WaitForUserOperation extends Operation {
	private static final long serialVersionUID = 1568822044770767623L;
	protected boolean wait = true;
	private WaitForUserOperationView waitView;
	
	protected void initialize() throws ClassNotFoundException {
		this.waitView = new WaitForUserOperationView(this);
		parameterTypes = new Class[0];
		parameterNames = new String[0];
		resultTypes = new Class[0];
		resultNames = new String[0];
	}
	
	public void doIt() {
		this.wait = true; 
		this.waitView.setVisible(true);
		while(wait) {
			try {
				Thread.yield();
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.waitView.setVisible(false);
	}
	
	protected boolean isBatchOperation() {
		return false;
	}
}
