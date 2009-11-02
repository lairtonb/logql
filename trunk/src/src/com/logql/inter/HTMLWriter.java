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

    $Id: HTMLWriter.java,v 1.2 2009/10/29 05:11:17 mreddy Exp $
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

public class HTMLWriter implements Writer {
	private final String _Style = "<style type=\"text/css\">\n" +
		"th {\n" +
		"	color:#555580;\n" +
		"	background-color:#E4EADF;\n" +
		"	font:bold 7pt/12pt Verdana, Arial, Helvetica, sans-serif;\n" +
		"	vertical-align:top;\n" +
		"	text-align:left;\n" +
		"	padding:1px 5px 3px 5px;\n" +
		"	border-right:1px solid #CCCCDD;\n" +
		"	white-space:nowrap;\n" +
		"}\n" +
		"td {\n" +
		"	color:#000000;\n" +
		"	font:8pt/12pt Verdana, Arial, Helvetica, sans-serif;\n" +
		"	vertical-align:top;\n" +
		"	padding:3px 5px 3px 5px;\n" +
		"	border-right:1px solid #CCCCDD;\n" +
		"	border-top:1px solid #555580;\n" +
		"}\n" +
		"table {\n" +
		"	border-top:2px solid #CCCCDD;\n" +
		"	border-right:none;\n" +
		"	border-bottom:1px solid #555580;\n" +
		"	border-left:1px solid #CCCCDD;\n" +
		"}\n" +
		"b {" +
		"	font:bold 16pt Verdana, Arial, Helvetica, sans-serif;" +
		"}" +
		"table tr:hover td {\n" +
		"	background-color:#EEEEEE;\n" +
		"	border-bottom-color:#EEEEEE;\n" +
		"}\n" +
		"</style>\n";
	boolean detError;
	public void setDetailedError(boolean det) {
		detError = det;
	}

	public void write(ResultSet rs, OutputStream out) throws IOException, SQLException {
		BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(out));
		int colCount = rs.getMetaData().getColumnCount();

		bout.write("<TABLE><TR>");
		for (int i = 1; i <= colCount; i++) {
			bout.write("<TH>");
			bout.write(rs.getMetaData().getColumnLabel(i));
			bout.write("</TH>");
		}

		bout.write("</TR>");
		int row = 0;
		while (rs.next()) {
			row++;
			bout.write("<TR>");
			for (int i = 1; i <= colCount; i++) {
				bout.write("<TD>");
				bout.write(rs.getString(i));
				bout.write("</TD>");
			}
			bout.write("</TR>");
		}
		bout.write("</TABLE>");
		Map<String, int[]> errors = ((ResultSetMetaLQ)rs.getMetaData()).getErrorLines();
		bout.write("<P>"+ UtilMethods.getErrorString(errors, detError)+"</P>");
		bout.flush();
	}
}
