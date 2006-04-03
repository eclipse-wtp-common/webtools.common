/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.eclipse.wst.common.componentcore.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.datamodel.ProjectMigratorDataModelProvider;
import org.eclipse.wst.common.componentcore.datamodel.properties.IProjectMigratorDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.IWorkspaceRunnableWithStatus;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;


public class ModuleMigratorManager {

	private static HashMap managerCache = new HashMap();

	private static HashSet migrated = new HashSet();

	private boolean migrating;

	private HashSet moved = new HashSet();

	public ModuleMigratorManager() {
		super();
	}

	public static ModuleMigratorManager getManager(IProject proj) {
		ModuleMigratorManager manager = (ModuleMigratorManager) managerCache.get(proj);
		if (manager == null) {
			manager = new ModuleMigratorManager();
			managerCache.put(proj, manager);
		}
		return manager;
	}
	
	

	private void migrateComponentsIfNecessary(IProject project, boolean multiComps) {
		if (multiComps) {
			setupAndMigrateComponentProject(project);
		} else {
			IProject[] projects = WorkbenchResourceHelper.getWorkspace().getRoot().getProjects();
			for (int i = 0; i < projects.length; i++) {
				IProject proj = projects[i];
				setupAndMigrateComponentProject(proj);
			}
		}
	}

	private IStatus validateEdit(IResource aRes) {

		ISchedulingRule validateEditRule = null;
		IStatus status;
		IFile[] validateFiles;
		try {
			if (aRes.getType() == IResource.FILE )
				validateFiles = new IFile[] {(IFile)aRes};
			else
				validateFiles = getAllContainerFiles((IContainer)aRes);
			IWorkspace workspace = aRes.getWorkspace();
			validateEditRule = workspace.getRuleFactory().validateEditRule(validateFiles);
			Platform.getJobManager().beginRule(validateEditRule, null);
			status = workspace.validateEdit(validateFiles, null);
			if (!status.isOK()) {
				StringBuffer validateString = new StringBuffer();
				for (int i = 0; i < validateFiles.length; i++) {
					IFile file = validateFiles[i];
					validateString.append('\n');
					validateString.append(file.getProjectRelativePath());
				}
				Logger.getLogger().logWarning("During project migration on: " + aRes.getProject() + " validate edit failed on files: " + validateString.toString());
			}
			} finally {
			if (validateEditRule != null) {
				Platform.getJobManager().endRule(validateEditRule);
			}
		}
		return status;

	}

	private void setupAndMigrateComponentProject(IProject proj) {
		migrated.add(proj);
		IDataModel dm = DataModelFactory.createDataModel(new ProjectMigratorDataModelProvider());
		dm.setStringProperty(IProjectMigratorDataModelProperties.PROJECT_NAME, proj.getName());
		try {
			dm.getDefaultOperation().execute(null, null);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void migrateOldMetaData(IProject aProject, final boolean multiComps) throws CoreException {
		migrating = true;
		IWorkspaceRunnableWithStatus workspaceRunnable = new IWorkspaceRunnableWithStatus(aProject) {

			public void run(IProgressMonitor pm) throws CoreException {
				IProject aProj = (IProject) this.getInfo();
				try {
					if (aProj.isAccessible() && ModuleCoreNature.isFlexibleProject(aProj)) {
						if (aProj.findMember(".wtpmodules") != null) {
							if (!moved.contains(aProj))
								moveOldMetaDataFile();
						} else
							moved.add(aProj);
						if (needsComponentMigration(aProj, multiComps))
							migrateComponentsIfNecessary(aProj, multiComps);
					}
				} finally {
					migrating = false;
				}
			}

			private boolean needsComponentMigration(IProject aProj, boolean multiComps) throws CoreException {

				boolean needs = !migrated.contains(aProj);
				if (multiComps)
					return (needs && multiComps);
				else
					return (aProj.findMember(".settings/.component") != null) && (ProjectFacetsManager.create(aProj) == null) && needs;
			}
		};

		ResourcesPlugin.getWorkspace().run(workspaceRunnable, null, IWorkspace.AVOID_UPDATE, null);
	}

	private void moveMetaDataFile(IProject project) {
		IResource oldfile = project.findMember(".wtpmodules");
		if (oldfile != null && oldfile.exists()) {

			try {
				if (!validateEdit((IFile)oldfile).isOK()) return;
				IFolder settingsFolder = project.getFolder(".settings");
				if (!settingsFolder.exists())
					settingsFolder.create(true, true, null);
				oldfile.move(new Path(".settings/.component"), true, null);
			} catch (CoreException e) {
				Platform.getLog(ModulecorePlugin.getDefault().getBundle()).log(
						new Status(IStatus.ERROR, ModulecorePlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
			}
		}
	}

	private void moveOldMetaDataFile() {

		try {
			IProject[] projects = WorkbenchResourceHelper.getWorkspace().getRoot().getProjects();
			for (int i = 0; i < projects.length; i++) {
				IProject project = projects[i];
				if (!moved.contains(project))
					moveMetaDataFile(project);
				IFolder depFolder = project.getFolder(".deployables");
				if (depFolder.exists()) {
					if (!validateEdit(depFolder).isOK()) return;
					depFolder.delete(true, null);
				}
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
				moved.add(project);
			}

		} catch (Exception e) {
		}

	}

	protected boolean isMigrating() {
		return migrating;
	}

	private List collectFiles(IResource[] members, List result) throws CoreException {
		// recursively collect files for the given members
		for (int i = 0; i < members.length; i++) {
			IResource res = members[i];
			if (res instanceof IFolder) {
				collectFiles(((IFolder) res).members(), result);
			} else if (res instanceof IFile) {
				result.add(res);
			}
		}
		return result;
	}

	/**
	 * List of all files in the project.
	 * <p>
	 * Note: A more efficient way to do this is to use {@link IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int)}
	 * 
	 * @param 1.0.0
	 * @return list of files in the project
	 * 
	 * @see IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int)
	 * @since 1.0.0
	 */
	private IFile[] getAllContainerFiles(IContainer container) {
		List result = new ArrayList();
		if (container == null)
			return new IFile[0];
		try {
			result = collectFiles(container.members(), result);
		} catch (CoreException e) {
		}
		return (IFile[])result.toArray(new IFile[result.size()]);
	}

}
