/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
 * Refer to {@link IURI#getChildren}.
 * 
 * @since 1.0
 */
public interface IURIFilter
{
  /**
   * Filters the given IURI.
   * @param uri The resource to filter.
   * @return True if the resource matches the filter,
   * false if it does not.
   * @param uri the uri that will be accepted or rejected.
   * @return returns true if this uri is accepted.
   * @throws URIException If the filter fails to analyze the resource.
   */
  public boolean accepts ( IURI uri ) throws URIException;
}
