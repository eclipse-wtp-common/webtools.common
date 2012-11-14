/*******************************************************************************
 * Copyright (c) 2012 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.flat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;

/**
 * A class to load and keep track of all IFlattenParticipantProviders, 
 * and also to assist in locating IFlattenParticipants of a specific ID. 
 */
public class FlattenParticipantModel {
	
	private static FlattenParticipantModel model;
	public static FlattenParticipantModel getDefault() {
		if( model == null )
			model = new FlattenParticipantModel();
		return model;
	}
	
	private ArrayList<FlattenParticipantProviderWrapper> providers;
	public FlattenParticipantModel() {
		
	}
	
	public IFlattenParticipant getParticipant(String id) {
		return getParticipant(id, null);
	}

	public IFlattenParticipant getParticipant(String id, Properties properties) {
		IFlattenParticipantProvider[] providers = getProviders();
		IFlattenParticipant result = null;
		for( int i = 0; i < providers.length; i++ ) {
			result = providers[i].findParticipant(id, properties);
			if( result != null )
				return result;
		}
		return null;
	}
	
	protected IFlattenParticipantProvider[] getProviders() {
		if( providers == null )
			loadProviders();
		return providers.toArray(new IFlattenParticipantProvider[providers.size()]);
	}
	
	protected void loadProviders() {
		ArrayList<FlattenParticipantProviderWrapper> tmp = new ArrayList<FlattenParticipantProviderWrapper>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ModulecorePlugin.PLUGIN_ID, "flattenParticipantProvider"); //$NON-NLS-1$
		String id = null;
		for( int j = 0; j < cf.length; j++ ) {
			tmp.add(getWrapper(cf[j]));
		}
		
		// Sort
		Collections.sort(tmp, new Comparator<FlattenParticipantProviderWrapper>() {
			public int compare(FlattenParticipantProviderWrapper o1,
					FlattenParticipantProviderWrapper o2) {
				if( o1 == null && o2 == null )
					return 0;
				if( o1 == null )
					return 1;
				if( o2 == null )
					return -1;
				return o2.getWeight() - o1.getWeight();
			}
		});
		
		providers = tmp;
	}
	
	private FlattenParticipantProviderWrapper getWrapper(IConfigurationElement element) {
		return new FlattenParticipantProviderWrapper(element);
	}
	
	private static class FlattenParticipantProviderWrapper implements IFlattenParticipantProvider {
		private IConfigurationElement cf;
		private int weight;
		private IFlattenParticipantProvider delegate;
		public FlattenParticipantProviderWrapper(IConfigurationElement element) {
			this.cf = element;
			String s = element.getAttribute("weight");
			try {
				weight = Integer.parseInt(s);
			} catch(NumberFormatException nfe ) {
				// TODO trace / log? Maybe not necessary. Use default weight
				weight = 0; // default weight
			}
		}
		
		public int getWeight() {
			return weight;
		}
		
		public IFlattenParticipant findParticipant(String id, Properties props) {
			if( delegate == null )
				loadDelegate();
			return delegate == null ? null : delegate.findParticipant(id, props);
		}
		
		private void loadDelegate() {
			try {
				delegate = (IFlattenParticipantProvider) cf.createExecutableExtension("class");
			} catch(CoreException ce ) {
				// TODO where to do the logging?
			}
		}
	}
	
}
