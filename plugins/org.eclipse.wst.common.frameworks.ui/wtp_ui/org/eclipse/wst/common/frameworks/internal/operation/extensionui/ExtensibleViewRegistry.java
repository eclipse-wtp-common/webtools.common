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
package org.eclipse.wst.common.frameworks.internal.operation.extensionui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.core.util.RegistryReader;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;

public class ExtensibleViewRegistry extends RegistryReader {

	private static ExtensibleViewRegistry INSTANCE = null;
	private Map extendedViewerMap = null;
	private Map descriptorMap = null;

	protected interface Elements {
		public static final String EXTENDED_VIEWER = "extendedViewer"; //$NON-NLS-1$
		public static final String VIEWER_ID = "viewerID"; //$NON-NLS-1$
		public static final String PARENT_VIEWER_ID = "parentViewerID"; //$NON-NLS-1$
		public static final String FACTORY_CLASS = "factoryClass"; //$NON-NLS-1$
		public static final String ICON = "icon"; //$NON-NLS-1$
		public static final String LABEL = "label"; //$NON-NLS-1$
		public static final String FUNCTION_GROUP_ID = "functionGroupID"; //$NON-NLS-1$
	}

	/**
	 *  
	 */
	private ExtensibleViewRegistry() {
		super(WTPUIPlugin.PLUGIN_ID, WTPUIPlugin.EXTENDED_VIEWER_REGISTRY_EXTENSION_POINT);
	}

	public static ExtensibleViewRegistry getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ExtensibleViewRegistry();
			INSTANCE.readRegistry();
		}
		return INSTANCE;
	}

	public Descriptor getDescriptor(String viewerID) {
		return (Descriptor) getDescriptorMap().get(viewerID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.internal.registry.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public boolean readElement(IConfigurationElement element) {
		if (element.getName().equals(Elements.EXTENDED_VIEWER)) {
			Descriptor descriptor = new Descriptor(element);
			getDescriptorMap().put(descriptor.getViewerID(), descriptor);
			return true;
		}
		return false;
	}

	/**
	 * @return Returns the extendedViewerMap.
	 */
	protected Map getExtendedViewerMap() {
		if (extendedViewerMap == null)
			extendedViewerMap = new HashMap();
		return extendedViewerMap;
	}

	/**
	 * @return Returns the descriptorMap.
	 */
	protected Map getDescriptorMap() {
		if (descriptorMap == null)
			descriptorMap = new HashMap();
		return descriptorMap;
	}

	public class Descriptor {

		private String viewerID = null;
		private String parentViewerID = null;
		private String factoryClass = null;
		private String icon = null;
		private String label = null;
		private String functionGroupID = null;

		private String toStringCache = null;
		private IConfigurationElement element = null;

		/**
		 *  
		 */
		public Descriptor(IConfigurationElement element) {
			this.element = element;

			this.viewerID = this.element.getAttribute(ExtensibleViewRegistry.Elements.VIEWER_ID);
			this.parentViewerID = this.element.getAttribute(ExtensibleViewRegistry.Elements.PARENT_VIEWER_ID);
			this.factoryClass = this.element.getAttribute(ExtensibleViewRegistry.Elements.FACTORY_CLASS);
			this.icon = this.element.getAttribute(ExtensibleViewRegistry.Elements.ICON);
			this.label = this.element.getAttribute(ExtensibleViewRegistry.Elements.LABEL);
			this.functionGroupID = this.element.getAttribute(ExtensibleViewRegistry.Elements.FUNCTION_GROUP_ID);
		}

		public IExtensibleViewFactory createFactoryInstance() {
			IExtensibleViewFactory factoryInstance = null;
			try {
				factoryInstance = (IExtensibleViewFactory) this.element.createExecutableExtension(getFactoryClass());
			} catch (CoreException e) {
				WTPUIPlugin.logError(e);
			}
			return factoryInstance;
		}

		/**
		 * @return Returns the factoryClass.
		 */
		public String getFactoryClass() {
			return factoryClass;
		}

		/**
		 * @return Returns the functionGroupID.
		 */
		public String getFunctionGroupID() {
			return functionGroupID;
		}

		/**
		 * @return Returns the icon.
		 */
		public String getIcon() {
			return icon;
		}

		/**
		 * @return Returns the label.
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * @return Returns the parentViewerID.
		 */
		public String getParentViewerID() {
			return parentViewerID;
		}

		/**
		 * @return Returns the viewerID.
		 */
		public String getViewerID() {
			return viewerID;
		}

		@Override
		public String toString() {
			if (toStringCache == null)
				toStringCache = "ExtensibleViewRegistry.Info [viewerID=\"" + getViewerID() + "\",parentViewerID=\"" + getParentViewerID() + "\",factoryClass=" + getFactoryClass() + "\",functionGroupID=\"" + getFunctionGroupID() + "\"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			return toStringCache;
		}
	}

}
