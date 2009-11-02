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

    $Id: XLFieldMeta.java,v 1.2 2009/10/29 05:11:10 mreddy Exp $
*/
package com.logql.meta.xl;

import java.util.ArrayList;

import org.apache.poi.ss.util.CellReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.logql.meta.FieldMeta;

public class XLFieldMeta extends FieldMeta {
	protected int xlcolPos;
	protected String colRef;
	CellReference cref;
	protected boolean isCellField;

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

	public boolean isCellField() {
		return isCellField;
	}

	public void setCellField(boolean isCellField) {
		this.isCellField = isCellField;
	}

	public CellReference getCref() {
		return cref;
	}

	public void setCref(CellReference cref) {
		this.cref = cref;
	}
}
