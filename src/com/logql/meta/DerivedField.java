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

    $Id: DerivedField.java,v 1.2 2009/10/29 05:11:15 mreddy Exp $
*/
package com.logql.meta;

import org.w3c.dom.Node;

public class DerivedField extends FieldMeta {
	String dclassName;
	String source;
	FieldMeta srcMeta;

	public void readConfig(Node nd) {
		super.readConfig(nd);
		dclassName = nd.getAttributes().getNamedItem("implementation")
				.getNodeValue().trim();
		source = nd.getAttributes().getNamedItem("source").getNodeValue()
				.trim();
	}

	public String getImplementationClass() {
		return dclassName;
	}

	public void process(LogMeta lm) {
		srcMeta = lm.getFieldMeta(source);
		if(srcMeta == null)
			throw new IllegalArgumentException("Unknown source field: "+source
					+" for derived field: "+name);
		storageType = srcMeta.getStorageType();
		actualType = FIELD_STRING;
	}

	public FieldMeta getSourceField() {
		return srcMeta;
	}
}
