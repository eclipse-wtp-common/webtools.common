package org.eclipse.wst.validation.core;

public interface IValidationRegistry {
	
	/**
	 * This api returns the IValidator given the validatorUniqueName id.
	 * @param validatorUniqueId
	 * @return IValidator
	 * @since WTP 1.0
	 */
	public IValidator getValidator(String validatorUniqueId) throws InstantiationException;

}
