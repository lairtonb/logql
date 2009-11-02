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

    $Id: ColorPick.java,v 1.2 2009/10/29 05:11:17 mreddy Exp $
*/
package com.logql.ui;

import java.awt.Color;

public class ColorPick {
	static int[][] colors={
		{156,154,255},
		{156,48,99},
		{255,255,206},
		{206,255,255},
		{99,0,99},
		{255,130,132},
		{0,101,206},
		{206,207,255},
		{0,0,132},
		{255,0,255},
		{255,255,0},
		{0,255,255},
		{132,0,132},
		{132,0,0},
		{0,130,132},
		{0,0,255},
		{0,207,255},
		{206,255,255},
		{206,255,206},
		{255,255,156},
		{156,207,255},
		{153,204,255},
		{255,153,204},
		{204,153,255},
		{255,204,153},
		{51,102,255},
		{51,204,204},
		{153,204,0},
		{255,204,0},
		{255,153,0},
		{255,102,0},
		{102,102,153},
		{150,150,150},
		{0,51,102},
		{51,153,102},
		{0,51,0},
		{51,51,0},
		{153,51,0},
		{153,51,102},
		{51,51,153},
		{51,51,51}};

	public static Color[] getColors(int numOfColors){
		return getColors(numOfColors,255);
	}
	public static Color[] getColors(int numOfColors,int alpha){
		int colPos=0;
		Color[] ret=new Color[numOfColors];
		for(int i=0;i<numOfColors;i++,colPos++){
			if(colPos==colors.length)colPos=0;
			ret[i]=new Color(colors[colPos][0],
					colors[colPos][1],
					colors[colPos][2],
					alpha);
		}
		return ret;
	}
}
