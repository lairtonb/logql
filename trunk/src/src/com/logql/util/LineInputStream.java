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

    $Id: LineInputStream.java,v 1.2 2009/10/29 05:11:08 mreddy Exp $
*/
package com.logql.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class LineInputStream {
	public static final int _BuffSize = 8192;

	byte[] buff = new byte[_BuffSize];
	ExpandableByteBuffer ebuff = new ExpandableByteBuffer();
	int startPos = 0, endPos = 0, buffSize = 0;
	InputStream fin;
	boolean gotCR, finished;

	public LineInputStream(File f) throws FileNotFoundException {
		fin = new FileInputStream(f);
	}

	public LineInputStream(InputStream in) {
		fin = in;
	}

	public byte[] readLine(Marker mark) throws IOException {
		if (isBufferEmpty()) {
			if (finished)
				return null;
			else if (!refill())
				return null;
		}
		if (endPos >= 0 && endPos < buffSize)
			startPos = buff[endPos] == '\n' ? endPos + 1 : endPos;

		while (++endPos < buffSize && buff[endPos] != '\n');

		if ((endPos < buffSize && buff[endPos] == '\n') || finished) {
			// ideal case, full line is in current buffer
			mark.lineStartPos = startPos;
			mark.startPos = startPos;
			mark.endPos = buff[endPos - 1] == '\r' ? endPos - 1 : endPos;
			mark.lineEndPos = mark.endPos;
			return buff;
		} else {
			// oops cross over
			ebuff.append(buff, startPos, endPos);
			while (!finished && (endPos >= buffSize || buff[endPos] != '\n')) {
				if (refill()) {
					endPos--;
					while (++endPos < buffSize && buff[endPos] != '\n');
					ebuff.append(buff, startPos, endPos);
				}
			}
			mark.lineStartPos = 0;
			mark.startPos = 0;
			mark.endPos = ebuff.buff[ebuff.pos - 1] == '\r' ? ebuff.pos - 1 : ebuff.pos;
			mark.lineEndPos = mark.endPos;
			ebuff.reset();
			return ebuff.buff;
		}
	}

	public void close() throws IOException {
		fin.close();
	}

	private boolean refill() throws IOException {
		startPos = endPos = 0;
		buffSize = fin.read(buff);
		finished = buffSize <= 0;
		return !finished;
	}

	private boolean isBufferEmpty() {
		return endPos + 1 >= buffSize;
	}

	public class ExpandableByteBuffer {
		byte[] buff;
		int pos;

		public ExpandableByteBuffer() {
			buff = new byte[_BuffSize * 2];
		}

		public void append(byte[] b1, int start, int end) {
			int len = end - start;
			if (pos + len > buff.length) {
				int nSiz = buff.length << 1;
				if (nSiz < pos + len)
					nSiz = pos + len + _BuffSize;
				byte[] nbuff = new byte[nSiz];
				System.arraycopy(buff, 0, nbuff, 0, pos);
				buff = nbuff;
			}
			System.arraycopy(b1, start, buff, pos, len);
			pos += len;
		}

		public void reset() {
			pos = 0;
		}
	}
}
