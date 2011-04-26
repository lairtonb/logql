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

    $Id: UtilMethodsTest.java,v 1.2 2009-10-29 05:11:19 mreddy Exp $
*/
package test.logql.util;

import test.logql.query.TestUtil;
import junit.framework.TestCase;

import com.logql.util.Marker;
import com.logql.util.UtilMethods;

public class UtilMethodsTest extends TestCase{

	public void testIndexOf() {
		String[][] tests = {{"test,all",",","1","6","4"},
				{"test,all",",","4","7","4"},
				{"test,all",",","0","5","4"},
				{"test,all",",","0","3","-1"},
				{"www.diskviz.com/themes.html",".com","3","17","11"},
				{"www.diskviz.com/themes.html",".com","11","17","11"},
				{"www.diskviz.com/themes.html",".com","3","15","11"},
				{"www.diskviz.com/themes.html",".com","3","13","-1"},
				{"www.diskviz.com/themes.html",".htm","3","14","-1"},
				{"www.diskviz.com/themes.html","www.","5","17","-1"}};

		Marker mark = new Marker();
		int count = 0;
		for(String[] test: tests){
			byte[] carr = test[0].getBytes();
			byte[] sep = test[1].getBytes();
			mark.startPos = Integer.parseInt(test[2]);
			mark.lineEndPos = Integer.parseInt(test[3]);
			int req = Integer.parseInt(test[4]);

			assertTrue((count++)+")"+test[0],UtilMethods.indexOf(carr, sep, mark) == req);
		}
	}

	public void testRemoveQuotes() {

	}

	public void testParseInt() {
		String[][] tests = { { "123454689", "0", "9" },
				{ "1493248672, something", "0", "10" },
				{ "something, 598473922", "11", "20" },
				{ "457, -3891,more", "5", "10" } };
		Marker mark = new Marker();

		for (String[] test : tests) {
			mark.startPos = Integer.parseInt(test[1]);
			mark.endPos = Integer.parseInt(test[2]);
			int req = Integer.parseInt(test[0].substring(mark.startPos,
					mark.endPos));
			int got = UtilMethods.parseInt(test[0].getBytes(), mark);
			assertTrue(test[0], req == got);
		}
	}

	public void testParseLong() {
		String[][] tests = { { "123454689", "0", "9" },
				{ "51493248672, something", "0", "11" },
				{ "something, 59847392267", "11", "22" },
				{ "457, -3891,more", "5", "10" } };
		Marker mark = new Marker();

		for (String[] test : tests) {
			mark.startPos = Integer.parseInt(test[1]);
			mark.endPos = Integer.parseInt(test[2]);
			long req = Long.parseLong(test[0].substring(mark.startPos,
					mark.endPos));
			long got = UtilMethods.parseLong(test[0].getBytes(), mark);
			assertTrue(test[0], req == got);
		}
	}
	
	public void testHasDot(){
		String[][] tests = {{"something, 234096, more","11","17"},
				{"234.3249, other","0","8"},
				{".234, adsf","0","4"},
				{"lio, 23.","5","8"},
				{"test, 234.32, sa","6","12"},
				{"12345","0","4"},
				{"123.45","0","5"}};
		Marker mark = new Marker();
		for(String[] test: tests){
			mark.startPos = Integer.parseInt(test[1]);
			mark.endPos = Integer.parseInt(test[2]);
			boolean req = test[0].substring(mark.startPos, mark.endPos).indexOf('.') > -1;
			boolean got = UtilMethods.hasDot(test[0].getBytes(), mark);
			assertTrue(test[0], req == got);
		}
	}

	public void testStringEquals(){
		String[][] tests ={{"1234testing123","test","4","8","true"},
				{"2234testing123","test","4","6","false"},
				{"3234testing123","test","4","11","false"}};
		
		Marker mark = new Marker();
		for(String[] test:tests) {
			mark.startPos=Integer.parseInt(test[2]);
			mark.endPos=Integer.parseInt(test[3]);
			boolean req = test[4].equals("true");
			boolean got = UtilMethods.stringEquals(test[0].getBytes(), test[1].getBytes(), mark);
			assertTrue(test[0],req == got);
		}
	}

	public void testStartsWith() {
		String[][] tests = { { "/themes.html", "/themes", "true" },
				{ "/themes.html", "/themes.html", "true" },
				{ "/themes.html", "themes.html", "false" },
				{ "testlen", "testlength", "false" },
				{ "http://", "ftp", "false" },
				{ "http://www.google.com/", "http://", "true" },
				{ "/", "/", "true" },
				{ "t", "f", "false" },
				{ "", "http", "false" },
				{ "http", "", "true" } };
		Marker mark = new Marker();
		for (String[] test : tests) {
			mark.startPos = 0;
			mark.endPos = test[0].length();
			mark.lineEndPos = test[0].length();
			boolean val = UtilMethods.startsWith(test[0].getBytes(), test[1]
					.getBytes(), mark);
			if (test[2].equals("true"))
				assertTrue(test[0]+", "+test[1], val);
			else
				assertFalse(test[0]+", "+test[1], val);
		}
		String testStr = "http://www.google.com/query";
		byte[] src = testStr.getBytes();
		mark.startPos = 5;
		mark.endPos = testStr.length();
		
		assertTrue(UtilMethods.startsWith(src, "//www.google.com".getBytes(), mark));
		
		mark.endPos = 8;
		assertFalse(UtilMethods.startsWith(src, "//www.google.com".getBytes(), mark));
	}

	public void testEndsWith() {
		String[][] tests = { { "/themes.html", "/themes", "false" },
				{ "/themes.html", "/themes.html", "true" },
				{ "/themes.html", ".html", "true" },
				{ "testlen", "testlength", "false" },
				{ "http://", "ftp", "false" },
				{ "http://www.google.com/", ".com/", "true" },
				{ "/", "/", "true"},
				{ "t", "f","false"},
				{ "", "http", "false"},
				{ "t", "", "true"}};
		Marker mark = new Marker();
		for (String[] test : tests) {
			mark.startPos = 0;
			mark.endPos = test[0].length();
			mark.lineEndPos = test[0].length();
			boolean val = UtilMethods.endsWith(test[0].getBytes(), test[1]
					.getBytes(), mark);
			if (test[2].equals("true"))
				assertTrue(test[0]+", "+test[1], val);
			else
				assertFalse(test[0]+", "+test[1], val);
		}

		String testStr = "http://www.google.com/query";
		byte[] src = testStr.getBytes();
		mark.startPos = 5;
		mark.endPos = 22;

		assertTrue(UtilMethods.endsWith(src, ".com/".getBytes(), mark));

		mark.endPos = 8;
		assertFalse(UtilMethods.startsWith(src, "//www.google.com".getBytes(), mark));
	}

	public void testGetFiles(){
		String[][] tests = {{TestUtil.testDataDir()+"access-may/access_log.20060508","1"},
				{TestUtil.testDataDir()+"access-may/access_log.20060508,"+
			TestUtil.testDataDir()+"access-may/access_log.20060509","2"},
				{"\""+TestUtil.testDataDir()+"access-may/access_log.*\"","8"},
				{TestUtil.testDataDir()+"access-may/access_log.*,"+
					TestUtil.testDataDir()+"access-jun/access_log.*","13"},
				{"access_log.20060508@"+TestUtil.testDataDir()+"zips/acc-may.zip","1"},
				{"access_log.*@"+TestUtil.testDataDir()+"zips/acc-may.zip","8"},
				{"access_log.20060504@"+TestUtil.testDataDir()+"zips/acc-*.zip","2"},
				{"access_log.*@"+TestUtil.testDataDir()+"zips/acc-*.zip","14"},
				{"\"access_log.*@"+TestUtil.testDataDir()+"zips/acc-may.zip\","+
					"access_log.*@"+TestUtil.testDataDir()+"zips/acc-jun.zip","14"},
				{TestUtil.testDataDir()+"zips/acc-jun.zip","7"},
/*TODO:				{"access_log*","2"},
				{"./access_log*","2"},
				{"access_log.20060523","1"}*/};
		
		for(String[] test: tests){
			int got = UtilMethods.getFiles(test[0]).size();
			int req = Integer.parseInt(test[1]);
			assertTrue(test[0],got==req);
		}
	}
	
	public void testIPConversion(){
		String[] tests = {"0.0.0.0","1.0.0.0",
				"0.1.0.0",
				"0.0.1.0",
				"0.0.0.1",
				"255.0.0.0",
				"0.255.0.0",
				"0.0.255.0",
				"0.0.0.255",
				"255.255.255.255",
				"10.11.12.13",
				"192.168.1.0",
				"192.0.168.147"};
		for (String test : tests) {
			int num = UtilMethods.ipToInt(test.getBytes(), 0, test.length());
			String val = UtilMethods.intToIp(num);
			assertTrue("String:" + test + " got: " + val, val.equals(test));
		}
	}
}
