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

    $Id: Reader.java,v 1.2 2009/10/29 05:11:16 mreddy Exp $
*/
package com.logql.meta;

import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;

public interface Reader {
	public void init(InputStream in)throws IOException;
	public void init(InputStream in, int skip)throws IOException;
	public void first();
	public void last();
	public int[] getErrors();
	public boolean hasNext();
	public boolean next();
	public String getString(int col);
	public int getInt(int col);
	public GregorianCalendar getDate(int col);
	public long getLong(int col);
	public double getDouble(int col);
	public int getLineCount();
	public Object getObject(int col);
	public FlexiRow getFlexiRow();
	public int[] getFlexiRowMap();
}
