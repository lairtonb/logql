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

    $Id: QueryXLTest.java,v 1.2 2009-10-29 05:11:19 mreddy Exp $
*/
package test.logql.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.logql.inter.QConnection;
import junit.framework.TestCase;

public class QueryXLTest extends TestCase {

	public void testXLQueries(){
		try{
			Statement stmt = QConnection.createStatement();
			stmt.executeQuery("from "+TestUtil.testDataDir()+"xltest/test1.xls use excel");
			ResultSet rs = stmt.executeQuery("select count(*)");
			assertTrue("No results",rs.next());
			assertTrue("Count is wrong: "+rs.getLong(1), rs.getLong(1) == 4998);
		}catch(SQLException se){
			TestUtil.throwNullPointerException(se);
		}
	}

	public void testMultiXLFiles() {
		try {
			Statement stmt = QConnection.createStatement();
			stmt.executeQuery("from "+TestUtil.testDataDir()+"xltest/ER*.xls use excel(A9:K16)");
			String[][] queries = { 
					{ "select $c$6, k20", "3", "4200" },
					{ "select c6, k9", "3", "4200" },
					{"select description, d9 where air > 0","2","1966"},
					{"select month(date), total","3","4200"},
					{"select month($g$5), total","3","4200"},
					{"select month($g$5), total where month(g5) = march","1","1814"},
					{"select month($g$5), $k$20","3","4200"},
					{"select month(date), air where air > 0","3","1966"},
					{"select description, total where description = 'American Airlines'","1","1016"},
					{"select description, lodging where lodging >0","2","1800"},
					{"select weekofmonth(a9), total","4","4200"},
					{"select weekofmonth($g$5), $k$20","2","4200"}};
			for (String[] query : queries) {
				ResultSet rs = stmt.executeQuery(query[0]);
				int rowCount = 0;
				int resultCount = 0;
				while (rs.next()) {
					resultCount++;
					rowCount += rs.getDouble(2);
				}
				int reqLines = Integer.parseInt(query[1]);
				int reqTotal = Integer.parseInt(query[2]);
				TestCase.assertTrue(query[0] + " - lines - " + resultCount,
						resultCount == reqLines);
				TestCase.assertTrue(query[0] + " - got - " + rowCount,
						rowCount == reqTotal);
			}
		} catch (SQLException se) {
			TestUtil.throwNullPointerException(se);
		}
	}
	
	public void testExceptionOnMetricCellField(){
		Statement stmt = QConnection.createStatement();
		try {
			stmt.executeQuery("from "+TestUtil.testDataDir()+"xltest/ER*.xls use excel(A9:K16)");

			SQLException e = null;
			//metric field and cell metric field
			try {
				stmt.executeQuery("select $c6, air, k20");
			} catch (SQLException se) {
				e = se;
			}
			
			assertTrue("Exception expected", e != null);
			e = null;

			//group field and cell metric field
			try {
				stmt.executeQuery("select $c$6, description, $k$20");
			} catch (SQLException se) {
				e = se;
			}
			
			assertTrue("Exception expected", e != null);
		} catch (SQLException e1) {
			TestUtil.throwNullPointerException(e1);
		}
	}
}
