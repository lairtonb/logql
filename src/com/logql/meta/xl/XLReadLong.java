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

    $Id: XLReadLong.java,v 1.2 2009/10/29 05:11:10 mreddy Exp $
*/
package com.logql.meta.xl;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.logql.meta.FlexiRow;

public class XLReadLong extends XLReadField {
	HSSFFormulaEvaluator  evaluator;

	public XLReadLong(XLFieldMeta meta) {
		super(meta);
	}

	public boolean initRead(HSSFWorkbook book, HSSFSheet sheet) {
		evaluator = new HSSFFormulaEvaluator(sheet, book);
		return true;
	}

	public boolean read(HSSFRow hrow, FlexiRow row) {
		HSSFCell cell = hrow.getCell(xlColPos);
		if(cell == null){
			row.longArr[arrPos] = 0;
		} else {
			if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING){
				return false;
			} else if(cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
				evaluator.setCurrentRow(hrow);
				HSSFFormulaEvaluator.CellValue cval = evaluator.evaluate(cell);
				if(cval.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
					row.longArr[arrPos] = (long) cval.getNumberValue();
				}
			} else {
				row.longArr[arrPos] = (long) cell.getNumericCellValue();
			}
		}
		return true;
	}
}
