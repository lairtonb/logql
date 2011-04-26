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

    $Id: StdReadInt.java,v 1.2 2009-10-29 05:11:12 mreddy Exp $
*/
package com.logql.meta.binary;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.util.InputStreamWrapper;

public class BinReadJoinString extends BinReadField {
	BinFieldMeta bmeta;
	File joinFile;
	byte[][] joinData;

	public BinReadJoinString(FieldMeta meta) {
		super(meta);
		bmeta = (BinFieldMeta)meta;
	}

	public void init(InputStreamWrapper i) throws IOException {
		File f = i.getFile();
		if(f == null)
			throw new IllegalArgumentException("Zip files not supported");
		File nFile = JoinFieldsPool.getJoinFile(f, bmeta);
		if(joinFile!=null && joinFile.equals(nFile)){
			// we already have the data.
			return;
		}
		joinData = JoinFieldsPool.getKeys(nFile);
		joinFile = nFile;
		
	}

	public int byteCount() {
		return Integer.SIZE>>3;
	}

	public boolean read(DataInputStream stream, FlexiRow row)
			throws IOException {
		int val = stream.readInt();
		if(val < 0 || val >= joinData.length)
			return false; // corrupt record
		int len = joinData[val].length;
		if (row.charArr[arrPos].length < joinData[val].length) 
			row.charArr[arrPos] = new byte[len];

		System.arraycopy(joinData[val], 0, row.charArr[arrPos], 0, len);
		row.charSiz[arrPos] = len;
		return true;
	}

}
