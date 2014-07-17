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
package gui.options;

import gui.OptionsView;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextPane;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import operations.Operation;
import java.awt.Toolkit;

/**
 * The view of the option depends on the type of the option. For a numerical option
 * it constsis of an input field, a text showing the min. and max. values, a help text and
 * a button to open the help page. 
 * 
 * @author	Volker Baecker
 **/
public class OptionView extends javax.swing.JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private JButton jButton = null;
	private JLabel jLabel = null;
	private JPanel jPanel = null;
	private JTextField jTextField = null;
	private JPanel jPanel1 = null;
	private JTextPane jTextPane = null;
	private JTextField jTextField1 = null;
	private JButton editButton = null;
	Option model;
	private JCheckBox jCheckBox = null;  //  @jve:decl-index=0:visual-constraint="570,82"
	private JComboBox jComboBox = null;  //  @jve:decl-index=0:visual-constraint="91,215"
	
	public OptionView(Option option) {
		super();
		this.setModel(option);
		initialize();
	}

	/**
	 * Return the JFrameContentPane property value.
	 * @return javax.swing.JPanel
	 */
	protected javax.swing.JPanel getJFrameContentPane() {
		if (ivjJFrameContentPane == null) {
			jLabel = new JLabel();
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new BoxLayout(ivjJFrameContentPane, BoxLayout.Y_AXIS));
			jLabel.setText(model.getName());
			jLabel.setPreferredSize(new java.awt.Dimension(150,19));
			jLabel.setMaximumSize(new java.awt.Dimension(2147483647,2147483647));
			jLabel.setMinimumSize(new java.awt.Dimension(120,19));
			ivjJFrameContentPane.add(getJPanel1(), null);
		}
		return ivjJFrameContentPane;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {

		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setName("JFrame1");
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setBounds(45, 25, 379, 76);
		this.setTitle(model.getName());
		this.setContentPane(getJFrameContentPane());
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	protected JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("?");
			jButton.setPreferredSize(new java.awt.Dimension(20,20));
			jButton.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 10));
			jButton.setMargin(new java.awt.Insets(2,2,2,2));
			jButton.setToolTipText("get help for this option");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					OptionsView optionsView = (OptionsView) getJPanel().getTopLevelAncestor();
					String operationName = optionsView.getModel().getOperation().name();
					model.openHelpFor(operationName);
				}
			});
		}
		return jButton;
	}
	
	protected JButton getEditButton() {
		if (editButton == null) {
			editButton = new JButton();
			editButton.setText("edit");
			editButton.setPreferredSize(new java.awt.Dimension(80,15));
			editButton.setMargin(new java.awt.Insets(2,2,2,2));
			editButton.setToolTipText("edit the options values");
			editButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.openEditor();
			}
			});
		}
		return editButton;
	}
	
	protected JButton getBrowseButton() {
		if (editButton == null) {
			editButton = new JButton();
			editButton.setText("browse");
			editButton.setPreferredSize(new java.awt.Dimension(80,20));
			editButton.setMargin(new java.awt.Insets(2,2,2,2));
			editButton.setToolTipText("select folder using a filebrowser");
			editButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					OptionsView optionsView = (OptionsView) getJPanel().getTopLevelAncestor();
					Operation operation = optionsView.getModel().getOperation();
					model.browse(operation);
			}
			});
		}
		return editButton;
	}
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			jPanel.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);
			jPanel.setMinimumSize(new java.awt.Dimension(96,20));
			jPanel.setMaximumSize(new java.awt.Dimension(2147483647,20));
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 0;
			gridBagConstraints6.insets = new java.awt.Insets(0,1,0,1);
			gridBagConstraints7.gridx = 1;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.ipadx = 9;
			gridBagConstraints7.ipady = 1;
			gridBagConstraints7.insets = new java.awt.Insets(0,0,0,1);
			gridBagConstraints8.gridx = 2;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.ipadx = 9;
			gridBagConstraints8.ipady = 1;
			gridBagConstraints8.insets = new java.awt.Insets(0,0,0,1);
			gridBagConstraints9.gridx = 3;
			gridBagConstraints9.gridy = 0;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.ipadx = 9;
			gridBagConstraints9.ipady = 1;
			gridBagConstraints9.insets = new java.awt.Insets(0,0,0,1);
			jPanel.add(getJButton(), gridBagConstraints6);
			jPanel.add(jLabel, gridBagConstraints7);
			boolean added = false;
			if (model.isBoolean()) {
				jPanel.add(getJCheckBox(), gridBagConstraints8);
				added = true;
			} 
			if (model.isChoice()) {
				jPanel.add(getJComboBox(), gridBagConstraints8);
				added = true;
			}
			if (model.isList()) {
				jPanel.add(getEditButton(), gridBagConstraints8);
				added = true;
			}
			if (!added) {
				jPanel.add(getJTextField(), gridBagConstraints8);
			}
			if (model.isForFilename()) {
				jPanel.add(getBrowseButton(), gridBagConstraints9);
			} else {
				jPanel.add(getJTextField1(), gridBagConstraints9);
			}
		}
		return jPanel;
	}
	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setPreferredSize(new java.awt.Dimension(80,19));
			jTextField.setText(model.getValue());
			jTextField.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {    
					model.setValue(jTextField.getText());
				}
			});
			jTextField.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setValue(jTextField.getText());
					jTextField.selectAll();
				}
			});
		}
		return jTextField;
	}
	
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	public JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));
			jPanel1.setMinimumSize(new java.awt.Dimension(96,20));
			jPanel1.setMaximumSize(new java.awt.Dimension(2147483647,40));
			jPanel1.setPreferredSize(new java.awt.Dimension(319,20));
			jPanel1.add(getJPanel(), null);
			jPanel1.add(getJTextPane(), null);
		}
		return jPanel1;
	}
	/**
	 * This method initializes jTextPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */    
	private JTextPane getJTextPane() {
		if (jTextPane == null) {
			jTextPane = new JTextPane();
			jTextPane.setText(model.getShortHelpText());
			jTextPane.setEnabled(false);
			jTextPane.setBackground(new java.awt.Color(224,223,227));
			jTextPane.setPreferredSize(new java.awt.Dimension(156,19));
			jTextPane.setMaximumSize(new java.awt.Dimension(2147483647,20));
			jTextPane.setDisabledTextColor(java.awt.SystemColor.controlDkShadow);
		}
		return jTextPane;
	}
	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getJTextField1() {
		if (jTextField1 == null) {
			jTextField1 = new JTextField();
			jTextField1.setPreferredSize(new java.awt.Dimension(90,19));
			jTextField1.setEnabled(false);
			jTextField1.setText(model.getRangeString());
			jTextField1.setBackground(new java.awt.Color(224,223,227));
			jTextField1.setDisabledTextColor(java.awt.SystemColor.controlDkShadow);
		}
		return jTextField1;
	}

	/**
	 * @param option
	 */
	public void setModel(Option model) {
		this.model = model;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable arg0, Object aspect) {
		if (aspect.equals("name")) {
			this.updateName();
		}
		if (aspect.equals("value")) {
			this.updateValue();
		}
	}

	/**
	 * 
	 */
	private void updateName() {
		this.jLabel.setText(model.getName());
		
	}
	
	/**
	 * 
	 */
	private void updateValue() {
		this.getJTextField().setText(model.getValue());
		
	}
	
	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	protected JCheckBox getJCheckBox() {
		if (jCheckBox == null) {
			jCheckBox = new JCheckBox();
			jCheckBox.setPreferredSize(new java.awt.Dimension(90,19));
			jCheckBox.setSelected(model.getBooleanValue());
			jCheckBox.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					boolean checked = true;
					if (jCheckBox.getSelectedObjects()==null) {
						checked = false;
					}
					model.setValue((new Boolean(checked)).toString());
				}
			});
		}
		return jCheckBox;
	}
	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */    
	protected JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox(model.getChoices());
			jComboBox.setSelectedItem(model.getValue());
			jComboBox.setPreferredSize(new java.awt.Dimension(90,19));
			jComboBox.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setValue((String) jComboBox.getSelectedItem());
				}
			});
		}
		return jComboBox;
	}
         }  //  @jve:decl-index=0:visual-constraint="129,49"
