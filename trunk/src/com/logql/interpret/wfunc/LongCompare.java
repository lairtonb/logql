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

    $Id: LongCompare.java,v 1.2 2009/10/29 05:11:11 mreddy Exp $
*/
package com.logql.interpret.wfunc;

import com.logql.interpret.func.LongCopy;
import com.logql.meta.FlexiRow;

public class LongCompare implements ComparisonOperator {
	LongCopy lcopy;
	long reqValue;
	int opr;

	public void init(int[] srcMap){
		lcopy.init(srcMap);
	}
	public boolean evaluate(FlexiRow row){
		switch (opr) {
		case _OperatorEquals:
			return lcopy.getLong(row) == reqValue;
		case _OperatorNotEquals:
			return lcopy.getLong(row) != reqValue;
		case _OperatorGreater:
			return lcopy.getLong(row) > reqValue;
		case _OperatorLesser:
			return lcopy.getLong(row) < reqValue;
		}
		return false;
	}

	public void processRHS(String val) {
		try{
			reqValue = Long.parseLong(val.trim());
		}catch(NumberFormatException nfe){
			throw new IllegalArgumentException("Invalid Value: "+val+" Expecting Long");
		}
	}
	public void setOperator(String op) {
		if (op.equals("="))
			opr = _OperatorEquals;
		else if (op.equals("#"))
			opr = _OperatorNotEquals;
		else if (op.equals(">"))
			opr = _OperatorGreater;
		else if (op.equals("<"))
			opr = _OperatorLesser;
		if (opr == -1)
			throw new IllegalArgumentException("Unknown operator for type long: " + op);
	}
	public void setFunction(WhereFunction func) {
		lcopy = (LongCopy)func;
	}
}
