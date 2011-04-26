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

    $Id: FlexiRowTester.java,v 1.4 2009-10-29 05:11:18 mreddy Exp $
*/
package test.logql.util;

import com.logql.meta.FlexiRow;

import junit.framework.TestCase;

public class FlexiRowTester extends TestCase {

	public void testStringEquals(){
		String[][] tests ={{"1234testing123","test","4","4","true","3556498"},
				{"2234testing123","test","4","2","false","3697"},
				{"3234testing123","test","4","7","false","-1422446064"},
				{"","test","0","0","false","0"},
				{"test","","0","4","false","3556498"},
				{"","","0","0","true","0"},
				{"test","test","0","4","true","3556498"},
				//Multi lingual
				//spanish string value for "test"
				{"1234análisis123","análisis","4","8","true","-394486420"},
				{"1234análisis123","analisis","4","8","false","-394486420"},
				{"1234analisis123","análisis","4","8","false","-1024922388"},
				{"1234modèle123","modèle","4","6","true","-1068919521"},
				{"1234modèle123","modele","4","6","false","-1068919521"},
				{"1234modele123","modèle","4","6","false","-1068799396"},
				{"è, ê, á, à, û, ô","è, ê, á, à, û, ô","0","16","true","-1968045242"}};
		int ssiz = 1;
		int isiz = 3;
		int dtsiz = 1;
		int lsiz = 1;
		int fsiz = 2;
		int dsiz = 1;
		int osiz = 1;
		int[] map = new int[0];
		FlexiRow tempRow = new FlexiRow(ssiz, ssiz, isiz, dtsiz, lsiz, fsiz, dsiz, osiz, map);
		tempRow.isTempRow = true;
		FlexiRow tarRow = new FlexiRow(ssiz, 0, isiz, dtsiz, lsiz, fsiz, dsiz, osiz, map);

		int count = 0;
		for(String[] test:tests){
			byte[] src = test[0].getBytes();
			System.arraycopy(src, 0, tempRow.charArr[0], 0, src.length);
			tempRow.charOffset[0]=Integer.parseInt(test[2]);
			tempRow.charSiz[0]=Integer.parseInt(test[3]);

			tarRow.stringArr[0]=test[1];
			tarRow.charSiz[0]=test[1].getBytes().length;
			boolean got = tempRow.equals(tarRow);
			boolean exp = test[4].equals("true");
			int expHash = Integer.parseInt(test[5]);

			assertTrue("hash "+count, expHash == tempRow.computeHashCode());
			assertTrue("test: "+count++, got == exp);
		}
	}

	public void testEqualHash () {
		String[][] tests = {{"1234testing123", "", "", "test","4","4","0","0","false","3556498"},
				{"1234testing123", "", "test", "","4","4","0","0","true","3556498"}};
		int ssiz = 2;
		int isiz = 3;
		int dtsiz = 1;
		int lsiz = 1;
		int fsiz = 2;
		int dsiz = 1;
		int osiz = 1;
		int[] map = new int[0];
		FlexiRow tempRow = new FlexiRow(ssiz, ssiz, isiz, dtsiz, lsiz, fsiz, dsiz, osiz, map);
		tempRow.isTempRow = true;
		FlexiRow tarRow = new FlexiRow(ssiz, 0, isiz, dtsiz, lsiz, fsiz, dsiz, osiz, map);

		int count = 0;
		for(String[] test:tests){
			byte[] src = test[0].getBytes();
			System.arraycopy(src, 0, tempRow.charArr[0], 0, src.length);
			tempRow.charOffset[0]=Integer.parseInt(test[4]);
			tempRow.charSiz[0]=Integer.parseInt(test[5]);

			src = test[1].getBytes();
			System.arraycopy(src, 0, tempRow.charArr[1], 0, src.length);
			tempRow.charOffset[1]=Integer.parseInt(test[6]);
			tempRow.charSiz[1]=Integer.parseInt(test[7]);

			tarRow.stringArr[0]=test[2];
			tarRow.charSiz[0]=test[2].getBytes().length;
			tarRow.stringArr[1]=test[3];
			tarRow.charSiz[1]=test[3].getBytes().length;
			boolean got = tempRow.equals(tarRow);
			boolean exp = test[8].equals("true");
			int expHash = Integer.parseInt(test[9]);

			assertTrue("hash "+count, expHash == tempRow.computeHashCode());
			assertTrue("test: "+count++, got == exp);
			assertFalse("Target row should never be equal to temp row", tarRow.equals(tempRow));
		}
	}

	public void testDateEquals(){
		String[][] tests = {{"15-Mar-2006","15-Mar-2006","true","840115580"},
				{"","","true","0"},
				{"","some","false","0"},
				{"15-Mar-2006","","false","840115580"},
				{"2006","2006","true","1537220"}};
		int ssiz = 1;
		int isiz = 3;
		int dtsiz = 1;
		int lsiz = 1;
		int fsiz = 2;
		int dsiz = 1;
		int osiz = 1;
		int[] map = new int[0];
		FlexiRow tempRow = new FlexiRow(ssiz, ssiz, isiz, dtsiz, lsiz, fsiz, dsiz, osiz, map);
		tempRow.isTempRow = true;
		FlexiRow tarRow = new FlexiRow(ssiz, 0, isiz, dtsiz, lsiz, fsiz, dsiz, osiz, map);

		int count = 0;
		for(String[] test:tests){
			tempRow.stringArr[0]=test[0];

			tarRow.stringArr[0]=test[1];
			boolean got = tempRow.equals(tarRow);
			boolean exp = test[2].equals("true");
			int expHash = Integer.parseInt(test[3]);

			assertTrue("hash "+count, expHash == tempRow.computeHashCode());
			assertTrue("test: "+count++, got == exp);
		}
	}

	public void testInt(){
		String[][] tests = {{"156","156","true","156"},
				{"56","156","false","56"},
				{"0","324","false","0"},
				{"234","0","false","234"},
				{"0","0","true","0"},
				{"-156","-156","true","-156"},
				{"156","-156","false","156"}};
		int ssiz = 0;
		int isiz = 2;
		int dtsiz = 1;
		int lsiz = 1;
		int fsiz = 2;
		int dsiz = 1;
		int osiz = 1;
		int[] map = new int[0];
		FlexiRow tempRow = new FlexiRow(ssiz, ssiz, isiz, dtsiz, lsiz, fsiz, dsiz, osiz, map);
		tempRow.isTempRow = true;
		FlexiRow tarRow = new FlexiRow(ssiz, 0, isiz, dtsiz, lsiz, fsiz, dsiz, osiz, map);
		tempRow.intGroupBy[0] = true;
		tarRow.intGroupBy[0] = true;
		tempRow.intArr[1] = 500;
		tarRow.intArr[1] = 456;

		int count = 0;
		for (String[] test : tests) {
			tempRow.intArr[0] = Integer.parseInt(test[0]);

			tarRow.intArr[0] = Integer.parseInt(test[1]);
			boolean got = tempRow.equals(tarRow);
			boolean exp = test[2].equals("true");
			int expHash = Integer.parseInt(test[3]);

			assertTrue("hash "+count, expHash == tempRow.computeHashCode());
			assertTrue("test: " + count++, got == exp);
		}
	}

	public void testCombi(){
		String[][] tests = {
				{"2234testing123","test","4","2","15-Mar-2006","15-Mar-2006", "156","156","false","-2105378927"}, //col one off
				{"1234testing123","test","4","4","15-Mar-2006","15-Mar-2008","156","156","false","1140556850"}, //col 2 off
				{"1234testing123","test","4","4","15-Mar-2006","15-Mar-2006","156","-156","false","1140556850"}, //col 3 off
				{"1234testing123","test","4","4","15-Mar-2006","15-Mar-2006","156","156","true","1140556850"},
				{"1234testing123","test","4","0","15-Mar-2006","15-Mar-2006","156","156","false","273779360"},
				{"1234testing123","","4","0","15-Mar-2006","15-Mar-2006","156","156","true","273779360"},
				{"1234testing123","test","4","4","","","156","156","true","110251594"}};
		int ssiz = 2;
		int isiz = 2;
		int dtsiz = 1;
		int lsiz = 1;
		int fsiz = 2;
		int dsiz = 1;
		int osiz = 1;
		int[] map = new int[0];
		FlexiRow tempRow = new FlexiRow(ssiz, ssiz, isiz, dtsiz, lsiz, fsiz, dsiz, osiz, map);
		tempRow.isTempRow = true;
		FlexiRow tarRow = new FlexiRow(ssiz, 0, isiz, dtsiz, lsiz, fsiz, dsiz, osiz, map);
		tempRow.intGroupBy[0] = true;
		tarRow.intGroupBy[0] = true;
		tempRow.intArr[1] = 500;
		tarRow.intArr[1] = 456;

		int count = 0;
		for (String[] test : tests) {
			//////column 1
			byte[] src = test[0].getBytes();
			System.arraycopy(src, 0, tempRow.charArr[0], 0, src.length);
			tempRow.charOffset[0]=Integer.parseInt(test[2]);
			tempRow.charSiz[0]=Integer.parseInt(test[3]);

			tarRow.stringArr[0]=test[1];
			tarRow.charSiz[0]=test[1].getBytes().length;

			/////column 2
			tempRow.stringArr[1]=test[4];
			tarRow.stringArr[1]=test[5];

			/////column 3
			tempRow.intArr[0] = Integer.parseInt(test[6]);
			tarRow.intArr[0] = Integer.parseInt(test[7]);

			/////execute
			boolean got = tempRow.equals(tarRow);
			boolean exp = test[8].equals("true");
			int expHash = Integer.parseInt(test[9]);

			assertTrue("hash "+count, expHash == tempRow.computeHashCode());
			assertTrue("test: " + count++, got == exp);
		}		
	}
}
