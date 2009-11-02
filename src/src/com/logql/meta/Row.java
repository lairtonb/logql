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

    $Id: Row.java,v 1.2 2009/10/29 05:11:15 mreddy Exp $
*/
package com.logql.meta;

import java.util.Date;
import java.util.GregorianCalendar;

public interface Row {
	public int hashCode();

	public int getInt(int col);
	public long getLong(int col);
	public float getFloat(int col);
	public double getDouble(int col);
	public GregorianCalendar getDate(int col);
	public long getBytes(int col);
	public String getString(int col);
	public Object getObject(int col);

	public void setInt(int col, int val);
	public void setLong(int col, long val);
	public void setFloat(int col, float val);
	public void setDouble(int col, double val);
	public void setDate(int col,Date dt);
	public void setBytes(int col, long val);
	public void setString(int col, String val);
	public void setObject(int col, Object o);
}
