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
import org.eclipse.core.runtime.IStatus;

/**
 * A IURIScheme represents a single scheme for some a family of
 * Univeral Resource Identifiers. Examples include "file", "http"
 * and "platform" (Eclipse).
 * 
 * @since 1.0
 */
public interface IURIScheme
{
  /**
   * @param uri the URI to be created.
   * @return Returns a new IURI.
   * @throws URIException if the uri specified is not valid or
   * can not be created.
   */
  public IURI newURI ( String uri ) throws URIException;

  /**
   * @param url the url used to create the URI.
   * @return Returns a new IURI.
   * @throws URIException if the url specified is not valid or
   * can not be created.
   */
  public IURI newURI ( URL url ) throws URIException;

  /**
   * @param uri the URI to be created.
   * @return Returns a new IURI.
   * @throws URIException if the uri specified is not valid or
   * can not be created.
   */
  public IURI newURI ( IURI uri ) throws URIException;

  /**
   * @return Returns the proper name of the scheme.
   */
  public String toString ();

  /**
   * @return Returns true if and only if this is a hierarchical scheme.
   */
  public boolean isHierarchical ();

  /**
   * @param uri the uri to check for validity.
   * @return Returns true if and only if the given IURI satisfies the
   * grammatical requirements of the scheme. Absolute URIs must
   * begin with "<scheme>:". Relative URIs must either not contain
   * a colon, ":", or it must begin with "./".
   */
  public boolean isValid ( IURI uri );

  /**
   * @param uri the uri to check for validity.
   * @return Returns a Status object indicating whether or not the given
   * IURI is valid. The severity and message of the Status object
   * will describe this.
   */
  public IStatus validate ( IURI uri );
}
