/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.common.uriresolver.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPluginRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.uriresolver.internal.provisional.URIResolverPlugin;

/**
 * @author csalter
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class URIResolverExtensionRegistryReader {

		protected static final String EXTENSION_POINT_ID = "resolverExtensions";
		protected static final String TAG_NAME = "resolverExtension";
		protected static final String ATT_ID = "id";
	    protected static final String ELEM_PROJECT_NATURE_ID = "projectNature";
	    protected static final String ATT_RESOURCE_TYPE = "resourceType";		    		
		protected static final String ATT_CLASS = "class";
	    protected static final String ATT_STAGE = "stage";		
	    protected static final String VAL_STAGE_PRE = "prenormalization";
	    protected static final String VAL_STAGE_POST = "postnormalization";
	    protected static final String ATT_VALUE = "value";
      protected static final String ATT_PRIORITY = "priority";
	   

		protected URIResolverExtensionRegistry registry;

		public URIResolverExtensionRegistryReader(URIResolverExtensionRegistry registry) {
			this.registry = registry;
		}

		/**
		 * read from plugin registry and parse it.
		 */
		public void readRegistry() {
			IPluginRegistry pluginRegistry = Platform.getPluginRegistry();
			IExtensionPoint point = pluginRegistry.getExtensionPoint(URIResolverPlugin.getInstance().getDescriptor().getUniqueIdentifier(), EXTENSION_POINT_ID);
			if (point != null) {
				IConfigurationElement[] elements = point.getConfigurationElements();
				for (int i = 0; i < elements.length; i++) {
					readElement(elements[i]);
				}
			}
		}

		/**
		 * readElement() - parse and deal with an extension like:
		 *
		 * <extension point="org.eclipse.wst.contentmodel.util_implementation">
		 *    <util_implementation class = org.eclipse.wst.baseutil.CMUtilImplementationImpl  />
		 * </extension>
		 */
		protected void readElement(IConfigurationElement element) {
			if (element.getName().equals(TAG_NAME)) {
				//String id = element.getAttribute(ATT_ID);
				String className = element.getAttribute(ATT_CLASS);
				//String projectNatureId = element.getAttribute(ATT_PROJECT_NATURE_ID);
				String resourceType = element.getAttribute(ATT_RESOURCE_TYPE);
				String stage = element.getAttribute(ATT_STAGE);	
        String priority = element.getAttribute(ATT_PRIORITY); 
        if(priority == null || priority.equals(""))
        {
          priority = URIResolverExtensionRegistry.PRIORITY_MEDIUM;
        }
				List projectNatureIds = new ArrayList();
				IConfigurationElement[] ids = element.getChildren(ELEM_PROJECT_NATURE_ID);
				int numids = ids.length;
				for(int i = 0; i < numids; i++)
				{
				  String tempid = ids[i].getAttribute(ATT_VALUE);
				  
				  if(tempid != null)
				  {
				    projectNatureIds.add(tempid);
				  }
				}
				if (className != null) {
					try {
						ClassLoader classLoader = element.getDeclaringExtension().getDeclaringPluginDescriptor().getPlugin().getClass().getClassLoader();
						int stageint = URIResolverExtensionRegistry.STAGE_POSTNORMALIZATION;
						if(stage.equalsIgnoreCase(VAL_STAGE_PRE))
						{
						  stageint = URIResolverExtensionRegistry.STAGE_PRENORMALIZATION;
						}
						registry.put(className, classLoader, projectNatureIds, resourceType, stageint, priority);
					} catch (Exception e) {
					}
				}
			}
		}
	}
