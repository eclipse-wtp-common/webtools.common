package org.eclipse.wst.validation.internal.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.ValOperation;

/**
 * This interface is implemented by objects that visit enabled validators.
 * <p> 
 * Usage:
 * <pre>
 * class Visitor implements IValidatorVisitor {
 *    public void visit(Validator validator, IProgressMonitor monitor) {
 *       // your code here
 *    }
 * }
 * ValidatorManager vm = ValidatorManager.getDefault();
 * vm.accept(new Visitor(), ...);
 * </pre>
 * </p> 
 * <p>
 * Clients may implement this interface.
 * </p>
 */
public interface IValidatorVisitor {
	
	/**
	 * Visits the given validator.
	 * @param validator
	 */
	void visit(Validator validator, IProject project, boolean isManual, 
			boolean isBuild, ValOperation operation, IProgressMonitor monitor);

}
