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

    $Id: WildCardMatch.java,v 1.2 2009/10/29 05:11:08 mreddy Exp $
*/
package com.logql.util;

import com.logql.interpret.wfunc.StringLike;

public class WildCardMatch {

	StringLike like;
	Marker mark = new Marker();

	public WildCardMatch(String val){
		like = new StringLike('*');
		like.processRHS(val);
	}
	
	public boolean matches(String tar){
		byte[] res = tar.getBytes();
		mark.startPos = 0;
		mark.endPos = res.length;
		mark.lineEndPos = mark.endPos;

		return like.evaluate(res, mark);
	}
}
