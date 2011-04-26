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

    $Id: CSVMeta.java,v 1.3 2009-10-29 05:11:12 mreddy Exp $
*/
package com.logql.meta.std;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.logql.meta.FieldMeta;
import com.logql.meta.LogMeta;
import com.logql.meta.Reader;
import com.logql.util.ArgumentsTokenizer;
import com.logql.util.UtilMethods;

public class CSVMeta extends LogMeta {
	int headerLine = 1;
	boolean hasQuotes = true;
	ArrayList<FieldMeta> orderedMeta;

	//TODO we shouldn't need two columns for csv to work
	@Override
	public Reader getReader(Collection<FieldMeta> req) {
		ArrayList<StdReadField> read=new ArrayList<StdReadField>();

		for(int i=0;i<orderedMeta.size();i++){
			StdReadField r = getReader(orderedMeta.get(i));
			read.add(r);
			if (i != orderedMeta.size() - 1) {
				StdSeperator sep = null;
				if (hasQuotes)
					sep = new CSVSeperator(FIELD_SEPERATOR_ID);
				else {
					sep = new StdSeperator(FIELD_SEPERATOR_ID);
					sep.setSeperator(",");
				}

				read.add(sep);
			}
		}
		if (hasQuotes)
			read.add(new CSVEmptySeperator(FIELD_SEPERATOR_ID));
		else
			read.add(new EmptySeperator(FIELD_SEPERATOR_ID));

		return compile(req,read);
	}

	public void readConfig(Node nd) {
		super.readConfig(nd);
		Node skip = nd.getAttributes().getNamedItem("skipLines");
		if (skip != null) {
			try {
				headerLine = Integer.parseInt(skip.getNodeValue().trim());
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException(
						"Invalid value for skipLines in config " + name);
			}
		}
		Node quote = nd.getAttributes().getNamedItem("hasQuotes");
		if(quote != null) {
			hasQuotes = quote.getNodeValue().equalsIgnoreCase("true");
		}
		NodeList nl = nd.getChildNodes();
		orderedMeta = new ArrayList<FieldMeta>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node cnd = nl.item(i);
			if (cnd.getNodeType() == Node.TEXT_NODE
					|| cnd.getNodeType() == Node.COMMENT_NODE)
				continue;
			FieldMeta meta = null;
			if (cnd.getNodeName().equals("field")
					|| cnd.getNodeName().equals("derivedField")) {
				meta = getFieldMeta(cnd.getAttributes().getNamedItem("name")
						.getNodeValue());
				orderedMeta.add(meta);
			}
		}
	}

	public boolean hasQuotes() {
		return hasQuotes;
	}

	public void setHasQuotes(boolean q) {
		hasQuotes = q;
	}

	public void mergeConfig (InputStream f, boolean validate) throws IOException {
		HashMap<String, CSVFieldMeta> map = new HashMap<String, CSVFieldMeta>();
		for(FieldMeta meta: orderedMeta) {
			CSVFieldMeta cmeta = (CSVFieldMeta) meta;
			map.put(cmeta.getHeaderColumnName(), cmeta);
		}
		headerLine = 1;
		readConfig(f);
		
		replaceValues(fields, map);
		replaceValues(orderedMeta, map);
		//validate
		for(FieldMeta meta: orderedMeta) {
			CSVFieldMeta cmeta = (CSVFieldMeta)meta;
			map.remove(cmeta.getHeaderColumnName());
		}
		if(map.size() > 0 && validate){
			StringBuffer sb = new StringBuffer();
			for(FieldMeta meta:map.values()){
				sb.append(meta.getName());
			}
			throw new IllegalArgumentException("Unmapped fields from config (not found in file):"+sb.toString());
		}
		compute();
	}
	
	private void replaceValues(List<FieldMeta> fields,
			HashMap<String, CSVFieldMeta> map) {
		for (int i = 0; i < fields.size(); i++) {
			CSVFieldMeta cmeta = map.get(fields.get(i).getName());
			if (cmeta != null) {
				fields.set(i, cmeta);
			}
		}
	}

	public void readConfig(String args, InputStream f) throws IOException {
		headerLine = 1;
		if (args != null && args.length() > 0) {
			args = UtilMethods.removeQuotes(args);
			try {
				headerLine = Integer.parseInt(args);
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException("Invalid value: " + args
						+ ". Expecting header line number");
			}
		}
		readConfig(f);
	}

	public void readConfig(InputStream f) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(f));
		String line = null;
		for (int i = 0; i < headerLine && (line = in.readLine()) != null; i++)
			;
		if (line == null)
			throw new IOException("File Empty?");
		ArgumentsTokenizer at = new ArgumentsTokenizer(line);
		String tok;
		int id = 0;
		orderedMeta = new ArrayList<FieldMeta>();
		fields = new ArrayList<FieldMeta>();
		while ((tok = at.nextToken()) != null) {
			FieldMeta meta = getFieldMetaObject();
			if (headerLine < 0)
				meta.setName(Integer.toString(id + 1));
			else
				meta.setName(tok);
			meta.setStorageType(FieldMeta.FIELD_STRING);
			fields.add(meta);
			orderedMeta.add(meta);
		}
		in.close();
		compute();
	}
	
	public int getSkip() {
		return headerLine;
	}
	
	public FieldMeta getFieldMetaObject() {
		return new CSVFieldMeta();
	}
}
