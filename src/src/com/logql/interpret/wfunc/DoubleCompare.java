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

    $Id: DoubleCompare.java,v 1.2 2009/10/29 05:11:10 mreddy Exp $
*/
package com.logql.interpret.wfunc;

import com.logql.interpret.func.DoubleCopy;
import com.logql.meta.FlexiRow;

public class DoubleCompare implements ComparisonOperator {
	int opr;
	DoubleCopy dFunction;
	double reqValue;

	public void init(int[] srcMap){
		dFunction.init(srcMap);
	}

	public boolean evaluate(FlexiRow row){
		switch (opr) {
		case _OperatorEquals:
			return dFunction.getDouble(row) == reqValue;
		case _OperatorNotEquals:
			return dFunction.getDouble(row) != reqValue;
		case _OperatorGreater:
			return dFunction.getDouble(row) > reqValue;
		case _OperatorLesser:
			return dFunction.getDouble(row) < reqValue;
		}
		return false;
	}

	public void processRHS(String val) {
		try{
			reqValue = Double.parseDouble(val.trim());
		}catch(NumberFormatException nfe){
			throw new IllegalArgumentException("Invalid value: "+val+". Expecting number");
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
			throw new IllegalArgumentException("Unknown operator for type double: "+ op);
	}

	public void setFunction(WhereFunction func) {
		dFunction = (DoubleCopy)func;
	}
}
