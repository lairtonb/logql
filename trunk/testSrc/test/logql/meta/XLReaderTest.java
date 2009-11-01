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

    $Id: XLReaderTest.java,v 1.3 2009/10/29 05:11:07 mreddy Exp $
*/
package test.logql.meta;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.xml.sax.SAXException;

import test.logql.query.TestUtil;

import com.logql.meta.Config;
import com.logql.meta.FieldMeta;
import com.logql.meta.Reader;
import com.logql.meta.xl.XLFieldMeta;
import com.logql.meta.xl.XLMeta;

import junit.framework.TestCase;

public class XLReaderTest extends TestCase {

	public boolean validateMeta(XLMeta meta){
		String[][] colType = {{"Date","3"},
				{"ConsumerLocation","0"},
				{"PeerConsumerLocation","0"},
				{"DEVICE","0"},
				{"QOS","8"},
				{"PROTOCOL","0"},
				{"SOURCE","0"},
				{"DESTINATION","0"},
				{"SrcBytes","8"}};
		if(meta.getOrderedMeta().size() != colType.length){
			throw new IllegalArgumentException("Column count does not match");
		}
		ArrayList<XLFieldMeta> ometa = meta.getOrderedMeta();
		for(int i=0;i<ometa.size();i++){
			if(!ometa.get(i).getName().equalsIgnoreCase(colType[i][0]))
				throw new IllegalArgumentException("Column name ("
						+ ometa.get(i).getName() + ") does not match: "
						+ colType[i][0]);
			int type = Integer.parseInt(colType[i][1]);
			if(ometa.get(i).getActualType() != type)
				throw new IllegalArgumentException("Column types do not match for col: "
						+ colType[i][0]);
		}
		return true;
	}

	public void testReadMeta(){
		try {
			XLMeta xm = new XLMeta();
			xm.readConfig(new FileInputStream(TestUtil.testDataDir()+"xltest/test1.xls"));
			assertTrue("Test 1",validateMeta(xm));
		}catch(IllegalArgumentException iae){
			TestUtil.throwNullPointerException(iae);
		} catch (IOException ie) {
			TestUtil.throwNullPointerException(ie);
		}
		try {
			XLMeta xm = new XLMeta();
			xm.readConfig(new FileInputStream(TestUtil.testDataDir()+"xltest/test2.xls"));
			assertTrue("Test 2",validateMeta(xm));
		}catch(IllegalArgumentException iae){
			TestUtil.throwNullPointerException(iae);
		} catch (IOException ie) {
			TestUtil.throwNullPointerException(ie);
		}
		try {
			XLMeta xm = new XLMeta();
			xm.readConfig("Sheet2!E9:M5007", new FileInputStream(
					TestUtil.testDataDir()+"xltest/test3.xls"));
			assertTrue("Test 3",validateMeta(xm));
		}catch(IllegalArgumentException iae){
			TestUtil.throwNullPointerException(iae);
		} catch (IOException ie) {
			TestUtil.throwNullPointerException(ie);
		}		
	}

	public void testSpecifiedRange() {
		try {
			XLMeta xm = new XLMeta();
			xm.readConfig("Sheet2!E9:M5007", new FileInputStream(
					TestUtil.testDataDir()+"xltest/test3.xls"));
			ArrayList<FieldMeta> reqFields = new ArrayList<FieldMeta>();
			reqFields.add(xm.getFieldMeta("srcbytes"));
			Reader read = xm.getReader(reqFields);
			int colId = reqFields.get(0).getId();
			double sum = 0;
			read.init(new FileInputStream(TestUtil.testDataDir()+"xltest/test3.xls"));

			while (read.next()) {
				sum += read.getDouble(colId);
			}
			double diff = 1816990254 - sum;
			assertTrue("Sum is off : " + diff, diff < 0.01 && diff > -0.01);
		} catch (IOException ie) {
			TestUtil.throwNullPointerException(ie);
		}
	}

	public void testTopRightCell(){
		try {
			XLMeta xm = new XLMeta();
			xm.readConfig("Sheet2!E9", new FileInputStream(
					TestUtil.testDataDir()+"xltest/test3.xls"));
			ArrayList<FieldMeta> reqFields = new ArrayList<FieldMeta>();
			reqFields.add(xm.getFieldMeta("srcbytes"));
			Reader read = xm.getReader(reqFields);
			int colId = reqFields.get(0).getId();
			double sum = 0;
			read.init(new FileInputStream(TestUtil.testDataDir()+"xltest/test3.xls"));

			while (read.next()) {
				sum += read.getDouble(colId);
			}
			double diff = 1816990254 - sum;
			assertTrue("Sum is off : " + diff, diff < 0.01 && diff > -0.01);
		} catch (IOException ie) {
			TestUtil.throwNullPointerException(ie);
		}
	}

	public void testMetaRange (){
		try {
			XLMeta xm = (XLMeta) Config.load(TestUtil.testDataDir()+"config.xml")
				.getConfig("xlRangeTestData");

			ArrayList<FieldMeta> reqFields = new ArrayList<FieldMeta>();
			reqFields.add(xm.getFieldMeta("srcbytes"));
			Reader read = xm.getReader(reqFields);
			int colId = reqFields.get(0).getId();
			long sum = 0;
			read.init(new FileInputStream(TestUtil.testDataDir()+"xltest/test3.xls"));

			while (read.next()) {
				sum += read.getLong(colId);
			}
			long diff = 1816990254 - sum;
			assertTrue("Sum is off : " + diff, diff == 0);
		} catch (IOException ie) {
			TestUtil.throwNullPointerException(ie);
		} catch (SAXException se) {
			TestUtil.throwNullPointerException(se);
		}
	}

	public void testMetaTopRight (){
		try {
			XLMeta xm = (XLMeta) Config.load(TestUtil.testDataDir()+"config.xml")
				.getConfig("xlCellTestData");

			ArrayList<FieldMeta> reqFields = new ArrayList<FieldMeta>();
			reqFields.add(xm.getFieldMeta("srcbytes"));
			Reader read = xm.getReader(reqFields);
			int colId = reqFields.get(0).getId();
			long sum = 0;
			read.init(new FileInputStream(TestUtil.testDataDir()+"xltest/test3.xls"));

			while (read.next()) {
				sum += read.getLong(colId);
			}
			long diff = 1816990254 - sum;
			assertTrue("Sum is off : " + diff, diff == 0);
		} catch (IOException ie) {
			TestUtil.throwNullPointerException(ie);
		} catch (SAXException se) {
			TestUtil.throwNullPointerException(se);
		}
	}

	public void testMulti(){
		try {
			XLMeta xm = new XLMeta();
			xm.readConfig("Sheet2!E9", new FileInputStream(
					TestUtil.testDataDir()+"xltest/test3.xls"));
			ArrayList<FieldMeta> reqFields = new ArrayList<FieldMeta>();
			reqFields.add(xm.getFieldMeta("srcbytes"));
			Reader read = xm.getReader(reqFields);
			int colId = reqFields.get(0).getId();
			double sum = 0;
			read.init(new FileInputStream(TestUtil.testDataDir()+"xltest/test3.xls"));

			while (read.next()) {
				sum += read.getDouble(colId);
			}

			read.init(new FileInputStream(TestUtil.testDataDir()+"xltest/test3-1.xls"));

			while (read.next()) {
				sum += read.getDouble(colId);
			}
			double diff = 3633786936l - sum;
			assertTrue("Sum is off : " + diff, diff < 0.01);
		} catch (IOException ie) {
			TestUtil.throwNullPointerException(ie);
		}
	}

	public void testErrors() {
		try {
			XLMeta xm = new XLMeta();
			xm.readConfig("Sheet2!E9:M5007", new FileInputStream(
					TestUtil.testDataDir()+"xltest/test3.xls"));
			GregorianCalendar s = new GregorianCalendar(2005,8,13);
			GregorianCalendar e = new GregorianCalendar(2005,8,30);
			ArrayList<FieldMeta> reqFields = new ArrayList<FieldMeta>();
			reqFields.add(xm.getFieldMeta("date"));
			reqFields.add(xm.getFieldMeta("qos"));
			reqFields.add(xm.getFieldMeta("source"));
			Reader reader = xm.getReader(reqFields);

			int colId = reqFields.get(0).getId();
			reader.init(new FileInputStream(TestUtil.testDataDir()+"xltest/test3.xls"));

			int rlineCount = 0;
			while (reader.next()) {
				rlineCount++;
				GregorianCalendar d = reader.getDate(colId);
				assertTrue("Row: "+rlineCount,d.after(s) && d.before(e));
			}

			assertTrue("Error count: " + reader.getErrors().length, reader
					.getErrors().length == 4);
		} catch (IOException ie) {
			TestUtil.throwNullPointerException(ie);
		}
	}

	public void testAccount() {
		runAccountTest(TestUtil.testDataDir()+"xltest/account.xls");
	}

	public void testSeek() {
		// if the first cell of the table is empty, search for an entry
		// to identify column type
		runAccountTest(TestUtil.testDataDir()+"xltest/account-seek.xls");
	}

	public void runAccountTest(String file){
		//this tests for validation fields
		//boolean fields
		//conditional formating
		//formula
		try{
			XLMeta xm = new XLMeta();
			xm.readConfig(new FileInputStream(file));
			ArrayList<FieldMeta> req = new ArrayList<FieldMeta>();
			req.add(xm.getFieldMeta("Balance"));
			req.add(xm.getFieldMeta("Bank"));
			req.add(xm.getFieldMeta("Account"));
			Reader reader = xm.getReader(req);
			
			int balId = xm.getFieldMeta("Balance").getId();
			int bankId = xm.getFieldMeta("Bank").getId();
			int accId = xm.getFieldMeta("Account").getId();
			reader.init(new FileInputStream(file));
			double sum = 0;
			int line = 0;
			while(reader.next()){
				line++;
				sum += reader.getDouble(balId);
				assertTrue("Bank " + line, reader.getString(bankId).equals("ICICI")
						|| reader.getString(bankId).equals("Citibank"));
				assertTrue("Account " + line, reader.getString(accId).equals("TRUE")
						|| reader.getString(accId).equals("FALSE"));
			}
			double diff = sum - 20050491.44;
			assertTrue("Differance: "+diff,diff < .01 && diff >-0.01);
		}catch(IOException ie){
			TestUtil.throwNullPointerException(ie);
		}
	}
	
	public void testErrorHeader (){
		try{
			String[] expectedHeader = { "58210", "DESCRIPTION", "Air", "Hotel",
					"Air-F", "Phone", "Hotel-H", "Entertainment", "58210-J",
					"TOTAL" };
			XLMeta xm = new XLMeta();
			xm.readConfig("A9:K16", new FileInputStream(
					TestUtil.testDataDir()+"xltest/bad-header.xls"));
			ArrayList<XLFieldMeta> orderedMeta = xm.getOrderedMeta();
			assertTrue("Wrong number of fields: got: " + orderedMeta.size()
					+ " expected: " + expectedHeader.length,
					expectedHeader.length == orderedMeta.size());
			for (int i = 0; i < orderedMeta.size(); i++) {
				assertTrue("Wrong field name:" + orderedMeta.get(i).getName()
						+ " : " + expectedHeader[i], expectedHeader[i]
						.equals(orderedMeta.get(i).getName()));
			}
		}catch(IOException ie){
			TestUtil.throwNullPointerException(ie);
		}
	}
}
