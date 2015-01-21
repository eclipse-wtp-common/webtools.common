/*******************************************************************************
 * Copyright (c) 2008, 2013 IBM Corporation and others.
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Validators can assert that certain resources (usually folders) are not validated. This class keeps track of those
 * assertions.
 * @author karasiuk
 *
 */
public class DisabledResourceManager implements IProjectChangeListener {
	
	public static DisabledResourceManager getDefault(){
		return Singleton.disabledResourceManager;
	}
	
	private Set<IPath> _disabled = new HashSet<IPath>(100);
	private Set<IProject>	_loadedProjects = new HashSet<IProject>(40);
	
	private DisabledResourceManager(){
		EventManager.getManager().addProjectChangeListener(this);
	}
	
	public void disableValidation(IResource resource){
		// We check for two reasons, 1) we may save some work, 2) we force the project to be loaded 
		if (isDisabled(resource))return;
		
		Set<IPath> copy = new HashSet<IPath>(_disabled.size()+2);
		copy.addAll(_disabled);
		copy.add(resource.getFullPath());
		save(copy, resource.getProject());
		_disabled = copy;
	}
	
	public void enableValidation(IResource resource){
		// We check for two reasons, 1) we may save some work, 2) we force the project to be loaded 
		if (!isDisabled(resource))return;
		
		Set<IPath> copy = new HashSet<IPath>(_disabled.size()+2);
		copy.addAll(_disabled);
		copy.remove(resource.getFullPath());
		save(copy, resource.getProject());
		_disabled = copy;		
	}
		
	private void save(Set<IPath> disabled, IProject project) {
		Serializer ser = new Serializer(200);
		String name = project.getName();
		for (IPath fullPath : disabled){
			if (name.equals(fullPath.segment(0))){
				ser.put(fullPath.removeFirstSegments(1).toPortableString());
			}
		}
		PreferencesWrapper prefs = PreferencesWrapper.getPreferences(project, null);
		prefs.put(PrefConstants.disabled, ser.toString());
		try {
			prefs.flush();
		}
		catch (BackingStoreException e){
			ValidationPlugin.getPlugin().handleException(e);
		}
	}
	
	private void load(IProject project){
		Set<IPath> copy = new HashSet<IPath>(_disabled.size()+10);
		copy.addAll(_disabled);
		PreferencesWrapper prefs = PreferencesWrapper.getPreferences(project, null);
		String disabled = prefs.get(PrefConstants.disabled, ""); //$NON-NLS-1$
		if (disabled.length() > 0){
			Deserializer des = new Deserializer(disabled);
			IPath projectPath = project.getFullPath();
			while (des.hasNext()) {
				String pathString = des.getString();
				IPath path = Path.fromPortableString(pathString);
				/*
				 * technically, devices aren't preserved during append(), but
				 * there shouldn't be any for in-workspace paths
				 */
				copy.add(projectPath.append(path));
			}
		}
		_disabled = copy;
	}

	public void dispose(){
		EventManager.getManager().removeProjectChangeListener(this);
	}
	
	/**
	 * Answer true if this resource should not be validated.
	 * 
	 * @param resource the resource that is being tested.
	 * @return true if the resource should not be validated.
	 */
	public boolean isDisabled(IResource resource){
		IProject project = resource.getProject();
		if (_loadedProjects.contains(project))return _disabled.contains(resource.getFullPath());
		return isDisabled(resource, project); 		
	}
	
	private synchronized boolean isDisabled(IResource resource, IProject project){
		if (!_loadedProjects.contains(project)){
			load(project);
			_loadedProjects.add(project);
		}
		return _disabled.contains(resource.getFullPath());
	}
	
	public void addDisabled(IResource resource){
		_disabled.add(resource.getFullPath());
	}

	public void projectChanged(IProject project, int type) {
		switch (type) {
		case IProjectChangeListener.ProjectDeleted:
		case IProjectChangeListener.ProjectClosed:
			projectRemoved(project);
			break;
			
		}
		
	}
	
	private synchronized void projectRemoved(IProject project) {
		_loadedProjects.remove(project);
		Set<IPath> copy = new HashSet<IPath>(100);
		String projectName = project.getName();
		for (IPath fullPath : _disabled){
			if (!projectName.equals(fullPath.segment(0))) copy.add(fullPath);
		}
		_disabled = copy;
	}
	
	/**
	 * Store the singleton for the DisabledResourceManager. This approach is used to avoid having to synchronize the
	 * DisabledResourceManager.getDefault() method.
	 * 
	 * @author karasiuk
	 *
	 */
	private static class Singleton {
		static DisabledResourceManager disabledResourceManager = new DisabledResourceManager();
	}


}
