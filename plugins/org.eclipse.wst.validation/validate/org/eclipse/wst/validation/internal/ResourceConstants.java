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
package org.eclipse.wst.validation.internal;



/**
 * Constants used to access the string resources associated with the plug-in.
 * 
 * See the plugin.properties file, in the plugin's directory, for the contents of the strings.
 */
public interface ResourceConstants {
	// A marker cannot be added to the task list.
	String VBF_EXC_CANNOT_ADD_MARKER = "VBF_EXC_CANNOT_ADD_MARKER"; //$NON-NLS-1$ 

	// A marker cannot be removed from the task list.
	String VBF_EXC_CANNOT_REMOVE_MARKER = "VBF_EXC_CANNOT_REMOVE_MARKER"; //$NON-NLS-1$ 

	// Internal error has occurred.
	String VBF_EXC_INTERNAL = "VBF_EXC_INTERNAL"; //$NON-NLS-1$ 

	// If the validator cannot be loaded because it didn't specify a helper
	String VBF_EXC_HELPER_MISSING = "VBF_EXC_HELPER_MISSING"; //$NON-NLS-1$ 

	// If the validator cannot be loaded because its helper cannot be loaded
	String VBF_EXC_HELPER_CANNOTLOAD = "VBF_EXC_HELPER_CANNOTLOAD"; //$NON-NLS-1$ 

	// A java.lang.Runtime error has occured during the build.
	String VBF_EXC_RUNTIME = "VBF_EXC_RUNTIME"; //$NON-NLS-1$ 

	// If the "Validator" extension point has been removed from the plugin.xml file.
	String VBF_EXC_MISSING_VALIDATOR_EP = "VBF_EXC_MISSING_VALIDATOR_EP"; //$NON-NLS-1$ 

	// If the user has specified an invalid type filter in their plugin.xml file.
	// i.e., it isn't an instance of IResource.
	String VBF_EXC_INVALID_TYPE_FILTER = "VBF_EXC_INVALID_TYPE_FILTER"; //$NON-NLS-1$ 

	// The validator extension has made a mistake in its plugin.xml's projectNature tag
	String VBF_EXC_MISSING_PROJECTNATURE_ID = "VBF_EXC_MISSING_PROJECTNATURE_ID"; //$NON-NLS-1$ 

	// The validator extension has made a mistake in its plugin.xml's syntax.
	String VBF_EXC_VALIDATORNAME_IS_NULL = "VBF_EXC_VALIDATORNAME_IS_NULL"; //$NON-NLS-1$ 

	// Title for the IProgressMonitor.
	String VBF_STATUS_PROGRESSMONITOR_TITLE = "VBF_STATUS_PROGRESSMONITOR_TITLE"; //$NON-NLS-1$ 

	// Status line for the IProgressMonitor
	String VBF_STATUS_INITIALIZING = "VBF_STATUS_INITIALIZING"; //$NON-NLS-1$ 

	// If the user has cancelled validation, each validator might have cleanup to do. This message
	// is shown to tell the user which validator is being cleaned up at the moment.
	String VBF_STATUS_VALIDATOR_CLEANUP = "VBF_STATUS_VALIDATOR_CLEANUP"; //$NON-NLS-1$ 

	// If the user cancelled validation, remove all of the validator's tasks from the task list, and
	// put an entry saying that validation on {project} using {validator} was cancelled.
	String VBF_STATUS_VALIDATOR_TERMINATED = "VBF_STATUS_VALIDATOR_TERMINATED"; //$NON-NLS-1$ 

	// Before a validator is begun, this message informs the user that validation, using a
	// particular validator, has begun
	String VBF_STATUS_STARTING_VALIDATION = "VBF_STATUS_STARTING_VALIDATION"; //$NON-NLS-1$ 

	// After a validator is finished, this message informs the user that validtaion, using a
	// particular validator, has completed.
	String VBF_STATUS_ENDING_VALIDATION = "VBF_STATUS_ENDING_VALIDATION"; //$NON-NLS-1$ 

	// If a validator throws an unchecked exception, this message is displayed to the user.
	String VBF_STATUS_ENDING_VALIDATION_ABNORMALLY = "VBF_STATUS_ENDING_VALIDATION_ABNORMALLY"; //$NON-NLS-1$ 

	// If the build's getDelta(getProject()) method returns null, let the user know that a full
	// validation will be performed because there's no delta information.
	String VBF_STATUS_NULL_DELTA = "VBF_STATUS_NULL_DELTA"; //$NON-NLS-1$ 

	String VBF_EXC_SYNTAX_NULL_NAME = "VBF_EXC_SYNTAX_NULL_NAME"; //$NON-NLS-1$ 
	String VBF_EXC_SYNTAX_NO_HELPER = "VBF_EXC_SYNTAX_NO_HELPER"; //$NON-NLS-1$ 
	String VBF_EXC_SYNTAX_NO_HELPER_CLASS = "VBF_EXC_SYNTAX_NO_HELPER_CLASS"; //$NON-NLS-1$ 
	String VBF_EXC_SYNTAX_NO_HELPER_THROWABLE = "VBF_EXC_SYNTAX_NO_HELPER_THROWABLE"; //$NON-NLS-1$ 
	String VBF_EXC_SYNTAX_NO_VAL_THROWABLE = "VBF_EXC_SYNTAX_NO_VAL_THROWABLE"; //$NON-NLS-1$ 
	String VBF_EXC_INVALID_RESOURCE = "VBF_EXC_INVALID_RESOURCE"; //$NON-NLS-1$ 

	String VBF_EXC_NULLCREATE = "VBF_EXC_NULLCREATE"; //$NON-NLS-1$ 
	String VBF_EXC_NULLSAVE = "VBF_EXC_NULLSAVE"; //$NON-NLS-1$ 
	String VBF_EXC_SAVE = "VBF_EXC_SAVE"; //$NON-NLS-1$ 
	String VBF_EXC_NULLRETRIEVE = "VBF_EXC_NULLRETRIEVE"; //$NON-NLS-1$ 
	String VBF_EXC_RETRIEVE = "VBF_EXC_RETRIEVE"; //$NON-NLS-1$ 

	String VBF_EXC_BADVMD = "VBF_EXC_BADVMD"; //$NON-NLS-1$ 
	String VBF_EXC_OPENPRJ = "VBF_EXC_OPENPRJ"; //$NON-NLS-1$ 
	String VBF_EXC_EXISTPRJ = "VBF_EXC_EXISTPRJ"; //$NON-NLS-1$ 
	String VBF_EXC_BADPRJ = "VBF_EXC_BADPRJ"; //$NON-NLS-1$ 
	String VBF_EXC_MULTIPRJ = "VBF_EXC_MULTIPRJ"; //$NON-NLS-1$ 
	String VBF_EXC_BADVAL = "VBF_EXC_BADVAL"; //$NON-NLS-1$ 

	String VBF_STATUS_START_REMOVING_OLD_MESSAGES = "VBF_STATUS_START_REMOVING_OLD_MESSAGES"; //$NON-NLS-1$ 
	String VBF_STATUS_FINISH_REMOVING_OLD_MESSAGES = "VBF_STATUS_FINISH_REMOVING_OLD_MESSAGES"; //$NON-NLS-1$ 

	String VBF_TASK_WARN_MESSAGE_LIMIT_VAL = "VBF_TASK_WARN_MESSAGE_LIMIT_VAL"; //$NON-NLS-1$
	
	String VBF_VALIDATION_JOB_MSG = "VBF_VALIDATION_JOB_MSG"; //$NON-NLS-1$

	String VBF_EXC_DISABLEV = "VBF_EXC_DISABLEV"; //$NON-NLS-1$ 
	String VBF_EXC_DISABLEH = "VBF_EXC_DISABLEH"; //$NON-NLS-1$ 
	String VBF_EXC_ORPHAN_IVALIDATOR = "VBF_EXC_ORPHAN_IVALIDATOR"; //$NON-NLS-1$ 

	String VBF_STATUS_LOOKING = "VBF_STATUS_LOOKING"; //$NON-NLS-1$ 
	String VBF_STATUS_LOOKINGDONE = "VBF_STATUS_LOOKINGDONE"; //$NON-NLS-1$ 
	String VBF_STATUS_REMOVING = "VBF_STATUS_REMOVING"; //$NON-NLS-1$ 
	String VBF_STATUS_REMOVINGDONE = "VBF_STATUS_REMOVINGDONE"; //$NON-NLS-1$
  
	String VBF_WRONG_CONTEXT_FOR_DELEGATE = "VBF_WRONG_CONTEXT_FOR_DELEGATE"; //$NON-NLS-1$
	String VBF_NO_DELEGATE = "VBF_NO_DELEGATE"; //$NON-NLS-1$
	String VBF_CANNOT_INSTANTIATE_DELEGATE = "VBF_CANNOT_INSTANTIATE_DELEGATE"; //$NON-NLS-1$  
}
