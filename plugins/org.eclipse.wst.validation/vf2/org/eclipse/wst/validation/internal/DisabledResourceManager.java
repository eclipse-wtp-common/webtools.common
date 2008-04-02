/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

/**
 * Validators can assert that certain resources (usually folders) are not validated. This class keeps track of those
 * assertions.
 * @author karasiuk
 *
 */
public class DisabledResourceManager implements IProjectChangeListener {
	
	private static DisabledResourceManager _me;
	
	public static DisabledResourceManager getDefault(){
		if (_me == null)_me = new DisabledResourceManager();
		return _me;
	}
	
	private Set<IResource> _disabled = new HashSet<IResource>(100);
	
	private DisabledResourceManager(){
		EventManager.getManager().addProjectChangeListener(this);
	}
	
	public void dispose(){
		EventManager.getManager().removeProjectChangeListener(this);
	}
	
	/**
	 * Answer true if this resource and any of it's children should not be validated.
	 * 
	 * @param resource the resource that is being tested.
	 * @return true if the resource should not be validated.
	 */
	public boolean isDisabled(IResource resource){
		return !_disabled.contains(resource);
	}
	
	public void addDisabled(IResource resource){
		_disabled.add(resource);
	}

	public void projectChanged(IProject project, int type) {
		switch (type) {
		case IProjectChangeListener.ProjectDeleted:
		case IProjectChangeListener.ProjectClosed:
			projectRemoved(project);
			break;
			
		}
		
	}
	
	private void projectRemoved(IProject project) {
		Set<IResource> copy = new HashSet<IResource>(100);
		for (IResource resource : _disabled){
			if (resource.getProject() != project)copy.add(resource);
		}
		_disabled = copy;
	}

}
