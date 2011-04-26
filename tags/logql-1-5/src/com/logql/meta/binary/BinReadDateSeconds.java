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

    $Id: StdReadString.java,v 1.2 2009-10-29 05:11:11 mreddy Exp $
*/
package com.logql.meta.binary;

import java.io.DataInputStream;
import java.io.IOException;

import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;

public class BinReadDateSeconds extends BinReadField {

	public BinReadDateSeconds(FieldMeta meta) {
		super(meta);
	}

	public int byteCount() {
		return Integer.SIZE>>3;
	}

	public boolean read(DataInputStream stream, FlexiRow row)
			throws IOException {
		row.dateArr[arrPos].setTimeInMillis(stream.readInt() * 1000l);
		return true;
	}

}
