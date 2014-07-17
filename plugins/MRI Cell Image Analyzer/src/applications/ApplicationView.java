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
package applications;

import java.awt.Point;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import operations.Operation;
import operations.OperationView;
import java.awt.Toolkit;

/**
 *  List view of an application. Manages a list of operations in a box. Operations
 *  can be added to the box by using drag-and-drop.
 *  
 * @author	Volker Baecker
 **/
public class ApplicationView extends javax.swing.JFrame {
	private static final long serialVersionUID = 1L;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private JScrollPane jScrollPane = null;
	private JPanel jPanel = null;
	protected Application model;
	
	public ApplicationView(Application model) {
		super();
		this.model = model;
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
			ivjJFrameContentPane.setPreferredSize(new java.awt.Dimension(19,19));
			ivjJFrameContentPane.add(getJScrollPane(), null);
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
		this.setBounds(45, 25, 317, 273);
		this.setTitle(model.name());
		this.setContentPane(getJFrameContentPane());
		this.addWindowListener(new java.awt.event.WindowAdapter() {   
			public void windowActivated(java.awt.event.WindowEvent e) {    
				model.moveToFront((ApplicationView)getJPanel().getTopLevelAncestor());
			} 
			public void windowClosing(java.awt.event.WindowEvent e) {
				model.applicationViewClosed((ApplicationView)getJPanel().getTopLevelAncestor());
				dispose();
			}
		});
		ArrayList<Operation> operations = model.getOperations();
		double xSize = 0;
		double ySize = 0;
		int maxButtonWidth = 0;
		int buttonWidth;
		for (Operation anOperation : operations) {
			OperationView view = anOperation.view();
			buttonWidth = view.getRunOperationButton().getText().length();
			if (buttonWidth > maxButtonWidth) maxButtonWidth = buttonWidth;
			if (anOperation.getParameterNames()!=null && anOperation.getParameterNames().length>0) {
				view.getParameterButton().setEnabled(true);
			}
			getJPanel().add(view.getJPanel());
			if (view.getSize().getWidth()>xSize) xSize = view.getSize().getWidth();
			ySize += view.getSize().getHeight()+1;
		}
		for (Operation anOperation : operations) {
			OperationView view = anOperation.view();
			JButton button = view.getRunOperationButton(); 
			String text = button.getText();
			if (text.length()<maxButtonWidth) {
				while(text.length()<maxButtonWidth) {
					text += " ";
				}
			}
			button.setText(text);
		}
		this.setSize((int)xSize+16, Math.min((int)ySize, 600));
		if (model.getOperations().size()==0) {
			this.setSize(200, 400);
		}
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
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	public JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
		}
		return jPanel;
	}
	
	/**
	 * @return Returns the model.
	 */
	public Application getModel() {
		return model;
	}

	public void operationDropedAt(Operation operation, Point localPoint) {
		if (this.getState()!=ApplicationView.NORMAL) return;
		ArrayList<Operation> operations = model.getOperations();
		int addAfterIndex = -1;
		for (int i=0; i<operations.size(); i++) {
			Operation op = (Operation) operations.get(i);
			Point opOrigin = new Point(op.view().getJPanel().getX(), op.view().getJPanel().getY());
			if (localPoint.getY()>opOrigin.getY()) {
				addAfterIndex = i;
			}
		}
		if (operation.view().isInOperationsBox()) return;
		if (operations.contains(operation)) return;
		model.addOperationAfterIndex(operation, addAfterIndex);
	}

	public void rebuild() {
		this.initialize();
		
	}

	public void removeOperation(Operation operation) {
		jPanel.remove(operation.view().getJPanel());
		model.removeOperation(operation);
	}
  }
