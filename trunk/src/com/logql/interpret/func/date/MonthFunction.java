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

    $Id: MonthFunction.java,v 1.2 2009/10/29 05:11:16 mreddy Exp $
*/
package com.logql.interpret.func.date;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.logql.interpret.func.DateFunction;
import com.logql.meta.FlexiRow;

public class MonthFunction extends DateFunction {
	public static final String[] _Month = { "January", "February", "March",
			"April", "May", "June", "July", "August", "September", "October",
			"November", "December" };
	public static final String[] _MonthShort = { "Jan", "Feb", "Mar",
		"Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct",
		"Nov", "Dec" };

	@Override
	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		dst.stringArr[dstColPos] = _Month[src.dateArr[srcColPos].get(Calendar.MONTH)];
		dst.dateArr[dstColPos2] = src.dateArr[srcColPos];
		return true;
	}

	public int getIntValue(FlexiRow row) {
		return row.dateArr[srcColPos].get(Calendar.MONTH);
	}

	public int processArgVal(String arg) {
		if (arg.length() == 1) {
			try {
				return Integer.parseInt(arg) - 1;
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException(
						"Invalid value for day of week: " + arg);
			}
		}
		for (int i = 0; i < _Month.length; i++) {
			if (_Month[i].equalsIgnoreCase(arg)) {
				return i;
			}
			if (_MonthShort[i].equalsIgnoreCase(arg)) {
				return i;
			}
		}
		return -1;
	}

	public int processArgVal (GregorianCalendar gc) {
		return gc.get(Calendar.MONTH);
	}
}
