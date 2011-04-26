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

    $Id: ArgumentsTokenizer.java,v 1.2 2009/10/29 05:11:08 mreddy Exp $
*/
package com.logql.util;

public class ArgumentsTokenizer {
	String val;

	public ArgumentsTokenizer(String val) {
		this.val = val;
	}

	public String nextToken() {
		if (val == null || val.length() == 0)
			return null;
		String dval = null;
		char cep = val.charAt(0);
		if (cep == '\"' || cep == '\'') {
			int li = val.indexOf(cep, 1);
			if (li == -1)
				throw new IllegalArgumentException("Unable to find closing: " + cep);
			dval = val.substring(1, li).trim();

			li = val.indexOf(",", li);
			if (li != -1)
				val = val.substring(li + 1).trim();
			else
				val = null;

			return dval;
		} else {
			int li = val.indexOf(",");
			int bloc = val.indexOf("(");
			if(bloc < li){
				int eloc = val.indexOf(")",bloc);
				if(eloc > bloc){
					//search for ',' after eloc
					li = val.indexOf(",",eloc);
				}
			}
			if (li == -1) {
				li = 0;
				dval = val;
				val = null;
				return dval;
			}
			dval = val.substring(0, li).trim();

			val = val.substring(li + 1).trim();
			return dval;
		}
	}

	public int countTokens() {
		String temp = val;
		int ret = 0;
		for (; nextToken() != null; ret++)
			;
		val = temp;
		return ret;
	}
}
