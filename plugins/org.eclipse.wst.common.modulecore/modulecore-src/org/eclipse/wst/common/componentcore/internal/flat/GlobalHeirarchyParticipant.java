/*******************************************************************************
 * Copyright (c) 2013 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Red Hat - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.flat;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.internal.flat.FlatVirtualComponent.FlatComponentTaskModel;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

/**
 * This class will make use of the heirarchyFlattenParticipant extension point
 * to discover all globally-registered heirarchy participants and provide them 
 * in one clean participant. This allows a project / module (such as an EAR project)
 * to accept child module nestings it may otherwise be unable to know about. 
 */
public class GlobalHeirarchyParticipant extends AbstractFlattenParticipant {
	private ArrayList<IFlattenParticipant> list = null;
	
	@Override
	public boolean isChildModule(IVirtualComponent rootComponent,
			IVirtualReference reference, FlatComponentTaskModel dataModel) {
		ensureLoaded();
		Iterator<IFlattenParticipant> it = list.iterator();
		while(it.hasNext() ) {
			if( it.next().isChildModule(rootComponent, reference, dataModel))
				return true;
		}
		return false;
	}

	@Override
	public boolean isChildModule(IVirtualComponent rootComponent, FlatComponentTaskModel dataModel, IFlatFile file) {
		ensureLoaded();
		Iterator<IFlattenParticipant> it = list.iterator();
		while(it.hasNext() ) {
			if( it.next().isChildModule(rootComponent, dataModel, file))
				return true;
		}
		return false;
	}
	
	private synchronized void ensureLoaded() {
		if( list != null ) 
			return;
		
		list = new ArrayList<IFlattenParticipant>();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf2 = registry.getConfigurationElementsFor(ModulecorePlugin.PLUGIN_ID, "heirarchyFlattenParticipant"); //$NON-NLS-1$
		for( int i = 0; i < cf2.length; i++ ) {
			String clazz = cf2[i].getAttribute("class");
			try {
				IFlattenParticipant o = (IFlattenParticipant)cf2[i].createExecutableExtension("class");
				if( o != null )
					list.add(o);
				else {
					ModulecorePlugin.log(IStatus.WARNING, 0, 
							"Unable to create global heirarchy participant " + clazz, null);
				}
			} catch(CoreException ce) {
				ModulecorePlugin.log(IStatus.WARNING, 0, 
						"Unable to create global heirarchy participant " + clazz, ce);
			}
		}
	}

}
