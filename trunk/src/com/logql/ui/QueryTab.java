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

    $Id: QueryTab.java,v 1.3 2009/11/01 01:39:06 mreddy Exp $
*/
package com.logql.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.DefaultCategoryDataset;
import org.jfree.data.DefaultPieDataset;

import com.logql.inter.QConnection;
import com.logql.inter.ResultSetMetaLQ;
import com.logql.util.UtilMethods;

public class QueryTab extends JPanel {
	public static final long serialVersionUID = 1;

	ResultSetTable model = new ResultSetTable();
	JTextField query;
	JTable table;
	Statement stmt;
	String schema;
	String lquery;
	JTextField status;
	JCheckBoxMenuItem detailedErros;
	int scrollBarWidth;
	QueryFrame parent;
	boolean activated = true;
	ArrayList<ChartMetaData> charts = new ArrayList<ChartMetaData>();

	public QueryTab(QueryFrame parent){
		stmt = QConnection.createStatement();
		this.parent = parent;
	}

	public void init(Dimension pdim){
		setLayout(new BorderLayout());
		JToolBar jpan = new JToolBar();
		jpan.setFloatable(false);
		JLabel lab=new JLabel("logQL>");
		jpan.add(lab);
		query = new JTextField();
		query.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
					fireQuery();
				}
			}
		});
		jpan.add(query);
		JButton btn = new JButton("Go");
		btn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				fireQuery();
			}
		});
		jpan.add(btn);

		add(jpan,BorderLayout.NORTH);

		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		makeTableSortable();
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent le){
				update();
			}
		});

		JScrollBar vbar = new JScrollBar(JScrollBar.VERTICAL);
		scrollBarWidth = vbar.getPreferredSize().width;
		JScrollPane scroll = new JScrollPane(table);
		add(scroll,BorderLayout.CENTER);
	}

	public void update(){
		for(ChartMetaData cmeta:charts){
			cmeta.compile();
		}
	}

	public static ImageIcon getImage(String fileName) {
		URL url = QueryTab.class.getResource(fileName);
		if (url != null) {
			return new ImageIcon(url);
		} else {
			File f = new File("." + fileName);
			if (f.exists()) {
				try {
					return new ImageIcon(f.toURL());
				} catch (MalformedURLException e) {
				}
			}
		}
		return null;
	}

	protected void makeTableSortable()
	{
		// install table header renderer to draw sort direction icons
		final JTableHeader header = table.getTableHeader();
		final TableCellRenderer origRenderer = header.getDefaultRenderer();
		header.setDefaultRenderer(new TableCellRenderer()
		{
			final Icon downIcon = getImage("/images/sortD16.gif");
			final Icon upIcon = getImage("/images/sortA16.gif");

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				Component c = origRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (c instanceof JLabel == false)
					return c;

				((JLabel)c).setHorizontalTextPosition(SwingConstants.LEFT);

				if (model.getSortColumn() == column)
					((JLabel)c).setIcon(model.isAscending() ? upIcon : downIcon);
				else
					((JLabel)c).setIcon(null);

				// set prefered width
				int width = c.getPreferredSize().width + 2 * table.getColumnModel().getColumnMargin() + 4;
				TableColumn tcolumn = table.getColumnModel().getColumn(column);
				if (tcolumn.getPreferredWidth() < width)
					tcolumn.setPreferredWidth(width);

				return c;
			}
		});

		// sort the table on clicks in the table header
		header.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					int col = table.getColumnModel().getColumnIndexAtX(e.getX());
					if (col >= 0)
						model.sort(col);
					header.repaint();
				}
			}
		});

		// reordering columns currently breaks sorting.
		// can turn this back on when fixed.
		header.setReorderingAllowed(false);
	}

	public void fireQuery(){
		if (UtilMethods._CheckUpdate) {
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					if(UtilMethods.hasNewVersion()){
						JOptionPane.showMessageDialog(
								QueryTab.this,
								"New version of logQL available for download. "+
								"Visit www.logql.com for more details",
								"Info", JOptionPane.INFORMATION_MESSAGE);		
					}
				}
			});
		}
		String squery = query.getText();
		try{
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			ResultSet rs = stmt.executeQuery(squery);
			if(rs!= null){
				lquery = squery;
				model.updateResultSet(rs);
				charts =  new ArrayList<ChartMetaData>();
				ResultSetMetaLQ rmeta = (ResultSetMetaLQ)rs.getMetaData();
				schema= rmeta.getTableName(0);
				Map<String, int[]> err = rmeta.getErrorLines();
				status.setText(UtilMethods.getErrorString(err, detailedErros.isSelected()));
				status.setCaretPosition(0);
				resizeTableColumns();
			} else if(squery.toLowerCase().startsWith("from ")) {
				updateTitle(squery);
				parent.describe();
			}
		}catch(SQLException se){
			JOptionPane.showMessageDialog(this, se.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		}finally{
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public void resizeTableColumns() {
		int width = getWidth() - scrollBarWidth;
		int currWidth = 0;
		for (int i = 0; i < model.getColumnCount(); i++) {
			TableColumn tcolumn = table.getColumnModel().getColumn(i);
			currWidth += tcolumn.getPreferredWidth();
		}
		if (currWidth < width) {
			int perCol = width / model.getColumnCount();

			for (int i = 0; i < model.getColumnCount(); i++) {
				TableColumn tcolumn = table.getColumnModel().getColumn(i);
				if (tcolumn.getPreferredWidth() < perCol)
					tcolumn.setPreferredWidth(perCol);
			}
		}
		table.revalidate();
		table.repaint();
	}

	public void updateTitle(String query){
		String lquery = query.toLowerCase();
		if(lquery.startsWith("from ")){
			int end = lquery.length();
			if(lquery.indexOf(" use ")>-1){
				end = lquery.indexOf(" use ");
			}
			parent.setTitle("logQL - "+query.substring("from ".length(),end).trim());
		}	
	}

	public void fireQuery(String query) throws SQLException {
		stmt.executeQuery(query);
		updateTitle(query);
	}

	public void setStatusBar(JTextField stat){
		status = stat;
	}

	public void setDetailedError(JCheckBoxMenuItem chk) {
		detailedErros = chk;
	}

	public String[] describe() throws SQLException{
		return QConnection.describe(stmt);
	}

	public ResultSetTable getModel() {
		return model;
	}

	public void chart(String c) {
		ChartDataUI cdata;
		cdata = new ChartDataUI(parent, "Chart ");
		cdata.setLocationRelativeTo(this);
		String title = "";
//		if (schema != null) {
//			title = schema;
//			File f = new File(schema);
//			if (f.exists()) {
//				title = schema.substring(f.getParent().length());
//				if (title.startsWith("/") || title.startsWith("\\"))
//					title.substring(1);
//			}
//		}
		cdata.setData(title, c, model, new ChartMetaData(), true);
		cdata.setVisible(true);
		if (cdata.isOkPressed()) {
			ChartMetaData meta = (ChartMetaData) cdata.getMetaData();
			Window jd = null;
			ChartPanel cp = new ChartPanel(meta.chart);

			JDialog diag = new JDialog(parent);
			jd = diag;

			diag.setTitle("logQL: " + title);
			diag.getContentPane().add(cp, BorderLayout.CENTER);
			diag.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			meta.setContainer(jd);
			charts.add(meta);
			meta.compile();
			jd.pack();
			jd.setLocationRelativeTo(this);
			jd.setVisible(true);
		}
	}
	
	public class ChartMetaData {

		JFreeChart chart;
		private int[] categories = new int[0];
		private String[] series = new String[0]; // filled by ChartDataUI
		private int[] cols = new int[0]; // filled by ChartDataUI
		private int type;// dataType
		private int chartType;
		private int transperancy = 0;
		private boolean pie;
		private int orientation;
		private Window container;

		public void setCategories(int[] cat) {
			categories = cat;
		}

		public void setContainer(Window cont) {
			container = cont;
		}

		public void setPie(boolean p) {
			pie = p;
		}

		public void setTransperancy(int t) {
			transperancy = t;
		}

		public void setChartType(int t) {
			type = t;
		}

		public void setChart(JFreeChart c) {
			chart = c;
		}

		public void setType(int t){
			type = t;
		}

		public void setOrientation(int o) {
			orientation = o;
		}

		public void addColumn(int pos, String name) {
			int length = series.length;
			String[] tseries = new String[series.length + 1];
			int[] tcols = new int[cols.length + 1];

			System.arraycopy(series, 0, tseries, 0, length);
			System.arraycopy(cols, 0, tcols, 0, length);
			cols = tcols;
			series = tseries;
			cols[length] = pos;
			series[length] = name;
		}
		
		public JFreeChart getChart(){
			return chart;
		}

		public void removeColumn(int pos) {
			// a column was removed from our source table. if we are displaying
			// the column being removed,
			// remove that column from our display. if that was the only column
			// we need to close. If we are
			// displaying a column higher than the one removed, make
			// adjustments.
			for (int i = cols.length - 1; i >= 0; i--) {
				int col = cols[i];
				if (col == pos) {
					if (cols.length == 1) {
						if (container == null)
							throw new IllegalStateException(
									"no container to close");
						container.setVisible(false);
						container.dispose();
					} else {
						int length = series.length - 1;
						String[] tseries = new String[length];
						int[] tcols = new int[length];
						System.arraycopy(series, 0, tseries, 0, i);
						System.arraycopy(series, i + 1, tseries, i, length - i);
						System.arraycopy(cols, 0, tcols, 0, i);
						System.arraycopy(cols, i + 1, tcols, i, length - i);

						cols = tcols;
						series = tseries;
					}
				} else if (col > pos) {
					--cols[i];
				}
			}
			compile();
		}

		private boolean verifyNotMoreThan100(double[] values) {
			double total = 0;
			for (int i = 0; i < values.length; i++)
				total += values[i];
			if (total > 100)
				return false;
			return true;
		}

		public void compile() {
			int[] sele = table.getSelectedRows();
			if (sele == null || sele.length == 0) {
				sele = new int[table.getRowCount()];
				for (int i = 0; i < sele.length; i++)
					sele[i] = i;
			}
			if (!pie) {
				DefaultCategoryDataset data = new DefaultCategoryDataset();
				int maxData = 500;
				int maxRows = 0x7fffffff;
				if (sele.length * cols.length > maxData) {
					maxRows = maxData / cols.length;
					JOptionPane.showMessageDialog(container,
							"Max limit for charts reached. Only first "
									+ maxRows + " rows will be plotted.",
							"Info", JOptionPane.INFORMATION_MESSAGE);
				}
				double[] values = new double[cols.length];
				for (int j = 0; j < sele.length && j < maxRows; j++) {
					int row = sele[j];
					String category = "";
					for (int i = 0; i < categories.length; i++) {
						if (i == 0)
							category = model.getValueAt(row, categories[i])
									.toString();
						else
							category += " - "
									+ model.getValueAt(row, categories[i])
											.toString();
					}

					if (type == ChartDataUI.PERCENT)
					{
						double total = 0;
						for (int i = 0; i < cols.length; i++)
						{
							Object o=model.getValueAt(row, cols[i]);
							if(o==null)
								o=new Double(0);
							values[i] = ((Number)o ).doubleValue();
							total += values[i];
						}
						if (total == 0)
							continue;
						for (int i = 0; i < values.length; i++)
							values[i] = (values[i] / total) * 100.0;
						if (!verifyNotMoreThan100(values))
							System.out.println(category);
						for (int i = 0; i < cols.length; i++)
						{
							data.addValue(values[i], series[i], category);
						}
					}
					else {
						boolean gZero = true;
						for (int i = 0; i < cols.length; i++) {
							Object o = model.getValueAt(row, cols[i]);
							if (o == null)
								o = new Double(0);
							values[i] = ((Number) o).doubleValue();
							if (values[i] > 0)
								gZero = false;
						}
						if (!gZero)
							for (int i = 0; i < cols.length; i++) {
								if (orientation == ChartDataUI.ORIENTATION_TOP_DOWN)
									data.addValue(values[i], series[i],category);
								else
									data.addValue(values[i], category, series[i]);
							}
					}
				}
				if (sele.length > 75) {
					CategoryAxis domainAxis = ((CategoryPlot) chart.getPlot()).getDomainAxis();
					domainAxis.setVisible(false);
				} else {
					CategoryAxis domainAxis = ((CategoryPlot) chart.getPlot()).getDomainAxis();
					domainAxis.setVisible(true);
					domainAxis.setCategoryLabelPositions(CategoryLabelPositions
							.createUpRotationLabelPositions(Math.PI / 6.0));
				}
				((CategoryPlot) chart.getPlot()).setDataset(data);
				chart.getCategoryPlot().configureRangeAxes();
				chart.getCategoryPlot().configureDomainAxes();
			} else {
				DefaultPieDataset data = new DefaultPieDataset();

				List<Wedge> wedges = new ArrayList<Wedge>();
				double total = 0;
				for (int i = 0; i < sele.length; i++) {
					int row = sele[i];

					Wedge wedge = new Wedge();
					wedge.name = "";

					for (int k = 0; k < categories.length; k++)
						if (k == categories.length - 1) {
							wedge.name += model.getValueAt(row, categories[k])
									.toString();
						} else {
							wedge.name += model.getValueAt(row, categories[k])
									.toString()
									+ " - ";
						}

					wedge.value = (Number) model.getValueAt(row, cols[0]);
					if (wedge.value == null)
						wedge.value = new Double(0);
					wedges.add(wedge);
					total += wedge.value.doubleValue();
				}
				Collections.sort(wedges);

				int maxWedges = 20;

				if (wedges.size() > maxWedges) {
					Wedge superWedge = new Wedge();
					superWedge.name = "All Remaining";

					double value = 0;
					for (int i = wedges.size() - 1; i >= maxWedges; i--) {
						value += ((Wedge) wedges.get(i)).value.doubleValue();
						wedges.remove(i);
					}

					superWedge.value = new Double(value);
					wedges.add(superWedge);
				}

				NumberFormat formatter = NumberFormat.getPercentInstance();
				formatter.setMaximumFractionDigits(2);
				for (int i = 0; i < wedges.size(); i++) {
					Wedge wedge = (Wedge) wedges.get(i);
					double pct = wedge.value.doubleValue() / total;
					String label = wedge.name + " (" + formatter.format(pct)
							+ ")";
					data.setValue(label, wedge.value.doubleValue());
				}

				((PiePlot) chart.getPlot()).setDataset(data);
			}
		}
	}

	private class Wedge implements Comparable<Wedge> {
		String name;
		Number value;

		public int compareTo(Wedge o) {
			if (o == this)
				return 0;
			else if (this.equals(o))
				return 0;
			else if (o == null)
				return -1;
			else {
				Wedge that = o;
				return (int) (that.value.doubleValue() - value.doubleValue());
			}
		}
	}
}
