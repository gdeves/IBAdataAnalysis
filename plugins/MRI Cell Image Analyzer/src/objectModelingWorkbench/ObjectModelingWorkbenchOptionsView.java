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
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Toolkit;
/**
 * The user interface to change the options of the ObjectModelingWorkbench. At the time
 * being the only options are the width and the height of the canvas in which the images
 * are displayed.
 * 
 * @author Volker B�cker
 */
public class ObjectModelingWorkbenchOptionsView extends JFrame implements Observer {
	private static final long serialVersionUID = 8407861456297840823L;
	private JPanel jContentPane = null;
	private JTabbedPane jTabbedPane = null;
	private JPanel jPanel = null;
	private JTextField canvasWidthTextField = null;
	private JLabel canvasWidthLabel = null;
	private JLabel canvasHeightLabel = null;
	private JTextField canvasHeightTextField = null;
	protected ImageCanvas model;

	public ObjectModelingWorkbenchOptionsView() {
		super();
		initialize();
	}
	
	public ObjectModelingWorkbenchOptionsView(ImageCanvas model) {
		super();
		this.model = model;
		model.getBroadcaster().addObserver(this);
		initialize();
	}

	private void initialize() {
		this.setSize(300, 200);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("MRI OMW Options");
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
			jContentPane.add(getJTabbedPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("canvas size", null, getJPanel(), null);
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			canvasHeightLabel = new JLabel();
			canvasHeightLabel.setText("canvas height:");
			canvasWidthLabel = new JLabel();
			canvasWidthLabel.setText("canvas width:");
			jPanel = new JPanel();
			jPanel.add(canvasWidthLabel, null);
			jPanel.add(getCanvasWidthTextField(), null);
			jPanel.add(canvasHeightLabel, null);
			jPanel.add(getCanvasHeightTextField(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes canvasWidthTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCanvasWidthTextField() {
		if (canvasWidthTextField == null) {
			canvasWidthTextField = new JTextField();
			canvasWidthTextField.setPreferredSize(new java.awt.Dimension(50,20));
			canvasWidthTextField.setText(Integer.toString(model.getWidth()));
			canvasWidthTextField.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					int value;
					try {
						value = Integer.parseInt(canvasWidthTextField.getText());
					} catch (Exception exc) {
						getCanvasWidthTextField().selectAll();
						getCanvasWidthTextField().grabFocus();
						return;
					}
					model.setWidth(value);
				}
			});
			canvasWidthTextField.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyPressed(java.awt.event.KeyEvent e) {
					if (e.getKeyCode()!=KeyEvent.VK_ENTER) return;
					int value;
					try {
						value = Integer.parseInt(canvasWidthTextField.getText());
					} catch (Exception exc) {
						getCanvasWidthTextField().selectAll();
						getCanvasWidthTextField().grabFocus();
						return;
					}
					model.setWidth(value);
				}
			});
		}
		return canvasWidthTextField;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getCanvasHeightTextField() {
		if (canvasHeightTextField == null) {
			canvasHeightTextField = new JTextField();
			canvasHeightTextField.setPreferredSize(new java.awt.Dimension(50,20));
			canvasHeightTextField.setText(Integer.toString(model.getHeight()));
			canvasHeightTextField.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					int value;
					try {
						value = Integer.parseInt(canvasHeightTextField.getText());
					} catch (Exception exc) {
						getCanvasHeightTextField().selectAll();
						getCanvasHeightTextField().grabFocus();
						return;
					}
					model.setHeight(value);
				}
			});
			canvasHeightTextField.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyPressed(java.awt.event.KeyEvent e) {
					if (e.getKeyCode()!=KeyEvent.VK_ENTER) return;
					int value;
					try {
						value = Integer.parseInt(canvasHeightTextField.getText());
					} catch (Exception exc) {
						getCanvasHeightTextField().selectAll();
						getCanvasHeightTextField().grabFocus();
						return;
					}
					model.setHeight(value);
				}
			});
		}
		return canvasHeightTextField;
	}

	public void update(Observable aspect, Object value) {
		if (aspect.equals("width")) this.updateWidth((String)value);
		if (aspect.equals("height")) this.updateHeight((String)value);
	}

	protected void updateHeight(String height) {
		this.getCanvasHeightTextField().setText(height);
		
	}

	protected void updateWidth(String width) {
		this.getCanvasWidthTextField().setText(width);
	}

}
