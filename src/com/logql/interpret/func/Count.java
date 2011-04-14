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

    $Id: Count.java,v 1.2 2009-10-29 05:11:15 mreddy Exp $
*/
package com.logql.interpret.func;

import java.util.HashSet;

import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.meta.LogMeta;
import com.logql.util.ByteStringWrapper;
import com.logql.util.IntWrapper;

public class Count extends SelectFunction {
	public static final int _NoField = 0;
	public static final int _StringField = 1;
	public static final int _IntField = 2;
	
	int dataType;
	ByteStringWrapper sholder;
	IntWrapper iholder;

	@Override
	public int getSrcColumnId() {
		return field != null ? field.getId() : -1;
	}

	@Override
	public void compute(FlexiRow src, FlexiRow dst) {
		switch (dataType) {
		case _NoField:
			dst.longArr[dstColPos]++;
			break;
		case _StringField: {
			HashSet<ByteStringWrapper> set;
			if (dst.objArr[dstColPos2] == null) {
				set = new HashSet<ByteStringWrapper>();
				dst.objArr[dstColPos2] = set;
			} else {
				set = (HashSet<ByteStringWrapper>) dst.objArr[dstColPos2];
			}
			sholder.setValue(src.charArr[srcColPos], 0, src.charSiz[srcColPos]);
			sholder.computeHash();
			if (!set.contains(sholder)) {
				ByteStringWrapper st = new ByteStringWrapper();
				st.copy(sholder);
				set.add(st);
			}
			break;
		}
		case _IntField: {
			HashSet<IntWrapper> set;
			if (dst.objArr[dstColPos2] == null) {
				set = new HashSet<IntWrapper>();
				dst.objArr[dstColPos2] = set;
			} else {
				set = (HashSet<IntWrapper>) dst.objArr[dstColPos2];
			}
			iholder.setValue(src.intArr[srcColPos]);
			if (!set.contains(iholder)) {
				IntWrapper st = new IntWrapper(iholder.getValue());
				set.add(st);
			}
			break;
		}
		}
	}

	@Override
	public void postProcess(FlexiRow prev, FlexiRow curr){
		curr.longArr[dstColPos] = ((HashSet)curr.objArr[dstColPos2]).size();
	}

	@Override
	public int getStorageType() {
		return FieldMeta.FIELD_LONG;
	}

	@Override
	public int getSecondaryStroageType() {
		return FieldMeta.FIELD_OBJECT;
	}

	@Override
	public boolean isMetricField() {
		return true;
	}

	@Override
	public boolean requiresPostProcess() {
		return dataType != _NoField;
	}

	public void processFunctionArgs(LogMeta lm, String args) {
		args = args.trim();
		if(args.length() == 0 || args.equals("*")){
			dataType = _NoField;
			return;
		}
		FieldMeta fm = lm.getFieldMeta(args);
		if (fm == null)
			throw new IllegalArgumentException("Unknown field: " + args);
		field = fm;
		if (field.getStorageType() == FieldMeta.FIELD_STRING) {
			dataType = _StringField;
			sholder = new ByteStringWrapper();
		} else if (field.getStorageType() == FieldMeta.FIELD_INTEGER) {
			dataType = _IntField;
			iholder = new IntWrapper();
		} else {
			throw new IllegalArgumentException("Cannot use: " + args
					+ " in count function");
		}
	}
}
