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

    $Id: DomainTest.java,v 1.2 2009-10-29 05:11:19 mreddy Exp $
*/
package test.logql.query;

import com.logql.interpret.derived.Domain;
import com.logql.util.Marker;

import junit.framework.TestCase;

public class DomainTest extends TestCase {

	public void testDomainName(){
		String[][] testData = {{"0123http://www.google.com/?q= dummydata","4","29","www.google.com"},
				{"work http://www.google.com test","5","26","www.google.com"},
				{"testftp://www.google.com test","4","24", ""},
				{"","0","0",""}};
		Marker mark = new Marker();
		Domain df = new Domain();
		for(String[] test:testData){
			byte[] carr = test[0].getBytes();
			mark.startPos = Integer.parseInt(test[1]);
			mark.endPos = Integer.parseInt(test[2]);
			df.find(carr, mark);
			assertTrue(test[0], TestUtil.getString(carr, mark).equals(test[3]));
		}
	}
}
