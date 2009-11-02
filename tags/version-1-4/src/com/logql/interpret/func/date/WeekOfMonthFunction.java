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

    $Id: WeekOfMonthFunction.java,v 1.2 2009/10/29 05:11:16 mreddy Exp $
*/
package com.logql.interpret.func.date;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.logql.interpret.func.DateFunction;
import com.logql.meta.FlexiRow;

public class WeekOfMonthFunction extends DateFunction {
	public static final String[] _Week ={"W1","W2","W3","W4","W5","W6","W7","W8"};
	@Override
	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		dst.stringArr[dstColPos] = _Week[src.dateArr[srcColPos].get(Calendar.WEEK_OF_MONTH) - 1];
		dst.dateArr[dstColPos2] = src.dateArr[srcColPos];
		return true;
	}

	public int getIntValue(FlexiRow row) {
		return row.dateArr[srcColPos].get(Calendar.WEEK_OF_MONTH);
	}

	public int processArgVal(String arg) {
		if (arg.length() == 1) {
			try {
				int ret = Integer.parseInt(arg);
				if(ret>5)
					throw new IllegalArgumentException("Invalid value for week: "+arg);
				return ret;
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException(
						"Invalid value for week: " + arg);
			}
		}
		for (int i = 0; i < _Week.length; i++) {
			if (_Week[i].equalsIgnoreCase(arg)) {
				return i + 1;
			}
//			if (_DayOfWeekShort[i].equalsIgnoreCase(arg)) {
//				return i + 1;
//			}
		}
		return -1;
	}

	public int processArgVal (GregorianCalendar gc) {
		return gc.get(Calendar.WEEK_OF_MONTH);
	}
}
