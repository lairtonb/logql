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

    $Id: IntCompare.java,v 1.2 2009/10/29 05:11:10 mreddy Exp $
*/
package com.logql.interpret.wfunc;

import com.logql.interpret.func.IntCopy;
import com.logql.meta.FlexiRow;

public class IntCompare implements ComparisonOperator {
	int opr;
	int reqValue;
	IntCopy iFunction;

	public void init(int[] srcMap){
		iFunction.init(srcMap);
	}

	public boolean evaluate(FlexiRow row){
		switch (opr) {
		case _OperatorEquals:
			return iFunction.getInteger(row) == reqValue;
		case _OperatorNotEquals:
			return iFunction.getInteger(row) != reqValue;
		case _OperatorGreater:
			return iFunction.getInteger(row) > reqValue;
		case _OperatorLesser:
			return iFunction.getInteger(row) < reqValue;
		}
		return false;
	}

	public void processRHS(String val) {
		try{
			reqValue = Integer.parseInt(val.trim());
		}catch(NumberFormatException nfe){
			throw new IllegalArgumentException("Invalid Value: "+val+" Expecting Integer");
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
			throw new IllegalArgumentException("Unknown operator for type int: "
					+ op);
	}

	public void setFunction(WhereFunction func) {
		iFunction = (IntCopy)func;
	}
}
