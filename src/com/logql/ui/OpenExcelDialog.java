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

    $Id: OpenExcelDialog.java,v 1.2 2009/10/29 05:11:18 mreddy Exp $
*/
package com.logql.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;

public class OpenExcelDialog extends JDialog implements OpenInterface {
	public static final long serialVersionUID = 1;
	private javax.swing.JButton ivjBtnCancel = null;
	private javax.swing.JButton ivjBtnFile = null;
	private javax.swing.JButton ivjBtnOK = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JLabel ivjLabOptional = null;
	private javax.swing.JLabel ivjLabRange = null;
	private javax.swing.JTextField ivjTxtFile = null;
	private javax.swing.JTextField ivjTxtRange = null;
	private javax.swing.JLabel ivjLabFile = null;

/**
 * OpenExcelDialog constructor comment.
 * @param owner java.awt.Frame
 */
public OpenExcelDialog(java.awt.Frame owner) {
	super(owner, "Open Excel File", true);
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
			constraintsLabFile.ipadx = 26;
			constraintsLabFile.insets = new java.awt.Insets(17, 16, 11, 40);
			getJDialogContentPane().add(getLabFile(), constraintsLabFile);

			java.awt.GridBagConstraints constraintsLabRange = new java.awt.GridBagConstraints();
			constraintsLabRange.gridx = 1; constraintsLabRange.gridy = 2;
			constraintsLabRange.ipadx = 9;
			constraintsLabRange.insets = new java.awt.Insets(9, 15, 0, 6);
			getJDialogContentPane().add(getLabRange(), constraintsLabRange);

			java.awt.GridBagConstraints constraintsTxtFile = new java.awt.GridBagConstraints();
			constraintsTxtFile.gridx = 2; constraintsTxtFile.gridy = 1;
			constraintsTxtFile.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTxtFile.weightx = 1.0;
			constraintsTxtFile.ipadx = 255;
			constraintsTxtFile.insets = new java.awt.Insets(14, 8, 8, 7);
			getJDialogContentPane().add(getTxtFile(), constraintsTxtFile);

			java.awt.GridBagConstraints constraintsTxtRange = new java.awt.GridBagConstraints();
			constraintsTxtRange.gridx = 2; constraintsTxtRange.gridy = 2;
constraintsTxtRange.gridheight = 2;
			constraintsTxtRange.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTxtRange.weightx = 1.0;
			constraintsTxtRange.ipadx = 255;
			constraintsTxtRange.insets = new java.awt.Insets(6, 7, 15, 8);
			getJDialogContentPane().add(getTxtRange(), constraintsTxtRange);

			java.awt.GridBagConstraints constraintsBtnFile = new java.awt.GridBagConstraints();
			constraintsBtnFile.gridx = 3; constraintsBtnFile.gridy = 1;
			constraintsBtnFile.ipadx = -5;
			constraintsBtnFile.insets = new java.awt.Insets(12, 7, 5, 6);
			getJDialogContentPane().add(getBtnFile(), constraintsBtnFile);

			java.awt.GridBagConstraints constraintsLabOptional = new java.awt.GridBagConstraints();
			constraintsLabOptional.gridx = 1; constraintsLabOptional.gridy = 3;
			constraintsLabOptional.ipadx = 7;
			constraintsLabOptional.insets = new java.awt.Insets(1, 13, 3, 26);
			getJDialogContentPane().add(getLabOptional(), constraintsLabOptional);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 4;
			constraintsJPanel1.gridwidth = 3;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.weighty = 1.0;
			constraintsJPanel1.ipadx = 272;
			constraintsJPanel1.ipady = -4;
			constraintsJPanel1.insets = new java.awt.Insets(4, 10, 8, 5);
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
 * Return the TxtFile property value.
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
 * Return the LabOptional property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabOptional() {
	if (ivjLabOptional == null) {
		try {
			ivjLabOptional = new javax.swing.JLabel();
			ivjLabOptional.setName("LabOptional");
			ivjLabOptional.setText("(Optional)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabOptional;
}
/**
 * Return the LabRange property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabRange() {
	if (ivjLabRange == null) {
		try {
			ivjLabRange = new javax.swing.JLabel();
			ivjLabRange.setName("LabRange");
			ivjLabRange.setText("Range name");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabRange;
}
/**
 * Return the TxtField property value.
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
 * Return the TxtRange property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getTxtRange() {
	if (ivjTxtRange == null) {
		try {
			ivjTxtRange = new javax.swing.JTextField();
			ivjTxtRange.setName("TxtRange");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTxtRange;
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
		setName("OpenExcelDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(450, 160);
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
		sb.append(" use excel");
		if (getTxtRange().getText().trim().length() > 0)
			sb.append("('").append(getTxtRange().getText()).append("')");
		return sb.toString();
	}

	public boolean okClicked() {
		return okClick;
	}
	public void openFile(){
		JFileChooser choose = new JFileChooser();
		choose.setMultiSelectionEnabled(true);
		choose.setFileFilter(new FileFilter(){
			public boolean accept(File pathName){
				if(pathName.isDirectory())
					return true;
				return (pathName.getName().endsWith(".xls"));
			}

			public String getDescription() {
				return "Microsoft Excel (*.xls)";
			}
		});
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
