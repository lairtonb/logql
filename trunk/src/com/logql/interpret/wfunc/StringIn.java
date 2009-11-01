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

    $Id: StringIn.java,v 1.2 2009/10/29 05:11:11 mreddy Exp $
*/
package com.logql.interpret.wfunc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import com.logql.interpret.StatementImpl;
import com.logql.interpret.func.SelectFunction;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.util.ArgumentsTokenizer;
import com.logql.util.ByteStringWrapper;
import com.logql.util.Marker;

public class StringIn implements ComparisonOperator {
	StringWhereFunction sget;
	boolean eq;
	HashSet<ByteStringWrapper> req;
	ByteStringWrapper swrap = new ByteStringWrapper();
	Marker mark = new Marker();

	public void init(int[] srcMap) {
		sget.init(srcMap);
	}

	//TODO: select strtok(useragent,';',2), count(*)  where strtok(useragent,';',2) notin ('ScoutJet')
	//if the contents has ), it breaks
	public boolean evaluate(FlexiRow row) {
		mark.startPos = 0;
		byte[] res = sget.getString(row, mark);
		mark.lineEndPos = mark.endPos;
		swrap.setValue(res, mark.startPos, mark.endPos);
		swrap.computeHash();
		return eq ? req.contains(swrap) : !req.contains(swrap);
	}

	public void processRHS(String val) {
		if (val.startsWith("(") && val.endsWith(")")) {
			val = val.substring(1, val.length() - 1);
		}
		req = new HashSet<ByteStringWrapper>();
		ArgumentsTokenizer tok = new ArgumentsTokenizer(val);
		String b = null;
		while ((b = tok.nextToken()) != null)
			req.add(new ByteStringWrapper(b));
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
			if (funcs.get(0).getStorageType() != FieldMeta.FIELD_STRING) {
				throw new IllegalArgumentException(
						"Inner query result should be of type string");
			}
			ResultSet rs = exe.execute(meta);
			req = new HashSet<ByteStringWrapper>();
			while (rs.next()) {
				req.add(new ByteStringWrapper(rs.getString(1)));
			}
		} else {
			req = new HashSet<ByteStringWrapper>();
			ArgumentsTokenizer tok = new ArgumentsTokenizer(val);
			String b = null;
			while ((b = tok.nextToken()) != null)
				req.add(new ByteStringWrapper(b));
		}
	}

	public void setOperator(String op) {
		if (op.equals("in"))
			eq = true;
		else if (op.equals("notin"))
			eq = false;
	}

	public void setFunction(WhereFunction func) {
		sget = (StringWhereFunction) func;
	}
}
