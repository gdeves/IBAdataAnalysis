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

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Toolkit;
/**
 * The interface of the accept or skip or exit operation shows three buttons: accept, skip
 * and exit. If accept is pressed the application continues with the next operation after
 * the accept or skip or exit operation. If the button skip is pressed the application 
 * skips a configurable number of operations. If the button exit is pressed the 
 * application is stopped.
 * 
 * @author Volker Baecker
 */
public class AcceptOrSkipOrExitOperationView extends javax.swing.JFrame {
	private static final long serialVersionUID = 1L;
	AcceptOrSkipOrExitOperation model;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private JPanel jPanel = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	private JButton jButton2 = null;
	private JLabel jLabel = null;

	public AcceptOrSkipOrExitOperationView() {
		super();
		initialize();
	}

	public AcceptOrSkipOrExitOperationView(AcceptOrSkipOrExitOperation model) {
		super();
		this.model=model;
		initialize();
	}
	
	/**
	 * Return the JFrameContentPane property value.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJFrameContentPane() {
		if (ivjJFrameContentPane == null) {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new BorderLayout());
			ivjJFrameContentPane.add(getJPanel(), java.awt.BorderLayout.NORTH);
		}
		return ivjJFrameContentPane;
	}

	private void initialize() {

		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setName("JFrame1");
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setBounds(45, 25, 252, 58);
		this.setTitle("accept or skip");
		this.setContentPane(getJFrameContentPane());

	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			jLabel = new JLabel();
			jPanel = new JPanel();
			jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.X_AXIS));
			jLabel.setText("          ");
			jPanel.add(getJButton(), null);
			jPanel.add(getJButton1(), null);
			jPanel.add(jLabel, null);
			jPanel.add(getJButton2(), null);
		}
		return jPanel;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("accept");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setDoAccept(true);
					model.wait = false;
				}
			});
		}
		return jButton;
	}
	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("skip");
			jButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setDoSkip(true);
					model.wait = false;
				}
			});
		}
		return jButton1;
	}
	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("exit");
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setDoExit(true);
					model.wait = false;
				}
			});
		}
		return jButton2;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
