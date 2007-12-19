package org.eclipse.wst.validation.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.model.IValidatorVisitor;

/**
 * Keep track of a validation operation when it is triggered as part of a build.
 * @author karasiuk
 *
 */
public class ValOperationManager implements IResourceChangeListener {
	
	private static ValOperationManager _me;

	private ValOperation 	_operation;

	public static synchronized ValOperationManager getDefault(){
		if (_me == null)_me = new ValOperationManager();
		return _me;
	}
	
	private ValOperationManager(){
		init();
	}
				
	/**
	 * Initialize yourself for a new validation operation.
	 */
	private void init(){
		_operation = new ValOperation();
	}

	public void resourceChanged(IResourceChangeEvent event) {
		int type = event.getType();
		int kind = event.getBuildKind();
		
		IProject project = null;
		if (event.getSource() instanceof IProject) {
			project = (IProject) event.getSource();
		}
		
		if (kind == IncrementalProjectBuilder.CLEAN_BUILD && ((type & IResourceChangeEvent.PRE_BUILD) != 0)){
			processClean(event);
		}
		
		if (isBuildStarting(event)){
			IValidatorVisitor visitor = new IValidatorVisitor(){

				public void visit(Validator validator, IProject project, boolean isManual, 
						boolean isBuild, ValOperation operation, IProgressMonitor monitor) {
					
					validator.validationStarting(project, operation.getState(), monitor);					
				}				
			};
			ValManager.getDefault().accept(visitor, project, false, true, _operation, new NullProgressMonitor());
			
		}
		
		if (isBuildFinished(event)){
			IValidatorVisitor visitor = new IValidatorVisitor(){

				public void visit(Validator validator, IProject project, boolean isManual, 
						boolean isBuild, ValOperation operation, IProgressMonitor monitor) {
					
					validator.validationFinishing(project, operation.getState(), monitor);					
				}
				
			};
			ValManager.getDefault().accept(visitor, project, false, true, _operation, new NullProgressMonitor());
			//TODO this may prove to be a mistake, I may be clearing the validation result too soon.
			init();
		}
		
		
		//TODO remove this	
		if (Misc.isLogging()){
			String kindName = null;
			if (kind == IncrementalProjectBuilder.AUTO_BUILD)kindName = "Auto"; //$NON-NLS-1$
			else if (kind == IncrementalProjectBuilder.CLEAN_BUILD)kindName = "Clean"; //$NON-NLS-1$
			else if (kind == IncrementalProjectBuilder.FULL_BUILD)kindName = "Full"; //$NON-NLS-1$
			else if (kind == IncrementalProjectBuilder.INCREMENTAL_BUILD)kindName = "Incremental"; //$NON-NLS-1$
			else kindName = String.valueOf(kind);
			
			StringBuffer b = new StringBuffer(100);
			
			String sourceName = "unknown"; //$NON-NLS-1$
			if (event.getSource() instanceof IResource) {
				IResource res = (IResource) event.getSource();
				sourceName = res.getName();
			}
			else if (event.getSource() instanceof IWorkspace) {
				sourceName = "Workspace";			 //$NON-NLS-1$
			}
			b.append("Source="+sourceName+", kind="+kindName+", type="+type); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if ((type & IResourceChangeEvent.POST_BUILD) != 0)b.append(", post build"); //$NON-NLS-1$
			if ((type & IResourceChangeEvent.PRE_BUILD) != 0){
				b.append(", pre build"); //$NON-NLS-1$
			}
			IResourceDelta rd = event.getDelta();
			if (rd == null)b.append(", there was no resource delta"); //$NON-NLS-1$
			
			Misc.log(b);
		}
		
	}
	
	/**
	 * Determine if we are starting a new build cycle.
	 * @param event
	 * @return
	 */
	private boolean isBuildStarting(IResourceChangeEvent event) {
		int type = event.getType();
		int kind = event.getBuildKind();
		
		if (ResourcesPlugin.getWorkspace().isAutoBuilding()){
			if (((type & IResourceChangeEvent.PRE_BUILD) != 0) && 
					(kind == IncrementalProjectBuilder.AUTO_BUILD || 
					kind == IncrementalProjectBuilder.INCREMENTAL_BUILD ||
					kind == IncrementalProjectBuilder.FULL_BUILD))return true;
			
			if (((type & IResourceChangeEvent.POST_BUILD) != 0) && 
					(kind == IncrementalProjectBuilder.CLEAN_BUILD))return true;
			
			return false;
		}
		else {
			if (kind == IncrementalProjectBuilder.AUTO_BUILD )return false;
			if (((type & IResourceChangeEvent.PRE_BUILD) != 0) && 
					(kind == IncrementalProjectBuilder.INCREMENTAL_BUILD ||
					kind == IncrementalProjectBuilder.FULL_BUILD))return true;
			return false;
		}
	}

	/**
	 * Determine if we are at the end of a build cycle. This will give callers the ability to
	 * clear caches etc.
	 *  
	 * @param event
	 * @return return true if we are just finishing a build.
	 */
	private boolean isBuildFinished(IResourceChangeEvent event) {
		/*
		 * I discovered these conditions empirically, by running running different types of builds
		 * and noticing which events were signaled.
		 * 
		 * When auto build is enabled, it seemed like all the cases ended with an AutoBuild build type, a 
		 * POST_BUILD and a target of WorkSpace.
		 * 
		 * When auto build is not enabled, the AUTO BUILD events are signaled, so they should be ignored.
		 * Anything other than a clean POST_BUILD seemed to indicate the end of a build cycle. (In some cases
		 * this might be a little too aggressive, but since the end result is just the deleting of some 
		 * caches, it is better to be aggressive than to leave the caches around too long).
		 */
		int type = event.getType();
		int kind = event.getBuildKind();
		
		if (ResourcesPlugin.getWorkspace().isAutoBuilding()){
			if (((type & IResourceChangeEvent.POST_BUILD) != 0) && kind == IncrementalProjectBuilder.AUTO_BUILD
				&& event.getSource() instanceof IWorkspace){
					return true;
				}
		}
		else {
			if (kind != IncrementalProjectBuilder.AUTO_BUILD && ((type & IResourceChangeEvent.POST_BUILD) != 0)){
				return true;
			}
		}
		
		return false;
	}
	
	private void processClean(IResourceChangeEvent event){
		// Originally I was using this to monitor IProject build requests as well, but that is not not needed
		// since these will be handled by the IncrementalProjectBuilder.clean() method.
		IProgressMonitor monitor = new NullProgressMonitor();
		Object source = event.getSource();
		if (source instanceof IWorkspace) {
			ValManager.getDefault().clean(null, _operation, monitor);
		}
		
	}

	public ValOperation getOperation() {
		return _operation;
	}

}
