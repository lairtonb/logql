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

    $Id: InputStreamWrapper.java,v 1.2 2009/10/29 05:11:08 mreddy Exp $
*/
package com.logql.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class InputStreamWrapper {
	protected File file;
	protected ZipFile zfile; 
	protected ZipEntry zent;

	public InputStreamWrapper(File f) {
		file = f;
	}

	public InputStreamWrapper(ZipFile zf, ZipEntry ze) {
		zfile = zf;
		zent = ze;
	}

	public InputStream getInputStream() throws IOException {
		if (file != null) {
			if (file.getName().toLowerCase().endsWith(".gz")) {
				return new GZIPInputStream(new FileInputStream(file), 8192);
			} else {
				return new FileInputStream(file);
			}
		} else {
			return zfile.getInputStream(zent);
		}
	}

	public boolean validate() {
		if (file != null && !file.exists())
			return false;
		return true;
	}

	public String toString() {
		if (file != null) {
			return file.toString();
		} else {
			return zent.toString();
		}
	}
}
