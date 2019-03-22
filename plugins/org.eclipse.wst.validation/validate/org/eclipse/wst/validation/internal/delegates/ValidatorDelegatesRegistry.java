/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.validation.internal.delegates;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.validation.internal.ConfigurationConstants;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * ValidatorDelegatesRegistry is a singleton used to store validator delegate
 * descriptors for each delegating validator.
 */
public class ValidatorDelegatesRegistry {
	
  /**
   * The one and only instance of this registry.
   */
  private static ValidatorDelegatesRegistry _instance;

  /**
   * Provides the one and only instance of this class.
   * 
   * The actual platform extension registry reading happens at this time.
   * 
   * @see ValidatorDelegatesRegistryReader
   * 
   * @return the validator delegates registry singleton instance.
   */
  public static ValidatorDelegatesRegistry getInstance() {
    if (_instance == null) {
    	ValidatorDelegatesRegistry instance = new ValidatorDelegatesRegistry();
    	ValidatorDelegatesRegistryReader reader = new ValidatorDelegatesRegistryReader(instance);
    	reader.readRegistry();
    	instance.initDefaults();
    	_instance = instance;
    }

    return _instance;
  }

  /**
   * Determine if the product defined any default delegate validators, by setting the old
   * DELEGATES_PREFERENCE preference in the default preference scope. If they did, we remember
   * them, so that if we every need to return a default for the delegating validator, we can return the one
   * that the product requested.
   */
  private void initDefaults() {
	  	// This code was copied from the ValidationConfiguration#deserializeDelegates() method. The code was copied because this
	  	// change is part of a patch and I didn't want to disturb the ValidationConfiguration class.
	  
		IEclipsePreferences prefs = new DefaultScope().getNode(ValidationPlugin.PLUGIN_ID);
		String delegatePref = prefs.get("DELEGATES_PREFERENCE", null); //$NON-NLS-1$
		if (delegatePref == null)return;

		int delegatesIndex = delegatePref.indexOf(ConfigurationConstants.DELEGATE_VALIDATORS);
		String delegates = delegatePref.substring(delegatesIndex + ConfigurationConstants.DELEGATE_VALIDATORS.length(), delegatePref.length());

		if (delegates == null)return;

		_defaultDelegates = new HashSet<String>(10);
		StringTokenizer tokenizer = new StringTokenizer(delegates, ConfigurationConstants.ELEMENT_SEPARATOR);
		while (tokenizer.hasMoreTokens()) {
			String delegateConfiguration = tokenizer.nextToken();
			int separatorIndex = delegateConfiguration.indexOf(ConfigurationConstants.DELEGATES_SEPARATOR);
//			String targetID = delegateConfiguration.substring(0, separatorIndex);
			String delegateID = delegateConfiguration.substring(separatorIndex + 1);
			_defaultDelegates.add(delegateID);
		}

	}

/**
   * The map of target validator id to Map of delegates by id.
   */
  private Map<String, Map<String,ValidatorDelegateDescriptor>> _delegatesByTarget = 
	  new HashMap<String, Map<String,ValidatorDelegateDescriptor>>();
  
  /**
   * Validator ids that have been defined by the product to be the default validators, for some of the delegating validtors.
   */
  private Set<String> _defaultDelegates;

  /**
   * Adds a descriptor to the registry.
   * 
   * @param descriptor
   *          The descriptor to add. Must not be null.
   */
  void add(ValidatorDelegateDescriptor descriptor) {
    if (descriptor == null)return;

    String targetID = descriptor.getTargetID();

    Map<String,ValidatorDelegateDescriptor> delegates = _delegatesByTarget.get(targetID);

    if (delegates == null) {
      delegates = new HashMap<String,ValidatorDelegateDescriptor>();
      _delegatesByTarget.put(targetID, delegates);
    }

    delegates.put(descriptor.getId(), descriptor);
  }

  /**
   * Provides the default delegate ID for the given delegating validator ID.
   * 
   * @param targetID
   *          The delegating validator's ID.
   * @return a String with the ID of the default delegate.
   */
  public String getDefaultDelegate(String targetID)
  {
    Map<String,ValidatorDelegateDescriptor> delegates = getDelegateDescriptors(targetID);

    if (delegates == null)return null;

    if (_defaultDelegates != null){
    	for (ValidatorDelegateDescriptor vdd : delegates.values()){
    		String id = vdd.getId();
    		if (_defaultDelegates.contains(id))return id;
    	}
    }
    
    // TODO: Implement a default attribute and use that to get the default?
    // What happens if two or more delegates claim to be the default one?
    // For now, just pick the first one in the list.

    Iterator<ValidatorDelegateDescriptor> delegatesIterator = delegates.values().iterator();
    
    if (!delegatesIterator.hasNext())return null;

    return delegatesIterator.next().getId();
  }

  /**
   * Retrieves a delegate instance based on the target and delegate IDs.
   * 
   * @param targetID
   *          the delegating validator's ID.
   * @param delegateID
   *          the delegate validator's ID.
   * @return an instance of the delegate validator or null if one cannot be
   *         found.
   */
  public IValidator getDelegate(String targetID, String delegateID) throws ValidationException
  {
    ValidatorDelegateDescriptor descriptor = getDescriptor(targetID, delegateID);

    if (descriptor == null)return null;

    IValidator delegate = descriptor.getValidator();

    return delegate;
  }

  /**
   * Provides a map with all delegates descriptors for the given target, keyed
   * by their ID.
   * 
   * @param targetID
   *          the target (delegating) validator id. Must not be null.
   * @return a Map <string, ValidatorDelegateDescriptor>. May be null if the ID
   *         passed in is not a delegating validator.
   */
  public Map<String,ValidatorDelegateDescriptor> getDelegateDescriptors(String targetID) {
    return _delegatesByTarget.get(targetID);
  }

  /**
   * Finds a delegate descriptor based on the target id and delegate id.
   * 
   * @param targetID
   *          the delegating validator's id
   * @param delegateID
   *          the delegate id
   * @return a ValidatorDelegateDescriptor for the given target and id or null
   *         if one cannot be found.
   */
  public ValidatorDelegateDescriptor getDescriptor(String targetID, String delegateID)
  {
    Map<String,ValidatorDelegateDescriptor> delegates = _delegatesByTarget.get(targetID);

    if (delegates == null || delegateID == null)return null;
    
    ValidatorDelegateDescriptor descriptor = (ValidatorDelegateDescriptor) delegates.get(delegateID);

    return descriptor;
  }

  /**
   * Determines if a given validator has delegates.
   * 
   * @param targetID
   *          the target validator id.
   * @return true if the target has registered delegates, false otherwise.
   */
  public boolean hasDelegates(String targetID)
  {
    Map<String,ValidatorDelegateDescriptor> delegatesByID = _delegatesByTarget.get(targetID);

    boolean hasDelegates = (delegatesByID != null && !delegatesByID.isEmpty());
    return hasDelegates;
  }
}
