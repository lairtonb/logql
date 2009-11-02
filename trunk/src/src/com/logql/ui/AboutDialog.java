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

    $Id: AboutDialog.java,v 1.2 2009/10/29 05:11:18 mreddy Exp $
*/
package com.logql.ui;

import java.awt.FlowLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;

public class AboutDialog extends JDialog {
	public static final long serialVersionUID=1;
	private javax.swing.JButton ivjBtnOk = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JTabbedPane ivjJTabbedPane1 = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JLabel ivjLabEmail = null;
	private javax.swing.JLabel ivjLabLogo = null;
	private javax.swing.JLabel ivjLabValEmail = null;
	private javax.swing.JLabel ivjLabValName = null;
	private javax.swing.JLabel ivjLabBuild = null;
	private javax.swing.JLabel ivjLabName = null;
	private javax.swing.JLabel ivjLabRegistered = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JPanel ivjDiskViz = null;
	private javax.swing.JPanel ivjPanCredits = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == AboutDialog.this.getBtnOk()) 
				connEtoM1(e);
		};
	};
	private javax.swing.JTable ivjScrollPaneTable = null;

/**
 * AboutDialog constructor comment.
 * @param owner java.awt.Frame
 */
public AboutDialog(java.awt.Frame owner) {
	super(owner,"About");
	initialize();
}
/**
 * connEtoM1:  (BtnOk.action.actionPerformed(java.awt.event.ActionEvent) --> AboutDialog.dispose()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dispose();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
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
			ivjBtnOk.setText("Ok");
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
 * Return the Page property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDiskViz() {
	if (ivjDiskViz == null) {
		try {
			ivjDiskViz = new javax.swing.JPanel();
			ivjDiskViz.setName("logQL");
			ivjDiskViz.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsLabName = new java.awt.GridBagConstraints();
			constraintsLabName.gridx = 1; constraintsLabName.gridy = 3;
			constraintsLabName.anchor = java.awt.GridBagConstraints.EAST;
			constraintsLabName.ipadx = 3;
			constraintsLabName.insets = new java.awt.Insets(2, 51, 5, 5);
			getDiskViz().add(getLabName(), constraintsLabName);

			java.awt.GridBagConstraints constraintsLabLogo = new java.awt.GridBagConstraints();
			constraintsLabLogo.gridx = 1; constraintsLabLogo.gridy = 1;
			constraintsLabLogo.gridwidth = 2;
			constraintsLabLogo.insets = new java.awt.Insets(7, 4, 5, 1);
			getDiskViz().add(getLabLogo(), constraintsLabLogo);

			java.awt.GridBagConstraints constraintsLabRegistered = new java.awt.GridBagConstraints();
			constraintsLabRegistered.gridx = 1; constraintsLabRegistered.gridy = 2;
			constraintsLabRegistered.ipadx = 13;
			constraintsLabRegistered.insets = new java.awt.Insets(5, 5, 1, 1);
			getDiskViz().add(getLabRegistered(), constraintsLabRegistered);

			java.awt.GridBagConstraints constraintsLabValName = new java.awt.GridBagConstraints();
			constraintsLabValName.gridx = 2; constraintsLabValName.gridy = 3;
			constraintsLabValName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLabValName.ipadx = 1;
			constraintsLabValName.insets = new java.awt.Insets(2, 2, 5, 153);
			getDiskViz().add(getLabValName(), constraintsLabValName);

			java.awt.GridBagConstraints constraintsLabEmail = new java.awt.GridBagConstraints();
			constraintsLabEmail.gridx = 1; constraintsLabEmail.gridy = 4;
			constraintsLabEmail.anchor = java.awt.GridBagConstraints.EAST;
			constraintsLabEmail.ipadx = 2;
			constraintsLabEmail.insets = new java.awt.Insets(5, 50, 19, 9);
			getDiskViz().add(getLabEmail(), constraintsLabEmail);

			java.awt.GridBagConstraints constraintsLabValEmail = new java.awt.GridBagConstraints();
			constraintsLabValEmail.gridx = 2; constraintsLabValEmail.gridy = 4;
			constraintsLabValEmail.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLabValEmail.ipadx = 1;
			constraintsLabValEmail.insets = new java.awt.Insets(5, 2, 19, 155);
			getDiskViz().add(getLabValEmail(), constraintsLabValEmail);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDiskViz;
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
			ivjJDialogContentPane.setLayout(new java.awt.BorderLayout());
			getJDialogContentPane().add(getJTabbedPane1(), "Center");
			getJDialogContentPane().add(getJPanel1(), "South");
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
			ivjJPanel1.setBorder(new javax.swing.border.LineBorder(getJPanel1().getBackground().darker()));
			ivjJPanel1.setLayout(new FlowLayout(FlowLayout.RIGHT));
//			getJPanel1().add(getLabBuild(), getLabBuild().getName());
//			getJPanel1().add(Box.createHorizontalGlue());
			getJPanel1().add(getBtnOk());
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
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPane1().setViewportView(getScrollPaneTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getJTabbedPane1() {
	if (ivjJTabbedPane1 == null) {
		try {
			ivjJTabbedPane1 = new javax.swing.JTabbedPane();
			ivjJTabbedPane1.setName("JTabbedPane1");
			ivjJTabbedPane1.insertTab("logQL", null, getDiskViz(), null, 0);
			ivjJTabbedPane1.insertTab("Credits", null, getPanCredits(), null, 1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane1;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabBuild() {
	if (ivjLabBuild == null) {
		try {
			ivjLabBuild = new javax.swing.JLabel();
			ivjLabBuild.setName("LabBuild");
			ivjLabBuild.setText("Build: 20090603   Version: 1.3");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabBuild;
}
/**
 * Return the LabEmail property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabEmail() {
	if (ivjLabEmail == null) {
		try {
			ivjLabEmail = new javax.swing.JLabel();
			ivjLabEmail.setName("LabEmail");
			ivjLabEmail.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
			ivjLabEmail.setText("Email: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabEmail;
}
/**
 * Return the LabLogo property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabLogo() {
	if (ivjLabLogo == null) {
		try {
			ivjLabLogo = new javax.swing.JLabel();
			ivjLabLogo.setName("LabLogo");
			ivjLabLogo.setText("JLabel2");
			ivjLabLogo.setMaximumSize(new java.awt.Dimension(300, 200));
			ivjLabLogo.setMinimumSize(new java.awt.Dimension(300, 200));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabLogo;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabName() {
	if (ivjLabName == null) {
		try {
			ivjLabName = new javax.swing.JLabel();
			ivjLabName.setName("LabName");
			ivjLabName.setAlignmentX(java.awt.Component.RIGHT_ALIGNMENT);
			ivjLabName.setText("Name: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabName;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabRegistered() {
	if (ivjLabRegistered == null) {
		try {
			ivjLabRegistered = new javax.swing.JLabel();
			ivjLabRegistered.setName("LabRegistered");
			ivjLabRegistered.setText("Registered to:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabRegistered;
}
/**
 * Return the LabValEmail property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabValEmail() {
	if (ivjLabValEmail == null) {
		try {
			ivjLabValEmail = new javax.swing.JLabel();
			ivjLabValEmail.setName("LabValEmail");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabValEmail;
}
/**
 * Return the LabValName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabValName() {
	if (ivjLabValName == null) {
		try {
			ivjLabValName = new javax.swing.JLabel();
			ivjLabValName.setName("LabValName");
			ivjLabValName.setText("ValName");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabValName;
}
/**
 * Return the PanCredits property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getPanCredits() {
	if (ivjPanCredits == null) {
		try {
			ivjPanCredits = new javax.swing.JPanel();
			ivjPanCredits.setName("PanCredits");
			ivjPanCredits.setLayout(new java.awt.BorderLayout());
			getPanCredits().add(getJScrollPane1(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPanCredits;
}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new javax.swing.JTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
//			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getBtnOk().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AboutDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(310, 350);
		setContentPane(getJDialogContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code begin {2}
//	Color c=new Color(0xd1d1d1);
//	getContentPane().setBackground(c);
//	getDiskViz().setBackground(c);
//	getJPanel1().setBackground(c);
//	getPanCredits().setBackground(c);
//	getLabEmail().setVisible(false);
//	getLabName().setVisible(false);
//	getLabValEmail().setVisible(false);
	getLabValName().setVisible(false);

	getLabRegistered().setText(" ");
	getLabName().setText(" ");
	getLabEmail().setText(" ");

	try{
		ImageIcon splash=new ImageIcon(AboutDialog.class.getResource("/images/splash.gif"));
		getLabLogo().setIcon(splash);
	}catch(Exception e){
		//Should never happen
	}

	//credits
	Object[] head={"Package name","URL"};
	Object[][] data={{"JFreeChart","http://www.jfree.org"},
			{"Apache POI ","http://poi.apache.org/"}
	};
	
	((DefaultTableModel)getScrollPaneTable().getModel()).setDataVector(data,head);
	getLabBuild().setVisible(false);
	setResizable(false);

// user code end
}

}
