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

    $Id: OpenGeneralDialog.java,v 1.2 2009/10/29 05:11:18 mreddy Exp $
*/
package com.logql.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

import org.xml.sax.SAXException;

import com.logql.meta.Config;

public class OpenGeneralDialog extends JDialog implements OpenInterface{
	public static final long serialVersionUID = 9823;
	private javax.swing.JButton ivjBtnCancel = null;
	private javax.swing.JButton ivjBtnConfigFile = null;
	private javax.swing.JButton ivjBtnFile = null;
	private javax.swing.JButton ivjBtnOk = null;
	private javax.swing.JComboBox ivjJComboBox1 = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JLabel ivjLabConfig = null;
	private javax.swing.JLabel ivjLabConfigFile = null;
	private javax.swing.JLabel ivjLabFile = null;
	private javax.swing.JTextField ivjtxtConfigFile = null;
	private javax.swing.JTextField ivjtxtFrom = null;

	
	public OpenGeneralDialog(java.awt.Frame owner) {
		super(owner, "Open Custom File", true);
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
 * Return the BtnConfigFile property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getBtnConfigFile() {
	if (ivjBtnConfigFile == null) {
		try {
			ivjBtnConfigFile = new javax.swing.JButton();
			ivjBtnConfigFile.setName("BtnConfigFile");
			ivjBtnConfigFile.setText("...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBtnConfigFile;
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
 * Return the BtnOk property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getBtnOk() {
	if (ivjBtnOk == null) {
		try {
			ivjBtnOk = new javax.swing.JButton();
			ivjBtnOk.setName("BtnOk");
			ivjBtnOk.setText("OK");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBtnOk;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBox1() {
	if (ivjJComboBox1 == null) {
		try {
			ivjJComboBox1 = new javax.swing.JComboBox();
			ivjJComboBox1.setName("JComboBox1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBox1;
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
			constraintsLabFile.ipadx = 59;
			constraintsLabFile.insets = new java.awt.Insets(20, 9, 10, 3);
			getJDialogContentPane().add(getLabFile(), constraintsLabFile);

			java.awt.GridBagConstraints constraintsLabConfigFile = new java.awt.GridBagConstraints();
			constraintsLabConfigFile.gridx = 1; constraintsLabConfigFile.gridy = 2;
			constraintsLabConfigFile.ipadx = 20;
			constraintsLabConfigFile.insets = new java.awt.Insets(10, 9, 11, 3);
			getJDialogContentPane().add(getLabConfigFile(), constraintsLabConfigFile);

			java.awt.GridBagConstraints constraintsLabConfig = new java.awt.GridBagConstraints();
			constraintsLabConfig.gridx = 1; constraintsLabConfig.gridy = 3;
			constraintsLabConfig.ipadx = 42;
			constraintsLabConfig.insets = new java.awt.Insets(9, 9, 6, 3);
			getJDialogContentPane().add(getLabConfig(), constraintsLabConfig);

			java.awt.GridBagConstraints constraintstxtFrom = new java.awt.GridBagConstraints();
			constraintstxtFrom.gridx = 2; constraintstxtFrom.gridy = 1;
			constraintstxtFrom.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintstxtFrom.weightx = 1.0;
			constraintstxtFrom.ipadx = 267;
			constraintstxtFrom.insets = new java.awt.Insets(17, 3, 7, 9);
			getJDialogContentPane().add(gettxtFrom(), constraintstxtFrom);

			java.awt.GridBagConstraints constraintstxtConfigFile = new java.awt.GridBagConstraints();
			constraintstxtConfigFile.gridx = 2; constraintstxtConfigFile.gridy = 2;
			constraintstxtConfigFile.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintstxtConfigFile.weightx = 1.0;
			constraintstxtConfigFile.ipadx = 269;
			constraintstxtConfigFile.insets = new java.awt.Insets(7, 3, 8, 7);
			getJDialogContentPane().add(gettxtConfigFile(), constraintstxtConfigFile);

			java.awt.GridBagConstraints constraintsJComboBox1 = new java.awt.GridBagConstraints();
			constraintsJComboBox1.gridx = 2; constraintsJComboBox1.gridy = 3;
			constraintsJComboBox1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBox1.weightx = 1.0;
			constraintsJComboBox1.ipadx = 151;
			constraintsJComboBox1.insets = new java.awt.Insets(5, 3, 1, 3);
			getJDialogContentPane().add(getJComboBox1(), constraintsJComboBox1);

			java.awt.GridBagConstraints constraintsBtnFile = new java.awt.GridBagConstraints();
			constraintsBtnFile.gridx = 3; constraintsBtnFile.gridy = 1;
			constraintsBtnFile.ipadx = -6;
			constraintsBtnFile.insets = new java.awt.Insets(15, 4, 4, 12);
			getJDialogContentPane().add(getBtnFile(), constraintsBtnFile);

			java.awt.GridBagConstraints constraintsBtnConfigFile = new java.awt.GridBagConstraints();
			constraintsBtnConfigFile.gridx = 3; constraintsBtnConfigFile.gridy = 2;
			constraintsBtnConfigFile.ipadx = -6;
			constraintsBtnConfigFile.insets = new java.awt.Insets(5, 4, 5, 12);
			getJDialogContentPane().add(getBtnConfigFile(), constraintsBtnConfigFile);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 4;
			constraintsJPanel1.gridwidth = 3;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.weighty = 1.0;
			constraintsJPanel1.ipadx = 276;
			constraintsJPanel1.insets = new java.awt.Insets(1, 7, 7, 4);
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
			getJPanel1().add(getBtnOk(), getBtnOk().getName());
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
 * Return the LabConfig property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabConfig() {
	if (ivjLabConfig == null) {
		try {
			ivjLabConfig = new javax.swing.JLabel();
			ivjLabConfig.setName("LabConfig");
			ivjLabConfig.setText("Meta");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabConfig;
}
/**
 * Return the LabConfigFile property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabConfigFile() {
	if (ivjLabConfigFile == null) {
		try {
			ivjLabConfigFile = new javax.swing.JLabel();
			ivjLabConfigFile.setName("LabConfigFile");
			ivjLabConfigFile.setText("Meta File");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabConfigFile;
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
 * Return the txtConfigFile property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField gettxtConfigFile() {
	if (ivjtxtConfigFile == null) {
		try {
			ivjtxtConfigFile = new javax.swing.JTextField();
			ivjtxtConfigFile.setName("txtConfigFile");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjtxtConfigFile;
}
/**
 * Return the txtFrom property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField gettxtFrom() {
	if (ivjtxtFrom == null) {
		try {
			ivjtxtFrom = new javax.swing.JTextField();
			ivjtxtFrom.setName("txtFrom");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjtxtFrom;
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
		setName("GeneralDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(426, 185);
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
	getBtnConfigFile().addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			openConfig();
		}
	});
	gettxtConfigFile().addFocusListener(new FocusAdapter(){
		public void focusLost(FocusEvent fe){
			String f = gettxtConfigFile().getText().trim();
			if(f.length() >0){
				File fl = new File(f);
				if(fl.exists() && fl.isFile()){
					readConfig();
				}
			}
		}
	});
	getBtnOk().addActionListener(new ActionListener(){
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
				gettxtFrom().setText(sb.toString());
			}
		}
	}
	public void openConfig(){
		JFileChooser choose = new JFileChooser();
		if(choose.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			gettxtConfigFile().setText(choose.getSelectedFile().toString());
			readConfig();
		}
	}
	public void readConfig(){
		try{
			getJComboBox1().removeAllItems();
			Config c = Config.load(gettxtConfigFile().getText());
			for (String item : c.getConfigNames())
				getJComboBox1().addItem(item);
		}catch(IllegalArgumentException iae){
			JOptionPane.showMessageDialog(this, iae.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			gettxtConfigFile().setText("");
		}catch(SAXException se){
			JOptionPane.showMessageDialog(this, se.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			gettxtConfigFile().setText("");
		}catch(IOException ie){
			JOptionPane.showMessageDialog(this, ie.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			gettxtConfigFile().setText("");
		}
	}
	boolean okClick;

	public boolean okClicked() {
		return okClick;
	}

	public String getFromClause() {
		StringBuffer sb = new StringBuffer();
		sb.append("from ");
		sb.append(gettxtFrom().getText());
		sb.append(" use ");
		sb.append(getJComboBox1().getSelectedItem().toString());
		sb.append("@");
		sb.append(gettxtConfigFile().getText());
		return sb.toString();
	}
}
