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

    $Id: ResultSetTable.java,v 1.2 2009-10-29 05:11:18 mreddy Exp $
*/
package com.logql.ui;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.table.AbstractTableModel;

import com.logql.inter.ResultSetMetaLQ;

public class ResultSetTable extends AbstractTableModel {
	public static final long serialVersionUID = 823689;

	ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
	int colCount = 0;
	int sortCol =-1;
	boolean ascending;
	String[] header;
	int[] jcolTypes;
	Class<?>[] colTypes;

	public void updateResultSet(ResultSet rs) throws SQLException{
		ResultSetMetaLQ meta = (ResultSetMetaLQ)rs.getMetaData();
		colCount = meta.getColumnCount();

		data = new ArrayList<ArrayList<Object>>();
		header = new String[colCount];
		colTypes = new Class[colCount];
		jcolTypes = new int[colCount];

		for (int i = 1; i <= colCount; i++) {
			header[i - 1] = meta.getColumnLabel(i);
			colTypes[i - 1] = getColClass(meta.getColumnType(i));
			jcolTypes[i - 1] = meta.getColumnType(i);
		}

		while (rs.next()) {
			ArrayList<Object> row = new ArrayList<Object>();
			boolean emptyRow=true;
			//filter out empty rows, in excel we can have empty cells
			//and there's no easy way for the query engine to filter these.
			for (int i = 1; i <= colCount; i++) {
				Object obj = getObject(i, rs);
				if(obj != null)
					if(obj instanceof String) {
						if(((String)obj).length() > 0)
							emptyRow = false;
					} else
						emptyRow = false;
				row.add(getObject(i, rs));
			}
			if (!emptyRow) {
				data.add(row);
			}
		}
		sortCol = -1;
		ascending = false;
		fireTableStructureChanged();
	}

	protected Class<?> getColClass(int colType){
		switch (colType) {
		case Types.VARCHAR:
			return String.class;
		case Types.INTEGER:
			return Integer.class;
		case Types.DATE:
			return DateEncaps.class;
		case Types.BIGINT:
			return Long.class;
		case Types.FLOAT:
			return Float.class;
		case Types.DECIMAL:
			return Double.class;
		}
		return null;
	}

	protected Object getObject(int colNum, ResultSet rs) throws SQLException {
		switch (jcolTypes[colNum - 1]) {
		case Types.VARCHAR:
			return rs.getString(colNum);
		case Types.INTEGER:
			return new Integer(rs.getInt(colNum));
		case Types.DATE:
			return new DateEncaps(rs.getString(colNum), rs.getDate(colNum));
		case Types.BIGINT:
			return new Long(rs.getLong(colNum));
		case Types.FLOAT:
			return new Float(rs.getLong(colNum));
		case Types.DECIMAL:
			return new Double(rs.getDouble(colNum));
		}
		return null;
	}

	public Class<?> getColumnClass(int col) {
		return colTypes[col];
	}

	public String getColumnName (int col){
		return header[col];
	}

	public int getColumnCount() {
		return colCount;
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int row, int col) {
		return data.get(row).get(col);
	}

	public int getSortColumn(){
		return sortCol;
	}

	public boolean isAscending(){
		return ascending;
	}

	public void sort(int col){
		if(sortCol == col)
			ascending = !ascending;
		else
			ascending = false;
		sortCol = col;
		Comparator<ArrayList<Object>> comp = null;
		switch(jcolTypes[sortCol]){
		case Types.VARCHAR:
			comp = new Comparator<ArrayList<Object>>(){
				public int compare(ArrayList<Object> o1, ArrayList<Object> o2){
					return ((Comparable<String>)o1.get(sortCol)).compareTo((String)o2.get(sortCol));
				}
			};
			break;
		case Types.INTEGER:
			comp = new Comparator<ArrayList<Object>>(){
				public int compare(ArrayList<Object> o1, ArrayList<Object> o2){
					return ((Comparable<Integer>)o1.get(sortCol)).compareTo((Integer)o2.get(sortCol));
				}
			};
			break;
		case Types.DATE:
			comp = new Comparator<ArrayList<Object>>(){
				public int compare(ArrayList<Object> o1, ArrayList<Object> o2){
					return ((Comparable<DateEncaps>)o1.get(sortCol)).compareTo((DateEncaps)o2.get(sortCol));
				}
			};
			break;
		case Types.BIGINT:
			comp = new Comparator<ArrayList<Object>>(){
				public int compare(ArrayList<Object> o1, ArrayList<Object> o2){
					return ((Comparable<Long>)o1.get(sortCol)).compareTo((Long)o2.get(sortCol));
				}
			};
			break;
		case Types.FLOAT:
			comp = new Comparator<ArrayList<Object>>(){
				public int compare(ArrayList<Object> o1, ArrayList<Object> o2){
					return ((Comparable<Float>)o1.get(sortCol)).compareTo((Float)o2.get(sortCol));
				}
			};
			break;

		case Types.DECIMAL:
			comp = new Comparator<ArrayList<Object>>(){
				public int compare(ArrayList<Object> o1, ArrayList<Object> o2){
					return ((Comparable<Double>)o1.get(sortCol)).compareTo((Double)o2.get(sortCol));
				}
			};
			break;
		}
		
		if(comp == null){
			sortCol = -1;
			return;
		}
		if(!ascending){
			comp = new NegComp(comp);
		}
		Collections.sort(data, comp);

		fireTableDataChanged();
	}

	private class NegComp implements Comparator<ArrayList<Object>>{
		Comparator<ArrayList<Object>> comp;
		public NegComp(Comparator<ArrayList<Object>> c){
			comp = c;
		}
		public int compare(ArrayList<Object> o1, ArrayList<Object>o2){
			return comp.compare(o2, o1);
		}
	}
	public static class DateEncaps implements Comparable<DateEncaps> {
		String val;
		Date date;

		public DateEncaps(String v, Date d) {
			val = v;
			date = d;
		}

		public int compareTo(DateEncaps de) {
			return date.compareTo(de.date);
		}

		public String toString(){
			return val;
		}
	}
}
