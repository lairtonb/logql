/*
    Copyright 2010 Manmohan Reddy

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

    $Id: Row.java,v 1.2 2009-10-29 05:11:15 mreddy Exp $
*/
package com.logql.meta.binary;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import com.logql.meta.FieldMeta;
import com.logql.meta.LogMeta;
import com.logql.meta.std.StdReadField;
import com.logql.meta.std.StdReader;
import com.logql.meta.std.StdSeperator;
import com.logql.util.InputStreamWrapper;

public class BinaryReader extends StdReader {
	DataInputStream din;
	BinReadInterface breader;

	public BinaryReader(StdReadField reader, Collection<FieldMeta> req,
			LogMeta meta) {
		super(reader, req, meta);
		breader = (BinReadInterface) reader;
	}

	protected boolean recurseMap(StdReadField sfld) {
		if (sfld.getColumnId() > -1) {
			((BinReadField)sfld).setArrPos(flexiRowMap[sfld.getColumnId()]);
		} else if (sfld instanceof StdSeperator) {
			BinReadSep sep = (BinReadSep) sfld;
			if (sep.left != null) {
				recurseMap(sep.left);
			} else {
				while (sep.right != null && ((BinReadSep) sep.right).left == null) {
					sep.collapseLeft((BinReadSep) sep.right);
					sep.right = ((BinReadSep) sep.right).right;
				}
			}
			if (sep.right != null) {
				recurseMap(sep.right);
			}
		}
		return true;
	}

	public void init(InputStreamWrapper in) throws IOException {
		breader.init(in);
		super.init(in);
	}

	public void init(InputStream fin, int skip) throws IOException {
		errLines = new ArrayList<Integer>();
		lineCount = skip;
		finished = false;
		din = new DataInputStream(new BufferedInputStream(fin));

		refill();
	}

	protected void refill() {
		pos=buffSize=0;

		for(int i=0;i<buff.length;i++){
			lineCount++;
			try {
				try {
					if (!breader.read(din, buff[i])) {
						i--;
						errLines.add(lineCount);
					} else {
						buffSize++;
					}
				} catch (EOFException e) {
					finished = true;
					din.close();
				}
			} catch (IOException ie) {
				// should not happen
				finished = true;
			}
		}
	}
}
