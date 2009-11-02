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

    $Id: YearFunction.java,v 1.2 2009/10/29 05:11:16 mreddy Exp $
*/
package com.logql.interpret.func.date;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.logql.interpret.func.DateFunction;
import com.logql.meta.FlexiRow;

public class YearFunction extends DateFunction {

	@Override
	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		dst.stringArr[dstColPos] = _YearString[src.dateArr[srcColPos].get(Calendar.YEAR) - 1970];
		dst.dateArr[dstColPos2] = src.dateArr[srcColPos];
		return true;
	}

	public int getIntValue(FlexiRow row) {
		return row.dateArr[srcColPos].get(Calendar.YEAR);
	}

	public int processArgVal(String arg) {
		try{
			int ret = Integer.parseInt(arg);
			if(ret<1970 || ret >2020)
				throw new IllegalArgumentException("Year has to be between 1970 and 2020");
			return ret;
		}catch(NumberFormatException nfe){
			throw new IllegalArgumentException("Invalid value for year: "+arg);
		}
	}

	public int processArgVal (GregorianCalendar gc) {
		return gc.get(Calendar.YEAR);
	}

	public static final String[] _YearString = { "1970", "1971", "1972",
			"1973", "1974", "1975", "1976", "1977", "1978", "1979", "1980",
			"1981", "1982", "1983", "1984", "1985", "1986", "1987", "1988",
			"1989", "1990", "1991", "1992", "1993", "1994", "1995", "1996",
			"1997", "1998", "1999", "2000", "2001", "2002", "2003", "2004",
			"2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012",
			"2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020" };
}
