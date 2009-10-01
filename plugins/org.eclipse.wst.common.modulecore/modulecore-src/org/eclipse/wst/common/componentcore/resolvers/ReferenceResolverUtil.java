package org.eclipse.wst.common.componentcore.resolvers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

public class ReferenceResolverUtil {
	public static ReferenceResolverUtil instance = null;
	public static ReferenceResolverUtil getDefault() {
		if( instance == null )
			instance = new ReferenceResolverUtil();
		return instance;
	}
	
	private HashMap<String, ReferenceResolverWrapper> resolvers = null;
	private ArrayList<IReferenceResolver> sorted = null;
	private DefaultReferenceResolver defaultResolver = new DefaultReferenceResolver();
	public IReferenceResolver[] getResolvers() {
		if( resolvers == null )
			loadResolvers();
		if (sorted.isEmpty()) return new IReferenceResolver[0];
        return (IReferenceResolver[]) sorted.toArray(new IReferenceResolver[sorted.size()]);
	}
	
	public IReferenceResolver getResolver(IVirtualComponent context, ReferencedComponent reference) {
		getResolvers();
		Iterator<IReferenceResolver> i = sorted.iterator();
		IReferenceResolver resolver;
		while(i.hasNext()) {
			resolver = i.next();
			if( resolver.canResolve(context, reference))
				return resolver;
		}
		return defaultResolver;
	}
	
	public IReferenceResolver getResolver(IVirtualReference reference) {
		getResolvers();
		Iterator<IReferenceResolver> i = sorted.iterator();
		IReferenceResolver resolver;
		while(i.hasNext()) {
			resolver = i.next();
			if( resolver.canResolve(reference))
				return resolver;
		}
		return defaultResolver;
	}
	
	protected void loadResolvers() {
		HashMap<String, ReferenceResolverWrapper> map = new HashMap<String, ReferenceResolverWrapper>();
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ModulecorePlugin.PLUGIN_ID, "referenceResolver"); //$NON-NLS-1$
		String id = null;
		for( int j = 0; j < cf.length; j++ ) {
			id = cf[j].getAttribute("id");
			try {
				map.put(id, new ReferenceResolverWrapper(
						id, (IReferenceResolver)
							cf[j].createExecutableExtension("class"),
						cf[j].getAttribute("weight")));
			} catch( CoreException ce ) {
				// TODO figure it out
			}
		}
		resolvers = map;
		
		// Cache the sorted ones
		List<ReferenceResolverWrapper> list = new ArrayList(resolvers.values());
		Comparator comparator = new Comparator() { 
			public int compare(Object o1, Object o2) {
				if( !(o1 instanceof ReferenceResolverWrapper))
					return -1;
				if( !(o2 instanceof ReferenceResolverWrapper))
					return 1;
				return ((ReferenceResolverWrapper)o2).getWeight()
				 	- ((ReferenceResolverWrapper)o1).getWeight();
			}
		};
		
		Collections.sort(list, comparator);
		ArrayList<IReferenceResolver> sorted = new ArrayList<IReferenceResolver>();
		Iterator i = list.iterator();
		while(i.hasNext())
			sorted.add(((ReferenceResolverWrapper)i.next()).getResolver());
		this.sorted = sorted;
	}
	
	
	protected class ReferenceResolverWrapper {
		private String id;
		private IReferenceResolver resolver;
		private int weight;
		public ReferenceResolverWrapper(String id, IReferenceResolver resolver, String weight) {
			this.id = id;
			this.resolver = resolver;
			try {
				this.weight = Integer.parseInt(weight);
			} catch( NumberFormatException nfe) {
				this.weight = 1000;
			}
		}
		public int getWeight() {
			return weight;
		}
		public String getId() {
			return id;
		}
		public IReferenceResolver getResolver() {
			return resolver;
		}
	}
	
}
