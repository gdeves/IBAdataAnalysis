package splitAndMerge;

import help.HelpSystem;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.event.WindowEvent;
import java.util.Observable;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import sun.awt.VerticalBagLayout;

import java.awt.BorderLayout;

public class saveTool extends JFrame {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JButton Ok = null;
	private JLabel conditionSplitLabel = null;
	private JTextField conditionSplitValue = null;
	private JProgressBar progressBar = null;
	private JTextField thresholdBackgroundValue = null;
	private JLabel backgroundLabel = null;
	private JTextField thresholdSmallShapeValue = null;
	private JLabel smallShapeLabel = null;
	private JButton addConditionMerge = null;
	private JTextField conditionMerge1Value = null;
	private JLabel conditionMergeLabel = null;
	private JButton help = null;
	private JPanel top = null;
	private JPanel bottom = null;
	private JScrollPane center = null;
	private JPanel centerLine = null;
	private JComboBox conditionMerge1 = null;
	private JComboBox conditionSplit = null;
	private JLabel mergeLabel = null;


	/**
	 * This is the default constructor
	 */
	public saveTool() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setSize(new Dimension(493, 451));
        this.setTitle("Split and merge");
        this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			conditionMergeLabel = new JLabel();
			conditionMergeLabel.setText("Select merge condition and threshold :");
			conditionMergeLabel.setBounds(new Rectangle(9, 40, 218, 24));
			conditionMergeLabel.setName("conditionMergeLabel");
			smallShapeLabel = new JLabel();
			smallShapeLabel.setText("Mean size of small shapes :");
			smallShapeLabel.setBounds(new Rectangle(13, 45, 177, 22));
			backgroundLabel = new JLabel();
			backgroundLabel.setText("Define background :");
			backgroundLabel.setBounds(new Rectangle(13, 14, 130, 22));
			conditionSplitLabel = new JLabel();
			conditionSplitLabel.setText("Select condition to split :");
			conditionSplitLabel.setBounds(new Rectangle(10, 6, 138, 24));
			conditionSplitLabel.setName("conditionSplitLabel");
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getTop(), BorderLayout.PAGE_START);
			jContentPane.add(getCenter(), BorderLayout.CENTER);
			jContentPane.add(getBottom(), BorderLayout.PAGE_END);
		}
		return jContentPane;
	}
	
	/**
	 * This method initializes Ok	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOk() {
		if (Ok == null) {
			Ok = new JButton();
			Ok.setText("Ok");
			Ok.setBounds(new Rectangle(397, 75, 73, 31));
			Ok.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
				}
			});
		}
		return Ok;
	}

	/**
	 * This method initializes conditionValue	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getConditionValue() {
		if (conditionSplitValue == null) {
			conditionSplitValue = new JTextField();
			conditionSplitValue.setHorizontalAlignment(JTextField.RIGHT);
			conditionSplitValue.setName("conditionSplitValue");
			conditionSplitValue.setSize(new Dimension(50, 24));
			conditionSplitValue.setLocation(new Point(423, 6));
			conditionSplitValue.setText("9");
			conditionSplitValue.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {    
				}
			});
		}
		return conditionSplitValue;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getJProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
			progressBar.setBounds(new Rectangle(14, 115, 456, 23));
		}
		return progressBar;
	}

	/**
	 * This method initializes thresholdBackground	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getThresholdBackground() {
		if (thresholdBackgroundValue == null) {
			thresholdBackgroundValue = new JTextField();
			thresholdBackgroundValue.setHorizontalAlignment(JTextField.RIGHT);
			thresholdBackgroundValue.setBounds(new Rectangle(231, 14, 47, 22));
			thresholdBackgroundValue.setText("10");
			thresholdBackgroundValue.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {    
					
				}
			});
		}
		return thresholdBackgroundValue;
	}

	/**
	 * This method initializes smallShape	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getSmallShape() {
		if (thresholdSmallShapeValue == null) {
			thresholdSmallShapeValue = new JTextField();
			thresholdSmallShapeValue.setHorizontalAlignment(JTextField.RIGHT);
			thresholdSmallShapeValue.setBounds(new Rectangle(231, 45, 47, 22));
			thresholdSmallShapeValue.setText("26");
			thresholdSmallShapeValue.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {    
					
				}
			});
		}
		return thresholdSmallShapeValue;
	}
	
	
	
	/**
	 * This method initializes addConditionMerge	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddConditionMerge() {
		if (addConditionMerge == null) {
			addConditionMerge = new JButton();
			addConditionMerge.setText("Add condition");
			addConditionMerge.setBounds(new Rectangle(330, 75, 143, 24));
			addConditionMerge.setName("addConditionMerge");
			addConditionMerge.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {

				}
			});
		}
		return addConditionMerge;
	}

	/**
	 * This method initializes conditionMergeValue	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getConditionMergeValue() {
		if (conditionMerge1Value == null) {
			conditionMerge1Value = new JTextField();
			conditionMerge1Value.setHorizontalAlignment(JTextField.RIGHT);
			conditionMerge1Value.setName("conditionMerge1Value");
			conditionMerge1Value.setSize(new Dimension(50, 24));
			conditionMerge1Value.setLocation(new Point(423, 39));
			conditionMerge1Value.setText("75");
			conditionMerge1Value.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {    
					
				}
			});
		}
		return conditionMerge1Value;
	}

	/**
	 * This method initializes help	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getHelp() {
		if (help == null) {
			help = new JButton();
			help.setText("Help");
			help.setBounds(new Rectangle(314, 75, 73, 31));
			help.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					openHelp();
				}
			});
		}
		return help;
	}

	public void openHelp() {
		HelpSystem help = HelpSystem.getCurrent();
		help.openHelpFor(this.name());
	}

	/**
	 * @return the name of this component
	 */
	protected String name() {
		return "split-and-merge";
	}
	
	public void windowActivated(WindowEvent arg0) {
	}

	public void windowClosed(WindowEvent arg0) {
	}

	public void windowClosing(WindowEvent arg0) {
	}

	public void windowDeactivated(WindowEvent arg0) {
	}

	public void windowDeiconified(WindowEvent arg0) {
	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void windowOpened(WindowEvent arg0) {
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void update(Observable o, Object arg) {
		if(arg.equals("start working")) {
			startProgressBar();
		}
		if(arg.equals("stop working")) {
			stopProgressBar();
		}
	}

	private void stopProgressBar() {
		this.progressBar.setIndeterminate(false);
	}

	private void startProgressBar() {
		this.progressBar.setIndeterminate(true);
	}
	
	public JFrame getCurrent() {
		return this;
	}

	/**
	 * This method initializes top	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTop() {
		if (top == null) {
			mergeLabel = new JLabel();
			mergeLabel.setBounds(new Rectangle(9, 75, 120, 24));
			mergeLabel.setText("Merge condition :");
			top = new JPanel();
			top.setLayout(null);
			top.setPreferredSize(new Dimension(484, 111));
			top.add(conditionSplitLabel, null);
			top.add(getConditionSplit(), null);
			top.add(getConditionValue(), null);
			top.add(conditionMergeLabel, null);
			top.add(getConditionMergeValue(), null);
			top.add(getAddConditionMerge(), null);
			top.add(getJComboBox2(), null);
			top.add(mergeLabel, null);
		}
		return top;
	}

	/**
	 * This method initializes bottom	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getBottom() {
		if (bottom == null) {
			bottom = new JPanel();
			bottom.setLayout(null);
			bottom.setPreferredSize(new Dimension(484, 157));
			bottom.add(backgroundLabel, null);
			bottom.add(getThresholdBackground(), null);
			bottom.add(smallShapeLabel, null);
			bottom.add(getSmallShape(), null);
			bottom.add(getHelp(), null);
			bottom.add(getOk(), null);
			bottom.add(getJProgressBar(), null);
		}
		return bottom;
	}

	/**
	 * This method initializes center	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getCenter() {
		if (center == null) {
			center = new JScrollPane();
			center.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			center.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			center.setLocation(new Point(0, 111));
			center.setSize(new Dimension(485, 100));
			center.setViewportView(getCenterLine());
		}
		return center;
	}
	
	public JPanel createMergeCondition() {
		return null;
	}

	/**
	 * This method initializes centerLine	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	public JPanel getCenterLine() {
		if (centerLine == null) {
			centerLine = new JPanel();
			centerLine.setLayout(new VerticalBagLayout());
		}
		return centerLine;
	}
	
	public void repaintCenterLine() {
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJComboBox2() {
		if (conditionMerge1 == null) {
			conditionMerge1 = new JComboBox();
			conditionMerge1.setLocation(new Point(234, 39));
			conditionMerge1.setSize(new Dimension(160, 24));
		}
		return conditionMerge1;
	}

	/**
	 * This method initializes conditionSplit	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getConditionSplit() {
		if (conditionSplit == null) {
			conditionSplit = new JComboBox();
			conditionSplit.setLocation(new Point(234, 6));
			conditionSplit.setSize(new Dimension(160, 24));
			conditionSplit.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusLost(java.awt.event.FocusEvent e) {
					System.out.println("focusLost()"); // TODO Auto-generated Event stub focusLost()
				}
			});
		}
		return conditionSplit;
	}
	
	static public int getLineHeight() {		
		return 25;
	}

}
