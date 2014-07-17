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
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JScrollPane;

import operations.Operation;
import utils.FileFromUser;
import java.awt.Toolkit;
import javax.swing.JCheckBox;

/**
 * The user can select an operation, change the options of the operation and apply it either
 * to the current slice or to the whole series in the object modeling workbench.
 * 
 * @author Volker B�cker
 */
public class ApplyOperationToSeriesView extends JFrame implements Observer {

	private static final long serialVersionUID = 8239749989224531589L;
	private JPanel jContentPane = null;
	private JList operationsList = null;
	private JPanel mainPanel = null;
	private JButton applyToSeriesButton = null;
	private JButton optionsButton = null;
	private JButton applyToSliceButton = null;
	private JScrollPane jScrollPane = null;
	protected ApplyOperationToSeries model;
	private JCheckBox applyTranslationsCheckBox = null;

	public ApplyOperationToSeriesView() {
		super();
		initialize();
	}

	public ApplyOperationToSeriesView(ApplyOperationToSeries model) {
		super();
		this.model = model;
		model.addObserver(this);
		initialize();
	}

	private void initialize() {
		this.setSize(398, 278);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("MRI OMW - apply operation to series");
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
			jContentPane.add(getMainPanel(), java.awt.BorderLayout.SOUTH);
			jContentPane.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	public void update(Observable arg0, Object arg1) {
	}

	/**
	 * This method initializes operationsList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getOperationsList() {
		if (operationsList == null) {
			operationsList = new JList(model.getOperationNames());
		}
		return operationsList;
	}

	/**
	 * This method initializes mainPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(null);
			mainPanel.setPreferredSize(new java.awt.Dimension(1,100));
			mainPanel.add(getApplyToSeriesButton(), null);
			mainPanel.add(getOptionsButton(), null);
			mainPanel.add(getApplyToSliceButton(), null);
			mainPanel.add(getApplyTranslationsCheckBox(), null);
		}
		return mainPanel;
	}

	/**
	 * This method initializes applyToSeriesButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getApplyToSeriesButton() {
		if (applyToSeriesButton == null) {
			applyToSeriesButton = new JButton();
			applyToSeriesButton.setBounds(new java.awt.Rectangle(15,52,180,28));
			applyToSeriesButton.setText("apply operation to series...");
			applyToSeriesButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Operation op = (Operation) model.getOperations().get(getOperationsList().getSelectedIndex());
					File targetFile = FileFromUser.getSaveFile("save series", "tif", ".tif");
					if (targetFile==null) return;
					model.applyOperationToSeries(op, targetFile);
				}
			});
		}
		return applyToSeriesButton;
	}

	/**
	 * This method initializes optionsButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOptionsButton() {
		if (optionsButton == null) {
			optionsButton = new JButton();
			optionsButton.setBounds(new java.awt.Rectangle(15,13,180,28));
			optionsButton.setText("change options...");
			optionsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Operation op = (Operation) model.getOperations().get(getOperationsList().getSelectedIndex());
					op.getOptions().view().setVisible(true);
				}
			});
		}
		return optionsButton;
	}

	/**
	 * This method initializes applyToSliceButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getApplyToSliceButton() {
		if (applyToSliceButton == null) {
			applyToSliceButton = new JButton();
			applyToSliceButton.setBounds(new java.awt.Rectangle(205,52,180,28));
			applyToSliceButton.setText("apply operation to slice");
			applyToSliceButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Operation op = (Operation) model.getOperations().get(getOperationsList().getSelectedIndex());
					model.applyOperationToSlice(op);
				}
			});
		}
		return applyToSliceButton;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			jScrollPane.setPreferredSize(new java.awt.Dimension(80,120));
			jScrollPane.setViewportView(getOperationsList());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes applyTranslationsCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getApplyTranslationsCheckBox() {
		if (applyTranslationsCheckBox == null) {
			applyTranslationsCheckBox = new JCheckBox();
			applyTranslationsCheckBox.setBounds(new java.awt.Rectangle(213,13,171,28));
			applyTranslationsCheckBox.setText("apply translations");
			applyTranslationsCheckBox
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							model.setApplyTranslations(!model.isApplyTranslations());
						}
					});
		}
		return applyTranslationsCheckBox;
	}

}  //  @jve:decl-index=0:visual-constraint="128,43"
