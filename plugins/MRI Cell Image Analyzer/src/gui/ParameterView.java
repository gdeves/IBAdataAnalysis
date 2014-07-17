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

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextPane;
import java.awt.Toolkit;
/**
 * A view in which the user can connect the input parameters of an operation
 * to the results of previous operations.
 *
 * @author	Volker Baecker
 **/
public class ParameterView extends javax.swing.JFrame {
	private static final long serialVersionUID = 1L;
	protected Parameter model;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JScrollPane jScrollPane = null;
	private JScrollPane jScrollPane1 = null;
	private JScrollPane jScrollPane2 = null;
	private JPanel jPanel2 = null;
	private JPanel jPanel3 = null;
	private JPanel jPanel4 = null;
	private JButton jButton = null;
	private JPanel jPanel5 = null;
	private JList jList = null;
	private JList jList1 = null;
	private JList jList2 = null;
	private JPanel jPanel6 = null;
	private JTextPane jTextPane = null;
	private JScrollPane jScrollPane3 = null;
	
	public ParameterView(Parameter model) {
		super();
		this.model = model;
		initialize();
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {

		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJPanel());
		this.setName("JFrame1");
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setBounds(45, 25, 464, 372);
		this.setTitle("parameters for " + model.getOperation().name());
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.add(getJPanel1(), java.awt.BorderLayout.CENTER);
			jPanel.add(getJPanel5(), java.awt.BorderLayout.SOUTH);
			jPanel.add(getJPanel6(), java.awt.BorderLayout.NORTH);
		}
		return jPanel;
	}
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jLabel2 = new JLabel();
			jLabel1 = new JLabel();
			jLabel = new JLabel();
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.X_AXIS));
			jLabel.setText(" parameter");
			jLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
			jLabel1.setText(" from operation");
			jLabel2.setText(" output");
			jPanel1.add(getJPanel2(), null);
			jPanel1.add(getJPanel3(), null);
			jPanel1.add(getJPanel4(), null);
		}
		return jPanel1;
	}
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJList());
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getJList1());
		}
		return jScrollPane1;
	}
	/**
	 * This method initializes jScrollPane2	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane2() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setViewportView(getJList2());
		}
		return jScrollPane2;
	}
	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setLayout(new BorderLayout());
			jPanel2.add(jLabel, java.awt.BorderLayout.NORTH);
			jPanel2.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		}
		return jPanel2;
	}
	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.setLayout(new BorderLayout());
			jPanel3.add(jLabel1, java.awt.BorderLayout.NORTH);
			jPanel3.add(getJScrollPane1(), java.awt.BorderLayout.CENTER);
		}
		return jPanel3;
	}
	/**
	 * This method initializes jPanel4	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel4() {
		if (jPanel4 == null) {
			jPanel4 = new JPanel();
			jPanel4.setLayout(new BorderLayout());
			jPanel4.add(jLabel2, java.awt.BorderLayout.NORTH);
			jPanel4.add(getJScrollPane2(), java.awt.BorderLayout.CENTER);
		}
		return jPanel4;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("apply");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					int parameterIndex = jList.getSelectedIndex();
					int operationIndex = jList1.getSelectedIndex();
					int resultIndex = jList2.getSelectedIndex();
					model.mapOperationInput(parameterIndex, operationIndex, resultIndex);
					updateParameterText();
				}
			});
		}
		return jButton;
	}
	
	private void updateParameterText() {
		this.getJTextPane().setText(model.getMappingTextForParameters());
	}
	
	/**
	 * This method initializes jPanel5	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel5() {
		if (jPanel5 == null) {
			jPanel5 = new JPanel();
			jPanel5.add(getJButton(), null);
		}
		return jPanel5;
	}
	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */    
	private JList getJList() {
		if (jList == null) {
			jList = new JList(model.getParameterNames());
			jList.setSelectedIndex(0);
			jList.addListSelectionListener(new javax.swing.event.ListSelectionListener() { 
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {    
					changedSelectedParameter();
				}
			});
			this.changedSelectedParameter();
		}
		return jList;
	}

	private void changedSelectedParameter() {
		this.updateOperationList();
	}

	private void updateOperationList() {
		JList list = this.getJList1();
		list.removeAll();
		list.setListData(model.getInputOperationsForParameter(this.getJList().getSelectedIndex()));
		this.changedSelectedOperation();
	}

	private void changedSelectedOperation() {
		this.updateResultList();
	}

	private void updateResultList() {
		JList list = this.getJList2();
		list.removeAll();
		list.setListData(model.getResultNamesFor(this.getJList().getSelectedIndex(), 
											 this.getJList1().getSelectedIndex()));
	}

	/**
	 * This method initializes jList1	
	 * 	
	 * @return javax.swing.JList	
	 */    
	private JList getJList1() {
		if (jList1 == null) {
			jList1 = new JList();
			jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() { 
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {    
					changedSelectedOperation();
				}
			});
		}
		return jList1;
	}
	/**
	 * This method initializes jList2	
	 * 	
	 * @return javax.swing.JList	
	 */    
	private JList getJList2() {
		if (jList2 == null) {
			jList2 = new JList();
		}
		return jList2;
	}
	/**
	 * This method initializes jPanel6	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel6() {
		if (jPanel6 == null) {
			jPanel6 = new JPanel();
			jPanel6.setLayout(new BorderLayout());
			jPanel6.add(getJScrollPane3(), java.awt.BorderLayout.NORTH);
		}
		return jPanel6;
	}
	/**
	 * This method initializes jTextPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */    
	private JTextPane getJTextPane() {
		if (jTextPane == null) {
			jTextPane = new JTextPane();
			jTextPane.setPreferredSize(new java.awt.Dimension(7,40));
			jTextPane.setText(model.getMappingTextForParameters());
			jTextPane.setEnabled(false);
		}
		return jTextPane;
	}
	/**
	 * This method initializes jScrollPane3	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane3() {
		if (jScrollPane3 == null) {
			jScrollPane3 = new JScrollPane();
			jScrollPane3.setViewportView(getJTextPane());
		}
		return jScrollPane3;
	}
                }  //  @jve:decl-index=0:visual-constraint="10,10"
