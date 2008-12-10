/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.Validator.V2;

/**
 * The class that all Validators that wish to use version two of the validation framework must subclass.
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * @author karasiuk
 *
 */
public abstract class AbstractValidator {
	
	private V2 _parent;
	
	/**
	 * Validate the resource. The validator is called from a WorkspaceJob, so
	 * the validator itself does not need to establish it's own IWorkspaceRunnable.
	 * <p>
	 * If you override this method then you should not override the other validate method.
	 * </p>
	 * 
	 * @param resource
	 * 		The resource to be validated.
	 * 
	 * @param kind
	 * 		The way the resource changed. It uses the same values as the kind
	 * 		parameter in IResourceDelta.
	 * 
	 * @param state
	 * 		A way to pass arbitrary, validator specific, data from one
	 * 		invocation of a validator to the next, during the validation phase.
	 * 		At the end of the validation phase, this object will be cleared,
	 * 		thereby allowing any of this state information to be garbaged
	 * 		collected.
	 * 
	 * @param monitor
	 * 		A monitor that you can use to report your progress. To be a well
	 * 		behaved validator you need to check the isCancelled() method at
	 * 		appropriate times.
	 * 
	 * @return the result of the validation. This may be, but usually isn't, null.
	 */
	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor){
		return null;
	}
	
	/**
	 * Validate the resource. The validator is called from a WorkspaceJob, so
	 * the validator itself does not need to establish it's own
	 * IWorkspaceRunnable.
	 * <p>
	 * If you override this method then you should not override the other
	 * validate method.
	 * </p>
	 * 
	 * @param event
	 *            An object that describes the resource to be validated and why
	 *            it should be validated.
	 * 
	 * @param state
	 *            A way to pass arbitrary, validator specific, data from one
	 *            invocation of a validator to the next, during the validation
	 *            phase. At the end of the validation phase, this object will be
	 *            cleared, thereby allowing any of this state information to be
	 *            garbaged collected.
	 * 
	 * @param monitor
	 *            A monitor that you can use to report your progress. To be a
	 *            well behaved validator you need to check the isCancelled()
	 *            method at appropriate times.
	 * 
	 * @return the result of the validation. Null should never be returned. If
	 *         null is returned then the other validate method will be called as
	 *         well.
	 */
	public ValidationResult validate(ValidationEvent event, ValidationState state, IProgressMonitor monitor){
		return null;
	}
	
	
	/**
	 * A call back method that lets the validator know that the project is being
	 * cleaned. This method gives the validator a chance to do any special
	 * cleanup. The default is to do nothing.
	 * <p>
	 * If the entire workspace is being cleaned, then the first call will have a
	 * null project, and then there will be subsequent calls for each open
	 * project in the workspace.</p>
	 * 
	 * @param project
	 * 		The project being cleaned. This may be null, which is an indication
	 * 		that the workspace is being cleaned.
	 * 
	 * @param state
	 * 		A way to pass arbitrary, validator specific, data from one
	 * 		invocation of a validator to the next, during the validation phase.
	 * 
	 * @param monitor
	 * 		The monitor that should be used for reporting progress if the clean
	 * 		takes a long time.
	 */
	public void clean(IProject project, ValidationState state, IProgressMonitor monitor){		
	}
		
	/**
	 * This method is called before any validation takes place. It allows
	 * validators to perform any initialization that they might need.
	 * 
	 * @param project
	 * 		The project that is being validated. For the very first call in the
	 * 		validation phase, this will be null. A null project is the signal
	 * 		that a top level validation is starting. Subsequently, the project
	 * 		will be set, as each of the individual projects are validated.
	 * 
	 * @param state
	 * 		A way to pass arbitrary, validator specific, data from one
	 * 		invocation of a validator to the next, during the validation phase.
	 * 
	 * @param monitor
	 * 		The monitor that should be used for reporting progress if the initialization
	 * 		takes a long time.
	 */
	public void validationStarting(IProject project, ValidationState state, IProgressMonitor monitor){		
	}
	
	/**
	 * This method will be called when validation is complete. It allows
	 * validators to perform any cleanup that they might need to do.
	 * 
	 * @param project
	 * 		The project that was validated. The very last call in the validation
	 * 		sets this to null so that the validator knows that all the
	 * 		projects have now been validated.
	 * 
	 * @param state
	 * 		A way to pass arbitrary, validator specific, data from one
	 * 		invocation of a validator to the next, during the validation phase.
	 * 
	 * @param monitor
	 * 		The monitor that should be used for reporting progress if the cleanup
	 * 		takes a long time.
	 */
	public void validationFinishing(IProject project, ValidationState state, IProgressMonitor monitor){		
	}
		
	/**
	 * Answer the validator that you belong to. The validator controls the
	 * filters and various other settings.
	 * 
	 * @nooverride
	 */
	public V2 getParent(){
		return _parent;
	}
	
	void setParent(V2 parent){
		_parent = parent;
	}
}
