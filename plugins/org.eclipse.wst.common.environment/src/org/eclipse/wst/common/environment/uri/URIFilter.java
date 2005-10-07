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

/**
 * This interface is implemented by classes that visit or list URIs.
 * Refer to {@link URI#getChildren}.
 */
public interface URIFilter
{
  /**
   * Filters the given URI.
   * @param uri The resource to filter.
   * @return True if the resource matches the filter,
   * false if it does not.
   * @throws URIException If the filter fails to analyze the resource.
   */
  public boolean accepts ( URI uri ) throws URIException;
}
