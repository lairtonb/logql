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

    $Id: IntToIP.java,v 1.2 2009/10/29 05:11:15 mreddy Exp $
*/
package com.logql.interpret.func;

import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.meta.LogMeta;
import com.logql.util.ArgumentsTokenizer;
import com.logql.util.UtilMethods;

public class IntToIP extends SelectFunction {
	boolean numberField;

	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		int num = 0;
		if (numberField) {
			num = src.intArr[srcColPos];
		} else {
			try {
				num = UtilMethods.parseInt(src.charArr[srcColPos], 0, src.charSiz[srcColPos]);
			} catch (NumberFormatException nfe) {
				return false;
			}
		}
		dst.stringArr[dstColPos] = UtilMethods.intToIp(num);
		return true;
	}

	public void copyToDst(FlexiRow tmp, FlexiRow dst) {
		dst.stringArr[dstColPos] = tmp.stringArr[dstColPos];
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

	public void processFunctionArgs(LogMeta lm, String args) {
		ArgumentsTokenizer at = new ArgumentsTokenizer(args);
		field = lm.getFieldMeta(at.nextToken());
		if(field == null)
			throw new IllegalArgumentException("Unknown field: " + args);
		switch (field.getActualType()) {
		case FieldMeta.FIELD_STRING:
			numberField = false;
			break;
		case FieldMeta.FIELD_INTEGER:
			numberField = true;
			break;
		default:
			throw new IllegalArgumentException("inttoip function can only be used on int or string field");
		}
	}
}
