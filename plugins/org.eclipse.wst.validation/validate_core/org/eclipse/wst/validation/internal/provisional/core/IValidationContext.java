/*******************************************************************************
 * Copyright (c) 2001, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.provisional.core;

/**
 * This class is used to to separate the IValidator from the model loading. A model 
 * is a group of object(s) that must follow some rules.
 * <p>
 * The model is loaded differently depending on whether the validator is running in 
 * as-you-type or builder based context. If the <code>loadModel</code> method was a method on the IValidator, 
 * then there would need to be two versions of validators, one for as-you-type and one for 
 * builder based. Because <code>loadModel</code> is separate from the IValidator, we provide 
 * two different IHelpers instead, and ship the one specific to the environment.
 * </p>
 * <p>
 * Each IValidationContext implementation loads a specific model as identified by a String
 * <code>symbolicName</code>. The symbolicName can be any value except null or the 
 * empty string. Each validator identifies the symbolic names which it needs, and the
 * type of model which needs to be returned when that symbolic name is loaded via a 
 * <code>loadModel</code> method. An IValidationContext can support more than one IValidator; 
 * the helper needs to support every model that each validator needs.
 * </p>
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 */
public interface IValidationContext {
	/**
	 * <p>
	 * Load the model identified by <code>symbolicName</code>. <code>symbolicName</code> 
	 * must not be null or the empty string if the validator needs to be run in both 
	 * as-you-type and builder based validation.
	 * </p>
	 * <p>
	 * The symbolicName is a string name that is registered in the implementation of the
	 * IValidatorContext. For an example our internal implementation of the loadModel use
	 * method names for symbolicNames to load the model object to be validated. Users can
	 * use their own model load mechanism.
	 * </p>
	 * 
	 * @see <code>WorkbenchContext</code>
	 */
	Object loadModel(String symbolicName);

	/*
	 * 
	 * [issue : CS - It's not at all clear how these parameters would get passed into a helper.  I'd suggest
	 * providing getProperty() and setProperty() methods on the class to allow contextual information to be 
	 * supplied and queried in a generic manner.]
	 */
	/**
	 * <p>
	 * Load the model identified by <code>symbolicName</code> and <code>parms</code>.
	 * <code>symbolicName</code> must not be null or the empty string if the validator 
	 * needs to be run in both as-you-type and builder based validation. 
	 * If <code>parms</code> is null then this
	 * method behaves the same as
	 * 
	 * @link #loadModel(String).
	 * 
	 * This method differs from
	 * @link #loadModel(String) because it takes parameters, from the IValidator, which 
	 * 		 are available only at runtime. If you need to pass a live object to the IValidationContext, 
	 * 		 this is the loadModel method to use.
	 * </p>
	 * 
	 * @param symbolicName String identifier for model.
	 * @param parms 	   List of parameters that the model takes for invocation.
	 */
	Object loadModel(String symbolicName, Object[] parms);
	
	/**
	 * @return a string array with the list of URI's that need to be validated in the
	 * case of Incremental build.
	 */
	
	String[] getURIs();

}
