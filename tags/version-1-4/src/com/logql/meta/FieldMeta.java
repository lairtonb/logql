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

    $Id: FieldMeta.java,v 1.2 2009/10/29 05:11:16 mreddy Exp $
*/
package com.logql.meta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FieldMeta implements Comparable<FieldMeta>{
	public static final int FIELD_STRING=0;
	public static final int FIELD_INTEGER=1;
	//	hack, since object is only used by count and it's final output is integer
	public static final int FIELD_OBJECT=2;
	public static final int FIELD_DATE=3;
	public static final int FIELD_IP=4;
	public static final int FIELD_LONG=5;
	public static final int FIELD_BYTES=6;
	public static final int FIELD_FLOAT=7;
	public static final int FIELD_DOUBLE=8;  

	protected int id;
	protected int storageType;
	protected int actualType;
	protected String name;
	protected ArrayList<String> alias=new ArrayList<String>();
	protected SimpleDateFormat format;

	public String getName() {
		return name;
	}

	public void readConfig(Node nd){
		name = nd.getAttributes().getNamedItem("name").getNodeValue().trim();
		if(name.indexOf(" " ) > -1 || name.indexOf("\t") >-1)
			throw new IllegalArgumentException("Invalid field name: "+name);

		String stype = "string";
		if(nd.getAttributes().getNamedItem("type")!=null)
			stype = nd.getAttributes().getNamedItem("type").getNodeValue();
		storageType = actualType = getType(stype);

		if (storageType == -1) {
			throw new IllegalArgumentException("Unrecognized type: " + stype
					+ " for node: " + name);
		}
		if (storageType == FIELD_DATE) {
			String sformat = nd.getAttributes().getNamedItem("format").getNodeValue();
			if(!sformat.equalsIgnoreCase("utc"))
				format = new SimpleDateFormat(sformat);
		}
		if (storageType == FIELD_BYTES) {
			storageType = FIELD_LONG;
		}
		if (storageType == FIELD_IP) {
			storageType = FIELD_INTEGER;
		}
		NodeList nalias = nd.getChildNodes();
		alias = new ArrayList<String>();
		for (int i = 0; i < nalias.getLength(); i++) {
			Node and = nalias.item(i);
			if (and.getNodeName().equals("alias")) {
				alias.add(and.getAttributes().getNamedItem("name")
						.getNodeValue());
			}
		}
	}

	public ArrayList<String> getAlias(){
		return alias;
	}

	public int getActualType() {
		return actualType;
	}

	public void setDateFormatter(SimpleDateFormat sf) {
		format = sf;
	}

	public SimpleDateFormat getDateFormater() {
		return format;
	}

	public int getStorageType() {
		return storageType;
	}
	
	public void setName(String na){
		name = na;
	}
	
	public void setStorageType(int t){
		storageType = t;
		actualType = t;
	}

	public void setId(int i){
		id = i;
	}

	public int getId() {
		return id;
	}

	public static int getType(String s) {
		s = s.toLowerCase().trim();
		if (s.equals("string") || s.equals("char") || s.equals("varchar"))
			return FIELD_STRING;
		else if (s.equals("integer") || s.equals("int"))
			return FIELD_INTEGER;
		else if (s.equals("long"))
			return FIELD_LONG;
		else if (s.equals("float"))
			return FIELD_FLOAT;
		else if (s.equals("double") || s.equals("number") || s.equals("decimal"))
			return FIELD_DOUBLE;
		else if (s.equals("date"))
			return FIELD_DATE;
//		else if (s.equals("ip"))
//			return FIELD_IP;
//		else if (s.equals("bytes"))
//			return FIELD_BYTES;

		return -1;
	}

	public static String getTypeString(int type) {
		switch (type) {
		case FIELD_STRING:
			return "string";
		case FIELD_INTEGER:
			return "integer";
		case FIELD_DATE:
			return "date";
		case FIELD_IP:
			return "ip";
		case FIELD_LONG:
			return "long";
		case FIELD_BYTES:
			return "bytes";
		case FIELD_FLOAT:
			return "float";
		case FIELD_DOUBLE:
			return "double";
		}
		return "error: unknown type";
	}

	public boolean equals(Object o){
		if( !(o instanceof FieldMeta))
			return false;
		return ((FieldMeta)o).id == id;
	}
	public int compareTo(FieldMeta o2) {
		if (storageType != o2.storageType) {
			return storageType - o2.storageType;
		}
		return 0;
	}

	public String toString() {
		return name + "\t" + getTypeString(actualType);
	}
}
