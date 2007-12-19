package org.eclipse.wst.validation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The class that all Validators that wish to use version two of the validation framework must subclass.
 * @author karasiuk
 *
 */
public abstract class AbstractValidator {
	
	/**
	 * Validate the resource. The validator is called from a WorkspaceJob, so the validator itself does not need
	 * to establish it's own IWorkspaceRunnable.
	 * 
	 * @param resource the resource to be validated.
	 * 
	 * @param kind the way the resource changed. It uses the same values as the kind parameter
	 * in IResourceDelta.
	 * 
	 * @param state a way to pass arbitrary, validator specific, data from one invocation of a validator to
	 * the next, during the validation phase. At the end of the validation phase, this object will be cleared,
	 * thereby allowing any of this state information to be garbaged collected.
	 * 
	 * @param monitor a monitor that you can use to report your progress. To be a well behaved validator you need
	 * to check the isCancelled() method at the appropriate times.
	 * 
	 * @return the result of the validation. This may be, but usually isn't, null. 
	 */
	public abstract ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor);
	
	/**
	 * The project is being cleaned, this method gives the validator a chance to do any special cleanup.
	 * The default is to do nothing.
	 * <p>
	 * If the entire workspace is being cleaned, then the first call will have a null project, and then there will be
	 * subsequent calls for each open project in the workspace.
	 * 
	 * @param project the project being cleaned. This may be null, which is an indication that the workspace
	 * is being cleaned. 
	 * 
	 * @param state a way to pass arbitrary, validator specific, data from one invocation of a validator to
	 * the next, during the validation phase.
	 * 
	 * @param monitor the monitor that should be used for reporting progress if the clean takes a long time.
	 */
	public void clean(IProject project, ValidationState state, IProgressMonitor monitor){		
	}
		
	/**
	 * This method will be called before any validation takes place. It allows validators to perform any
	 * initialization that they might need. 
	 *  
	 * @param project the project that is being validated. For the very first call in the validation phase,
	 * this will be null. That is the signal to the validator that a top level validation is starting.
	 * Subsequently, the project will be set, as each of the individual projects are validated.
	 * 
	 * @param state a way to pass arbitrary, validator specific, data from one invocation of a validator to
	 * the next, during the validation phase.
	 * 
	 * @param monitor the monitor that should be used for reporting progress if the clean takes a long time.
	 */
	public void validationStarting(IProject project, ValidationState state, IProgressMonitor monitor){		
	}
	
	/**
	 * This method will be called when validation is complete. It allows validators to perform any
	 * cleanup that they might need to do.  
	 *  
	 * @param project the project that was validated. The very last call in the validation will set this to 
	 * null so that the validator knows that all the projects have now been validated.
	 * 
	 * @param state a way to pass arbitrary, validator specific, data from one invocation of a validator to
	 * the next, during the validation phase.
	 * 
	 * @param monitor the monitor that should be used for reporting progress if the clean takes a long time.
	 */
	public void validationFinishing(IProject project, ValidationState state, IProgressMonitor monitor){		
	}
	
	/**
	 * The validator is allowed to assert dependencies between various resources. The default would be to 
	 * scope these assertions with the validator's extension point id. If a validator would like to share
	 * these assertions with other validators, they can use this method to override the id that is used
	 * to scope the assertions.
	 * <p>
	 * So for example if you had two validators that wanted to share the assertions. The second validator would
	 * override this method and answer the first validator's extension point id.
	 * <p>
	 * The default behavior is to return null, which means that the default scope will be used.
	 * 
	 * @return null if you wish to use the default scoping mechanism.
	 */
	public String getDependencyId(){
		return null;
	}

}
