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

    $Id: ByteStringWrapper.java,v 1.2 2009/10/29 05:11:08 mreddy Exp $
*/
package com.logql.util;

public class ByteStringWrapper {
	byte[] val;
	int epos, spos, hash;

	public ByteStringWrapper(){}

	public ByteStringWrapper(String s){
		val = s.getBytes();
		epos = val.length;
		computeHash();
	}

	public void copy(ByteStringWrapper s) {
		val = new byte[s.epos - s.spos];
		System.arraycopy(s.val, s.spos, val, 0, val.length);
		epos = s.epos;
		hash = s.hash;
	}

	public byte[] getValue(){
		return val;
	}

	public void setValue(byte[] arr, int s, int e){
		val = arr;
		spos = s;
		epos = e;
	}

	public void computeHash() {
		hash = 0;

		for (int i = spos; i < epos; i++) {
			hash = 31 * hash + val[i];
		}
	}

	public int hashCode() {
		return hash;
	}

	public boolean equals(Object o) {
		ByteStringWrapper t = (ByteStringWrapper) o;
		if (t.epos - t.spos == epos - spos) {
			int tp = t.spos;
			int p = spos;
			while (p != epos)
				if (val[p++] != t.val[tp++])
					return false;
			return true;
		}
		return false;
	}
}