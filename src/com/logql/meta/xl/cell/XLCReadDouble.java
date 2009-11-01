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

    $Id: XLCReadDouble.java,v 1.2 2009/10/29 05:11:18 mreddy Exp $
*/
package com.logql.meta.xl.cell;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;

import com.logql.meta.FlexiRow;
import com.logql.meta.xl.XLFieldMeta;
import com.logql.meta.xl.XLReadField;

public class XLCReadDouble extends XLReadField {
	HSSFFormulaEvaluator  evaluator;
	CellReference cref;
	double value;

	public XLCReadDouble(XLFieldMeta meta) {
		super(meta);
		cref = meta.getCref();
	}

	public boolean initRead(HSSFWorkbook workbook, HSSFSheet sheet) {
		evaluator = new HSSFFormulaEvaluator(sheet, workbook);
		HSSFRow row = sheet.getRow(cref.getRow());
		if (row != null) {
			HSSFCell cell = row.getCell(cref.getCol());
			if(cell == null){
				value = 0;
			} else {
				if(cell.getCellType() == HSSFCell.CELL_TYPE_STRING){
					value = 0;
				} else if(cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
					evaluator.setCurrentRow(row);
					HSSFFormulaEvaluator.CellValue cval = evaluator.evaluate(cell);
					if(cval.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
						value = cval.getNumberValue();
					}
				} else {
					value = cell.getNumericCellValue();
				}
			}
		}
		return true;
	}

	public boolean read(HSSFRow hrow, FlexiRow row) {
		row.doubleArr[arrPos] = value;
		return true;
	}

}
