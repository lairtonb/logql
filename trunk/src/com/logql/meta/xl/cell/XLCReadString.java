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

    $Id: XLCReadString.java,v 1.2 2009/10/29 05:11:18 mreddy Exp $
*/
package com.logql.meta.xl.cell;

import java.text.DecimalFormat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;

import com.logql.meta.FlexiRow;
import com.logql.meta.xl.XLFieldMeta;
import com.logql.meta.xl.XLReadField;

public class XLCReadString extends XLReadField {
	public static final byte[] TRUE = "TRUE".getBytes();
	public static final byte[] FALSE = "FALSE".getBytes();
	DecimalFormat format = (DecimalFormat)DecimalFormat.getInstance();
	CellReference cref;
	byte[] value;

	public XLCReadString(XLFieldMeta meta){
		super(meta);
		format.applyPattern("#.#####");
		cref = meta.getCref();
	}

	public boolean initRead(Workbook workbook, Sheet sheet) {
		Row row = sheet.getRow(cref.getRow());
		if (row != null) {
			Cell cell = row.getCell((int)cref.getCol());
			if(cell == null){
				value = new byte[0];
				return true;
			}

			if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
				value = format.format(cell.getNumericCellValue()).getBytes();
			} else if(cell.getCellType() == Cell.CELL_TYPE_BOOLEAN){
				value = cell.getBooleanCellValue() ? TRUE : FALSE;
			} else {
				value = cell.getRichStringCellValue().getString().getBytes();
			}
		}
		return true;
	}

	public boolean read(Row hrow, FlexiRow row) {
		if (row.charArr[arrPos].length < value.length)
			row.charArr[arrPos] = new byte[value.length];
		System.arraycopy(value, 0, row.charArr[arrPos], 0, value.length);
		row.charSiz[arrPos] = value.length;

		return true;
	}
}
