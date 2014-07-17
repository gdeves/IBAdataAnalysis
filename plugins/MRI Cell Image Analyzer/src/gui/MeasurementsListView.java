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
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.ListSelectionModel;

import java.awt.Toolkit;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

/**
 * Opens the content of a results table in a JTable. If the table contains
 * the centroids the objects of the selected rows will be selected if the 
 * mask is the active image.
 * 
 * @author	Volker Baecker
 **/
public class MeasurementsListView extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JScrollPane tableScrollPane = null;
	private JTable measurementsTable = null;
	private MeasurementsList model;

	public MeasurementsListView() {
		super();
		initialize();
	}

	public MeasurementsListView(MeasurementsList list) {
		super();
		this.model = list;
		list.addObserver(this);
		initialize();
	}

	private void initialize() {
		this.setSize(300, 200);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("MRI Measurements List");
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
			jContentPane.add(getTableScrollPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	public void update(Observable arg0, Object arg1) {
		// Nothing to do yet
	}

	/**
	 * This method initializes tableScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getTableScrollPane() {
		if (tableScrollPane == null) {
			tableScrollPane = new JScrollPane();
			tableScrollPane.setViewportView(getMeasurementsTable());
		}
		return tableScrollPane;
	}

	/**
	 * This method initializes measurementsTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getMeasurementsTable() {
		if (measurementsTable == null) {
			TableModel tableModel = new MeasurementsTableModel(model.getMeasurements());
			measurementsTable = new JTable(tableModel);
			ListSelectionModel rowSM = measurementsTable.getSelectionModel();
			rowSM.addListSelectionListener(new ListSelectionListener() {
			    public void valueChanged(ListSelectionEvent e) {
			        //Ignore extra messages.
			        if (e.getValueIsAdjusting()) return;
			        ListSelectionModel lsm =
			            (ListSelectionModel)e.getSource();
			        if (lsm.isSelectionEmpty()) {
			            
			        } else {
			        	Vector<Integer> selection = new Vector<Integer>();
			        	for (int i=lsm.getMinSelectionIndex(); i<=lsm.getMaxSelectionIndex(); i++) {
			        		if (lsm.isSelectedIndex(i)) {
			        			selection.add(new Integer(i));
			        		}
			        	}
			        	model.selectObjects(selection);
			        }
			    }
			});
		}
		return measurementsTable;
	}

}
