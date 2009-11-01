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

    $Id: XLMeta.java,v 1.2 2009/10/29 05:11:08 mreddy Exp $
*/
package com.logql.meta.xl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.hssf.util.CellReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.logql.meta.FieldMeta;
import com.logql.meta.LogMeta;
import com.logql.meta.Reader;
import com.logql.meta.std.StdReadField;
import com.logql.meta.std.StdSeperator;
import com.logql.meta.xl.cell.XLCReadDate;
import com.logql.meta.xl.cell.XLCReadDouble;
import com.logql.meta.xl.cell.XLCReadInt;
import com.logql.meta.xl.cell.XLCReadLong;
import com.logql.meta.xl.cell.XLCReadString;
import com.logql.util.UtilMethods;

public class XLMeta extends LogMeta {
	int headerLine = 1;
	ArrayList<XLFieldMeta> orderedMeta;
	HSSFWorkbook wb;
	HSSFSheet sheet;
	AreaReference range;
	short minCol, maxCol;
	int minRow, maxRow;
	boolean hasOnlyCellFields;
	String sname;

	@Override
	public Reader getReader(Collection<FieldMeta> req) {
		ArrayList<StdReadField> read=new ArrayList<StdReadField>();

		for(int i=0;i<orderedMeta.size();i++){
			StdReadField r = getReader(orderedMeta.get(i));
			read.add(r);
			read.add(new XLSep(FIELD_SEPERATOR_ID));
		}

		String cellMetricField = null;
		String metricField = null;
		for(FieldMeta fm: req){
			XLFieldMeta xfm = (XLFieldMeta)fm;
			if(xfm.isCellField){
				XLReadField rf = getReader(xfm); 
				read.add(rf);
				if(xfm.getActualType() >= FieldMeta.FIELD_LONG){
					cellMetricField = xfm.getName();
				}
				read.add(new XLSep(FIELD_SEPERATOR_ID));
			} else {
				metricField = xfm.getName();
			}
		}
		//we cannot have regular metic fields and 
		if (cellMetricField != null && metricField != null) {
			throw new IllegalArgumentException("Cannot use a regular field ("
					+ metricField + ") and metric cell field ("
					+ cellMetricField + ") in the same query");
		} 
		hasOnlyCellFields = cellMetricField != null;

		return compile(req,read);
	}

	public boolean hasOnlyCellFields (){
		return hasOnlyCellFields;
	}

	public void readConfig(Node nd) {
		super.readConfig(nd);
		Node ndrange = nd.getAttributes().getNamedItem("range");
		if (ndrange != null) {
			range = new AreaReference(ndrange.getNodeValue());
			if (range != null) {
				if (range.getCells().length < 1 || range.getCells().length >2)
					throw new IllegalArgumentException(
							"Invalid range, expecting single table or top left cell");
				if (range.getCells().length == 2) {
					CellReference cells[] = range.getCells();
					sname = cells[0].getSheetName();
					minRow = cells[0].getRow();
					minCol = cells[0].getCol();
					maxRow = cells[1].getRow() + 1;
					maxCol = (short) (cells[1].getCol() + 1);
				} else {
					CellReference cell = range.getCells()[0];
					if (cell.getSheetName() != null) {
						sname = cell.getSheetName();
					}
					minRow = cell.getRow();
					minCol = cell.getCol();

				}
			}
		}
		if(range == null)
			throw new IllegalArgumentException("Problem with range attribute");
		NodeList nl = nd.getChildNodes();
		orderedMeta = new ArrayList<XLFieldMeta>();
		short col = minCol;
		HashSet<String> fnames=new HashSet<String>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node cnd = nl.item(i);
			if (cnd.getNodeType() == Node.TEXT_NODE
					|| cnd.getNodeType() == Node.COMMENT_NODE)
				continue;
			if (cnd.getNodeName().equals("field")
					|| cnd.getNodeName().equals("derivedField")) {
				XLFieldMeta xmeta = (XLFieldMeta) getFieldMeta(cnd.getAttributes()
						.getNamedItem("name").getNodeValue());
				if(fnames.contains(xmeta.getName())){
					throw new IllegalArgumentException("Field defined twice: "
							+ xmeta.getName());
				}
				fnames.add(xmeta.getName());
				xmeta.xlcolPos = col;
				CellReference colRef = new CellReference(minRow,col);
				xmeta.colRef = colRef.toString();
				orderedMeta.add(xmeta);
				col++;
			}
		}
	}

	public FieldMeta getFieldMetaObject(){
		return new XLFieldMeta();
	}

	protected Reader getReader(StdSeperator currOp, Collection<FieldMeta> req, LogMeta lmeta) {
		return new XLReader(currOp, req, lmeta);
	}

	public String getSheetName(){
		return sname;
	}

	public int getMinRow() {
		return minRow;
	}

	public int getMaxRow() {
		return maxRow;
	}

	public XLReadField getReader(XLFieldMeta fm){
		if (fm.isCellField()) {
			switch (fm.getActualType()) {
			case FieldMeta.FIELD_STRING:
				return new XLCReadString(fm);
			case FieldMeta.FIELD_INTEGER:
				return new XLCReadInt(fm);
			case FieldMeta.FIELD_DATE:
				return new XLCReadDate(fm);
			case FieldMeta.FIELD_LONG:
				return new XLCReadLong(fm);
			case FieldMeta.FIELD_DOUBLE:
				return new XLCReadDouble(fm);
			}
		} else {
			switch (fm.getActualType()) {
			case FieldMeta.FIELD_STRING:
				return new XLReadString(fm);
			case FieldMeta.FIELD_INTEGER:
				return new XLReadInt(fm);
			case FieldMeta.FIELD_DATE:
				return new XLReadDate(fm);
			case FieldMeta.FIELD_LONG:
				return new XLReadLong(fm);
			case FieldMeta.FIELD_DOUBLE:
				return new XLReadDouble(fm);
			}
		}
		return null;
	}

	public void readConfig(String args, InputStream f) throws IOException {
		headerLine = 1;
		if (args != null && args.trim().length() > 0) {
			args = UtilMethods.removeQuotes(args);
			range = new AreaReference(args);
		}
		readConfig(f);
	}

	public void readConfig(InputStream f) throws IOException {
		wb = new HSSFWorkbook(f);
		sheet = wb.getSheetAt(0);
		if(range != null && range.getCells().length == 2){
			CellReference cells[] = range.getCells();
			sname = cells[0].getSheetName();
			if(sname == null || sname.length() == 0){
				sname = wb.getSheetName(0);
			} else {
				sheet = wb.getSheet(sname);
			}
			if(sheet == null)
				throw new IllegalArgumentException("Unknown sheet: "+sname);
			minRow = cells[0].getRow();
			minCol = cells[0].getCol();
			maxRow = cells[1].getRow() + 1;
			maxCol = (short) (cells[1].getCol() + 1);
		} else {
			HSSFRow row = null;
			if (range != null) {
				if (range.getCells().length != 1)
					throw new IllegalArgumentException(
							"Invalid range, expecting single table or top left cell");
				CellReference cell = range.getCells()[0];
				if (cell.getSheetName() != null) {
					sname = cell.getSheetName();
					sheet = wb.getSheet(sname);
					if (sheet == null)
						throw new IllegalArgumentException("Unknown sheet: " + sname);
				} else {
					sname = wb.getSheetName(0);
				}
				minRow = cell.getRow();
				minCol = cell.getCol();
				row = sheet.getRow(minRow);
			} else {
				// search
				sname = wb.getSheetName(0);
				minRow = 0;
				for (; minRow < 65000 && ((row = sheet.getRow(minRow)) == null); minRow++)
					;
				minCol = 0;
				for (; minCol < 255 && row.getCell(minCol) == null; minCol++)
					;
			}
			maxCol = minCol;
			for (; maxCol < 255 && row.getCell(maxCol) != null; maxCol++)
				;
		}
		HSSFRow hrow = sheet.getRow(minRow);
		HSSFRow trow = sheet.getRow(minRow+1);
		if(hrow == null)
			throw new IllegalArgumentException("Invalid sheet, header Row not found");
		if(trow == null)
			throw new IllegalArgumentException("Invalid sheet, table is empty?");

		orderedMeta = new ArrayList<XLFieldMeta>();
		fields = new ArrayList<FieldMeta>();
		HashSet<String> fnames=new HashSet<String>();
		for (short col = minCol; col < maxCol; col++) {
			XLFieldMeta xmeta = new XLFieldMeta();
			HSSFCell cell = hrow.getCell(col);
			if (cell == null) {
				CellReference cr = new CellReference(minRow, col);
				throw new IllegalArgumentException("Break in header at: " + cr.toString());
			}
			HSSFCell tcell = trow.getCell(col);
			if (tcell == null) {
				int lrow = maxRow > 0 ? maxRow : 0xff;
				for(short seekRow = (short)(minRow + 2); seekRow < lrow; seekRow++) {
					HSSFRow sRow = sheet.getRow(seekRow);
					if(sRow != null) {
						tcell = sRow.getCell(col);
						if(tcell != null) {
							break;
						}
					}
				}
				if (tcell == null) {
					CellReference cr = new CellReference(minRow + 1, col);
					throw new IllegalArgumentException(
							"Empty column found in row at: "
									+ cr.toString());
				}
			}

			String colName = null;
			if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
				colName = cell.getRichStringCellValue().getString();
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				colName = Double.toString(cell.getNumericCellValue());
			} else if(cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
				continue;
			} else {
				CellReference cr = new CellReference(minRow, col);
				throw new IllegalArgumentException(
						"Column names should be of type string, error at: " + cr.toString());
			}
			int sloc = colName.indexOf(" "); 
			if(sloc > -1){
				colName = colName.substring(0, sloc);
			}
			if(fnames.contains(colName)){
				colName += "-" + (char)('A'+col);
			}
			xmeta.setName(colName);
			fnames.add(colName);

			setCellType(tcell, xmeta);
			xmeta.xlcolPos = col;
			CellReference colRef = new CellReference(minRow,col);
			xmeta.colRef = colRef.toString();
			fields.add(xmeta);
			orderedMeta.add(xmeta);
		}
		compute();
	}

	protected void setCellType(HSSFCell tcell, XLFieldMeta xmeta){
		if (tcell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			xmeta.setStorageType(FieldMeta.FIELD_STRING);
		} else if (tcell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
			xmeta.setStorageType(FieldMeta.FIELD_STRING);
		} else if (tcell.getCellType() == HSSFCell.CELL_TYPE_ERROR) {
			throw new IllegalArgumentException("Unsupported cell type: " + xmeta.getName());
		} else {
			if (HSSFDateUtil.isCellDateFormatted(tcell)) {
				xmeta.setStorageType(FieldMeta.FIELD_DATE);
			} else if (tcell.getCellStyle().getDataFormat() == 
					HSSFDataFormat.getBuiltinFormat("@")) {
				xmeta.setStorageType(FieldMeta.FIELD_STRING);
			} else {
				xmeta.setStorageType(FieldMeta.FIELD_DOUBLE);
			}
		}		
	}

	public ArrayList<XLFieldMeta> getOrderedMeta(){
		return orderedMeta;
	}
	public FieldMeta getFieldMeta(String name) {
		FieldMeta ret = super.getFieldMeta(name);

		// is this a column reference?
		if (ret == null) {
			for (XLFieldMeta xmeta : orderedMeta) {
				if (xmeta.colRef.equalsIgnoreCase(name)) {
					ret = xmeta;
					break;
				}
			}
		}

		// is this a cell field?
		if( ret == null) {
			CellReference cref = new CellReference(name);
				if (cref.getSheetName() != null && cref.getSheetName().length() > 0) {
					throw new IllegalArgumentException("Cannot use sheet name when referencing a cell: "+name);
				}
				XLFieldMeta xfm = new XLFieldMeta();
				xfm.setCellField(true);
				xfm.setCref(cref);
				HSSFRow row = sheet.getRow(cref.getRow());
				if(row == null){
					throw new IllegalArgumentException("Unknown cell: "+name);
				}
				HSSFCell cell = row.getCell(cref.getCol());
				if(cell == null){
					throw new IllegalArgumentException("Unknown cell: "+name);
				}
				setCellType(cell, xfm);
				xfm.setName(cref.toString());
				fields.add(xfm);
				compute();
				ret = xfm;
			}

		return ret;
	}
}
