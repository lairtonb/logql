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

    $Id: StdReadIP.java,v 1.2 2009/10/29 05:11:12 mreddy Exp $
*/
package com.logql.meta.std;

import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.util.Marker;

public class StdReadIP extends StdReadField {

	public StdReadIP(FieldMeta col){
		super(col);
		throw new IllegalArgumentException("No longer supported");
	}
	@Override
	public boolean read(byte[] carr, Marker mark, FlexiRow row) {
		int temp, digit, act, count;
		temp = digit = act = count = 0;
		count = 24;
		for (int i = mark.startPos; i < mark.endPos; i++) {
			if (carr[i] == '.') {
				act |= temp << count;
				count -= 8;
				temp = 0;
			} else {
				digit = Character.digit(carr[i], 10);
				if (temp == 0)
					temp = digit;
				else
					temp = temp * 10 + digit;
			}
		}
		act |= temp << count;

		row.intArr[arrPos] = act;

		return true;
	}

}
