package org.eclipse.wst.validation.internal;

import org.eclipse.wst.validation.ValidationFactory;
import org.eclipse.wst.validation.core.IValidator;

public class ValidationFactoryImpl implements ValidationFactory {
	static ValidationFactory inst = null;
	
	public ValidationFactoryImpl() {
		super();
	}

	public static ValidationFactory getInstance() {
		if(inst == null)
			inst = new ValidationFactoryImpl();
		return inst;
	}

	public IValidator getValidator(String validatorUniqueId) throws InstantiationException {
		ValidationRegistryReader reader = ValidationRegistryReader.getReader();
		return reader.getValidator(validatorUniqueId);
	}

}
