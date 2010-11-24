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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.common.componentcore.ui.ModuleCoreUIPlugin;
import org.eclipse.wst.common.componentcore.ui.propertypage.AddModuleDependenciesPropertiesPage;
import org.eclipse.wst.common.componentcore.ui.propertypage.AddModuleDependenciesPropertiesPage.ComponentResourceProxy;


/*
 *  The only valid elements this content provider (should) provide
 *  are IProject or IVirtualComponent objects. The runtime paths portion is 
 *  shared with the preference page itself where they can both modify the data. 
 * 
 * This provider no longer "meddles" in to the content as it used to, 
 * but rather serves as only a view of it. 
 */
public class ComponentDependencyContentProvider extends LabelProvider implements IStructuredContentProvider, ITableLabelProvider, ITreeContentProvider {
	
	final static String PATH_SEPARATOR = String.valueOf(IPath.SEPARATOR);
	
	private IVirtualComponent component;
	private ArrayList<IVirtualReference> runtimePaths;
	private ArrayList<ComponentResourceProxy> resourceMappings;
	private DecoratingLabelProvider decProvider = new DecoratingLabelProvider(
	                new WorkbenchLabelProvider(), PlatformUI.getWorkbench().
	                 getDecoratorManager().getLabelDecorator());
	private IVirtualComponentLabelProvider[] delegates;
	public ComponentDependencyContentProvider(AddModuleDependenciesPropertiesPage addModuleDependenciesPropertiesPage) {
		super();
		decProvider.addListener(addModuleDependenciesPropertiesPage);
		delegates = DependencyPageExtensionManager.loadDelegates();
	}

	public void setRuntimePaths(ArrayList<IVirtualReference> runtimePaths) {
		this.runtimePaths = runtimePaths;
	}

	public void setResourceMappings(ArrayList<ComponentResourceProxy> mappings) {
		this.resourceMappings = mappings;
	}
	
	public Object[] getElements(Object inputElement) {
		Object[] empty = new Object[0];
		if( !(inputElement instanceof IWorkspaceRoot))
			return empty;
		ArrayList<Object> list = new ArrayList<Object>();
		list.addAll(resourceMappings);
		list.addAll(runtimePaths);
		return list.toArray();
	}
	
	public Image getColumnImage(Object element, int columnIndex) {
		if( element instanceof ComponentResourceProxy) {
			return ModuleCoreUIPlugin.getInstance().getImage("folder");
		}
		if (element instanceof IVirtualReference) {
			if( columnIndex == AddModuleDependenciesPropertiesPage.SOURCE_COLUMN ){
				return handleSourceImage(((IVirtualReference)element).getReferencedComponent());
			} else if(columnIndex == AddModuleDependenciesPropertiesPage.DEPLOY_COLUMN){
				return ModuleCoreUIPlugin.getInstance().getImage("jar_obj");
			}
		} 
		if (element instanceof IProject){
			return decProvider.getImage(element);
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if( element instanceof ComponentResourceProxy) {
			if( columnIndex == AddModuleDependenciesPropertiesPage.SOURCE_COLUMN ) { 
				return ((ComponentResourceProxy)element).source.toString();
			} else if( columnIndex == AddModuleDependenciesPropertiesPage.DEPLOY_COLUMN ) {
				if(((ComponentResourceProxy)element).runtimePath.isRoot())
					return ((ComponentResourceProxy)element).runtimePath.toString();
				else
					return ((ComponentResourceProxy)element).runtimePath.makeRelative().toString();
			}
		}
		if( element instanceof IVirtualReference) {
			if (columnIndex == AddModuleDependenciesPropertiesPage.SOURCE_COLUMN) {
				return handleSourceText(((IVirtualReference)element).getReferencedComponent());
			} else if (columnIndex == AddModuleDependenciesPropertiesPage.DEPLOY_COLUMN) {
				return new Path(AddModuleDependenciesPropertiesPage.getSafeRuntimePath((IVirtualReference)element)).makeRelative().toString();
			} 
		}
		return null;
	}

	
	private String handleSourceText(IVirtualComponent component) {
		if( delegates != null ) {
			for( int i = 0; i < delegates.length; i++ )
				if( delegates[i].canHandle(component))
					return delegates[i].getSourceText(component);
		}
		
		// default impl
		if( component.isBinary() ) {
			IPath p = (IPath)component.getAdapter(IPath.class);
			return p == null ? null : p.toString();
		}
		return component.getProject().getName();
	}

	private Image handleSourceImage(IVirtualComponent component) {
		if( delegates != null ) {
			for( int i = 0; i < delegates.length; i++ )
				if( delegates[i].canHandle(component))
					return delegates[i].getSourceImage(component);
		}
		
		// default impl
		if(component.isBinary())
			return ModuleCoreUIPlugin.getInstance().getImage("jar_obj");
		else return decProvider.getImage(component.getProject());
	}
	
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	public void setComponent(IVirtualComponent component) {
		this.component = component;
	}

	public IVirtualComponent getComponent(){
		return component;
	}

	public Object[] getChildren(Object parentElement) {
		return null;
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		return false;
	}
	
}
