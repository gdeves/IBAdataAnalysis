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

import gui.options.Option;
import gui.options.OptionView;

import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * This is a modal view of the options. The rest of the interface is 
 * blocked while the dialog is open. This is used in the
 * ObjectModelingWorkbench.
 *  
 * @author	Volker Baecker
 **/
public class OptionsDialogView extends JDialog {
	private static final long serialVersionUID = 1L;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private JPanel jPanel = null;
	private JScrollPane jScrollPane = null;
	protected Options model;
	
	public OptionsDialogView(Options options) {
		super();
		this.model = options;
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
			ivjJFrameContentPane.setLayout(new BoxLayout(ivjJFrameContentPane, BoxLayout.Y_AXIS));
			ivjJFrameContentPane.add(getJScrollPane(), null);
		}
		return ivjJFrameContentPane;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
	//	this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setName("JFrame1");
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setBounds(45, 25, 365, 273);
		this.setTitle(model.getName());
		this.setContentPane(getJFrameContentPane());
		this.setUpOptions();
	}

	private void setUpOptions() {
		ArrayList<Option> options = model.getOptions();
		double xSize = 0;
		double ySize = 0;
		for (Option option : options) {
			OptionView view = option.newView();
			this.getJPanel().add(view.getJPanel1());
			double currentXSize = view.getSize().getWidth();
			if (currentXSize>xSize) xSize = currentXSize;
			ySize += view.getSize().getHeight();
		}
		if (ySize==0) ySize = 100;
		if (options.size()==0) {
			JLabel aLabel = new JLabel("This operation has no options");
			this.getJPanel().add(aLabel);
			this.setSize(300,100);
		} else {
			this.setSize((int)xSize+4, Math.min((int)ySize, 600));
		}
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
		}
		return jPanel;
	}
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJPanel());
		}
		return jScrollPane;
	}

	public Options getModel() {
		return model;
	}

	public void setModel(Options model) {
		this.model = model;
	}
}
