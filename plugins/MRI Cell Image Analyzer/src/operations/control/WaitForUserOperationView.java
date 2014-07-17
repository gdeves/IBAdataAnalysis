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
package operations.control;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.Toolkit;

/**
 * Opens a button with the text continue. The application is paused until the button is
 * pressed.
 * 
 * @author Volker
 */
public class WaitForUserOperationView extends javax.swing.JFrame {
	private static final long serialVersionUID = 1L;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	protected WaitForUserOperation model;
	private JButton jButton = null;
	
	public WaitForUserOperationView() {
		super();
		initialize();
	}
	
	public WaitForUserOperationView(WaitForUserOperation model) {
		super();
		this.model=model;
		initialize();
	}

	private javax.swing.JPanel getJFrameContentPane() {
		if (ivjJFrameContentPane == null) {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new BorderLayout());
			ivjJFrameContentPane.add(getJButton(), java.awt.BorderLayout.CENTER);
		}
		return ivjJFrameContentPane;
	}

	private void initialize() {

		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setName("JFrame1");
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setBounds(45, 25, 148, 93);
		this.setTitle("Confirm");
		this.setContentPane(getJFrameContentPane());

	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("continue");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.wait = false;
				}
			});
		}
		return jButton;
	}
 }  //  @jve:decl-index=0:visual-constraint="10,10"
