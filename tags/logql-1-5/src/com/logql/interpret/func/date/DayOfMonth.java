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

    $Id: DayOfMonth.java,v 1.2 2009/10/29 05:11:16 mreddy Exp $
*/
package com.logql.interpret.func.date;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.logql.interpret.func.DateFunction;
import com.logql.meta.FlexiRow;

public class DayOfMonth extends DateFunction {
	public static final String[] _Day = { "1", "2", "3", "4", "5", "6", "7", "8",
			"9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
			"20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
			"31" };
	@Override
	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		dst.stringArr[dstColPos] = _Day[src.dateArr[srcColPos].get(Calendar.DATE) - 1];
		dst.dateArr[dstColPos2] = src.dateArr[srcColPos];
		return true;
	}

	public int getIntValue(FlexiRow row) {
		return row.dateArr[srcColPos].get(Calendar.DATE);
	}

	public int processArgVal(String arg) {
		try{
			int ret = Integer.parseInt(arg);
			if(ret > 31)
				throw new IllegalArgumentException("Expect value less than 31, got: "+arg);
			return ret;
		}catch(NumberFormatException nfe){
			throw new IllegalArgumentException("Invalid argument: " + arg
					+ " Expecting number less than 31");
		}
	}

	public int processArgVal (GregorianCalendar gc) {
		return gc.get(Calendar.DATE);
	}
}
