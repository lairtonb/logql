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

    $Id: NumberParserTest.java,v 1.2 2009/10/29 05:11:18 mreddy Exp $
*/
package test.logql.util;

import com.logql.util.Marker;
import com.logql.util.NumberParser;

import junit.framework.TestCase;

public class NumberParserTest extends TestCase {

	public void testDouble() {
		String[][] tests = { { "12345.689", "0", "9" },
				{ "76549.248672, something", "0", "12" },
				{ "something, 598473922.8997", "11", "25" },
				{ "457, -3891,more", "5", "10" } };
		Marker mark = new Marker();
		NumberParser parser = new NumberParser();
		for (String[] test : tests) {
			mark.startPos = Integer.parseInt(test[1]);
			mark.endPos = Integer.parseInt(test[2]);
			double req = Double.parseDouble(test[0].substring(mark.startPos,
					mark.endPos));
			double got = parser.readDouble(test[0].getBytes(), mark);
			assertTrue(test[0], req == got);
		}
	}

	public void testFloat() {
		String[][] tests = { { "12345.689", "0", "9" },
				{ "76549.248672, something", "0", "12" },
				{ "something, 598473922.8997", "11", "25" },
				{ "457, -3891,more", "5", "10" } };
		Marker mark = new Marker();
		NumberParser parser = new NumberParser();
		for (String[] test : tests) {
			mark.startPos = Integer.parseInt(test[1]);
			mark.endPos = Integer.parseInt(test[2]);
			float req = Float.parseFloat(test[0].substring(mark.startPos,
					mark.endPos));
			float got = parser.readFloat(test[0].getBytes(), mark);
			assertTrue(test[0], req == got);
		}
	}
}
