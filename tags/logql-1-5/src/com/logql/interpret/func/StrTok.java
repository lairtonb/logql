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

    $Id: StrTok.java,v 1.2 2009/10/29 05:11:15 mreddy Exp $
*/
package com.logql.interpret.func;

import com.logql.interpret.wfunc.StringWhereFunction;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.meta.LogMeta;
import com.logql.util.ArgumentsTokenizer;
import com.logql.util.Marker;
import com.logql.util.UtilMethods;

public class StrTok extends SelectFunction implements StringWhereFunction {
	Marker mark = new Marker();
	int count = -1;
	String seperator;
	byte[] sep;
	boolean strict = true;
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
		find(dst.charArr[dstColPos], mark);
		dst.charOffset[dstColPos] = mark.startPos;
		dst.charSiz[dstColPos] = mark.endPos - mark.startPos;
		return true;
	}
	
	public void find(byte[] carr, Marker m) {
		int l = -1;
		int i = 1;
		for (; i < count; i++) {
			l = UtilMethods.indexOf(carr, sep, m);
			if (l == -1)
				break;
			m.startPos = l + 1;
		}
		l = UtilMethods.indexOf(carr, sep, m);
		if (l != -1)
			m.endPos = l;
		else if(strict && i < count)
			m.endPos = m.startPos;			
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
		find(src, m);
		return src;
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

	private static final String _ErrMsg = "Invalid args, use syntax strtok(<fieldName>, <seperator>, <count>).";
	public void processFunctionArgs(LogMeta lm, String args) {
		ArgumentsTokenizer at =new ArgumentsTokenizer(args);
		int ct = at.countTokens();
		if(ct<3)
			throw new IllegalArgumentException(_ErrMsg);

		String srcName = at.nextToken().trim();
		field = lm.getFieldMeta(srcName);
		if (field == null) {
			if (srcName.indexOf('(') > -1) {
				child = UtilMethods.processStringFunction(lm, srcName);
			} else {
				throw new IllegalArgumentException("Unknown field: " + srcName);
			}
		}

		seperator = at.nextToken();
		sep = seperator.getBytes();
		if(sep.length == 0)
			throw new IllegalArgumentException(_ErrMsg);

		try{
			count = Integer.parseInt(at.nextToken());
		}catch(NumberFormatException nfe){
			throw new IllegalArgumentException(_ErrMsg);
		}
		if(ct == 4)
			strict = at.nextToken().equalsIgnoreCase("true");
	}
}
