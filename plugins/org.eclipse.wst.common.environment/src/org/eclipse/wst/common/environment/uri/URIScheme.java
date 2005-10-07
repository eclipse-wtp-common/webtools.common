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
 * A URIScheme represents a single scheme for some a family of
 * Univeral Resource Identifiers. Examples include "file", "http"
 * and "platform" (Eclipse).
 */
public interface URIScheme
{
  /**
   * Returns a new URI.
   */
  public URI newURI ( String uri ) throws URIException;

  /**
   * Returns a new URI.
   */
  public URI newURI ( URL url ) throws URIException;

  /**
   * Returns a new URI.
   */
  public URI newURI ( URI uri ) throws URIException;

  /**
   * Returns the proper name of the scheme.
   */
  public String toString ();

  /**
   * Returns true if and only if this is a hierarchical scheme.
   */
  public boolean isHierarchical ();

  /**
   * Returns true if and only if the given URI satisfies the
   * grammatical requirements of the scheme. Absolute URIs must
   * begin with "<scheme>:". Relative URIs must either not contain
   * a colon, ":", or it must begin with "./".
   */
  public boolean isValid ( URI uri );

  /**
   * Returns a Status object indicating whether or not the given
   * URI is valid. The severity and message of the Status object
   * will describe this.
   */
  public IStatus validate ( URI uri );
}
