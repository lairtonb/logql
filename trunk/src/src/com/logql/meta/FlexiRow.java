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

    $Id: FlexiRow.java,v 1.5 2009/10/29 05:11:16 mreddy Exp $
*/
package com.logql.meta;

import java.util.Date;
import java.util.GregorianCalendar;

public class FlexiRow implements Row {

	/*Standard ordering
	 * String
	 * int
	 * date
	 * long
	 * float
	 * double
	 * */
	public int[] intArr;
	public boolean[] intGroupBy;
	public GregorianCalendar[] dateArr;
	public byte[][] charArr;
	public int[] charSiz;
	public int[] charOffset;
	public long[] longArr;
	public float[] floatArr;
	public double[] doubleArr;
	public String[] stringArr;
	public Object[] objArr;
	public int hash=-1;
	public int[] map;
	public boolean isTempRow;

	public FlexiRow(int ssiz, int csiz, int isiz, int dtsiz, int lsiz,
			int fsiz, int dsiz, int osiz, int[] map) {
		if (ssiz > 0) {
			stringArr = new String[ssiz];
			charSiz = new int[ssiz];
		}
		if (csiz > 0){
			charArr = new byte[csiz][100];
			charSiz = new int[csiz];
			if (ssiz > 0) // this is the temprow
				charOffset = new int[csiz];
		}
		if (isiz > 0) {
			intArr = new int[isiz];
			intGroupBy = new boolean[isiz];
		}
		if (dtsiz > 0) {
			dateArr = new GregorianCalendar[dtsiz];
			for (int i = 0; i < dtsiz; i++) {
				dateArr[i] = new GregorianCalendar();
			}
		}
		if (lsiz > 0)
			longArr = new long[lsiz];
		if (fsiz > 0)
			floatArr = new float[fsiz];
		if (dsiz > 0)
			doubleArr = new double[dsiz];
		if (osiz > 0)
			objArr = new Object[osiz];
		this.map = map;
	}

	public long getBytes(int col) {
		return longArr[map[col]];
	}

	public double getDouble(int col) {
		return doubleArr[map[col]];
	}

	public float getFloat(int col) {
		return floatArr[map[col]];
	}

	public int getInt(int col) {
		return intArr[map[col]];
	}

	public GregorianCalendar getDate(int col) {
		return dateArr[map[col]];
	}

	public long getLong(int col) {
		return longArr[map[col]];
	}

	public Object getObject(int col) {
		return objArr[map[col]];
	}

	public String getString(int col) {
		if (stringArr == null)
			return getArrString(col);
		return stringArr[map[col]];
	}

	public String getArrString(int col) {
		int pos = map[col];
		return new String(charArr[pos], 0, charSiz[pos]);
	}

	public byte[] getStringArr(int col) {
		return charArr[map[col]];
	}

	public void setBytes(int col, long val) {
		longArr[map[col]] = val;
	}

	public void setDate(int col, Date dt) {
		intArr[map[col]] = (int) dt.getTime() / 1000;
	}

	public void setDouble(int col, double val) {
		doubleArr[map[col]] = val;
	}

	public void setFloat(int col, float val) {
		floatArr[map[col]] = val;
	}

	public void setInt(int col, int val) {
		intArr[map[col]] = val;
	}

	public void setDateAsInt(int col, int val) {
		intArr[map[col]] = val;
	}

	public void setLong(int col, long val) {
		longArr[map[col]] = val;
	}

	public void setObject(int col, Object o) {
		objArr[map[col]] = o;
	}

	public void setString(int col, String val) {
		stringArr[map[col]] = val;
	}

	public void setStringArr(int col, byte[] arr) {
		charArr[map[col]] = arr;
	}

	public int hashCode() {
		return hash;
	}

	public void setHashCode(int h) {
		hash = h;
	}

	public int computeHashCode() {
		int h = 0;
		//Called only on tempRow
		//src can be charArr or String, target is always String
		if(charSiz != null)
		for (int i = 0; i < charSiz.length; i++) {
			if (charSiz[i] <= 0) {
				if (stringArr[i] != null) {
					String tmp = stringArr[i];
					int len = tmp.length();
					for (int k = 0; k < len; k++) {
						h = 31 * h + tmp.charAt(k);
					}
				}
			} else {
				byte[] tmp = charArr[i];
				int len = charOffset[i] + charSiz[i];
				for (int k = charOffset[i]; k < len; k++) {
					h = 31 * h + tmp[k];
				}
			}
		}
		int len = intArr != null ? intArr.length : 0;
		for (int k = 0; k < len; k++) {
			if (intGroupBy[k])
				h = 31 * h + intArr[k];
		}
		hash = h;
		return h;
	}

	public boolean equals(Object o) {
		if(!isTempRow)
			return false;

		FlexiRow r = (FlexiRow) o;

		//Called only on tempRow
		//src can be charArr or String, target is always String
		if(charSiz != null)
		for (int i = 0; i < charSiz.length; i++) {
			String tar = r.stringArr[i];
			if (charSiz[i] <= 0) {
				if (stringArr[i] == null){ 
					if (tar.length() != 0) 
							return false;
				}else if (!stringArr[i].equals(tar)){
					return false;
				}
			} else {
				byte[] tmp = charArr[i];
				int tlen = r.charSiz[i]; 
				if (tlen != charSiz[i])
					return false;
				for (int j = 0, k = charOffset[i]; j < tlen; j++, k++) {
					if (tmp[k] != tar.charAt(j)) {
						if (tar.charAt(j) > 0x7F) {
							if ((tmp[k] & 0xFF) == tar.charAt(j)) {
								continue;
							}
							if (tar.charAt(j) > 0xFF) {
								char c = tar.charAt(j);
								byte b1 = (byte) (c >> 8);
								byte b2 = (byte) (c & 0xff);
								if (b1 == tmp[k] && b2 == tmp[k + 1]) {
									k++;
									continue;
								}
							}
						}
						return false;
					}
				}
			}
		}
		int len = intArr != null ? intArr.length : 0;
		int[] tArr = r.intArr;
		for (int k = 0; k < len; k++) {
			if (intGroupBy[k])
				if (intArr[k] != tArr[k])
					return false;
		}
		return true;
	}
}
