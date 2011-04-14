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
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
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

	public QueryTab(QueryFrame parent){
		stmt = QConnection.createStatement();
		this.parent = parent;
	}

	public void init(Dimension pdim, boolean hideQuery){
		setLayout(new BorderLayout());
		JToolBar jpan = new JToolBar();
		jpan.setFloatable(false);
		if (!hideQuery) {
			JLabel lab = new JLabel("logQL>");
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
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					fireQuery();
				}
			});
			jpan.add(btn);

			add(jpan, BorderLayout.NORTH);
		}
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		makeTableSortable();

		JScrollBar vbar = new JScrollBar(JScrollBar.VERTICAL);
		scrollBarWidth = vbar.getPreferredSize().width;
		JScrollPane scroll = new JScrollPane(table);
		add(scroll,BorderLayout.CENTER);
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
			f = new File("../logql/"+fileName);
			if(f.exists()){
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

	public void fireQuery() {
		fireQuery(query.getText());
	}

	public void fireQuery(String squery) {
		try{
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			ResultSet rs = stmt.executeQuery(squery);
			if(rs!= null){
				lquery = squery;
				model.updateResultSet(rs);
				ResultSetMetaLQ rmeta = (ResultSetMetaLQ)rs.getMetaData();
				schema= rmeta.getTableName(0);
				if (!parent.hideQuery) {
					Map<String, List<Integer>> err = rmeta.getErrorLines();
					status.setText(UtilMethods.getErrorString(err,
							detailedErros.isSelected()));
					status.setCaretPosition(0);
				}
				resizeTableColumns();
			} else if(squery.toLowerCase().startsWith("from ")) {
				updateTitle(squery);
				parent.describe();
			}
		}catch(SQLException se){
			JOptionPane.showMessageDialog(this, se.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
			se.printStackTrace();
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

	public void executeQuery(String query) throws SQLException {
		stmt.executeQuery(query);
		updateTitle(query);
	}

	public void setStatusBar(JTextField stat){
		status = stat;
	}

	public void setDetailedError(JCheckBoxMenuItem chk) {
		detailedErros = chk;
		detailedErros.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UtilMethods._ErrorDetails = ((JCheckBoxMenuItem)e.getSource()).isSelected();
			}
		});
	}

	public String[] describe() throws SQLException{
		return QConnection.describe(stmt);
	}

	public ResultSetTable getModel() {
		return model;
	}
}
