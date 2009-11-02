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

    $Id: GrepData.java,v 1.2 2009/10/29 05:11:07 mreddy Exp $
*/
package com.logql.interpret;

import java.sql.ResultSet;

import com.logql.interpret.func.SelectFunction;
import com.logql.meta.FlexiRow;

public class GrepData extends GroupBy {

	GrepResultSet gr;
	boolean initialized;

	public GrepData(SelectMeta sm, SelectFunction[] sel) {
		super(sm, sel);
	}

	public void initialize(int[] srcMap) {
		super.initialize(srcMap);
		if(gr==null)
			gr = new GrepResultSet(smeta);
		initialized = true;
	}
	
	public boolean initialized() {
		return initialized;
	}

	public FlexiRow findOrCreate(FlexiRow fr) {
		for (SelectFunction sf : func) {
			sf.copyToTmp(fr, tempRow);
		}

		gr.add(tempRow);

		return tempRow;
	}

	public ResultSet getResultSet() {
		return gr;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Grep: \n");
		for (SelectFunction sf : func) {
			sb.append("\t").append(sf.toString()).append("\n");
		}
		return sb.toString();
	}
}
