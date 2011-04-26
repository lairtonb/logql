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

    $Id: LogMeta.java,v 1.2 2009-10-29 05:11:16 mreddy Exp $
*/
package com.logql.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.logql.meta.std.StdReadDate;
import com.logql.meta.std.StdReadDouble;
import com.logql.meta.std.StdReadField;
import com.logql.meta.std.StdReadFloat;
import com.logql.meta.std.StdReadIP;
import com.logql.meta.std.StdReadInt;
import com.logql.meta.std.StdReadLong;
import com.logql.meta.std.StdReadString;
import com.logql.meta.std.StdReader;
import com.logql.meta.std.StdSeperator;

public abstract class LogMeta {
	public static final int FIELD_SEPERATOR_ID=-5;
	public static final int FIELD_DUMMY_ID=-6;

	protected String name;
	protected ArrayList<FieldMeta> fields;
	protected int intPos,datePos,longPos,floatPos,doublePos;

	public abstract Reader getReader(Collection<FieldMeta> req);
	
	public Writer getWriter(List<FieldMeta> srcFields) {
		throw new IllegalArgumentException("Not implemented");
	}

	public Reader compile(Collection<FieldMeta> req, ArrayList<StdReadField> read){
		if(read.size()==0)
			throw new IllegalArgumentException("Malformed lineFormat, no data found");
		if(read.size()>1){
			if(read.size()==1 && read.get(0) instanceof StdSeperator)
				throw new IllegalArgumentException("Malformed lineFormat, no variables found");
			else{
				//insert dummies as required. will be removed from the tree.
				if(read.get(0) instanceof StdSeperator){
					StdReadString str=new StdReadString(FIELD_DUMMY_ID);
					read.add(0, str);
				}
				if(read.get(read.size()-1) instanceof StdSeperator){
					StdReadString str=new StdReadString(FIELD_DUMMY_ID);
					read.add(str);
				}
			}
		}

		//construct execution tree
		LinkedList<StdReadField> stackVar = new LinkedList<StdReadField>();
		StdSeperator currOp=null;

		for(int i=read.size()-1;i>=0;i--){
			if(read.get(i) instanceof StdSeperator){
				if(currOp!=null){
					if (stackVar.size()>=2){
						currOp.left=stackVar.removeLast();
						currOp.right=stackVar.removeLast();
					}
					stackVar.addLast(currOp);
				}
				currOp=(StdSeperator)read.get(i);
			}else{
				stackVar.addLast(read.get(i));
			}
		}
		if(currOp!=null && stackVar.size()==2){
			currOp.left=stackVar.removeLast();
			currOp.right=stackVar.removeLast();
		}
		if(stackVar.size()!=0)
			throw new IllegalArgumentException("Invalid lineFormat: unbalanced");

		if(req == null)
			//validation run
			return null;

		HashSet<Integer> reqFieldIds=new HashSet<Integer>();
		reqFieldIds.add(new Integer(FIELD_SEPERATOR_ID));
		for(FieldMeta meta:req){
			reqFieldIds.add(new Integer(meta.getId()));
		}

		recurseRemove(currOp,reqFieldIds);

		return getReader(currOp, req, this);
	}

	protected Reader getReader(StdSeperator currOp, Collection<FieldMeta> req,
			LogMeta lmeta) {
		return new StdReader(currOp, req, lmeta);
	}

	protected void recurseRemove(StdSeperator sep,HashSet<Integer> reqFields){
		if (sep.left != null) {
			if (!reqFields.contains(new Integer(sep.left.getColumnId()))) {
				sep.left = null;
			} else if (sep.left.getColumnId() == FIELD_SEPERATOR_ID) {
				recurseRemove((StdSeperator) sep.left, reqFields);
			}
		}
		if (sep.right != null) {
			if (sep.right.getColumnId() == FIELD_SEPERATOR_ID) {
				recurseRemove((StdSeperator) sep.right, reqFields);
			} else if (!reqFields.contains(new Integer(sep.right.getColumnId()))) {
				sep.right = null;
			}
		}
	}

	public void readConfig(Node nd){
		name=nd.getAttributes().getNamedItem("name").getNodeValue();
		NodeList nl=nd.getChildNodes();
		fields=new ArrayList<FieldMeta>();
		HashSet<String> fnames=new HashSet<String>();
		for(int i=0;i<nl.getLength();i++){
			Node cnd=nl.item(i);
			if(cnd.getNodeType()==Node.TEXT_NODE ||cnd.getNodeType()==Node.COMMENT_NODE)
				continue;
			FieldMeta meta=null;
			if(cnd.getNodeName().equals("field")){
				meta = getFieldMetaObject();
			}else if(cnd.getNodeName().equals("derivedField")){
				meta=new DerivedField();
			}
			if (meta != null) {
				meta.readConfig(cnd);
				fields.add(meta);
				if (fnames.contains(meta.getName().toLowerCase()))
					throw new IllegalArgumentException("Field defined twice: "
							+ meta.getName());
				fnames.add(meta.getName().toLowerCase());
			}
		}
		//init derived fields
		for(FieldMeta fm:fields){
			if(fm instanceof DerivedField){
				((DerivedField)fm).process(this);
			}
		}
		compute();
	}

	public FieldMeta getFieldMetaObject(){
		return new FieldMeta();
	}

	public void compute(){
		Collections.sort(fields);
		int fieldType=FieldMeta.FIELD_STRING;
		for(int lid=0;lid<fields.size();lid++){
			fields.get(lid).id=lid;
			if(fields.get(lid).storageType!=fieldType){
				switch (fieldType) {
				case FieldMeta.FIELD_STRING:
					intPos = lid;
					break;
				case FieldMeta.FIELD_INTEGER:
					datePos = lid;
					break;
				case FieldMeta.FIELD_DATE:
					longPos = lid;
					break;					
				case FieldMeta.FIELD_LONG:
					floatPos = lid;
					break;
				case FieldMeta.FIELD_FLOAT:
					doublePos = lid;
					break;
				}
				fieldType=fields.get(lid).storageType;
			}
		}
	}

	public ArrayList<FieldMeta> getFields(){
		return fields;
	}
	public FieldMeta getFieldMeta(String name){
		for(FieldMeta meta:fields){
			if(meta.getName().equalsIgnoreCase(name) || meta.getAlias().contains(name))
				return meta;
		}
		return null;
	}
	public FieldMeta getFieldMeta(int colId){
		return fields.get(colId);
	}

	public StdReadField getReader(FieldMeta colId){
		switch (colId.getActualType()){
		case FieldMeta.FIELD_STRING: return new StdReadString(colId);
		case FieldMeta.FIELD_INTEGER: return new StdReadInt(colId);
		case FieldMeta.FIELD_DATE: return new StdReadDate(colId);
		case FieldMeta.FIELD_IP: return new StdReadIP(colId);
		case FieldMeta.FIELD_LONG: return new StdReadLong(colId);
		case FieldMeta.FIELD_BYTES: return new StdReadLong(colId);
		case FieldMeta.FIELD_FLOAT: return new StdReadFloat(colId);
		case FieldMeta.FIELD_DOUBLE: return new StdReadDouble(colId);
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public int getSkip() {
		return 0;
	}
}
