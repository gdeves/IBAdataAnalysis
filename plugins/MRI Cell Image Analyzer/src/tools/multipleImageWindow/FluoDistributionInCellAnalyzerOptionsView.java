package tools.multipleImageWindow;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JTabbedPane;
import javax.swing.JCheckBox;
import java.awt.Rectangle;
import javax.swing.JTextField;
import java.awt.Point;
import javax.swing.JLabel;

public class FluoDistributionInCellAnalyzerOptionsView extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JTabbedPane jTabbedPane = null;

	private JPanel preprocessingOptionsPanel = null;

	private JCheckBox useBackgroundSubtractionCheckBox = null;

	private JPanel cellOptionsPanel = null;

	private JTextField membraneWidthTextField = null;

	private JLabel cellWidthLabel = null;

	protected FluoDistributionInCellAnalyzer model;

	/**
	 * This is the default constructor
	 */
	public FluoDistributionInCellAnalyzerOptionsView() {
		super();
		initialize();
	}

	public FluoDistributionInCellAnalyzerOptionsView(FluoDistributionInCellAnalyzer model) {
		this.model = model;
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(342, 112);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.setContentPane(getJContentPane());
		this.setTitle("Fluo Distribution Analyzer Options");
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
			jContentPane.add(getJTabbedPane(), BorderLayout.CENTER);
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
			jTabbedPane.addTab("pre-processing", null, getPreprocessingOptionsPanel(), null);
			jTabbedPane.addTab("cell", null, getCellOptionsPanel(), null);
		}
		return jTabbedPane;
	}

	/**
	 * This method initializes preprocessingOptionsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPreprocessingOptionsPanel() {
		if (preprocessingOptionsPanel == null) {
			preprocessingOptionsPanel = new JPanel();
			preprocessingOptionsPanel.setLayout(null);
			preprocessingOptionsPanel.add(getUseBackgroundSubtractionCheckBox(), null);
		}
		return preprocessingOptionsPanel;
	}

	/**
	 * This method initializes useBackgroundSubtractionCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getUseBackgroundSubtractionCheckBox() {
		if (useBackgroundSubtractionCheckBox == null) {
			useBackgroundSubtractionCheckBox = new JCheckBox();
			useBackgroundSubtractionCheckBox.setBounds(new Rectangle(9, 12, 201, 21));
			useBackgroundSubtractionCheckBox.setText("find and subtract background");
			
		}
		return useBackgroundSubtractionCheckBox;
	}

	/**
	 * This method initializes cellOptionsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCellOptionsPanel() {
		if (cellOptionsPanel == null) {
			cellWidthLabel = new JLabel();
			cellWidthLabel.setBounds(new Rectangle(14, 14, 115, 16));
			cellWidthLabel.setText("width of membrane:");
			cellOptionsPanel = new JPanel();
			cellOptionsPanel.setLayout(null);
			cellOptionsPanel.add(getMembraneWidthTextField(), null);
			cellOptionsPanel.add(cellWidthLabel, null);
		}
		return cellOptionsPanel;
	}

	/**
	 * This method initializes membraneWidthTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getMembraneWidthTextField() {
		if (membraneWidthTextField == null) {
			membraneWidthTextField = new JTextField();
			membraneWidthTextField.setSize(new Dimension(32, 20));
			membraneWidthTextField.setLocation(new Point(141, 12));
		}
		return membraneWidthTextField;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
