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

    $Id: GrepResultSet.java,v 1.2 2009/10/29 05:11:07 mreddy Exp $
*/
package com.logql.interpret;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.logql.interpret.func.SelectFunction;
import com.logql.meta.FlexiRow;

public class GrepResultSet extends ResultSetImpl {

	LinkedList<FlexiRow> result = new LinkedList<FlexiRow>();
	SelectFunction[] postExe;
	SelectFunction[] func;
	int pos = 0;
	volatile boolean finished;

	public GrepResultSet(SelectMeta smeta) {
		super(smeta);
		postExe = new SelectFunction[0];
		func = new SelectFunction[smeta.getFunctions().size()];
		smeta.getFunctions().toArray(func);
		ArrayList<SelectFunction> rp = new ArrayList<SelectFunction>();
		for(SelectFunction ps:func){
			if(ps.requiresPostProcess())
				rp.add(ps);
		}
		if(rp.size()>0){
			postExe = new SelectFunction[rp.size()];
			rp.toArray(postExe);
		}
	}

	public void add(FlexiRow row) {
		synchronized (result) {
			FlexiRow nrow = new FlexiRow(smeta.ssiz, 0, smeta.isiz, smeta.dtsiz,
					smeta.lsiz, smeta.fsiz, smeta.dsiz, smeta.osiz,
					smeta.flexiRowMap);

			for (SelectFunction sf : func)
				sf.copyToDst(row, nrow);
			for (SelectFunction sf : postExe)
				sf.postProcess(null, nrow);
			result.add(nrow);
			result.notify();
		}
	}

	public boolean next() throws SQLException {
		synchronized (result) {
			while (size() == 0 && !finished) {
				try {
					result.wait();
				} catch (InterruptedException ie) {

				}
			}
			if (size() > 0) {
				curr = result.removeFirst();
				return true;
			}

			return false;
		}
	}

	private int size() {
		return result.size();
	}

	public void close() throws SQLException {
		synchronized (result) {
			finished = true;
			result.notify();
		}
	}

	public boolean isClosed() throws SQLException {
		return finished;
	}

	public boolean isAfterLast() throws SQLException {
		return size() == 0 && finished;
	}

	public boolean isLast() throws SQLException {
		return size() == 1 && finished;
	}

	public boolean last() throws SQLException {
		throw new SQLException("Operation not supported");
	}

	public void afterLast() throws SQLException {
		throw new SQLException("Operation not supported");
	}

	public void beforeFirst() throws SQLException {
		throw new SQLException("Operation not supported");
	}

	public boolean absolute(int arg0) throws SQLException {
		throw new SQLException("Operation not supported");
	}

	public boolean first() throws SQLException {
		throw new SQLException("Operation not supported");
	}

	public boolean isBeforeFirst() throws SQLException {
		throw new SQLException("Operation not supported");
	}

	public boolean isFirst() throws SQLException {
		throw new SQLException("Operation not supported");
	}

}
