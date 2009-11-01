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

    $Id: DateFunction.java,v 1.2 2009/10/29 05:11:15 mreddy Exp $
*/
package com.logql.interpret.func;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import com.logql.interpret.wfunc.DateWhereFunction;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.meta.LogMeta;
import com.logql.util.UtilMethods;

public abstract class DateFunction extends SelectFunction implements DateWhereFunction {

	protected SimpleDateFormat formatter;
	protected Date date;
	protected boolean utcOutput;

	@Override
	public void copyToDst(FlexiRow src, FlexiRow dst) {
		dst.stringArr[dstColPos] = src.stringArr[dstColPos];
		dst.charSiz[dstColPos] = src.stringArr[dstColPos].length();
		dst.dateArr[dstColPos2] = (GregorianCalendar)src.dateArr[dstColPos2].clone();
	}

	public void postProcess(FlexiRow prev, FlexiRow curr) {
		if(utcOutput){
			curr.stringArr[dstColPos] = Integer
				.toString((int)(curr.dateArr[dstColPos2].getTimeInMillis()/1000));
		} else {
			date.setTime(curr.dateArr[dstColPos2].getTimeInMillis());
			curr.stringArr[dstColPos] = formatter.format(date);
		}
	}

	@Override
	public int getStorageType() {
		return FieldMeta.FIELD_STRING;
	}

	@Override
	public int getSecondaryStroageType(){
		return FieldMeta.FIELD_DATE;
	}

	@Override
	public boolean isMetricField() {
		return false;
	}

	@Override
	public boolean requiresPostProcess() {
		return formatter != null || utcOutput;
	}

	@Override
	public void processFunctionArgs(LogMeta lm, String args) {
		if (args.indexOf("todate") == 0 && args.indexOf("(") > -1) {
			int eloc = args.indexOf(")");
			if (eloc == -1)
				throw new IllegalArgumentException("Invalid input: " + args);
			String targs = args.substring(args.indexOf("(") + 1, eloc).trim();
			field = UtilMethods.processToDate(lm, targs);
			args = args.substring(eloc + 1);
		}
		int cloc = args.indexOf(",");
		if (cloc > -1) {
			String format = UtilMethods.removeQuotes(args.substring(cloc + 1));
			if(format.equalsIgnoreCase("utc")){
				utcOutput = true;
			}else{
				formatter = new SimpleDateFormat(format);
			}
			date = new Date();
			args = args.substring(0, cloc);
		}
		args = args.trim();
		if (field == null) {
			field = lm.getFieldMeta(args);
			if (field == null || field.getStorageType() != FieldMeta.FIELD_DATE)
				throw new IllegalArgumentException("Unknown field: " + field);
		}
	}
}
