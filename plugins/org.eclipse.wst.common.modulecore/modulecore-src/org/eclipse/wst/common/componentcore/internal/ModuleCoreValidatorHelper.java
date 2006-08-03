package org.eclipse.wst.common.componentcore.internal;

import org.eclipse.core.resources.IResource;
import org.eclipse.wst.validation.internal.operations.WorkbenchContext;


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
