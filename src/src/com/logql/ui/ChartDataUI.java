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

    $Id: ChartDataUI.java,v 1.2 2009/10/29 05:11:17 mreddy Exp $
*/
package com.logql.ui;

import java.awt.Color;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.Legend;
import org.jfree.chart.StandardLegend;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.CategoryItemRenderer;

public class ChartDataUI extends JDialog {
	public static final long serialVersionUID = 14;

	private javax.swing.JButton ivjBtnCancel = null;
	private javax.swing.JButton ivjBtnOk = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JLabel ivjLabType = null;
	private javax.swing.JLabel ivjLTransparency = null;
	private javax.swing.JSlider ivjTransparency = null;
	private javax.swing.JComboBox ivjType = null;
	private javax.swing.JTable ivjTable = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JComboBox ivjPlot = null;
	private javax.swing.JLabel ivjLabOrientation = null;
	private javax.swing.JComboBox ivjOrientation = null;
	private javax.swing.JCheckBox ivjJCheckBox1 = null;
/**
 * ChartData constructor comment.
 */
public ChartDataUI() {
	super();
	initialize();
}
/**
 * ChartData constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public ChartDataUI(java.awt.Frame owner, String title) {
	super(owner, title,true);
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
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBox1() {
	if (ivjJCheckBox1 == null) {
		try {
			ivjJCheckBox1 = new javax.swing.JCheckBox();
			ivjJCheckBox1.setName("JCheckBox1");
			ivjJCheckBox1.setText("Open in seperate window");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBox1;
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

			java.awt.GridBagConstraints constraintsLTransparency = new java.awt.GridBagConstraints();
			constraintsLTransparency.gridx = 1; constraintsLTransparency.gridy = 1;
			constraintsLTransparency.ipadx = 22;
			constraintsLTransparency.insets = new java.awt.Insets(11, 11, 3, 1);
			getJDialogContentPane().add(getLTransparency(), constraintsLTransparency);

			java.awt.GridBagConstraints constraintsTransparency = new java.awt.GridBagConstraints();
			constraintsTransparency.gridx = 2; constraintsTransparency.gridy = 1;
			constraintsTransparency.gridwidth = 2;
			constraintsTransparency.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTransparency.weightx = 1.0;
			constraintsTransparency.ipadx = 108;
			constraintsTransparency.insets = new java.awt.Insets(10, 4, 2, 17);
			getJDialogContentPane().add(getTransparency(), constraintsTransparency);

			java.awt.GridBagConstraints constraintsLabType = new java.awt.GridBagConstraints();
			constraintsLabType.gridx = 1; constraintsLabType.gridy = 2;
			constraintsLabType.ipadx = 39;
			constraintsLabType.insets = new java.awt.Insets(7, 9, 6, 7);
			getJDialogContentPane().add(getLabType(), constraintsLabType);

			java.awt.GridBagConstraints constraintsType = new java.awt.GridBagConstraints();
			constraintsType.gridx = 2; constraintsType.gridy = 2;
			constraintsType.gridwidth = 2;
			constraintsType.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsType.weightx = 1.0;
			constraintsType.ipadx = 18;
			constraintsType.insets = new java.awt.Insets(3, 2, 1, 19);
			getJDialogContentPane().add(getType(), constraintsType);

			java.awt.GridBagConstraints constraintsBtnOk = new java.awt.GridBagConstraints();
			constraintsBtnOk.gridx = 1; constraintsBtnOk.gridy = 7;
			constraintsBtnOk.gridwidth = 2;
			constraintsBtnOk.ipadx = 34;
			constraintsBtnOk.insets = new java.awt.Insets(3, 33, 15, 18);
			getJDialogContentPane().add(getBtnOk(), constraintsBtnOk);

			java.awt.GridBagConstraints constraintsBtnCancel = new java.awt.GridBagConstraints();
			constraintsBtnCancel.gridx = 3; constraintsBtnCancel.gridy = 7;
			constraintsBtnCancel.ipadx = 12;
			constraintsBtnCancel.insets = new java.awt.Insets(3, 18, 15, 39);
			getJDialogContentPane().add(getBtnCancel(), constraintsBtnCancel);

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 1; constraintsJScrollPane1.gridy = 5;
			constraintsJScrollPane1.gridwidth = 3;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.ipadx = 228;
			constraintsJScrollPane1.ipady = 98;
			constraintsJScrollPane1.insets = new java.awt.Insets(2, 11, 3, 17);
			getJDialogContentPane().add(getJScrollPane1(), constraintsJScrollPane1);

			java.awt.GridBagConstraints constraintsPlot = new java.awt.GridBagConstraints();
			constraintsPlot.gridx = 2; constraintsPlot.gridy = 3;
			constraintsPlot.gridwidth = 2;
			constraintsPlot.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPlot.weightx = 1.0;
			constraintsPlot.ipadx = 18;
			constraintsPlot.insets = new java.awt.Insets(1, 2, 1, 19);
			getJDialogContentPane().add(getPlot(), constraintsPlot);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 1; constraintsJLabel1.gridy = 3;
			constraintsJLabel1.ipadx = 23;
			constraintsJLabel1.insets = new java.awt.Insets(5, 9, 6, 59);
			getJDialogContentPane().add(getJLabel1(), constraintsJLabel1);

			java.awt.GridBagConstraints constraintsLabOrientation = new java.awt.GridBagConstraints();
			constraintsLabOrientation.gridx = 1; constraintsLabOrientation.gridy = 4;
			constraintsLabOrientation.ipadx = 36;
			constraintsLabOrientation.insets = new java.awt.Insets(5, 8, 7, 6);
			getJDialogContentPane().add(getLabOrientation(), constraintsLabOrientation);

			java.awt.GridBagConstraints constraintsOrientation = new java.awt.GridBagConstraints();
			constraintsOrientation.gridx = 2; constraintsOrientation.gridy = 4;
			constraintsOrientation.gridwidth = 2;
			constraintsOrientation.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOrientation.weightx = 1.0;
			constraintsOrientation.ipadx = 18;
			constraintsOrientation.insets = new java.awt.Insets(1, 1, 2, 20);
			getJDialogContentPane().add(getOrientation(), constraintsOrientation);

			java.awt.GridBagConstraints constraintsJCheckBox1 = new java.awt.GridBagConstraints();
			constraintsJCheckBox1.gridx = 1; constraintsJCheckBox1.gridy = 6;
			constraintsJCheckBox1.gridwidth = 3;
			constraintsJCheckBox1.ipadx = 10;
			constraintsJCheckBox1.insets = new java.awt.Insets(3, 8, 2, 91);
			getJDialogContentPane().add(getJCheckBox1(), constraintsJCheckBox1);
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
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Plot");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
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
			getJScrollPane1().setViewportView(getTable());
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
 * Return the LabOrientation property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabOrientation() {
	if (ivjLabOrientation == null) {
		try {
			ivjLabOrientation = new javax.swing.JLabel();
			ivjLabOrientation.setName("LabOrientation");
			ivjLabOrientation.setText("Orientation");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabOrientation;
}
/**
 * Return the LabType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLabType() {
	if (ivjLabType == null) {
		try {
			ivjLabType = new javax.swing.JLabel();
			ivjLabType.setName("LabType");
			ivjLabType.setText("Chart type");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLabType;
}
/**
 * Return the LTransparency property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLTransparency() {
	if (ivjLTransparency == null) {
		try {
			ivjLTransparency = new javax.swing.JLabel();
			ivjLTransparency.setName("LTransparency");
			ivjLTransparency.setText("Transparency");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLTransparency;
}
/**
 * Return the Orientation property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getOrientation() {
	if (ivjOrientation == null) {
		try {
			ivjOrientation = new javax.swing.JComboBox();
			ivjOrientation.setName("Orientation");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOrientation;
}
/**
 * Return the Plot property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getPlot() {
	if (ivjPlot == null) {
		try {
			ivjPlot = new javax.swing.JComboBox();
			ivjPlot.setName("Plot");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPlot;
}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getTable() {
	if (ivjTable == null) {
		try {
			ivjTable = new javax.swing.JTable();
			ivjTable.setName("Table");
			getJScrollPane1().setColumnHeaderView(ivjTable.getTableHeader());
			ivjTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTable;
}
/**
 * Return the JSlider1 property value.
 * @return javax.swing.JSlider
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSlider getTransparency() {
	if (ivjTransparency == null) {
		try {
			ivjTransparency = new javax.swing.JSlider();
			ivjTransparency.setName("Transparency");
			ivjTransparency.setMaximum(255);
			ivjTransparency.setValue(0);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTransparency;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getType() {
	if (ivjType == null) {
		try {
			ivjType = new javax.swing.JComboBox();
			ivjType.setName("Type");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjType;
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
		setName("ChartData");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(278, 305);
		setContentPane(getJDialogContentPane());
		getJCheckBox1().setVisible(false);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	model=new TModel();
	getTable().setModel(model);
	getType().addItem("Regular");
	getType().addItem("Stacked");
	getType().addItem("100% Stacked");
	
	getPlot().addItem("3D");
	getPlot().addItem("2D");

	getOrientation().addItem("Top - Down");
	getOrientation().addItem("Left - Right");

	getTransparency().setValue(75);

	getBtnOk().addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			if(compileChart()){
				okPressed=true;
				dispose();
			}
		}
	});
	getBtnCancel().addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
			dispose();
		}
	});
	// user code end
}

	public static final int ORIENTATION_TOP_DOWN = 1;
	public static final int ORIENTATION_LEFT_RIGHT = 2;

	public static final int REGULAR=0;
	public static final int STACKED=1;
	public static final int PERCENT=3;

	public static final int PIE=0;
	public static final int COLUMN=1;
	public static final int BAR=2;
	public static final int AREA=3;
	public static final int LINE=4;
	public static final int SCATTER=5;

	TModel model;
	int chartType=1;
	String chartName;
	boolean okPressed;
	QueryTab.ChartMetaData cmd;

	public QueryTab.ChartMetaData getMetaData(){
		return cmd;
	}
	public void setData(String name,String chart,TableModel mod,QueryTab.ChartMetaData meta){
		setData(name,chart,mod,meta,false);
	}
	public void setData(String name,String chart,TableModel mod,QueryTab.ChartMetaData meta,boolean orientation){
		cmd=meta;
		chartName=name;
		String low=chart.toLowerCase().trim();
		if(low.equals("pie"))
			chartType=PIE;
		else if(low.equals("scatter"))
			chartType=SCATTER;
		else if(low.equals("bar"))
			chartType=BAR;
		else if(low.equals("column"))
			chartType=COLUMN;
		else if(low.equals("area"))
			chartType=AREA;
		else
			chartType=LINE;
		
		model.setData(mod);
		if(!orientation || chartType==SCATTER){
			getLabOrientation().setVisible(false);
			getOrientation().setVisible(false);
		}
		if(chartType==PIE){
			getType().setVisible(false);
			getLabType().setVisible(false);
			getLabOrientation().setVisible(false);
			getOrientation().setVisible(false);
		}else if(chartType==AREA){
			getTransparency().setValue(50);
			getPlot().setVisible(false);
//			getType().removeItem("Stacked");
			getJLabel1().setVisible(false);///label for plot
		}else if(chartType==LINE || chartType==SCATTER){
			getPlot().setVisible(false);
			getJLabel1().setVisible(false);
		}
	}
	public boolean seprateWindow(){
		return getJCheckBox1().isSelected();
	}
	
//	Added for reportviewer

	static boolean presetOptions = false;
	static ChartOptions options;
	
	public void setChartOptions(ChartOptions c){
		//System.out.println("Setting char options inside for reuse");
		presetOptions = true;
		options = c;
	}
	
	public boolean initData()
	{
		if(presetOptions){
			model=new TModel();
			//System.out.println("Number of series: "+options.getOptions().length);
			//System.out.println("Selected chart type is "+chartType);
			if(chartType!=PIE){
				//System.out.println("Charting a non-PIE");
				if(chartType == SCATTER){
					System.out.println("Scatter plot not implemented");
				}
				else {
					model.updateData(options.getOptions());
				}
			}else {
				model.updateData(options.getOptions());
			}
		
			getTable().setModel(model);
			getPlot().setSelectedItem(options.getDimension());
			getType().setSelectedItem(options.getType());
		
			getOrientation().setSelectedItem("Top - Down");
			model.setCategories(options.getCategories());
			model.fireTableStructureChanged();
		}else 
			setChartOptions(options);
		
		if(compileChart(true))
			return true;
		else
			return false;
	}
	
	public ChartOptions getChartOptions()
	{
		//System.out.println("Returning options here ");
		return options;
	}
	
	public boolean compileChart()
	{
		return compileChart(false);
	}
	// end of additions for reportviewer
		
	public static class AssignColor extends DefaultDrawingSupplier{
		public static final long serialVersionUID = 1235;

		ArrayList<Color> colors=new ArrayList<Color>();
		int pos=0;
		public AssignColor(int alpha){
			Color[] c=ColorPick.getColors(40,alpha);
			for(int i=0;i<c.length;i++)
				colors.add(c[i]);
		}
		public Paint getNextPaint(){
			if(pos==colors.size())
				pos=0;
			return (Paint)colors.get(pos++);
		}
	}
	public boolean compileChart(boolean val){
//		 Construct savedoptions here to be reused later
		ChartOptions options = new ChartOptions();
		
		Boolean tru=new Boolean(true);
		int series=0;
		ArrayList<String> colNames=new ArrayList<String>();
		ArrayList<Object>[] rows = new ArrayList[model.data.size()];
		for(int i=0;i<model.data.size();i++){
			ArrayList<Object> row=model.data.get(i);

			if(row.get(1).equals(tru)&& !row.get(2).equals(model.ALL_OPT)){
				cmd.addColumn(((Integer)row.get(2)).intValue(),(String)row.get(0));
				colNames.add((String)row.get(0));
				series++;
				
			}
			rows[i]=row;
		}
		options.setOptions(rows);
		
		if(series==0){
			JOptionPane.showMessageDialog(getContentPane(),"Please select a series to plot","Error",JOptionPane.ERROR_MESSAGE);
			return false;
		}else if(chartType==SCATTER && series!=2){
			JOptionPane.showMessageDialog(getContentPane(),"For scatter chart, please select 2 series","Error",JOptionPane.ERROR_MESSAGE);
		}
		int []categories=new int[model.categories.size()];
		for(int i=0;i<model.categories.size();i++){
			categories[i]=((Integer)model.categories.get(i)).intValue();
		}

		options.setCategories(model.categories);
		cmd.setCategories(categories);
		if(chartType==PIE){
			cmd.setPie(true);
			cmd.setTransperancy(getTransparency().getValue()-255);
		}
		cmd.setChartType(chartType);
		options.setChartType(chartType);
		/////////////////////////////////////
		///create chart
		boolean d3=getPlot().getSelectedItem().equals("3D");

		if(d3)
			options.setDimension("3D");
		else
			options.setDimension("2D");
				
		boolean regular=getType().getSelectedItem().equals("Regular");

		if(regular)
			options.setType("Regular");
		else
			options.setType("Stacked");

		switch(chartType){
			case PIE:{ 
				if(d3)
					cmd.setChart(ChartFactory.createPieChart3D(chartName,null,true,true,false));
				else
					cmd.setChart(ChartFactory.createPieChart(chartName,null,true,true,false));
				break;
			}case COLUMN:
			case BAR:{
				PlotOrientation po=chartType==COLUMN?PlotOrientation.HORIZONTAL:PlotOrientation.VERTICAL;

				if(d3 && regular)
					cmd.setChart(ChartFactory.createBarChart3D(chartName,"","",null,po,true,true,false));
				else if(!d3 && regular)
					cmd.setChart(ChartFactory.createBarChart(chartName,"","",null,po,true,true,false));
				else if(d3 && !regular)
					cmd.setChart(ChartFactory.createStackedBarChart3D(chartName,"","",null,po,true,true,false));
				else if(!d3 && !regular)
					cmd.setChart(ChartFactory.createStackedBarChart(chartName,"","",null,po,true,true,false));

				break;
			}case AREA:{
				if(regular)
					cmd.setChart(ChartFactory.createAreaChart(chartName,"","",null,PlotOrientation.VERTICAL,true,true,false));
				else{
					cmd.setChart(ChartFactory.createStackedAreaChart(chartName,"","",null,PlotOrientation.VERTICAL,true,true,false));
					QLStackedAreaRenderer rend=new QLStackedAreaRenderer();
					rend.setToolTipGenerator(new StandardCategoryToolTipGenerator());
					cmd.getChart().getCategoryPlot().setRenderer(rend);
				}
					
				break;
			}case LINE:cmd.setChart(ChartFactory.createLineChart(chartName,"","",null,PlotOrientation.VERTICAL,true,true,false));break;
			case SCATTER:{
				cmd.setChart(ChartFactory.createScatterPlot(chartName,colNames.get(0).toString(),colNames.get(1).toString(),null,PlotOrientation.VERTICAL,true,true,false));
				final Legend legend = cmd.getChart().getLegend();
			    if (legend instanceof StandardLegend) {
			    	final StandardLegend sl = (StandardLegend) legend;
			          sl.setDisplaySeriesShapes(true);
			    }
				
			}
		}
		/////////////////////////////////////
		///Assign the colors
		int transp=255-getTransparency().getValue();
		if(chartType!=PIE && chartType!=SCATTER){
			CategoryItemRenderer rend=((CategoryPlot)cmd.getChart().getPlot()).getRenderer();
			Color[] c=ColorPick.getColors(series,transp);
			for(int i=0;i<c.length;i++){
				rend.setSeriesPaint(i,c[i]);
			}
	        NumberAxis rangeAxis = (NumberAxis) ((CategoryPlot) cmd.getChart().getPlot()).getRangeAxis();
	        rangeAxis.setStandardTickUnits(createIntegerTickUnits());
		}else if(chartType==PIE){
			PiePlot pPlot=(PiePlot)cmd.getChart().getPlot();
//			pPlot.setForegroundAlpha((float)((double)transp/255.0));
//			pPlot.setLabelGenerator(null);
			AssignColor ac=new AssignColor(transp);
			pPlot.setDrawingSupplier(ac);
		}
		////////////////////////////////////
		///chart calculation
		if(chartType!=PIE && chartType!=SCATTER){
			String sele=(String)getType().getSelectedItem();

			if("Regular".equals(sele)){
				cmd.setType(REGULAR);
			}else if("Stacked".equals(sele)){
				cmd.setType(STACKED);
			}
		}
		/////////////////////////////////////
		///Orientation
		if(getOrientation().isVisible()){
			if(getOrientation().getSelectedItem().equals("Top - Down")){
				cmd.setOrientation(ORIENTATION_TOP_DOWN);
			}else{
				cmd.setOrientation(ORIENTATION_LEFT_RIGHT);
			}
		}
		setChartOptions(options);
		return true;
	}

	public class TModel extends AbstractTableModel{
		public static final long serialVersionUID = 876;

		public final Integer ALL_OPT=new Integer(-550);
		String[] header={"Column","Include"};
		Class[] c={String.class,Boolean.class};
		ArrayList<ArrayList<Object>> data=new ArrayList<ArrayList<Object>>();
		ArrayList<Object> categories=new ArrayList<Object>();

		public int getColumnCount() {
			return 2;
		}
		public String getColumnName(int col){
			return header[col];
		}
		public Class<?> getColumnClass(int col){
			return c[col];
		}
	
		public int getRowCount() {
			return data.size();
		}
		public boolean isCellEditable(int row,int col){
			return col==1;
		}
		public Object getValueAt(int row, int col) {
			ArrayList arow=(ArrayList)data.get(row);
			return arow.get(col);
		}
		public void setValueAt(Object value,int row,int col){
			if(col==1){
				ArrayList<Object> arow=data.get(row);
				arow.set(col,value);
				if(chartType==PIE){
					for(int i=0;i<data.size();i++){
						if(i==row)continue;
						arow=data.get(i);
						arow.set(col,new Boolean(false));
					}
				}else{
					if(arow.get(2).equals(ALL_OPT)){
						boolean sele=((Boolean)value).booleanValue();
						for(int i=0;i<data.size();i++){
							arow=data.get(i);
							arow.set(col,new Boolean(sele));
						}						
					}
				}
				fireTableDataChanged();
			}
		}
		public void setData(TableModel model){
			if(chartType!=PIE){
				ArrayList<Object> allOpt=new ArrayList<Object>();
				allOpt.add("All");
				allOpt.add(new Boolean(false));
				allOpt.add(ALL_OPT);
				data.add(allOpt);
			}
			for(int i=0;i<model.getColumnCount();i++){
				// only numbers can be charted
				if(Number.class.isAssignableFrom(model.getColumnClass(i))){
					ArrayList<Object> row=new ArrayList<Object>();
					row.add(model.getColumnName(i));
					row.add(new Boolean(false));
					row.add(new Integer(i));
					data.add(row);
				}else{
					categories.add(new Integer(i));
				}
			}
			fireTableStructureChanged();
		}

		public void updateData(ArrayList<Object>[] a)
		{
			for(int i=0;i<a.length;i++){
				data.add(a[i]);
			}
			
		}

		public void setCategories(ArrayList<Object> a)
		{
			categories = a;
		}

		public ArrayList getCategories()
		{
			return categories;
		}
	}
	/**
	 * @return Returns the okPressed.
	 */
	public boolean isOkPressed() {
		return okPressed;
	}
	/**
	 * @param okPressed The okPressed to set.
	 */
	public void setOkPressed(boolean okPressed) {
		this.okPressed = okPressed;
	}
    public static TickUnitSource createIntegerTickUnits() {

        TickUnits units = new TickUnits();

        units.add(new NumberTickUnit(1,              new DecimalFormat("0")));
        units.add(new NumberTickUnit(2,              new DecimalFormat("0")));
        units.add(new NumberTickUnit(5,              new DecimalFormat("0")));
        units.add(new NumberTickUnit(10,             new DecimalFormat("0")));
        units.add(new NumberTickUnit(20,             new DecimalFormat("0")));
        units.add(new NumberTickUnit(50,             new DecimalFormat("0")));
        units.add(new NumberTickUnit(100,            new DecimalFormat("0")));
        units.add(new NumberTickUnit(200,            new DecimalFormat("0")));
        units.add(new NumberTickUnit(500,            new DecimalFormat("0")));
        units.add(new NumberTickUnit(1000,           new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(2000,           new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(5000,           new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(10000,          new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(20000,          new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(50000,          new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(100000,         new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(200000,         new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(500000,         new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(1000000,        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(2000000,        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(5000000,        new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(10000000,       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(20000000,       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(50000000,       new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(100000000,      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(200000000,      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(500000000,      new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(1000000000,     new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(2000000000,     new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(5000000000.0,   new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(10000000000.0,  new DecimalFormat("#,##0")));
        //-----------------
        units.add(new NumberTickUnit(10000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(20000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(40000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(80000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(160000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(320000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(640000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(1280000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(2560000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(5120000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(10240000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(20480000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(40960000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(81920000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(163840000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(327680000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(655360000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(1310720000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(2621440000000000.0,  new DecimalFormat("#,##0")));
        units.add(new NumberTickUnit(5242880000000000.0,  new DecimalFormat("#,##0")));


        return units;

    }
}
