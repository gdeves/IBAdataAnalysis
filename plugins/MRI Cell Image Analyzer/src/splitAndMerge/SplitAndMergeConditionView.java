package splitAndMerge;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SplitAndMergeConditionView {
	
	private SplitAndMergeToolView splitAndMergeToolView;
	private JPanel lineConditionMerge;
	private JLabel label;
	private JButton delButton;
	private JComboBox conditionMergeChoice;
	private JTextField conditionMerge;
	private JComboBox logicalOperator = null;
	private SplitAndMergeCondition model;
	private JLabel notLabel;
	private JCheckBox notCheckBox;
	
	public SplitAndMergeConditionView(SplitAndMergeToolView localSplitAndMergeToolView, SplitAndMergeCondition localModel) {
		splitAndMergeToolView = localSplitAndMergeToolView;
		model = localModel;
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
		delButton.setBounds(new Rectangle(7, 12, 57, SplitAndMergeToolView.getLineHeight()));
		delButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				splitAndMergeToolView.getModel().getListMergeCondition().remove(model);
				splitAndMergeToolView.getListCondition().remove(getCurrent());
				splitAndMergeToolView.repaintCenterLine();
			}
		});
        conditionMergeChoice = new JComboBox(splitAndMergeToolView.getMerge());
        conditionMergeChoice.setLocation(new Point(186, 13));
        conditionMergeChoice.setSize(new Dimension(136, 24));
        conditionMergeChoice.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent e) {
        		model.setCondition(conditionMergeChoice.getSelectedItem().toString());
			}
		});

        conditionMerge = new JTextField();
        conditionMerge.setBounds(new Rectangle(330, 12, 54, 25));
        conditionMerge.setHorizontalAlignment(JTextField.RIGHT);
        conditionMerge.addFocusListener(new java.awt.event.FocusAdapter() {
			public void focusLost(java.awt.event.FocusEvent e) {
				if(!conditionMerge.getText().equals(""))
					model.setValue(Integer.parseInt(conditionMerge.getText()));
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
	
	public SplitAndMergeConditionView getCurrent() {
		return this;
	}

	/**
	 * This method initializes logicalOperator	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	public JComboBox getLogicalOperator() {
		if (logicalOperator == null) {
			logicalOperator = new JComboBox(splitAndMergeToolView.getOperator());
			logicalOperator.setBounds(new Rectangle(390, 12, 71, 25));
			logicalOperator.addItemListener(new java.awt.event.ItemListener() {
	        	public void itemStateChanged(java.awt.event.ItemEvent e) {
	        		model.setOperator(logicalOperator.getSelectedItem().toString());
	        	}
	        });
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
			notCheckBox = new JCheckBox("not");
			notCheckBox.setBounds(new Rectangle(162, 12, 21, 25));
			notCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					model.setNot(notCheckBox.isSelected());
				}
			});
		}
		return notCheckBox;
	}
}
