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

    $Id: XLSep.java,v 1.2 2009/10/29 05:11:09 mreddy Exp $
*/
package com.logql.meta.xl;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.meta.std.StdSeperator;
import com.logql.util.Marker;
import com.logql.util.UtilMethods;

public class XLSep extends StdSeperator implements XLReadInterface{
	public XLSep(int col) {
		super(col);
	}

	public XLSep(FieldMeta col) {
		super(col);
	}

	public boolean initRead(HSSFWorkbook book, HSSFSheet sheet) {
		if (left != null) {
			if (!((XLReadInterface) left).initRead(book, sheet)
					&& UtilMethods._AbortLine)
				return false;
		}
		if (right != null) {
			return ((XLReadInterface) right).initRead(book, sheet);
		}
		return true;
	}

	public boolean read(byte[] carr, Marker mark, FlexiRow row) {
		return false;
	}

	public boolean read(HSSFRow hrow, FlexiRow row) {
		if (left != null) {
			if (!((XLReadInterface) left).read(hrow, row)
					&& UtilMethods._AbortLine)
				return false;
		}
		if (right != null) {
			return ((XLReadInterface) right).read(hrow, row);
		}
		return true;
	}
}
