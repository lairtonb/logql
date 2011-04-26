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

    $Id: AllTests.java,v 1.2 2009-10-29 05:11:19 mreddy Exp $
*/
package test.logql.query;

import test.logql.meta.CSVReaderTest;
import test.logql.meta.SepReaderTest;
import test.logql.meta.StdReaderTest;
import test.logql.util.ArgumentsTokenizerTest;
import test.logql.util.FlexiRowTester;
import test.logql.util.LineInputStreamTest;
import test.logql.util.NumberParserTest;
import test.logql.util.UtilMethodsTest;
import test.logql.util.WildCardMatchTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for test.logql.query");
		//$JUnit-BEGIN$
		suite.addTestSuite(ArgumentsTokenizerTest.class);
		suite.addTestSuite(LineInputStreamTest.class);
//		suite.addTestSuite(XLReaderTest.class);
		suite.addTestSuite(UtilMethodsTest.class);
		suite.addTestSuite(NumberParserTest.class);
		suite.addTestSuite(WildCardMatchTest.class);
		suite.addTestSuite(FlexiRowTester.class);
		suite.addTestSuite(LookupIPRangeTest.class);

		suite.addTestSuite(StdReaderTest.class);
		suite.addTestSuite(CSVReaderTest.class);
		suite.addTestSuite(SepReaderTest.class);

		suite.addTestSuite(QueryTester.class);
		suite.addTestSuite(QueryDateTester.class);
//		suite.addTestSuite(QueryXLTest.class);
		suite.addTestSuite(StringFunctionTester.class);
		suite.addTestSuite(DomainTest.class);

		//$JUnit-END$
		return suite;
	}

}
