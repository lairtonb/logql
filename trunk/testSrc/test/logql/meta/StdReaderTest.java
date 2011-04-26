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

    $Id: StdReaderTest.java,v 1.2 2009-10-29 05:11:07 mreddy Exp $
*/
package test.logql.meta;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import test.logql.query.TestUtil;

import com.logql.meta.Config;
import com.logql.meta.FieldMeta;
import com.logql.meta.LogMeta;
import com.logql.meta.Reader;
import com.logql.meta.std.StdReader;

import junit.framework.TestCase;

public class StdReaderTest extends TestCase {
	Config conf ;

	public void setUp() throws Exception {
		conf = Config.load(TestUtil.testDataDir()+"config.xml");
	}

	public void testReadError(){
		LogMeta meta = conf.getConfig("apache-common");
		File f = new File(TestUtil.testDataDir()+"access_err.log");

		try {
			ArrayList<FieldMeta> reqFields = new ArrayList<FieldMeta>();
			reqFields.add(meta.getFieldMeta("host"));
			reqFields.add(meta.getFieldMeta("date"));
			reqFields.add(meta.getFieldMeta("respCode"));
			reqFields.add(meta.getFieldMeta("bytes"));
			reqFields.add(meta.getFieldMeta("path"));
			reqFields.add(meta.getFieldMeta("userAgent"));
			StdReader reader = (StdReader) meta.getReader(reqFields);
			reader.init(new FileInputStream(f));

			int rlineCount = 0;
			while(reader.next()){
				rlineCount++;
			}

			assertTrue("Line counts do not match", rlineCount == 41);
			assertTrue("Error count is wrong",reader.getErrors().size() == 7);
		} catch (IOException ie) {
			NullPointerException ne = new NullPointerException();
			ne.initCause(ie);
			throw ne;
		}
	}

	public void testReadLargeFile(){
		LogMeta meta = conf.getConfig("apache-common");
		File f = new File(TestUtil.testDataDir()+"access.log");
		try {
			ArrayList<FieldMeta> reqFields = new ArrayList<FieldMeta>();
			reqFields.add(meta.getFieldMeta("host"));
			reqFields.add(meta.getFieldMeta("date"));
			reqFields.add(meta.getFieldMeta("respCode"));
			reqFields.add(meta.getFieldMeta("path"));
			reqFields.add(meta.getFieldMeta("bytes"));
			int btCol = meta.getFieldMeta("bytes").getId();
			Reader reader = meta.getReader(reqFields);
			reader.init(new FileInputStream(f));

			long bytes = 0;
			int rlineCount = 0;
			while(reader.next()){
				rlineCount++;
				bytes += reader.getLong(btCol);
			}
			assertTrue("Line counts do not match", rlineCount == 2775);
			assertTrue("Byte sum is off: "+bytes, bytes == 237013415);
		} catch (IOException ie) {
			NullPointerException ne = new NullPointerException();
			ne.initCause(ne);
			throw ne;
		}
	}

	public void testReadMulti(){
		LogMeta meta = conf.getConfig("apache-common");
		File f = new File(TestUtil.testDataDir()+"access.log");
		try {
			ArrayList<FieldMeta> reqFields = new ArrayList<FieldMeta>();
			reqFields.add(meta.getFieldMeta("host"));
			reqFields.add(meta.getFieldMeta("date"));
			reqFields.add(meta.getFieldMeta("respCode"));
			reqFields.add(meta.getFieldMeta("path"));
			reqFields.add(meta.getFieldMeta("bytes"));
			int btCol = meta.getFieldMeta("bytes").getId();
			Reader reader = meta.getReader(reqFields);
			reader.init(new FileInputStream(f));

			long bytes = 0;
			int rlineCount = 0;
			while(reader.next()){
				rlineCount++;
				bytes += reader.getLong(btCol);
			}

			reader.init(new FileInputStream(TestUtil.testDataDir()+"access_small.log"));
			while(reader.next()){
				rlineCount++;
				bytes += reader.getLong(btCol);
			}

			assertTrue("Line counts do not match", rlineCount == 2885);
			assertTrue("Byte sum is off: "+bytes, bytes == 239820022);
		} catch (IOException ie) {
			NullPointerException ne = new NullPointerException();
			ne.initCause(ne);
			throw ne;
		}		
	}
}
