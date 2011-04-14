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

    $Id: TestUtil.java,v 1.3 2009-11-01 02:06:16 mreddy Exp $
*/
package test.logql.query;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

import com.logql.util.Marker;

public class TestUtil {

	public static String testDataDir() {
		//TODO: need a better way to do this
		File fromIDE = new File("./testData");

		if(fromIDE.exists())
			return "./testData/";
		return "../testData/";
	}

	public static String getString(byte[] data, Marker mark){
		return new String(data,mark.startPos, mark.endPos - mark.startPos);
	}
	public static void throwNullPointerException(Exception se) {
		NullPointerException npe = new NullPointerException(se.getMessage());
		npe.initCause(se);
		throw npe;
	}

	public static void stdTestQuery(String[][] queries, Statement stmt){
		for(String[] query:queries){
			try{
				ResultSet rs = stmt.executeQuery(query[0]);
				int rowCount = 0;
				int resultCount = 0;
				while(rs.next()){
					resultCount++;
					rowCount += rs.getLong(2);
				}
				int reqLines = Integer.parseInt(query[1]);
				int reqTotal = Integer.parseInt(query[2]);
				TestCase.assertTrue(query[0]+" - lines - "+resultCount, resultCount == reqLines);
				TestCase.assertTrue(query[0]+" - got - "+rowCount,rowCount == reqTotal);
			}catch(SQLException se){
				TestUtil.throwNullPointerException(se);	
			}
		}
	}

	public static void sumTestQuery(String[][] queries, Statement stmt){
		for(String[] query:queries){
			try{
				ResultSet rs = stmt.executeQuery(query[0]);
				int rowCount = 0;
				long resultCount = 0;
				while(rs.next()){
					resultCount++;
					rowCount += rs.getLong(2);
				}
				int reqLines = Integer.parseInt(query[1]);
				int reqTotal = Integer.parseInt(query[2]);
				TestCase.assertTrue(query[0]+" - lines - "+resultCount, resultCount == reqLines);
				TestCase.assertTrue(query[0]+" - got - "+rowCount,rowCount == reqTotal);
			}catch(SQLException se){
				TestUtil.throwNullPointerException(se);	
			}
		}
	}
}
