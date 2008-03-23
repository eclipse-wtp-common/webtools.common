/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation;

import java.util.HashMap;
import java.util.Map;

/**
 * Keep track of arbitrary validation data, during the course of a validation.
 * <p>
 * To enable more efficient caching, the validation framework, allows individual validators to cache
 * arbitrary data during the validation process. Use of this object is completely optional.
 * <p>
 * Since some validators wish to share data with other validators, any validator can see the state data for
 * any other validator, since the id is simply the validator's extension id.
 * <p>
 * This object is freed at the end of the validation process.
 * 
 * @author karasiuk
 *
 */
public class ValidationState {

	private Map<String, Object> _map = new HashMap<String, Object>(50);
	
	/**
	 * Save some state information.
	 * 
	 * @param id by convention this is the fully qualified validator extension id.
	 * For example: org.eclipse.wst.html.ui.HTMLValidator
	 * 
	 * @param value any arbitrary data that the validator might find useful. The validation framework
	 * doesn't do anything with this object except pass it along during the validation process. 
	 */
	public void put(String id, Object value){
		_map.put(id, value);
	}
	
	/**
	 * Answer the state data for the given validator.
	 * 
	 * @param id by convention this is the fully qualified validator extension point id.
	 * For example org.eclipse.wst.html.ui.HTMLValidator
	 * 
	 * @return any arbitrary data that the validator might find useful, including null.
	 */
	public Object get(String id){
		return _map.get(id);
	}

	
}
