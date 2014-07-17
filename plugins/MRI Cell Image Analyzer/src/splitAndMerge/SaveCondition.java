package splitAndMerge;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;

public class SaveCondition {

	private JPanel lineConditionMerge;
	private JLabel label;
	private JButton delButton;
	private JComboBox conditionMergeChoice;
	private JTextField conditionMerge;
	private JComboBox logicalOperator = null;
	private JCheckBox notCheckBox = null;
	private JLabel notLabel = null;
	
	public SaveCondition() {
	}
	
	public JPanel createMergeCondition() {
		notLabel = new JLabel();
		notLabel.setBounds(new Rectangle(136, 12, 38, 25));
		notLabel.setText("not :");
		lineConditionMerge = new JPanel();
		lineConditionMerge.setLayout(null);
		lineConditionMerge.setMinimumSize(new Dimension(416, SplitAndMergeToolView.getLineHeight()*2));
		lineConditionMerge.setSize(new Dimension(493, 50));
		lineConditionMerge.setPreferredSize(new Dimension(416, SplitAndMergeToolView.getLineHeight()*2));
		delButton = new JButton("Del");
		label = new JLabel("Condition : ");
		label.setLocation(new Point(67, 12));
		label.setSize(new Dimension(76, SplitAndMergeToolView.getLineHeight()));
		delButton.setBounds(new Rectangle(7, 12, 57, 25));
		delButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
			}
		});
        conditionMergeChoice = new JComboBox();
        conditionMergeChoice.setLocation(new Point(186, 13));
        conditionMergeChoice.setSize(new Dimension(136, 24));
        conditionMergeChoice.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent e) {
			}
		});

        conditionMerge = new JTextField();
        conditionMerge.setBounds(new Rectangle(330, 12, 54, 25));
        conditionMerge.setHorizontalAlignment(JTextField.RIGHT);
        conditionMerge.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent e) {
			}
		});

		lineConditionMerge.add(delButton);
		lineConditionMerge.add(label);
        lineConditionMerge.add(conditionMergeChoice);
        lineConditionMerge.add(conditionMerge);
        lineConditionMerge.add(getLogicalOperator(), null);
        lineConditionMerge.add(getNotCheckBox(), null);
        lineConditionMerge.add(notLabel, null);
        return lineConditionMerge;
	}

	public JPanel getLineConditionMerge() {
		return lineConditionMerge;
	}


	/**
	 * This method initializes logicalOperator	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	public JComboBox getLogicalOperator() {
		if (logicalOperator == null) {
			logicalOperator = new JComboBox();
			logicalOperator.setBounds(new Rectangle(390, 12, 71, 25));
		}
		return logicalOperator;
	}

	public JTextField getConditionMerge() {
		return conditionMerge;
	}

	public JComboBox getConditionMergeChoice() {
		return conditionMergeChoice;
	}

	/**
	 * This method initializes notCheckBox	
	 * 	
	 * @return javax.swing.JCheckBox	
	 */
	private JCheckBox getNotCheckBox() {
		if (notCheckBox == null) {
			notCheckBox = new JCheckBox();
			notCheckBox.setBounds(new Rectangle(162, 12, 21, 25));
			notCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					System.out.println("stateChanged()"); // TODO Auto-generated Event stub stateChanged()
				}
			});
		}
		return notCheckBox;
	}
	
}
