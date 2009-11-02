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

    $Id: QueryTester.java,v 1.2 2009/10/29 05:11:19 mreddy Exp $
*/
package test.logql.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

import com.logql.inter.QConnection;


public class QueryTester extends TestCase{
	Statement stmt;

	@Before
	public void setUp() throws Exception {
		stmt = QConnection.createStatement();
		stmt.executeQuery("from "+TestUtil.testDataDir()+"access.log use apache-common@"+TestUtil.testDataDir()+"config.xml");
	}

	public void testCount(){
		String[][] queries = {
				{"select pATh, count(*)", "187","2775"},
				{"seLEct path, count(respcode)","187","297"},
				{"select respcode, count(*)","5","2775"},
				{"select respcode, count(useragent)","5","111"},
				{"select respcode, count(path)","5","297"},
				{"select path, coUNt(usERagent), count(respCOde)","187","1133"}
		};
		TestUtil.stdTestQuery(queries, stmt);
	}

	public void testLike(){
		String[][] queries = {
				{"select path, count(*) where path like '/theme%'","7","682"},
				{"sELect path, count(*) whERe path like '%.html'","24","315"},
				{"select path, count(*) whEre path notlike '%.html'","163","2460"},
				{"select pAth, count(*) where path lIke '/graph_gallery.html%'","1","43"},
				{"select path, count(*) where path like '%/graph_gallery.html'","1","43"},
				{"select path, count(*) where path like '%8%'","28","732"},
				{"select paTh, count(*) where path like '%gallery%'","1","43"},
				{"select useRAgent, count(*) where useragent liKe 'Moz%.NET CLR%50727)'","3","700"}
		};
		TestUtil.stdTestQuery(queries, stmt);
	}

	public void testStrTok(){
		
	}
	
	public void testWhere(){
		try {
			Statement wstmt = QConnection.createStatement();
			wstmt.executeQuery("from "+TestUtil.testDataDir()+"access_small.log use apache-common@"+TestUtil.testDataDir()+"config.xml");
			String[][] queries = {
					{ "select path, count(*) where path like '%.html'", "12","20" },
					{"select path, count(*) where path like '%.html' AND host = 66.249.72.39", "8", "8" },
					{"select path, count(*) where path like '%.html' and host = 66.249.72.39 or host = 86.128.125.109", "11","14"},
					{"select path, count(*) where path like '%.html' and (host = 66.249.72.39 OR host = 86.128.125.109)","8","10"},
					{"select path, count(*) where path like '%.html' and ((host = 66.249.72.39 or host = 86.128.125.109) and respcode = 400)","2","2"}
			};
			TestUtil.stdTestQuery(queries, wstmt);
		} catch (SQLException se) {
			TestUtil.throwNullPointerException(se);
		}
	}

	public void testStringSubQueries(){
		// file 1 file 1
		try{
			Statement stmt = QConnection.createStatement();
			stmt.executeQuery("from "+TestUtil.testDataDir()+"access.log use apache-common@"+TestUtil.testDataDir()+"config.xml");
			ResultSet rs = stmt.executeQuery("select count(host) where host in "+
					"(select host where referer like '%google%') and "+
					"path = '/images/graph/graph1-s.JPG'");
			assertTrue("No reult", rs.next());
			assertTrue("Incorrect result, expecting 21, got: "+rs.getInt(1),rs.getInt(1)==21);
		}catch(SQLException se){
			TestUtil.throwNullPointerException(se);
		}
		//file 1 file 1 differant 
		try{
			Statement stmt = QConnection.createStatement();
			ResultSet rs = stmt.executeQuery("select count(host) from "+TestUtil.testDataDir()+"access.log use "+
					"apache-common@"+TestUtil.testDataDir()+"config.xml where host in (select "+
					"host from "+TestUtil.testDataDir()+"access.log use apache-common@"+TestUtil.testDataDir()+"config.xml "+
					" where referer like '%google%') and path = '/images/graph/graph1-s.JPG'");
			assertTrue("No reult", rs.next());
			assertTrue("Incorrect result, expecting 21, got: "+rs.getInt(1),rs.getInt(1)==21);
		}catch(SQLException se){
			TestUtil.throwNullPointerException(se);
		}
		// change schema
		try{
			Statement stmt = QConnection.createStatement();
			ResultSet rs = stmt.executeQuery("select count(host) from "+TestUtil.testDataDir()+"access.log use "+
					"apache-common@"+TestUtil.testDataDir()+"config.xml where host in (select "+
					"host from "+TestUtil.testDataDir()+"google-ref.txt use cSv) and path = "+
					"'/images/graph/graph1-s.JPG'");
			assertTrue("No reult", rs.next());
			assertTrue("Incorrect result, expecting 21, got: "+rs.getInt(1),rs.getInt(1)==21);
		}catch(SQLException se){
			TestUtil.throwNullPointerException(se);
		}
	}
	
	public void testStringSubQueryCondition() {
		try{
			Statement stmt = QConnection.createStatement();
			stmt.executeQuery("from "+TestUtil.testDataDir()+"access.log use apache-common@"+TestUtil.testDataDir()+"config.xml");
			ResultSet rs = stmt.executeQuery("select count(host) where host in "+
					"(select host where referer like '%google%' and path = '/graph_gallery.html') and "+
					"path = '/images/graph/graph1-s.JPG'");
			assertTrue("No reult", rs.next());
			assertTrue("Incorrect result, expecting 19, got: "+rs.getInt(1),rs.getInt(1)==19);
			
			rs = stmt.executeQuery("select count(host) where host in "+
					"(select host where referer like '%google%' or referer like '%ask.com%') and "+
					"path = '/images/graph/graph1-s.JPG'");
			assertTrue("No reult", rs.next());
			assertTrue("Incorrect result, expecting 21, got: "+rs.getInt(1),rs.getInt(1)==21);
		}catch(SQLException se){
			TestUtil.throwNullPointerException(se);
		}
	}

	public void testIntSubQueries() {
		// file 1 file 1
		try{
			Statement stmt = QConnection.createStatement();
			stmt.executeQuery("from "+TestUtil.testDataDir()+"det-small.csv use csvTestData@"+TestUtil.testDataDir()+"config.xml");
			ResultSet rs = stmt.executeQuery("select count(consumerlocation) where"+
					" consumerlocation in (select peerconsumerlocation where srcBytes > 40000)");
			assertTrue("No reult", rs.next());
			assertTrue("Incorrect result, expecting 22, got: "+rs.getInt(1),rs.getInt(1)==22);
		}catch(SQLException se){
			TestUtil.throwNullPointerException(se);
		}
		//file 1 file 1 differant 
		try{
			Statement stmt = QConnection.createStatement();
			ResultSet rs = stmt.executeQuery("select count(consumerlocation) from "+
					TestUtil.testDataDir()+"det-small.csv use "+
					"csvTestData@"+TestUtil.testDataDir()+"config.xml where "+
					"consumerlocation in (select peerconsumerlocation from "+
					TestUtil.testDataDir()+"det-err.csv use "+
					"csvTestData@"+TestUtil.testDataDir()+"config.xml "+
					"where srcBytes > 40000)");
			assertTrue("No reult", rs.next());
			assertTrue("Incorrect result, expecting 1, got: "+rs.getInt(1),rs.getInt(1)==1);
		}catch(SQLException se){
			TestUtil.throwNullPointerException(se);
		}
		// file 1 second file not mentioned
		//TODO: need full path here
		try{
			Statement stmt = QConnection.createStatement();
			stmt.executeQuery("from "+TestUtil.testDataDir()+"det-err.csv use csvTestData@"+TestUtil.testDataDir()+"config.xml");
			ResultSet rs = stmt.executeQuery("select count(consumerlocation) from "+
					TestUtil.testDataDir()+"det-small.csv use "+
					"csvTestData@"+TestUtil.testDataDir()+"config.xml where "+
					"consumerlocation in (select peerconsumerlocation  "+
					"where srcBytes > 40000)");
			assertTrue("No reult", rs.next());
			assertTrue("Incorrect result, expecting 1, got: "+rs.getInt(1),rs.getInt(1)==1);
		}catch(SQLException se){
			TestUtil.throwNullPointerException(se);
		}
	}

	public void testSticky(){
		try{
			Statement stmt = QConnection.createStatement();
			stmt.executeQuery("from "+TestUtil.testDataDir()+"access.log use apache-common@"+TestUtil.testDataDir()+"config.xml");
			ResultSet rs = stmt.executeQuery("select bytes");
			assertTrue("Test1.1",rs.next());
			assertTrue("Test1.2",rs.getLong(1) == 237013415);

			ResultSet rs2 = stmt.executeQuery("select bytes from "+TestUtil.testDataDir()+"access_small.log");
			assertTrue("Test2.1 ",rs2.next());
			assertTrue("Test2.2",rs2.getLong(1) == 2806607);

			ResultSet rs3 = stmt.executeQuery("select bytes");
			assertTrue("Test3.1",rs3.next());
			assertTrue("Test3.2",rs3.getLong(1) == 237013415);

			ResultSet rs4 = stmt.executeQuery("select sum(srcbytes) "+
					"from "+TestUtil.testDataDir()+"det-small.csv use csv(2)");
			assertTrue("Test4.1 ",rs4.next());
			double diff = 1816990254 - rs4.getDouble(1);
			assertTrue("Test4.2",diff < 0.01);

			ResultSet rs5 = stmt.executeQuery("select bytes");
			assertTrue("Test5.1",rs5.next());
			assertTrue("Test5.2",rs5.getLong(1) == 237013415);

			stmt.executeQuery("from "+TestUtil.testDataDir()+"access_small.log");

			ResultSet rs6 = stmt.executeQuery("select bytes");
			assertTrue("Test6.1",rs6.next());
			assertTrue("Test6.2",rs6.getLong(1) == 2806607);
		}catch(SQLException se){
			TestUtil.throwNullPointerException(se);
		}

		try{
			Statement stmt = QConnection.createStatement();
			stmt.executeQuery("use apache-common@"+TestUtil.testDataDir()+"config.xml");

			SQLException ex = null;
			try{
				stmt.executeQuery("select bytes");
			}catch(SQLException se){
				ex = se;
			}
			assertTrue("No exception caught: ", ex != null);
			
			ResultSet rs2 = stmt.executeQuery("select bytes from "+TestUtil.testDataDir()+"access_small.log");
			assertTrue("Test2.1.1 ",rs2.next());
			assertTrue("Test2.2.1",rs2.getLong(1) == 2806607);
		}catch(SQLException se){
			TestUtil.throwNullPointerException(se);
		}
	}

	@After
	public void tearDown() throws Exception {
		
	}

	public void testGrep(){
		String[][] queries = {
			{"grep path, bytes from "+TestUtil.testDataDir()+"access/access_log.20060501.gz use apache-common@"+TestUtil.testDataDir()+"config.xml","269","4729984"},
			{"grep path, bytes from "+TestUtil.testDataDir()+"access/access_log.2006050*.gz use apache-common@"+TestUtil.testDataDir()+"config.xml","4262","256127727"}
		};
		TestUtil.sumTestQuery(queries, stmt);		
	}

}
