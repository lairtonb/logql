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

    $Id: GrepResultSet.java,v 1.2 2009-10-29 05:11:07 mreddy Exp $
*/
package com.logql.interpret.func;

import com.logql.interpret.wfunc.StringWhereFunction;
import com.logql.meta.FlexiRow;
import com.logql.util.Marker;

public class JoinFunction extends SelectFunction implements StringWhereFunction {

	@Override
	public int getStorageType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isMetricField() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean requiresPostProcess() {
		// TODO Auto-generated method stub
		return false;
	}

	public byte[] getString(FlexiRow row, Marker m) {
		// TODO Auto-generated method stub
		return null;
	}

}
