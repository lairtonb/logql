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

    $Id: StringCompare.java,v 1.2 2009/10/29 05:11:11 mreddy Exp $
*/
package com.logql.interpret.wfunc;

import com.logql.meta.FlexiRow;
import com.logql.util.Marker;
import com.logql.util.UtilMethods;

public class StringCompare implements ComparisonOperator {
	StringWhereFunction sget;
	byte[] req;
	boolean eq;
	Marker mark = new Marker();
	public String rhs,lhs;

	public void init(int[] srcMap) {
		sget.init(srcMap);
	}

	public boolean evaluate(FlexiRow row) {
		byte[] tar = sget.getString(row, mark);
		return eq ? UtilMethods.stringEquals(tar, req, mark):
			!UtilMethods.stringEquals(tar, req, mark);
	}

	public void processRHS(String val) {
		req = UtilMethods.removeQuotes(val).getBytes();
	}

	public void setFunction(WhereFunction wc) {
		sget = (StringWhereFunction) wc;
	}

	public String toString() {
		return sget.toString() + " = " + new String(req);
	}
	public void setOperator(String op) {
		if (op.equals("="))
			eq = true;
		else if (op.equals("#"))
			eq = false;
	}
}
