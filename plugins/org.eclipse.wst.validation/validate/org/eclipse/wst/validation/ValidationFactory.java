package org.eclipse.wst.validation;

import org.eclipse.wst.validation.core.IValidator;
import org.eclipse.wst.validation.internal.ValidationFactoryImpl;

/**
 * This interface provides a way to access an instance of the ValidationFactoryImpl
 * instance, an internal implementation that provides access to some of the internal state
 * of validators.
 * 
 * @since 1.0
 */
public interface ValidationFactory {
	
	ValidationFactory instance = ValidationFactoryImpl.getInstance();
	/**
	 * This api returns the IValidator given the validatorUniqueName id. The unique
	 * id name is the class name that is defined in the class name element in the 
	 * plugin extension of the validator.
	 * @param validatorUniqueId
	 * @return IValidator
	 * @since WTP 1.0
	 */
	public IValidator getValidator(String validatorUniqueId) throws InstantiationException;

}
