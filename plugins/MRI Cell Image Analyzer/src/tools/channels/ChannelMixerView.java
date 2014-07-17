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


import java.awt.Toolkit;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JSlider;

/**
 * Select the channles to be displayed and set the min and max display of the 
 * three channels. The interface has a drop down list to select between min 
 * and max, a checkbox to select the channels to be displayes and a slider
 * to adjust the min or max for each channel. Besides this there is a "keep adjustment"
 * checkbox. If it is checked the current adjustment will be applied to the new image
 * when the active image changes.
 *
 * @author	Volker Baecker
 **/
public class ChannelMixerView extends javax.swing.JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private JLabel jLabel = null;
	private JComboBox jComboBox = null;
	private JCheckBox jCheckBox = null;
	private JCheckBox jCheckBox1 = null;
	private JCheckBox jCheckBox2 = null;
	private JSlider greenSlider = null;
	private JSlider blueSlider = null;
	private JSlider redSlider = null;
	protected ChannelMixer model;
	
	private JCheckBox jCheckBox3 = null;
	public ChannelMixerView() {
		super();
		initialize();
	}

	public ChannelMixerView(ChannelMixer model) {
		super();
		this.model = model;
		initialize();
		model.addObserver(this);
	}
	
	/**
	 * Return the JFrameContentPane property value.
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJFrameContentPane() {
		if (ivjJFrameContentPane == null) {
			jLabel = new JLabel();
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(null);
			jLabel.setText("mix:");
			jLabel.setBounds(9, 5, 30, 14);
			ivjJFrameContentPane.add(jLabel, null);
			ivjJFrameContentPane.add(getJComboBox(), null);
			ivjJFrameContentPane.add(getJCheckBox(), null);
			ivjJFrameContentPane.add(getJCheckBox1(), null);
			ivjJFrameContentPane.add(getJCheckBox2(), null);
			ivjJFrameContentPane.add(getGreenSlider(), null);
			ivjJFrameContentPane.add(getBlueSlider(), null);
			ivjJFrameContentPane.add(getRedSlider(), null);
			ivjJFrameContentPane.add(getJCheckBox3(), null);
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
		this.setBounds(45, 25, 317, 153);
		this.setTitle("Channel Mixer");
		this.setContentPane(getJFrameContentPane());

		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {    
				model.close();
				dispose();
			}
		});
	}
	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */    
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox();
			jComboBox.setBounds(51, 1, 118, 22);
			jComboBox.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
					model.blockEvents();
					model.setModus((String)jComboBox.getSelectedItem());
					model.unblockEvents();
				}
			});
			for (int i=0; i<model.modi().length; i++) {
				String modus = model.modi()[i];
				jComboBox.addItem(modus);
			}
		}
		return jComboBox;
	}
	/**
	 * This method initializes jCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getJCheckBox() {
		if (jCheckBox == null) {
			jCheckBox = new JCheckBox();
			jCheckBox.setBounds(8, 36, 59, 21);
			jCheckBox.setText("red");
			jCheckBox.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.blockEvents();
					model.setShowRed(!model.isShowRed());
					model.updateImages();
					model.unblockEvents();
				}
			});
		}
		return jCheckBox;
	}
	/**
	 * This method initializes jCheckBox1	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getJCheckBox1() {
		if (jCheckBox1 == null) {
			jCheckBox1 = new JCheckBox();
			jCheckBox1.setBounds(8, 64, 59, 21);
			jCheckBox1.setText("green");
			jCheckBox1.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {   
					model.blockEvents();
					model.setShowGreen(!model.isShowGreen());
					model.updateImages();
					model.unblockEvents();
				}
			});
		}
		return jCheckBox1;
	}
	/**
	 * This method initializes jCheckBox2	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getJCheckBox2() {
		if (jCheckBox2 == null) {
			jCheckBox2 = new JCheckBox();
			jCheckBox2.setBounds(8, 91, 59, 21);
			jCheckBox2.setText("blue");
			jCheckBox2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {   
					model.blockEvents();
					model.setShowBlue(!model.isShowBlue());
					model.updateImages();
					model.unblockEvents();
				}
			});
		}
		return jCheckBox2;
	}
	/**
	 * This method initializes greenSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */    
	private JSlider getGreenSlider() {
		if (greenSlider == null) {
			greenSlider = new JSlider();
			greenSlider.setBounds(68, 62, 233, 25);
			greenSlider.setMinimum(0);
			greenSlider.setMaximum(255);
			greenSlider.setPaintLabels(true);
			greenSlider.addMouseListener(new java.awt.event.MouseAdapter() {   
				public void mouseReleased(java.awt.event.MouseEvent e) {    
					model.unblockEvents();
				} 
				public void mousePressed(java.awt.event.MouseEvent e) {    
					model.blockEvents();
				}
			});
			greenSlider.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					if (model.modus.equals(model.modi()[0])) {
						model.setGreenMin(greenSlider.getValue());
					}
					if (model.modus.equals(model.modi()[1])) {
						model.setGreenMax(greenSlider.getValue());
					}
				}
			});
		}
		return greenSlider;
	}
	/**
	 * This method initializes blueSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */    
	private JSlider getBlueSlider() {
		if (blueSlider == null) {
			blueSlider = new JSlider();
			blueSlider.setBounds(68, 89, 233, 25);
			blueSlider.setMinimum(0);
			blueSlider.setMaximum(255);
			blueSlider.addMouseListener(new java.awt.event.MouseAdapter() {   
				public void mouseReleased(java.awt.event.MouseEvent e) {    
					model.unblockEvents();
				} 
				public void mousePressed(java.awt.event.MouseEvent e) {    
					model.blockEvents();
				}
			});
			blueSlider.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					if (model.modus.equals(model.modi()[0])) {
						model.setBlueMin(blueSlider.getValue());
					}
					if (model.modus.equals(model.modi()[1])) {
						model.setBlueMax(blueSlider.getValue());
					}
				}
			});
		}
		return blueSlider;
	}
	/**
	 * This method initializes redSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */    
	private JSlider getRedSlider() {
		if (redSlider == null) {
			redSlider = new JSlider();
			redSlider.setBounds(68, 34, 233, 25);
			redSlider.setMinimum(0);
			redSlider.setMaximum(255);
			redSlider.addMouseListener(new java.awt.event.MouseAdapter() {   
				public void mouseReleased(java.awt.event.MouseEvent e) {    
					model.unblockEvents();
				} 
				public void mousePressed(java.awt.event.MouseEvent e) {    
					model.blockEvents();
				}
			});
			redSlider.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {    
					if (model.modus.equals(model.modi()[0])) {
						model.setRedMin(redSlider.getValue());
					}
					if (model.modus.equals(model.modi()[1])) {
						model.setRedMax(redSlider.getValue());
					}
				}
			});
		}
		return redSlider;
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable arg0, Object anAspect) {
		if (anAspect.equals("minMax")) {
			this.updateMinMax();
		}
		if (anAspect.equals("modus")) {
			this.updateModus();
		}
		if (anAspect.equals("channelSelection")) {
			this.updateChannelSelection();
		}
		if (anAspect.equals("keepAdjustment")) {
			this.updateKeepAdjustment();
		}
	}


	private void updateKeepAdjustment() {
		this.getJCheckBox3().setSelected(model.isKeepAdjustment());
	}

	private void updateChannelSelection() {
		this.getJCheckBox().setSelected(model.isShowRed());
		this.getJCheckBox1().setSelected(model.isShowGreen());
		this.getJCheckBox2().setSelected(model.isShowBlue());
	}

	private void updateModus() {
		this.getJComboBox().setSelectedItem(model.getModus());
	}

	private void updateMinMax() {
		if (model.getModus()==model.modi()[0]) {
			this.getRedSlider().setValue((int)model.getRedMin());
			this.getGreenSlider().setValue((int)model.getGreenMin());
			this.getBlueSlider().setValue((int)model.getBlueMin());
		}
		if (model.getModus()==model.modi()[1]) {
			this.getRedSlider().setValue((int)model.getRedMax());
			this.getGreenSlider().setValue((int)model.getGreenMax());
			this.getBlueSlider().setValue((int)model.getBlueMax());
		}
	}
	
	/**
	 * This method initializes jCheckBox3	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */    
	private JCheckBox getJCheckBox3() {
		if (jCheckBox3 == null) {
			jCheckBox3 = new JCheckBox();
			jCheckBox3.setBounds(180, 1, 129, 21);
			jCheckBox3.setText("keep adjustment");
			jCheckBox3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					model.toggleKeepAdjustment();
				}
			});
		}
		return jCheckBox3;
	}
        }  //  @jve:decl-index=0:visual-constraint="10,10"
