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
package org.eclipse.wst.common.modulecore.internal.builder;

import java.util.HashMap;

public class ComponentStructuralBuilderExtensionRegistry {
    
	protected static boolean extPointHasRead = false;

	protected static ComponentStructuralBuilderExtensionRegistry instance = null;

	protected static HashMap builderExtensions = null;

	protected static ComponentStructuralBuilderExtensionReader builderExtensionReader = null;
    /**
     * 
     */
    public ComponentStructuralBuilderExtensionRegistry() {
        super();
    }
    
	private static HashMap getExtensionPoints() {
		if (!extPointHasRead) {
		    builderExtensionReader = new ComponentStructuralBuilderExtensionReader();
		    builderExtensionReader.readRegistry();
			extPointHasRead = true;
		}
		if (builderExtensionReader == null)
			return null;
		return ComponentStructuralBuilderExtensionReader.getExtensionPoints();
	}
	
	protected static ComponentStructuralProjectBuilderDataModel getComponentStructuralBuilderDMForServerTargetID(String serverTargetID) {
	    return (ComponentStructuralProjectBuilderDataModel)getExtensionPoints().get(serverTargetID);
	}
	/**
	 * Gets the instance.
	 * 
	 * @return Returns a EjbPageExtensionRegistry
	 */
	public static ComponentStructuralBuilderExtensionRegistry getInstance() {
		if (instance == null)
			instance = new ComponentStructuralBuilderExtensionRegistry();
		return instance;
	}
}
