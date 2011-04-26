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

    $Id: CSVSeperator.java,v 1.2 2009/10/29 05:11:11 mreddy Exp $
*/
package com.logql.meta.std;

import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.util.Marker;
import com.logql.util.UtilMethods;

public class CSVSeperator extends StdSeperator {
	static final byte[] comma = {','};
	static final byte[] quote = {'\"'};

	public CSVSeperator(int col){
		super(col);
	}
	public CSVSeperator(FieldMeta col){
		super(col);
	}

	@Override
	public boolean read(byte[] carr, Marker mark, FlexiRow row) {
		int sepPos = -1;
		if (mark.startPos != mark.lineStartPos) {
			if (carr[mark.startPos - 1] == '\"')
				sepPos = UtilMethods.indexOf(carr, quote, mark);
			else
				sepPos = UtilMethods.indexOf(carr, comma, mark);
		} else {
			//first column
			sepPos = mark.startPos;
			while (carr[sepPos] == ' ')
				sepPos++;
			if (carr[sepPos] == '\"') {
				mark.startPos = sepPos + 1;
				sepPos = UtilMethods.indexOf(carr, quote, mark);
			} else {
				sepPos = UtilMethods.indexOf(carr, comma, mark);
			}
		}

		if (sepPos == -1) {
			return false;
		}
		mark.endPos = sepPos;

		if(left!=null){
			if(!left.read(carr, mark, row) && UtilMethods._AbortLine)
				return false;
		}
		if(right!=null){
			if (carr[sepPos] == '\"') {
				mark.startPos = sepPos + 1;
				sepPos = UtilMethods.indexOf(carr, comma, mark);

				if (sepPos == -1)
					return false;
			}
			sepPos++;

			//check if a quote exists
			while(carr[sepPos] == ' ') sepPos++;
			
			if(carr[sepPos] == '\"')
				sepPos++;

			mark.startPos=sepPos;
			return right.read(carr, mark, row);
		}
		return true;
	}
}
