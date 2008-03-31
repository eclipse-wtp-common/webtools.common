/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.validation.internal.delegates;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.internal.ResourceConstants;
import org.eclipse.wst.validation.internal.ResourceHandler;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.LocalizedMessage;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * ValidatorDelegateDescriptor stores information about a delegate validator.
 * <p>
 * It is also used to store the instance of the validator it describes. To
 * obtain the instance call getValidator().
 * </p>
 */
public class ValidatorDelegateDescriptor {
	/**
	 * The platform configuration element describing this delegate.
	 */
	private IConfigurationElement _delegateConfiguration;

	/**
	 * The delegating validator's ID.
	 */
	private String _delegatingValidatorID;

	/**
	 * The delegate's ID.
	 */
	private String _id;

	/**
	 * The delegate's display name.
	 */
	private String _name;

	/**
	 * Constructs a descriptor.
	 * 
	 * @param id
	 *            The delegate's unique id. Must be unique in the context of a
	 *            delegating validator.
	 * @param delegateConfiguration
	 *            The delegates configuration element
	 * @param delegateName
	 *            The delegate's display name.
	 * @param targetValidatorID
	 *            The target validator's unique id.
	 */
	public ValidatorDelegateDescriptor(String id,
			IConfigurationElement delegateConfiguration, String delegateName,
			String targetValidatorID) {

		assert id != null;
		assert delegateConfiguration != null;
		assert targetValidatorID != null;

		_id = id;
		_delegateConfiguration = delegateConfiguration;
		_name = delegateName;
		_delegatingValidatorID = targetValidatorID;
	}

	/**
	 * Provides the delegate's ID.
	 * 
	 * @return a string with the fully qualified class name of this validator
	 *         implementation.
	 */
	public String getId() {
		return _id;
	}

	/**
	 * Provides the delegate's name.
	 * 
	 * @return a String with the validator's display name.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Retrieves the target validator's ID.
	 * 
	 * @return a String with the fully qualified class name of the delegating
	 *         validator who will delegate its implementation to the validator
	 *         described by this descriptor.
	 */
	public String getTargetID() {
		return _delegatingValidatorID;
	}

	/**
	 * Provides the instance of the validator delegate pointed to by this descriptor.
	 * 
	 * @return an IValidator instance.
	 * @throws ValidationException
	 */
	public IValidator getValidator() throws ValidationException {
		try {
			IValidator delegate = (IValidator) _delegateConfiguration
					.createExecutableExtension(ValidatorDelegatesRegistryReader.CLASS_ATTRIBUTE);
			return delegate;
		} catch (CoreException e) {
			handleException(e);
		}
		return null;
	}

	/**
	 * Answer an AbstractValidator if one has been defined, otherwise answer null.
	 */
	public AbstractValidator getValidator2() throws ValidationException {
		try {
			Object o = _delegateConfiguration
					.createExecutableExtension(ValidatorDelegatesRegistryReader.CLASS_ATTRIBUTE);
			if (o instanceof AbstractValidator)
				return (AbstractValidator) o;
		} catch (CoreException e) {
			handleException(e);
		}
		return null;
	}

	private void handleException(CoreException e) throws ValidationException {
		ValidationPlugin.getPlugin().handleException(e);
		String delegatingValidatorName = getTargetID();
		ValidatorMetaData vmd = ValidationRegistryReader.getReader()
			.getValidatorMetaData(getTargetID());
		if (vmd != null)delegatingValidatorName = vmd.getValidatorDisplayName();
		throw new ValidationException(new LocalizedMessage(
			IMessage.HIGH_SEVERITY, ResourceHandler.getExternalizedMessage(
			ResourceConstants.VBF_CANNOT_INSTANTIATE_DELEGATE, new String[] { getName(), delegatingValidatorName })));
	}
}
