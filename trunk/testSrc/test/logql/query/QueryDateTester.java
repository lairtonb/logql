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

    $Id: QueryDateTester.java,v 1.2 2009-10-29 05:11:19 mreddy Exp $
*/
package test.logql.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.logql.inter.QConnection;

import junit.framework.TestCase;


public class QueryDateTester extends TestCase{
	Statement stmt;

	public void setUp() throws Exception {
		stmt = QConnection.createStatement();
		stmt.executeQuery("from "+TestUtil.testDataDir()+"access-testDate.log use apache-common@"+TestUtil.testDataDir()+"config.xml");
	}

	public void testDayFunction(){
		String[][] queries = {{"select date, count(*)","8","5550"},
				{"select date, count(*) where year(date) = 2006", "4", "2775"},
				{"select date, count(*) where date > 31-dec-2005", "4", "2775"},
				{"select day(date), count(*) where date = 2005-12-31","1","845"},
				{"select day(date,'MMM-dd-yyyy'), count(*) where date < 2006-1-1","4","2775"},
				{"select date, count(*) where date # todate('7-5-2005', 'dd-MM-yyyy')","7","4752"},
				{"select date, count(*) where date <  todate('7-5-2005', 'dd-MM-yyyy')","2", "1132"}};
		TestUtil.stdTestQuery(queries, stmt);
	}

	public void testDayOfMonth() {
		String[][] queries = {{"select dayofmonth(date), count(*)","4","5550"},
				{"select dayofmonth(date), count(*) where dayofmonth(date) > 6","2","3286"},
				{"select dayofmonth(date), count(*) where dayofmonth(date) <  todate('7-5-2005', 'dd-MM-yyyy')","2","2264"}
		};
		TestUtil.stdTestQuery(queries, stmt);
	}

	public void testDayOfWeek() {
		String[][] queries = {{"select dayofweek(date), count(*)","3","5550"},
				{"select dayofweek(date), count(*) where dayofweek(date) < saturday","2","2775"},
				{"select dayofweek(date), count(*) where dayofweek(date) = todate('7-5-2005', 'dd-MM-yyyy')","1","2775"}
		};
		TestUtil.stdTestQuery(queries, stmt);
	}

	public void testHourFunction() {
		String[][] queries = {{"select hour(date), count(*)","24","5550"},
				{"select hour(date,'hh:mm a'), count(*) where hour(date) < 12","12","2646"},
				{"select hour(date), count(*) where hour(date) > todate('12:00','HH:mm')","11","2774"}};
		TestUtil.stdTestQuery(queries, stmt);
	}

	public void testMonthFunction() {
		String[][] queries = {{"select month(date), count(*)", "3","5550"},
				{"select month(date,'MMM'), count(*) where month(date) < december","2","3860"},
				{"select month(date), count(*) where month(date) = todate('12','MM')","1","1690"}};
		TestUtil.stdTestQuery(queries, stmt);
	}

	public void testWeekOfMonthFunction() {
		String[][] queries = {{"select weekofmonth(date), count(*)","4","5550"},
				{"select weekofmonth(date), count(*) where weekofmonth(date) > w4","2","1690"}};
		TestUtil.stdTestQuery(queries, stmt);
	}

	public void testWeekofYearFunction(){
		String[][] queries = {{"select weekofyear(date), count(*)","4","5550"},
				{"select weekofyear(date), count(*) where weekofyear(date) = w18","1","620"}};
		TestUtil.stdTestQuery(queries, stmt);
	}

	public void testYearFunction() {
		String[][] queries = {{"select year(date), count(*)","2","5550"},
				{"select year(date), count(*) where year(date) = 2005","1","2775"}};
		TestUtil.stdTestQuery(queries, stmt);
	}

//	public void testToDate(){
//		
//	}

	public void testUTCDate() {
		GregorianCalendar s = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		s.set(Calendar.YEAR, 1994);
		s.set(Calendar.MONTH, 4);
		s.set(Calendar.DAY_OF_MONTH, 9);
		GregorianCalendar e = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		e.set(Calendar.YEAR, 2005);
		e.set(Calendar.MONTH, 4);
		e.set(Calendar.DAY_OF_MONTH, 11);

		Statement stmt = QConnection.createStatement();
		try {
			ResultSet rs = stmt.executeQuery("select todate(starttime, utc) from "+TestUtil.testDataDir()+"nfc.txt use sep(\"|\",5)");
			rs.next();
			GregorianCalendar got = new GregorianCalendar();
			got.setTime(rs.getDate(1));
			assertTrue("Date not within range", s.before(got) && e.after(got));
		} catch (SQLException se) {
			NullPointerException npe = new NullPointerException();
			npe.initCause(se);
			throw npe;
		}

		try {
			ResultSet rs = stmt.executeQuery("select todate(starttime, utc)"+
					" from "+TestUtil.testDataDir()+"nfc.txt use nfc@"+TestUtil.testDataDir()+"config.xml");
			rs.next();
			GregorianCalendar got = new GregorianCalendar();
			got.setTime(rs.getDate(1));
			assertTrue("Date not within range (xml config)", s.before(got) && e.after(got));
		} catch (SQLException se) {
			NullPointerException npe = new NullPointerException();
			npe.initCause(se);
			throw npe;
		}
	}
}
