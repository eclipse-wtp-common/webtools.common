/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.environment.uri;

import java.net.URL;

/**
 * This is a factory for creating new IURI and IURIScheme objects.
 * 
 * @since 1.0
 */
public interface IURIFactory
{
  /**
   * Creates and returns a new IURI for the given string.
   * @param uri the uri to be created.
   * @return the new URI.
   * @throws URIException if the uri parameter is not a valid URI.
   */
  public IURI newURI ( String uri ) throws URIException;

  /**
   * Creates and returns a new IURI for the given URL.
   * @param url the url to use to create this URI
   * @return the new URI.
   * @throws URIException if the url parameter is not a valid url.
   */
  public IURI newURI ( URL url ) throws URIException;

  /**
   * Creates and returns a new IURIScheme for the given scheme string.
   * If the string contains no colons, the entire string is interpretted
   * as the name of the scheme. If the string contains a colon, then the
   * substring up to but excluding the first colon is interpretted as the
   * name of the scheme, meaning the caller can pass in any IURI string in
   * order to get a IURIScheme object.
   * 
   * @param schemeOrURI the scheme or URI from which to create the scheme.
   * @return the new Scheme.
   * @throws URIException if schemeOrUri parameter does not contain
   * a valid scheme or URI name.
   */
  public IURIScheme newURIScheme ( String schemeOrURI ) throws URIException;
}
