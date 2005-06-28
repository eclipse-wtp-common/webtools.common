/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *******************************************************************************/
package org.eclipse.wst.common.uriresolver.internal.provisional;


/**
 * A URIResolverExtension is used to extends the URI resolution processes in order to 
 * apply context specific resolution strategies.  
 */
public interface URIResolverExtension 
{
  public void resolve(URIResolverInput input, URIResolverResult result); 
}
