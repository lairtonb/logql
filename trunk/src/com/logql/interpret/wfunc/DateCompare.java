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

    $Id: DateCompare.java,v 1.2 2009/10/29 05:11:11 mreddy Exp $
*/
package com.logql.interpret.wfunc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import com.logql.interpret.func.DateFunction;
import com.logql.interpret.func.date.DayFunction;
import com.logql.interpret.func.date.FullDateFunction;
import com.logql.meta.FlexiRow;
import com.logql.util.ArgumentsTokenizer;
import com.logql.util.UtilMethods;

public class DateCompare implements ComparisonOperator {
	DateWhereFunction dFunction;
	int opr = -1;
	int reqValue;
	GregorianCalendar reqDate;
	boolean usesToDate;

	public void init(int[] srcMap){
		dFunction.init(srcMap);
	}

	public boolean evaluate(FlexiRow row) {
		switch (opr) {
		case _OperatorEquals:
			return dFunction.getIntValue(row) == reqValue;
		case _OperatorNotEquals:
			return dFunction.getIntValue(row) != reqValue;
		case _OperatorGreater:
			return dFunction.getIntValue(row) > reqValue;
		case _OperatorLesser:
			return dFunction.getIntValue(row) < reqValue;
		}
		return false;
	}

	public void setFunction(WhereFunction func) {
		dFunction = (DateFunction)func;
		replace();
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
			throw new IllegalArgumentException("Unknown operator for date: "
					+ op);
		replace();
	}

	public void processRHS(String arg) {
		String val = new String(arg);
		if (val.startsWith("todate")) {
			usesToDate = true;
			int sloc = val.indexOf("(");
			int eloc = val.lastIndexOf(")");
			if (sloc == -1 || eloc == -1)
				throw new IllegalArgumentException("Invalid :" + val);
			val = val.substring(sloc + 1, eloc).trim();

			ArgumentsTokenizer tok = new ArgumentsTokenizer(val);
			if(tok.countTokens() != 2)
				throw new IllegalArgumentException("Invalid aruguments, expecting todate('date','format')");
			String dval = tok.nextToken();
			String fval = tok.nextToken();
			
			try{
				reqDate = new GregorianCalendar();
				reqDate.setTime(new SimpleDateFormat(fval).parse(dval));
				reqValue = dFunction.processArgVal(reqDate);
			}catch (ParseException e){
				throw new IllegalArgumentException("Invalid: "+val);
			}
		} else {
			val = UtilMethods.removeQuotes(arg);
			reqValue = dFunction.processArgVal(val);
		}
		replace();
	}

	protected void replace() {
		if (usesToDate && dFunction != null && dFunction instanceof DayFunction
				&& opr > _OperatorNotEquals) {
			FullDateFunction tmp = new FullDateFunction();
			tmp.setField(dFunction.getRequiredField());
			reqValue = tmp.processArgVal(reqDate);
			dFunction = tmp;
		}
	}
}
