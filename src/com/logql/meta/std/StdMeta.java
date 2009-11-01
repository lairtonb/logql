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

    $Id: StdMeta.java,v 1.2 2009/10/29 05:11:12 mreddy Exp $
*/
package com.logql.meta.std;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.logql.meta.FieldMeta;
import com.logql.meta.LogMeta;
import com.logql.meta.Reader;

public class StdMeta extends LogMeta {

	String lineFormat;
	char varChar;

	public Reader getReader(Collection<FieldMeta> req){
		ArrayList<StdReadField> read=new ArrayList<StdReadField>();
		char[] cArr=lineFormat.toCharArray();
		boolean readSeperator=true;
		StringBuffer sbuff=new StringBuffer();

		char varChar='$';

		for(int i=0;i<cArr.length;i++){
			if(cArr[i] ==varChar){
				if(readSeperator){
					if(sbuff.length()>0){
						StdSeperator sep=new StdSeperator(FIELD_SEPERATOR_ID);
						sep.setSeperator(sbuff.toString());
						read.add(sep);
					}
					readSeperator=false;
				}else if(!readSeperator){
					String var=sbuff.toString();
					FieldMeta meta=getFieldMeta(var);
					if(meta==null)
						throw new IllegalArgumentException("No meta found for variable: "+var+" used in lineFormat");
					if(!read.isEmpty() && !(read.get(read.size()-1) instanceof StdSeperator)){
						String prev=getFieldMeta(read.get(read.size()-1).getColumnId()).getName();
						throw new IllegalArgumentException("No seperator between fields "+prev+" and "+var+" in lineFormat");
					}
					StdReadField readField=getReader(meta);
					read.add(readField);
					readSeperator=true;
				}
				sbuff=new StringBuffer();
			} else {
				sbuff.append(cArr[i]);
			}
		}
		if(sbuff.length()>0){
			if(readSeperator){
				StdSeperator sep=new StdSeperator(FIELD_SEPERATOR_ID);
				sep.setSeperator(sbuff.toString());
				read.add(sep);
			}else{
				throw new IllegalArgumentException("Malformed lineFormat, unterminated variable: "+sbuff.toString());
			}
		}
		if(read.isEmpty() || !(read.get(read.size() -1) instanceof StdSeperator))
			read.add(new EmptySeperator(FIELD_SEPERATOR_ID));
		
		return compile(req, read);
	}


	public void readConfig(Node nd){
		super.readConfig(nd);
		NodeList nl=nd.getChildNodes();
		for(int i=0;i<nl.getLength();i++){
			Node cnd=nl.item(i);
			if(cnd.getNodeName().equals("lineFormat")){
				Node vch=cnd.getAttributes().getNamedItem("variableChar");
				if(vch==null)
					throw new IllegalArgumentException("variableChar not found for config: "+name);
				if(vch.getNodeValue().length()!=1)
					throw new IllegalArgumentException("variableChar should be a single char. Error with config: "+name);
				varChar=vch.getNodeValue().charAt(0);
				lineFormat=cnd.getFirstChild().getNodeValue();
				getReader(new ArrayList<FieldMeta>());
				break;
			}
		}
	}
	
}
