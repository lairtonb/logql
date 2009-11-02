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

    $Id: StatementImpl.java,v 1.2 2009/10/29 05:11:07 mreddy Exp $
*/
package com.logql.interpret;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.logql.interpret.wfunc.Operator;
import com.logql.meta.Config;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.meta.LogMeta;
import com.logql.meta.Reader;
import com.logql.meta.std.CSVMeta;
import com.logql.meta.std.SepMeta;
import com.logql.meta.xl.XLMeta;
import com.logql.util.InputStreamWrapper;
import com.logql.util.UtilMethods;

public class StatementImpl implements Statement {
	LogMeta currConfig;
	List<InputStreamWrapper> input;
	String fClause;

	public class ExecuteMeta {
		public GroupBy group;
		public String fClause;
		public MetricOps ops;
		public Operator where;
		public SelectMeta smeta;
		public HashSet<FieldMeta> requiredFields;
		public LogMeta currConfig;
		public List<InputStreamWrapper> input;
	}

	static{
		try{
			Class.forName("com.logql.util.UtilMethods");
		}catch(ClassNotFoundException cfe){
			cfe.printStackTrace();
		}
	}

	protected boolean externalOp(String query) throws SQLException{
		String lquery = query.toLowerCase();
		ExecuteMeta emeta = new ExecuteMeta();
		try {
			if (lquery.startsWith("from ")) {
				processFrom(emeta, query);
				input = emeta.input;
				fClause = emeta.fClause;
				if(emeta.currConfig != null){
					currConfig = emeta.currConfig;
				}
				return true;
			} else if (lquery.startsWith("use ")) {
				emeta.input = input;
				processUse(emeta, query);
				currConfig = emeta.currConfig;
				return true;
			}
		} catch (IllegalArgumentException iae) {
			SQLException se = new SQLException(iae.getMessage());
			se.initCause(iae);
			throw se;
		} catch (Exception e){
			SQLException se = new SQLException("Unknown error: "+e.getMessage()+": "+e.getClass().getName());
			se.initCause(e);
			throw se;
		}
		return false;
	}

	public ResultSet executeQuery(String query) throws SQLException{
		if (externalOp(query)) {
			return null;
		}
		try{
			final ExecuteMeta emeta = compile(query);
			if (emeta.group instanceof GrepData) {
				// if grep command, spawn a thread to processess
				new Thread() {
					public void run() {
						execute(emeta);
					}
				}.start();
				//wait till resultsetMeta is initialized to return
				while (!((GrepData) emeta.group).initialized()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ie) {
					}
				}
				return emeta.group.getResultSet();
			} else {
				// for group/select command
				return execute(emeta);
			}
		} catch(IllegalArgumentException ie){
			SQLException se = new SQLException(ie.getMessage());
			se.initCause(ie);
			throw se;
		} catch (Exception e){
			SQLException se = new SQLException("Unknown error: "+e.getMessage()+": "+e.getClass().getName());
			se.initCause(e);
			throw se;
		}
	}

	public LogMeta getCurrConfig() {
		return currConfig;
	}
	
	public String[] getMetaInfo() throws SQLException{
		if(currConfig == null)
			throw new SQLException("No config loaded");
		String[] ret = new String[currConfig.getFields().size()];
		int i = 0;
		for (FieldMeta fm : currConfig.getFields()) {
			ret[i++] = fm.toString();
		}
		return ret;
	}

	public ExecuteMeta compile(String query) throws SQLException{
		String lquery = query.toLowerCase();
		boolean grepCommand = false;
		String selectPart = null;
		String fromClause = null;
		String useClause = null;
		String whereClause = null;
		String orderClause = null;

		int loc = lquery.indexOf("order by");
		if (loc > -1) {
			orderClause = query.substring(loc + 9).trim();
			query = query.substring(0, loc);
			lquery = lquery.substring(0, loc);
		}
		loc = lquery.indexOf(" where ");
		if (loc > -1) {
			whereClause = query.substring(loc + 7).trim();
			query = query.substring(0, loc);
			lquery = lquery.substring(0, loc);
		}
		loc = lquery.indexOf(" use ");
		if (loc > -1) {
			useClause = query.substring(loc + 5).trim();
			query = query.substring(0, loc);
			lquery = lquery.substring(0, loc);
		}
		loc = lquery.indexOf(" from ");
		if (loc > -1) {
			fromClause = query.substring(loc + 6).trim();
			query = query.substring(0, loc);
			lquery = lquery.substring(0, loc);
		}
		if (lquery.startsWith("grep ")) {
			grepCommand = true;
			selectPart = query.substring(5).trim();
		} else if (lquery.startsWith("select ")) {
			grepCommand = false;
			selectPart = query.substring(7).trim();
		}

		ExecuteMeta emeta =  new ExecuteMeta();
		emeta.currConfig = currConfig;
		emeta.input = input;
		emeta.fClause = fClause;
		//////////////////////////////////////////////////////////////////////////////////////
		////////////// From clause
		if (fromClause != null && fromClause.length() > 0)
			processFrom(emeta, fromClause);

		emeta.requiredFields = new HashSet<FieldMeta>();

		///////////////////////////////////////////////////////////////////////////////////
		////////////// Use clause
		if (useClause != null && useClause.length() > 0)
			processUse(emeta, useClause);

		//////////////////////////////////////////////////////////////////////////////////
		//////////////validate
		if (emeta.currConfig == null)
			throw new IllegalArgumentException("No configuration loaded");
		if (emeta.input == null || emeta.input.isEmpty())
			throw new IllegalArgumentException("No from clause");

		/////////////////////////////////////////////////////////////////////////////////////
		/////////// Where clause
		if (whereClause != null && whereClause.length() > 0) {
			CompileWhere cw=new CompileWhere(emeta.currConfig);
			emeta.where=cw.compile(whereClause, this);
			emeta.requiredFields = cw.getRequiredFields();
		}

		//////////////////////////////////////////////////////////////////////////////////////
		///////////// Select clause
		if(selectPart==null || selectPart.trim().length()==0)
			throw new IllegalArgumentException("No fields for output");
		emeta.smeta=new SelectMeta(emeta.currConfig);
		emeta.smeta.compile(selectPart, emeta.fClause, grepCommand);

		emeta.group = emeta.smeta.getGroupBy();
		emeta.ops = emeta.smeta.getOps();
		
		//////////////////////////////////////////////////////////////////////////////////////
		///////////// Order clause
		if(orderClause != null && orderClause.trim().length() > 0){
			if(emeta.group instanceof GrepData)
				throw new IllegalArgumentException("Cannot use order by with grep");
			OrderBy ord = new OrderBy();
			ord.compile(orderClause, emeta.smeta);
			((GroupBy)emeta.group).setOrder(ord);
		}

		emeta.requiredFields.addAll(emeta.smeta.getRequiredFields());

		emeta.currConfig.compute();
		return emeta;
	}

	public void processFrom(ExecuteMeta emeta, String fromClause){
		if (fromClause == null || fromClause.length() == 0)
			throw new IllegalArgumentException("Empty from clause");

		int u = fromClause.toLowerCase().indexOf(" use ");
		String useClause = null;
		if (u > -1) {
			useClause = fromClause.substring(u + " use ".length()).trim();
			fromClause = fromClause.substring(0, u);
		}

		if(fromClause.toLowerCase().startsWith("from ")){
			fromClause = fromClause.substring("from ".length());
		}
		emeta.fClause = fromClause;
		if (fromClause != null && fromClause.length() > 0) {
			emeta.input = UtilMethods.getFiles(fromClause);
		}
		if (emeta.input == null || emeta.input.isEmpty())
			throw new IllegalArgumentException("Invalid from clause");
		else {
			boolean valid = true;
			for (InputStreamWrapper wrap : emeta.input) {
				valid |= wrap.validate();
			}
			if (!valid)
				throw new IllegalArgumentException("Invalid from clause");
		}

		if (useClause != null) {
			processUse(emeta, useClause);
		}
	}

	public boolean execute(String cmd) throws SQLException {
		throw new SQLException("Not implemented");
	}

	public void processUse(ExecuteMeta emeta, String usingClause) {
		if (usingClause == null || usingClause.length() == 0)
			throw new IllegalArgumentException("Empty use clause");

		if (usingClause.toLowerCase().startsWith("use ")) {
			usingClause = usingClause.substring("use ".length());
		}

		String lusing = usingClause.toLowerCase();
		int aloc = usingClause.indexOf("@");
		if (aloc > -1) {
			File f = new File(usingClause.substring(aloc + 1));
			if (!f.exists() || !f.isFile())
				throw new IllegalArgumentException(
						"Config file does not exist: " + f.toString());
			Config tconf = null;
			try {
				tconf = Config.load(f.toString());
			} catch (Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException(
						"Error loading config file: " + f.toString());
			}
			emeta.currConfig = tconf.getConfig(usingClause.substring(0, aloc));
		} else if (lusing.equals("csv") || lusing.equals("excel")
				|| (lusing.indexOf("(") > -1 && (lusing.startsWith("csv") 
						|| usingClause.startsWith("sep") || usingClause.startsWith("excel")))) {
			int eloc = usingClause.length();
			String args = null;
			if (usingClause.indexOf("(") > -1) {
				if (usingClause.indexOf(")") > -1)
					eloc = usingClause.indexOf(")");
				args = usingClause
						.substring(usingClause.indexOf("(") + 1, eloc);
			}
			try {
				if (emeta.input == null || emeta.input.isEmpty())
					throw new IllegalArgumentException("Execute from command first");
				if (lusing.startsWith("csv")) {
					CSVMeta cmeta = new CSVMeta();
					cmeta.readConfig(args, emeta.input.get(0).getInputStream());
					emeta.currConfig = cmeta;
				} else if (lusing.startsWith("sep")) {
					SepMeta smeta = new SepMeta();
					smeta.readConfig(args, emeta.input.get(0).getInputStream());
					emeta.currConfig = smeta;
				} else if (lusing.startsWith("excel")){
					XLMeta xmeta = new XLMeta();
					xmeta.readConfig(args, emeta.input.get(0).getInputStream());
					emeta.currConfig = xmeta;
				}
			} catch (IOException ie) {
				throw new IllegalArgumentException("Error reading file: " 
						+ emeta.input.get(0).toString());
			}
		} else {
			if (emeta.currConfig == null) {
				File f = new File(usingClause);
				if (!f.exists() || !f.isFile())
					throw new IllegalArgumentException("Unknown config: "+ usingClause);
				Config tconf = null;
				try {
					tconf = Config.load(f.toString());
				} catch (Exception e) {
					e.printStackTrace();
					throw new IllegalArgumentException(
							"Error loading config file: " + f.toString());
				}
				emeta.currConfig = tconf.getDefaultConfig();
				if (emeta.currConfig == null)
					throw new IllegalArgumentException(
							"No default config specified in file: "+ f.toString());
			}
		}

		if (emeta.currConfig == null)
			throw new IllegalArgumentException("No meta configuration loaded");
	}

	public ResultSet execute(ExecuteMeta emeta) {
		Reader reader = emeta.currConfig.getReader(emeta.requiredFields);
		HashMap<String, int[]> errors = new HashMap<String, int[]>();
		for (InputStreamWrapper iwrap : emeta.input) {
			try {
				reader.init(iwrap.getInputStream());
			} catch (IOException ie) {
				ie.printStackTrace();
				throw new IllegalArgumentException(
						"Error processing file: " + iwrap.toString());
			}

			if (emeta.group != null)
				emeta.group.initialize(reader.getFlexiRowMap());
			if (emeta.ops != null)
				emeta.ops.initialize(reader.getFlexiRowMap());
			if (emeta.where != null)
				emeta.where.init(reader.getFlexiRowMap());

			FlexiRow crow = null, dst = null;
			if (emeta.where == null) {
				while (reader.next()) {
					crow = reader.getFlexiRow();
					dst = emeta.group.findOrCreate(crow);
					emeta.ops.compute(crow, dst);
				}
			} else {
				while (reader.next()) {
					crow = reader.getFlexiRow();
					if (emeta.where.evaluate(crow)) {
						dst = emeta.group.findOrCreate(crow);
						emeta.ops.compute(crow, dst);
					}
				}
			}
			if (reader.getErrors().length > 0) {
				errors.put(iwrap.toString(), reader.getErrors());
			}
		}
		try{
			emeta.group.getResultSet().close();
			((ResultSetMetaImpl)emeta.group.getResultSet().getMetaData()).setErrors(errors);
			((ResultSetMetaImpl)emeta.group.getResultSet().getMetaData())
				.setLineCount(reader.getLineCount());
		}catch(SQLException se){}
		return emeta.group.getResultSet();
	}

	public StatementImpl clone() {
		StatementImpl si = new StatementImpl();
		si.currConfig = currConfig;
		si.input = input;
		return si;
	}

	//////////////////////////////////////////////////////////////////////////////////////////
	public void addBatch(String arg0) throws SQLException {
		// not supported

	}

	public void cancel() throws SQLException {
		// not supported

	}

	public void clearBatch() throws SQLException {
		// not supported

	}

	public void clearWarnings() throws SQLException {
		// not supported

	}

	public void close() throws SQLException {
		// not supported

	}

	public boolean execute(String arg0, int arg1) throws SQLException {
		// not supported
		return false;
	}

	public boolean execute(String arg0, int[] arg1) throws SQLException {
		// not supported
		return false;
	}

	public boolean execute(String arg0, String[] arg1) throws SQLException {
		// not supported
		return false;
	}

	public int[] executeBatch() throws SQLException {
		// not supported
		return null;
	}

	public int executeUpdate(String arg0) throws SQLException {
		// not supported
		return 0;
	}

	public int executeUpdate(String arg0, int arg1) throws SQLException {
		// not supported
		return 0;
	}

	public int executeUpdate(String arg0, int[] arg1) throws SQLException {
		// not supported
		return 0;
	}

	public int executeUpdate(String arg0, String[] arg1) throws SQLException {
		// not supported
		return 0;
	}

	public Connection getConnection() throws SQLException {
		// not supported
		return null;
	}

	public int getFetchDirection() throws SQLException {
		// not supported
		return 0;
	}

	public int getFetchSize() throws SQLException {
		// not supported
		return 0;
	}

	public ResultSet getGeneratedKeys() throws SQLException {
		// not supported
		return null;
	}

	public int getMaxFieldSize() throws SQLException {
		// not supported
		return 0;
	}

	public int getMaxRows() throws SQLException {
		// not supported
		return 0;
	}

	public boolean getMoreResults() throws SQLException {
		// not supported
		return false;
	}

	public boolean getMoreResults(int arg0) throws SQLException {
		// not supported
		return false;
	}

	public int getQueryTimeout() throws SQLException {
		// not supported
		return 0;
	}

	public ResultSet getResultSet() throws SQLException {
		// not supported
		return null;
	}

	public int getResultSetConcurrency() throws SQLException {
		// not supported
		return 0;
	}

	public int getResultSetHoldability() throws SQLException {
		// not supported
		return 0;
	}

	public int getResultSetType() throws SQLException {
		// not supported
		return 0;
	}

	public int getUpdateCount() throws SQLException {
		// not supported
		return 0;
	}

	public SQLWarning getWarnings() throws SQLException {
		// not supported
		return null;
	}

	public boolean isClosed() throws SQLException {
		// not supported
		return false;
	}

	public boolean isPoolable() throws SQLException {
		// not supported
		return false;
	}

	public void setCursorName(String arg0) throws SQLException {
		// not supported

	}

	public void setEscapeProcessing(boolean arg0) throws SQLException {
		// not supported

	}

	public void setFetchDirection(int arg0) throws SQLException {
		// not supported

	}

	public void setFetchSize(int arg0) throws SQLException {
		// not supported

	}

	public void setMaxFieldSize(int arg0) throws SQLException {
		// not supported

	}

	public void setMaxRows(int arg0) throws SQLException {
		// not supported

	}

	public void setPoolable(boolean arg0) throws SQLException {
		// not supported

	}

	public void setQueryTimeout(int arg0) throws SQLException {
		// not supported

	}

	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// not supported
		return false;
	}

	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// not supported
		return null;
	}

}
