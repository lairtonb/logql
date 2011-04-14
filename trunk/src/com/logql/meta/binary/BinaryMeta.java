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

    $Id: StdReadInt.java,v 1.2 2009-10-29 05:11:12 mreddy Exp $
*/
package com.logql.meta.binary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.logql.meta.FieldMeta;
import com.logql.meta.LogMeta;
import com.logql.meta.Reader;
import com.logql.meta.Writer;
import com.logql.meta.std.StdReadField;
import com.logql.meta.std.StdSeperator;

public class BinaryMeta extends LogMeta {
	ArrayList<BinFieldMeta> orderedMeta;

	@Override
	public Reader getReader(Collection<FieldMeta> req) {
		ArrayList<StdReadField> read=new ArrayList<StdReadField>();

		for(int i=0;i<orderedMeta.size();i++){
			BinReadField r = getReader(orderedMeta.get(i));
			read.add(r);

			BinReadSep sep = new BinReadSep(FIELD_SEPERATOR_ID);
			sep.setLeftBytes(r.byteCount());
			read.add(sep);
		}

		return compile(req,read);
	}

	public Writer getWriter(List<FieldMeta> srcFields)
	{
		return new BinaryWriter(this,srcFields);
	}

	protected Reader getReader(StdSeperator currOp, Collection<FieldMeta> req,
			LogMeta lmeta) {
		return new BinaryReader(currOp, req, lmeta);
	}

	public BinReadField getReader(FieldMeta colId){
		switch (colId.getActualType()){
		case FieldMeta.FIELD_STRING: {
			String joinFile = ((BinFieldMeta)colId).getJoinFile();
			if(joinFile != null && joinFile.length() > 0) {
				return new BinReadJoinString(colId);
			}
			return new BinReadInt(colId);
		}
		case FieldMeta.FIELD_INTEGER: return new BinReadInt(colId);
		case FieldMeta.FIELD_DATE: return new BinReadDateSeconds(colId);
		case FieldMeta.FIELD_IP: new NullPointerException("IP not supported in binary mode");
		case FieldMeta.FIELD_LONG: return new BinReadLong(colId);
		case FieldMeta.FIELD_BYTES: return new BinReadLong(colId);
		case FieldMeta.FIELD_FLOAT: return new BinReadFloat(colId);
		case FieldMeta.FIELD_DOUBLE: return new BinReadDouble(colId);
		}
		return null;
	}

	public void readConfig(Node nd) {
		//TODO maybe I should have field meta maintain a list of ordered fields
		super.readConfig(nd);
		NodeList nl = nd.getChildNodes();
		orderedMeta = new ArrayList<BinFieldMeta>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node cnd = nl.item(i);
			if (cnd.getNodeType() == Node.TEXT_NODE
					|| cnd.getNodeType() == Node.COMMENT_NODE)
				continue;
			BinFieldMeta meta = null;
			if (cnd.getNodeName().equals("field")
					|| cnd.getNodeName().equals("derivedField")) {
				meta = (BinFieldMeta)getFieldMeta(cnd.getAttributes()
						.getNamedItem("name").getNodeValue());
				orderedMeta.add(meta);
			}
		}
	}

	public FieldMeta getFieldMetaObject(){
		return new BinFieldMeta();
	}

	public ArrayList<BinFieldMeta> getOrderedFieldMeta(){
		return orderedMeta;
	}
}
