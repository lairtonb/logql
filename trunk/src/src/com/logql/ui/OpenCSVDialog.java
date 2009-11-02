/*
    Copyright 2006 Manmohan Reddy

    This file is part of logQL.

    logQL is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    logQL is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with logQL.  If not, see <http://www.gnu.org/licenses/>.

    $Id: OpenCSVDialog.java,v 1.2 2009/10/29 05:11:18 mreddy Exp $
*/
package com.logql.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.border.LineBorder;

public class OpenCSVDialog extends JDialog implements OpenInterface {
	public static final long serialVersionUID= 324;
	private javax.swing.JButton ivjBtnCancel = null;
	private javax.swing.JButton ivjBtnFile = null;
	private javax.swing.JButton ivjBtnOK = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JLabel ivjLabFile = null;
	private javax.swing.JLabel ivjLabHeader = null;
	private javax.swing.JTextField ivjTxtFile = null;
	private javax.swing.JTextField ivjTxtHeaderLine = null;

/**
 * OpenCSVDialog constructor comment.
 * @param owner java.awt.Frame
 */
public OpenCSVDialog(java.awt.Frame owner) {
	super(owner, "Open CSV File", true);
	initialize();
}

/**
 * Return the BtnCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getBtnCancel() {
	if (ivjBtnCancel == null) {
		try {
			ivjBtnCancel = new javax.swing.JButton();
			ivjBtnCancel.setName("BtnCancel");
			ivjBtnCancel.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBtnCancel;
}
/**
 * Return the BtnFile property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getBtnFile() {
	if (ivjBtnFile == null) {
		try {
			ivjBtnFile = new javax.swing.JButton();
			ivjBtnFile.setName("BtnFile");
			ivjBtnFile.setText("...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBtnFile;
}
/**
 * Return the BtnOK property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getBtnOK() {
	if (ivjBtnOK == null) {
		try {
			ivjBtnOK = new javax.swing.JButton();
			ivjBtnOK.setName("BtnOK");
			ivjBtnOK.setText("OK");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBtnOK;
}
/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsLabFile = new java.awt.GridBagConstraints();
			constraintsLabFile.gridx = 1; constraintsLabFile.gridy = 1;
			constraintsLabFile.ipadx = 68;
			constraintsLabFile.insets = new java.awt.Insets(19, 14, 12, 272);
			getJDialogContentPane().add(getLabFile(), constraintsLabFile);

			java.awt.GridBagConstraints constraintsLabHeader = new java.awt.GridBagConstraints();
			constraintsLabHeader.gridx = 1; constraintsLabHeader.gridy = 2;
			constraintsLabHeader.ipadx = 19;
			constraintsLabHeader.insets = new java.awt.Insets(10, 13, 7, 273);
			getJDialogContentPane().add(getLabHeader(), constraintsLabHeader);

			java.awt.GridBagConstraints constraintsTxtFile = new java.awt.GridBagConstraints();
			constraintsTxtFile.gridx = 1; constraintsTxtFile.gridy = 1;
			constraintsTxtFile.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTxtFile.weightx = 1.0;
			constraintsTxtFile.ipadx = 266;
			constraintsTxtFile.insets = new java.awt.Insets(15, 98, 10, 5);
			getJDialogContentPane().add(getTxtFile(), constraintsTxtFile);

			java.awt.GridBagConstraints constraintsTxtHeaderLine = new java.awt.GridBagConstraints();
			constraintsTxtHeaderLine.gridx = 1; constraintsTxtHeaderLine.gridy = 2;
			constraintsTxtHeaderLine.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTxtHeaderLine.weightx = 1.0;
			constraintsTxtHeaderLine.ipadx = 266;
			constraintsTxtHeaderLine.insets = new java.awt.Insets(6, 98, 5, 5);
			getJDialogContentPane().add(getTxtHeaderLine(), constraintsTxtHeaderLine);

			java.awt.GridBagConstraints constraintsBtnFile = new java.awt.GridBagConstraints();
			constraintsBtnFile.gridx = 2; constraintsBtnFile.gridy = 1;
			constraintsBtnFile.ipadx = -6;
			constraintsBtnFile.insets = new java.awt.Insets(14, 5, 6, 11);
			getJDialogContentPane().add(getBtnFile(), constraintsBtnFile);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 3;
			constraintsJPanel1.gridwidth = 2;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.weighty = 1.0;
			constraintsJPanel1.ipadx = 275;
			constraintsJPanel1.ipady = 2;
			constraintsJPanel1.insets = new java.awt.Insets(6, 8, 14, 4);
			getJDialogContentPane().add(getJPanel1(), constraintsJPanel1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.FlowLayout(FlowLayout.RIGHT));
			getJPanel1().add(getBtnOK(), getBtnOK().getName());
			getJPanel1().add(getBtnCancel(), getBtnCancel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the LabFile property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabFile() {
	if (ivjLabFile == null) {
		try {
			ivjLabFile = new javax.swing.JLabel();
			ivjLabFile.setName("LabFile");
			ivjLabFile.setText("File");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabFile;
}
/**
 * Return the LabHeader property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabHeader() {
	if (ivjLabHeader == null) {
		try {
			ivjLabHeader = new javax.swing.JLabel();
			ivjLabHeader.setName("LabHeader");
			ivjLabHeader.setText("Header Line");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabHeader;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getTxtFile() {
	if (ivjTxtFile == null) {
		try {
			ivjTxtFile = new javax.swing.JTextField();
			ivjTxtFile.setName("TxtFile");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTxtFile;
}
/**
 * Return the JTextField11 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getTxtHeaderLine() {
	if (ivjTxtHeaderLine == null) {
		try {
			ivjTxtHeaderLine = new javax.swing.JTextField();
			ivjTxtHeaderLine.setName("TxtHeaderLine");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTxtHeaderLine;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("OpenCSVDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(426, 160);
		setContentPane(getJDialogContentPane());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getJPanel1().setBorder(new LineBorder(getBackground().brighter()));

	getBtnFile().addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			openFile();
		}
	});
	getTxtHeaderLine().setText("1");
	getBtnOK().addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			okClick = true;
			setVisible(false);
		}
	});
	getBtnCancel().addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			okClick = false;
			setVisible(false);
		}
	});
	// user code end
}
	private boolean okClick;
	public String getFromClause() {
		StringBuffer sb = new StringBuffer();
		sb.append("from ");
		sb.append(getTxtFile().getText());
		sb.append(" use csv(").append(getTxtHeaderLine().getText()).append(")");
		return sb.toString();
	}

	public boolean okClicked() {
		return okClick;
	}
	public void openFile(){
		JFileChooser choose = new JFileChooser();
		choose.setMultiSelectionEnabled(true);
		if(choose.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			File[] files = choose.getSelectedFiles();
			if (files.length > 0) {
				StringBuffer sb = new StringBuffer();
				sb.append(files[0]);
				for(int i = 1; i < files.length;i++){
					sb.append(",").append(files[i]);
				}
				getTxtFile().setText(sb.toString());
			}
		}
	}
}
