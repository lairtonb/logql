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

    $Id: StdReadFloat.java,v 1.2 2009/10/29 05:11:12 mreddy Exp $
*/
package com.logql.meta.std;

import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.util.Marker;
import com.logql.util.NumberParser;
import com.logql.util.UtilMethods;

public class StdReadFloat extends StdReadField {

	NumberParser p = new NumberParser();

	public StdReadFloat(FieldMeta col){
		super(col);
	}

	@Override
	public boolean read(byte[] carr, Marker mark, FlexiRow row) {
		UtilMethods.ltrim(carr, mark);
		UtilMethods.rtrim(carr, mark);
		
		try {
			if (mark.endPos > mark.startPos)
				if(!UtilMethods.hasDot(carr, mark))
					row.floatArr[arrPos] = UtilMethods.parseInt(carr, mark);
				else
					row.floatArr[arrPos] = p.readFloat(carr, mark);
			else
				row.floatArr[arrPos] = 0;
		} catch (NumberFormatException nfe) {
			row.floatArr[arrPos] = 0;
			return false;
		}
		return true;
	}

}
