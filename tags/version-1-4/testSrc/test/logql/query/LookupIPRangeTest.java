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

    $Id: LookupIPRangeTest.java,v 1.3 2009/10/29 05:11:19 mreddy Exp $
*/
package test.logql.query;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.logql.inter.QConnection;
import com.logql.interpret.func.LookupIPRange.RangeStruct;
import com.logql.interpret.func.LookupIPRange.RangeComparator;

import junit.framework.TestCase;

public class LookupIPRangeTest extends TestCase {
	ArrayList<RangeStruct> testList;

	public void setUp() throws Exception {
		testList = new ArrayList<RangeStruct>();
		for (int i = 1; i < 500; i += 10) {
			RangeStruct rs = new RangeStruct(i, i + 9);
			testList.add(rs);
		}
	}

	public void testSort(){
		Collections.shuffle(testList);
		Collections.sort(testList);
		
		int comp = 1;
		for (RangeStruct stu : testList) {
			assertTrue("Not sorted:" + stu.toString(), stu.getFromIp() == comp);
			comp += 10;
		}
	}

	public void testLookup(){
		int[] lookupTest = { 1, 500, 130, 20, 41, 167, 200, 315 };
		for (int i = 0; i < lookupTest.length; i++) {
			assertInRange(lookupTest[i]);
		}

		Random rand = new Random();
		for (int i = 0; i < 150; i++) {
			int lookup = rand.nextInt(500);
			if(lookup == 0) continue;
			assertInRange(lookup);
		}
	}

	private void assertInRange(int lookup){
		RangeStruct rs = new RangeStruct(lookup, 0);
		int loc = Collections.binarySearch(testList, rs, new RangeComparator());
		assertTrue("Element not found:" + lookup + " got: " + loc, loc > -1);
		RangeStruct ret = testList.get(loc);
		assertTrue("Invalid find: " + ret.toString() + " for: " + lookup, 
				ret.getFromIp() <= lookup && ret.getToIP() >= lookup);
	}

	public void testLookupQueries(){
		Statement stmt = QConnection.createStatement();
		try {
			stmt.executeQuery("from "+TestUtil.testDataDir()+"access_small.log use apache-common@"+TestUtil.testDataDir()+"config.xml");
			//TODO: should work withoutsamples dir
			String[][] queries = {
					{ "select lookupiprange(host,\""+TestUtil.testDataDir()+"ip-to-country.csv\"), count(*)", "4", "110" },
					{ "select lookupiprange(host,\""+TestUtil.testDataDir()+"ip-to-country.csv\"), count(*) where lookupiprange(host,\""+TestUtil.testDataDir()+"ip-to-country.csv\") = 'UNITED STATES'","1","72"}};
			TestUtil.stdTestQuery(queries, stmt);
		} catch (SQLException se) {
			TestUtil.throwNullPointerException(se);
		}
	}
}
