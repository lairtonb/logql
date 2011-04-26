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

    $Id: URLDecode.java,v 1.2 2009/10/29 05:11:15 mreddy Exp $
*/
package com.logql.interpret.func;

import com.logql.interpret.wfunc.StringWhereFunction;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.meta.LogMeta;
import com.logql.util.Marker;
import com.logql.util.UtilMethods;

public class URLDecode extends SelectFunction implements StringWhereFunction {
	Marker mark = new Marker();
	StringWhereFunction child;
	byte[] ret = new byte[100];

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
		dst.charArr[dstColPos] = decode(dst.charArr[dstColPos], mark);
		dst.charOffset[dstColPos] = mark.startPos;
		dst.charSiz[dstColPos] = mark.endPos - mark.startPos;
		return true;
	}

	public byte[] decode(byte[] carr, Marker m) {
		int len = m.endPos - m.startPos; 
		if( len > ret.length){
			ret = new byte[len + 50];
		}
		System.arraycopy(carr, m.startPos, ret, 0, len);
		int nstart = 0;
		m.lineStartPos = 0;
		m.startPos = 0;
		m.endPos = len;
		m.lineEndPos = len;

		int i = m.startPos;
		int ep = m.endPos - 2;
		for (; i < ep; i++) {
			if (ret[i] == '+') {
				ret[i] = ' ';
			} else if (ret[i] == '%') {
				ret[i] = convertHex(ret, i + 1);
				nstart = i + 3;
				System.arraycopy(ret, nstart, ret, i + 1, m.endPos - nstart);
				ep -= 2;
			}
		}
		m.endPos = ep + 2;
		m.lineEndPos = m.endPos;
		for (; i < m.endPos; i++) {
			if (ret[i] == '+') {
				ret[i] = ' ';
			}
		}
		return ret;
	}

	public byte convertHex(byte[] carr, int s) {
		byte ret = 0;
		ret = getDigit(carr[s]);
		ret *= 16;
		ret += getDigit(carr[s + 1]);
		return ret;
	}

	public static byte getDigit(byte b){
		switch(b){
			case '0': return 0;
			case '1': return 1;
			case '2': return 2;
			case '3': return 3;
			case '4': return 4;
			case '5': return 5;
			case '6': return 6;
			case '7': return 7;
			case '8': return 8;
			case '9': return 9;
			case 'A': return 10;
			case 'B': return 11;
			case 'C': return 12;
			case 'D': return 13;
			case 'E': return 14;
			case 'F': return 15;
			case 'a': return 10;
			case 'b': return 11;
			case 'c': return 12;
			case 'd': return 13;
			case 'e': return 14;
			case 'f': return 15;
		}
		return -1;
	}

	@Override
	public void copyToDst(FlexiRow src, FlexiRow dst) {
		dst.stringArr[dstColPos] = new String(src.charArr[dstColPos],
				src.charOffset[dstColPos], src.charSiz[dstColPos]);
		dst.charSiz[dstColPos] = src.charSiz[dstColPos];
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

		return decode(src, m);
	}

	@Override
	public int getStorageType() {
		return FieldMeta.FIELD_STRING;
	}

	@Override
	public boolean isMetricField() {
		return false;
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
	public boolean requiresPostProcess() {
		return false;
	}

	public void processFunctionArgs(LogMeta lm, String args) {
		args = args.trim();
		field = lm.getFieldMeta(args);
		if (field == null) {
			if (args.indexOf('(') > -1) {
				child = UtilMethods.processStringFunction(lm, args);
			} else {
				throw new IllegalArgumentException("Unknown field: " + args);
			}
		}
	}
}
