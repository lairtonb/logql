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

    $Id: IntCopy.java,v 1.2 2009/10/29 05:11:14 mreddy Exp $
*/
package com.logql.interpret.func;

import com.logql.interpret.wfunc.WhereFunction;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;

public class IntCopy extends SelectFunction implements WhereFunction{

	@Override
	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		dst.intArr[dstColPos] = src.intArr[srcColPos];
		dst.intGroupBy[dstColPos] = true;
		return true;
	}

	@Override
	public void copyToDst(FlexiRow src, FlexiRow dst) {
		dst.intArr[dstColPos] = src.intArr[dstColPos];
		dst.intGroupBy[dstColPos] = true;
	}

	public int getInteger(FlexiRow row){
		return row.intArr[srcColPos];
	}

	@Override
	public int getStorageType() {
		return FieldMeta.FIELD_INTEGER;
	}

	@Override
	public boolean isMetricField() {
		return false;
	}

	@Override
	public boolean requiresPostProcess() {
		return false;
	}
}
