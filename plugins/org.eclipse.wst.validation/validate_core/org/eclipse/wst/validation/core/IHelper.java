/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.core;


/**
 * <p>
 * This class is used to to separate the IValidator from the model loading. A model 
 * is a group of object(s) that must follow some rules.
 * </p>
 * <p>
 * The model is loaded differently depending on whether the validator is running in 
 * UI or headless context. If the <code>loadModel</code> method was a method on the IValidator, 
 * then there would need to be two versions of validators, one for headless and one for 
 * UI. Because <code>loadModel</code> is separate from the IValidator, we provide 
 * two different IHelpers instead, and ship the one specific to the environment.
 * </p>
 * <p>
 * Each IHelper implementation loads a specific model as identified by a String
 * <code>symbolicName</code>. The symbolicName can be any value except null or the 
 * empty string. Each validator identifies the symbolic names which it needs, and the
 * type of model which needs to be returned when that symbolic name is loaded via a 
 * <code>loadModel</code> method. An IHelper can support more than one IValidator; 
 * the helper needs to support every model that each validator needs.
 * </p>
 * 
 * [issue : CS - This is a rather strange interface. Perhaps a better name would be 'IValidatorContext' 
 * The word 'helper' seems very vague.  The loadModel(String) method seems reasonable to me since 
 * a validator may need to load a model differently depending on the context its invoked from.
 * In any case I'd assume that the fileName that's provided via IValidator.validate() would be the only
 * obvious parameter that would get passed into loadModel().  We should make sure the param names are 
 * consistent (i.e. symbolicName should by fileName). We also need to give some documentation to demonstrate
 * how a validator would typically invoke the loadModel(String) methods from within the validate() 
 * method body to obtain a 'context specific' model]
 */
public interface IHelper {
	/**
	 * <p>
	 * Load the model identified by <code>symbolicName</code>.<code>symbolicName</code> 
	 * must not be null or the empty string if the validator needs to be run in both 
	 * Headless and UI.
	 * </p>
	 */
	public Object loadModel(String symbolicName);

	/**
	 * <p>
	 * Load the model identified by <code>symbolicName</code> and <code>parms</code>.
	 * <code>symbolicName</code> must not be null or the empty string if the validator 
	 * needs to be run in both Headless and UI. If <code>parms</code> is null then this
	 * method behaves the same as
	 * 
	 * @link #loadModel(String).
	 * 
	 * This method differs from
	 * @link #loadModel(String) because it takes parameters, from the IValidator, which 
	 * 		 are available only at runtime. If you need to pass a live object to the IHelper, 
	 * 		 this is the loadModel method to use.
	 * </p>
	 * @param symbolicName String identifier for model
	 * @param parms 	   parms list of parameters that the model takes for invocation
	 * 
	 * [issue : CS - It's not at all clear how these parameters would get passed into a helper.  I'd suggest
	 * providing getProperty() and setProperty() methods on the class to allow contextual information to be 
	 * supplied and queried in a generic manner.]
	 */
	public Object loadModel(String symbolicName, Object[] parms);
}