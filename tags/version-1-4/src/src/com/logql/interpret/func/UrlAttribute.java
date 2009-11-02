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

    $Id: UrlAttribute.java,v 1.2 2009/10/29 05:11:15 mreddy Exp $
*/
package com.logql.interpret.func;

import com.logql.interpret.wfunc.StringWhereFunction;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.meta.LogMeta;
import com.logql.util.ArgumentsTokenizer;
import com.logql.util.Marker;
import com.logql.util.UtilMethods;

public class UrlAttribute extends SelectFunction implements StringWhereFunction {
	public static final byte[] UrlSeperator = "?".getBytes();
	public static final byte[] AttrSeperator = "&".getBytes();

	protected byte[] attribute;
	protected Marker mark = new Marker();
	protected URLDecode decoder = new URLDecode();
	protected boolean decode = true;
	StringWhereFunction child;

	public void init(int[] srcMap, int[] dstMap) {
		super.init(srcMap, dstMap);
		if (child != null) {
			child.init(srcMap);
		}
	}

	public void init(int[] srcMap) {
		super.init(srcMap);
		if (child != null)
			child.init(srcMap);
	}

	@Override
	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		if (child != null) {
			dst.charArr[dstColPos] = child.getString(src, mark);
		} else {
			dst.charArr[dstColPos] = src.charArr[srcColPos];
			mark.startPos = 0;
			mark.endPos = src.charSiz[srcColPos];
			mark.lineEndPos = src.charSiz[srcColPos];
		}
		dst.charArr[dstColPos] = find(dst.charArr[dstColPos], mark);
		dst.charOffset[dstColPos] = mark.startPos;
		dst.charSiz[dstColPos] = mark.endPos - mark.startPos;
		return true;
	}

	@Override
	public void copyToDst(FlexiRow src, FlexiRow dst) {
		dst.stringArr[dstColPos] = new String(src.charArr[dstColPos],
				src.charOffset[dstColPos], src.charSiz[dstColPos]);
		dst.charSiz[dstColPos] = src.charSiz[dstColPos];
	}

	public int getSrcColumnId() {
		if (child == null)
			return field.getId();
		else
			return ((SelectFunction) child).getSrcColumnId();
	}

	public FieldMeta getRequiredField() {
		if (child == null)
			return field;
		else
			return ((SelectFunction) child).getRequiredField();
	}

	@Override
	public int getStorageType() {
		return FieldMeta.FIELD_STRING;
	}

	@Override
	public boolean isMetricField() {
		return false;
	}

	@Override
	public boolean requiresPostProcess() {
		return false;
	}

	public byte[] getString(FlexiRow row, Marker m) {
		byte[] src = null;
		if (child != null) {
			src = child.getString(row, m);
		} else {
			src = row.charArr[srcColPos];
			m.startPos = 0;
			m.endPos = row.charSiz[srcColPos];
			m.lineEndPos = m.endPos;
		}
		return find(src, m);
	}

	public byte[] find(byte[] arr, Marker m){
		int spos = UtilMethods.indexOf(arr, UrlSeperator, m);
		if(spos < 0){
			// no query found on this query
			m.startPos = m.endPos;
			return arr;
		}
		m.startPos = spos + 1;
		int epos = m.endPos;
		do {
			epos = UtilMethods.indexOf(arr, AttrSeperator, m);
			if(epos == -1)
				epos = m.endPos;
			if(UtilMethods.startsWith(arr, attribute, m)){
				m.startPos+=attribute.length;
				m.endPos = epos;
				if(decode){
					return decoder.decode(arr, m);
				}
				return arr;
			}
			m.startPos = epos + 1;
		}while (m.startPos < m.endPos);
		m.startPos = m.endPos;

		return arr;
	}

	public void setDecode(boolean d){
		decode = d;
	}

	public void setAttribute(String arr) {
		if(!arr.endsWith("="))
			arr+="=";
		attribute = arr.getBytes();
	}

	private static final String _ErrMsg = "Invalid args, use syntax urlattribute(<fieldName>, <attribute>, <decode true|false>).";
	public void processFunctionArgs(LogMeta lm, String args) {
		ArgumentsTokenizer at = new ArgumentsTokenizer(args);
		int count = at.countTokens();
		if (count < 2)
			throw new IllegalArgumentException(_ErrMsg);
		// first get the field
		String fieldName = at.nextToken();
		field = lm.getFieldMeta(fieldName);
		if (field == null) {
			if (fieldName.indexOf("(") > -1) {
				child = UtilMethods.processStringFunction(lm, args);
			} else {
				throw new IllegalArgumentException("Unknown field: " + args);
			}
		}
		// get the attribute name
		String attName = at.nextToken();
		if (attName.length() == 0) {
			throw new IllegalArgumentException("Cannot proces empty attribute");
		}
		setAttribute(attName);
		//check if decode if false
		if (count >= 3) {
			String d = at.nextToken().toLowerCase();
			if (d.equals("true")) {
				decode = true;
			} else if (d.equals("false")) {
				decode = false;
			} else {
				throw new IllegalArgumentException(_ErrMsg);
			}
		}
	}
}
