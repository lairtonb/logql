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

    $Id: SelectFunction.java,v 1.2 2009/10/29 05:11:15 mreddy Exp $
*/
package com.logql.interpret.func;

import com.logql.interpret.SelectMeta;
import com.logql.meta.FieldMeta;
import com.logql.meta.FlexiRow;
import com.logql.meta.LogMeta;

public abstract class SelectFunction {
	protected int dstColId;
	protected String alias;
	private int colPos; //from the select query
	protected FieldMeta field;
	protected SelectMeta smeta;
	protected int dstColPos2 = -1;

	public boolean copyToTmp(FlexiRow src, FlexiRow dst) {
		//called first for groupby functions
		//this is to copy from src to tmp then search
		return true;
	}

	public void copyToDst(FlexiRow tmp, FlexiRow dst) {
		//called second for groupby functions
		//this is to copy from tmp to dst row
	}

	public void compute(FlexiRow src, FlexiRow dst) {
		//called on metric function to copy from src to dst
	}

	public void postProcess(FlexiRow prev, FlexiRow curr){
		
	}
	public abstract int getStorageType();
	public abstract boolean requiresPostProcess();
	public abstract boolean isMetricField();

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getDstColId() {
		return dstColId;
	}

	public void setDstColId(int dstColId) {
		this.dstColId = dstColId;
	}

	public void setSelectMeta(SelectMeta sm){
		smeta = sm;
	}

	public int getSrcColumnId() {
		return field.getId();
	}

	public int getColPos() {
		return colPos;
	}

	public void setColPos(int colPos) {
		this.colPos = colPos;
	}

	public void setField(FieldMeta fmeta){
		field=fmeta;
	}
	
	protected int srcColPos;
	protected int dstColPos;
	
	public void init(int[] srcMap, int[] dstMap){
		if (getSrcColumnId() != -1) {
			srcColPos = srcMap[getSrcColumnId()];
		}
		//dstMap will be null when a where function calls it
		if(dstMap != null)
			dstColPos=dstMap[dstColId];
	}

	// implementation for where functions
	public void init(int[] srcMap) {
		init(srcMap, null);
	}

	public FieldMeta getRequiredField(){
		return field;
	}

	public String toString() {
		return alias + " " + getClass().getName() + "-" + colPos + "-"
				+ getSrcColumnId() + "-" + dstColId;
	}

	public int getDstColPos(){
		return dstColPos;
	}

	public int getDstColPos2(){
		return dstColPos2;
	}

	public void setDstColPos2(int pos) {
		dstColPos2 = pos;
	}

	public int getSecondaryStroageType() {
		return -1;
	}

	public void processFunctionArgs(LogMeta lm, String args) {

	}
}
