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

    $Id: StringLike.java,v 1.2 2009/10/29 05:11:11 mreddy Exp $
*/
package com.logql.interpret.wfunc;

import java.util.ArrayList;

import com.logql.meta.FlexiRow;
import com.logql.util.Marker;
import com.logql.util.UtilMethods;

public class StringLike implements ComparisonOperator {
	public static final int _Empty = -1;
	public static final int _StartsWith = 0;
	public static final int _EndsWith = 1;
	public static final int _Full = 2;
	public static final int _Exp = 3;

	StringWhereFunction sget;
	byte[][] req;
	boolean eq = true;
	boolean startsWith;
	boolean endsWith;
	int runType;
	boolean moreThanTwo;
	char wildChar = '%';
	Marker mark = new Marker();

	public StringLike() {
	}

	public StringLike(char sep) {
		wildChar = sep;
	}

	public void init(int[] srcMap){
		sget.init(srcMap);
	}

	public boolean evaluate(FlexiRow row) {
		mark.startPos = 0;
		byte[] res = sget.getString(row, mark);
		mark.lineEndPos = mark.endPos;
		return evaluate(res, mark);
	}
	
	public boolean evaluate (byte[] res, Marker mark){
		int pos = 0;

		switch (runType) {
		case _Empty: {
			return eq;
		}
		case _StartsWith: {
			return eq ? UtilMethods.startsWith(res, req[0], mark) : 
				!UtilMethods.startsWith(res, req[0], mark);
		}
		case _EndsWith: {
			return eq ? UtilMethods.endsWith(res, req[0], mark): 
				!UtilMethods.endsWith(res, req[0], mark);
		}
		case _Full: {
			return eq ? UtilMethods.stringEquals(res, req[0], mark):
				!UtilMethods.stringEquals(res, req[0], mark);
		}
		}

		int sp = UtilMethods.indexOf(res, req[pos++], mark);
		if ((sp == -1) || (startsWith && sp != mark.startPos))
			return eq ? false : true;

		if (moreThanTwo) {
			for (; pos < req.length - 1; pos++) {
				mark.startPos = sp + req[pos - 1].length;
				sp = UtilMethods.indexOf(res, req[pos], mark);
				if (sp == -1)
					return eq ? false : true;
			}
		}

		if (pos == req.length - 1) {
			mark.startPos = sp + req[pos -1].length;
			sp = UtilMethods.indexOf(res, req[pos++], mark);
		}
		if (sp == -1 || (endsWith && sp + req[pos -1].length != mark.endPos))
			return eq ? false : true;

		return eq ? true : false;
	}

	public void setFunction(WhereFunction wc) {
		sget = (StringWhereFunction) wc;
	}

	public void processRHS(String val) {
		val = UtilMethods.removeQuotes(val);
		char[] carr = val.toCharArray();

		StringBuffer sbuff = new StringBuffer();
		ArrayList<String> parts = new ArrayList<String>();
		for (int i = 0; i < carr.length; i++) {
			if (carr[i] == wildChar) {
				if (sbuff.length() > 0)
					parts.add(sbuff.toString());
				sbuff = new StringBuffer();
			} else {
				if (i == 0)
					startsWith = true;
				if (i == carr.length -1)
					endsWith = true;

				sbuff.append(carr[i]);
			}
		}
		if (sbuff.length() > 0)
			parts.add(sbuff.toString());

		if(parts.size() == 0) {
			runType = _Empty;
			return;
		}

		req = new byte[parts.size()][];
		for (int i = 0; i < parts.size(); i++) {
			req[i] = parts.get(i).getBytes();
		}
		moreThanTwo = req.length > 2;
		runType = _Exp;
		if(parts.size() == 1){
			if(startsWith && endsWith && true)
				runType = _Full;
			else if(startsWith)
				runType = _StartsWith;
			else if(endsWith)
				runType = _EndsWith;
		}
	}

	public void setOperator(String op) {
		if (op.equals("like"))
			eq = true;
		else if (op.equals("notlike"))
			eq = false;
	}
}
