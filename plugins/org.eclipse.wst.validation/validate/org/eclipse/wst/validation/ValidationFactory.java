package org.eclipse.wst.validation;

import org.eclipse.wst.validation.core.IValidator;
import org.eclipse.wst.validation.internal.ValidationFactoryImpl;

public interface ValidationFactory {
	
	ValidationFactory instance = ValidationFactoryImpl.getInstance();
	/**
	 * This api returns the IValidator given the validatorUniqueName id.
	 * @param validatorUniqueId
	 * @return IValidator
	 * @since WTP 1.0
	 */
	public IValidator getValidator(String validatorUniqueId) throws InstantiationException;

}
