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

    $Id: OpenInterface.java,v 1.2 2009/10/29 05:11:18 mreddy Exp $
*/
package com.logql.ui;

import java.awt.Component;

public interface OpenInterface {
	public String getFromClause();
	public boolean okClicked();
	public void setVisible(boolean b);
	public void setLocationRelativeTo(Component c);
	public void dispose();
}
