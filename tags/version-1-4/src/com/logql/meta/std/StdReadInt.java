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

    $Id: StdReadInt.java,v 1.2 2009/10/29 05:11:12 mreddy Exp $
*/
package com.logql.meta.std;

import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.util.Marker;
import com.logql.util.UtilMethods;

public class StdReadInt extends StdReadField {

	public StdReadInt(FieldMeta col){
		super(col);
	}
	@Override
	public boolean read(byte[] carr, Marker mark, FlexiRow row) {
		try {
			row.intArr[arrPos] = UtilMethods.parseInt(carr, mark.startPos, mark.endPos);
			return true;
		} catch (NumberFormatException nfe) {
			row.intArr[arrPos] = 0;
			return false;
		}
	}

}
