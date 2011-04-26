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

    $Id: HostName.java,v 1.2 2009/10/29 05:11:14 mreddy Exp $
*/
package com.logql.interpret.func;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import com.logql.interpret.wfunc.StringWhereFunction;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.meta.LogMeta;
import com.logql.util.ByteStringWrapper;
import com.logql.util.Marker;
import com.logql.util.UtilMethods;

public class HostName extends SelectFunction implements StringWhereFunction {
	Marker mark = new Marker();
	int count = -1;
	String seperator;
	byte[] sep = ".".getBytes();
	byte[] ip = new byte[4];
	boolean strict = true;
	public static HashMap<ByteStringWrapper, ByteStringWrapper> cache 
		= new HashMap<ByteStringWrapper, ByteStringWrapper>();
	ByteStringWrapper tmpKey = new ByteStringWrapper();
	StringWhereFunction child;

	public void init(int[] srcMap, int[] dstMap) {
		super.init(srcMap, dstMap);
		if (child != null) {
			child.init(srcMap);
		}
	}

	public void init(int[] srcMap) {
		super.init(srcMap);
		if (child != null)
			child.init(srcMap);
	}

	@Override
	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		if (child != null) {
			dst.charArr[dstColPos] = child.getString(src, mark);
		} else {
			dst.charArr[dstColPos] = src.charArr[srcColPos];
			mark.startPos = 0;
			mark.endPos = src.charSiz[srcColPos];
			mark.lineEndPos = src.charSiz[srcColPos];
		}
		dst.charArr[dstColPos] = resolveIP(dst.charArr[dstColPos], mark);
		dst.charOffset[dstColPos] = mark.startPos;
		dst.charSiz[dstColPos] = mark.endPos - mark.startPos;
		return true;
	}
	
	public byte[] resolveIP(byte[] carr, Marker m) {
		Marker partMarker = new Marker();
		partMarker.startPos = m.startPos;
		partMarker.lineStartPos = m.lineStartPos;
		partMarker.endPos = m.endPos;
		partMarker.lineEndPos = m.lineEndPos;

		tmpKey.setValue(carr, m.startPos, m.endPos);
		tmpKey.computeHash();
		if(cache.containsKey(tmpKey)){
			byte[] ret = cache.get(tmpKey).getValue();
			m.lineStartPos = 0;
			m.startPos = 0;
			m.endPos = ret.length;
			m.lineEndPos = ret.length;
			return ret;
		}

		int i = 0;
		for(i=0;i<3;i++){
			int pos = UtilMethods.indexOf(carr, sep, partMarker);
			if(pos  == -1)
				break;
			partMarker.endPos = pos;
			try {
				ip[i] = (byte) UtilMethods.parseInt(carr, partMarker);
			} catch (NumberFormatException nfe) {
				break;
			}
			partMarker.startPos = pos + 1;
			partMarker.endPos = m.endPos;
		}
		if(i == 3){
			try{
				ip[i] = (byte) UtilMethods.parseInt(carr, partMarker);
				InetAddress iadd = InetAddress.getByAddress(ip);
				byte[] ret = iadd.getHostName().getBytes();
				m.lineStartPos = 0;
				m.startPos = 0;
				m.endPos = ret.length;
				m.lineEndPos = ret.length;

				ByteStringWrapper nkey = new ByteStringWrapper();
				nkey.copy(tmpKey);
				ByteStringWrapper nval = new ByteStringWrapper();
				tmpKey.setValue(ret, 0, ret.length);
				tmpKey.computeHash();
				nval.copy(tmpKey);
				cache.put(nkey, nval);
				
				return ret;
			}catch(NumberFormatException nfe){

			}catch(UnknownHostException uhe){
				
			}
		}
		return carr;
	}

	@Override
	public void copyToDst(FlexiRow src, FlexiRow dst) {
		dst.stringArr[dstColPos] = new String(src.charArr[dstColPos],
				src.charOffset[dstColPos], src.charSiz[dstColPos]);
	}

	public byte[] getString(FlexiRow row, Marker m) {
		byte[] src = null;
		if (child != null) {
			src = child.getString(row, m);
		} else {
			src = row.charArr[srcColPos];
			m.startPos = 0;
			m.endPos = row.charSiz[srcColPos];
			m.lineEndPos = m.endPos;
		}

		return resolveIP(src, m);
	}

	@Override
	public int getStorageType() {
		return FieldMeta.FIELD_STRING;
	}

	@Override
	public boolean isMetricField() {
		return false;
	}
	
	public int getSrcColumnId() {
		if (child == null)
			return field.getId();
		else
			return ((SelectFunction) child).getSrcColumnId();
	}

	public FieldMeta getRequiredField() {
		if (child == null)
			return field;
		else
			return ((SelectFunction) child).getRequiredField();
	}

	@Override
	public boolean requiresPostProcess() {
		return false;
	}

	public void processFunctionArgs(LogMeta lm, String args) {
		args = args.trim();
		field = lm.getFieldMeta(args);
		if (field == null) {
			if (args.indexOf('(') > -1) {
				child = UtilMethods.processStringFunction(lm, args);
			} else {
				throw new IllegalArgumentException("Unknown field: " + args);
			}
		}
	}
}
