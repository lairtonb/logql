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

    $Id: XLReadString.java,v 1.2 2009/10/29 05:11:09 mreddy Exp $
*/
package com.logql.meta.xl;

import java.text.DecimalFormat;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.logql.meta.FlexiRow;

public class XLReadString extends XLReadField {
	public static final byte[] TRUE = "TRUE".getBytes();
	public static final byte[] FALSE = "FALSE".getBytes();
	DecimalFormat format = (DecimalFormat)DecimalFormat.getInstance();

	public XLReadString(XLFieldMeta meta){
		super(meta);
		format.applyPattern("#.#####");
	}

	public boolean initRead(HSSFWorkbook book, HSSFSheet sheet) {
		return true;
	}

	public boolean read(HSSFRow hrow, FlexiRow row) {
		HSSFCell cell = hrow.getCell(xlColPos);
		if(cell == null){
			row.charSiz[arrPos] = 0;
			return true;
		}
		byte[] arr = null;
		if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
			arr = format.format(cell.getNumericCellValue()).getBytes();
		} else if(cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN){
			arr = cell.getBooleanCellValue() ? TRUE : FALSE;
		} else {
			arr = cell.getRichStringCellValue().getString().getBytes();
		}
		if (row.charArr[arrPos].length < arr.length) 
			row.charArr[arrPos] = new byte[arr.length];
		System.arraycopy(arr, 0, row.charArr[arrPos], 0, arr.length);
		row.charSiz[arrPos] = arr.length;
		return true;
	}
}
