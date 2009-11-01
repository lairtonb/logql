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

    $Id: IntIn.java,v 1.2 2009/10/29 05:11:11 mreddy Exp $
*/
package com.logql.interpret.wfunc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import com.logql.interpret.StatementImpl;
import com.logql.interpret.func.IntCopy;
import com.logql.interpret.func.SelectFunction;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.util.ArgumentsTokenizer;
import com.logql.util.IntWrapper;

public class IntIn implements ComparisonOperator {
	IntCopy icopy;
	HashSet<IntWrapper> req;
	IntWrapper iwrap = new IntWrapper();
	boolean eq;

	public void init(int[] srcMap){
		icopy.init(srcMap);
	}

	public boolean evaluate(FlexiRow row) {
		iwrap.setValue(icopy.getInteger(row));
		return eq ? req.contains(iwrap) : !req.contains(iwrap);
	}

	public void processRHS(String val) {
		if (val.startsWith("(") && val.endsWith(")")) {
			val = val.substring(1, val.length() - 1);
		}
		req = new HashSet<IntWrapper>();
		ArgumentsTokenizer tok = new ArgumentsTokenizer(val);
		String b = null;
		while ((b = tok.nextToken()) != null) {
			try {
				int r = Integer.parseInt(b);
				req.add(new IntWrapper(r));
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException(
						"Invalid value, expecting int, got: " + b);
			}
		}
	}

	public void processRHS(String val, StatementImpl parent) throws SQLException{
		if (val.startsWith("(") && val.endsWith(")")) {
			val = val.substring(1, val.length() - 1);
		}
		String lval = val.toLowerCase();
		if(lval.startsWith("select ")|| lval.startsWith("grep ")){
			StatementImpl exe = parent.clone();
			StatementImpl.ExecuteMeta meta = exe.compile(val);
			ArrayList<SelectFunction> funcs = new ArrayList<SelectFunction>(
					meta.smeta.getFunctions());
			funcs.addAll(meta.smeta.getMetricFunctions());
			if (funcs.size() == 0 || funcs.size() > 1) {
				throw new IllegalArgumentException(
						"Inner query should return only one column");
			}
			if (funcs.get(0).getStorageType() != FieldMeta.FIELD_INTEGER) {
				throw new IllegalArgumentException(
						"Inner query result should be of type string");
			}
			ResultSet rs = exe.execute(meta);
			req = new HashSet<IntWrapper>();
			while (rs.next()) {
				req.add(new IntWrapper(rs.getInt(1)));
			}
		} else {
			req = new HashSet<IntWrapper>();
			ArgumentsTokenizer tok = new ArgumentsTokenizer(val);
			String b = null;
			while ((b = tok.nextToken()) != null) {
				try {
					int r = Integer.parseInt(b);
					req.add(new IntWrapper(r));
				} catch (NumberFormatException nfe) {
					throw new IllegalArgumentException(
							"Invalid value, expecting int, got: " + b);
				}
			}
		}
	}

	public void setOperator(String op) {
		if (op.equals("in"))
			eq = true;
		else if (op.equals("notin"))
			eq = false;
	}

	public void setFunction(WhereFunction func) {
		icopy = (IntCopy) func;
	}
}
