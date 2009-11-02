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

    $Id: NumberParser.java,v 1.2 2009/10/29 05:11:08 mreddy Exp $
*/
package com.logql.util;

public class NumberParser {
	private boolean isNegative;
	private int decExponent;
	private byte digits[] = new byte[100];
	private double nan;
	private int nDigits;
	private int bigIntExp;
	private int bigIntNBits;
	private boolean mustSetRoundDir = false;
	private int roundDir;

	public NumberParser() {
	}

	private static final long signMask = 0x8000000000000000L;
	private static final long expMask = 0x7ff0000000000000L;
	private static final long fractMask = ~(signMask | expMask);
	private static final int expShift = 52;
	private static final int expBias = 1023;
	private static final long fractHOB = (1L << expShift);
	private static final int maxDecimalDigits = 15;
	private static final int maxDecimalExponent = 308;
	private static final int minDecimalExponent = -324;
	private static final int bigDecimalExponent = 324;

	private static final long highbyte = 0xff00000000000000L;
	private static final long lowbytes = ~highbyte;

	private static final int singleMaxDecimalDigits = 7;
	private static final int singleMaxDecimalExponent = 38;
	private static final int singleMinDecimalExponent = -45;

	private static final int intDecimalDigits = 9;

	private static int countBits(long v) {
		if (v == 0L)
			return 0;

		while ((v & highbyte) == 0L) {
			v <<= 8;
		}
		while (v > 0L) {
			v <<= 1;
		}

		int n = 0;
		while ((v & lowbytes) != 0L) {
			v <<= 8;
			n += 8;
		}
		while (v != 0L) {
			v <<= 1;
			n += 1;
		}
		return n;
	}

	private static UtilInteger b5p[];

	private static synchronized UtilInteger big5pow(int p) {
		assert p >= 0 : p;
		if (b5p == null) {
			b5p = new UtilInteger[p + 1];
		} else if (b5p.length <= p) {
			UtilInteger t[] = new UtilInteger[p + 1];
			System.arraycopy(b5p, 0, t, 0, b5p.length);
			b5p = t;
		}
		if (b5p[p] != null)
			return b5p[p];
		else if (p < small5pow.length)
			return b5p[p] = new UtilInteger(small5pow[p]);
		else if (p < long5pow.length)
			return b5p[p] = new UtilInteger(long5pow[p]);
		else {
			int q, r;
			q = p >> 1;
			r = p - q;
			UtilInteger bigq = b5p[q];
			if (bigq == null)
				bigq = big5pow(q);
			if (r < small5pow.length) {
				return (b5p[p] = bigq.mult(small5pow[r]));
			} else {
				UtilInteger bigr = b5p[r];
				if (bigr == null)
					bigr = big5pow(r);
				return (b5p[p] = bigq.mult(bigr));
			}
		}
	}

	private static UtilInteger multPow52(UtilInteger v, int p5, int p2) {
		if (p5 != 0) {
			if (p5 < small5pow.length) {
				v = v.mult(small5pow[p5]);
			} else {
				v = v.mult(big5pow(p5));
			}
		}
		if (p2 != 0) {
			v.lshiftMe(p2);
		}
		return v;
	}

	private static UtilInteger constructPow52(int p5, int p2) {
		UtilInteger v = new UtilInteger(big5pow(p5));
		if (p2 != 0) {
			v.lshiftMe(p2);
		}
		return v;
	}

	private UtilInteger doubleToBigInt(double dval) {
		long lbits = Double.doubleToLongBits(dval) & ~signMask;
		int binexp = (int) (lbits >>> expShift);
		lbits &= fractMask;
		if (binexp > 0) {
			lbits |= fractHOB;
		} else {
			assert lbits != 0L : lbits;
			binexp += 1;
			while ((lbits & fractHOB) == 0L) {
				lbits <<= 1;
				binexp -= 1;
			}
		}
		binexp -= expBias;
		int nbits = countBits(lbits);
		int lowOrderZeros = expShift + 1 - nbits;
		lbits >>>= lowOrderZeros;

		bigIntExp = binexp + 1 - nbits;
		bigIntNBits = nbits;
		return new UtilInteger(lbits);
	}

	private static double ulp(double dval, boolean subtracting) {
		long lbits = Double.doubleToLongBits(dval) & ~signMask;
		int binexp = (int) (lbits >>> expShift);
		double ulpval;
		if (subtracting && (binexp >= expShift) && ((lbits & fractMask) == 0L)) {
			binexp -= 1;
		}
		if (binexp > expShift) {
			ulpval = Double
					.longBitsToDouble(((long) (binexp - expShift)) << expShift);
		} else if (binexp == 0) {
			ulpval = Double.MIN_VALUE;
		} else {
			ulpval = Double.longBitsToDouble(1L << (binexp - 1));
		}
		if (subtracting)
			ulpval = -ulpval;

		return ulpval;
	}

	private float stickyRound(double dval) {
		long lbits = Double.doubleToLongBits(dval);
		long binexp = lbits & expMask;
		if (binexp == 0L || binexp == expMask) {
			return (float) dval;
		}
		lbits += (long) roundDir;
		return (float) Double.longBitsToDouble(lbits);
	}

	public void readString(byte[] nbytes, Marker m)
			throws NumberFormatException {
		isNegative = false;
		boolean signSeen = false;
		int decExp;
		byte c;

		parseNumber: {
			int l = m.endPos;
			int len = m.endPos - m.startPos;
			if (len <= 0)
				throw new NumberFormatException("empty String");
			int i = m.startPos;
			switch (c = nbytes[i]) {
			case '-':
				isNegative = true;
				// FALLTHROUGH
			case '+':
				i++;
				signSeen = true;
			}

			c = nbytes[i];
			if (c == 'N' || c == 'I') {
				boolean potentialNaN = false;
				byte targetChars[] = null;

				if (c == 'N') {
					targetChars = notANumber;
					potentialNaN = true;
				} else {
					targetChars = infinity;
				}

				int j = 0;
				while (i < l && j < targetChars.length) {
					if (nbytes[i] == targetChars[j]) {
						i++;
						j++;
					} else
						break parseNumber;
				}

				if ((j == targetChars.length) && (i == l)) {
					if (potentialNaN) {
						nan = Double.NaN; // NaN
					} else if (isNegative) {
						nan = Double.NEGATIVE_INFINITY;
					} else {
						nan = Double.POSITIVE_INFINITY;
					}
					return;
				} else {
					break parseNumber;
				}

			}

			if (digits.length < len)
				digits = new byte[len + 1];

			int nDigits = 0;
			boolean decSeen = false;
			int decPt = 0;
			int nLeadZero = 0;
			int nTrailZero = 0;
			digitLoop: while (i < l) {
				switch (c = nbytes[i]) {
				case '0':
					if (nDigits > 0) {
						nTrailZero += 1;
					} else {
						nLeadZero += 1;
					}
					break;
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					while (nTrailZero > 0) {
						digits[nDigits++] = '0';
						nTrailZero -= 1;
					}
					digits[nDigits++] = c;
					break; // out of switch.
				case '.':
					if (decSeen) {
						throw new NumberFormatException("multiple points");
					}
					decPt = i - m.startPos;
					if (signSeen) {
						decPt -= 1;
					}
					decSeen = true;
					break;
				default:
					break digitLoop;
				}
				i++;
			}
			if (nDigits == 0) {
				digits = zero;
				nDigits = 1;
				if (nLeadZero == 0) {
					break parseNumber;
				}
			}

			if (decSeen) {
				decExp = decPt - nLeadZero;
			} else {
				decExp = nDigits + nTrailZero;
			}

			if ((i < l) && ((c = nbytes[i]) == 'e') || (c == 'E')) {
				int expSign = 1;
				int expVal = 0;
				int reallyBig = Integer.MAX_VALUE / 10;
				boolean expOverflow = false;
				switch (nbytes[++i]) {
				case '-':
					expSign = -1;
				case '+':
					i++;
				}
				int expAt = i;
				expLoop: while (i < l) {
					if (expVal >= reallyBig) {
						expOverflow = true;
					}
					switch (c = nbytes[i++]) {
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						expVal = expVal * 10 + ((int) c - (int) '0');
						continue;
					default:
						i--;
						break expLoop;
					}
				}
				int expLimit = bigDecimalExponent + nDigits + nTrailZero;
				if (expOverflow || (expVal > expLimit)) {
					decExp = expSign * expLimit;
				} else {
					decExp = decExp + expSign * expVal;
				}

				if (i == expAt)
					break parseNumber;
			}

			if (i < l
					&& ((i != l - 1) || (nbytes[i] != 'f' && nbytes[i] != 'F'
							&& nbytes[i] != 'd' && nbytes[i] != 'D'))) {
				break parseNumber;
			}

			this.decExponent = decExp;
			this.nDigits = nDigits;

			return;
		}
		throw new NumberFormatException("");
	}

	public double doubleValue() {
		int kDigits = Math.min(nDigits, maxDecimalDigits + 1);
		long lValue;
		double dValue;
		double rValue, tValue;

		roundDir = 0;

		int iValue = (int) digits[0] - (int) '0';
		int iDigits = Math.min(kDigits, intDecimalDigits);
		for (int i = 1; i < iDigits; i++) {
			iValue = iValue * 10 + (int) digits[i] - (int) '0';
		}
		lValue = (long) iValue;
		for (int i = iDigits; i < kDigits; i++) {
			lValue = lValue * 10L + (long) ((int) digits[i] - (int) '0');
		}
		dValue = (double) lValue;
		int exp = decExponent - kDigits;

		if (nDigits <= maxDecimalDigits) {
			if (exp == 0 || dValue == 0.0)
				return (isNegative) ? -dValue : dValue; // small floating
			else if (exp >= 0) {
				if (exp <= maxSmallTen) {
					rValue = dValue * small10pow[exp];
					if (mustSetRoundDir) {
						tValue = rValue / small10pow[exp];
						roundDir = (tValue == dValue) ? 0
								: (tValue < dValue) ? 1 : -1;
					}
					return (isNegative) ? -rValue : rValue;
				}
				int slop = maxDecimalDigits - kDigits;
				if (exp <= maxSmallTen + slop) {
					dValue *= small10pow[slop];
					rValue = dValue * small10pow[exp - slop];

					if (mustSetRoundDir) {
						tValue = rValue / small10pow[exp - slop];
						roundDir = (tValue == dValue) ? 0
								: (tValue < dValue) ? 1 : -1;
					}
					return (isNegative) ? -rValue : rValue;
				}
			} else {
				if (exp >= -maxSmallTen) {
					rValue = dValue / small10pow[-exp];
					tValue = rValue * small10pow[-exp];
					if (mustSetRoundDir) {
						roundDir = (tValue == dValue) ? 0
								: (tValue < dValue) ? 1 : -1;
					}
					return (isNegative) ? -rValue : rValue;
				}
			}
		}

		if (exp > 0) {
			if (decExponent > maxDecimalExponent + 1) {
				return (isNegative) ? Double.NEGATIVE_INFINITY
						: Double.POSITIVE_INFINITY;
			}
			if ((exp & 15) != 0) {
				dValue *= small10pow[exp & 15];
			}
			if ((exp >>= 4) != 0) {
				int j;
				for (j = 0; exp > 1; j++, exp >>= 1) {
					if ((exp & 1) != 0)
						dValue *= big10pow[j];
				}
				double t = dValue * big10pow[j];
				if (Double.isInfinite(t)) {
					t = dValue / 2.0;
					t *= big10pow[j];
					if (Double.isInfinite(t)) {
						return (isNegative) ? Double.NEGATIVE_INFINITY
								: Double.POSITIVE_INFINITY;
					}
					t = Double.MAX_VALUE;
				}
				dValue = t;
			}
		} else if (exp < 0) {
			exp = -exp;
			if (decExponent < minDecimalExponent - 1) {
				return (isNegative) ? -0.0 : 0.0;
			}
			if ((exp & 15) != 0) {
				dValue /= small10pow[exp & 15];
			}
			if ((exp >>= 4) != 0) {
				int j;
				for (j = 0; exp > 1; j++, exp >>= 1) {
					if ((exp & 1) != 0)
						dValue *= tiny10pow[j];
				}
				double t = dValue * tiny10pow[j];
				if (t == 0.0) {
					t = dValue * 2.0;
					t *= tiny10pow[j];
					if (t == 0.0) {
						return (isNegative) ? -0.0 : 0.0;
					}
					t = Double.MIN_VALUE;
				}
				dValue = t;
			}
		}

		UtilInteger bigD0 = new UtilInteger(lValue, digits, kDigits, nDigits);
		exp = decExponent - nDigits;

		correctionLoop: while (true) {
			UtilInteger bigB = doubleToBigInt(dValue);

			int B2, B5;
			int D2, D5;
			int Ulp2;
			if (exp >= 0) {
				B2 = B5 = 0;
				D2 = D5 = exp;
			} else {
				B2 = B5 = -exp;
				D2 = D5 = 0;
			}
			if (bigIntExp >= 0) {
				B2 += bigIntExp;
			} else {
				D2 -= bigIntExp;
			}
			Ulp2 = B2;
			int hulpbias;
			if (bigIntExp + bigIntNBits <= -expBias + 1) {
				hulpbias = bigIntExp + expBias + expShift;
			} else {
				hulpbias = expShift + 2 - bigIntNBits;
			}
			B2 += hulpbias;
			D2 += hulpbias;
			int common2 = Math.min(B2, Math.min(D2, Ulp2));
			B2 -= common2;
			D2 -= common2;
			Ulp2 -= common2;
			bigB = multPow52(bigB, B5, B2);
			UtilInteger bigD = multPow52(new UtilInteger(bigD0), D5, D2);
			UtilInteger diff;
			int cmpResult;
			boolean overvalue;
			if ((cmpResult = bigB.cmp(bigD)) > 0) {
				overvalue = true; // our candidate is too big.
				diff = bigB.sub(bigD);
				if ((bigIntNBits == 1) && (bigIntExp > -expBias)) {
					Ulp2 -= 1;
					if (Ulp2 < 0) {
						Ulp2 = 0;
						diff.lshiftMe(1);
					}
				}
			} else if (cmpResult < 0) {
				overvalue = false; // our candidate is too small.
				diff = bigD.sub(bigB);
			} else {
				break correctionLoop;
			}
			UtilInteger halfUlp = constructPow52(B5, Ulp2);
			if ((cmpResult = diff.cmp(halfUlp)) < 0) {
				roundDir = overvalue ? -1 : 1;
				break correctionLoop;
			} else if (cmpResult == 0) {
				dValue += 0.5 * ulp(dValue, overvalue);
				roundDir = overvalue ? -1 : 1;
				break correctionLoop;
			} else {
				dValue += ulp(dValue, overvalue);
				if (dValue == 0.0 || dValue == Double.POSITIVE_INFINITY)
					break correctionLoop; // oops. Fell off end of range.
				continue;
			}
		}
		return (isNegative) ? -dValue : dValue;
	}

	public float floatValue() {
		int kDigits = Math.min(nDigits, singleMaxDecimalDigits + 1);
		int iValue;
		float fValue;
		iValue = (int) digits[0] - (int) '0';
		for (int i = 1; i < kDigits; i++) {
			iValue = iValue * 10 + (int) digits[i] - (int) '0';
		}
		fValue = (float) iValue;
		int exp = decExponent - kDigits;
		if (nDigits <= singleMaxDecimalDigits) {
			if (exp == 0 || fValue == 0.0f)
				return (isNegative) ? -fValue : fValue; // small floating
			else if (exp >= 0) {
				if (exp <= singleMaxSmallTen) {
					fValue *= singleSmall10pow[exp];
					return (isNegative) ? -fValue : fValue;
				}
				int slop = singleMaxDecimalDigits - kDigits;
				if (exp <= singleMaxSmallTen + slop) {
					fValue *= singleSmall10pow[slop];
					fValue *= singleSmall10pow[exp - slop];
					return (isNegative) ? -fValue : fValue;
				}
			} else {
				if (exp >= -singleMaxSmallTen) {
					fValue /= singleSmall10pow[-exp];
					return (isNegative) ? -fValue : fValue;
				}
			}
		} else if ((decExponent >= nDigits)
				&& (nDigits + decExponent <= maxDecimalDigits)) {
			long lValue = (long) iValue;
			for (int i = kDigits; i < nDigits; i++) {
				lValue = lValue * 10L + (long) ((int) digits[i] - (int) '0');
			}
			double dValue = (double) lValue;
			exp = decExponent - nDigits;
			dValue *= small10pow[exp];
			fValue = (float) dValue;
			return (isNegative) ? -fValue : fValue;

		}
		if (decExponent > singleMaxDecimalExponent + 1) {
			/*
			 * Lets face it. This is going to be Infinity. Cut to the chase.
			 */
			return (isNegative) ? Float.NEGATIVE_INFINITY
					: Float.POSITIVE_INFINITY;
		} else if (decExponent < singleMinDecimalExponent - 1) {
			return (isNegative) ? -0.0f : 0.0f;
		}

		mustSetRoundDir = true;
		double dValue = doubleValue();
		return stickyRound(dValue);
	}

	public double readDouble(byte[] arr, Marker mark) {
		nan = 0;
		readString(arr, mark);
		if (nan != 0)
			return nan;
		return doubleValue();
	}

	public float readFloat(byte[] arr, Marker mark) {
		nan = 0;
		readString(arr, mark);
		if (nan != 0) {
			if (nan == Double.NaN)
				return Float.NaN;
			else if (nan == Double.POSITIVE_INFINITY)
				return Float.POSITIVE_INFINITY;
			else if (nan == Double.NEGATIVE_INFINITY)
				return Float.NEGATIVE_INFINITY;
		}
		return floatValue();
	}

	private static final double small10pow[] = { 1.0e0, 1.0e1, 1.0e2, 1.0e3,
			1.0e4, 1.0e5, 1.0e6, 1.0e7, 1.0e8, 1.0e9, 1.0e10, 1.0e11, 1.0e12,
			1.0e13, 1.0e14, 1.0e15, 1.0e16, 1.0e17, 1.0e18, 1.0e19, 1.0e20,
			1.0e21, 1.0e22 };

	private static final float singleSmall10pow[] = { 1.0e0f, 1.0e1f, 1.0e2f,
			1.0e3f, 1.0e4f, 1.0e5f, 1.0e6f, 1.0e7f, 1.0e8f, 1.0e9f, 1.0e10f };

	private static final double big10pow[] = { 1e16, 1e32, 1e64, 1e128, 1e256 };
	private static final double tiny10pow[] = { 1e-16, 1e-32, 1e-64, 1e-128,
			1e-256 };

	private static final int maxSmallTen = small10pow.length - 1;
	private static final int singleMaxSmallTen = singleSmall10pow.length - 1;

	private static final int small5pow[] = { 1, 5, 5 * 5, 5 * 5 * 5,
			5 * 5 * 5 * 5, 5 * 5 * 5 * 5 * 5, 5 * 5 * 5 * 5 * 5 * 5,
			5 * 5 * 5 * 5 * 5 * 5 * 5, 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 };

	private static final long long5pow[] = {
			1L,
			5L,
			5L * 5,
			5L * 5 * 5,
			5L * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5
					* 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5
					* 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5
					* 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5
					* 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5
					* 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5
					* 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5
					* 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5
					* 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5,
			5L * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5
					* 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5 * 5, };

	private static final byte infinity[] = { 'I', 'n', 'f', 'i', 'n', 'i', 't',
			'y' };
	private static final byte notANumber[] = { 'N', 'a', 'N' };
	private static final byte zero[] = { '0', '0', '0', '0', '0', '0', '0', '0' };

}

class UtilInteger {
	int nWords;
	int data[];

	public UtilInteger(int v) {
		nWords = 1;
		data = new int[1];
		data[0] = v;
	}

	public UtilInteger(long v) {
		data = new int[2];
		data[0] = (int) v;
		data[1] = (int) (v >>> 32);
		nWords = (data[1] == 0) ? 1 : 2;
	}

	public UtilInteger(UtilInteger other) {
		data = new int[nWords = other.nWords];
		System.arraycopy(other.data, 0, data, 0, nWords);
	}

	private UtilInteger(int[] d, int n) {
		data = d;
		nWords = n;
	}

	public UtilInteger(long seed, byte digit[], int nd0, int nd) {
		int n = (nd + 8) / 9;
		if (n < 2)
			n = 2;
		data = new int[n];
		data[0] = (int) seed;
		data[1] = (int) (seed >>> 32);
		nWords = (data[1] == 0) ? 1 : 2;
		int i = nd0;
		int limit = nd - 5;
		int v;
		while (i < limit) {
			int ilim = i + 5;
			v = (int) digit[i++] - (int) '0';
			while (i < ilim) {
				v = 10 * v + (int) digit[i++] - (int) '0';
			}
			multaddMe(100000, v);
		}
		int factor = 1;
		v = 0;
		while (i < nd) {
			v = 10 * v + (int) digit[i++] - (int) '0';
			factor *= 10;
		}
		if (factor != 1) {
			multaddMe(factor, v);
		}
	}

	public void lshiftMe(int c) throws IllegalArgumentException {
		if (c <= 0) {
			if (c == 0)
				return; // silly.
			else
				throw new IllegalArgumentException("negative shift count");
		}
		int wordcount = c >> 5;
		int bitcount = c & 0x1f;
		int anticount = 32 - bitcount;
		int t[] = data;
		int s[] = data;
		if (nWords + wordcount + 1 > t.length) {
			// reallocate.
			t = new int[nWords + wordcount + 1];
		}
		int target = nWords + wordcount;
		int src = nWords - 1;
		if (bitcount == 0) {
			// special hack, since an anticount of 32 won't go!
			System.arraycopy(s, 0, t, wordcount, nWords);
			target = wordcount - 1;
		} else {
			t[target--] = s[src] >>> anticount;
			while (src >= 1) {
				t[target--] = (s[src] << bitcount) | (s[--src] >>> anticount);
			}
			t[target--] = s[src] << bitcount;
		}
		while (target >= 0) {
			t[target--] = 0;
		}
		data = t;
		nWords += wordcount + 1;
		while (nWords > 1 && data[nWords - 1] == 0)
			nWords--;
	}

	public int normalizeMe() throws IllegalArgumentException {
		int src;
		int wordcount = 0;
		int bitcount = 0;
		int v = 0;
		for (src = nWords - 1; src >= 0 && (v = data[src]) == 0; src--) {
			wordcount += 1;
		}
		if (src < 0) {
			throw new IllegalArgumentException("zero value");
		}
		nWords -= wordcount;
		if ((v & 0xf0000000) != 0) {
			for (bitcount = 32; (v & 0xf0000000) != 0; bitcount--)
				v >>>= 1;
		} else {
			while (v <= 0x000fffff) {
				v <<= 8;
				bitcount += 8;
			}
			while (v <= 0x07ffffff) {
				v <<= 1;
				bitcount += 1;
			}
		}
		if (bitcount != 0)
			lshiftMe(bitcount);
		return bitcount;
	}

	public UtilInteger mult(int iv) {
		long v = iv;
		int r[];
		long p;

		// guess adequate size of r.
		r = new int[(v * ((long) data[nWords - 1] & 0xffffffffL) > 0xfffffffL) ? nWords + 1
				: nWords];
		p = 0L;
		for (int i = 0; i < nWords; i++) {
			p += v * ((long) data[i] & 0xffffffffL);
			r[i] = (int) p;
			p >>>= 32;
		}
		if (p == 0L) {
			return new UtilInteger(r, nWords);
		} else {
			r[nWords] = (int) p;
			return new UtilInteger(r, nWords + 1);
		}
	}

	public void multaddMe(int iv, int addend) {
		long v = iv;
		long p;

		p = v * ((long) data[0] & 0xffffffffL) + ((long) addend & 0xffffffffL);
		data[0] = (int) p;
		p >>>= 32;
		for (int i = 1; i < nWords; i++) {
			p += v * ((long) data[i] & 0xffffffffL);
			data[i] = (int) p;
			p >>>= 32;
		}
		if (p != 0L) {
			data[nWords] = (int) p;
			nWords++;
		}
	}

	public UtilInteger mult(UtilInteger other) {
		int r[] = new int[nWords + other.nWords];
		int i;

		for (i = 0; i < this.nWords; i++) {
			long v = (long) this.data[i] & 0xffffffffL;
			long p = 0L;
			int j;
			for (j = 0; j < other.nWords; j++) {
				p += ((long) r[i + j] & 0xffffffffL) + v
						* ((long) other.data[j] & 0xffffffffL);
				r[i + j] = (int) p;
				p >>>= 32;
			}
			r[i + j] = (int) p;
		}
		for (i = r.length - 1; i > 0; i--)
			if (r[i] != 0)
				break;
		return new UtilInteger(r, i + 1);
	}

	public UtilInteger add(UtilInteger other) {
		int i;
		int a[], b[];
		int n, m;
		long c = 0L;

		if (this.nWords >= other.nWords) {
			a = this.data;
			n = this.nWords;
			b = other.data;
			m = other.nWords;
		} else {
			a = other.data;
			n = other.nWords;
			b = this.data;
			m = this.nWords;
		}
		int r[] = new int[n];
		for (i = 0; i < n; i++) {
			c += (long) a[i] & 0xffffffffL;
			if (i < m) {
				c += (long) b[i] & 0xffffffffL;
			}
			r[i] = (int) c;
			c >>= 32;
		}
		if (c != 0L) {
			int s[] = new int[r.length + 1];
			System.arraycopy(r, 0, s, 0, r.length);
			s[i++] = (int) c;
			return new UtilInteger(s, i);
		}
		return new UtilInteger(r, i);
	}

	public UtilInteger sub(UtilInteger other) {
		int r[] = new int[this.nWords];
		int i;
		int n = this.nWords;
		int m = other.nWords;
		int nzeros = 0;
		long c = 0L;
		for (i = 0; i < n; i++) {
			c += (long) this.data[i] & 0xffffffffL;
			if (i < m) {
				c -= (long) other.data[i] & 0xffffffffL;
			}
			if ((r[i] = (int) c) == 0)
				nzeros++;
			else
				nzeros = 0;
			c >>= 32;
		}
		assert c == 0L : c;
		assert dataInRangeIsZero(i, m, other);
		return new UtilInteger(r, n - nzeros);
	}

	private static boolean dataInRangeIsZero(int i, int m, UtilInteger other) {
		while (i < m)
			if (other.data[i++] != 0)
				return false;
		return true;
	}

	public int cmp(UtilInteger other) {
		int i;
		if (this.nWords > other.nWords) {
			// if any of my high-order words is non-zero,
			// then the answer is evident
			int j = other.nWords - 1;
			for (i = this.nWords - 1; i > j; i--)
				if (this.data[i] != 0)
					return 1;
		} else if (this.nWords < other.nWords) {
			// if any of other's high-order words is non-zero,
			// then the answer is evident
			int j = this.nWords - 1;
			for (i = other.nWords - 1; i > j; i--)
				if (other.data[i] != 0)
					return -1;
		} else {
			i = this.nWords - 1;
		}
		for (; i > 0; i--)
			if (this.data[i] != other.data[i])
				break;

		int a = this.data[i];
		int b = other.data[i];
		if (a < 0) {

			if (b < 0) {
				return a - b;
			} else {
				return 1;
			}
		} else {

			if (b < 0) {
				return -1;
			} else {
				return a - b;
			}
		}
	}

	public int quoRemIteration(UtilInteger S) throws IllegalArgumentException {

		if (nWords != S.nWords) {
			throw new IllegalArgumentException("disparate values");
		}

		int n = nWords - 1;
		long q = ((long) data[n] & 0xffffffffL) / (long) S.data[n];
		long diff = 0L;
		for (int i = 0; i <= n; i++) {
			diff += ((long) data[i] & 0xffffffffL) - q
					* ((long) S.data[i] & 0xffffffffL);
			data[i] = (int) diff;
			diff >>= 32;
		}
		if (diff != 0L) {
			long sum = 0L;
			while (sum == 0L) {
				sum = 0L;
				for (int i = 0; i <= n; i++) {
					sum += ((long) data[i] & 0xffffffffL)
							+ ((long) S.data[i] & 0xffffffffL);
					data[i] = (int) sum;
					sum >>= 32;
				}

				assert sum == 0 || sum == 1 : sum;
				q -= 1;
			}
		}

		long p = 0L;
		for (int i = 0; i <= n; i++) {
			p += 10 * ((long) data[i] & 0xffffffffL);
			data[i] = (int) p;
			p >>= 32;
		}
		assert p == 0L : p;
		return (int) q;
	}

	public long longValue() {
		assert this.nWords > 0 : this.nWords;

		if (this.nWords == 1)
			return ((long) data[0] & 0xffffffffL);

		assert dataInRangeIsZero(2, this.nWords, this);
		assert data[1] >= 0;
		return ((long) (data[1]) << 32) | ((long) data[0] & 0xffffffffL);
	}

}