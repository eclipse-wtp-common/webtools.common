/*******************************************************************************
 * Copyright (c) 2003, 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.HashMap;

public class ComponentStructuralBuilderExtensionRegistry {
    
	protected static boolean extPointHasRead = false; //$NON-NLS-1$

	protected static ComponentStructuralBuilderExtensionRegistry INSTANCE = null; //$NON-NLS-1$

	protected static HashMap builderExtensions = null; //$NON-NLS-1$

	protected static ComponentStructuralBuilderExtensionReader builderExtensionReader = null; //$NON-NLS-1$
    /**
     * 
     */
    public ComponentStructuralBuilderExtensionRegistry() {
        super();
    }
    
	private static void getExtensionPoints() {
		if (!extPointHasRead) {
		    builderExtensionReader = new ComponentStructuralBuilderExtensionReader();
		    builderExtensionReader.readRegistry();
			extPointHasRead = true;
		}
		if (builderExtensionReader == null)
			return;
		builderExtensions = ComponentStructuralBuilderExtensionReader.getExtensionPoints();
	}
	
	protected static ComponentStructuralBuilderDataModel getComponentStructuralBuilderDMForServerTargetID(String serverTargetID, String componentTypeID) {
	    if(!extPointHasRead)
	        getExtensionPoints();
	    if(builderExtensions == null || builderExtensions.isEmpty()) return null;
	    
	    ComponentStructuralBuilderCache cache = null;
	    if(builderExtensions.containsKey(serverTargetID))
	        cache = (ComponentStructuralBuilderCache)builderExtensions.get(serverTargetID);
	    if(cache == null) {
	        if(builderExtensions.containsKey("default")){
	            cache = (ComponentStructuralBuilderCache)builderExtensions.get("default");
	        }
	    }
	    if(cache == null)
	        return null;
	    return cache.getAvailableComponentStructuralBuilderForID(componentTypeID);
	}
	/**
	 * Gets the instance.
	 * 
	 * @return Returns a EjbPageExtensionRegistry
	 */
	public static ComponentStructuralBuilderExtensionRegistry getInstance() {
		if (INSTANCE == null)
		    INSTANCE = new ComponentStructuralBuilderExtensionRegistry();
		return INSTANCE;
	}
}
