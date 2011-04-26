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

    $Id: ResultSetImpl.java,v 1.2 2009/10/29 05:11:07 mreddy Exp $
*/
package com.logql.interpret;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Map;

import com.logql.interpret.ResultSetMetaImpl.ColumnData;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;

public abstract class ResultSetImpl implements ResultSet {

	protected FlexiRow curr;
	SelectMeta smeta;
	ResultSetMetaImpl rmeta;
	NumberFormat numFormat;

	public ResultSetImpl(SelectMeta smeta) {
		this.smeta = smeta;
		rmeta = new ResultSetMetaImpl(smeta);
		numFormat = DecimalFormat.getInstance();
		((DecimalFormat)numFormat).applyPattern("#.##");
		numFormat.setMaximumFractionDigits(2);
		numFormat.setMinimumFractionDigits(2);
	}

	public String getString(int col) throws SQLException {
		ColumnData cd = rmeta.getColumnData(col);
		switch (cd.colType) {
		case FieldMeta.FIELD_STRING: return curr.stringArr[cd.dstColPos];
		case FieldMeta.FIELD_INTEGER: return Integer.toString(curr.intArr[cd.dstColPos]);
		case FieldMeta.FIELD_DATE: return curr.stringArr[cd.dstColPos]; // should not happen
		case FieldMeta.FIELD_LONG: return Long.toString(curr.longArr[cd.dstColPos]);
		case FieldMeta.FIELD_FLOAT: return numFormat.format(curr.floatArr[cd.dstColPos]);
		case FieldMeta.FIELD_DOUBLE: return numFormat.format(curr.doubleArr[cd.dstColPos]);
		}
		return null;
	}

	public String getString(String name) throws SQLException {
		return getString(rmeta.getColumnPos(name));
	}

	public int findColumn(String name) throws SQLException {
		return rmeta.getColumnPos(name);
	}

	public Date getDate(int col) throws SQLException {
		return new Date(curr.dateArr[rmeta.getColumnData(col).dstColPos2].getTimeInMillis());
	}

	public Date getDate(String name) throws SQLException {
		return getDate(rmeta.getColumnPos(name));
	}

	public double getDouble(int col) throws SQLException {
		return curr.doubleArr[rmeta.getColumnData(col).dstColPos];
	}

	public double getDouble(String name) throws SQLException {
		return getDouble(rmeta.getColumnPos(name));
	}

	public int getType() throws SQLException {
		return TYPE_FORWARD_ONLY;
	}
	
	public float getFloat(int col) throws SQLException {
		return curr.floatArr[rmeta.getColumnData(col).dstColPos];
	}

	public float getFloat(String name) throws SQLException {
		return getFloat(rmeta.getColumnPos(name));
	}

	public int getInt(int col) throws SQLException {
		return curr.intArr[rmeta.getColumnData(col).dstColPos];
	}

	public int getInt(String name) throws SQLException {
		return getInt(rmeta.getColumnPos(name));
	}

	public long getLong(int col) throws SQLException {
		return curr.longArr[rmeta.getColumnData(col).dstColPos];
	}

	public long getLong(String name) throws SQLException {
		return getLong(rmeta.getColumnPos(name));
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return rmeta;
	}

	public int getConcurrency() throws SQLException {
		// not supported
		return CONCUR_READ_ONLY;
	}

	public Object getObject(int col) throws SQLException {
		ColumnData cd = rmeta.getColumnData(col);
		switch (cd.colType) {
		case FieldMeta.FIELD_STRING: return curr.stringArr[cd.dstColPos];
		case FieldMeta.FIELD_INTEGER: return new Integer(curr.intArr[cd.dstColPos]);
		case FieldMeta.FIELD_DATE: return curr.stringArr[cd.dstColPos]; // should not happen
		case FieldMeta.FIELD_LONG: return new Long(curr.longArr[cd.dstColPos]);
		case FieldMeta.FIELD_FLOAT: return new Float(curr.floatArr[cd.dstColPos]);
		case FieldMeta.FIELD_DOUBLE: return new Double(curr.doubleArr[cd.dstColPos]);
		}
		return null;
	}

	public Object getObject(String name) throws SQLException {
		return getObject(rmeta.getColumnPos(name));
	}

//////////////////////////////////////////////////////////////////////////////////

	public Object getObject(int arg0, Map<String, Class<?>> arg1)
			throws SQLException {
		// not supported
		return null;
	}

	public Object getObject(String arg0, Map<String, Class<?>> arg1)
			throws SQLException {
		// not supported
		return null;
	}
	public Date getDate(int arg0, Calendar arg1) throws SQLException {
		// not supported
		return null;
	}

	public Date getDate(String arg0, Calendar arg1) throws SQLException {
		// not supported
		return null;
	}

	public void cancelRowUpdates() throws SQLException {
		// not supported

	}

	public void clearWarnings() throws SQLException {
		// not supported

	}

	public void deleteRow() throws SQLException {
		// not supported

	}

	public Array getArray(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public Array getArray(String arg0) throws SQLException {
		// not supported
		return null;
	}

	public InputStream getAsciiStream(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public InputStream getAsciiStream(String arg0) throws SQLException {
		// not supported
		return null;
	}

	public BigDecimal getBigDecimal(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public BigDecimal getBigDecimal(String arg0) throws SQLException {
		// not supported
		return null;
	}

	public BigDecimal getBigDecimal(int arg0, int arg1) throws SQLException {
		// not supported
		return null;
	}

	public BigDecimal getBigDecimal(String arg0, int arg1) throws SQLException {
		// not supported
		return null;
	}

	public InputStream getBinaryStream(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public InputStream getBinaryStream(String arg0) throws SQLException {
		// not supported
		return null;
	}

	public Blob getBlob(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public Blob getBlob(String arg0) throws SQLException {
		// not supported
		return null;
	}

	public boolean getBoolean(int arg0) throws SQLException {
		// not supported
		return false;
	}

	public boolean getBoolean(String arg0) throws SQLException {
		// not supported
		return false;
	}

	public byte getByte(int arg0) throws SQLException {
		// not supported
		return 0;
	}

	public byte getByte(String arg0) throws SQLException {
		// not supported
		return 0;
	}

	public byte[] getBytes(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public byte[] getBytes(String arg0) throws SQLException {
		// not supported
		return null;
	}

	public Reader getCharacterStream(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public Reader getCharacterStream(String arg0) throws SQLException {
		// not supported
		return null;
	}

	public Clob getClob(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public Clob getClob(String arg0) throws SQLException {
		// not supported
		return null;
	}

	public String getCursorName() throws SQLException {
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

	public int getHoldability() throws SQLException {
		// not supported
		return 0;
	}

	public Reader getNCharacterStream(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public Reader getNCharacterStream(String arg0) throws SQLException {
		// not supported
		return null;
	}

	public String getNString(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public String getNString(String arg0) throws SQLException {
		// not supported
		return null;
	}

	public Ref getRef(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public Ref getRef(String arg0) throws SQLException {
		// not supported
		return null;
	}

	public int getRow() throws SQLException {
		// not supported
		return 0;
	}

	public short getShort(int arg0) throws SQLException {
		// not supported
		return 0;
	}

	public short getShort(String arg0) throws SQLException {
		// not supported
		return 0;
	}

	public Statement getStatement() throws SQLException {
		// not supported
		return null;
	}

	public Time getTime(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public Time getTime(String arg0) throws SQLException {
		// not supported
		return null;
	}

	public Time getTime(int arg0, Calendar arg1) throws SQLException {
		// not supported
		return null;
	}

	public Time getTime(String arg0, Calendar arg1) throws SQLException {
		// not supported
		return null;
	}

	public Timestamp getTimestamp(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public Timestamp getTimestamp(String arg0) throws SQLException {
		// not supported
		return null;
	}

	public Timestamp getTimestamp(int arg0, Calendar arg1) throws SQLException {
		// not supported
		return null;
	}

	public Timestamp getTimestamp(String arg0, Calendar arg1)
			throws SQLException {
		// not supported
		return null;
	}

	public URL getURL(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public URL getURL(String arg0) throws SQLException {
		// not supported
		return null;
	}

	public InputStream getUnicodeStream(int arg0) throws SQLException {
		// not supported
		return null;
	}

	public InputStream getUnicodeStream(String arg0) throws SQLException {
		// not supported
		return null;
	}

	public SQLWarning getWarnings() throws SQLException {
		// not supported
		return null;
	}

	public void insertRow() throws SQLException {
		// not supported

	}

	public void moveToCurrentRow() throws SQLException {
		// not supported

	}

	public void moveToInsertRow() throws SQLException {
		// not supported

	}

	public boolean previous() throws SQLException {
		return false;
	}

	public void refreshRow() throws SQLException {
		// not supported

	}

	public boolean relative(int arg0) throws SQLException {
		// not supported
		return false;
	}

	public boolean rowDeleted() throws SQLException {
		// not supported
		return false;
	}

	public boolean rowInserted() throws SQLException {
		// not supported
		return false;
	}

	public boolean rowUpdated() throws SQLException {
		// not supported
		return false;
	}

	public void setFetchDirection(int arg0) throws SQLException {
		// not supported

	}

	public void setFetchSize(int arg0) throws SQLException {
		// not supported

	}

	public void updateArray(int arg0, Array arg1) throws SQLException {
		// not supported

	}

	public void updateArray(String arg0, Array arg1) throws SQLException {
		// not supported

	}

	public void updateAsciiStream(int arg0, InputStream arg1)
			throws SQLException {
		// not supported

	}

	public void updateAsciiStream(String arg0, InputStream arg1)
			throws SQLException {
		// not supported

	}

	public void updateAsciiStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		// not supported

	}

	public void updateAsciiStream(String arg0, InputStream arg1, int arg2)
			throws SQLException {
		// not supported

	}

	public void updateAsciiStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// not supported

	}

	public void updateAsciiStream(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		// not supported

	}

	public void updateBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
		// not supported

	}

	public void updateBigDecimal(String arg0, BigDecimal arg1)
			throws SQLException {
		// not supported

	}

	public void updateBinaryStream(int arg0, InputStream arg1)
			throws SQLException {
		// not supported

	}

	public void updateBinaryStream(String arg0, InputStream arg1)
			throws SQLException {
		// not supported

	}

	public void updateBinaryStream(int arg0, InputStream arg1, int arg2)
			throws SQLException {
		// not supported

	}

	public void updateBinaryStream(String arg0, InputStream arg1, int arg2)
			throws SQLException {
		// not supported

	}

	public void updateBinaryStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// not supported

	}

	public void updateBinaryStream(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		// not supported

	}

	public void updateBlob(int arg0, Blob arg1) throws SQLException {
		// not supported

	}

	public void updateBlob(String arg0, Blob arg1) throws SQLException {
		// not supported

	}

	public void updateBlob(int arg0, InputStream arg1) throws SQLException {
		// not supported

	}

	public void updateBlob(String arg0, InputStream arg1) throws SQLException {
		// not supported

	}

	public void updateBlob(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// not supported

	}

	public void updateBlob(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		// not supported

	}

	public void updateBoolean(int arg0, boolean arg1) throws SQLException {
		// not supported

	}

	public void updateBoolean(String arg0, boolean arg1) throws SQLException {
		// not supported

	}

	public void updateByte(int arg0, byte arg1) throws SQLException {
		// not supported

	}

	public void updateByte(String arg0, byte arg1) throws SQLException {
		// not supported

	}

	public void updateBytes(int arg0, byte[] arg1) throws SQLException {
		// not supported

	}

	public void updateBytes(String arg0, byte[] arg1) throws SQLException {
		// not supported

	}

	public void updateCharacterStream(int arg0, Reader arg1)
			throws SQLException {
		// not supported

	}

	public void updateCharacterStream(String arg0, Reader arg1)
			throws SQLException {
		// not supported

	}

	public void updateCharacterStream(int arg0, Reader arg1, int arg2)
			throws SQLException {
		// not supported

	}

	public void updateCharacterStream(String arg0, Reader arg1, int arg2)
			throws SQLException {
		// not supported

	}

	public void updateCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// not supported

	}

	public void updateCharacterStream(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// not supported

	}

	public void updateClob(int arg0, Clob arg1) throws SQLException {
		// not supported

	}

	public void updateClob(String arg0, Clob arg1) throws SQLException {
		// not supported

	}

	public void updateClob(int arg0, Reader arg1) throws SQLException {
		// not supported

	}

	public void updateClob(String arg0, Reader arg1) throws SQLException {
		// not supported

	}

	public void updateClob(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// not supported

	}

	public void updateClob(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// not supported

	}

	public void updateDate(int arg0, Date arg1) throws SQLException {
		// not supported

	}

	public void updateDate(String arg0, Date arg1) throws SQLException {
		// not supported

	}

	public void updateDouble(int arg0, double arg1) throws SQLException {
		// not supported

	}

	public void updateDouble(String arg0, double arg1) throws SQLException {
		// not supported

	}

	public void updateFloat(int arg0, float arg1) throws SQLException {
		// not supported

	}

	public void updateFloat(String arg0, float arg1) throws SQLException {
		// not supported

	}

	public void updateInt(int arg0, int arg1) throws SQLException {
		// not supported

	}

	public void updateInt(String arg0, int arg1) throws SQLException {
		// not supported

	}

	public void updateLong(int arg0, long arg1) throws SQLException {
		// not supported

	}

	public void updateLong(String arg0, long arg1) throws SQLException {
		// not supported

	}

	public void updateNCharacterStream(int arg0, Reader arg1)
			throws SQLException {
		// not supported

	}

	public void updateNCharacterStream(String arg0, Reader arg1)
			throws SQLException {
		// not supported

	}

	public void updateNCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// not supported

	}

	public void updateNCharacterStream(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// not supported

	}

	public void updateNClob(int arg0, Reader arg1) throws SQLException {
		// not supported

	}

	public void updateNClob(String arg0, Reader arg1) throws SQLException {
		// not supported

	}

	public void updateNClob(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// not supported

	}

	public void updateNClob(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// not supported

	}

	public void updateNString(int arg0, String arg1) throws SQLException {
		// not supported

	}

	public void updateNString(String arg0, String arg1) throws SQLException {
		// not supported

	}

	public void updateNull(int arg0) throws SQLException {
		// not supported

	}

	public void updateNull(String arg0) throws SQLException {
		// not supported

	}

	public void updateObject(int arg0, Object arg1) throws SQLException {
		// not supported

	}

	public void updateObject(String arg0, Object arg1) throws SQLException {
		// not supported

	}

	public void updateObject(int arg0, Object arg1, int arg2)
			throws SQLException {
		// not supported

	}

	public void updateObject(String arg0, Object arg1, int arg2)
			throws SQLException {
		// not supported

	}

	public void updateRef(int arg0, Ref arg1) throws SQLException {
		// not supported

	}

	public void updateRef(String arg0, Ref arg1) throws SQLException {
		// not supported

	}

	public void updateRow() throws SQLException {
		// not supported

	}

	public void updateShort(int arg0, short arg1) throws SQLException {
		// not supported
	}

	public void updateShort(String arg0, short arg1) throws SQLException {
		// not supported

	}

	public void updateString(int arg0, String arg1) throws SQLException {
		// not supported

	}

	public void updateString(String arg0, String arg1) throws SQLException {
		// not supported

	}

	public void updateTime(int arg0, Time arg1) throws SQLException {
		// not supported

	}

	public void updateTime(String arg0, Time arg1) throws SQLException {
		// not supported

	}

	public void updateTimestamp(int arg0, Timestamp arg1) throws SQLException {
		// not supported

	}

	public void updateTimestamp(String arg0, Timestamp arg1)
			throws SQLException {
		// not supported

	}

	public boolean wasNull() throws SQLException {
		// not supported
		return false;
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
