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

    $Id: ToLower.java,v 1.2 2009/10/29 05:11:15 mreddy Exp $
*/
package com.logql.interpret.func;

import com.logql.interpret.wfunc.StringWhereFunction;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.meta.LogMeta;
import com.logql.util.Marker;
import com.logql.util.UtilMethods;

public class ToLower extends SelectFunction implements StringWhereFunction {
	public static final int _MinValue = 'A' - 1;
	public static final int _MaxValue = 'Z' + 1;
	StringWhereFunction child;
	Marker mark = new Marker();

	public void init(int[] srcMap, int[] dstMap) {
		super.init(srcMap, dstMap);
		if (child != null) {
			child.init(srcMap);
		}
	}

	//TODO: do not modify the source
	public void init(int[] srcMap) {
		super.init(srcMap);
		if (child != null)
			child.init(srcMap);
	}

	@Override
	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		if (child != null) {
			dst.charArr[dstColPos] = child.getString(src, mark);
			dst.charOffset[dstColPos] = mark.startPos;
			dst.charSiz[dstColPos] = mark.endPos - mark.startPos;
		} else {
			dst.charArr[dstColPos] = src.charArr[srcColPos];
			dst.charOffset[dstColPos] = 0;
			dst.charSiz[dstColPos] = src.charSiz[srcColPos];

			mark.endPos = src.charSiz[srcColPos];
		}
		convert(dst.charArr[dstColPos], mark);
		return true;
	}

	@Override
	public void copyToDst(FlexiRow src, FlexiRow dst) {
		dst.stringArr[dstColPos] = new String(src.charArr[dstColPos], src.charOffset[dstColPos],
				src.charSiz[dstColPos]);
		dst.charSiz[dstColPos] = src.charSiz[dstColPos];
	}

	public void convert(byte[] carr, Marker m) {
		for (int i = m.startPos; i < m.endPos; i++) {
			if (carr[i] < _MaxValue && carr[i] > _MinValue) {
				carr[i] += 32;
			}
		}
	}

	@Override
	public int getStorageType() {
		return FieldMeta.FIELD_STRING;
	}

	public byte[] getString(FlexiRow row, Marker m) {
		byte[] ret = null;
		if (child != null) {
			ret = child.getString(row, m);
		} else {
			m.startPos = 0;
			m.endPos = row.charSiz[srcColPos];
			m.lineEndPos = row.charSiz[srcColPos];
			ret = row.charArr[srcColPos];
		}
		convert(ret, m);
		return ret;
	}

	@Override
	public boolean isMetricField() {
		return false;
	}

	@Override
	public boolean requiresPostProcess() {
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
