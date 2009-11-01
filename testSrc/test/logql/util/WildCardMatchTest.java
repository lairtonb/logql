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

    $Id: WildCardMatchTest.java,v 1.2 2009/10/29 05:11:18 mreddy Exp $
*/
package test.logql.util;

import com.logql.util.WildCardMatch;

import junit.framework.TestCase;

public class WildCardMatchTest extends TestCase {

	public void testMatch(){
		String[][] tests = {{"themes", "themes","true" },
				{"the","themes","false"},
				{"themes","the","false"},
				{"the*","themes","true"},
				{"the*","Ethemes","false"},
				{"*mes","themes","true"},
				{"*mes","themesri","false"},
				{"*themes*","themes","true"},
				{"the*s","themes","true"},
				{"ER*.csv","ER1.csv", "true"},
				{"*","them","true"},
				{"the w*ld is f*t","the world is flat","true"},
				{"* world * fl*t","the world is flat","true"},
				{"* world * flat","the earth is round","false"}};
		
		for(String[] test: tests){
			WildCardMatch mat = new WildCardMatch(test[0]);
			boolean got = mat.matches(test[1]);
			boolean req = test[2].equals("true");
			assertTrue(test[0], got == req);
		}
	}
}
