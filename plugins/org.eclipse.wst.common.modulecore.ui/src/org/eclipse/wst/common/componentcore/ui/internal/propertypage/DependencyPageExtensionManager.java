/******************************************************************************
 * Copyright (c) 2010 Red Hat and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *    Rob Stryker - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - added support for enablement expressions
 ******************************************************************************/

package org.eclipse.wst.common.componentcore.ui.internal.propertypage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.resources.IProject;
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
	private static final String DEFAULT_WEIGHT = "100"; //$NON-NLS-1$
	private static DependencyPageExtensionManager manager = null;
	public static DependencyPageExtensionManager getManager() {
		if( manager == null )
			manager = new DependencyPageExtensionManager();
		return manager;
	}
	
	private HashMap<String, IDependencyPageProvider> providers = null;
	private HashMap<String, String> providerWeight;
	
	public IDependencyPageProvider getProvider(IFacetedProject project) {
		if( providers == null )
			loadProviders();
		IDependencyPageProvider foundProvider = null;
		int highestWeight = 0;
		for (Iterator iterator = providers.keySet().iterator(); iterator.hasNext();) {
			String id = (String) iterator.next();
			IDependencyPageProvider temp = providers.get(id);
			if( temp.canHandle(project)) {
				int weight = Integer.valueOf(providerWeight.get(id)).intValue();
				if (foundProvider == null) {
					foundProvider = temp;
					highestWeight = weight;
				}
				else {
					if (highestWeight < weight) {
						foundProvider = temp;
						highestWeight = weight;
					}
				}
			}
		}
		return foundProvider;
	}
	
	private void loadProviders() {
		HashMap<String, IDependencyPageProvider> temp = new HashMap<String, IDependencyPageProvider>();
		HashMap<String, String> tempProviderWeight = new HashMap<String, String>();
		String weight;
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(
				ModuleCoreUIPlugin.PLUGIN_ID, "moduleDependencyPropertyPage"); //$NON-NLS-1$
		for( int i = 0; i < cf.length; i++ ) {
			try {
				temp.put(cf[i].getAttribute("id"),  //$NON-NLS-1$
					(IDependencyPageProvider)cf[i].createExecutableExtension("class"));  //$NON-NLS-1$
				weight = cf[i].getAttribute("weight"); //$NON-NLS-1$
				tempProviderWeight.put(cf[i].getAttribute("id"),(weight == null) ? DEFAULT_WEIGHT : weight); //$NON-NLS-1$
			} catch( CoreException ce ) 
			{
				ModuleCoreUIPlugin.log( ce );
			}
		}
		providers = temp;
		providerWeight = tempProviderWeight;
	}
	
	public WizardFragment[] loadAllReferenceWizardFragments() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(
				ModuleCoreUIPlugin.PLUGIN_ID, "referenceWizardFragment"); //$NON-NLS-1$
		ArrayList<WizardFragment> list = new ArrayList<WizardFragment>();
		for( int i = 0; i < cf.length; i++ ) {
			try {
				list.add((WizardFragment)cf[i].createExecutableExtension("class"));
			} catch( CoreException ce) 
			{
				ModuleCoreUIPlugin.log( ce );
			}
		}
		return list.toArray(new WizardFragment[list.size()]);
	}
	
	public WizardFragment loadReferenceWizardFragment(String id) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(
				ModuleCoreUIPlugin.PLUGIN_ID, "referenceWizardFragment"); //$NON-NLS-1$
		for( int i = 0; i < cf.length; i++ ) {
			if( cf[i].getAttribute("id").equals(id)) //$NON-NLS-1$
				try {
					return (WizardFragment)cf[i].createExecutableExtension("class"); //$NON-NLS-1$
				} catch( CoreException ce) 
				{
					ModuleCoreUIPlugin.log( ce );
				}
		}
		return null;
	}
	
	public List<ReferenceExtension> getExposedReferenceExtensions() 
	{
		final List<ReferenceExtension> list = new ArrayList<ReferenceExtension>();
		final Set<String> hidden = new HashSet<String>();
		
        for( ReferenceExtension ext : getAllReferenceExtensions() )
        {
            if( ext.isHidden() )
            {
				hidden.add( ext.getId() );
			}
		}
		
        for( ReferenceExtension ext : getAllReferenceExtensions() )
        {
            if( ! ext.isHidden() && ! hidden.contains( ext.getId() ) )
            {
                list.add( ext );
            }
		}
		
		return list;
	}
	
	public List<ReferenceExtension> getAllReferenceExtensions() 
	{
		final ArrayList<ReferenceExtension> list = new ArrayList<ReferenceExtension>();
		final IExtensionRegistry registry = Platform.getExtensionRegistry();
		
		for( IConfigurationElement cf : registry.getConfigurationElementsFor( ModuleCoreUIPlugin.PLUGIN_ID, "referenceWizardFragment" ) )
		{
			list.add( new ReferenceExtension( cf ) );
		}

		return list;
	}
	
	public ReferenceExtension findReferenceExtension(String id) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(
				ModuleCoreUIPlugin.PLUGIN_ID, "referenceWizardFragment"); //$NON-NLS-1$
		ArrayList<ReferenceExtension> list = new ArrayList<ReferenceExtension>();
		for( int i = 0; i < cf.length; i++ ) {
			if(cf[i].getAttribute("id").equals(id)) //$NON-NLS-1$
				return new ReferenceExtension(cf[i]);
		}
		return null;
	}
	
	public class ReferenceExtension {
		private IConfigurationElement element;
		private String id, name, imageLoc;
		private Image image;
		private boolean hidden;
		private Expression enablementExpression;
		
		public ReferenceExtension(IConfigurationElement element) 
		{
			this.element = element;
			this.id = element.getAttribute("id"); //$NON-NLS-1$
			this.name = element.getAttribute("name"); //$NON-NLS-1$
			this.imageLoc = element.getAttribute("icon"); //$NON-NLS-1$
			this.hidden = Boolean.parseBoolean(element.getAttribute("hidden")); //$NON-NLS-1$
			
			for( IConfigurationElement child : element.getChildren( "enablement" ) ) //$NON-NLS-1$
			{
	            try
	            {
	                this.enablementExpression = ExpressionConverter.getDefault().perform( child );
	            }
	            catch( CoreException e )
	            {
	            	ModuleCoreUIPlugin.log( e );
	            }
			}
		}
		
		public String getId() { return this.id;}
		public String getName() { return this.name; }
		public boolean isHidden() { return this.hidden; }
		
		public boolean isApplicable( final IProject project )
		{
			if( this.enablementExpression != null )
			{
		        final EvaluationContext evalContext = new EvaluationContext( null, project );
		        evalContext.setAllowPluginActivation( true );
		        
	            try
	            {
	                final EvaluationResult evalResult = this.enablementExpression.evaluate( evalContext );
	                
	                if( evalResult == EvaluationResult.FALSE )
	                {
	                    return false;
	                }
	            }
	            catch( CoreException e )
	            {
	            	ModuleCoreUIPlugin.log( e );
	            }
			}
			
			return true;
		}
		
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
	
	public static IVirtualComponentLabelProvider[] loadDelegates() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(
				ModuleCoreUIPlugin.PLUGIN_ID, "virtualComponentLabelProvider"); //$NON-NLS-1$
		List<IConfigurationElement> list = Arrays.asList(cf);
		Comparator c = new Comparator<IConfigurationElement>() {
			public int compare(IConfigurationElement o1,
					IConfigurationElement o2) {
				String o1String, o2String;
				int o1int, o2int;
				o1String=o1.getAttribute("weight");
				o2String=o2.getAttribute("weight");
				try {
					o1int = Integer.parseInt(o1String);
				} catch(NumberFormatException nfe ) {
					o1int = 0;
				}
				try {
					o2int = Integer.parseInt(o2String);
				} catch(NumberFormatException nfe ) {
					o2int = 0;
				}
				return o1int-o2int;
			}
		};
		Collections.sort(list, c);
		ArrayList<IVirtualComponentLabelProvider> retList = new ArrayList<IVirtualComponentLabelProvider>();
		Iterator<IConfigurationElement> i = list.iterator();
		while(i.hasNext()) {
			try {
				IConfigurationElement el = i.next();
				String className = el.getAttribute("class");
				retList.add((IVirtualComponentLabelProvider)el.createExecutableExtension("class"));
			} catch( CoreException ce) {
				ModuleCoreUIPlugin.log( ce );
			}
		}
		return retList.toArray(new IVirtualComponentLabelProvider[retList.size()]);
	}

}
