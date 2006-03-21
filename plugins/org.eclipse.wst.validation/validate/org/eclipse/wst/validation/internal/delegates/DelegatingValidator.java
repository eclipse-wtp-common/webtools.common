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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ResourceConstants;
import org.eclipse.wst.validation.internal.ResourceHandler;
import org.eclipse.wst.validation.internal.ValidationRegistryReader;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.LocalizedMessage;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IProjectValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * This class is to be used as a base class by clients who want to provide
 * alternate validator implementations for a given validator type.
 * 
 * It locates the currently configured delegate for this validator and calls its
 * validate method.
 * 
 * @see IValidator
 * @see IDelegatingValidator
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still
 * under development and expected to change significantly before reaching
 * stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 * </p>
 */
public class DelegatingValidator implements IDelegatingValidator
{
  /**
   * Default constructor.
   */
  public DelegatingValidator()
  {
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#cleanup(org.eclipse.wst.validation.internal.provisional.core.IReporter)
   */
  public void cleanup(IReporter reporter)
  {
    // [Issue] This method does not get passed the validation context. How are
    // going to know the delegate in order to invoke its cleanup method?
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#validate(org.eclipse.wst.validation.internal.provisional.core.IValidationContext,
   *      org.eclipse.wst.validation.internal.provisional.core.IReporter)
   */
  public void validate(IValidationContext helper, IReporter reporter) throws ValidationException
  {
    ValidatorMetaData vmd = ValidationRegistryReader.getReader().getValidatorMetaData(this);
    String validatorName = vmd.getValidatorDisplayName();

    // We need to ensure that the context is an IProjectValidationContext. The
    // limitation of using an IValidationContext is that it does not readily
    // provide the project the validator is being invoked upon.

    if (!(helper instanceof IProjectValidationContext))
    {
      throw new ValidationException(new LocalizedMessage(IMessage.HIGH_SEVERITY, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_WRONG_CONTEXT_FOR_DELEGATE, new String[] { validatorName })));
    }

    IProjectValidationContext projectContext = (IProjectValidationContext) helper;
    IProject project = projectContext.getProject();

    ValidatorDelegateDescriptor delegateDescriptor = null;

    try
    {
      ProjectConfiguration projectConfig = ConfigurationManager.getManager().getProjectConfiguration(project);

      delegateDescriptor = projectConfig.getDelegateDescriptor(vmd);
    }
    catch (InvocationTargetException e)
    {
      // Already dealt with by the framework.
    }
    finally
    {
      if (delegateDescriptor == null)
      {
        throw new ValidationException(new LocalizedMessage(IMessage.HIGH_SEVERITY, ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_NO_DELEGATE, new String[] { vmd.getValidatorDisplayName() })));
      }
    }

    IValidator delegate = delegateDescriptor.getValidator();

    // We need to make it look like this validator is the one generating
    // messages so we wrap the reporter and use this validator as the source.
    // The validation framework does not recognize our validators because they
    // are not registered directly with the framework.
    // We could make them work like the aggregated validators but that would
    // create problems with markers not being cleaned up if someone switches
    // validators.
    // TODO : Maybe we could clear all the markers generated by a delegate when
    // the user chooses a different delegate implementation?

    DelegatingReporter delegatingReporter = new DelegatingReporter(this, reporter);

    delegate.validate(helper, delegatingReporter);
  }

  /**
   * Wraps the original reporter instance to make it look like this validator is
   * the one generating messages. This is needed because the framework ignores
   * messages coming from the delegate validator because it is not registered
   * with the validation framework.
   */
  private class DelegatingReporter implements IReporter
  {
    /**
     * The reporter passed originally to the delegating validator by the
     * framework.
     */
    IReporter delegatingReporter;

    /**
     * The delegating validator.
     */
    IValidator delegatingValidator;

    /**
     * Constructor.
     * 
     * @param validator
     *          the original validator.
     * @param reporter
     *          the reporter originally passed to the delegating validator.
     */
    DelegatingReporter(IValidator validator, IReporter reporter)
    {
      delegatingReporter = reporter;
      delegatingValidator = validator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.validation.internal.provisional.core.IReporter#addMessage(org.eclipse.wst.validation.internal.provisional.core.IValidator,
     *      org.eclipse.wst.validation.internal.provisional.core.IMessage)
     */
    public void addMessage(IValidator origin, IMessage message)
    {
      delegatingReporter.addMessage(delegatingValidator, message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.validation.internal.provisional.core.IReporter#displaySubtask(org.eclipse.wst.validation.internal.provisional.core.IValidator,
     *      org.eclipse.wst.validation.internal.provisional.core.IMessage)
     */
    public void displaySubtask(IValidator validator, IMessage message)
    {
      delegatingReporter.displaySubtask(delegatingValidator, message);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.validation.internal.provisional.core.IReporter#getMessages()
     */
    public List getMessages()
    {
      return delegatingReporter.getMessages();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.validation.internal.provisional.core.IReporter#isCancelled()
     */
    public boolean isCancelled()
    {
      return delegatingReporter.isCancelled();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.validation.internal.provisional.core.IReporter#removeAllMessages(org.eclipse.wst.validation.internal.provisional.core.IValidator)
     */
    public void removeAllMessages(IValidator origin)
    {
      delegatingReporter.removeAllMessages(delegatingValidator);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.validation.internal.provisional.core.IReporter#removeAllMessages(org.eclipse.wst.validation.internal.provisional.core.IValidator,
     *      java.lang.Object)
     */
    public void removeAllMessages(IValidator origin, Object object)
    {
      delegatingReporter.removeAllMessages(delegatingValidator, object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.validation.internal.provisional.core.IReporter#removeMessageSubset(org.eclipse.wst.validation.internal.provisional.core.IValidator,
     *      java.lang.Object, java.lang.String)
     */
    public void removeMessageSubset(IValidator validator, Object obj, String groupName)
    {
      delegatingReporter.removeMessageSubset(delegatingValidator, obj, groupName);
    }
  }
}
