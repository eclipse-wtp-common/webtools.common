/*******************************************************************************
 * Copyright (c) 2003, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.impl;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.util.IPathProvider;
import org.eclipse.wst.common.internal.emf.utilities.ExtendedEcoreUtil;

public class ResourceTreeRoot extends ResourceTreeNode {
	
	private class ResourceTreeResourceListener extends AdapterImpl {
		public void notifyChanged(Notification msg) {
			
			if(msg.getFeature() == ComponentcorePackage.eINSTANCE.getWorkbenchComponent_Resources()) {
				ComponentResource resource = null;
				switch(msg.getEventType()) {
				case Notification.ADD:
					resource = (ComponentResource) msg.getNewValue();
					if(resource != null)
						resource.eAdapters().add(getResourcePathListenerAdapter());					
					break; 
				case Notification.ADD_MANY:
					List newValues = (List) msg.getNewValue();					
					for (int i = 0; i < newValues.size(); i++) {
						resource = (ComponentResource) newValues.get(i);
						resource.eAdapters().add(getResourcePathListenerAdapter());
					}
					break;
				case Notification.REMOVE:
					resource = (ComponentResource) msg.getOldValue();
					if(resource != null){
						resource.eAdapters().remove(getResourcePathListenerAdapter());
						removeChild(resource);
					}
					break;
				case Notification.REMOVE_MANY:
					List removedValues = (List) msg.getOldValue();
					if (removedValues != null) {
						for (int i = 0; i < removedValues.size(); i++) {
							resource = (ComponentResource) removedValues.get(i);
							resource.eAdapters().remove(getResourcePathListenerAdapter());
							removeChild(resource);
						}
					}
					break;
				}
			}
		}
	}
	
	private class ResourcePathListener extends AdapterImpl {
		
		public void setTarget(Notifier newTarget) {
			if(newTarget instanceof ComponentResource) {				
//				if(getTarget() != null) {
//					if(getPathProvider().getPath((ComponentResource)getTarget()) != null)
//						removeChild((ComponentResource)getTarget());
//				}
				ComponentResource resource = (ComponentResource) newTarget;
				if(resource != null) {
					if(getPathProvider().getPath(resource) != null)
						addChild(resource);					
				}		
			}
			super.setTarget(newTarget);
		}
		
		public void notifyChanged(Notification msg) {
			
			if(msg.getFeature() == getPathProvider().getFeature()) {
				ComponentResource resource = (ComponentResource) msg.getNotifier();
				switch(msg.getEventType()) {
				case Notification.SET:
					
					// remove the old value 
					IPath oldPath = (IPath)msg.getOldValue();
					if(oldPath!=null)
						removeChild(oldPath, resource);
					
					IPath newPath = (IPath)msg.getNewValue();
					if(newPath!=null)
						addChild(resource);
					break; 
				}
			}
		}
	}

	// TODO The source tree should be attached to the project modules root, not each module.
	public static ResourceTreeRoot getSourceResourceTreeRoot(WorkbenchComponent aModule) {
		ResourceTreeRootAdapter resourceTreeAdapter = (ResourceTreeRootAdapter) ExtendedEcoreUtil.getAdapter(aModule, aModule.eAdapters(), ResourceTreeRootAdapter.SOURCE_ADAPTER_TYPE);
		if (resourceTreeAdapter != null)
			return resourceTreeAdapter.getResourceTreeRoot();
		resourceTreeAdapter = new ResourceTreeRootAdapter(ResourceTreeRootAdapter.SOURCE_TREE);
		aModule.eAdapters().add(resourceTreeAdapter);
		return resourceTreeAdapter.getResourceTreeRoot();
	}

	public static ResourceTreeRoot getDeployResourceTreeRoot(WorkbenchComponent aModule) {
		ResourceTreeRootAdapter resourceTreeAdapter = (ResourceTreeRootAdapter) ExtendedEcoreUtil.getAdapter(aModule, aModule.eAdapters(), ResourceTreeRootAdapter.DEPLOY_ADAPTER_TYPE);
		if (resourceTreeAdapter != null)
			return resourceTreeAdapter.getResourceTreeRoot();
		resourceTreeAdapter = new ResourceTreeRootAdapter(ResourceTreeRootAdapter.DEPLOY_TREE);
		aModule.eAdapters().add(resourceTreeAdapter);
		return resourceTreeAdapter.getResourceTreeRoot();
	}
	
	private final WorkbenchComponent module;
	private ResourceTreeResourceListener listener;
	private ResourcePathListener pathListener;

	public ResourceTreeRoot(WorkbenchComponent aModule, IPathProvider aPathProvider) {
		super("/", null, aPathProvider); //$NON-NLS-1$
		module = aModule; 	
		init();
	}

	private void init() {
		module.eAdapters().add(getResourceTreeListenerAdapter());
		List moduleResources = module.getResources();
		ComponentResource moduleResource = null;		 
		
		for (int i = 0; i < moduleResources.size(); i++) {
			moduleResource = (ComponentResource) moduleResources.get(i);
			addChild(moduleResource);
		}
		
	}

	protected ResourceTreeResourceListener getResourceTreeListenerAdapter() {
		if(listener == null)
			listener = new ResourceTreeResourceListener();
		return listener;
	}

	protected ResourcePathListener getResourcePathListenerAdapter() {
		if(pathListener == null)
			pathListener = new ResourcePathListener();
		return pathListener;
	}

	public ComponentResource[] findModuleResources(URI aURI) {
		IPath path = new Path(aURI.toString());
		try {
			if (ModuleURIUtil.ensureValidFullyQualifiedPlatformURI(aURI, false))
				path = path.removeFirstSegments(1); 
		} catch (UnresolveableURIException uurie) {

		}
		return findModuleResources(path, ResourceTreeNode.CREATE_NONE);
	}

}
