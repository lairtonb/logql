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

    $Id: DayFunction.java,v 1.2 2009/10/29 05:11:16 mreddy Exp $
*/
package com.logql.interpret.func.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.logql.interpret.func.DateFunction;
import com.logql.meta.FlexiRow;
import com.logql.meta.LogMeta;

public class DayFunction extends DateFunction {

	@Override
	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		System.arraycopy(NumBytes[src.dateArr[srcColPos].get(Calendar.DAY_OF_MONTH) - 1], 0, 
				dst.charArr[dstColPos], 0, 2);
		System.arraycopy(NumBytes[src.dateArr[srcColPos].get(Calendar.MONTH)], 0,
				dst.charArr[dstColPos], 2, 2);
		int year =src.dateArr[srcColPos].get(Calendar.YEAR);
		if(year < 1990 || year > 2010){
			System.arraycopy(Integer.toString(year).getBytes(), 0, dst.charArr[dstColPos], 4, 4);
		} else {
			System.arraycopy(YearBytes[src.dateArr[srcColPos].get(Calendar.YEAR) -1990], 0,
					dst.charArr[dstColPos], 4, 4);	
		}
		dst.charSiz[dstColPos] = 8;
		dst.dateArr[dstColPos2] = src.dateArr[srcColPos];
		return true;
	}

	public void copyToDst(FlexiRow src, FlexiRow dst) {
		dst.stringArr[dstColPos]= new String(src.charArr[dstColPos],0,src.charSiz[dstColPos]);
		dst.charSiz[dstColPos] = src.charSiz[dstColPos]; 
		dst.dateArr[dstColPos2] = (GregorianCalendar)src.dateArr[dstColPos2].clone();
	}

	public int getIntValue(FlexiRow row) {
		return processArgVal(row.dateArr[srcColPos]);
	}

	private static final String[] formats = { "yyyy-MM-dd", "MM-dd-yyyy", 
		"MMM-dd-yyyy", "dd-MMM-yyyy" };

	public int processArgVal(String arg) {
		Date val = null;
		for(String format:formats){
			try{
				//TODO: it doesn't always return an error... be clear
				val = new SimpleDateFormat(format).parse(arg);
				break;
			}catch(ParseException e){}
		}
		if(val == null){
			throw new IllegalArgumentException("Invalid argument: "+arg+" Use yyyy-MM-dd (2007-12-31) format,");
		}
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(val);
		return processArgVal(gc);
	}

	public int processArgVal (GregorianCalendar gc) {
		int ret = gc.get(Calendar.YEAR);
		ret *= 1000;
		ret += gc.get(Calendar.DAY_OF_YEAR);
		return ret;
	}

	@Override
	public void processFunctionArgs(LogMeta lm, String args) {
		super.processFunctionArgs(lm, args);
		if(formatter == null && !utcOutput){
			formatter = new SimpleDateFormat("dd-MMM-yy");
			date = new Date();
		}
	}

	public final byte[][] NumBytes = { { ' ', '0' }, { ' ', '1' },
			{ ' ', '2' }, { ' ', '3' }, { ' ', '4' }, { ' ', '5' },
			{ ' ', '6' }, { ' ', '7' }, { ' ', '8' }, { ' ', '9' },
			{ '1', '0' }, { '1', '1' }, { '1', '2' }, { '1', '3' },
			{ '1', '4' }, { '1', '5' }, { '1', '6' }, { '1', '7' },
			{ '1', '8' }, { '1', '9' }, { '2', '0' }, { '2', '1' },
			{ '2', '2' }, { '2', '3' }, { '2', '4' }, { '2', '5' },
			{ '2', '6' }, { '2', '7' }, { '2', '8' }, { '2', '9' },
			{ '3', '0' }, { '3', '1' } };
	public final byte[][] YearBytes = { { '1', '9', '9', '0' },
			{ '1', '9', '9', '1' }, { '1', '9', '9', '2' },
			{ '1', '9', '9', '3' }, { '1', '9', '9', '4' },
			{ '1', '9', '9', '5' }, { '1', '9', '9', '6' },
			{ '1', '9', '9', '7' }, { '1', '9', '9', '8' },
			{ '1', '9', '9', '9' }, { '2', '0', '0', '0' },
			{ '2', '0', '0', '1' }, { '2', '0', '0', '2' },
			{ '2', '0', '0', '3' }, { '2', '0', '0', '4' },
			{ '2', '0', '0', '5' }, { '2', '0', '0', '6' },
			{ '2', '0', '0', '7' }, { '2', '0', '0', '8' },
			{ '2', '0', '0', '9' }, { '2', '0', '1', '0' } };
}
