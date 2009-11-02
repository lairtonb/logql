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

    $Id: WeekOfYearFunction.java,v 1.2 2009/10/29 05:11:16 mreddy Exp $
*/
package com.logql.interpret.func.date;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.logql.interpret.func.DateFunction;
import com.logql.meta.FlexiRow;

public class WeekOfYearFunction extends DateFunction {
	public static final String[] _Week = { "W1", "W2", "W3", "W4", "W5", "W6",
			"W7", "W8", "W9", "W10", "W11", "W12", "W13", "W14", "W15", "W16",
			"W17", "W18", "W19", "W20", "W21", "W22", "W23", "W24", "W25",
			"W26", "W27", "W28", "W29", "W30", "W31", "W32", "W33", "W34",
			"W35", "W36", "W37", "W38", "W39", "W40", "W41", "W42", "W43",
			"W44", "W45", "W46", "W47", "W48", "W49", "W50", "W51", "W52",
			"W53", "W54", "W55", "W56", "W57", "W58", "W59" };

	@Override
	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		dst.stringArr[dstColPos] = _Week[src.dateArr[srcColPos].get(Calendar.WEEK_OF_YEAR) - 1];
		dst.dateArr[dstColPos2] = src.dateArr[srcColPos];
		return true;
	}

	public int getIntValue(FlexiRow row) {
		return row.dateArr[srcColPos].get(Calendar.WEEK_OF_YEAR);
	}

	public int processArgVal(String arg) {
		if (arg.length() == 1) {
			try {
				int ret = Integer.parseInt(arg);
				if(ret>53)
					throw new IllegalArgumentException("Invalid value for Week: "+arg);
				return ret;
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException(
						"Invalid value for day of week: " + arg);
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
		return gc.get(Calendar.WEEK_OF_YEAR);
	}
}
