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

    $Id: XLReadDate.java,v 1.2 2009/10/29 05:11:10 mreddy Exp $
*/
package com.logql.meta.xl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.logql.meta.FlexiRow;

public class XLReadDate extends XLReadField {

	public XLReadDate(XLFieldMeta meta) {
		super(meta);
	}

	public boolean initRead(Workbook book, Sheet sheet) {
		return true;
	}

	public boolean read(Row hrow,FlexiRow row) {
		Cell cell = hrow.getCell(xlColPos);
		if (cell == null) {
			return false;
		} else {
			if (cell.getCellType() == Cell.CELL_TYPE_STRING
					|| !DateUtil.isCellDateFormatted(cell)) {
				return false;
			}
			row.dateArr[arrPos].clear();
			row.dateArr[arrPos].setTime(cell.getDateCellValue());
		}
		return true;
	}
}
