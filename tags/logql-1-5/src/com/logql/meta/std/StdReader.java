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

    $Id: StdReader.java,v 1.2 2009-10-29 05:11:12 mreddy Exp $
*/
package com.logql.meta.std;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.meta.LogMeta;
import com.logql.meta.Reader;
import com.logql.util.InputStreamWrapper;
import com.logql.util.LineInputStream;
import com.logql.util.Marker;
import com.logql.util.UtilMethods;

public class StdReader implements Reader {
	public static final int _BuffSize = 50;
	protected FlexiRow currRow;
	protected StdReadField reader;
	protected int lineCount;
	protected ArrayList<Integer> errLines = new ArrayList<Integer>();
	protected int[] flexiRowMap;
	protected int  ssiz, isiz, dtsiz, lsiz, fsiz, dsiz;
	protected LogMeta meta;
	protected LineInputStream in;

	public StdReader(StdReadField reader, Collection<FieldMeta> req, LogMeta meta) {
		this.reader = reader;
		this.meta = meta;
		// have to do mapping, for FlexiRow
		flexiRowMap = new int[meta.getFields().size()];
		for(int i=0;i<flexiRowMap.length;i++)
			flexiRowMap[i]=-1;

		ArrayList<FieldMeta> sreq = new ArrayList<FieldMeta>(req);
		Collections.sort(sreq);
		int pos = 0, currType = FieldMeta.FIELD_STRING;
		FieldMeta fmeta = null;
		for (int i=0;i<sreq.size();i++) {
			fmeta = sreq.get(i);
			if (fmeta.getStorageType() != currType) {
				setSize(currType, pos);
				pos = 0;
				currType = fmeta.getStorageType();
			}
			flexiRowMap[fmeta.getId()] = pos++;
		}
		setSize(currType, pos);

		recurseMap(reader);

		buff = new FlexiRow[_BuffSize];
		for (int i = 0; i < buff.length; i++)
			buff[i] = new FlexiRow(0, ssiz, isiz, dtsiz, lsiz, fsiz, dsiz, 0,
					flexiRowMap);
	}

	public int[] getFlexiRowMap() {
		return flexiRowMap;
	}

	private void setSize(int type, int pos){
		switch (type) {
		case FieldMeta.FIELD_STRING:
			ssiz = pos;
			break;
		case FieldMeta.FIELD_INTEGER:
			isiz = pos;
			break;
		case FieldMeta.FIELD_DATE:
			dtsiz=pos;
			break;
		case FieldMeta.FIELD_LONG:
			lsiz = pos;
			break;
		case FieldMeta.FIELD_FLOAT:
			fsiz = pos;
			break;
		case FieldMeta.FIELD_DOUBLE:
			dsiz = pos;
			break;
		}
	}

	public int getLineCount(){
		return lineCount;
	}

	protected boolean recurseMap(StdReadField sfld) {
		if (sfld.getColumnId() > -1) {
			sfld.arrPos = flexiRowMap[sfld.getColumnId()];
			return true;
		} else if (sfld instanceof StdSeperator) {
			StdSeperator sep = (StdSeperator) sfld;
			boolean hc = false;
			if (sep.left != null) {
				hc = recurseMap(sep.left);
				if (!hc)
					sep.left = null;
			}
			if (sep.right != null) {
				hc = recurseMap(sep.right);
				if (!hc)
					sep.right = null;
			}
			return sep.left != null || sep.right != null;
		}
		return false;
	}

	public void init(InputStreamWrapper wrapper) throws IOException {
		init(wrapper.getInputStream());
	}

	public void init(InputStream fin) throws IOException {
		init(fin, meta.getSkip());
	}

	public void init(InputStream fin, int skip) throws IOException {
		errLines = UtilMethods._ErrorDetails ? new ArrayList<Integer>():
			new ArrayList<Integer>(){
			    int lsize = 0;
					public boolean add(Integer e) {
						lsize++;
						return true;
					}

					public int size() {
						return lsize;
					}
					
					public Integer get(int i) {
						throw new IllegalArgumentException("Error details not enabled");
					}
			};
		lineCount = skip;
		finished = false;
		in = new LineInputStream(fin);
		for(int i=0;i<skip;i++)
			in.readLine(mark);
		refill();
	}

	public double getDouble(int col) {
		return currRow.getDouble(col);
	}

	public FlexiRow getFlexiRow(){
		return currRow;
	}

	public int getInt(int col) {
		return currRow.getInt(col);
	}
	
	public GregorianCalendar getDate(int col){
		return currRow.getDate(col);
	}

	public long getLong(int col) {
		return currRow.getLong(col);
	}

	public Object getObject(int col) {
		return null;
	}

	public String getString(int col) {
		return currRow.getString(col);
	}

	protected int pos = 0, buffSize = 0, currLine = 0;
	protected boolean finished;
	protected FlexiRow[] buff;

	public boolean hasNext() {
		return !(pos == buffSize && finished);
	}

	public void last() {
		// not supported
	}

	public void first() {
		// not supported
	}

	public List<Integer> getErrors() {
		return errLines;
	}

	public boolean next() {
		if (pos < buffSize) {
			currRow = buff[pos++];
			return true;
		} else if (!finished) {
			refill();
			pos = 0;
			if (pos < buffSize) {
				currRow = buff[pos++];
				return true;
			}
		}
		return false;
	}

	Marker mark=new Marker();
	protected void refill() {
		pos=buffSize=0;

		byte[] bbuff=null;
		for(int i=0;i<buff.length;i++){
			lineCount++;
			try {
				bbuff = in.readLine(mark);
				if (bbuff == null){
					finished = true;
					in.close();
					break;
				}
				if(!reader.read(bbuff, mark, buff[i])){
					i--;
					errLines.add(lineCount);
				}
				else {
					buffSize++;
				}
			} catch (IOException ie) {
				// should not happen
				finished = true;
			}
		}
	}
}
