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
package org.eclipse.wst.common.componentcore.ui.propertypage.internal;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.ui.propertypage.AddModuleDependenciesPropertiesPage.ComponentResourceProxy;


/*
 *  The only valid elements this content provider (should) provide
 *  are IProject or IVirtualComponent objects. The runtime paths portion is 
 *  shared with the preference page itself where they can both modify the data. 
 * 
 * This provider no longer "meddles" in to the content as it used to, 
 * but rather serves as only a view of it. 
 */
public class ComponentDependencyContentProvider extends LabelProvider implements IStructuredContentProvider, ITableLabelProvider {
	
	final static String PATH_SEPARATOR = String.valueOf(IPath.SEPARATOR);
	
	private HashMap<IVirtualComponent, String> runtimePaths;
	private ArrayList<ComponentResourceProxy> resourceMappings;
	public ComponentDependencyContentProvider() {
		super();
	}

	public void setRuntimePaths(HashMap<IVirtualComponent, String> paths) {
		this.runtimePaths = paths;
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
		list.addAll(runtimePaths.keySet());
		return list.toArray();
	}
	
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if( element instanceof ComponentResourceProxy) {
			if( columnIndex == 0 ) 
				return ((ComponentResourceProxy)element).runtimePath.toString();
			else if( columnIndex == 1 ) 
				return ((ComponentResourceProxy)element).source.toString();
		}
		if (element instanceof IVirtualComponent) {
			IVirtualComponent comp = (IVirtualComponent)element;
			if (columnIndex == 0) {
				if( runtimePaths == null || runtimePaths.get(element) == null) {
					return new Path(PATH_SEPARATOR).toString();
				}
				return runtimePaths.get(element);
			} else if (columnIndex == 1) {
				if( comp.isBinary() && comp instanceof VirtualArchiveComponent) {
					IPath p = ((VirtualArchiveComponent)comp).getWorkspaceRelativePath();
					if( p == null )
						p = new Path(((VirtualArchiveComponent)comp).getUnderlyingDiskFile().getAbsolutePath());
					return p.toString();
				}
				return comp.getProject().getName();
			}
		} else if (element instanceof IProject){
			if (columnIndex == 0) {
				if( runtimePaths == null || runtimePaths.get(element) == null) {
					return new Path(PATH_SEPARATOR).toString();
				}
				return runtimePaths.get(element);
			} else {
				return ((IProject)element).getName();
			}
		}
		return null;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
