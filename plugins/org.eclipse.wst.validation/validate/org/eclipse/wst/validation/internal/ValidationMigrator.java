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
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.internal.core.SeverityEnum;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

import com.ibm.wtp.common.logger.LogEntry;
import com.ibm.wtp.common.logger.proxy.Logger;

/**
 * This class migrates the following: 1. if a validator class name changes, its old messages must be
 * updated to use the new class name
 * 
 * Also, if a validator is not installed any more, its old messages are removed by this class'
 * removeOrphanedTasks() method.
 */
public final class ValidationMigrator implements ConfigurationConstants {
	private static ValidationMigrator _inst = null;
	private final static IMarker[] NO_MARKERS = new IMarker[0];
	private IWorkspaceRunnable _workspaceMigrator = null;
	private IWorkspaceRunnable _projectMigrator = null;

	// This interface is needed so that migration of an IProject can be done inside
	// an IWorkspaceRunnable (i.e., one build instead of many).
	interface ProjectMigrator extends IWorkspaceRunnable {
		public void setProject(IProject project);

		public IProject getProject();
	}


	private ValidationMigrator() {
	}

	public static ValidationMigrator singleton() {
		if (_inst == null) {
			_inst = new ValidationMigrator();
		}
		return _inst;
	}

	private IWorkspaceRunnable getGlobalMigrator() {
		if (_workspaceMigrator == null) {
			_workspaceMigrator = new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) {
					try {
						// Whether the workspace has been migrated or not, check for orphan markers
						// and remove them.
						IWorkspaceRoot root = TaskListUtility.getRoot();
						removeOrphanTasks(monitor, root);

						GlobalConfiguration gp = ConfigurationManager.getManager().getGlobalConfiguration();
						if (gp.isVersionCurrent()) {
							// Workspace has already been migrated. Don't re-migrate the workspace,
							// and don't re-migrate the projects in the workspace.
							//    1. Since this is the current version, all open projects have already
							// been migrated once.
							//    2. Any project that isn't open can't be migrated
							//    3. Any project that was opened after the workspace was migrated is
							// itself migrated on the "OPEN" notification
							return;
						}

						// Do not migrate all of the validators or the projects; let the project
						// migration take care of that step once validation has been awoken on
						// the project.

						// Once all of the migration is complete, mark the preference as current.
						gp.markVersionCurrent();
					} catch (InvocationTargetException exc) {
						Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
						if (logger.isLoggingLevel(Level.SEVERE)) {
							LogEntry entry = ValidationPlugin.getLogEntry();
							entry.setSourceIdentifier("ValidationMigrator.migrate"); //$NON-NLS-1$
							entry.setTargetException(exc);
							logger.write(Level.SEVERE, entry);

							if (exc.getTargetException() != null) {
								entry.setTargetException(exc.getTargetException());
								logger.write(Level.SEVERE, entry);
							}
						}
					}
				}
			};
		}
		return _workspaceMigrator;
	}

	private IWorkspaceRunnable getProjectMigrator(IProject project) {
		if (_projectMigrator == null) {
			_projectMigrator = new ProjectMigrator() {
				private IProject _project = null;

				public void setProject(IProject project) {
					_project = project;
				}

				public IProject getProject() {
					return _project;
				}

				public void run(IProgressMonitor monitor) {
					try {
						// orphan tasks (i.e., IMarkers that are corrupt) need to be removed
						// periodically,
						// instead of once when a project is migrated. If a project is migrated but
						// has
						// orphan tasks, the user can close & reopen the project to have the orphan
						// tasks
						// removed.
						removeOrphanTasks(monitor, getProject());

						ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(getProject());
						if (prjp.isVersionCurrent()) {
							// Project has already been migrated.
							return;
						}

						migrateValidator(monitor, ValidationRegistryReader.getReader().getValidatorMetaData(getProject()), getProject());
						migrateBuilder(monitor, getProject());

						// Once all of the migration is complete, migrate the version number of the
						// preferences
						prjp.markVersionCurrent();
					} catch (InvocationTargetException exc) {
						Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
						if (logger.isLoggingLevel(Level.SEVERE)) {
							LogEntry entry = ValidationPlugin.getLogEntry();
							entry.setSourceIdentifier("ValidationMigrator.migrateBuilder"); //$NON-NLS-1$
							entry.setTargetException(exc);
							logger.write(Level.SEVERE, entry);

							if (exc.getTargetException() != null) {
								entry.setTargetException(exc.getTargetException());
								logger.write(Level.SEVERE, entry);
							}
						}
					}
				}
			};
		}
		((ProjectMigrator) _projectMigrator).setProject(project);
		return _projectMigrator;
	}

	public void migrateRoot(IProgressMonitor monitor) {
		boolean wasSuspended = ValidatorManager.getManager().isSuspended();
		try {
			ValidatorManager.getManager().suspendAllValidation(true); // don't validate when
			// migrating
			if (!ResourcesPlugin.getWorkspace().isTreeLocked())
				ResourcesPlugin.getWorkspace().run(getGlobalMigrator(), monitor);
		} catch (CoreException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidationMigrator::migrate"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
		} finally {
			ValidatorManager.getManager().suspendAllValidation(wasSuspended);
		}
	}

	public void migrate(IProgressMonitor monitor, IProject project) {
		boolean wasSuspended = ValidatorManager.getManager().isProjectSuspended(project);
		try {
			ValidatorManager.getManager().suspendValidation(project, true); // Don't run validation
			// when migrating
			if (!ResourcesPlugin.getWorkspace().isTreeLocked())
				ResourcesPlugin.getWorkspace().run(getProjectMigrator(project), monitor);
		} catch (CoreException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidationMigrator::migrate"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
		} finally {
			ValidatorManager.getManager().suspendValidation(project, wasSuspended);
		}
	}


	/**
	 * This method has package instead of private visibility because if it were private, then the
	 * compiler needs to access this method via a synthetic accessor method, and that can have
	 * performance consequences.
	 */
	void migrateValidator(IProgressMonitor monitor, Set vmds, IResource resource) {
		if (vmds == null) {
			return;
		}

		Iterator iterator = vmds.iterator();
		while (iterator.hasNext()) {
			ValidatorMetaData vmd = (ValidatorMetaData) iterator.next();
			migrateValidator(monitor, vmd, resource);
		}
	}

	/**
	 * If the Validator has registered a name change, update all existing validation markers with
	 * the new name.
	 */
	private void migrateValidator(IProgressMonitor monitor, ValidatorMetaData vmd, IResource resource) {
		ValidatorMetaData.MigrationMetaData mmd = vmd.getMigrationMetaData();
		if (mmd == null) {
			// no migration necessary
			return;
		}

		Set idList = mmd.getIds();
		if (idList == null) {
			// nothing to migrate
			return;
		}

		Iterator iterator = idList.iterator();
		while (iterator.hasNext()) {
			String[] ids = (String[]) iterator.next();
			if (ids.length != 2) {
				// log
				continue;
			}

			String from = ids[0];
			String to = ids[1];
			if ((from == null) || (to == null)) {
				// log
				continue;
			}

			try {
				TaskListUtility.updateOwner(from, to, resource);
			} catch (CoreException exc) {
				Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
				if (logger.isLoggingLevel(Level.SEVERE)) {
					LogEntry entry = ValidationPlugin.getLogEntry();
					entry.setSourceID("ValidationMigrator.migrateValidatorClass"); //$NON-NLS-1$
					entry.setTargetException(exc);
					logger.write(Level.SEVERE, entry);
				}
			}
		}
	}

	/**
	 * This method removes all orphaned validation markers (i.e., with no "owner" attribute set or
	 * when the validator isn't installed any more).
	 * 
	 * This method has package instead of private visibility because if it were private, then the
	 * compiler needs to access this method via a synthetic accessor method, and that can have
	 * performance consequences.
	 */
	void removeOrphanTasks(IProgressMonitor monitor, IResource resource) {
		// 1. Previous owner of "messageLimit" message was IReporter, but now is ValidatorManager.
		//    This will be taken care of by the "removeOrphanTasks" call, because
		// ValidatorManager.isInternalOwner
		//    will return false for IReporter.class.toString();
		try {
			IMarker[] orphanTasks = getOrphanTasks(monitor, resource);
			if (orphanTasks.length > 0) {
				monitor.subTask(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_REMOVING));
				ResourcesPlugin.getWorkspace().deleteMarkers(orphanTasks);
				monitor.subTask(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_REMOVINGDONE));
			}
		} catch (CoreException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceID("ValidationMigrator.removeOrphanTasks"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);
			}
		}
	}

	/**
	 * Return any markers whose owners do not exist (either the validator is not installed any more,
	 * or the marker was created incorrectly).
	 */
	private IMarker[] getOrphanTasks(IProgressMonitor monitor, IResource resource) throws CoreException {
		monitor.subTask(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_LOOKING));
		try {
			if (resource == null) {
				return NO_MARKERS;
			}

			int orphanCount = 0;
			IMarker[] orphanList = null;
			IMarker[] markers = TaskListUtility.getValidationTasks(resource, SeverityEnum.ALL_MESSAGES);
			if (markers != null) {
				orphanList = new IMarker[markers.length];
				for (int i = 0; i < markers.length; i++) {
					IMarker marker = markers[i];

					Object owner = marker.getAttribute(VALIDATION_MARKER_OWNER);
					// If the owner is an existing validator or a validation framework class, then
					// it's
					// not an orphaned task.
					if ((owner == null) || !(owner instanceof String) || !((ValidationRegistryReader.getReader().isExistingValidator((String) owner) || ValidatorManager.getManager().isInternalOwner((String) owner)))) {
						orphanList[orphanCount++] = marker;
					}
				}
			}

			if (orphanCount == 0) {
				return NO_MARKERS;
			}

			IMarker[] result = new IMarker[orphanCount];
			System.arraycopy(orphanList, 0, result, 0, orphanCount);
			return result;
		} finally {
			monitor.subTask(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_STATUS_LOOKINGDONE));
		}
	}

	/**
	 * This method has package instead of private visibility because if it were private, then the
	 * compiler needs to access this method via a synthetic accessor method, and that can have
	 * performance consequences.
	 */
	void migrateBuilder(IProgressMonitor monitor, IProject p) {
		try {
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(p);
			String version = prjp.getVersion();
			if (!version.equals(VERSION4_03)) {
				// builder was migrated already. Only 4.03 needs to have its id migrated.
				return;
			}

			int newIdIndex = -1;
			int oldIdIndex = -1;
			if (p.exists() && p.isOpen()) {
				IProjectDescription description = p.getDescription();
				ICommand[] commands = description.getBuildSpec();
				for (int j = 0; j < commands.length; j++) {
					org.eclipse.core.resources.ICommand c = commands[j];
					String name = c.getBuilderName();
					if (name.equals("com.ibm.etools.j2ee.validationbuilder")) { //$NON-NLS-1$
						oldIdIndex = j;
					} else if (name.equals("org.eclipse.wst.validation.internal.core.validationbuilder")) { //$NON-NLS-1$
						newIdIndex = j;
					}
				}

				if ((oldIdIndex > -1) && (newIdIndex > -1)) {
					// Don't need to add the new, just delete the old.
					ICommand[] newCommands = new ICommand[commands.length - 1];
					if (oldIdIndex == 0) {
						System.arraycopy(commands, 1, newCommands, 0, commands.length - 1);
					} else if (oldIdIndex == commands.length) {
						System.arraycopy(commands, 0, newCommands, 0, commands.length - 1);
					} else {
						System.arraycopy(commands, 0, newCommands, 0, oldIdIndex);
						System.arraycopy(commands, oldIdIndex + 1, newCommands, oldIdIndex, commands.length - oldIdIndex - 1);
					}
					description.setBuildSpec(newCommands);
				} else if (oldIdIndex > -1) {
					// Delete the old and add the new.
					ICommand command = description.newCommand();
					command.setBuilderName("org.eclipse.wst.validation.internal.core.validationbuilder"); //$NON-NLS-1$
					commands[oldIdIndex] = command;
					description.setBuildSpec(commands);
				}

				p.setDescription(description, null);
			}
		} catch (CoreException exc) {
			// ignore. This is a temporary class anyways. Eventually everyone will move to a build
			// where the builder doesn't exist.
		} catch (InvocationTargetException exc) {
			Logger logger = ValidationPlugin.getPlugin().getMsgLogger();
			if (logger.isLoggingLevel(Level.SEVERE)) {
				LogEntry entry = ValidationPlugin.getLogEntry();
				entry.setSourceIdentifier("ValidationMigrator.migrateBuilder"); //$NON-NLS-1$
				entry.setTargetException(exc);
				logger.write(Level.SEVERE, entry);

				if (exc.getTargetException() != null) {
					entry.setTargetException(exc.getTargetException());
					logger.write(Level.SEVERE, entry);
				}
			}
		}
	}
}