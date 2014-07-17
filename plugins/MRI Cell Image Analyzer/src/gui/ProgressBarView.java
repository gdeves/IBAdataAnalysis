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

import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import java.awt.Toolkit;

/**
 * The view of the progress bar. The text is didplayed above the progress bar.
 *
 * @author	Volker Baecker
 **/
public class ProgressBarView extends JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JProgressBar jProgressBar = null;
	private JTextField jTextField = null;
	private JPanel jPanel = null;
	private ProgressBar model;

	public void update(Observable sender, Object aspect) {
		if (aspect.equals("min")) this.handleMinChanged();
		if (aspect.equals("max")) this.handleMaxChanged();
		if (aspect.equals("progress")) this.handleProgressChanged();
		if (aspect.equals("text")) this.handleTextChanged();
	}

	private void handleTextChanged() {
		this.getJTextField().setText(model.getText());
	}

	private void handleProgressChanged() {
		this.getJProgressBar().setValue(model.getProgress());
		if (model.getProgress()>=model.getMax()) {
			Toolkit.getDefaultToolkit().beep();   
			if (model.isCloseWhenFinished()) {
				this.dispose();
			}
		}
	}

	private void handleMaxChanged() {
		this.getJProgressBar().setMaximum(model.getMax());
	}

	private void handleMinChanged() {
		this.getJProgressBar().setMinimum(model.getMin());
	}

	public ProgressBarView() {
		super();
		initialize();
	}

	public ProgressBarView(ProgressBar model) {
		super();
		this.model = model;
		model.addObserver(this);
		initialize();
	}

	private void initialize() {
		this.setSize(300, 76);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("progress");
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
			jContentPane.add(getJPanel(), java.awt.BorderLayout.NORTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setStringPainted(true);
			jProgressBar.setMinimum(model.getMin());
			jProgressBar.setMaximum(model.getMax());
			jProgressBar.setValue(model.getProgress());
		}
		return jProgressBar;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setText(model.getText());
			jTextField.setEnabled(false);
			jTextField.setEditable(false);
		}
		return jTextField;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BoxLayout(getJPanel(), BoxLayout.Y_AXIS));
			jPanel.add(getJTextField(), null);
			jPanel.add(getJProgressBar(), null);
		}
		return jPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
