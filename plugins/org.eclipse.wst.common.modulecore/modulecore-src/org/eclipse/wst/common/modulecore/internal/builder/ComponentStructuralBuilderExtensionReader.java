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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.modulecore.internal.ModulecorePlugin;

public class ComponentStructuralBuilderExtensionReader extends RegistryReader {
	protected static HashMap builderExtensions = new HashMap();

	static final String ELEMENT_DEPLOYABLE_MODULE_BUILDER_EXT = "componentStructuralBuilder"; //$NON-NLS-1$
	//static final String ATT_OP_TYPE = "operationType"; //$NON-NLS-1$
	static final String SERVER_TARGET_ID = "serverTargetID"; //$NON-NLS-1$
	static final String DEPLOYABLE_MODULE_BUILDER = "componentStructuralBuilderDataModel"; //$NON-NLS-1$

    public ComponentStructuralBuilderExtensionReader() {
        super(ModulecorePlugin.PLUGIN_ID, "ComponentStructuralBuilder"); //$NON-NLS-1$
    }

	/**
	 * readElement() - parse and deal with an extension like: <operationExtension
	 * preOperationClass="com.ibm.etools....PreDeleteOperation"
	 * postOperationClass="com.ibm.etools....PostDeleteOperation"> </operationExtension>
	 */

	public boolean readElement(IConfigurationElement element) {
	    if (!element.getName().equals(ELEMENT_DEPLOYABLE_MODULE_BUILDER_EXT))
			return false;
		String serverTargetID = element.getAttribute(SERVER_TARGET_ID);
		ComponentStructuralProjectBuilderDataModel op = null;
        try {
            op = (ComponentStructuralProjectBuilderDataModel) element.createExecutableExtension(DEPLOYABLE_MODULE_BUILDER);
        } catch (CoreException e) {
            Logger.getLogger().log(e.toString());
        }
        if(op != null)
            addExtensionPoint(serverTargetID, op);
		return true;

	}

	/**
	 * Sets the extension point.
	 * 
	 * @param extensions
	 *            The extensions to set
	 */
	private static void addExtensionPoint(String serverTargetID, ComponentStructuralProjectBuilderDataModel builderOp) {
		builderExtensions.put(serverTargetID, builderOp);
	}

	protected static HashMap getExtensionPoints() {
		return builderExtensions;
	}

}
