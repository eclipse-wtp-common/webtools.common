package org.eclipse.wst.common.componentcore.internal;

import java.util.HashSet;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
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
		migrated.add(project);
		WorkspaceJob job = new WorkspaceJob("Adding Facets")
	      {
	        
	        public IStatus runInWorkspace(IProgressMonitor monitor)
	        {
	          try
	          {	
	        	  
	        	  IProject[] projects = WorkbenchResourceHelper.getWorkspace().getRoot().getProjects();
			      for (int i = 0; i < projects.length; i++) {
						IProject project = projects[i];
						
						IDataModel dm = DataModelFactory.createDataModel(new ProjectMigratorDataModelProvider());
						dm.setStringProperty(IProjectMigratorDataModelProperties.PROJECT_NAME,project.getName());
						try {
							dm.getDefaultOperation().execute(null,null);
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}}
	          catch (Exception e)
	          {
	        	  return Status.CANCEL_STATUS;
	          }
	          return Status.OK_STATUS;
	        }
	      };
	      job.schedule(5000);
		
	}
	public synchronized void migrateOldMetaData(IProject aProject) throws CoreException {
		migrating = true;
		try {
			if (aProject.isAccessible() && ModuleCoreNature.isFlexibleProject(aProject)) {
				if (aProject.findMember(".wtpmodules") != null && (!moved.contains(aProject)))
					moveOldMetaDataFile();
				if ((aProject.findMember(".settings/.component") != null) && 
						(ProjectFacetsManager.create(aProject) == null) &&
						(!migrated.contains(aProject)))
					migrateComponentsIfNecessary(aProject);
			}
		} finally {
			migrating = false;
		}
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
		WorkspaceJob job = new WorkspaceJob("Migrating metadata")
	      {
	        
	        public IStatus runInWorkspace(IProgressMonitor monitor)
	        {
	          try
	          {
	        	IProject[] projects = WorkbenchResourceHelper.getWorkspace().getRoot().getProjects();
	      		for (int i = 0; i < projects.length; i++) {
	      			IProject project = projects[i];
	      			if (!moved.contains(project))
		      			moveMetaDataFile(project);
	      				IFolder depFolder = project.getFolder(".deployables");
	      				if (depFolder.exists())
	      					depFolder.delete(true,monitor);
		      			project.refreshLocal(IResource.DEPTH_INFINITE,monitor);
		      			moved.add(project);
	      			}
	      		}
	          catch (Exception e)
	          {
	        	  return Status.CANCEL_STATUS;
	          }
	          return Status.OK_STATUS;
	        }
	      };
	      job.schedule();
	}
	protected boolean isMigrating() {
		return migrating;
	}

}
