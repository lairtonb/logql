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

    $Id: LookupIPRange.java,v 1.2 2009/10/29 05:11:15 mreddy Exp $
*/
package com.logql.interpret.func;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.logql.inter.QConnection;
import com.logql.interpret.wfunc.StringWhereFunction;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.meta.LogMeta;
import com.logql.util.ArgumentsTokenizer;
import com.logql.util.Marker;
import com.logql.util.UtilMethods;
/**
 * Load 
 * @author mohan
 *
 */
public class LookupIPRange extends SelectFunction implements StringWhereFunction {
	public final static byte[] NO_MATCH = "--No Match--".getBytes();
	boolean numberField;

	ArrayList<RangeStruct> structs;
	ArrayList<byte[]> nameBytes;
	RangeStruct key = new RangeStruct();
	RangeComparator ncompare = new RangeComparator();

	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		int num = getIP(src, srcColPos);
		if (num == -1) {
			return false;
		}

		dst.charArr[dstColPos] = lookup(num);
		dst.charSiz[dstColPos] = dst.charArr[dstColPos].length;
		return true;
	}

	private int getIP(FlexiRow row, int pos){
		int num = 0;
		if (numberField) {
			num = row.intArr[srcColPos];
		} else {
			try {
				num = UtilMethods.ipToInt(row.charArr[pos], 0, row.charSiz[pos]);
			} catch (NumberFormatException nfe) {
				return -1;
			}
		}
		return num;
	}

	public void copyToDst(FlexiRow tmp, FlexiRow dst) {
		dst.stringArr[dstColPos]= new String(tmp.charArr[dstColPos],0,tmp.charSiz[dstColPos]);
		dst.charSiz[dstColPos] = tmp.charSiz[dstColPos];
	}

	public byte[] lookup (int num) {
		key.fromIP = num;
		int pos = Collections.binarySearch(structs, key, ncompare);
		if(pos >-1)
			return nameBytes.get(structs.get(pos).targetId);
		return NO_MATCH;
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
		int tokCount = at.countTokens();
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

		//Construct the query
		String file = "./samples/ip-to-country.csv";
		if(tokCount == 2){
			file = at.nextToken();
		}

		String query = "grep start, end, name from "+file+" use csv";

		//Execute the query
		Statement stmt = QConnection.createStatement();
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}

		//Construct structure
		HashMap<byte[], Integer> stringPos = new HashMap<byte[], Integer>();
		structs = new ArrayList<RangeStruct>();
		nameBytes = new ArrayList<byte[]>();
		try {
			while(rs.next()){
				RangeStruct st = new RangeStruct();
				byte[] f = rs.getString(1).getBytes();
				byte[] t = rs.getString(2).getBytes();
				st.fromIP = UtilMethods.ipToInt(f, 0, f.length);
				st.toIP = UtilMethods.ipToInt(t, 0, t.length);
				byte[] name = rs.getString(3).getBytes();
				Integer pos = stringPos.get(name);
				if(pos == null){
					pos = nameBytes.size();
					nameBytes.add(name);
					stringPos.put(name, pos);
				}
				st.targetId = pos;
				structs.add(st);
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
		Collections.sort(structs);
	}

	public static class RangeStruct implements Comparable<RangeStruct>{
		int fromIP;
		int toIP;
		int targetId;

		public RangeStruct(){}

		public RangeStruct(int s, int e){
			fromIP = s;
			toIP = e;
		}

		public int getFromIp() {
			return fromIP;
		}

		public int getToIP() {
			return toIP;
		}

		public int compareTo (RangeStruct s){
			if(fromIP == s.fromIP)
				return toIP - s.toIP;
			else
				return fromIP - s.fromIP;
		}

		public String toString() {
			return fromIP + " - " + toIP;
		}
	}

	public static class RangeComparator implements Comparator<RangeStruct> {
		public int compare(RangeStruct midVal, RangeStruct key) {
			if (midVal.fromIP == key.fromIP)
				return 0;
			if (midVal.toIP == key.fromIP)
				return 0;

			if (midVal.fromIP > key.fromIP) {
				// go up
				return 1;
			} else {
				if (midVal.toIP > key.fromIP) {
					return 0;
				} else {
					// go down
					return -1;
				}
			}
		}
	}

	public byte[] getString(FlexiRow row, Marker m) {
		int num = getIP(row, srcColPos);
		if (num == -1) {
			return NO_MATCH;
		}
		byte[] ret = lookup(num);
		m.endPos = ret.length;
		return ret;
	}
}
