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

    $Id: SepMeta.java,v 1.2 2009/10/29 05:11:12 mreddy Exp $
*/
package com.logql.meta.std;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.logql.meta.FieldMeta;
import com.logql.meta.LogMeta;
import com.logql.meta.Reader;
import com.logql.util.ArgumentsTokenizer;

public class SepMeta extends LogMeta {
	int headerLine = 1;
	ArrayList<FieldMeta> orderedMeta;
	String seperator;

	@Override
	public Reader getReader(Collection<FieldMeta> req) {
		if(seperator.equals("\\t"))
			seperator = "\t";
		ArrayList<StdReadField> read=new ArrayList<StdReadField>();

		for(int i=0;i<orderedMeta.size();i++){
			StdReadField r = getReader(orderedMeta.get(i));
			read.add(r);

			if (i != orderedMeta.size() - 1) {
				StdSeperator sep = new StdSeperator(FIELD_SEPERATOR_ID);
				sep.setSeperator(seperator);
				read.add(sep);
			}
		}
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
		Node sep = nd.getAttributes().getNamedItem("seperator");
		if(sep == null)
			throw new IllegalArgumentException("Expecting seperator");
		seperator = sep.getNodeValue();
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

	public void readConfig (String args, InputStream f) throws IOException{
		headerLine = 1;
		ArgumentsTokenizer at = new ArgumentsTokenizer(args);
		int count = at.countTokens();
		if (count == 0)
			throw new IllegalArgumentException(
					"Invalid usage for sep function. Expected sep(<seperator>,[<headerLinePos>])");

		seperator = at.nextToken();
		if(count > 1){
			try{
				headerLine = Integer.parseInt(at.nextToken());
			}catch(NumberFormatException nfe){
				throw new IllegalArgumentException("Invalid value: "
						+args+". Expecting header line number");
			}
		}
		readConfig(f);
	}

	public void readConfig (InputStream f) throws IOException{
		if(seperator.equals("\\t"))
			seperator = "\t";
		BufferedReader in = new BufferedReader(new InputStreamReader(f));
		String line = null;
		for(int i=0;i<headerLine && (line=in.readLine())!=null;i++);
		if(line==null)
			throw new IOException("File Empty?");
		StringTokenizer st = new StringTokenizer(line,seperator);
		fields = new ArrayList<FieldMeta>();
		int id=0;
		orderedMeta = new ArrayList<FieldMeta>();
		fields = new ArrayList<FieldMeta>();
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			FieldMeta meta = new FieldMeta();
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
}
