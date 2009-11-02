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

    $Id: XLCReadDate.java,v 1.2 2009/10/29 05:11:18 mreddy Exp $
*/
package com.logql.meta.xl.cell;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;

import com.logql.meta.FlexiRow;
import com.logql.meta.xl.XLFieldMeta;
import com.logql.meta.xl.XLReadField;

public class XLCReadDate extends XLReadField{
	private static final Date BAD_VALUE = new Date(0);
	CellReference cref;
	Date value;

	public XLCReadDate(XLFieldMeta meta) {
		super(meta);
		cref = meta.getCref();
	}

	public boolean initRead(Workbook workbook, Sheet sheet) {
		Row row = sheet.getRow(cref.getRow());
		if (row != null) {
			Cell cell = row.getCell(cref.getCol());
			if (cell == null) {
				value = BAD_VALUE;
			} else {
				if (cell.getCellType() == Cell.CELL_TYPE_STRING
						|| !DateUtil.isCellDateFormatted(cell)) {
					value = BAD_VALUE;
				}
				value = cell.getDateCellValue();
			}
		}
		return true;
	}

	public boolean read(Row hrow,FlexiRow row) {
		row.dateArr[arrPos].clear();
		row.dateArr[arrPos].setTime(value);

		return true;
	}
}
