/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal;

import org.eclipse.core.resources.IResource;
import org.eclipse.wst.validation.internal.operations.WorkbenchContext;

/**
 * This has been deprecated since WTP 3.1.2 and will be deleted post WTP 3.2.
 * See https://bugs.eclipse.org/bugs/show_bug.cgi?id=292934
 * @deprecated 
 * @author jsholl
 */
public class ModuleCoreValidatorHelper extends WorkbenchContext {

		public static final String MODULECORE = "MODULECORE";
		public ModuleCoreValidatorHelper() {
			super();
			//	the following will register the helper's symbolic methods
		    Class [] args = new Class[0] ;
		    registerModel(MODULECORE, "loadModel", args);
		}
		/**
		 * Load the Trading Session for validation
		 */
		public Object loadModel() {
		return getProject();

	}		
		/**
		 * Given a resource, return its non-eclipse-specific location. If this
		 * resource, or type of resource, isn't handled by this helper, return
		 * null.
		 */
		public String getPortableName(IResource resource) {
			
			return resource.getFullPath().toString();
		}
	}
