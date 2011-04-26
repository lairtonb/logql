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

    $Id: ArgumentsTokenizerTest.java,v 1.2 2009-10-29 05:11:19 mreddy Exp $
*/
package test.logql.util;

import com.logql.util.ArgumentsTokenizer;

import junit.framework.TestCase;

public class ArgumentsTokenizerTest extends TestCase{

	public void testTokenizer(){
		String[][] tests = {{"test,text","2"},
				{"test","1"},
				{"\"test,test1\",test2","2"},
				{"test,\'te,st2\'","2"},
				{"'te,st1',\"tes,t2\"","2"},
				{"test1,\"t'e'st2\",'test3',test4","4"},
				{"strtok(path, /,5,false),'.',2","3"}};
		for(String[] test: tests){
			int req = Integer.parseInt(test[1]);
			ArgumentsTokenizer at = new ArgumentsTokenizer(test[0]);
			assertTrue(test[0], at.countTokens() == req);
		}
	}
}
