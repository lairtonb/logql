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

    $Id: KeyPhrase.java,v 1.2 2009/10/29 05:11:19 mreddy Exp $
*/
package com.logql.interpret.derived;

import com.logql.interpret.func.SelectFunction;
import com.logql.meta.FlexiRow;
import com.logql.meta.LogMeta;

public class KeyPhrase extends SelectFunction {

	@Override
	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void copyToDst(FlexiRow src, FlexiRow dst) {
		// TODO Auto-generated method stub

	}

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

	public void processFunctionArgs(LogMeta lm, String args) {
		// TODO Auto-generated method stub

	}

	public void setReaderColPos(int id) {
		// TODO Auto-generated method stub

	}

}
