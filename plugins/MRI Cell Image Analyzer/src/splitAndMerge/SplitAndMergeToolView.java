package splitAndMerge;

import help.HelpSystem;
import ij.IJ;
import ij.ImagePlus;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

public class SplitAndMergeToolView extends JFrame implements WindowListener, Observer{
	static public boolean isRunning = false;
	static public int mergeCondition;
	
	private ImagePlus image;  //  @jve:decl-index=0:
	private SplitAndMergeTool model;
	private Object[] split = new Object[]{"Standard deviation"} ;	
	private Object[] merge = new Object[]{"","Intensity","Gradient","Entropy","SNR"} ;
	private Object[] operator = new Object[]{"and","or"} ;
	private ArrayList<SplitAndMergeConditionView> listCondition = new ArrayList<SplitAndMergeConditionView>();
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
	private JButton help = null;
	private JPanel top = null;
	private JPanel bottom = null;
	private JScrollPane center = null;
	private JPanel centerLine = null;
	private JComboBox conditionSplit = null;
	private JLabel mergeLabel;

	/**
	 * This method initializes 
	 * 
	 */
	public SplitAndMergeToolView(SplitAndMergeTool model) {
		super();
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/icon.gif")));
		this.model = model;
		model.addObserver(this);
		SplitAndMergeToolView.mergeCondition = 0;
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
			smallShapeLabel = new JLabel();
			smallShapeLabel.setText("Mean size of small shapes :");
			smallShapeLabel.setBounds(new Rectangle(13, 45, 177, 22));
			backgroundLabel = new JLabel();
			backgroundLabel.setText("Define background :");
			backgroundLabel.setBounds(new Rectangle(13, 14, 130, 22));
			conditionSplitLabel = new JLabel();
			conditionSplitLabel.setText("Select condition to split :");
			conditionSplitLabel.setBounds(new Rectangle(10, 20, 138, 24));
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
					if (isRunning || emptyField()) {
						JOptionPane.showMessageDialog(jContentPane,
							    "Empty field",
							    "Warning",
							    JOptionPane.WARNING_MESSAGE);
						return;
					}
					try {
						image = IJ.getImage();
					}
					catch(Exception ex) {
						
					}
					if (image==null) return;
					image.getWindow().addWindowListener(model.getView());
					isRunning = true;
					model.run();
				}

			});
		}
		return Ok;
	}

	private boolean emptyField() {
		Iterator<SplitAndMergeConditionView> it = listCondition.iterator();
		while(it.hasNext()) {
			SplitAndMergeConditionView condition = it.next();
			if(condition.getConditionMergeChoice().getSelectedIndex()==0) return true;
			if(condition.getConditionMerge().getText().equals("")) return true;
		}
		return false;
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
			conditionSplitValue.setLocation(new Point(423, 20));
			conditionSplitValue.setText("9");
			conditionSplitValue.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {    
					model.setThresholdSplit(Integer.parseInt(conditionSplitValue.getText()));
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
					model.setThresholdBackground(Integer.parseInt(thresholdBackgroundValue.getText()));
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
					model.setThresholdSmallShape(Integer.parseInt(thresholdSmallShapeValue.getText()));
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
						centerLine.add(createMergeCondition());
						centerLine.setSize(centerLine.getWidth(), centerLine.getHeight()+getLineHeight());
						centerLine.revalidate();
						repaintCenterLine();
				}
			});
		}
		return addConditionMerge;
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
		if (image==null) return;
		model.stopThread();
		SplitAndMergeToolView.isRunning=false;
		image.getWindow().removeWindowListener(this);
		image = null;
		progressBar.setIndeterminate(false);
		
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
			top.setLocation(new Point(0, 0));
			top.setSize(new Dimension(484, 111));
			top.add(conditionSplitLabel, null);
			top.add(getConditionSplit(), null);
			top.add(getConditionValue(), null);
			top.add(getAddConditionMerge(), null);
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
			bottom.setSize(new Dimension(484, 157));
			bottom.setLocation(new Point(0, 267));
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
			center.setSize(new Dimension(485, 156));
			center.setViewportView(getCenterLine());
		}
		return center;
	}
	
	public JPanel createMergeCondition() {
		SplitAndMergeCondition conditionModel = new SplitAndMergeCondition();
		SplitAndMergeConditionView condition = new SplitAndMergeConditionView(this,conditionModel);
		listCondition.add(condition);
		model.getListMergeCondition().add(conditionModel);
		return condition.createMergeCondition();
	}

	/**
	 * This method initializes centerLine	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	public JPanel getCenterLine() {
		if (centerLine == null) {
			centerLine = new JPanel();
			centerLine.setLayout(new BoxLayout(centerLine, BoxLayout.Y_AXIS));
			//centerLine.setLayout(new VerticalBagLayout());
			centerLine.add(createMergeCondition(), null);
		}
		return centerLine;
	}
	
	public void repaintCenterLine() {
		centerLine.removeAll();
		centerLine.paintAll(centerLine.getGraphics());
		Iterator<SplitAndMergeConditionView> it = listCondition.iterator();
		while(it.hasNext()) {
			SplitAndMergeConditionView condition = it.next();
			centerLine.add(condition.getLineConditionMerge());
		}
		centerLine.revalidate();
	}

	/**
	 * This method initializes conditionSplit	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getConditionSplit() {
		if (conditionSplit == null) {
			conditionSplit = new JComboBox(split);
			conditionSplit.setLocation(new Point(234, 20));
			conditionSplit.setSize(new Dimension(160, 24));
		}
		return conditionSplit;
	}
	
	static public int getLineHeight() {		
		return 25;
	}

	public Object[] getMerge() {
		return merge;
	}

	public Object[] getSplit() {
		return split;
	}

	public Object[] getOperator() {
		return operator;
	}

	public SplitAndMergeTool getModel() {
		return model;
	}

	public ArrayList<SplitAndMergeConditionView> getListCondition() {
		return listCondition;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
