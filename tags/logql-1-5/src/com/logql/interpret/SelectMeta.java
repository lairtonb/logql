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

    $Id: SelectMeta.java,v 1.2 2009/10/29 05:11:07 mreddy Exp $
*/
package com.logql.interpret;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.logql.interpret.func.Count;
import com.logql.interpret.func.DoubleCopy;
import com.logql.interpret.func.DoubleSum;
import com.logql.interpret.func.FloatCopy;
import com.logql.interpret.func.FloatSum;
import com.logql.interpret.func.HostName;
import com.logql.interpret.func.IntCopy;
import com.logql.interpret.func.IntSum;
import com.logql.interpret.func.IntToIP;
import com.logql.interpret.func.LongCopy;
import com.logql.interpret.func.LongSum;
import com.logql.interpret.func.LookupIPRange;
import com.logql.interpret.func.SelectFunction;
import com.logql.interpret.func.StrTok;
import com.logql.interpret.func.StringFunction;
import com.logql.interpret.func.ToLower;
import com.logql.interpret.func.ToUpper;
import com.logql.interpret.func.URLDecode;
import com.logql.interpret.func.UrlAttribute;
import com.logql.interpret.func.date.DayFunction;
import com.logql.interpret.func.date.DayOfMonth;
import com.logql.interpret.func.date.DayOfWeekFunction;
import com.logql.interpret.func.date.HourFunction;
import com.logql.interpret.func.date.MonthFunction;
import com.logql.interpret.func.date.WeekOfMonthFunction;
import com.logql.interpret.func.date.WeekOfYearFunction;
import com.logql.interpret.func.date.YearFunction;
import com.logql.meta.DerivedField;
import com.logql.meta.FieldMeta;
import com.logql.meta.LogMeta;
import com.logql.util.UtilMethods;

public class SelectMeta {

	LogMeta meta;
	long lastQTime;
	int rowCount;
	int[] flexiRowMap;
	ArrayList<SelectFunction> functions;
	ArrayList<SelectFunction> metricFunctions;
	int  ssiz, isiz, osiz, dtsiz, lsiz, fsiz, dsiz;
	GroupBy group;
	MetricOps ops;
	String fromClause;
	boolean grepCommand;

	public SelectMeta(LogMeta lmeta) {
		meta = lmeta;
	}

	public GroupBy getGroupBy() {
		return group;
	}

	public MetricOps getOps() {
		return ops;
	}

	public int getColumnCount(){
		return functions.size()+metricFunctions.size();
	}

	public int getIntSize(){
		return isiz;
	}

	public ArrayList<SelectFunction> getColumns(){
		ArrayList<SelectFunction> ret=new ArrayList<SelectFunction>();
		ret.addAll(functions);
		ret.addAll(metricFunctions);
		return ret;
	}

	public void compile(String query, String from, boolean grep) {
		grepCommand = grep;
		fromClause = from;
		char[] ch = query.toCharArray();
		functions = new ArrayList<SelectFunction>();

		if (query.trim().equals("*")) {
			for (FieldMeta fm : meta.getFields()) {
				if (!(fm instanceof DerivedField))
					processField(fm.getName());
			}
		} else {
			int fb = 0;
			boolean q = false;
			StringBuffer field = new StringBuffer();
			for (int i = 0; i < ch.length; i++) {
				if (ch[i] == ',' && fb == 0 && !q) {
					processField(field.toString());
					field = new StringBuffer();
				} else {
					switch (ch[i]) {
					case '(':
						fb++;
						break;
					case ')':
						fb--;
						break;
					case '\"':
						q = !q;
					}
					field.append(ch[i]);
				}
			}
			processField(field.toString());
		}

		if (functions.size() == 0)
			throw new IllegalArgumentException("No select fields");

		Collections.sort(functions, new Comparator<SelectFunction>() {
			public int compare(SelectFunction f1, SelectFunction f2) {
				return f1.getStorageType() - f2.getStorageType();
			}
		});

		flexiRowMap = new int[functions.size()];
		int pos = 0, currType = FieldMeta.FIELD_STRING;
		SelectFunction f = null;
		for (int i = 0; i < functions.size(); i++) {
			f = functions.get(i);
			if (f.getStorageType() != currType) {
				setSize(currType, pos);
				pos = 0;
				currType = f.getStorageType();
			}
			f.setDstColId(i);
			flexiRowMap[i] = pos++;
		}
		setSize(currType, pos);

		//secondary type
		for(SelectFunction sf:functions){
			if(sf.getSecondaryStroageType()!=-1){
				allocSecType(sf);
			}
		}

		metricFunctions = new ArrayList<SelectFunction>();
		for (int i = functions.size() - 1; i >= 0; i--) {
			if (functions.get(i).isMetricField()) {
				metricFunctions.add(functions.get(i));
				functions.remove(i);
			}
		}
		Collections.reverse(metricFunctions);

		SelectFunction[] gfunc = new SelectFunction[functions.size()];
		for (int i = 0; i < functions.size(); i++) {
			gfunc[i] = functions.get(i);
		}
		if(grep)
			group = new GrepData(this, gfunc);
		else
			group = new GroupBy(this,gfunc);
		
		if(grep && metricFunctions.size() > 0){
			StringBuffer mfs = new StringBuffer();
			for(SelectFunction sf: metricFunctions)
				mfs.append(sf.toString()).append(",");
			throw new IllegalArgumentException("Cannot use metric functions on grep: "+mfs.toString());
		}

		SelectFunction[] mfunc = new SelectFunction[metricFunctions.size()];
		for(int i=0;i<metricFunctions.size();i++){
			mfunc[i] = metricFunctions.get(i);
		}
		ops= new MetricOps(mfunc, flexiRowMap);
	}

	private void allocSecType(SelectFunction sf) {
		switch (sf.getSecondaryStroageType()) {
		case FieldMeta.FIELD_OBJECT:
			sf.setDstColPos2(osiz++);
			break;
		case FieldMeta.FIELD_DATE:
			sf.setDstColPos2(dtsiz++);
			break;
		}
	}

	private void setSize(int type, int pos) {
		switch (type) {
		case FieldMeta.FIELD_STRING:
			ssiz = pos;
			break;
		case FieldMeta.FIELD_INTEGER:
			isiz = pos;
			break;
		case FieldMeta.FIELD_OBJECT:
			isiz = pos + isiz;
			osiz = pos;
			break;
		case FieldMeta.FIELD_DATE:
			dtsiz = pos;
			break;
		case FieldMeta.FIELD_LONG:
			lsiz = pos;
			break;
		case FieldMeta.FIELD_FLOAT:
			fsiz = pos;
			break;
		case FieldMeta.FIELD_DOUBLE:
			dsiz = pos;
			break;
		}
	}

	public List<FieldMeta> getRequiredFields() {
		ArrayList<FieldMeta> ret = new ArrayList<FieldMeta>();
		for (SelectFunction sf : group.func)
			if (sf.getRequiredField() != null)
				ret.add(sf.getRequiredField());
		for (SelectFunction sf : ops.ops)
			if (sf.getRequiredField() != null)
				ret.add(sf.getRequiredField());
		return ret;
	}

	protected void processField(String f) {
		f = f.trim();
		String alias = null;
		if (f.endsWith("\"")) {
			if (f.length() < 3)
				throw new IllegalArgumentException("Error parsing query");
			int loc = f.lastIndexOf("\"", f.length() - 2);
			if (loc == -1)
				throw new IllegalArgumentException("Invalid field: " + f);
			alias = f.substring(loc + 1, f.length() - 1);
			f = f.substring(0, loc);
		}
		f = f.trim();
		if (alias == null)
			alias = f;
		int bl = f.indexOf("(");
		SelectFunction reqFunc = null;
		if (bl > -1) {
			String funcName = f.substring(0, bl);
			int lbl = f.lastIndexOf(")");
			if (lbl == -1)
				throw new IllegalArgumentException("No closing brace for: " + f);
			String args = f.substring(bl+1, lbl);
			reqFunc = getFunction(funcName,args);
		} else {
			FieldMeta reqMeta = meta.getFieldMeta(f);
			if (reqMeta == null)
				if (f.equals("count"))
					reqFunc = new Count();
				else
					throw new IllegalArgumentException("Unknown field: " + f);
			reqFunc = getFunction(reqMeta);
		}
		reqFunc.setAlias(alias);
		functions.add(reqFunc);
		//jdbc col pos starts with 1 so set colpos after adding to arraylist
		reqFunc.setColPos(functions.size());
	}

	SelectFunction getFunction(String name, String args) {
		name = name.trim().toLowerCase();
		SelectFunction ret = null;
		if (name.equals("count"))
			ret = new Count();
		else if (name.equals("strtok"))
			ret = new StrTok();
		else if (name.equals("hostname"))
			ret = new HostName();
		else if (name.equals("urldecode"))
			ret = new URLDecode();
		else if (name.equals("urlattribute"))
			ret = new UrlAttribute();
		else if (name.equals("tolowercase"))
			ret = new ToLower();
		else if (name.equals("touppercase"))
			ret = new ToUpper();
		else if (name.equals("date") || name.equals("day"))
			ret = new DayFunction();
		else if (name.equals("hour"))
			ret = new HourFunction();
		else if (name.equals("dayofweek") || name.equals("weekday"))
			ret = new DayOfWeekFunction();
		else if (name.equals("weekofmonth"))
			ret = new WeekOfMonthFunction();
		else if (name.equals("weekofyear"))
			ret = new WeekOfYearFunction();
		else if (name.equals("month"))
			ret = new MonthFunction();
		else if (name.equals("dayofmonth"))
			ret = new DayOfMonth();
		else if (name.equals("year"))
			ret = new YearFunction();
		else if (name.equals("inttoip"))
			ret = new IntToIP();
		else if (name.equals("lookupiprange"))
			ret = new LookupIPRange();
		else if(name.equals("sum")){
			FieldMeta fm = meta.getFieldMeta(args);
			if (fm == null)
				throw new IllegalArgumentException("Unknown field: " + args);

			if (fm.getStorageType() == FieldMeta.FIELD_STRING)
				fm.setStorageType(FieldMeta.FIELD_DOUBLE);

			ret = getSumFunction(fm);
			if (ret == null)
				throw new IllegalArgumentException(
						"Cannot use sum function for: " + args);
			ret.setField(fm);
		} else if (name.equals("todate")) {
			FieldMeta fm = UtilMethods.processToDate(meta, args);
			args = "";
			DayFunction df = new DayFunction();
			df.setField(fm);
			ret = df;
		}
		else
			throw new IllegalArgumentException("Unknown function: " + name);

		ret.setSelectMeta(this);
		ret.processFunctionArgs(meta, args);
		return ret;
	}
	
	SelectFunction getFunction(FieldMeta rmeta) {
		SelectFunction ret = null;
		if (rmeta instanceof DerivedField) {
			ret = UtilMethods.getDerivedFunction((DerivedField) rmeta);
		} else {
			switch (rmeta.getActualType()) {
			case FieldMeta.FIELD_STRING: {
				ret = new StringFunction();
				break;
			}
			case FieldMeta.FIELD_INTEGER: {
				ret = new IntCopy();
				break;
			}
			case FieldMeta.FIELD_DATE: {
				ret = new DayFunction();
				ret.processFunctionArgs(meta, rmeta.getName());
				break;
			}
			case FieldMeta.FIELD_LONG: {
				ret = grepCommand ? new LongCopy() : new LongSum();
				break;
			}
			case FieldMeta.FIELD_FLOAT: {
				ret = grepCommand ? new FloatCopy() : new FloatSum();
				break;
			}
			case FieldMeta.FIELD_DOUBLE: {
				ret = grepCommand ? new DoubleCopy() : new DoubleSum();
				break;
			}
			}
		}
		if (ret == null) // should not happen
			throw new IllegalArgumentException("Unknown field type for: "
					+ rmeta.getName());
		ret.setField(rmeta);
		return ret;
	}
	
	SelectFunction getSumFunction(FieldMeta fm){
		switch (fm.getStorageType()) {
		case FieldMeta.FIELD_INTEGER:
			return new IntSum();
		case FieldMeta.FIELD_FLOAT:
			return new FloatSum();
		case FieldMeta.FIELD_LONG:
			return new LongSum();
		case FieldMeta.FIELD_DOUBLE:
			return new DoubleSum();
		}
		return null;
	}

	public LogMeta getMeta() {
		return meta;
	}

	public ArrayList<SelectFunction> getFunctions() {
		return functions;
	}

	public ArrayList<SelectFunction> getMetricFunctions() {
		return metricFunctions;
	}

	public String getFromClause() {
		return fromClause;
	}
}
