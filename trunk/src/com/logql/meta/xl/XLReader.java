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

    $Id: XLReader.java,v 1.2 2009/10/29 05:11:09 mreddy Exp $
*/
package com.logql.meta.xl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.logql.meta.FieldMeta;
import com.logql.meta.LogMeta;
import com.logql.meta.std.StdReader;
import com.logql.meta.std.StdSeperator;

public class XLReader extends StdReader {
	HSSFSheet sheet;
	int currRow;
	int maxRow;

	public XLReader(StdSeperator xsep, Collection<FieldMeta> req, LogMeta xmeta) {
		super(xsep,req,xmeta);
	}

	public void init(InputStream in) throws IOException {
		errLines = new ArrayList<Integer>();
		lineCount = 0;

		HSSFWorkbook book = new HSSFWorkbook(in);
		finished = false;
		sheet = book.getSheet(((XLMeta)meta).getSheetName());

		((XLReadInterface) reader).initRead(book, sheet);

		currRow = ((XLMeta)meta).getMinRow() + 1;
		maxRow = ((XLMeta)meta).getMaxRow();
		if (((XLMeta) meta).hasOnlyCellFields()) {
			refillCells();
		} else {
			refill();
		}
	}

	protected void refill() {
		pos=buffSize=0;

		for (int i = 0; i < buff.length; i++) {
			lineCount++;
			HSSFRow row = sheet.getRow(currRow++);
			if (maxRow > 0) {
				if (currRow > maxRow) {
					finished = true;
					break;
				}
			} else if (row == null) {
				finished = true;
				break;
			}
			if (row == null || !((XLReadInterface) reader).read(row, buff[i])) {
				i--;
				errLines.add(lineCount);
			} else {
				buffSize++;
			}
		}
	}

	protected void refillCells() {
		pos = buffSize = 0;

		HSSFRow row = sheet.getRow(currRow++);
		finished = true;
		if (row == null || !((XLReadInterface) reader).read(row, buff[0])) {
			errLines.add(lineCount);
		} else {
			buffSize++;
		}
	}

}
