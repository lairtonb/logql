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

    $Id: StdReadDate.java,v 1.2 2009/10/29 05:11:12 mreddy Exp $
*/
package com.logql.meta.std;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.util.Marker;
import com.logql.util.UtilMethods;

public class StdReadDate extends StdReadField {

	SimpleDateFormat dateFormat;

	public StdReadDate(FieldMeta col){
		super(col);
		dateFormat=col.getDateFormater();
	}
	@Override
	public boolean read(byte[] carr, Marker mark, FlexiRow row) {
		if (dateFormat == null) {
			try {
				row.dateArr[arrPos].setTimeInMillis(UtilMethods.parseInt(carr, mark) * 1000l);
				return true;
			} catch (NumberFormatException nfe) {
				return false;
			}
		} else {
			try {
				String s = new String(carr, mark.startPos, mark.endPos - mark.startPos);
				row.dateArr[arrPos].clear();
				row.dateArr[arrPos].setTimeInMillis(dateFormat.parse(s.trim()).getTime());
				return true;
			} catch (ParseException pe) {
				row.dateArr[arrPos].setTimeInMillis(System.currentTimeMillis());
				return false;
			}
		}
	}

}
