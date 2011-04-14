package com.logql.meta.binary;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import com.logql.meta.FieldMeta;
import com.logql.meta.Writer;

public class BinaryWriter implements Writer{
	DataOutputStream out;
	BinaryMeta meta;
	List<FieldMeta> srcFields;
	ArrayList<BinFieldWriter> writers;

	public BinaryWriter (BinaryMeta meta, List<FieldMeta> srcFields) {
		this.meta = meta;
		this.srcFields = srcFields;
	}

	public void init(File outBinFile) throws FileNotFoundException, IOException{
		if(outBinFile.getName().endsWith(".gz")){
			out = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(outBinFile)));
		}
		else {
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outBinFile)));
		}

		writers = new ArrayList<BinFieldWriter>();
		HashMap<String, FieldMeta> fieldNames = new HashMap<String, FieldMeta>();
		for(FieldMeta meta: srcFields) {
			fieldNames.put(meta.getName(), meta);
		}
		for(BinFieldMeta bmeta : meta.getOrderedFieldMeta()){
			BinFieldWriter writer = getWriter(bmeta,fieldNames.get(bmeta.getName()));
			writer.init(bmeta, outBinFile);
			writers.add(writer);
		}
	}

	protected BinFieldWriter getWriter(BinFieldMeta meta, FieldMeta srcMeta) {
		boolean shouldDefault = srcMeta == null;
		BinFieldWriter ret = null;
		switch (meta.getActualType()){
		case FieldMeta.FIELD_STRING:{
			if(meta.getJoinFile() != null && meta.getJoinFile().length() > 0){
				if(shouldDefault) {
					ret = new BinStringJoinDefaultWriter();
				} else {
					ret = new BinStringJoinWriter();
				}
			} else {
				if(shouldDefault) {
					ret = new BinStringDefaultWriter();
				} else {
					ret = new BinStringWriter();
				}
			}
			break;
		}
		case FieldMeta.FIELD_INTEGER: ret = shouldDefault? new BinIntDefaultWriter(): new BinIntWriter(); break;
		case FieldMeta.FIELD_DATE: ret = shouldDefault? new BinIntDefaultWriter(): new BinDateWriter(); break;
		case FieldMeta.FIELD_IP: throw new IllegalArgumentException("IP type not supported");
		case FieldMeta.FIELD_LONG: {
			if(shouldDefault) {
				ret = new BinLongDefaultWriter();
			} else {
				if(srcMeta.getActualType() == FieldMeta.FIELD_INTEGER) {
					ret = new BinIntLongWriter();
				} else{
					ret = new BinLongWriter();
				}
			}
			break;
		}
		case FieldMeta.FIELD_BYTES: ret = shouldDefault? new BinLongDefaultWriter(): new BinLongWriter(); break;
		case FieldMeta.FIELD_FLOAT: ret = shouldDefault? new BinFloatDefaultWriter(): new BinFloatWriter(); break;
		case FieldMeta.FIELD_DOUBLE: ret = shouldDefault? new BinDoubleDefaultWriter(): new BinDoubleWriter(); break;
		}
		return ret;
	}

	public void write(ResultSet rs) throws IOException, SQLException {
		while(rs.next()){
			for(BinFieldWriter writer:writers) {
				writer.write(rs, out);
			}
		}
		out.close();
		HashSet<File> mapFiles = new HashSet<File>();
		for(BinFieldWriter writer:writers) {
			//TODO:this is ugly, come up with a cleaner implementation later
			if(writer instanceof BinStringJoinWriter){
				BinStringJoinWriter sout = (BinStringJoinWriter)writer;
				if (sout.shouldSaveMap && !mapFiles.contains(sout.propsFile)) {
					JoinFieldsPool.saveJoinMap(sout.propsFile);
					mapFiles.add(sout.propsFile);
				}
				JoinFieldsPool.releasekeyIdMap(sout.propsFile);
			}
			writer.postProcess();
		}
	}
 

	//////////////////////////////////////////////////////////////////////////
	//////////////  Binary Writers
	//////////////////////////////////////////////////////////////////////////
	abstract class BinFieldWriter {
		protected BinFieldMeta meta;
		protected String fieldName; 

		public void init(BinFieldMeta meta, File srcFile) throws IOException {
			this.meta = meta;
			fieldName = meta.getName();
		}

		public abstract void write(ResultSet rs, DataOutputStream out)throws IOException, SQLException;
		
		public void postProcess() throws IOException{}
	}
	
	class BinIntWriter extends BinFieldWriter {
		@Override
		public void write(ResultSet rs, DataOutputStream out) throws IOException, SQLException {
			out.writeInt(rs.getInt(fieldName));
		}
	}

	class BinIntDefaultWriter extends BinFieldWriter {
		@Override
		public void write(ResultSet rs, DataOutputStream out) throws IOException, SQLException {
			out.writeInt(0);
		}
	}

	class BinIntLongWriter extends BinFieldWriter {
		@Override
		public void write(ResultSet rs, DataOutputStream out)
				throws IOException, SQLException {
			out.writeLong(rs.getInt(fieldName));
		}
	}
	
	class BinLongWriter extends BinFieldWriter {
		@Override
		public void write(ResultSet rs, DataOutputStream out)
				throws IOException, SQLException {
			out.writeLong(rs.getLong(fieldName));
		}
	}

	class BinLongDefaultWriter extends BinFieldWriter {
		@Override
		public void write(ResultSet rs, DataOutputStream out)
				throws IOException, SQLException {
			out.writeLong(0l);
		}
	}

	class BinFloatWriter extends BinFieldWriter {
		@Override
		public void write(ResultSet rs, DataOutputStream out)
				throws IOException, SQLException {
			out.writeFloat(rs.getFloat(fieldName));
		}
	}

	class BinFloatDefaultWriter extends BinFieldWriter {
		@Override
		public void write(ResultSet rs, DataOutputStream out)
				throws IOException, SQLException {
			out.writeFloat(0f);
		}
	}

	class BinDoubleWriter extends BinFieldWriter {
		@Override
		public void write(ResultSet rs, DataOutputStream out)
				throws IOException, SQLException {
			out.writeDouble(rs.getDouble(fieldName));
		}
	}

	class BinDoubleDefaultWriter extends BinFieldWriter {
		@Override
		public void write(ResultSet rs, DataOutputStream out)
				throws IOException, SQLException {
			out.writeDouble(0l);
		}
	}

	class BinDateWriter extends BinFieldWriter {
		@Override
		public void write(ResultSet rs, DataOutputStream out)
				throws IOException, SQLException {
			out.writeInt((int)(rs.getDate(fieldName).getTime()/1000));
		}
	}

	class BinStringWriter extends BinFieldWriter {
		@Override
		public void write(ResultSet rs, DataOutputStream out)
				throws IOException, SQLException {
			out.writeChars(rs.getString(fieldName));
		}
	}

	class BinStringDefaultWriter extends BinFieldWriter {
		@Override
		public void write(ResultSet rs, DataOutputStream out)
				throws IOException, SQLException {
			out.writeChars(" ");
		}
	}

	class BinStringJoinWriter extends BinFieldWriter {
		boolean shouldSaveMap;
		Map<String, Integer> map;
		int max;
		File propsFile;

		public void init(BinFieldMeta meta, File srcFile) throws IOException {
			super.init(meta, srcFile);
			propsFile = JoinFieldsPool.getJoinFile(srcFile, meta);
			map = JoinFieldsPool.getKeyIdMap(propsFile);
			if(map.size() == 0) {
				shouldSaveMap = true;
				map.put("", 0);
				max = 0;
			}
			else {
				max = Collections.max(map.values());
			}
		}

		@Override
		public void write(ResultSet rs, DataOutputStream out)
				throws IOException, SQLException {
			String key = rs.getString(meta.getName());
			Integer val = map.get(key);
			if(val == null){
				shouldSaveMap = true;
				val = ++max;
				map.put(key, val);
			}
			out.writeInt(val);
		}

		public void postProcess() throws IOException{
//			if (shouldSaveMap) {
//				JoinFieldsPool.saveJoinMap(propsFile);
//			}
//			JoinFieldsPool.releasekeyIdMap(propsFile);
		}
	}

	class BinStringJoinDefaultWriter extends BinStringJoinWriter {
		int val;

		public void init(BinFieldMeta meta, File srcFile) throws IOException {
			super.init(meta, srcFile);
			val = map.get(" ");
		}

		@Override
		public void write(ResultSet rs, DataOutputStream out)
				throws IOException, SQLException {
			out.writeInt(val);
		}

		public void postProcess() throws IOException{
			super.postProcess();
		}
	}
}
