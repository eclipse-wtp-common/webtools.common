/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.commands.ExecutionException;
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
		ModuleMigratorManager manager = (ModuleMigratorManager)managerCache.get(proj);
		if (manager == null) {
			manager = new ModuleMigratorManager();
			managerCache.put(proj,manager);
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
	private void setupAndMigrateComponentProject(IProject proj) {
		migrated.add(proj);
		IDataModel dm = DataModelFactory.createDataModel(new ProjectMigratorDataModelProvider());
		dm.setStringProperty(IProjectMigratorDataModelProperties.PROJECT_NAME,proj.getName());
		try {
			dm.getDefaultOperation().execute(null,null);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void migrateOldMetaData(IProject aProject, final boolean multiComps) throws CoreException {
		migrating = true;
		IWorkspaceRunnableWithStatus workspaceRunnable = new IWorkspaceRunnableWithStatus(aProject) {
			public void run(IProgressMonitor pm) throws CoreException {
				IProject aProj = (IProject)this.getInfo();
				try {
					if (aProj.isAccessible() && ModuleCoreNature.isFlexibleProject(aProj)) {
						if (aProj.findMember(".wtpmodules") != null) {
							if (!moved.contains(aProj))
								moveOldMetaDataFile();
						} else moved.add(aProj);
						if (needsComponentMigration(aProj,multiComps))
							migrateComponentsIfNecessary(aProj,multiComps);
					}
				} finally {
					migrating = false;
				}
			}

			private boolean needsComponentMigration(IProject aProj,boolean multiComps) throws CoreException {
				
			boolean needs = !migrated.contains(aProj);
			if (multiComps)
				return (needs && multiComps);
			else
				return (aProj.findMember(StructureEdit.MODULE_META_FILE_NAME) != null) && 
						(ProjectFacetsManager.create(aProj) == null) && needs;
			}
		};
		
		ResourcesPlugin.getWorkspace().run(workspaceRunnable, null,IWorkspace.AVOID_UPDATE,null);
		
		
		
		
	}
	private void moveMetaDataFile(IProject project) {
		IResource oldfile = project.findMember(".wtpmodules");
		if (oldfile != null && oldfile.exists()) {
			
			try {
					IFolder settingsFolder = project.getFolder(".settings");
					if (!settingsFolder.exists())
						settingsFolder.create(true,true,null);
					oldfile.move(new Path(StructureEdit.MODULE_META_FILE_NAME),true,null);
			} catch (CoreException e) {
				Platform.getLog(ModulecorePlugin.getDefault().getBundle()).log(new Status(IStatus.ERROR, ModulecorePlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
			}
		} //else {
//			oldfile = project.findMember(".settings/.component");
//			if (oldfile != null && oldfile.exists()) {
//				try {
//						oldfile.move(new Path(StructureEdit.MODULE_META_FILE_NAME),true,null);
//				} catch (CoreException e) {
//					Platform.getLog(ModulecorePlugin.getDefault().getBundle()).log(new Status(IStatus.ERROR, ModulecorePlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
//				}
//			} 
//			
//		}
	}
	private void moveOldMetaDataFile() {
//		WorkspaceJob job = new WorkspaceJob("Migrating metadata")
//	      {
//	        
//	        public IStatus runInWorkspace(IProgressMonitor monitor)
//	        {
	          try
	          {
	        	IProject[] projects = WorkbenchResourceHelper.getWorkspace().getRoot().getProjects();
	      		for (int i = 0; i < projects.length; i++) {
	      			IProject project = projects[i];
	      			if (!moved.contains(project))
		      			moveMetaDataFile(project);
	      				IFolder depFolder = project.getFolder(".deployables");
	      				if (depFolder.exists())
	      					depFolder.delete(true,null);
		      			project.refreshLocal(IResource.DEPTH_INFINITE,null);
		      			moved.add(project);
	      			}
	      		
	          } catch (Exception e) {
	          }
//	          return Status.OK_STATUS;
//	        }
//	      };
//	      job.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
//	      job.schedule();
	}
	protected boolean isMigrating() {
		return migrating;
	}

}
