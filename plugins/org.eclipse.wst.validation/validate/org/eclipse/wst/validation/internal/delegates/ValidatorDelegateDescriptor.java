/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
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
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.LocalizedMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * ValidatorDelegateDescriptor stores information about a delegate validator.
 * 
 * It is also used to store the instance of the validator it describes. To
 * obtain the instance call getValidator().
 */
public class ValidatorDelegateDescriptor
{
  /**
   * The platform configuration element describing this delegate.
   */
  private IConfigurationElement delegateConfiguration;

  /**
   * The delegating validator's ID.
   */
  private String delegatingValidatorID;

  /**
   * The delegate's ID.
   */
  private String id;

  /**
   * The delegate's display name.
   */
  private String name;

  /**
   * Constructs a descriptor.
   * 
   * @param id
   *          the delegate's uniques id. Must be unique in the context of a
   *          delegating validator.
   * @param delegateConfiguration
   *          the delegates configuration element
   * @param delegateName
   *          the delegate's display name.
   * @param targetValidatorID
   *          the target validator's unique id.
   */
  public ValidatorDelegateDescriptor(String id, IConfigurationElement delegateConfiguration, String delegateName, String targetValidatorID)
  {
    this.id = id;
    this.delegateConfiguration = delegateConfiguration;
    this.name = delegateName;
    this.delegatingValidatorID = targetValidatorID;
  }

  /**
   * Provides the delegate's ID.
   * 
   * @return a string with the fully qualified class name of this validator
   *         implementation.
   */
  public String getId()
  {
    return id;
  }

  /**
   * Provides the delegate's name.
   * 
   * @return a String with the validator's display name.
   */
  public String getName()
  {
    return name;
  }

  /**
   * Retrieves the target validator's ID.
   * 
   * @return a String with the fully qualified class name of the delegating
   *         validator who will delegate its implementation to the validator
   *         described by this descriptor.
   */
  public String getTargetID()
  {
    return delegatingValidatorID;
  }

  /**
   * Provides the instance of the validator delegate pointed to by this
   * descriptor.
   * 
   * @return an IValidator instance.
   * @throws ValidationException
   */
  public IValidator getValidator() throws ValidationException
  {
    try
    {
      IValidator delegate = (IValidator) delegateConfiguration.createExecutableExtension(ValidatorDelegatesRegistryReader.CLASS_ATTRIBUTE);
      return delegate;
    }
    catch (CoreException e)
    {
      String delegatingValidatorName = ValidationRegistryReader.getReader().getValidatorMetaData(getTargetID()).getValidatorDisplayName();
      throw new ValidationException(new LocalizedMessage(IMessage.HIGH_SEVERITY, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_CANNOT_INSTANTIATE_DELEGATE, new String[] { getName(), delegatingValidatorName })));
    }
  }
  
  /**
   * Answer an AbstractValidator  if one has been defined, otherwise answer null. 
   */
  public AbstractValidator getValidator2() throws ValidationException {
	    try
	    {
	      Object object = delegateConfiguration.createExecutableExtension(ValidatorDelegatesRegistryReader.CLASS_ATTRIBUTE);
	      if (object instanceof AbstractValidator) {
				return (AbstractValidator) object;
	      }
	    }
	    catch (CoreException e)
	    {
	      String delegatingValidatorName = ValidationRegistryReader.getReader().getValidatorMetaData(getTargetID()).getValidatorDisplayName();
	      throw new ValidationException(new LocalizedMessage(IMessage.HIGH_SEVERITY, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_CANNOT_INSTANTIATE_DELEGATE, new String[] { getName(), delegatingValidatorName })));
	    }
	    return null;
  }
}
