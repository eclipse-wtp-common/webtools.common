/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;

public class PasswordCipherUtil {
	private static final String DEFAULT_SUPPORTED_CRYPTO_ALGORITHMS[] = {"xor"}; //$NON-NLS-1$
	private static String _supported_crypto_algorithms[];

	public PasswordCipherUtil() {
	}

	public static byte[] decipher(byte abyte0[], String s) throws InvalidPasswordCipherException, UnsupportedCryptoAlgorithmException {
		if (s == null)
			throw new UnsupportedCryptoAlgorithmException();
		byte abyte1[] = null;
		if (s.equalsIgnoreCase(DEFAULT_SUPPORTED_CRYPTO_ALGORITHMS[0]))
			abyte1 = xor(abyte0);
		else
			throw new UnsupportedCryptoAlgorithmException();

		if (abyte1 == null)
			throw new InvalidPasswordCipherException();

		return abyte1;
	}

	public static byte[] encipher(byte abyte0[], String s) throws InvalidPasswordCipherException, UnsupportedCryptoAlgorithmException {
		if (s == null)
			throw new UnsupportedCryptoAlgorithmException();
		byte abyte1[] = null;
		if (s.equalsIgnoreCase(DEFAULT_SUPPORTED_CRYPTO_ALGORITHMS[0]))
			abyte1 = xor(abyte0);
		else
			throw new UnsupportedCryptoAlgorithmException();

		if (abyte1 == null)
			throw new InvalidPasswordCipherException();
		return abyte1;
	}

	public static String[] getSupportedCryptoAlgorithms() {
		return _supported_crypto_algorithms;
	}

	private static byte[] xor(byte abyte0[]) {
		byte abyte1[] = null;
		if (abyte0 != null) {
			abyte1 = new byte[abyte0.length];
			for (int i = 0; i < abyte0.length; i++)
				abyte1[i] = (byte) (0x5f ^ abyte0[i]);
		}
		return abyte1;
	}

	static {
		_supported_crypto_algorithms = null;
		if (_supported_crypto_algorithms == null)
			_supported_crypto_algorithms = DEFAULT_SUPPORTED_CRYPTO_ALGORITHMS;
	}
}