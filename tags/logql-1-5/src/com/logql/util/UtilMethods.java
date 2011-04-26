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

    $Id: UtilMethods.java,v 1.3 2009-11-01 01:39:06 mreddy Exp $
*/
package com.logql.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import com.logql.interpret.func.HostName;
import com.logql.interpret.func.SelectFunction;
import com.logql.interpret.func.StrTok;
import com.logql.interpret.func.ToLower;
import com.logql.interpret.func.ToUpper;
import com.logql.interpret.func.URLDecode;
import com.logql.interpret.func.UrlAttribute;
import com.logql.interpret.wfunc.StringWhereFunction;
import com.logql.meta.DerivedField;
import com.logql.meta.FieldMeta;
import com.logql.meta.LogMeta;

public class UtilMethods {
	public static boolean _AbortLine = true;
	public static boolean _CheckUpdate = true;
	private static Boolean _debugMode;
	public static boolean _ErrorDetails = false;

	public static byte[] hexToBytes(String str) {
		if (str == null) {
			return null;
		} else if (str.length() < 2) {
			return null;
		} else {
			int len = str.length() / 2;
			byte[] buffer = new byte[len];
			for (int i = 0; i < len; i++) {
				buffer[i] = (byte) Integer.parseInt(str.substring(i * 2,
						i * 2 + 2), 16);
			}
			return buffer;
		}

	}

	public static String bytesToHex(byte[] data) {
		if (data == null) {
			return null;
		} else {
			int len = data.length;
			String str = "";
			for (int i = 0; i < len; i++) {
				if ((data[i] & 0xFF) < 16)
					str = str + "0"
							+ java.lang.Integer.toHexString(data[i] & 0xFF);
				else
					str = str + java.lang.Integer.toHexString(data[i] & 0xFF);
			}
			return str.toUpperCase();
		}
	}

	public static boolean isDebugMode(){
		if(_debugMode == null){
			_debugMode = Boolean.parseBoolean(System.getProperty("logql.debug"));
		}
		return _debugMode;
	}

	public static int ltrim(byte[] barr, Marker mark) {
		return ltrim(barr, mark.startPos, mark.endPos);
	}

	public static int ltrim(byte[] barr, int s, int e) {
		int p = s - 1;
		while (++p < e && barr[p] <= ' ');
		return p;
	}

	public static int rtrim(byte[] barr, Marker mark) {
		return rtrim(barr, mark.startPos, mark.endPos);
	}

	public static String intToIp(int i) {
		return ((i >> 24) & 0xFF) + "." + 
				((i >> 16) & 0xFF) + "." +
				((i >> 8) & 0xFF) + "." + 
				(i & 0xFF);
	}

	public static int ipToInt(byte[] arr, int spos, int epos) {
		int finalNum = 0;
		int pow = 3;
		int num = 0;
		for (int i = spos; i < epos; i++) {
			if (arr[i] == '.') {
				finalNum |= num << (pow * 8);
				num = 0;
				pow--;
			} else {
				num = (num * 10) + getDigit(arr[i]);
			}
		}
		finalNum += ((num % 256 * Math.pow(256, pow)));
		return finalNum;
	}

	public static int rtrim(byte[] barr, int s, int e) {
		int p = e;
		while (--p >= s && barr[p] <= ' ');
		return p;
	}

	public static int indexOf(byte[] carr, byte[] sep, Marker mark) {
		int sepPos = -1;
		int max = mark.lineEndPos - sep.length;
		for (int i = mark.startPos; i <= max; i++) {
			if (carr[i] != sep[0]) {
				while (++i <= max && carr[i] != sep[0]);
			}
			if (i <= max) {
				if (sep.length == 1)
					return i;

				int j = i + 1;
				int end = i + sep.length;

				for (int k = 1; j < end && carr[j] == sep[k]; k++, j++);

				if (j == end) {
					sepPos = i;
					break;
				}
			}
		}
		return sepPos;
	}

	public static boolean startsWith(byte[] carr, byte[] req, Marker mark) {
		if (mark.endPos - mark.startPos >= req.length) {
			int p = mark.startPos, k = 0;
			for (; k < req.length && req[k] == carr[p]; k++, p++)
				;
			if (k == req.length)
				return true;
		}
		return false;
	}

	public static boolean endsWith(byte[] carr, byte[] req, Marker mark) {
		if (mark.endPos - mark.startPos >= req.length) {
			int p = mark.endPos - 1, k = req.length - 1, e = mark.endPos	- req.length;
			for (; p >= e && req[k] == carr[p]; k--, p--)
				;
			if (k == -1)
				return true;
		}
		return false;
	}
	
	public static boolean stringEquals(byte[] carr, byte[] req, Marker mark){
		if (req.length == (mark.endPos - mark.startPos)) {
			int c = -1;
			while (++c != req.length) {
				if (carr[c + mark.startPos] != req[c])
					return false;
			}
			return true;
		}
		return false;
	}

	public static List<InputStreamWrapper> getFiles(String fileName) {
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<InputStreamWrapper> ret = new ArrayList<InputStreamWrapper>();

		ArgumentsTokenizer st = new ArgumentsTokenizer(fileName);
		String tmp = null;
		while ((tmp = st.nextToken()) != null) {
			names.add(tmp);
		}

		for (String fName : names) {
			String zipContent = null;
			int aloc = fName.indexOf("@");
			if (aloc > -1) {
				zipContent = fName.substring(0, aloc);
				fName = fName.substring(aloc + 1, fName.length());
			}
			File f = new File(fName);
			String parent = f.getParent();
			String child = parent == null ? fName : fName.substring(parent
					.length());

			List<File> reqFiles = new ArrayList<File>();
			if (child.indexOf("*") > -1) {
				if(parent == null)
					parent = ".";
				if (child.startsWith("/") || child.startsWith("\\"))
					child = child.substring(1);
				final WildCardMatch wc = new WildCardMatch(child);
				File[] fli = new File(parent).listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return wc.matches(name);
					}
				});
				if (fli != null && fli.length > 0)
					reqFiles = Arrays.asList(fli);
			} else {
				reqFiles.add(f);
			}

			if (zipContent != null) {
				WildCardMatch wc = new WildCardMatch(zipContent);
				for (File tf : reqFiles) {
					ret.addAll(getZipEntries(tf, wc));
				}
			} else {
				for (File tf : reqFiles) {
					if (tf.getName().toLowerCase().endsWith(".zip")) {
						ret.addAll(getZipEntries(tf, new WildCardMatch("*")));
					} else {
						InputStreamWrapper isw = new InputStreamWrapper(tf);
						if(!isw.validate()){
							throw new IllegalArgumentException("Problem with file: "+isw.toString());
						}
						ret.add(isw);
					}
				}
			}
		}
		return ret;
	}

	public static ArrayList<InputStreamWrapper> getZipEntries(File zf, WildCardMatch wc){
		ArrayList<InputStreamWrapper> ret = new ArrayList<InputStreamWrapper>();
		try {
			ZipFile zfile = new ZipFile(zf);
			Enumeration<? extends ZipEntry> zenu = zfile.entries();
			while (zenu.hasMoreElements()) {
				ZipEntry ze = zenu.nextElement();
				if (!ze.isDirectory() && wc.matches(ze.getName())) {
					ret.add(new InputStreamWrapper(zfile, ze));
				}
			}
		} catch (ZipException ze) {
			throw new IllegalArgumentException("Invalid zip: " + zf.toString());
		} catch (IOException ie) {
			throw new IllegalArgumentException("Invalid zip: " + zf.toString());
		}
		return ret;
	}

	public static int getDigit(byte b){
		switch(b){
			case '0': return 0;
			case '1': return 1;
			case '2': return 2;
			case '3': return 3;
			case '4': return 4;
			case '5': return 5;
			case '6': return 6;
			case '7': return 7;
			case '8': return 8;
			case '9': return 9;
		}
		return -1;
	}

	public static int parseInt(byte[] arr, Marker mark) {
		return parseInt(arr, mark.startPos, mark.endPos);
	}

	public static int parseInt(byte[] arr, int s, int e){
		s=ltrim(arr, s, e);
		e=rtrim(arr,s,e)+1;

		int result=0;
		boolean negative=false;
		int limit;
		int multimin;
		int digit;

		int l = e -s;
		if(l == 0)
			return 0;
		if (l < 0)
		    throw new NumberFormatException("");

		if(arr[s]=='-'){
			negative=true;
			limit=Integer.MIN_VALUE;
			s++;
		}else{
			limit=-Integer.MAX_VALUE;
		}
		multimin = limit/10;

		if(s<e){
			digit=getDigit(arr[s++]);
			if(digit<0)
				throw new NumberFormatException();
			else
				result= -digit;

			while(s<e){
				digit=getDigit(arr[s++]);
				if(digit <0)
					throw new NumberFormatException ("");
				if(result<multimin)
					throw new NumberFormatException ("");

				result *= 10;

				if(result<limit +digit)
					throw new NumberFormatException(new String(arr,s,e));

				result -= digit;
			}
		}
		if(negative){
			if(s>1)
				return result;
			else
				throw new NumberFormatException("");
		} else {
			return -result;
		}
	}

	public static long parseLong(byte[] barr, Marker mark) {
		return parseLong(barr, mark.startPos, mark.endPos);
	}

	public static long parseLong(byte[] arr, int s, int e){
		s=ltrim(arr, s, e);
		e=rtrim(arr,s,e)+1;

		long result=0;
		boolean negative=false;

		long limit;
		long multimin;
		int digit;

		int l = e -s;
		if(l == 0)
			return 0;
		if (l < 0)
		    throw new NumberFormatException("");

		if(arr[s]=='-'){
			negative=true;
			limit=Long.MIN_VALUE;
			s++;
		}else{
			limit=-Long.MAX_VALUE;
		}
		multimin = limit/10;

		if(s<e){
			digit=getDigit(arr[s++]);
			if(digit<0)
				throw new NumberFormatException();
			else
				result= -digit;

			while(s<e){
				digit=getDigit(arr[s++]);
				if(digit <0)
					throw new NumberFormatException ("");
				if(result<multimin)
					throw new NumberFormatException ("");

				result *= 10;

				if(result<limit +digit)
					throw new NumberFormatException("");

				result -= digit;
			}
		}
		if(negative){
			if(s>1)
				return result;
			else
				throw new NumberFormatException("");
		} else {
			return -result;
		}
	}

	public static String removeQuotes(String val) {
		val = val.trim();
		if (val.startsWith("\"") && val.endsWith("\"")) {
			val = val.substring(1, val.length() - 1);
		} else if (val.startsWith("\'") && val.endsWith("\'")) {
			val = val.substring(1, val.length() - 1);
		}
		return val;
	}

	public static SelectFunction getDerivedFunction(DerivedField df) {
		if (df.getImplementationClass().equals(
				"com.logql.interpret.derived.SearchEngine")) {
			return new com.logql.interpret.derived.SearchEngine();
		} else if (df.getImplementationClass().equals(
				"com.logql.interpret.derived.Country")) {
			return new com.logql.interpret.derived.Country();
		} else if (df.getImplementationClass().equals(
				"com.logql.interpret.derived.KeyPhrase")) {
			return new com.logql.interpret.derived.KeyPhrase();
		} else if (df.getImplementationClass().equals(
				"com.logql.interpret.derived.Domain")) {
			return new com.logql.interpret.derived.Domain();
		}
		return null;
	}

	public static FieldMeta processToDate(LogMeta meta, String args) {
		ArgumentsTokenizer tok = new ArgumentsTokenizer(args);
		if (tok.countTokens() != 2)
			throw new IllegalArgumentException("Invalid usage of function."
					+ " Expecting todate(<fieldName>,\"<formatString>\")");
		String fname = tok.nextToken();
		String fString = tok.nextToken();
	
		FieldMeta fmeta = meta.getFieldMeta(fname);
		if (fmeta == null)
			throw new IllegalArgumentException("Unknown field: " + fname);
		if (fmeta.getStorageType() != FieldMeta.FIELD_STRING
				&& fmeta.getStorageType() != FieldMeta.FIELD_DATE)
			throw new IllegalArgumentException(
					"Cannot use todate function on field: " + fname);
		fmeta.setStorageType(FieldMeta.FIELD_DATE);
		if (!fString.equalsIgnoreCase("utc"))
			fmeta.setDateFormatter(new SimpleDateFormat(fString));

		return fmeta;
	}
	
	public static boolean hasDot(byte[] carr, Marker mark) {
		int k = mark.endPos;
		while (--k >= mark.startPos && carr[k] != '.')
			;
		return k != mark.startPos - 1;
	}

	public static String getErrorString(Map<String, List<Integer>> err, boolean detErr) {
		if (err != null && err.size() > 0) {
			int errCount = 0;
			for (List<Integer> t : err.values()) {
				errCount += t.size();
			}
			StringBuffer sb = new StringBuffer();
			sb.append("Error Lines: ").append(errCount);
			if (detErr) {
				sb.append("{");
				for (String file : err.keySet()) {
					sb.append(file).append("= (");
					List<Integer> lines = err.get(file);
					for (Integer line : lines)
						sb.append(line).append(", ");
					sb.append("),");
				}
				sb.append("}");
			}
			return sb.toString();
		}
		return "";
	}

	public static StringWhereFunction processStringFunction(LogMeta lm, String args) {
		int bloc = args.indexOf("(");
		int eloc = args.indexOf(")", bloc);
		if (bloc == -1 || eloc == -1)
			throw new IllegalArgumentException("Malformed function: " + args);
		String name = args.substring(0, bloc);
		StringWhereFunction ret = null;
		if (name.equalsIgnoreCase("strtok"))
			ret = new StrTok();
		else if (name.equalsIgnoreCase("hostname"))
			ret = new HostName();
		else if (name.equalsIgnoreCase("urldecode"))
			ret = new URLDecode();
		else if (name.equalsIgnoreCase("urlattribute"))
			ret = new UrlAttribute();
		else if (name.equalsIgnoreCase("tolowercase"))
			ret = new ToLower();
		else if (name.equalsIgnoreCase("touppercase"))
			ret = new ToUpper();
		if (ret == null)
			throw new IllegalArgumentException("Unknown function: " + args);
		args = args.substring(bloc + 1, eloc);
		ret.processFunctionArgs(lm, args);
		return ret;
	}
}
