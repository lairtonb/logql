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

    $Id: ResultSetMetaImpl.java,v 1.2 2009-10-29 05:11:07 mreddy Exp $
*/
package com.logql.interpret;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.logql.inter.ResultSetMetaLQ;
import com.logql.interpret.func.SelectFunction;
import com.logql.meta.FieldMeta;

public class ResultSetMetaImpl implements ResultSetMetaLQ {

	String fname;
	String sname;
	ArrayList<ColumnData> cdata = new ArrayList<ColumnData>();
	HashMap<String, ColumnData> nameMap=new HashMap<String, ColumnData>();
	Map<String, List<Integer>> errors;
	int lineCount;

	public ResultSetMetaImpl(SelectMeta smeta) {
		this.fname = smeta.getFromClause();
		this.sname = smeta.getMeta().getName();
		if (smeta.getFunctions() != null)
			for (SelectFunction sf : smeta.getFunctions())
				cdata.add(new ColumnData(sf));

		if (smeta.getMetricFunctions() != null)
			for (SelectFunction sf : smeta.getMetricFunctions())
				cdata.add(new ColumnData(sf));
		
		for(ColumnData cd:cdata){
			if(cd.label != null)
				nameMap.put(cd.label, cd);
			else
				nameMap.put(cd.name, cd);
		}
		
		Collections.sort(cdata, new Comparator<ColumnData>() {
			public int compare(ColumnData c1, ColumnData c2) {
				return c1.colPos - c2.colPos;
			}
		});
	}
	
	public int getColumnPos(String name) {
		ColumnData cd = nameMap.get(name);
		if (cd != null)
			return cd.colPos;
		return -1;
	}

	public ColumnData getColumnData(int pos) {
		return cdata.get(pos - 1);
	}

	public String getColumnLabel(int col) throws SQLException {
		if (col <= cdata.size()) {
			return cdata.get(col - 1).label == null ? cdata.get(col - 1).name
					: cdata.get(col - 1).label;
		}
		return null;
	}

	public String getColumnName(int col) throws SQLException {
		if(cdata.size() <= col){
			return cdata.get(col - 1).name == null ? cdata.get(col - 1).label
					: cdata.get(col - 1).name;
		}
		return null;
	}

	public int getColumnType(int col) throws SQLException {
		if (col <= cdata.size()) {
			return cdata.get(col -1).jdbcType;
		}
		return -1;
	}

	public String getSchemaName(int arg0) throws SQLException {
		return sname;
	}

	public String getTableName(int arg0) throws SQLException {
		return fname;
	}

	///////////////////////////////////////////////////////////////////
	public boolean isAutoIncrement(int arg0) throws SQLException {
		return false;
	}

	public boolean isCaseSensitive(int arg0) throws SQLException {
		return false;
	}

	public boolean isCurrency(int arg0) throws SQLException {
		return false;
	}

	public boolean isDefinitelyWritable(int arg0) throws SQLException {
		return false;
	}

	public int getColumnCount() throws SQLException {
		return cdata.size();
	}

	public boolean isReadOnly(int arg0) throws SQLException {
		return true;
	}

	public boolean isSearchable(int arg0) throws SQLException {
		return false;
	}

	public boolean isWritable(int arg0) throws SQLException {
		return false;
	}

	public Map<String, List<Integer>> getErrorLines(){
		return errors;
	}
	
	public void setErrors(Map<String, List<Integer>> err){
		errors = err;
	}

	public int getLineCount() {
		return lineCount;
	}

	public void setLineCount(int lc) {
		lineCount = lc;
	}

	public class ColumnData{
		String name;
		String label;
		int colPos;
		int colType;
		int jdbcType;
		int dstColPos, dstColPos2;
		
		public ColumnData(SelectFunction func){
			label = func.getAlias();
			colType = func.getStorageType();
			if (func.getRequiredField() != null) {
				name = func.getRequiredField().getName();
				if (func.getRequiredField().getActualType() == FieldMeta.FIELD_DATE)
					colType = FieldMeta.FIELD_DATE;
			}
			colPos = func.getColPos();
			jdbcType = jtype(colType);
			dstColPos = func.getDstColPos();
			dstColPos2 = func.getDstColPos2();
		}

		private int jtype(int type) {
			switch (type) {
			case FieldMeta.FIELD_STRING:
				return Types.VARCHAR;
			case FieldMeta.FIELD_INTEGER:
				return Types.INTEGER;
			case FieldMeta.FIELD_DATE:
				return Types.DATE;
			case FieldMeta.FIELD_LONG:
				return Types.BIGINT;
			case FieldMeta.FIELD_FLOAT:
				return Types.FLOAT;
			case FieldMeta.FIELD_DOUBLE:
				return Types.DECIMAL;
			}
			return -1;
		}
	}

	public String getCatalogName(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public String getColumnClassName(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public int getColumnDisplaySize(int arg0) throws SQLException {
		// not supported
		return 0;
	}

	public String getColumnTypeName(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public int getPrecision(int arg0) throws SQLException {
		// not supported
		return 0;
	}

	public int getScale(int arg0) throws SQLException {
		//not supported
		return 0;
	}

	public int isNullable(int arg0) throws SQLException {
		// not supported
		return 0;
	}

	public boolean isSigned(int arg0) throws SQLException {
		//not supported
		return true;
	}

	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// not supported
		return false;
	}

	public <T> T unwrap(Class<T> arg0) throws SQLException {
		//not supported
		return null;
	}
}
