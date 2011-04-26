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

    $Id: OrderBy.java,v 1.2 2009/10/29 05:11:07 mreddy Exp $
*/
package com.logql.interpret;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.logql.interpret.func.SelectFunction;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.util.ArgumentsTokenizer;

public class OrderBy {
	boolean desc = false;
	ArrayList<SelectFunction> sortCols;

	public void compile(String orderClause, SelectMeta smeta) {
		orderClause = orderClause.trim();
		String lorderClause = orderClause.toLowerCase();
		sortCols = new ArrayList<SelectFunction>();

		if (lorderClause.endsWith("desc")) {
			desc = true;
			lorderClause = lorderClause.substring(0, lorderClause.length() - 4).trim();
			orderClause = orderClause.substring(0, orderClause.length() - 4).trim();
		} else if (lorderClause.endsWith("asc")) {
			lorderClause = lorderClause.substring(0, lorderClause.length() - 3).trim();
			orderClause = orderClause.substring(0, orderClause.length() - 3).trim();
		}

		ArgumentsTokenizer at = new ArgumentsTokenizer(lorderClause);
		String field = null;

		while ((field = at.nextToken()) != null) {
			boolean found = false;
			for (SelectFunction sf : smeta.getFunctions()) {
				if ((sf.getRequiredField() != null && sf.getRequiredField()
						.getName().equals(field))
						|| sf.getAlias().equals(field)) {
					sortCols.add(sf);
					found = true;
					break;
				}
			}
			if(!found){
				for(SelectFunction sf: smeta.getMetricFunctions()){
					if ((sf.getRequiredField() != null && sf.getRequiredField()
							.getName().equals(field))
							|| sf.getAlias().equals(field)) {
						sortCols.add(sf);
						found = true;
						break;
					}
				}
				if(!found){
					throw new IllegalArgumentException("Unknown field: "+field);
				}
			}
		}
	}

	public void execute(FlexiRow[] rows) {
		Comparator<FlexiRow> comp = null;
		if (sortCols.size() > 1) {
			MultiSort ms = new MultiSort();
			for (SelectFunction sf : sortCols) {
				ms.addComprator(getSorter(sf));
			}
			comp = ms;
		} else {
			comp = getSorter(sortCols.get(0));
		}
		if (desc)
			comp = new Negate(comp);
		Arrays.sort(rows, comp);
	}

	protected ColumnSort getSorter(SelectFunction sf){
		int colType =  sf.getStorageType();

		if (sf.getRequiredField() != null
				&& sf.getRequiredField().getActualType() == FieldMeta.FIELD_DATE) {
			colType = FieldMeta.FIELD_DATE;
		}

		ColumnSort ret = null;
		switch (colType) {
		case FieldMeta.FIELD_STRING:
			ret = new StringSort(); break;
		case FieldMeta.FIELD_INTEGER:
			ret = new IntSort(); break;
		case FieldMeta.FIELD_DATE:
			ret = new DateSort(); break;
		case FieldMeta.FIELD_LONG:
			ret = new LongSort(); break;
		case FieldMeta.FIELD_FLOAT:
			ret = new FloatSort(); break;
		case FieldMeta.FIELD_DOUBLE:
			ret = new DoubleSort(); break;
		}
		
		if(ret instanceof DateSort){
			ret.column = sf.getDstColPos2();
		}else{
			ret.column = sf.getDstColPos();
		}

		return ret;
	}

	class MultiSort implements Comparator<FlexiRow> {
		ArrayList<ColumnSort> sort = new ArrayList<ColumnSort>();

		public void addComprator(ColumnSort c) {
			sort.add(c);
		}

		public int compare(FlexiRow r1, FlexiRow r2) {
			for (ColumnSort cs : sort) {
				int ret = cs.compare(r1, r2);
				if (ret != 0)
					return ret;
			}
			return 0;
		}
	}

	abstract class ColumnSort implements Comparator<FlexiRow>{
		int column;
	}

	class StringSort extends ColumnSort {
		public int compare(FlexiRow r1, FlexiRow r2) {
			return r1.stringArr[column].compareTo(r2.stringArr[column]);
		}
	}

	class DateSort extends ColumnSort {
		public int compare(FlexiRow r1, FlexiRow r2) {
			return r1.dateArr[column].compareTo(r2.dateArr[column]);
		}
	}

	class IntSort extends ColumnSort {
		public int compare(FlexiRow r1, FlexiRow r2) {
			return r1.intArr[column] - r2.intArr[column];
		}
	}

	class FloatSort extends ColumnSort {
		public int compare(FlexiRow r1, FlexiRow r2) {
			return (int) (r1.floatArr[column] - r2.floatArr[column]);
		}
	}

	class LongSort extends ColumnSort {
		public int compare(FlexiRow r1, FlexiRow r2) {
			return (int) (r1.longArr[column] - r2.longArr[column]);
		}
	}

	class DoubleSort extends ColumnSort {
		public int compare(FlexiRow r1, FlexiRow r2) {
			return (int) (r1.doubleArr[column] - r2.doubleArr[column]);
		}
	}

	class Negate implements Comparator<FlexiRow> {
		Comparator<FlexiRow> comp;

		public Negate(Comparator<FlexiRow> c) {
			comp = c;
		}

		public int compare(FlexiRow r1, FlexiRow r2) {
			return comp.compare(r2, r1);
		}
	}
}
