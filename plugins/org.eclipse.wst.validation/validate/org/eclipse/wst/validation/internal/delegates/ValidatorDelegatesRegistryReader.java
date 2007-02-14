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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * This class reads the plugin extension registry and registers each delegating
 * validator descriptor with the delegates registry.
 * 
 * @see ValidatorDelegatesRegistry
 */
class ValidatorDelegatesRegistryReader
{
  /**
   * The delegate class attribute. Must be visible in the package because it is
   * used by other classes in the package.
   */
  static final String CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$

  /**
   * The delegate element name.
   */
  private static final String DELEGATE_ELEMENT = "delegate"; //$NON-NLS-1$

  /**
   * The validator delegates extension point id.
   */
  private static final String EXTENSION_POINT_ID = "validatorDelegates"; //$NON-NLS-1$

  /**
   * The delegate name attribute.
   */
  private static final String NAME_ATTRIBUTE = "name"; //$NON-NLS-1$

  /**
   * Plugin id.
   */
  private static final String PLUGIN_ID = "org.eclipse.wst.validation"; //$NON-NLS-1$

  /**
   * The target id attribute name.
   */
  private static final String TARGET_ATTRIBUTE = "target"; //$NON-NLS-1$

  /**
   * The validator registry where the descriptors being read will be placed.
   */
  private ValidatorDelegatesRegistry registry;

  /**
   * Constructor.
   * 
   * @param registry
   *          the registry where the descriptors being read will be placed.
   */
  public ValidatorDelegatesRegistryReader(ValidatorDelegatesRegistry registry)
  {
    this.registry = registry;
  }

  /**
   * Reads a configuration element.
   * 
   * @param element
   *          the platform configuration element being read.
   */
  private void readElement(IConfigurationElement element)
  {
    String elementName = element.getName();

    if (elementName.equals(DELEGATE_ELEMENT))
    {
      String delegateID = element.getAttribute(CLASS_ATTRIBUTE);
      String delegateName = element.getAttribute(NAME_ATTRIBUTE);
      String targetValidatorID = element.getAttribute(TARGET_ATTRIBUTE);

      ValidatorDelegateDescriptor descriptor = new ValidatorDelegateDescriptor(delegateID, element, delegateName, targetValidatorID);

      registry.add(descriptor);
    }
  }

  /**
   * Read from the extensions registry and parse it.
   */
  void readRegistry()
  {
    IExtensionRegistry pluginRegistry = Platform.getExtensionRegistry();
    IExtensionPoint point = pluginRegistry.getExtensionPoint(PLUGIN_ID, EXTENSION_POINT_ID);

    if (point != null)
    {
      IConfigurationElement[] elements = point.getConfigurationElements();

      for (int index = 0; index < elements.length; index++)
      {
        readElement(elements[index]);
      }
    }
  }
}
