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
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import java.awt.Toolkit;

/**
 * The MRI Visual Scripting launcher window. Contains everything needed for
 * the visual scripting, that is: the Operations menu the Applications menu
 * and the about menu. Parts of the operations menu and the applications menu 
 * are created from the folders _operations and _applications.
 * 
 * @author	Volker Baecker
 **/
public class MRIVisualScriptingView extends JFrame {

	static final long serialVersionUID = 1567585955179391335L;
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private CellImageAnalyzer analyzer;

	public MRIVisualScriptingView() {
		super();
		initialize();
	}

	private void initialize() {
		this.setSize(300, 84);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setJMenuBar(getJJMenuBar());
		this.setContentPane(getJContentPane());
		this.setTitle("MRI Visual Scripting");
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				dispose();
			}
		});
		analyzer = this.getCellImageAnalyzer();
		CellImageAnalyzerView analyzerView = analyzer.getView();
		JMenu operationsMenu = analyzerView.getOperationsMenu();
		JMenu applicationsMenu = analyzerView.getApplicationsMenu();
		JMenu aboutMenu = analyzerView.getAboutMenu();
		this.getJMenuBar().add(operationsMenu);
		this.getJMenuBar().add(applicationsMenu);
		this.getJMenuBar().add(aboutMenu);
		CellImageAnalyzer.setCurrent(new CellImageAnalyzer());
	}

	protected CellImageAnalyzer getCellImageAnalyzer() {
		if (analyzer==null) analyzer = new CellImageAnalyzer();
		return analyzer;
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
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
		}
		return jJMenuBar;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
