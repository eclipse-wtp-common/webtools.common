/******************************************************************************
 * Copyright (c) 2009 Red Hat
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rob Stryker - initial implementation and ongoing maintenance
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.ui.internal.propertypage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.common.componentcore.ui.ModuleCoreUIPlugin;
import org.eclipse.wst.common.componentcore.ui.internal.taskwizard.WizardFragment;
import org.eclipse.wst.common.componentcore.ui.propertypage.IDependencyPageProvider;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;

public class DependencyPageExtensionManager {
	private static DependencyPageExtensionManager manager = null;
	public static DependencyPageExtensionManager getManager() {
		if( manager == null )
			manager = new DependencyPageExtensionManager();
		return manager;
	}
	
	private HashMap<String, IDependencyPageProvider> providers = null;
	
	public IDependencyPageProvider getProvider(IFacetedProject project) {
		if( providers == null )
			loadProviders();
		Iterator<IDependencyPageProvider> i = providers.values().iterator();
		IDependencyPageProvider temp;
		while(i.hasNext()) {
			temp = i.next();
			if( temp.canHandle(project))
				return temp;
		}
		return null;
	}
	
	private void loadProviders() {
		HashMap<String, IDependencyPageProvider> temp = new HashMap<String, IDependencyPageProvider>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(
				ModuleCoreUIPlugin.PLUGIN_ID, "moduleDependencyPropertyPage"); //$NON-NLS-1$
		for( int i = 0; i < cf.length; i++ ) {
			try {
				temp.put(cf[i].getAttribute("id"),  //$NON-NLS-1$
					(IDependencyPageProvider)cf[i].createExecutableExtension("class"));  //$NON-NLS-1$
			} catch( CoreException ce ) {}
		}
		providers = temp;
	}
	
	public WizardFragment loadReferenceWizardFragment(String id) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(
				ModuleCoreUIPlugin.PLUGIN_ID, "referenceWizardFragment"); //$NON-NLS-1$
		for( int i = 0; i < cf.length; i++ ) {
			if( cf[i].getAttribute("id").equals(id)) //$NON-NLS-1$
				try {
					return (WizardFragment)cf[i].createExecutableExtension("class"); //$NON-NLS-1$
				} catch( CoreException ce) {}
		}
		return null;
	}
	
	public ReferenceExtension[] getReferenceExtensions() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(
				ModuleCoreUIPlugin.PLUGIN_ID, "referenceWizardFragment"); //$NON-NLS-1$
		ArrayList<ReferenceExtension> list = new ArrayList<ReferenceExtension>();
		for( int i = 0; i < cf.length; i++ ) {
			list.add(new ReferenceExtension(cf[i]));
		}
		return list.toArray(new ReferenceExtension[list.size()]);
	}
	
	public class ReferenceExtension {
		private IConfigurationElement element;
		private String id, name, imageLoc;
		private Image image;
		public ReferenceExtension(IConfigurationElement element) {
			this.element = element;
			this.id = element.getAttribute("id"); //$NON-NLS-1$
			this.name = element.getAttribute("name"); //$NON-NLS-1$
			this.imageLoc = element.getAttribute("icon"); //$NON-NLS-1$
		}
		public String getId() { return this.id;}
		public String getName() { return this.name; }
		public Image getImage() { 
			if( image == null ) {
				if( imageLoc != null && element.getContributor().getName() != null) {
					ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin(element.getContributor().getName(), imageLoc);
					image = desc.createImage();
				}
			}
			return image;
		}
		public void disposeImage() {
			if( image != null ) {
				image.dispose();
				image = null;
			}
		}
	}
}
