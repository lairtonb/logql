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

    $Id: GroupBy.java,v 1.3 2009/10/29 05:11:07 mreddy Exp $
*/
package com.logql.interpret;

import java.sql.ResultSet;
import java.util.HashMap;

import com.logql.interpret.func.SelectFunction;
import com.logql.meta.FlexiRow;

public class GroupBy {
	protected SelectFunction[] func;

	HashMap<FlexiRow, FlexiRow> result = new HashMap<FlexiRow, FlexiRow>();
	protected FlexiRow tempRow;
	protected SelectMeta smeta;
	protected OrderBy order;
	GroupResultSet resultSet;

	public GroupBy(SelectMeta meta, SelectFunction[] sel) {
		smeta = meta;
		func = sel;
		tempRow = new FlexiRow(smeta.ssiz, smeta.ssiz, smeta.isiz, smeta.dtsiz,
				smeta.lsiz, smeta.fsiz, smeta.dsiz, smeta.osiz,
				smeta.flexiRowMap);
		tempRow.isTempRow = true;
	}

	public void initialize(int[] srcMap) {
		for (SelectFunction sf : func)
			sf.init(srcMap, tempRow.map);
	}

	public FlexiRow findOrCreate(FlexiRow fr) {
		for (SelectFunction sf : func) {
			sf.copyToTmp(fr, tempRow);
		}
		int h = tempRow.computeHashCode();
		FlexiRow fl = result.get(tempRow);
		if (fl == null) {
			fl = new FlexiRow(smeta.ssiz, 0, smeta.isiz, smeta.dtsiz,
					smeta.lsiz, smeta.fsiz, smeta.dsiz, smeta.osiz,
					smeta.flexiRowMap);
			for (SelectFunction sf : func)
				sf.copyToDst(tempRow, fl);
			fl.setHashCode(h);
			result.put(fl, fl);
		}
		return fl;
	}
	
	public ResultSet getResultSet() {
		if (resultSet == null) {
			resultSet = new GroupResultSet(result, smeta);

			if (order != null) {
				resultSet.order(order);
			}
		}
		return resultSet;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Group by: \n");
		for (SelectFunction sf : func) {
			sb.append("\t").append(sf.toString()).append("\n");
		}
		return sb.toString();
	}

	public OrderBy getOrder() {
		return order;
	}

	public void setOrder(OrderBy order) {
		this.order = order;
	}
}
