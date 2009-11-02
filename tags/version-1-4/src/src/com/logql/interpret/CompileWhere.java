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

    $Id: CompileWhere.java,v 1.2 2009/10/29 05:11:07 mreddy Exp $
*/
package com.logql.interpret;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;

import com.logql.interpret.func.DateFunction;
import com.logql.interpret.func.DoubleCopy;
import com.logql.interpret.func.FloatCopy;
import com.logql.interpret.func.HostName;
import com.logql.interpret.func.IntCopy;
import com.logql.interpret.func.LongCopy;
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
import com.logql.interpret.wfunc.AndOperator;
import com.logql.interpret.wfunc.BooleanOperator;
import com.logql.interpret.wfunc.ComparisonOperator;
import com.logql.interpret.wfunc.DateCompare;
import com.logql.interpret.wfunc.DoubleCompare;
import com.logql.interpret.wfunc.FloatCompare;
import com.logql.interpret.wfunc.IntCompare;
import com.logql.interpret.wfunc.IntIn;
import com.logql.interpret.wfunc.LongCompare;
import com.logql.interpret.wfunc.Operator;
import com.logql.interpret.wfunc.OrOperator;
import com.logql.interpret.wfunc.StringCompare;
import com.logql.interpret.wfunc.StringIn;
import com.logql.interpret.wfunc.StringLike;
import com.logql.interpret.wfunc.StringWhereFunction;
import com.logql.interpret.wfunc.WhereFunction;
import com.logql.meta.DerivedField;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.meta.LogMeta;
import com.logql.util.UtilMethods;

public class CompileWhere {
	public static final int _StateSearch = 0;
	public static final int _StateLHS = 1;
	public static final int _StateExp = 2;
	public static final int _StateRHS = 3;

	Stack<Operator> output = new Stack<Operator>();
	Stack<BooleanOperator> dump = new Stack<BooleanOperator>();
	HashSet<FieldMeta> reqFields = new HashSet<FieldMeta>();
	StatementImpl stmt;

	public static final char[][] _SearchChars = { { '\'', '\'' },
			{ '\"', '\"' } };

	LogMeta meta;

	public CompileWhere(LogMeta meta) {
		this.meta = meta;
	}

	public Operator compile(String whereClause, StatementImpl stmt) throws SQLException{
		this.stmt = stmt;
		if (whereClause.indexOf("`") > -1)
			throw new IllegalArgumentException(
					"Reserved character: ` please do not use in query");
		if (whereClause.indexOf("~") > -1)
			throw new IllegalArgumentException(
					"Reserved character: ~ please do not use in query");
		whereClause = whereClause.replaceAll("\\s", " ");
		whereClause = whereClause.replaceAll(" and ", " ` ");
		whereClause = whereClause.replaceAll(" AND ", " ` ");
		whereClause = whereClause.replaceAll(" or ", " ~ ");
		whereClause = whereClause.replaceAll(" OR ", " ~ ");
		whereClause += " ";

		int state = _StateSearch;
		char[] carr = whereClause.toCharArray();
		int fb = 0;
		int fbr = 0;
		boolean searchEndChar = false, startRead = false;
		char endChar = '\'';
		StringBuffer rhs = null, lhs = null, exp = null;
		for (int i = 0; i < carr.length; i++) {
			char c = carr[i];
			switch (state) {
			case _StateSearch: {
				if (Character.isJavaIdentifierStart(c)) {
					rhs = new StringBuffer();
					exp = new StringBuffer();
					lhs = new StringBuffer();
					lhs.append(c);
					state = _StateLHS;
				} else if (c == '(') {
					dump.push(new OpenBrace());
				} else if (c == ')') {
					while (!dump.isEmpty()) {
						BooleanOperator op = dump.pop();
						if (op instanceof OpenBrace) {
							break;
						}
						process(op);
					}
				} else if (isBooleanOperator(c)) {
					BooleanOperator opThis = getBooleanOperator(c);
					if (dump.isEmpty()) {
						dump.push(opThis);
					} else {
						while (!dump.isEmpty()) {
							BooleanOperator opTop = dump.pop();
							if (opTop instanceof OpenBrace) {
								dump.push(opTop);
								break;
							} else if (opTop.compareTo(opThis) >= 0) {
								process(opTop);
							} else {
								dump.push(opTop);
								break;
							}
						}
						dump.push(opThis);
					}
				}
				break;
			}
			case _StateLHS: {
				lhs.append(c);
				if (searchEndChar) {
					if (c == endChar) {
						state = _StateExp;
						startRead = false;
						searchEndChar = false;
					}
				} else if (fb > 0){
					if(c == '(')
						fb++;
					else if(c == ')')
						fb--;
				}else if (c == ' ') {
					state = _StateExp;
					startRead = false;
				} else if (c == '=' || c == '<' || c == '>' || c == '#') {
					int len = lhs.length();
					lhs.replace(len - 1, len, " ");
					exp.append(c);
					state = _StateRHS;
					startRead = false;
				} else {
					if(c == '(')
						fb++;
					else
						for (int j = 0; j < _SearchChars.length; j++) {
							if (c == _SearchChars[j][0]) {
								searchEndChar = true;
								endChar = _SearchChars[j][1];
							}
						}
				}
				break;
			}
			case _StateExp: {
				if (!startRead && c != ' ') {
					startRead = true;
				}
				if (startRead) {
					if (c == ' ' || isSeperatorChar(c)) {
						state = _StateRHS;
						startRead = false;
					} else {
						exp.append(c);
						if (exp.length() == 1 && isCompareOperator(c)) {
							state = _StateRHS;
							startRead = false;
						}
					}
				}
				break;
			}
			case _StateRHS: {
				if (!startRead && c != ' ') {
					startRead = true;
				}
				if (startRead) {
					rhs.append(c);
					if (searchEndChar) {
						if (c == endChar) {
							output.push(process(lhs.toString().trim(), exp.toString().trim(),
									rhs.toString().trim()));
							state = _StateSearch;
							startRead = false;
							searchEndChar = false;
						}
					} else if (fbr > 0){
						if(c == '(')
							fbr++;
						else if(c == ')')
							fbr--;
					} else if (c == ' ') {
						output.push(process(lhs.toString().trim(), exp.toString().trim(),
								rhs.toString().trim()));
						state = _StateSearch;
						startRead = false;
					} else if (c == ')') {
						int len = rhs.length();
						rhs.replace(len - 1, len, " ");
						output.push(process(lhs.toString().trim(), exp.toString().trim(),
								rhs.toString().trim()));
						state = _StateSearch;
						startRead = false;
						i--;
					} else {
						if(c == '(')
							fbr++;
						else
							for (int j = 0; j < _SearchChars.length; j++) {
								if (c == _SearchChars[j][0]) {
									searchEndChar = true;
									endChar = _SearchChars[j][1];
								}
							}
					}
				}
				break;
			}
			}
		}
		if(state != _StateSearch)
			throw new IllegalArgumentException("Malformed where clause");
		while (!dump.isEmpty()) {
			process(dump.pop());
		}
		if (output.size() != 1)
			throw new IllegalArgumentException("Unbalanced where clause");

		return output.pop();
	}

	protected boolean isSeperatorChar(char c) {
		for (int i = 0; i < _SearchChars.length; i++)
			if (_SearchChars[i][0] == c)
				return true;
		return false;
	}

	public HashSet<FieldMeta> getRequiredFields(){
		return reqFields;
	}

	protected ComparisonOperator process(String lhs, String exp, String rhs) throws SQLException{
		WhereFunction getter = null;
		exp = exp.toLowerCase();
		if (lhs.indexOf("(") > -1) {
			getter = getFunction(lhs);
			if (getter == null)
				throw new IllegalArgumentException("Invalid function: " + lhs);
		} else {
			FieldMeta fm = meta.getFieldMeta(lhs.toString().trim());
			if (fm == null) {
				throw new IllegalArgumentException("Unknown field: " + lhs);
			}
			getter = getFunction(fm);
		}
		reqFields.add(getter.getRequiredField());

		ComparisonOperator ret = null;

		switch (getter.getRequiredField().getActualType()) {
		case FieldMeta.FIELD_STRING: {
			if (exp.equals("=") || exp.equals("#")) {
				ret = new StringCompare();
			} else if (exp.equals("like") || exp.equals("notlike")) {
				ret = new StringLike();
			} else if (exp.equals("in") || exp.equals("notin")) {
				ret = new StringIn();
			}
			break;
		}
		case FieldMeta.FIELD_INTEGER: {
			if (exp.equals("in") || exp.equals("notin"))
				ret = new IntIn();
			else
				ret = new IntCompare();
			break;
		}
		case FieldMeta.FIELD_DATE: {
			DateCompare dc = new DateCompare();
			dc.setFunction((DateFunction) getter);
			ret = dc;
			break;
		}
		case FieldMeta.FIELD_IP: {
			// not supported
			return null;
		}
		case FieldMeta.FIELD_LONG: {
			LongCompare lc = new LongCompare();
			lc.setFunction((LongCopy) getter);
			ret = lc;
			break;
		}
		case FieldMeta.FIELD_FLOAT: {
			FloatCompare fc = new FloatCompare();
			fc.setFunction((FloatCopy) getter);
			ret = fc;
			break;
		}
		case FieldMeta.FIELD_DOUBLE: {
			DoubleCompare dc = new DoubleCompare();
			dc.setFunction((DoubleCopy) getter);
			ret = dc;
			break;
		}
		}
		ret.setFunction(getter);
		ret.setOperator(exp);

		if (ret instanceof StringIn)
			((StringIn) ret).processRHS(rhs, stmt);
		else if (ret instanceof IntIn)
			((IntIn) ret).processRHS(rhs, stmt);
		else
			ret.processRHS(rhs);

		return ret;
	}

	protected WhereFunction getFunction(String lhs) {
		int sloc = lhs.indexOf("(");
		int eloc = lhs.lastIndexOf(")");
		if (sloc == -1 || eloc == -1)
			throw new IllegalArgumentException("Invalid expression: " + lhs);
		String name = lhs.substring(0, sloc).trim().toLowerCase();
		String args = lhs.substring(sloc + 1, eloc);
		WhereFunction ret = null;
		if (name.equals("date") || name.equals("day"))
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
		else if (name.equals("lookupiprange"))
			ret = new LookupIPRange();
		else if (name.equals("todate")){
			ret = new DayFunction();
			args = lhs;
		} else if(name.equals("tonumber")){
			FieldMeta fm = meta.getFieldMeta(args);
			if (fm == null)
				throw new IllegalArgumentException("Unknown field: " + args);

			if (fm.getStorageType() == FieldMeta.FIELD_STRING)
				fm.setStorageType(FieldMeta.FIELD_DOUBLE);

			ret = new DoubleCopy();
			((DoubleCopy)ret).setField(fm);
			if (ret == null)
				throw new IllegalArgumentException(
						"Cannot use sum function for: " + args);
		}
		else
			throw new IllegalArgumentException("Unknown function: " + name);

		ret.processFunctionArgs(meta, args);
		return ret;
	}

	protected WhereFunction getFunction(FieldMeta meta) {
		if(meta instanceof DerivedField){
			SelectFunction func = (SelectFunction) UtilMethods
					.getDerivedFunction((DerivedField) meta);
			func.setField(meta);
			return (StringWhereFunction) func;
		}else {
			switch (meta.getActualType()) {
			case FieldMeta.FIELD_STRING: {
				StringFunction sg = new StringFunction();
				sg.setField(meta);
				return sg;
			}
			case FieldMeta.FIELD_INTEGER: {
				IntCopy ic = new IntCopy();
				ic.setField(meta);
				return ic;
			}
			case FieldMeta.FIELD_DATE: {
				DayFunction df = new DayFunction();
				df.processFunctionArgs(this.meta, meta.getName());
				return df;
			}
			case FieldMeta.FIELD_IP: {
				// not supported
				return null;
			}
			case FieldMeta.FIELD_LONG: {
				LongCopy lc = new LongCopy();
				lc.setField(meta);
				return lc;
			}
			case FieldMeta.FIELD_FLOAT: {
				FloatCopy fc = new FloatCopy();
				fc.setField(meta);
				return fc;
			}
			case FieldMeta.FIELD_DOUBLE: {
				DoubleCopy dc = new DoubleCopy();
				dc.setField(meta);
				return dc;
			}
			}
		}

		return null;
	}

	private boolean isBooleanOperator(char c) {
		return c == '`' || c == '~';
	}

	private boolean isCompareOperator(char c) {
		return c == '=' || c == '#' || c == '<' || c == '>';
	}

	private BooleanOperator getBooleanOperator(char c) {
		if (c == '`') {
			return new AndOperator();
		} else {
			return new OrOperator();
		}
	}

	private void process(BooleanOperator op) {
		if (output.size() < 2)
			throw new IllegalArgumentException("Unbalanced where clause");
		op.rhs = output.pop();
		op.lhs = output.pop();
		output.push(op);
	}

	class Stack<T> {
		LinkedList<T> data = new LinkedList<T>();

		public Stack() {
		}

		public T pop() {
			return data.removeLast();
		}

		public void push(T o) {
			data.addLast(o);
		}

		public boolean isEmpty() {
			return data.isEmpty();
		}

		public int size() {
			return data.size();
		}
	}

	class OpenBrace extends BooleanOperator {
		// dummy implementation for use in stack
		public int getOperatorPrecedence() {
			return -1;
		}

		public boolean evaluate(FlexiRow row) {
			return false;
		}
	}
}
