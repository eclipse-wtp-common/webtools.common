/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;


import java.io.UnsupportedEncodingException;

public class PasswordUtil {
	public static final String STRING_CONVERSION_CODE = "UTF8"; //$NON-NLS-1$
	public static final String EMPTY_STRING = ""; //$NON-NLS-1$
	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	public static final String DEFAULT_CRYPTO_ALGORITHM;
	private static final String SUPPORTED_CRYPTO_ALGORITHMS[];
	private static final byte BASE64_ENCODE_MAP[];
	private static final byte BASE64_DECODE_MAP[];

	public PasswordUtil() {
	}

	public static String decode(String s) throws InvalidPasswordDecodingException, UnsupportedCryptoAlgorithmException {
		if (s == null)
			throw new InvalidPasswordDecodingException();
		String s1 = getCryptoAlgorithm(s);
		if (s1 == null)
			throw new InvalidPasswordDecodingException();

		if (!isValidCryptoAlgorithm(s1))
			throw new UnsupportedCryptoAlgorithmException();

		String s2 = decode_password(removeCryptoAlgorithmTag(s), s1);
		if (s2 == null)
			throw new InvalidPasswordDecodingException();
		return s2;
	}

	public static String encode(String s) throws InvalidPasswordEncodingException, UnsupportedCryptoAlgorithmException {
		return encode(s, DEFAULT_CRYPTO_ALGORITHM);
	}

	public static String encode(String s, String s1) throws InvalidPasswordEncodingException, UnsupportedCryptoAlgorithmException {
		if (!isValidCryptoAlgorithm(s1))
			throw new UnsupportedCryptoAlgorithmException();
		if (s == null)
			throw new InvalidPasswordEncodingException();

		if (getCryptoAlgorithm(s) != null)
			throw new InvalidPasswordEncodingException();

		String s2 = encode_password(s.trim(), s1.trim());
		if (s2 == null)
			throw new InvalidPasswordEncodingException();
		return s2;
	}

	public static String getCryptoAlgorithm(String s) {
		String s1 = null;
		String innerS = s;
		if (innerS != null) {
			innerS = innerS.trim();
			if (innerS.length() >= 2) {
				int i = innerS.indexOf("{"); //$NON-NLS-1$
				if (i == 0) {
					int j = innerS.indexOf("}", ++i); //$NON-NLS-1$
					if (j > 0)
						if (i < j)
							s1 = innerS.substring(i, j).trim();
						else
							s1 = EMPTY_STRING;
				}
			}
		}
		return s1;
	}

	public static String getCryptoAlgorithmTag(String s) {
		String s1 = null;
		String s2 = getCryptoAlgorithm(s);
		if (s2 != null) {
			StringBuffer stringbuffer = new StringBuffer("{"); //$NON-NLS-1$
			if (s2.length() > 0)
				stringbuffer.append(s2);
			stringbuffer.append("}"); //$NON-NLS-1$
			s1 = stringbuffer.toString();
		}
		return s1;
	}

	public static boolean isValidCryptoAlgorithm(String s) {
		String innerS = s;
		if (innerS != null) {
			innerS = innerS.trim();
			if (innerS.length() == 0)
				return true;
			for (int i = 0; i < SUPPORTED_CRYPTO_ALGORITHMS.length; i++)
				if (innerS.equalsIgnoreCase(SUPPORTED_CRYPTO_ALGORITHMS[i]))
					return true;
		}
		return false;
	}

	public static boolean isValidCryptoAlgorithmTag(String s) {
		return isValidCryptoAlgorithm(getCryptoAlgorithm(s));
	}

	public static String passwordDecode(String s) {
		if (s == null)
			return null;
		String s1 = getCryptoAlgorithm(s);
		if (s1 == null)
			return s;
		if (!isValidCryptoAlgorithm(s1))
			return null;
		return decode_password(removeCryptoAlgorithmTag(s), s1);
	}

	public static String passwordEncode(String s) {
		return passwordEncode(s, DEFAULT_CRYPTO_ALGORITHM);
	}

	public static String passwordEncode(String s, String s1) {
		if (!isValidCryptoAlgorithm(s1))
			return null;
		if (s == null)
			return null;
		String s2 = getCryptoAlgorithm(s);
		if (s2 != null) {
			if (s2.equalsIgnoreCase(s1.trim()))
				return s.trim();
			return null;
		}
		return encode_password(s.trim(), s1.trim());
	}

	public static String removeCryptoAlgorithmTag(String s) {
		String s1 = null;
		String innerS = s;
		if (innerS != null) {
			innerS = innerS.trim();
			if (innerS.length() >= 2) {
				int i = innerS.indexOf("{"); //$NON-NLS-1$
				if (i == 0) {
					int j = innerS.indexOf("}", ++i); //$NON-NLS-1$
					if (j > 0)
						if (++j < innerS.length())
							s1 = innerS.substring(j).trim();
						else
							s1 = EMPTY_STRING;
				}
			}
		}
		return s1;
	}

	private static byte[] convert_to_bytes(String s) {
		byte abyte0[] = null;
		if (s != null)
			if (s.length() == 0)
				abyte0 = EMPTY_BYTE_ARRAY;
			else
				try {
					abyte0 = s.getBytes(STRING_CONVERSION_CODE);
				} catch (UnsupportedEncodingException unsupportedencodingexception) {
					//do nothing
				}
		return abyte0;
	}

	private static String convert_to_string(byte abyte0[]) {
		String s = null;
		if (abyte0 != null)
			if (abyte0.length == 0)
				s = EMPTY_STRING;
			else
				try {
					s = new String(abyte0, STRING_CONVERSION_CODE);
				} catch (UnsupportedEncodingException unsupportedencodingexception) {
					//do nothing
				}
		return s;
	}

	private static byte[] convert_viewable_to_bytes(String s) {
		byte abyte0[] = null;
		if (s != null)
			if (s.length() == 0)
				abyte0 = EMPTY_BYTE_ARRAY;
			else
				try {
					abyte0 = base64Decode(convert_to_bytes(s));
				} catch (Exception exception) {
					abyte0 = null;
				}
		return abyte0;
	}

	private static byte[] base64Decode(byte abyte0[]) {
		int i;
		for (i = abyte0.length; abyte0[--i] == 61;){
			//do nothing just finding index of 61
		}
		byte abyte1[] = new byte[(i + 1) - abyte0.length / 4];
		for (int j = 0; j < abyte0.length; j++)
			abyte0[j] = BASE64_DECODE_MAP[abyte0[j]];
		int k = abyte1.length - 2;
		int l = 0;
		int i1;
		for (i1 = 0; l < k; i1 += 4) {
			abyte1[l] = (byte) (abyte0[i1] << 2 & 0xff | abyte0[i1 + 1] >>> 4 & 0x3);
			abyte1[l + 1] = (byte) (abyte0[i1 + 1] << 4 & 0xff | abyte0[i1 + 2] >>> 2 & 0xf);
			abyte1[l + 2] = (byte) (abyte0[i1 + 2] << 6 & 0xff | abyte0[i1 + 3] & 0x3f);
			l += 3;
		}
		if (l < abyte1.length) {
			abyte1[l++] = (byte) (abyte0[i1] << 2 & 0xff | abyte0[i1 + 1] >>> 4 & 0x3);
			if (l < abyte1.length)
				abyte1[l] = (byte) (abyte0[i1 + 1] << 4 & 0xff | abyte0[i1 + 2] >>> 2 & 0xf);
		}
		return abyte1;
	}

	private static String convert_viewable_to_string(byte abyte0[]) {
		String s = null;
		if (abyte0 != null)
			if (abyte0.length == 0)
				s = EMPTY_STRING;
			else
				try {
					s = convert_to_string(base64Encode(abyte0));
				} catch (Exception exception) {
					s = null;
				}
		return s;
	}

	private static byte[] base64Encode(byte abyte0[]) {
		byte abyte1[] = new byte[((abyte0.length + 2) / 3) * 4];
		int i = 0;
		int j = 0;
		for (; i < abyte0.length - 2; i += 3) {
			abyte1[j++] = BASE64_ENCODE_MAP[abyte0[i] >>> 2 & 0x3f];
			abyte1[j++] = BASE64_ENCODE_MAP[abyte0[i + 1] >>> 4 & 0xf | abyte0[i] << 4 & 0x3f];
			abyte1[j++] = BASE64_ENCODE_MAP[abyte0[i + 2] >>> 6 & 0x3 | abyte0[i + 1] << 2 & 0x3f];
			abyte1[j++] = BASE64_ENCODE_MAP[abyte0[i + 2] & 0x3f];
		}
		if (i < abyte0.length) {
			abyte1[j++] = BASE64_ENCODE_MAP[abyte0[i] >>> 2 & 0x3f];
			if (i < abyte0.length - 1) {
				abyte1[j++] = BASE64_ENCODE_MAP[abyte0[i + 1] >>> 4 & 0xf | abyte0[i] << 4 & 0x3f];
				abyte1[j++] = BASE64_ENCODE_MAP[abyte0[i + 1] << 2 & 0x3f];
			} else {
				abyte1[j++] = BASE64_ENCODE_MAP[abyte0[i] << 4 & 0x3f];
			}
		}
		for (; j < abyte1.length; j++)
			abyte1[j] = 61;
		return abyte1;
	}

	private static String decode_password(String s, String s1) {
		StringBuffer stringbuffer = new StringBuffer();
		if (s1.length() == 0) {
			stringbuffer.append(s);
		} else {
			String s2 = null;
			if (s.length() > 0) {
				byte abyte0[] = convert_viewable_to_bytes(s);
				if (abyte0 == null)
					return null;
				if (abyte0.length > 0) {
					byte abyte1[] = null;
					try {
						abyte1 = PasswordCipherUtil.decipher(abyte0, s1);
					} catch (InvalidPasswordCipherException invalidpasswordcipherexception) {
						return null;
					} catch (UnsupportedCryptoAlgorithmException unsupportedcryptoalgorithmexception) {
						return null;
					}
					if (abyte1 != null && abyte1.length > 0)
						s2 = convert_to_string(abyte1);
				}
			}
			if (s2 != null && s2.length() > 0)
				stringbuffer.append(s2);
		}
		return stringbuffer.toString();
	}

	private static String encode_password(String s, String s1) {
		StringBuffer stringbuffer = new StringBuffer("{"); //$NON-NLS-1$
		if (s1.length() == 0) {
			stringbuffer.append("}").append(s); //$NON-NLS-1$
		} else {
			stringbuffer.append(s1).append("}"); //$NON-NLS-1$
			String s2 = null;
			if (s.length() > 0) {
				byte abyte0[] = convert_to_bytes(s);
				if (abyte0.length > 0) {
					byte abyte1[] = null;
					try {
						abyte1 = PasswordCipherUtil.encipher(abyte0, s1);
					} catch (InvalidPasswordCipherException invalidpasswordcipherexception) {
						return null;
					} catch (UnsupportedCryptoAlgorithmException unsupportedcryptoalgorithmexception) {
						return null;
					}
					if (abyte1 != null && abyte1.length > 0) {
						s2 = convert_viewable_to_string(abyte1);
						if (s2 == null)
							return null;
					}
				}
			}
			if (s2 != null && s2.length() > 0)
				stringbuffer.append(s2);
		}
		return stringbuffer.toString();
	}

	static {
		SUPPORTED_CRYPTO_ALGORITHMS = PasswordCipherUtil.getSupportedCryptoAlgorithms();
		DEFAULT_CRYPTO_ALGORITHM = SUPPORTED_CRYPTO_ALGORITHMS[0];
		byte abyte0[] = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
		BASE64_ENCODE_MAP = abyte0;
		BASE64_DECODE_MAP = new byte[128];
		for (int i = 0; i < BASE64_DECODE_MAP.length; i++)
			BASE64_DECODE_MAP[i] = -1;
		for (int j = 0; j < BASE64_ENCODE_MAP.length; j++)
			BASE64_DECODE_MAP[BASE64_ENCODE_MAP[j]] = (byte) j;
	}
}