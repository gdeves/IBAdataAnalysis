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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;

import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.text.NumberFormatter;

/**
 * The interface allows to change the opacity of an image in the ObjectModelingWorkbench.
 * 
 * @author Volker B�cker
 */
public class ImageDisplayOptionsView extends JFrame implements Observer {
	private static final long serialVersionUID = -2925985010931556169L;
	private JPanel jContentPane = null;
	private JPanel jPanel = null;
	private JSlider alphaSlider = null;
	private JLabel alphaLabel = null;
	private JFormattedTextField alphaTextField = null;
	protected ImageDisplayOptions model;

	public void update(Observable arg0, Object arg1) {
	}

	public ImageDisplayOptionsView() {
		super();
		initialize();
	}

	public ImageDisplayOptionsView(ImageDisplayOptions model) {
		super();
		this.model = model;
		model.addObserver(this);
		initialize();
	}
	
	private void initialize() {
		this.setSize(522, 95);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("MRI OMW - Image Display Options");
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
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			alphaLabel = new JLabel();
			alphaLabel.setText("opacity:");
			jPanel = new JPanel();
			jPanel.setPreferredSize(new java.awt.Dimension(349,62));
			jPanel.add(alphaLabel, null);
			jPanel.add(getAlphaSlider(), null);
			jPanel.add(getAlphaTextField(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jSlider	
	 * 	
	 * @return javax.swing.JSlider	
	 */
	private JSlider getAlphaSlider() {
		if (alphaSlider == null) {
			alphaSlider = new JSlider();
			alphaSlider.setMaximum(100);
			alphaSlider.setMajorTickSpacing(10);
			alphaSlider.setMinimum(0);
			alphaSlider.setMinorTickSpacing(1);
			alphaSlider.setPreferredSize(new java.awt.Dimension(300,42));
			alphaSlider.setPaintTicks(true);
			alphaSlider.setPaintLabels(true);
			alphaSlider.setValue(model.getAlphaAsInt());
			alphaSlider.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					JSlider source = (JSlider)e.getSource();
					int alpha = (int)source.getValue();
					if (!source.getValueIsAdjusting()) { //done adjusting
						getAlphaTextField().setValue(new Integer(alpha));
						model.setAlpha(alpha / 100f);
					} else {
						getAlphaTextField().setText(String.valueOf(alpha));
					}
				}
			});
		}
		return alphaSlider;
	}

	/**
	 * This method initializes alphaTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JFormattedTextField getAlphaTextField() {
		if (alphaTextField == null) {
			NumberFormat numberFormat = NumberFormat.getIntegerInstance();
			NumberFormatter formatter = new NumberFormatter(numberFormat);
			formatter.setMinimum(new Integer(0));
			formatter.setMaximum(new Integer(100));
			alphaTextField = new JFormattedTextField(formatter);
			alphaTextField.setValue(new Integer(model.getAlphaAsInt()));
			alphaTextField.setColumns(5);
			alphaTextField.getInputMap().put(KeyStroke.getKeyStroke(
                    KeyEvent.VK_ENTER, 0),
                    "check");
			alphaTextField.getActionMap().put("check", new AbstractAction() {

				private static final long serialVersionUID = 2734762616696881361L;

				public void actionPerformed(ActionEvent e) {
			    if (!getAlphaTextField().isEditValid()) { //The text is invalid.
			        Toolkit.getDefaultToolkit().beep();
			        getAlphaTextField().selectAll();
			    } else try {                    //The text is valid,
			    	getAlphaTextField().commitEdit();     //so use it.
			    	getAlphaSlider().setValue(((Integer)getAlphaTextField().getValue()).intValue());
			    } catch (java.text.ParseException exc) {
			    }
			}

			}
			);
			alphaTextField.addPropertyChangeListener("action",
					new java.beans.PropertyChangeListener() {
						public void propertyChange(java.beans.PropertyChangeEvent e) {
				        	System.out.println("text value changed");
							if ("value".equals(e.getPropertyName())) {
						        Number value = (Number)e.getNewValue();
						        if (value != null) {
						            getAlphaSlider().setValue(value.intValue());
						        }
						    } 
						}
					});
		}
		return alphaTextField;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
