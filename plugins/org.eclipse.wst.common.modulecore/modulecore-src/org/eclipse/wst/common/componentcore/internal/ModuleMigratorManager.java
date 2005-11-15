package org.eclipse.wst.common.componentcore.internal;

import java.util.HashSet;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
	private static ModuleMigratorManager manager;
	private HashSet migrated = new HashSet();
	private boolean migrating;
	private HashSet moved = new HashSet();
	public ModuleMigratorManager() {
		super();
	}
	public static ModuleMigratorManager getManager() {
		if (manager == null) {
			manager = new ModuleMigratorManager();
		}
		return manager;
	}
	private void migrateComponentsIfNecessary(IProject project) {
		
//		WorkspaceJob job = new WorkspaceJob("Adding Facets")
//	      {
//	        
//	        public IStatus runInWorkspace(IProgressMonitor monitor)
//	        {
//	          try
//	          {	
	        	  
	        	  IProject[] projects = WorkbenchResourceHelper.getWorkspace().getRoot().getProjects();
			      for (int i = 0; i < projects.length; i++) {
						IProject proj = projects[i];
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
						
//					}}
//	          catch (Exception e)
//	          {
//	        	  return Status.CANCEL_STATUS;
//	          }
//	          return Status.OK_STATUS;
//	        }
//	      };
//	      job.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
//	      job.schedule();
		
	}
	public synchronized void migrateOldMetaData(IProject aProject) throws CoreException {
		
		IWorkspaceRunnableWithStatus workspaceRunnable = new IWorkspaceRunnableWithStatus(aProject) {
			public void run(IProgressMonitor pm) throws CoreException {
				IProject aProj = (IProject)this.getInfo();
				migrating = true;
				try {
					if (aProj.isAccessible() && ModuleCoreNature.isFlexibleProject(aProj)) {
						if (aProj.findMember(".wtpmodules") != null) {
							if (!moved.contains(aProj))
								moveOldMetaDataFile();
						} else moved.add(aProj);
						if ((aProj.findMember(".settings/.component") != null) && 
								(ProjectFacetsManager.create(aProj) == null) &&
								(!migrated.contains(aProj)))
							migrateComponentsIfNecessary(aProj);
					}
				} finally {
					migrating = false;
				}
			}
		};
		
		ResourcesPlugin.getWorkspace().run(workspaceRunnable, null);
		
		
		
		
	}
	private void moveMetaDataFile(IProject project) {
		IResource oldfile = project.findMember(".wtpmodules");
		if (oldfile != null && oldfile.exists()) {
			
			try {
					IFolder settingsFolder = project.getFolder(".settings");
					if (!settingsFolder.exists())
						settingsFolder.create(true,true,null);
					oldfile.move(new Path(".settings/.component"),true,null);
			} catch (CoreException e) {
				Platform.getLog(ModulecorePlugin.getDefault().getBundle()).log(new Status(IStatus.ERROR, ModulecorePlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
			}
		}
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
