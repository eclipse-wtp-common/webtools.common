/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel;

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

public class DataModelExtensionReader extends RegistryReader {

    public class DataModelImplementExtensionCache {
       private HashMap items;
       
       public void addImplementExtensionCache(String functionGroupID, String id){
           if(items == null)
               items = new HashMap();
           if (definesExtensions.containsKey(functionGroupID))
               Logger.getLogger().logError(new RuntimeException("Duplicate " + IMPLEMENTS_TYPE_ELEMENT + " " + ATTRIBUTE_FG + " " + functionGroupID));
           else
               items.put(functionGroupID, id);
       }
       
       public String getIDForFunctionGroup(String functionGroupID){
           if(items == null || !items.containsKey(functionGroupID))
               return null;
           return (String)items.get(functionGroupID);
       }
    }

	private static final String EXTENSION = "DataModelProviderExtension";
	
    private static final String PROVIDER_ELEMENT = "DataModelProvider";
    private static final String DEFINES_TYPE_ELEMENT = "ProviderDefinesType";
    private static final String IMPLEMENTS_TYPE_ELEMENT = "ProviderImplementsType";
	
    private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_CLASS = "class";
    private static final String ATTRIBUTE_PROVIDER_TYPE = "providerType";
    private static final String ATTRIBUTE_PROVIDER_ID = "providerID";
    private static final String ATTRIBUTE_FG = "functionGroupID";
    

	private HashMap providerExtensions;
    private HashMap definesExtensions;
    private HashMap implementsExtensions;
    
    private boolean hasInitialized = false;
    
	public DataModelExtensionReader() {
		super(WTPCommonPlugin.PLUGIN_ID, EXTENSION);
    }

	public boolean readElement(IConfigurationElement element) {
		if (element.getName().equals(PROVIDER_ELEMENT)) {
    		String id = element.getAttribute(ATTRIBUTE_ID);
    		if (null == id || id.trim().length() == 0) {
    			Logger.getLogger().logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + PROVIDER_ELEMENT + " is missing " + ATTRIBUTE_ID));
    		}
    		String className = element.getAttribute(ATTRIBUTE_CLASS);
    		if (null == className || className.trim().length() == 0) {
    			Logger.getLogger().logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + PROVIDER_ELEMENT + " is missing " + ATTRIBUTE_CLASS));
    		}
            addProviderExtension(id, element);
        } else if(element.getName().equals(DEFINES_TYPE_ELEMENT)){
            String type = element.getAttribute(ATTRIBUTE_PROVIDER_TYPE);
            if (null == type || type.trim().length() == 0) {
                Logger.getLogger().logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + DEFINES_TYPE_ELEMENT + " is missing " + ATTRIBUTE_PROVIDER_TYPE));
            }
            String id = element.getAttribute(ATTRIBUTE_PROVIDER_ID);
            if (null == id || id.trim().length() == 0) {
                Logger.getLogger().logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + DEFINES_TYPE_ELEMENT + " is missing " + ATTRIBUTE_PROVIDER_ID));
            }
            addDefinesExtension(type, id);
        } else if(element.getName().equals(IMPLEMENTS_TYPE_ELEMENT)){
            String type = element.getAttribute(ATTRIBUTE_PROVIDER_TYPE);
            if (null == type || type.trim().length() == 0) {
                Logger.getLogger().logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + DEFINES_TYPE_ELEMENT + " is missing " + ATTRIBUTE_PROVIDER_TYPE));
            }
            String id = element.getAttribute(ATTRIBUTE_PROVIDER_ID);
            if (null == id || id.trim().length() == 0) {
                Logger.getLogger().logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + DEFINES_TYPE_ELEMENT + " is missing " + ATTRIBUTE_PROVIDER_ID));
            }
            String functionGroupID = element.getAttribute(ATTRIBUTE_FG);
            if (null == functionGroupID || functionGroupID.trim().length() == 0) {
                Logger.getLogger().logError(new RuntimeException("Extension:" + EXTENSION + " Element:" + DEFINES_TYPE_ELEMENT + " is missing " + ATTRIBUTE_FG));
            }
            addImplementsExtension(type, id, functionGroupID);
        }
		return true;
	}

	private void addProviderExtension(String id, IConfigurationElement element) {
		if (providerExtensions.containsKey(id)) {
			Logger.getLogger().logError(new RuntimeException("Duplicate " + PROVIDER_ELEMENT + " " + ATTRIBUTE_ID + " " + id));
		}
		providerExtensions.put(id, element);
	}
    
    private void addDefinesExtension(String type, String id) {
        if (definesExtensions.containsKey(type)) {
            Logger.getLogger().logError(new RuntimeException("Duplicate " + PROVIDER_ELEMENT + " " + ATTRIBUTE_PROVIDER_TYPE + " " + type));
        }
        definesExtensions.put(type, id);
    }
    
    private void addImplementsExtension(String type, String id, String functionGroupID) {
        DataModelImplementExtensionCache cache;
        if (providerExtensions.containsKey(type))
            cache = (DataModelImplementExtensionCache)providerExtensions.get(type);
        else
            cache = new DataModelImplementExtensionCache();
        cache.addImplementExtensionCache(functionGroupID, id);
        implementsExtensions.put(type, cache);
    }
    
	protected IConfigurationElement getProviderExtension(String id) {
        readRegistryIfNecessary();
		IConfigurationElement element = (IConfigurationElement) providerExtensions.get(id);
		if (null == element) {
			throw new RuntimeException("Extension:" + EXTENSION + " Element:" + PROVIDER_ELEMENT + " not found for " + ATTRIBUTE_ID + ": " + id);
		}
		return element;
	}
    
    protected String getDefinesExtension(String providerType) {
        readRegistryIfNecessary();
        String element = (String) definesExtensions.get(providerType);
        if (null == element) {
            throw new RuntimeException("Extension:" + EXTENSION + " Element:" + DEFINES_TYPE_ELEMENT + " not found for " + ATTRIBUTE_PROVIDER_TYPE + ": " + providerType);
        }
        return element;
    }
    
    protected String getImplementsExtension(String providerType, String functionGroupID) {
        readRegistryIfNecessary();
        if (!implementsExtensions.containsKey(providerType) || functionGroupID == null || functionGroupID.equals("")) {
            return getDefinesExtension(providerType);
        }
        DataModelImplementExtensionCache cache = (DataModelImplementExtensionCache) implementsExtensions.get(providerType);
        String providerID = cache.getIDForFunctionGroup(functionGroupID);
        if(providerID == null)
            return getDefinesExtension(providerType);
        return providerID;
    }
    
    private void readRegistryIfNecessary() {
        if (!hasInitialized) {
            providerExtensions = new HashMap();
            definesExtensions = new HashMap();
            implementsExtensions = new HashMap();
            readRegistry();
            hasInitialized = true;
        }
    }


    
	public IDataModelProvider getProvider(String id) {
		IDataModelProvider provider = null;
		IConfigurationElement element = getProviderExtension(id);
		try {
			provider = (IDataModelProvider) element.createExecutableExtension(ATTRIBUTE_CLASS);
		} catch (CoreException e) {
			Logger.getLogger().logError(e);
		}
		return provider;
	}
    
    public IDataModelProvider getProvider(String providerType, String functionGroupID) {
        String providerID = getImplementsExtension(providerType, functionGroupID);
        return getProvider(providerID);
    }
}
