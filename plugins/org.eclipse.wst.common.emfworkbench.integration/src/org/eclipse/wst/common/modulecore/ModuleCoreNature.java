package org.eclipse.wst.common.modulecore;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.wst.common.internal.emfworkbench.CompatibilityWorkbenchURIConverterImpl;
import org.eclipse.wst.common.internal.emfworkbench.integration.EditModelNature;
import org.eclipse.wst.common.modulecore.impl.UnresolveableURIException;
import org.eclipse.wst.common.modulecore.util.ModuleCore;

import com.ibm.wtp.emf.workbench.EMFWorkbenchContextBase;
import com.ibm.wtp.emf.workbench.ProjectResourceSet;
import com.ibm.wtp.emf.workbench.ProjectUtilities;
import com.ibm.wtp.emf.workbench.WorkbenchURIConverter;

//In Progress......

public class ModuleCoreNature extends EditModelNature implements IProjectNature, IModuleConstants, IResourceChangeListener {


	public void resourceChanged(IResourceChangeEvent anEvent) {
		// event.getDelta()
		// IResource changedResource = (IResource)event.getResource();
		// update()
	}

	public static ModuleCoreNature getModuleCoreNature(IProject aProject) {
		try {
			return (ModuleCoreNature) aProject.getNature(IModuleConstants.MODULE_NATURE_ID);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ModuleCoreNature addModuleCoreNatureIfNecessary(final IProject aProject, IProgressMonitor aMonitor) {
		try {
			if (aProject.hasNature(IModuleConstants.MODULE_NATURE_ID))
				return getModuleCoreNature(aProject);
			Job addNatureJob = new Job("Add Nature") {
				protected IStatus run(IProgressMonitor monitor) {
					try {
						IProjectDescription description = aProject.getDescription();

						String[] currentNatureIds = description.getNatureIds();
						String[] newNatureIds = new String[currentNatureIds.length + 1];
						System.arraycopy(currentNatureIds, 0, newNatureIds, 0, currentNatureIds.length);
						newNatureIds[currentNatureIds.length] = IModuleConstants.MODULE_NATURE_ID;
						description.setNatureIds(newNatureIds);
						aProject.setDescription(description, monitor);
					} catch (CoreException e) {
						e.printStackTrace();
					}
					return Status.OK_STATUS;
				}
			};
			final boolean[] mutex = new boolean[] {true};
			addNatureJob.addJobChangeListener(new JobChangeAdapter() {
				public void done(IJobChangeEvent event) {
					mutex[0] = false;
				}
			});
			addNatureJob.schedule();
			while(mutex[0]) {
				try {
					Thread.sleep(200);
				} catch(InterruptedException ie) {
					
				}
			}

		} catch (CoreException e) { 
			e.printStackTrace();
		}
		return getModuleCoreNature(aProject);
	}

	public ModuleStructuralModel getModuleStructuralModelForRead(Object anAccessorKey) {
		return (ModuleStructuralModel) getEditModelForRead(ModuleStructuralModelFactory.MODULE_STRUCTURAL_MODEL_ID, anAccessorKey);
	}

	public ModuleStructuralModel getModuleStructuralModelForWrite(Object anAccessorKey) {
		return (ModuleStructuralModel) getEditModelForWrite(ModuleStructuralModelFactory.MODULE_STRUCTURAL_MODEL_ID, anAccessorKey);
	}

	public ArtifactEditModel getModuleEditModelForRead(URI aModuleURI, Object anAccessorKey) {
		Map params = new HashMap();
		params.put(ModuleEditModelFactory.PARAM_MODULE_URI, aModuleURI);
		return (ArtifactEditModel) getEditModelForRead(getArtifactEditModelId(aModuleURI), anAccessorKey, params);
	}

	public ArtifactEditModel getModuleEditModelForWrite(URI aModuleURI, Object anAccessorKey) {
		Map params = new HashMap();
		params.put(ModuleEditModelFactory.PARAM_MODULE_URI, aModuleURI);
		return (ArtifactEditModel) getEditModelForWrite(getArtifactEditModelId(aModuleURI), anAccessorKey, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.emf.workbench.IEMFContextContributor#primaryContributeToContext(com.ibm.wtp.emf.workbench.EMFWorkbenchContextBase)
	 */
	public void primaryContributeToContext(EMFWorkbenchContextBase aNature) {
		if (emfContext == aNature)
			return;
		emfContext = aNature;
		getEmfContext().setDefaultToMOF5Compatibility(true);
		// Overriding superclass to use our own URI converter, which knows about binary projects
		ProjectResourceSet set = aNature.getResourceSet();
		set.setResourceFactoryRegistry(WTPResourceFactoryRegistry.INSTANCE);
		WorkbenchURIConverter conv = initializeWorbenchURIConverter(set);
		set.setURIConverter(conv);
		// initializeCacheEditModel();
		// addAdapterFactories(set);
		// set.getSynchronizer().addExtender(this); // added so we can be informed of closes to the
		// new J2EEResourceDependencyRegister(set); // This must be done after the URIConverter is

	}

	/**
	 * @param set
	 * @return
	 */
	private WorkbenchURIConverter initializeWorbenchURIConverter(ProjectResourceSet set) {
		WorkbenchURIConverter uriConverter = new CompatibilityWorkbenchURIConverterImpl();
		uriConverter.addInputContainer(getProject());
		return uriConverter;
	}

	public ResourceSet getResourceSet() {
		return getEmfContextBase().getResourceSet();
	}

	public String getNatureID() {
		return MODULE_NATURE_ID;
	}

	protected String getPluginID() {
		return MODULE_PLUG_IN_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.emf.workbench.IEMFContextContributor#secondaryContributeToContext(com.ibm.wtp.emf.workbench.EMFWorkbenchContextBase)
	 */
	public void secondaryContributeToContext(EMFWorkbenchContextBase aNature) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.emf.workbench.nature.EMFNature#configure()
	 */
	public void configure() throws CoreException {
		super.configure();
		addDeployableProjectBuilder();
        addLocalDependencyResolver();
	}

	private void addDeployableProjectBuilder() throws CoreException {
		IProjectDescription description = project.getDescription();
		ICommand[] builderCommands = description.getBuildSpec();
		boolean previouslyAdded = false;

		for (int i = 0; i < builderCommands.length; i++) {
			if (builderCommands[i].getBuilderName().equals(DEPLOYABLE_MODULE_BUILDER_ID))
				// builder already added no need to add again
				previouslyAdded = true;
			break;
		}
		if (!previouslyAdded) {
			// builder not found, must be added
			ICommand command = description.newCommand();
			command.setBuilderName(DEPLOYABLE_MODULE_BUILDER_ID);
			ICommand[] updatedBuilderCommands = new ICommand[builderCommands.length + 1];
			System.arraycopy(builderCommands, 0, updatedBuilderCommands, 1, builderCommands.length);
			updatedBuilderCommands[0] = command;
			description.setBuildSpec(updatedBuilderCommands);
			project.setDescription(description, null);
		}
	}
    private void addLocalDependencyResolver() throws CoreException {
        ProjectUtilities.addToBuildSpec(LOCAL_DEPENDENCY_RESOLVER_ID, getProject());
    }

	private String getArtifactEditModelId(URI aModuleURI) {
		ModuleStructuralModel structuralModel = null;
		try {
			structuralModel = getModuleStructuralModelForRead(Thread.currentThread());
			ModuleCore editUtility = (ModuleCore) structuralModel.getAdapter(ModuleCore.ADAPTER_TYPE);
			WorkbenchModule module = editUtility.findWorkbenchModuleByDeployName(ModuleURIUtil.getDeployedName(aModuleURI));
			return module.getModuleType().getModuleTypeId();
		} catch (UnresolveableURIException uurie) {
			// Ignore
		} finally {
			if (structuralModel != null)
				structuralModel.releaseAccess(Thread.currentThread());
		}
		return null;
	}
	/*
	 * private synchronized void update() { moduleHandlesMap.clear(); workbenchModulesMap.clear();
	 * projectModules = null; try { if (getProjectModules() != null) { List workBenchModules =
	 * getProjectModules().getWorkbenchModules(); for (int i = 0; i < workBenchModules.size(); i++) {
	 * WorkbenchModule wbm = (WorkbenchModule) workBenchModules.get(i); // IModuleHandle handle =
	 * wbm.getHandle(); if (handle == null || handle.getHandle() == null) continue;
	 * moduleHandlesMap.put(handle.getHandle(), handle); workbenchModulesMap.put(handle, wbm); } } }
	 * catch (RuntimeException e) { Logger.getLogger().write(e); } }
	 * 
	 * private ProjectModules getProjectModules() { if (projectModules == null) { Resource resource =
	 * getWTPModuleResource(); if (resource != null) { EList wtpModuleResourceContents =
	 * resource.getContents(); if (wtpModuleResourceContents != null &&
	 * wtpModuleResourceContents.get(0) != null) projectModules = (ProjectModules)
	 * wtpModuleResourceContents.get(0); } }
	 * 
	 * return projectModules; }
	 */


}
