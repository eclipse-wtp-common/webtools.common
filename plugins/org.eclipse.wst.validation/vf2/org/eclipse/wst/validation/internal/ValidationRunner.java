package org.eclipse.wst.validation.internal;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.model.IValidatorVisitor;

/**
 * Run the validators on a selected set of resources.
 * @author karasiuk
 *
 */
public class ValidationRunner implements IWorkspaceRunnable {
	
	private Map<IProject, Set<IResource>>		_projects;
	private	boolean			_isManual; 
	private boolean			_isBuild;
	private ValOperation	_valOperation;
	
	/**
	 * Validate the selected projects and/or resources.
	 * 
	 * @param projects
	 *            The selected projects. The key is an IProject and the value is
	 *            the Set of IResources that were selected. Often this will be
	 *            every resource in the project.
	 * 
	 * @param isManual
	 *            Is this a manual validation?
	 * 
	 * @param isBuild
	 *            Is this a build based validation?
	 * 
	 * @param monitor
	 *            progress monitor
	 */
	public static ValOperation validate(Map<IProject, Set<IResource>> projects, boolean isManual, 
		boolean isBuild, IProgressMonitor monitor) throws CoreException{
		ValidationRunner me = new ValidationRunner(projects, isManual, isBuild);
		ResourcesPlugin.getWorkspace().run(me, monitor);
		return me._valOperation;
	}
	
	private ValidationRunner(Map<IProject, Set<IResource>> projects, boolean isManual, boolean isBuild){
		_projects = projects;
		_isManual = isManual;
		_isBuild = isBuild;
		
	}
	
	private ValOperation execute(IProgressMonitor monitor){
		_valOperation = new ValOperation();
		ValManager manager = ValManager.getDefault();
		
		IValidatorVisitor startingVisitor = new IValidatorVisitor(){
			public void visit(Validator validator, IProject project, boolean isManual,
					boolean isBuild, ValOperation operation, IProgressMonitor monitor) {
				validator.validationStarting(project, operation.getState(), monitor);
			}			
		};
		
		IValidatorVisitor finishedVisitor = new IValidatorVisitor(){

			public void visit(Validator validator, IProject project, boolean isManual,
					boolean isBuild, ValOperation operation, IProgressMonitor monitor) {

				validator.validationFinishing(project, operation.getState(), monitor);				
			}			
		};
		
		manager.accept(startingVisitor, null, _isManual, _isBuild, _valOperation, monitor);
				
		for (Map.Entry<IProject, Set<IResource>> me : _projects.entrySet()){
			if (monitor.isCanceled()){
				_valOperation.getResult().setCanceled(true);
				return _valOperation;
			}
			IProject project = me.getKey();
			ValManager.getDefault().accept(startingVisitor, project, _isManual, _isBuild, _valOperation, monitor);
			for (IResource resource : me.getValue()){
				manager.validate(project, resource, IResourceDelta.NO_CHANGE, _isManual,_isBuild, 
					IncrementalProjectBuilder.AUTO_BUILD, _valOperation, monitor);
			}
			manager.accept(finishedVisitor, project, _isManual, _isBuild, _valOperation, monitor);
		}
		manager.accept(finishedVisitor, null, _isManual, _isBuild, _valOperation, monitor);
		return _valOperation;
	}

	public void run(IProgressMonitor monitor) throws CoreException {
		execute(monitor);		
	}

}
