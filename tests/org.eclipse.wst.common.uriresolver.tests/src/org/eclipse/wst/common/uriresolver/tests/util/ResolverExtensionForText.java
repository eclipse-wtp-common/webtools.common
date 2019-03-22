/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.wst.common.uriresolver.tests.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverExtension;

public class ResolverExtensionForText implements URIResolverExtension {
	
	public static final String PUBLIC_ID_URL = "http://www.public.org";
	public static final String PUBLIC_ID_LOCATION = "/folder/public.extension";
	
	public static final String SYSTEM_ID_URL = "http://www.system.org";
	public static final String SYSTEM_ID_LOCATION = "/folder/system.extension";
	
	public String resolve(IFile file, String baseLocation, String publicId, String systemId) {
		AuxiliaryCounter.getInstance().incrementCounter();
		if(PUBLIC_ID_URL.equals(publicId)) {
			return PUBLIC_ID_LOCATION;
		} else if(SYSTEM_ID_URL.equals(systemId)) {
			return SYSTEM_ID_LOCATION;
		}
		return null;
	}

}
