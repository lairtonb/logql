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

    $Id: IntWrapper.java,v 1.2 2009/10/29 05:11:08 mreddy Exp $
*/
package com.logql.util;

public class IntWrapper {
	int val;

	public IntWrapper() {
	}

	public IntWrapper(int v) {
		val = v;
	}

	public int getValue() {
		return val;
	}

	public void setValue(int v) {
		val = v;
	}

	public int hashCode() {
		return val;
	}

	public boolean equals(Object o) {
		return val == ((IntWrapper) o).val;
	}
}