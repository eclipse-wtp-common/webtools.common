/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

public class DefaultModuleHandler implements IModuleHandler {

	public String getArchiveName(IProject proj,IVirtualComponent comp) {
		if (comp != null)
			return comp.getName() + ".jar";
		return proj.getName() + ".jar";
	}

	public List<IProject> getFilteredProjectListForAdd(IVirtualComponent sourceComponent, List<IProject> availableProjects) {
		Iterator<IProject> i = availableProjects.iterator();
		IProject p;
		while(i.hasNext()) {
			p = i.next();
			if( !p.isOpen())
				i.remove();
			else if( p.equals(sourceComponent.getProject()))
				i.remove();
		}
		return availableProjects;
	}

	public boolean setComponentAttributes(IProject proj) {
		
		return true;
	}

}
