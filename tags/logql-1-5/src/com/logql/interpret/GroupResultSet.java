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

    $Id: GroupResultSet.java,v 1.2 2009-10-29 05:11:07 mreddy Exp $
*/
package com.logql.interpret;

import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.ArrayList;
import java.util.HashMap;

import com.logql.interpret.func.SelectFunction;
import com.logql.meta.FlexiRow;

public class GroupResultSet extends ResultSetImpl {
	private FlexiRow[] rows;
	int pos = 0;
	boolean closed;

	public GroupResultSet(HashMap<FlexiRow, FlexiRow> result, SelectMeta meta){
		super(meta);
		rows = new FlexiRow[result.size()];
		for (FlexiRow r : result.values())
			rows[pos++] = r;

		pos = 0;
		ArrayList<SelectFunction> post = new ArrayList<SelectFunction>();

		for(SelectFunction sf:meta.getFunctions())
			if(sf.requiresPostProcess())
				post.add(sf);
		for(SelectFunction sf:meta.getMetricFunctions())
			if(sf.requiresPostProcess())
				post.add(sf);
		postProcess(post);
	}

	public void order(OrderBy order) {
		order.execute(rows);
	}

	public void postProcess(ArrayList<SelectFunction> post) {
		FlexiRow prev = null;
		for (FlexiRow row : rows) {
			for (SelectFunction sf : post) {
				sf.postProcess(prev, row);
			}
			prev = row;
		}
	}

	public void close() throws SQLException {
		closed = true;
	}

	public int getSize(){
		return rows.length;
	}

	public boolean isClosed() throws SQLException {
		return closed;
	}

	public boolean next() throws SQLException {
		if (pos < rows.length) {
			curr = rows[pos++];
			return true;
		}
		return false;
	}

	public boolean isAfterLast() throws SQLException {
		return pos == rows.length;
	}

	public boolean isBeforeFirst() throws SQLException {
		return pos == -1;
	}

	public boolean isFirst() throws SQLException {
		return pos == 0;
	}

	public boolean isLast() throws SQLException {
		return pos == rows.length -1;
	}

	public boolean last() throws SQLException {
		pos = rows.length -1;
		return true;
	}
	
	public void afterLast() throws SQLException {
		pos = rows.length;
	}

	public void beforeFirst() throws SQLException {
		pos = -1;
	}

	public boolean absolute(int row) throws SQLException {
		if (row > 0 && row < rows.length) {
			pos = row;
			return true;
		} else if (row < 0 && Math.abs(row) < rows.length) {
			pos = rows.length + row;
			return true;
		}

		return false;
	}

	public boolean first() throws SQLException {
		pos = 1;
		return true;
	}

	public NClob getNClob(int arg0) throws SQLException {
		// no op
		return null;
	}

	public NClob getNClob(String arg0) throws SQLException {
		// no op
		return null;
	}

	public RowId getRowId(int arg0) throws SQLException {
		// no op
		return null;
	}

	public RowId getRowId(String arg0) throws SQLException {
		// no op
		return null;
	}

	public SQLXML getSQLXML(int arg0) throws SQLException {
		// no op
		return null;
	}

	public SQLXML getSQLXML(String arg0) throws SQLException {
		// no op
		return null;
	}

	public void updateNClob(int arg0, NClob arg1) throws SQLException {
		// no op
		
	}

	public void updateNClob(String arg0, NClob arg1) throws SQLException {
		// no op
		
	}

	public void updateRowId(int arg0, RowId arg1) throws SQLException {
		// no op
		
	}

	public void updateRowId(String arg0, RowId arg1) throws SQLException {
		// no op
		
	}

	public void updateSQLXML(int arg0, SQLXML arg1) throws SQLException {
		// no op
		
	}

	public void updateSQLXML(String arg0, SQLXML arg1) throws SQLException {
		// no op
		
	}
}
