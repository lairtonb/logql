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

    $Id: CSVWriter.java,v 1.2 2009/10/29 04:43:39 mreddy Exp $
*/
package com.logql.inter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.logql.util.UtilMethods;

public class CSVWriter implements Writer {

	boolean detErr;

	public void setDetailedError(boolean err){
		detErr = err;
	}

	public void write(ResultSet rs, OutputStream out) throws IOException,
			SQLException {
		BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(out));
		int colCount = rs.getMetaData().getColumnCount();

		for (int i = 1; i <= colCount; i++) {
			bout.write("\"");
			bout.write(rs.getMetaData().getColumnLabel(i));
			if (i == colCount)
				bout.write("\"");
			else
				bout.write("\",");
		}
		bout.write("\r\n");

		while (rs.next()) {
			for (int i = 1; i <= colCount; i++) {
				bout.write("\"");
				bout.write(rs.getString(i));
				if (i == colCount)
					bout.write("\"");
				else
					bout.write("\",");
			}
			bout.write("\r\n");
		}
		Map<String, int[]> errors = ((ResultSetMetaLQ)rs.getMetaData()).getErrorLines();
		bout.write("\r\n " + UtilMethods.getErrorString(errors, detErr));
		bout.write("\r\n");

		bout.flush();
	}
}
