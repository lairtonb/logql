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

    $Id: HourFunction.java,v 1.2 2009/10/29 05:11:16 mreddy Exp $
*/
package com.logql.interpret.func.date;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.logql.interpret.func.DateFunction;
import com.logql.meta.FlexiRow;

public class HourFunction extends DateFunction {
	public static final String[] hours = { "0:00", "1:00", "2:00", "3:00",
			"4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00",
			"12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00",
			"19:00", "20:00", "21:00", "22:00", "23:00", "24:00" };
	@Override
	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		dst.stringArr[dstColPos] = hours[src.dateArr[srcColPos].get(Calendar.HOUR_OF_DAY)];
		dst.dateArr[dstColPos2] = src.dateArr[srcColPos];
		return true;
	}

	public int getIntValue(FlexiRow row) {
		return row.dateArr[srcColPos].get(Calendar.HOUR_OF_DAY);
	}

	public int processArgVal(String arg) {
		try {
			return Integer.parseInt(arg);
		} catch (NumberFormatException nfe) {
			throw new IllegalArgumentException(
					"Invalid date value, enter only hour");
		}
	}

	public int processArgVal (GregorianCalendar gc) {
		return gc.get(Calendar.HOUR_OF_DAY);
	}
}
