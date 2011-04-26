/*
    Copyright 2010 Manmohan Reddy

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

    $Id: Row.java,v 1.2 2009-10-29 05:11:15 mreddy Exp $
*/
package com.logql.meta.binary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class JoinFieldsPool {
	static class PoolEntry{
		Map<String, Integer> keyIdMap;
		byte[][] keys;
		int count;
	}
	private ConcurrentHashMap<File, PoolEntry> keyIdPool;
	private WeakHashMap<File, PoolEntry> valuesPool;
	private static JoinFieldsPool jpool = new JoinFieldsPool();

	private JoinFieldsPool (){
		keyIdPool = new ConcurrentHashMap<File, PoolEntry>();
		valuesPool = new WeakHashMap<File, PoolEntry>();
	}
	
	public static synchronized Map<String, Integer> getKeyIdMap(File file) throws IOException{
		PoolEntry ent = null;
		if(jpool.keyIdPool.containsKey(file)){ 
			ent = jpool.keyIdPool.get(file);
		} else {
			ent = new PoolEntry();
			ent.keyIdMap = jpool.getJoinMap(file);
			jpool.keyIdPool.put(file, ent);
		}
		ent.count++;
		return ent.keyIdMap;
	}

	public static synchronized void releasekeyIdMap(File file) {
		PoolEntry ent = jpool.keyIdPool.get(file);
		if(ent != null){
			ent.count--;
			if(ent.count<=0){
				jpool.keyIdPool.remove(file);
			}
		}
	}

	public static synchronized void saveJoinMap (File file) throws IOException {
		PoolEntry ent = jpool.keyIdPool.get(file);
		if(ent == null)
			throw new IllegalArgumentException("No pool entry for key: "+file.toString());
		jpool.saveJoinMap(file, ent.keyIdMap);
	}

	public static synchronized byte[][] getKeys(File file) throws IOException{
		PoolEntry ent = null;
		if(jpool.keyIdPool.contains(file)){ 
			ent = jpool.valuesPool.get(file);
		} else {
			ent = new PoolEntry();
			String[] val = jpool.getJoinValues(file); 
			ent.keys = new byte[val.length][];
			for(int i=0;i<val.length;i++)
				ent.keys[i] = val[i].getBytes();
		}
		ent.count++;
		return ent.keys;
	}
	
	public static synchronized void removeKeys(File file) throws IOException {
		jpool.valuesPool.remove(file);
	}
	
	public static File getJoinFile(File srcFile, BinFieldMeta meta){
		return new File(srcFile.getParent(),meta.getJoinFile());
	}

	private String[] getJoinValues (File f) throws IOException {
		if(!f.exists()) {
			return new String[0];
		}
		BufferedReader reader = new BufferedReader(new FileReader(f));
		ArrayList<String> arr = new ArrayList<String>();
		String buff = null;
		while ((buff = reader.readLine())!=null){
			int loc = buff.indexOf(",");
			if(loc == -1){
				//corrupted record??
				continue;
			}
			int pos = Integer.parseInt(buff.substring(0,loc).trim());
			String val = buff.substring(loc+1);
			while(arr.size()<pos){
				//handle missing entries, this should not happen
				arr.add(null);
			}
			arr.add(val);
		}
		reader.close();
		String[] ret = new String[arr.size()];
		return arr.toArray(ret);
	}

	private Map<String, Integer> getJoinMap (File joinFile) throws IOException{
		HashMap<String, Integer> ret = new HashMap<String, Integer>();
		String[] values = getJoinValues(joinFile);
		for(int i=0;i<values.length;i++) {
			ret.put(values[i], i);
		}
		return ret;
	}

	final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	private void saveJoinValues(File joinFile, String[] arr) throws IOException {
		File bakFile = new File(joinFile.getParent(), joinFile.getName()+sdf.format(new Date())+".bak");
		if(bakFile.exists()) {
			bakFile.delete();
		}
		if(joinFile.exists()) {
			joinFile.renameTo(bakFile);
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(joinFile));
		for (int i = 0; i < arr.length; i++) {
			String val = arr[i]==null? "":arr[i];
			out.write(Integer.toString(i));
			out.write(",");
			out.write(val);
			out.write("\n");
		}
		out.close();
	}

	private void saveJoinMap(File joinFile, Map<String, Integer> jmap) throws IOException{
		if(jmap.size() == 0) {
			return;
		}
		Integer max = Collections.max(jmap.values());
		String[] arr = new String[max+1];
		for(String key: jmap.keySet()){
			arr[jmap.get(key)] = key;
		}
		saveJoinValues(joinFile, arr);
	}
}
