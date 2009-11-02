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

    $Id: XLCReadInt.java,v 1.2 2009/10/29 05:11:18 mreddy Exp $
*/
package com.logql.meta.xl.cell;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;

import com.logql.meta.FlexiRow;
import com.logql.meta.xl.XLFieldMeta;
import com.logql.meta.xl.XLReadField;

public class XLCReadInt extends XLReadField{
	FormulaEvaluator  evaluator;
	int value;
	CellReference cref;

	public XLCReadInt(XLFieldMeta meta) {
		super(meta);
		cref = meta.getCref();
	}

	public boolean initRead(Workbook workbook, Sheet sheet) {
		evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		Row row = sheet.getRow(cref.getRow());
		if (row != null) {
			Cell cell = row.getCell((int)cref.getCol());
			if (cell == null) {
				value = 0;
			} else {
				if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					value = 0;
				} else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
					CellValue cval = evaluator.evaluate(cell);
					if (cval.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						value = (int) cval.getNumberValue();
					}
				} else {
					value = (int) cell.getNumericCellValue();
				}
			}
		} else {
			value = 0;
		}
		return true;
	}

	public boolean read(Row hrow, FlexiRow row) {
		row.intArr[arrPos] = value;

		return true;
	}

}
