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

    $Id: ChartOptions.java,v 1.2 2009/10/29 05:11:18 mreddy Exp $
*/
package com.logql.ui;

import java.util.ArrayList;

public class ChartOptions {
	private ArrayList<Object>[] options;
	private int chartType;
	private String dimension;
	private String type;
	private String orientation;
	private ArrayList<Object> categories;
	
	public ChartOptions()
	{
		
	}
	
	public void setOptions(ArrayList<Object>[] list)
	{
		this.options = list;
	}
	
	public ArrayList<Object>[] getOptions()
	{
		return options;
	}
	
	public void setChartType(int type)
	{
		this.chartType =type;
	}
	
	public int getChartType()
	{
		return chartType;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public String getType()
	{
		return type;
	}
	
	public void setOrientation(String orient)
	{
		this.orientation = orient;
	}
	
	public String getOrientation()
	{
		return orientation;
	}
	
	public void setDimension(String dimen)
	{
		this.dimension = dimen;
	}
	
	public String getDimension()
	{
		return dimension;
	}

	public void setCategories(ArrayList<Object> list)
	{
		this.categories = list;
	}
	
	public ArrayList<Object> getCategories()
	{
		return categories;
	}
	
	public String toString()
	{
		return "ChartType:"+getChartType()+",Options"+getOptions()+",getCategories"+getCategories();
	}
}
