/*******************************************************************************
 * Copyright (c) 2012 - 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 * yyyymmdd bug      Email and other contact information
 * -------- -------- -----------------------------------------------------------
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.uriresolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @since 1.2
 */
public class URIHelper {

	private static final int DEFAULT_TIMEOUT = 2500;

	private URIHelper() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Attempts to obtain an {@link InputStream} from the provided url. If one
	 * cannot be opened before <code>timeout</code>, the result is null.
	 * 
	 * @param url
	 *            the url to get an InputStream from
	 * @param timeout
	 *            the amount of time to try to get an InputStream, a value of
	 *            zero or lower will cause a default value to be used
	 * @return an {@link InputStream} from the <code>url</code> if it can be
	 *         obtained in less time than <code>timeout</code>, otherwise
	 *         <code>null</code>
	 * 
	 */
	public static InputStream getInputStream(String url, int timeout) {
		final InputStream[] result = new InputStream[1];
		try {
			final URLConnection connection = new URL(url).openConnection();
			final Thread streamOpener = new Thread() {
				public void run() {
					try {
						result[0] = connection.getInputStream();
					}
					catch (IOException e) {
					}
					finally {
						/*
						 * Main thread moved on, cleanup.
						 */
						if (isInterrupted() && result[0] != null) {
							try {
								result[0].close();
							}
							catch (IOException e) {
							}
						}
					}
				}
			};
			streamOpener.start();
			try {
				streamOpener.join(timeout < 1 ? DEFAULT_TIMEOUT: timeout);
				/*
				 * allow some time to open the inputstream
				 */
				if (streamOpener.isAlive())
					streamOpener.interrupt();
			}
			catch (InterruptedException e) {
				if (result[0] == null){
					result[0] = connection.getInputStream();//bug407211
				}
				Thread.currentThread().interrupt();
			}
		}
		catch (IOException e) {
		}
		return result[0];
	}

}
