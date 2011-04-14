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

    $Id: CSVReaderTest.java,v 1.3 2009-10-29 05:11:07 mreddy Exp $
*/
package test.logql.meta;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashSet;

import test.logql.query.TestUtil;

import com.logql.meta.Config;
import com.logql.meta.FieldMeta;
import com.logql.meta.LogMeta;
import com.logql.meta.Reader;
import com.logql.meta.std.CSVMeta;
import com.logql.meta.std.StdReader;

import junit.framework.TestCase;

public class CSVReaderTest extends TestCase {
	Config conf ;

	public void setUp() throws Exception {
		conf = Config.load(TestUtil.testDataDir()+"config.xml");
	}

	public void testReadHeader() {
		CSVMeta cmeta = new CSVMeta();
		String[] areq = { "StartDate", "EndDate", "ServiceType", "Provider",
				"Consumer", "PeerConsumer", "Period", "ServiceLocation",
				"ConsumerLocation", "PeerConsumerLocation", "DEVICE", "QOS",
				"PROTOCOL", "SOURCE", "DESTINATION", "SrcBytes",
				"BILLABLE_BYTES", "Empty", "GroupId", "BillingRecordTypeId",
				"IsVolatile", "Amount" };
		HashSet<String> req = new HashSet<String>(Arrays.asList(areq));
		try {
			FileInputStream fis = new FileInputStream(
					TestUtil.testDataDir()+"det-small.csv");
			cmeta.readConfig("1", fis);
			fis.close();
			ArrayList<FieldMeta> fm = cmeta.getFields();
			for (FieldMeta met : fm) {
				req.remove(met.getName());
			}
			assert (req.isEmpty());
		} catch (IOException ie) {
			TestUtil.throwNullPointerException(ie);
		}
	}

	public void testRead (){
		LogMeta meta = conf.getConfig("csvTestData");
		File f = new File(TestUtil.testDataDir()+"det-small.csv");
		double reqAmtSum = 18169902.54;
		long reqSrcBytes = 1816990254;
		double amtSum  = 0;
		long bytesSum = 0;
		GregorianCalendar s = new GregorianCalendar(2005,8,14);
		GregorianCalendar e = new GregorianCalendar(2005,8,16);

		try {
			ArrayList<FieldMeta> reqFields = new ArrayList<FieldMeta>();
			int dtCol = meta.getFieldMeta("StartDate").getId();
			reqFields.add(meta.getFieldMeta("StartDate"));
			reqFields.add(meta.getFieldMeta("Consumer"));
			reqFields.add(meta.getFieldMeta("ConsumerLocation"));
			reqFields.add(meta.getFieldMeta("PROTOCOL"));
			reqFields.add(meta.getFieldMeta("SrcBytes"));
			int bytesCol = meta.getFieldMeta("SrcBytes").getId();
			reqFields.add(meta.getFieldMeta("Amount"));
			int amtCol = meta.getFieldMeta("Amount").getId();
			Reader reader = meta.getReader(reqFields);
			reader.init(new FileInputStream(f));

			int rlineCount = 0;
			while (reader.next()) {
				rlineCount++;
				GregorianCalendar d = reader.getDate(dtCol);
				assertTrue("Row: "+rlineCount,d.after(s) && d.before(e));
				amtSum += reader.getDouble(amtCol);
				bytesSum += reader.getLong(bytesCol);
			}

			assertTrue("Line counts do not match", rlineCount == 4998);
			assertTrue("Amount is off",Math.abs(reqAmtSum - amtSum) < 1);
			assertTrue("Bytes are off",bytesSum == reqSrcBytes);
		} catch (IOException ie) {
			TestUtil.throwNullPointerException(ie);
		}
	}

	public void testReadMulti(){
		LogMeta meta = conf.getConfig("csvTestData");
		File f = new File(TestUtil.testDataDir()+"det-small.csv");
		double reqAmtSum = 36339805.08;
		long reqSrcBytes = 3633980508l;
		double amtSum  = 0;
		long bytesSum = 0;
		GregorianCalendar s = new GregorianCalendar(2005,8,14);
		GregorianCalendar e = new GregorianCalendar(2005,8,16);

		try {
			ArrayList<FieldMeta> reqFields = new ArrayList<FieldMeta>();
			int dtCol = meta.getFieldMeta("StartDate").getId();
			reqFields.add(meta.getFieldMeta("StartDate"));
			reqFields.add(meta.getFieldMeta("Consumer"));
			reqFields.add(meta.getFieldMeta("ConsumerLocation"));
			reqFields.add(meta.getFieldMeta("PROTOCOL"));
			reqFields.add(meta.getFieldMeta("SrcBytes"));
			int bytesCol = meta.getFieldMeta("SrcBytes").getId();
			reqFields.add(meta.getFieldMeta("Amount"));
			int amtCol = meta.getFieldMeta("Amount").getId();
			Reader reader = meta.getReader(reqFields);
			reader.init(new FileInputStream(f));

			int rlineCount = 0;
			while (reader.next()) {
				rlineCount++;
				GregorianCalendar d = reader.getDate(dtCol);
				assertTrue("Row: "+rlineCount,d.after(s) && d.before(e));
				amtSum += reader.getDouble(amtCol);
				bytesSum += reader.getLong(bytesCol);
			}

			reader.init(new FileInputStream(f));

			while (reader.next()) {
				rlineCount++;
				GregorianCalendar d = reader.getDate(dtCol);
				assertTrue("Row: "+rlineCount,d.after(s) && d.before(e));
				amtSum += reader.getDouble(amtCol);
				bytesSum += reader.getLong(bytesCol);
			}

			assertTrue("Line counts do not match", rlineCount == 9996);
			assertTrue("Amount is off",Math.abs(reqAmtSum - amtSum) < 1);
			assertTrue("Bytes are off",bytesSum == reqSrcBytes);
		} catch (IOException ie) {
			TestUtil.throwNullPointerException(ie);
		}
	}

	public void testReadError() {
		LogMeta meta = conf.getConfig("csvTestData");
		File f = new File(TestUtil.testDataDir()+"det-err.csv");
		// 9, 14, 17-, 21, 25, 41, 53

		try {
			ArrayList<FieldMeta> reqFields = new ArrayList<FieldMeta>();
			reqFields.add(meta.getFieldMeta("StartDate"));
			reqFields.add(meta.getFieldMeta("Consumer"));
			reqFields.add(meta.getFieldMeta("ConsumerLocation"));
			reqFields.add(meta.getFieldMeta("PROTOCOL"));
			reqFields.add(meta.getFieldMeta("SrcBytes"));
			reqFields.add(meta.getFieldMeta("Amount"));
			StdReader reader = (StdReader) meta.getReader(reqFields);
			reader.init(new FileInputStream(f));

			int rlineCount = 0;
			while (reader.next()) {
				rlineCount++;
			}

			assertTrue("Line counts do not match", rlineCount == 73);
			assertTrue("Error count is wrong", reader.getErrors().size() == 6);
		} catch (IOException ie) {
			TestUtil.throwNullPointerException(ie);
		}
	}
	
	public void testEmptySeperator(){
		try{
			CSVMeta meta = new CSVMeta();
			BufferedInputStream rin =new BufferedInputStream(
					new FileInputStream(TestUtil.testDataDir()+"google-ref.txt")); 
			meta.readConfig(rin);
			rin.close();

			ArrayList<FieldMeta> reqFields = new ArrayList<FieldMeta>();
			reqFields.add(meta.getFieldMeta("host"));
			int col = meta.getFieldMeta("host").getId();
			StdReader reader = (StdReader) meta.getReader(reqFields);
			rin = new BufferedInputStream(new FileInputStream(
					TestUtil.testDataDir()+"google-ref.txt"));
			reader.init(rin);
			int rlineCount = 0;
			while(reader.next()){
				rlineCount++;
				String s = reader.getString(col).trim();
				assertFalse("quote found: "+rlineCount, s.startsWith("\"") || s.endsWith("\""));
			}
		}catch(IOException ie){
			TestUtil.throwNullPointerException(ie);
		}

		try{
			CSVMeta meta = new CSVMeta();
			BufferedInputStream rin =new BufferedInputStream(
					new FileInputStream(TestUtil.testDataDir()+"testcsvemptysep.csv")); 
			meta.readConfig(rin);
			rin.close();

			ArrayList<FieldMeta> reqFields = new ArrayList<FieldMeta>();
			reqFields.add(meta.getFieldMeta("Amount"));
			meta.getFieldMeta("Amount").setStorageType(FieldMeta.FIELD_DOUBLE);
			int col = meta.getFieldMeta("Amount").getId();
			StdReader reader = (StdReader) meta.getReader(reqFields);
			rin = new BufferedInputStream(new FileInputStream(TestUtil.testDataDir()+"testcsvemptysep.csv"));
			reader.init(rin);
			int rlineCount = 0;
			double sum = 0;
			while(reader.next()){
				rlineCount++;
				sum += reader.getDouble(col);
			}
			double diff = 18169902.54 - sum;
			assertTrue("Differance: " + diff, diff < 0.01);
		}catch(IOException ie){
			TestUtil.throwNullPointerException(ie);
		}		
	}
}
