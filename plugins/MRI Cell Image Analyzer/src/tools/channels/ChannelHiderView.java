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
package tools.channels;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import java.awt.Toolkit;

/**
 * Hide the red, the green or the blue channel of the current image. The interface consists of a radio button with
 * 4 choices, to hide the channel red, green, blue or none.
 * 
 * @author	Volker Baecker
 **/
public class ChannelHiderView extends javax.swing.JFrame{
	private static final long serialVersionUID = 1L;
	private JPanel jPanel = null;
	private JLabel jLabel = null;
	protected ChannelHider model;
	
	private JRadioButton jRadioButton = null;
	private JRadioButton jRadioButton1 = null;
	private JRadioButton jRadioButton2 = null;
	private ButtonGroup radiobuttons;
	private JRadioButton jRadioButton3 = null;
	public ChannelHiderView() {
		super();
		initialize();
	}

	public ChannelHiderView(ChannelHider chooser) {
		super();
		this.model = chooser;
		initialize();
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {

		this.setContentPane(getJPanel());
		this.setName("JFrame1");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this
				.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		this.setBounds(45, 25, 96, 112);
		this.setTitle("Channel Chooser");
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
			jPanel.setLayout(null);
			jLabel.setBounds(7, -1, 24, 18);
			jLabel.setText("hide");
			jPanel.add(jLabel, null);
			jPanel.add(getJRadioButton(), null);
			jPanel.add(getJRadioButton1(), null);
			jPanel.add(getJRadioButton2(), null);
			jPanel.add(getJRadioButton3(), null);
			radiobuttons = new ButtonGroup();
			radiobuttons.add(this.getJRadioButton());
			radiobuttons.add(this.getJRadioButton1());
			radiobuttons.add(this.getJRadioButton2());
			radiobuttons.add(this.getJRadioButton3());
		}
		return jPanel;
	}

	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getJRadioButton() {
		if (jRadioButton == null) {
			jRadioButton = new JRadioButton();
			jRadioButton.setBounds(34, -1, 63, 21);
			jRadioButton.setText("red");
			jRadioButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setShowRed(false);
				}
			});
		}
		return jRadioButton;
	}
	/**
	 * This method initializes jRadioButton1	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getJRadioButton1() {
		if (jRadioButton1 == null) {
			jRadioButton1 = new JRadioButton();
			jRadioButton1.setBounds(34, 19, 63, 21);
			jRadioButton1.setText("green");
			jRadioButton1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setShowGreen(false);
				}
			});
		}
		return jRadioButton1;
	}
	/**
	 * This method initializes jRadioButton2	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getJRadioButton2() {
		if (jRadioButton2 == null) {
			jRadioButton2 = new JRadioButton();
			jRadioButton2.setBounds(34, 39, 63, 21);
			jRadioButton2.setText("blue");
			jRadioButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setShowBlue(false);
				}
			});
		}
		return jRadioButton2;
	}
	/**
	 * This method initializes jRadioButton3	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getJRadioButton3() {
		if (jRadioButton3 == null) {
			jRadioButton3 = new JRadioButton();
			jRadioButton3.setBounds(34, 59, 63, 21);
			jRadioButton3.setText("none");
			jRadioButton3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.setShowGreen(true);
					model.setShowRed(true);
					model.setShowBlue(true);
				}
			});
		}
		return jRadioButton3;
	}
           }  //  @jve:decl-index=0:visual-constraint="181,9"
