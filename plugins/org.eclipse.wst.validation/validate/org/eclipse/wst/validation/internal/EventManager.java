/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
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
import java.util.logging.Level;

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
import org.eclipse.jem.util.UIContextDetermination;
import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.validation.internal.operations.IWorkbenchHelper;
import org.eclipse.wst.validation.plugin.ValidationPlugin;

/**
 * This class manages resource change events for the validation framework.
 */
public class EventManager implements IResourceChangeListener {
	private static EventManager _inst = null;
	private boolean _shutdown = false; // false means that eclipse is not shutting down, and true
	// means that it is shutting down. Used in two methods:
	// shutdown(),and resourceChanged(IResourceChangeEvent)
	private IResourceDeltaVisitor _postAutoBuildVisitor = null;
	private boolean _isActive = false; // has the registry been read?

	private EventManager() {
		super();
	}

	public static EventManager getManager() {
		if (_inst == null) {
			_inst = new EventManager();
		}
		return _inst;
	}

	public void opening(IProject project) {
		if (project == null || !ValidationPlugin.isActivated()) {
			return;
		}

		// When the project is opened, check for any orphaned tasks
		// or tasks whose owners need to be updated.
		ConfigurationManager.getManager().opening(project);
	}

	public void closing(IProject project) {
		if (project == null || !ValidationPlugin.isActivated()) {
			return;
		}

		try {
			boolean isMigrated = ConfigurationManager.getManager().isMigrated(project);
			// If it's not migrated, then it hasn't been loaded, and we don't want to load the
			// validator and its prerequisite plugins until they're needed.
			if (isMigrated) {
				ValidatorMetaData[] vmds = ConfigurationManager.getManager().getProjectConfiguration(project).getValidators();
				for (int i = 0; i < vmds.length; i++) {
					ValidatorMetaData vmd = vmds[i];

					if (!vmd.isActive()) {
						// If this validator has not been activated, or if it has been shut down,
						// don't activate it again.
						continue;
					}

					IWorkbenchHelper helper = null;
					try {
						helper = vmd.getHelper(project);
						helper.closing();
					} catch (InstantiationException exc) {
						// Remove the vmd from the reader's list
						ValidationRegistryReader.getReader().disableValidator(vmd);

						// Log the reason for the disabled validator
						Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
						if (logger.isLoggingLevel(Level.SEVERE)) {
							LogEntry entry = ValidationPlugin.getLogEntry();
							entry.setSourceID("EventManager::closing(IProject)"); //$NON-NLS-1$
							entry.setTargetException(exc);
							logger.write(Level.SEVERE, entry);
						}

						continue;
					} catch (Throwable exc) {
						// If there is a problem with this particular helper, log the error and
						// continue
						// with the next validator.
						Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
						if (logger.isLoggingLevel(Level.SEVERE)) {
							LogEntry entry = ValidationPlugin.getLogEntry();
							entry.setSourceID("EventManager::closing(IProject)"); //$NON-NLS-1$
							entry.setTargetException(exc);
							logger.write(Level.SEVERE, entry);
						}
						continue;
					}
				}

				ConfigurationManager.getManager().closing(project);
			}
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("EventManager::closing(" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
		}
	}

	public void deleting(IProject project) {
		if (project == null) {
			return;
		}

		try {
			boolean isMigrated = ConfigurationManager.getManager().isMigrated(project);
			// If it's not migrated, then it hasn't been loaded, and we don't want to load the
			// validator and its prerequisite plugins until they're needed.
			if (isMigrated) {
				ValidatorMetaData[] vmds = ConfigurationManager.getManager().getProjectConfiguration(project).getValidators();
				for (int i = 0; i < vmds.length; i++) {
					ValidatorMetaData vmd = vmds[i];

					if (!vmd.isActive()) {
						// If this validator has not been activated, or if it has been shut down,
						// don't activate it again.
						continue;
					}

					IWorkbenchHelper helper = null;
					try {
						helper = vmd.getHelper(project);
						helper.deleting();
					} catch (InstantiationException exc) {
						// Remove the vmd from the reader's list
						ValidationRegistryReader.getReader().disableValidator(vmd);

						// Log the reason for the disabled validator
						Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
						if (logger.isLoggingLevel(Level.SEVERE)) {
							LogEntry entry = ValidationPlugin.getLogEntry();
							entry.setSourceID("EventManager::deleting(IProject)"); //$NON-NLS-1$
							entry.setTargetException(exc);
							logger.write(Level.SEVERE, entry);
						}

						continue;
					} catch (Throwable exc) {
						// If there is a problem with this particular helper, log the error and
						// continue
						// with the next validator.
						Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
						if (logger.isLoggingLevel(Level.SEVERE)) {
							LogEntry entry = ValidationPlugin.getLogEntry();
							entry.setSourceID("EventManager::deleting(IProject)"); //$NON-NLS-1$
							entry.setTargetException(exc);
							logger.write(Level.SEVERE, entry);
						}
						continue;
					}
				}

				ConfigurationManager.getManager().deleting(project);
			}
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("EventManager::deleting(" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
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
					if (subdelta == null)
						return true;

					IResource resource = subdelta.getResource();
					if (resource instanceof IProject) {
						IProject project = (IProject) resource;
						if ((subdelta.getFlags() & IResourceDelta.DESCRIPTION) == IResourceDelta.DESCRIPTION) {
							try {
								ConfigurationManager.getManager().resetProjectNature(project); // flush
								// existing
								// "enabled
								// validator"
								// settings
								// and
								// reset
								// to
								// default
							} catch (InvocationTargetException exc) {
								Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
								if (logger.isLoggingLevel(Level.SEVERE)) {
									LogEntry entry = ValidationPlugin.getLogEntry();
									entry.setSourceIdentifier("EventManager::postAutoChange"); //$NON-NLS-1$
									entry.setTargetException(exc);
									logger.write(Level.SEVERE, entry);

									if (exc.getTargetException() != null) {
										entry.setTargetException(exc);
										logger.write(Level.SEVERE, entry);
									}
								}
							}
							return false;
						}

						if ((subdelta.getFlags() & IResourceDelta.OPEN) == IResourceDelta.OPEN) {
							if (project.isOpen()) {
								// Project was just opened. If project.isOpen() had returned false,
								// project
								// would just have been closed.
								opening(project);
							}
							// closing is called by PRE_CLOSE in resourceChanged
							//							else {
							//								closing(project);
							//							}
						}
					}

					return true;
				}
			};
		}

		try {
			delta.accept(_postAutoBuildVisitor, true);
		} catch (CoreException exc) {
			exc.printStackTrace();
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
		if (_shutdown && (!isActive())) {
			// If we're shutting down, and nothing has been activated, don't need to
			// do anything.
			return;
		}

		/*
		 * StringBuffer buffer = new StringBuffer(); buffer.append("IResourceChangeEvent type = ");
		 * buffer.append(event.getType()); buffer.append(", resource = ");
		 * buffer.append(event.getResource()); buffer.append(", source = ");
		 * buffer.append(event.getSource()); buffer.append(", delta = ");
		 * buffer.append(event.getDelta()); System.out.println(buffer.toString());
		 */

		if (event.getSource() instanceof IWorkspace) {
			if ((event.getType() == IResourceChangeEvent.PRE_DELETE) && (event.getResource() instanceof IProject)) {
				deleting((IProject) event.getResource());
			} else if ((event.getType() == IResourceChangeEvent.PRE_CLOSE) && (event.getResource() instanceof IProject)) {
				closing((IProject) event.getResource());
			} else if (event.getType() == IResourceChangeEvent.POST_BUILD) {
				postAutoChange(event.getDelta());
			}

		}
	}

	/**
	 * Notifies this manager that the ValidationPlugin is shutting down. (Usually implies that
	 * either the plugin could not load, or that the workbench is shutting down.)
	 * 
	 * The manager will then notify all active helpers of the shutdown, so that they may perform any
	 * last-minute writes to disk, cleanup, etc.
	 */
	public void shutdown() {
		try {
			_shutdown = true; // resourceChanged(IResourceChangeEvent) needs to know when a shutdown
			// has started.

			/*
			 * if( !isHeadless() && ConfigurationManager.getManager().isGlobalMigrated()) {
			 * GlobalConfiguration gp = ConfigurationManager.getManager().getGlobalConfiguration();
			 * gp.store(); // First, see if any validators are loaded. If none are, there is nothing
			 * to // clean up. if(gp.numberOfValidators() == 0) { return; } }
			 */

			// If the validators are loaded, then for every project in the workbench,
			// we must see if it has been loaded. If it has, every enabled IWorkbenchHelper
			// must be called to clean up. If the project hasn't been loaded, then no
			// IWorkbenchHelper built anything, and there's nothing to clean up.
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
					// the
					// validator and its prerequisite plugins until they're needed.
					if (isMigrated) {
						prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
						prjp.store();

						ValidatorMetaData[] vmdList = prjp.getEnabledValidators();
						// if vmdList is null, IProject has never been loaded, so nothing to clean
						// up
						if (vmdList != null) {
							for (int j = 0; j < vmdList.length; j++) {
								ValidatorMetaData vmd = vmdList[j];

								if (!vmd.isActive()) {
									// If this validator has not been activated, or if it has been
									// shut down, don't activate it again.
									continue;
								}

								IWorkbenchHelper helper = vmd.getHelper(project);
								if (helper != null) {
									try {
										helper.shutdown();
									} catch (Throwable exc) {
										// Since we're shutting down, ignore the exception.
									}
								}
							}
						}
					}
				} catch (InvocationTargetException exc) {
					Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
					if (logger.isLoggingLevel(Level.SEVERE)) {
						LogEntry entry = ValidationPlugin.getLogEntry();
						entry.setSourceIdentifier("EventManager::shutdown(" + project.getName() + ")"); //$NON-NLS-1$  //$NON-NLS-2$
						entry.setTargetException(exc);
						logger.write(Level.SEVERE, entry);

						if (exc.getTargetException() != null) {
							entry.setTargetException(exc);
							logger.write(Level.SEVERE, entry);
						}
					}
				}
			}
		} catch (Throwable exc) {
			// Since we're shutting down, ignore the exception.
		}
	}

	public boolean isActive() {
		// Have to use this convoluted technique for the shutdown problem.
		// i.e., when eclipse is shut down, if validation plugin hasn't been loaded,
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
	 * This method should be used to determine if the workbench is running in UI or Headless
	 */
	public static boolean isHeadless() {
		boolean ret = UIContextDetermination.getCurrentContext() == UIContextDetermination.HEADLESS_CONTEXT;
		return ret;
		//return UIContextDetermination.getCurrentContext() ==
		// UIContextDetermination.HEADLESS_CONTEXT;
	}
}