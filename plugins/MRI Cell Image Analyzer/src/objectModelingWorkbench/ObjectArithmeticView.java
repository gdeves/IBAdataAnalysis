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
package objectModelingWorkbench;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Toolkit;

/**
 * The user interface of ObjectArithmetic. 
 * Add to Object3D objects or subtract one from the other. The operation is performed
 * on the selections that make up the objects slice by slice.
 * 
 * @author Volker B�cker
 */
public class ObjectArithmeticView extends JFrame implements Observer {
	private static final long serialVersionUID = -9111198475298702449L;
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JComboBox operand1ComboBox = null;
	private JComboBox operatorComboBox = null;
	private JComboBox operand2ComboBox = null;
	private JLabel object1Label = null;
	private JLabel operandLabel = null;
	private JLabel object2Label = null;

	protected ObjectArithmetic model;
	private JButton applyButton = null;
	
	public void update(Observable arg0, Object arg1) {
	}

	public ObjectArithmeticView() {
		super();
		initialize();
	}

	public ObjectArithmeticView(ObjectArithmetic model) {
		super();
		this.model = model;
		model.addObserver(this);
		initialize();
	}
	
	private void initialize() {
		this.setSize(615, 158);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setTitle("MRI OMW - object arithmetic");
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			object2Label = new JLabel();
			object2Label.setBounds(new java.awt.Rectangle(415,14,74,16));
			object2Label.setText("object 2");
			operandLabel = new JLabel();
			operandLabel.setBounds(new java.awt.Rectangle(225,14,74,16));
			operandLabel.setText("operand");
			object1Label = new JLabel();
			object1Label.setBounds(new java.awt.Rectangle(35,14,74,16));
			object1Label.setText("object 1");
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.add(getOperand1ComboBox(), null);
			jPanel.add(getOperatorComboBox(), null);
			jPanel.add(getOperand2ComboBox(), null);
			jPanel.add(object1Label, null);
			jPanel.add(operandLabel, null);
			jPanel.add(object2Label, null);
			jPanel.add(getApplyButton(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes operand1ComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getOperand1ComboBox() {
		if (operand1ComboBox == null) {
			operand1ComboBox = new JComboBox(model.getOperands());
			operand1ComboBox.setBounds(new java.awt.Rectangle(35,38,155,25));
		}
		return operand1ComboBox;
	}

	/**
	 * This method initializes operatorComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getOperatorComboBox() {
		if (operatorComboBox == null) {
			operatorComboBox = new JComboBox(model.getOperators());
			operatorComboBox.setBounds(new java.awt.Rectangle(225,38,155,25));
		}
		return operatorComboBox;
	}

	/**
	 * This method initializes operand2ComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getOperand2ComboBox() {
		if (operand2ComboBox == null) {
			operand2ComboBox = new JComboBox(model.getOperands());
			operand2ComboBox.setBounds(new java.awt.Rectangle(415,38,155,25));
		}
		return operand2ComboBox;
	}

	/**
	 * This method initializes applyButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getApplyButton() {
		if (applyButton == null) {
			applyButton = new JButton();
			applyButton.setText("apply");
			applyButton.setLocation(new java.awt.Point(225,91));
			applyButton.setSize(new java.awt.Dimension(155,25));
			applyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.applyOperation(getOperand1ComboBox().getSelectedIndex(), 
										(String)getOperatorComboBox().getSelectedItem(), 
										getOperand2ComboBox().getSelectedIndex());
				}
			});
		}
		return applyButton;
	}

}  //  @jve:decl-index=0:visual-constraint="51,27"
