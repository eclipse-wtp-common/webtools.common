/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.validation.internal.operations.IWorkbenchContext;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * This class manages resource change events for the validation framework.
 */
public class EventManager implements IResourceChangeListener {
	private static EventManager _inst;
	
	// false means that eclipse is not shutting down, and true means that it is shutting down. 
	private boolean _shutdown; 

	private IResourceDeltaVisitor _postAutoBuildVisitor;
	private boolean _isActive; // has the registry been read?
	
	private Set<IProjectChangeListener> _listeners = new HashSet<IProjectChangeListener>(4);

	private EventManager() {
	}

	public static EventManager getManager() {
		if (_inst == null)_inst = new EventManager();
		return _inst;
	}
	
	public void addProjectChangeListener(IProjectChangeListener listener){
		_listeners.add(listener);
	}
	
	public void removeProjectChangeListener(IProjectChangeListener listener){
		_listeners.remove(listener);
	}
	
	private void signal(IProject project, int type){
		for (IProjectChangeListener pcl : _listeners){
			try {
				pcl.projectChanged(project, type);
			}
			catch (Exception e){
				ValidationPlugin.getPlugin().handleException(e);
			}
		}
	}

	public void opening(IProject project) {
		if (project == null || !ValidationPlugin.isActivated())return;
		
		signal(project, IProjectChangeListener.ProjectOpened);

		// When the project is opened, check for any orphaned tasks or tasks whose owners need to be updated.
		ConfigurationManager.getManager().opening(project);
	}

	public void closing(IProject project) {
		if (project == null || !ValidationPlugin.isActivated())return;
		
		signal(project, IProjectChangeListener.ProjectClosed);
		
		try {
			boolean isMigrated = ConfigurationManager.getManager().isMigrated(project);
			// If it's not migrated, then it hasn't been loaded, and we don't want to load the
			// validator and its prerequisite plug-ins until they're needed.
			if (isMigrated) {
				ValidatorMetaData[] vmds = ConfigurationManager.getManager().getProjectConfiguration(project).getValidators();
				for (ValidatorMetaData vmd : vmds) {

					if (!vmd.isActive()) {
						// If this validator has not been activated, or if it has been shut down,
						// don't activate it again.
						continue;
					}

					IWorkbenchContext helper = null;
					try {
						helper = vmd.getHelper(project);
						helper.closing();
					} catch (InstantiationException e) {
						// Remove the vmd from the reader's list
						ValidationRegistryReader.getReader().disableValidator(vmd);

						ValidationPlugin.getPlugin().handleException(e);
					} catch (Exception e) {
						// If there is a problem with this particular helper, log the error and
						// continue with the next validator.
						ValidationPlugin.getPlugin().handleException(e);
					}
				}

				ConfigurationManager.getManager().closing(project);
			}
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			if (e.getTargetException() != null)
				ValidationPlugin.getPlugin().handleException(e.getTargetException());
		}
	}

	public void deleting(IProject project) {
		if (project == null)return;
		
		signal(project, IProjectChangeListener.ProjectDeleted);

		try {
			boolean isMigrated = ConfigurationManager.getManager().isMigrated(project);
			// If it's not migrated, then it hasn't been loaded, and we don't want to load the
			// validator and its prerequisite plug-ins until they're needed.
			if (isMigrated) {
				ValidatorMetaData[] vmds = ConfigurationManager.getManager().getProjectConfiguration(project).getValidators();
				for (ValidatorMetaData vmd : vmds) {

					if (!vmd.isActive()) {
						// If this validator has not been activated, or if it has been shut down,
						// don't activate it again.
						continue;
					}

					IWorkbenchContext helper = null;
					try {
						helper = vmd.getHelper(project);
						helper.deleting();
					} catch (InstantiationException e) {
						// Remove the vmd from the reader's list
						ValidationRegistryReader.getReader().disableValidator(vmd);
						ValidationPlugin.getPlugin().handleException(e);
						continue;
					} catch (Exception e) {
						// If there is a problem with this particular helper, log the error and
						// continue with the next validator.
						ValidationPlugin.getPlugin().handleException(e);
						continue;
					}
				}

				ConfigurationManager.getManager().deleting(project);
			}
		} catch (InvocationTargetException e) {
			ValidationPlugin.getPlugin().handleException(e);
			if (e.getTargetException() != null)
				ValidationPlugin.getPlugin().handleException(e.getTargetException());

		}
	}

	/**
	 * If a project's description changes, The project may have changed its nature. Update the cache
	 * to reflect the new natures. The project could be opening. Migrate.
	 */
	private void postAutoChange(IResourceDelta delta) {
		if (_postAutoBuildVisitor == null) {
			_postAutoBuildVisitor = new IResourceDeltaVisitor() {
				public boolean visit(IResourceDelta subdelta) throws CoreException {
					if (subdelta == null)return false;

					IResource resource = subdelta.getResource();
					if (resource instanceof IProject) {
						IProject project = (IProject) resource;
						if ((subdelta.getFlags() & IResourceDelta.DESCRIPTION) == IResourceDelta.DESCRIPTION) {
							signal(project, IProjectChangeListener.ProjectChanged);
							return false;
						}

						if ((subdelta.getFlags() & IResourceDelta.OPEN) == IResourceDelta.OPEN) {
							if (project.isOpen()) {
								// Project was just opened. If project.isOpen() had returned false,
								// project would just have been closed.
								opening(project);
							}
						}
					}

					return false;
				}
			};
		}

		try {
			delta.accept(_postAutoBuildVisitor, true);
		} catch (CoreException exc) {
			ValidationPlugin.getPlugin().handleException(exc);
		}
	}

	/**
	 * Notifies this manager that some resource changes have happened on the platform. If the change
	 * is a project deletion, that project should be removed from the cache.
	 * 
	 * @see IResourceDelta
	 * @see IResource
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		if (_shutdown && !isActive()) {
			// If we're shutting down, and nothing has been activated, don't need to do anything.
			return;
		}

		if (Tracing.isLogging()){
			Tracing.log("Eventmanager-01: IResourceChangeEvent type = " + //$NON-NLS-1$
				Misc.resourceChangeEventType(event.getType()) + 
				", resource = " +  //$NON-NLS-1$
				event.getResource() + ", source = " + event.getSource() + ", delta = " +   //$NON-NLS-1$//$NON-NLS-2$
				event.getDelta());				
		}
		
		if (event.getSource() instanceof IWorkspace) {
			boolean isProject = event.getResource() instanceof IProject;
			if ((event.getType() == IResourceChangeEvent.PRE_DELETE) && isProject) {
				deleting((IProject) event.getResource());
			} else if ((event.getType() == IResourceChangeEvent.PRE_CLOSE) && isProject) {
				closing((IProject) event.getResource());
			} else if (event.getType() == IResourceChangeEvent.POST_BUILD) {
				postAutoChange(event.getDelta());
			}
		}
	}

	/**
	 * Notifies this manager that the ValidationPlugin is shutting down. (Usually implies that
	 * either the plug-in could not load, or that the workbench is shutting down.)
	 * <p>
	 * The manager will then notify all active helpers of the shutdown, so that they may perform any
	 * last-minute writes to disk, cleanup, etc.
	 */
	public void shutdown() {
		try {
			// resourceChanged(IResourceChangeEvent) needs to know when a shutdown has started.
			_shutdown = true;

			// If the validators are loaded, then for every project in the workbench,
			// we must see if it has been loaded. If it has, every enabled IWorkbenchContext
			// must be called to clean up. If the project hasn't been loaded, then no
			// IWorkbenchContext built anything, and there's nothing to clean up.
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot workspaceRoot = workspace.getRoot();
			IProject[] projects = workspaceRoot.getProjects();
			ProjectConfiguration prjp = null;
			IProject project = null;
			for (int i = 0; i < projects.length; i++) {
				project = projects[i];
				if (!project.isOpen()) {
					// If the project isn't opened, there's nothing to clean up.
					// If the project was opened, it would have been migrated, and there's something
					// to clean up.
					continue;
				}

				try {
					boolean isMigrated = ConfigurationManager.getManager().isMigrated(project);
					// If it's not migrated, then it hasn't been loaded, and we don't want to load
					// the validator and its prerequisite plug-ins until they're needed.
					if (isMigrated) {
						prjp = ConfigurationManager.getManager().getProjectConfiguration(project);

						ValidatorMetaData[] vmdList = prjp.getEnabledValidators();
						// if vmdList is null, IProject has never been loaded, so nothing to clean up
						if (vmdList != null) {
							for (int j = 0; j < vmdList.length; j++) {
								ValidatorMetaData vmd = vmdList[j];

								if (!vmd.isActive()) {
									// If this validator has not been activated, or if it has been
									// shut down, don't activate it again.
									continue;
								}

								IWorkbenchContext helper = vmd.getHelper(project);
								if (helper != null) {
									try {
										helper.shutdown();
									} catch (Exception exc) {
										// Since we're shutting down, ignore the exception.
									}
								}
							}
						}
					}
				} catch (InvocationTargetException e) {
					ValidationPlugin.getPlugin().handleException(e);
					if (e.getTargetException() != null)
						ValidationPlugin.getPlugin().handleException(e.getTargetException());

				}
			}
		} catch (Exception exc) {
			// Since we're shutting down, ignore the exception.
		}
	}

	public boolean isActive() {
		// Have to use this convoluted technique for the shutdown problem.
		// i.e., when eclipse is shut down, if validation plug-in hasn't been loaded,
		// the EventManager is activated for the first time, and it
		// sends many exceptions to the .log. At first, I wrote a
		// static method on ValidationRegistryReader, which returned true
		// if the registry had been read, and false otherwise. However,
		// that didn't solve the exception problem, because eclipse's
		// class loader failed to load the ValidationRegistryReader class.
		//
		// The fix is to keep all shutdown mechanisms in this class.
		// Track everything in here.
		return _isActive;
	}

	/**
	 * This method should only be called by the ValidationRegistryReader once the registry has been
	 * read.
	 */
	public void setActive(boolean b) {
		_isActive = b;
	}

	/**
	 * This method should be used to determine if the workbench is running in UI or Headless.
	 * 
	 * @deprecated This plug-in no longer depends on jem. If you need this function use the jem
	 * code directly.
	 */
	public static boolean isHeadless() {
		//return UIContextDetermination.getCurrentContext() == UIContextDetermination.HEADLESS_CONTEXT;
		return false;
	}
}
