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

    $Id: DayOfWeekFunction.java,v 1.2 2009/10/29 05:11:16 mreddy Exp $
*/
package com.logql.interpret.func.date;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.logql.interpret.func.DateFunction;
import com.logql.meta.FlexiRow;

public class DayOfWeekFunction extends DateFunction {
	public static final String[] _DayOfWeek = { "Sunday", "Monday", "Tuesday",
			"Wednesday", "Thursday", "Friday", "Saturday" };
	public static final String[] _DayOfWeekShort = { "Sun", "Mon", "Tue",
		"Wed", "Thu", "Fri", "Sat" };

	@Override
	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		dst.stringArr[dstColPos] = _DayOfWeek[src.dateArr[srcColPos].get(Calendar.DAY_OF_WEEK) - 1];
		dst.dateArr[dstColPos2] = src.dateArr[srcColPos];
		return true;
	}

	public int getIntValue(FlexiRow row) {
		return row.dateArr[srcColPos].get(Calendar.DAY_OF_WEEK);
	}

	public int processArgVal(String arg) {
		if (arg.length() == 1) {
			try {
				return Integer.parseInt(arg);
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException(
						"Invalid value for day of week: " + arg);
			}
		}
		for (int i = 0; i < _DayOfWeek.length; i++) {
			if (_DayOfWeek[i].equalsIgnoreCase(arg)) {
				return i + 1;
			}
			if (_DayOfWeekShort[i].equalsIgnoreCase(arg)) {
				return i + 1;
			}
		}
		throw new IllegalArgumentException("Invalid value for day of week: "+arg);
	}

	public int processArgVal (GregorianCalendar gc) {
		return gc.get(Calendar.DAY_OF_WEEK);
	}
}
