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

    $Id: FullDateFunction.java,v 1.2 2009/10/29 05:11:16 mreddy Exp $
*/
package com.logql.interpret.func.date;

import java.util.GregorianCalendar;

import com.logql.interpret.func.DateFunction;
import com.logql.meta.FlexiRow;

public class FullDateFunction extends DateFunction {

	@Override
	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		throw new IllegalArgumentException("Should not be used in select");
	}

	public int getIntValue(FlexiRow row) {
		return (int) (row.dateArr[srcColPos].getTimeInMillis() / 1000);
	}

	public int processArgVal(String arg) {
		throw new IllegalArgumentException("Invalid argument");
	}

	public int processArgVal(GregorianCalendar gc) {
		return (int) (gc.getTimeInMillis() / 1000);
	}

}
