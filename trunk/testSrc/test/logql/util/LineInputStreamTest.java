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

    $Id: LineInputStreamTest.java,v 1.2 2009/10/29 05:11:19 mreddy Exp $
*/
package test.logql.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import test.logql.query.TestUtil;

import junit.framework.TestCase;

import com.logql.util.LineInputStream;
import com.logql.util.Marker;

public class LineInputStreamTest extends TestCase{

	public void testRead(){
		//some lines should be CRLF others LF
		String[] tfiles = { TestUtil.testDataDir()+"sep-small.txt",
				TestUtil.testDataDir()+"det-small.csv", 
				TestUtil.testDataDir()+"access.log" };
		for (String file : tfiles) {
			File f = new File(file);
			try {
				BufferedReader in = new BufferedReader(new FileReader(f));
				LineInputStream lin = new LineInputStream(
						new FileInputStream(f));
				String buff = null;
				Marker mark = new Marker();
				int line = 0;
				while ((buff = in.readLine()) != null) {
					line++;
					byte[] arr = lin.readLine(mark);
					String lstr = new String(arr, mark.lineStartPos,
							mark.lineEndPos - mark.lineStartPos);
					assertTrue("Line: " + line, buff.equals(lstr));
					assertTrue(mark.startPos == mark.lineStartPos);
					assertTrue(mark.endPos == mark.lineEndPos);
				}
				in.close();
				lin.close();
			} catch (IOException ie) {
				NullPointerException ne = new NullPointerException();
				ne.initCause(ie);
				throw ne;
			}
		}
	}
	public void testReadCompressed(){
		
	}
}
