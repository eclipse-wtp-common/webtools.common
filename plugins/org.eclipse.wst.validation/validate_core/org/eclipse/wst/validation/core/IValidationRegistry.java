package org.eclipse.wst.validation.core;

import org.eclipse.wst.validation.internal.ValidationRegistryReader;



public interface IValidationRegistry {
	
	IValidationRegistry instance = ValidationRegistryReader.getReader();
	
	/**
	 * This api returns the IValidator given the validatorUniqueName id.
	 * @param validatorUniqueId
	 * @return IValidator
	 * @since WTP 1.0
	 */
	public IValidator getValidator(String validatorUniqueId) throws InstantiationException;

}
