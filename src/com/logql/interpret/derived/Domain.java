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

    $Id: Domain.java,v 1.2 2009/10/29 05:11:19 mreddy Exp $
*/
package com.logql.interpret.derived;

import com.logql.interpret.func.SelectFunction;
import com.logql.interpret.wfunc.StringWhereFunction;
import com.logql.meta.DerivedField;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.util.Marker;
import com.logql.util.UtilMethods;

public class Domain extends SelectFunction implements StringWhereFunction {
	Marker mark = new Marker();
	final static byte[] protocol = "http://".getBytes();

	@Override
	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		dst.charArr[dstColPos] = src.charArr[srcColPos];
		mark.startPos = 0;
		mark.endPos = src.charSiz[srcColPos];
		mark.lineEndPos = src.charSiz[srcColPos];
		find(src.charArr[srcColPos], mark);
		dst.charOffset[dstColPos] = mark.startPos;
		dst.charSiz[dstColPos] = mark.endPos - mark.startPos;
		return true;
	}

	public void find(byte[] carr, Marker m) {
		if (UtilMethods.startsWith(carr, protocol, m)) {
			int i = m.startPos + protocol.length;
			while (++i < m.endPos && carr[i] != '/')
				;
			m.startPos = m.startPos + protocol.length;
			m.endPos = i;
		} else {
			m.endPos = m.startPos;
		}
	}

	@Override
	public void copyToDst(FlexiRow src, FlexiRow dst) {
		dst.stringArr[dstColPos] = new String(src.charArr[dstColPos],
				src.charOffset[dstColPos], src.charSiz[dstColPos]);
	}

	public byte[] getString(FlexiRow row, Marker m) {
		m.startPos = 0;
		m.endPos = row.charSiz[srcColPos];
		m.lineEndPos = m.endPos;
		find(row.charArr[srcColPos], m);
		return row.charArr[srcColPos];
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

	@Override
	public FieldMeta getRequiredField(){
		return ((DerivedField)field).getSourceField();
	}

	@Override
	public void setField(FieldMeta fmeta){
		field=fmeta;
	}
	
	@Override
	public int getSrcColumnId() {
		return ((DerivedField)field).getSourceField().getId();
	}
}
