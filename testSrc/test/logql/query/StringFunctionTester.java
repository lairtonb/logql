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

    $Id: StringFunctionTester.java,v 1.2 2009-10-29 05:11:19 mreddy Exp $
*/
package test.logql.query;

import java.sql.Statement;

import org.junit.Before;

import com.logql.inter.QConnection;
import com.logql.interpret.func.StrTok;
import com.logql.interpret.func.ToLower;
import com.logql.interpret.func.ToUpper;
import com.logql.interpret.func.URLDecode;
import com.logql.interpret.func.UrlAttribute;
import com.logql.meta.Config;
import com.logql.meta.LogMeta;
import com.logql.util.Marker;

import junit.framework.TestCase;

public class StringFunctionTester extends TestCase {
	Config conf;
	Statement stmt;

	@Before
	public void setUp() throws Exception {
		conf = Config.load(TestUtil.testDataDir()+"config.xml");
		stmt = QConnection.createStatement();
		stmt.executeQuery("from "+TestUtil.testDataDir()+"access.log use apache-common@"+TestUtil.testDataDir()+"config.xml");
	}

	public void testStrTok(){
		LogMeta meta = conf.getConfig("apache-common");
		String[][] tests = {{"http://www.test.com/something?t=test1&ref=val&t=some","path, &, 2","ref=val"},
				{"http://www.test.com/Apperal/Mens/footware.html","path, /, 5","Mens"},
				{"/Apperal/Mens/footware.html","path,/,2", "Apperal"},
				{"/Apperal/Mens/footware.html","path,/,1",""},
				{"/Apperal/Mens/footware.html","path,/,5",""},
				{"/Apperal/Mens/footware.html","path,/,5, false","footware.html"},
				{"footware.html","path,'.',1","footware"},
				{"footware.html","path,'.',2","html"},
				{"/Apperal/Mens/footware.html","path,/,4","footware.html"},
				{"http://www.test.com/Apperal/Mens/footware.html","path, /, 3","Mens", "19","46"}};

		for(String[] test:tests){
			StrTok st = new StrTok();
			st.processFunctionArgs(meta, test[1]);
			byte[] tdata = test[0].getBytes();
			Marker mark = new Marker();
			if(test.length == 5){
				mark.startPos = Integer.parseInt(test[3]);
				mark.endPos = Integer.parseInt(test[4]);
				mark.lineEndPos = mark.endPos;
			}else{
				mark.startPos = 0;
				mark.endPos = tdata.length;
				mark.lineEndPos = tdata.length;
			}
			st.find(tdata, mark);
			String res = new String(tdata,mark.startPos,mark.endPos-mark.startPos);
			assertTrue(test[0]+"- got - "+res,res.equals(test[2]));
		}
	}

	public void testURLDecode(){
		String[][] tests = {{"http%3A%2F%2Fwww.bluechillies.com%2Fgoogle_adsense_alternate.html","http://www.bluechillies.com/google_adsense_alternate.html"},
				{"This+is+test","This is test"},
				{"http%3A%2F%2Fwww.bluechillies.com%2F","http://www.bluechillies.com/"},
				{"http%3A%2F%2Fwww.bluechillies.com%2","http://www.bluechillies.com%2"},
				{"%3A",":"},
				{"",""},
				{"+"," "},
				{"some+","some "}};
		URLDecode ud = new URLDecode();
		Marker mark = new Marker();
		int count = 0;
		for(String[] test:tests){
			byte[] data = test[0].getBytes();
			mark.endPos = data.length;
			mark.lineEndPos = data.length;
			byte[] res = ud.decode(data, mark);
			String s = new String(res,mark.startPos, mark.endPos - mark.startPos);
			assertTrue("Wrong -"+(count++)+" - "+test[0],s.equals(test[1]));
		}
	}
	
	public void testToLower(){
		String[][] tests = {{"abcTEST+tRaPall","test+trap","3","12"},
				{"Te%2FsT","te%2fst","0","7"},
				{"Test","","3","3"},
				{"TrAP","tr","0","2"},
				{"","","0","0"}};
		int count = 0;
		ToLower tl = new ToLower();
		Marker mark = new Marker();
		for(String[]test:tests){
			byte[] data = test[0].getBytes();
			mark.startPos = Integer.parseInt(test[2]);
			mark.endPos = Integer.parseInt(test[3]);
			mark.lineEndPos = mark.endPos;
			tl.convert(data, mark);
			String res = new String(data, mark.startPos, mark.endPos - mark.startPos);
			assertTrue("test: "+(count++)+" - "+res, res.equals(test[1]));
		}
	}

	public void testToUpper(){
		String[][] tests = {{"ABCtest+tRaPall","TEST+TRAP","3","12"},
				{"Te%2FsT","TE%2FST","0","7"},
				{"Test","","3","3"},
				{"tRap","TR","0","2"},
				{"","","0","0"}};
		int count = 0;
		ToUpper tl = new ToUpper();
		Marker mark = new Marker();
		for(String[]test:tests){
			byte[] data = test[0].getBytes();
			mark.startPos = Integer.parseInt(test[2]);
			mark.endPos = Integer.parseInt(test[3]);
			mark.lineEndPos = mark.endPos;
			tl.convert(data, mark);
			String res = new String(data, mark.startPos, mark.endPos - mark.startPos);
			assertTrue("test: "+(count++)+" - "+res, res.equals(test[1]));
		}
	}

	public void testStrTokQuery() {
		String[][] tests = {{"select strtok(path,/,2), count(*)","51","2775"},
				{"select path, count(*) where strtok(path,/,2) = 'images'","27","591"},
				{"select strtok(path,/,3), count(*)","33","2775"},
				{"select strtok(path,/,3,false), count(*)","75","2775"},
				{"select strtok(strtok(path,/,5,false),'.',2,false), count(*) where path like '%.html'","1","315"},
				{"select strtok(strtok(path,/,5,false),'.',2), count(*) where strtok(strtok(path,/,5,false),'.',2) = 'html'","1","315"},
				{"select strtok(referer,&,8), count(*) where referer like '%googlesyndication.com%'","20","29"}};
		TestUtil.stdTestQuery(tests, stmt);
	}

//	public void testHostQuery() {
//		String[][] queries = {{"select hostname(host), count(*)","24","110"},
//				{"select hostname(host), count(*)  where hostname(host) like '%.com'","6","26"},
//				{"select strtok(hostname(host),'.',7,false), count(*)","12","110"},
//				{"select strtok(hostname(host),'.',7,false), count(*) where strtok(hostname(host),'.',7,false) in ('com','uk','net','edu')","4","90"}};
//		Statement stmt = QConnection.createStatement();
//		try {
//			stmt.executeQuery("from ./testData/access_small.log use apache-common@./testData/config.xml");
//
//			for(String[] query:queries){
//				try{
//					ResultSet rs = stmt.executeQuery(query[0]);
//					int rowCount = 0;
//					int resultCount = 0;
//					while(rs.next()){
//						resultCount++;
//						rowCount += rs.getInt(2);
//					}
//					int reqLines = Integer.parseInt(query[1]);
//					int reqTotal = Integer.parseInt(query[2]);
//					if(resultCount != reqLines){
//						System.err.println(query[0]+" - lines - "+resultCount);
//					}
//					if(rowCount != reqTotal){
//						System.err.println(query[0]+" - got - "+rowCount);
//					}
//				}catch(SQLException se){
//					TestUtil.throwNullPointerException(se);	
//				}
//			}
//		} catch (SQLException se) {
//			TestUtil.throwNullPointerException(se);
//		}
//	}

	public void testURLDecodeQuery(){
		String[][] queries = {{"select urldecode(referer), count(*) where referer notlike 'http://www.diskviz%'","59","439"},
				{"select urldecode(strtok(referer,&,8)), count(*) where referer like '%googlesyndication.com%' and strtok(referer,'&',8) like 'url=%' or strtok(referer,'&',8) like 'ref=%'","9","9"},
				{"select referer, count(*) where urldecode(referer) like '%http://www.bluechillies.com/%'","1","1"},
				{"select referer, count(*) where urldecode(strtok(referer,&,8)) like '%http://www.bluechillies.com/%'","1","1"},
				{"select referer, count(*) where strtok(urldecode(referer),&,8) like '%http://www.bluechillies.com/%'","1","1"},
				{"select referer, count(*) where strtok(urldecode(referer),&,8) like '%http://www.bluechillies.com/%'","1","1"}};
		TestUtil.stdTestQuery(queries, stmt);
	}

	public void testToLowerQuery(){
		String[][] queries = {{"select strtok(path,'.',10,false), count(*) where tolowercase(strtok(path,'.',10,false)) = jpg","1","1206"},
				{"select strtok(path,'.',10,false), count(*) where strtok(tolowercase(path),'.',10,false) = jpg","1","1206"},
				{"select tolowercase(strtok(path,'.',10,false)), count(*)","46","2775"}};
		TestUtil.stdTestQuery(queries, stmt);
	}

	public void testToUpperQuery(){
		String[][] queries = {{"select strtok(path,'.',10,false), count(*) where touppercase(strtok(path,'.',10,false)) = JPG","1","1206"},
				{"select strtok(path,'.',10,false), count(*) where strtok(touppercase(path),'.',10,false) = JPG","1","1206"},
				{"select touppercase(strtok(path,'.',10,false)), count(*)","46","2775"}};
		TestUtil.stdTestQuery(queries, stmt);
	}

	public void testURLAttribute(){
		//input, key, startpos, endpos, target, 
		String[][] tests = {{"/test?name1=n2","name1","0","14","n2"},
				{"/test?name1=","name1","0","12",""},
				{"/test?name1=n1&name2=n2&name3=n3","name1","0","32","n1"},
				{"/test?name1=n1&name2=n2&name3=n3","name2","0","32","n2"},
				{"/test?name1=n1&name2=&name3=n3","name2","0","30",""},
				{"/test?name1=n1&name2=n2&name3=n3","name3","0","32","n3"},
				{"/test?name1=n1&name2=n2&name3=","name3","0","30",""},
				{"/test?name1=n1&name2=n2&name3=n3","name3","0","23",""},
				{"?name1=n1?name2=n2&name3=n3&name1=n1","name1","9","27",""},
				{"http://www.google.com/search?q=bonneville+salt+flats&hl=en&lr=&ie=UTF-8&edition=&start=10&sa=N","q","0","94","bonneville salt flats", "bonneville+salt+flats"},
				{"http://www.google.com/search?q=+eagle+(photo)&hl=en&lr=&ie=UTF-8&oe=UTF-8&start=10&sa=N","q","0","87"," eagle (photo)","+eagle+(photo)"},
				{"http://search.yahoo.com/search?p=AMERICAN+EAGLE&ei=UTF-8&vm=i&n=10&fl=0&x=wrt","p","0","77","AMERICAN EAGLE","AMERICAN+EAGLE"},
				{"http://search.msn.com/results.aspx?ps=ba%3d(1.60)0(.)0.......%26co%3d(0.15)4(0.1)3.200.2.5.10.1.3.%26rd%3d1%26pn%3d4%26&q=reno+air+races&ck_sc=1&ck_af=0","ps","0","152","ba=(1.60)0(.)0.......&co=(0.15)4(0.1)3.200.2.5.10.1.3.&rd=1&pn=4&","ba%3d(1.60)0(.)0.......%26co%3d(0.15)4(0.1)3.200.2.5.10.1.3.%26rd%3d1%26pn%3d4%26"},
				{"http://pagead2.googlesyndication.com/pagead/ads?client=ca-pub-1371252926667868&dt=1146773184781&lmt=1146773183&format=fp_al_lp&output=html&channel=8797325563&url=http%3A%2F%2Fwww.soft3k.com%2FWeb-Link-Validator-p9525.htm&ref=http%3A%2F%2Fwww.google.com%2Fsearch%3Fq%3Dfree%2Bbroken%2Blink%2Bvalidators%26hl%3Den%26lr%3D%26client%3Dfirefox-a%26rls%3Dorg.mozilla%3Aen-US%3Aofficial_s%26start%3D20%26sa%3DN&cc=100&u_h=600&u_w=800&u_ah=566&u_aw=800&u_cd=32&u_tz=-300&u_his=8&u_java=true&u_nplug=15&u_nmime=52&kw_type=radlink&prev_fmts=160x90_0ads_al&rt=ChBEWl69AA2AmAokPhRaCk9nEhFGcmVlIExpbmsgQ2hlY2tlchoI-sxXcPCn930gptmCAigB&hl=en&kw0=HTML+Validator&kw1=Free+Link+Checker&kw2=HTML+Checker&kw3=Web+Site+Checker&okw=Free+Link+Checker",
					"url","0","728",
					"http://www.soft3k.com/Web-Link-Validator-p9525.htm","http%3A%2F%2Fwww.soft3k.com%2FWeb-Link-Validator-p9525.htm"},
				{"http://pagead2.googlesyndication.com/pagead/ads?client=ca-pub-1371252926667868&dt=1146773184781&lmt=1146773183&format=fp_al_lp&output=html&channel=8797325563&url=http%3A%2F%2Fwww.soft3k.com%2FWeb-Link-Validator-p9525.htm&ref=http%3A%2F%2Fwww.google.com%2Fsearch%3Fq%3Dfree%2Bbroken%2Blink%2Bvalidators%26hl%3Den%26lr%3D%26client%3Dfirefox-a%26rls%3Dorg.mozilla%3Aen-US%3Aofficial_s%26start%3D20%26sa%3DN&cc=100&u_h=600&u_w=800&u_ah=566&u_aw=800&u_cd=32&u_tz=-300&u_his=8&u_java=true&u_nplug=15&u_nmime=52&kw_type=radlink&prev_fmts=160x90_0ads_al&rt=ChBEWl69AA2AmAokPhRaCk9nEhFGcmVlIExpbmsgQ2hlY2tlchoI-sxXcPCn930gptmCAigB&hl=en&kw0=HTML+Validator&kw1=Free+Link+Checker&kw2=HTML+Checker&kw3=Web+Site+Checker&okw=Free+Link+Checker",
					"ref","0","728",
					"http://www.google.com/search?q=free+broken+link+validators&hl=en&lr=&client=firefox-a&rls=org.mozilla:en-US:official_s&start=20&sa=N",
					"http%3A%2F%2Fwww.google.com%2Fsearch%3Fq%3Dfree%2Bbroken%2Blink%2Bvalidators%26hl%3Den%26lr%3D%26client%3Dfirefox-a%26rls%3Dorg.mozilla%3Aen-US%3Aofficial_s%26start%3D20%26sa%3DN"}};

		UrlAttribute ud = new UrlAttribute();
		Marker mark = new Marker();
		int count = 0;
		for(String[] test:tests){
			byte[] data = test[0].getBytes();
			ud.setAttribute(test[1]);
			ud.setDecode(true);
			mark.startPos = Integer.parseInt(test[2]);
			mark.endPos = Integer.parseInt(test[3]);
			mark.lineEndPos = mark.endPos;
			byte[] res = ud.find(data, mark);
			String s = new String(res,mark.startPos, mark.endPos - mark.startPos);
			assertTrue("Wrong -"+(count)+" - "+test[0],s.equals(test[4]));
			if(test.length == 6){
				ud.setDecode(false);
				mark.startPos = Integer.parseInt(test[2]);
				mark.endPos = Integer.parseInt(test[3]);
				mark.lineEndPos = mark.endPos;
				res = ud.find(data, mark);
				s = new String(res,mark.startPos, mark.endPos - mark.startPos);
				assertTrue("Wrong -"+(count++)+" - "+test[0],s.equals(test[5]));
			}
			count++;
		}
	}
	
	public void testURLAttributeQueries(){
		String[][] tests = {{"select urlattribute(referer,'q'), count(*) where referer like '%.google.com/%'","2","2"},
				{"select path, count(*) where referer like '%.google.com/%' and urlattribute(referer,'q') = 'broken links'","1","1"},
				{"select urlattribute(referer,'url'), count(*) where referer like '%googlesyndication.com%'","28","29"},
				{"select urlattribute(urlattribute(referer,'url'),'url'), count(*) where referer like '%googlesyndication.com%'  and urlattribute(referer,'url') like '%www.1-hit.com/%'","1","1"},
				{"select urlattribute(referer,'q'), count(*) where urlattribute(referer,'q') # ''","4","4"}};
		TestUtil.stdTestQuery(tests, stmt);		
	}
}
