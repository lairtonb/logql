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

    $Id: XLReadDouble.java,v 1.2 2009/10/29 05:11:09 mreddy Exp $
*/
package com.logql.meta.xl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.logql.meta.FlexiRow;

public class XLReadDouble extends XLReadField {
	FormulaEvaluator  evaluator;

	public XLReadDouble(XLFieldMeta meta) {
		super(meta);
	}

	public boolean initRead(Workbook book, Sheet sheet) {
		evaluator = book.getCreationHelper().createFormulaEvaluator();
		return true;
	}

	public boolean read(Row hrow, FlexiRow row) {
		Cell cell = hrow.getCell(xlColPos);
		if(cell == null){
			row.doubleArr[arrPos] = 0;
		} else {
			if(cell.getCellType() == Cell.CELL_TYPE_STRING){
				return false;
			} else if(cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
				CellValue cval = evaluator.evaluate(cell);
				if(cval.getCellType() == Cell.CELL_TYPE_NUMERIC){
					row.doubleArr[arrPos] = cval.getNumberValue();
				}
			} else {
				row.doubleArr[arrPos] = cell.getNumericCellValue();
			}
		}
		return true;
	}

}
