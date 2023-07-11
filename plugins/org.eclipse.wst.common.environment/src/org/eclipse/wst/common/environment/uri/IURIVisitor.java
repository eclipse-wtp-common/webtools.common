/*******************************************************************************
 * Copyright (c) 2004, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.environment.uri;

/**
 * This interface is implemented by classes that visit URIs.
 * Refer to {@link IURI#visit}.
 * 
 * @since 1.0
 */
public interface IURIVisitor
{
  /**
   * Visits the given IURI.
   * @param uri The resource to visit.
   * @return True if the resource's descendants should be visited,
   * or false if they should not be visited.
   * @throws URIException If the visit method fails.
   */
  public boolean visit ( IURI uri ) throws URIException;
}
