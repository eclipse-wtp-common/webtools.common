/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

/**
 * Constants used in the extension points.
 * @author karasiuk
 *
 */
public interface ExtensionConstants {
	
	/** validatorV2 - extension point for registering validators that use version 2 of the validation framework. */
	String validator = "validatorV2"; //$NON-NLS-1$
	
	/** build - is the validator enabled by default for build based validations? true or false */
	String build = "build"; //$NON-NLS-1$
	
	/** class - name of the attribute that holds the validator class. */
	String AttribClass = "class"; //$NON-NLS-1$
	
	/** manual - is the validator enabled by default for manual based validations? true or false */
	String manual = "manual"; //$NON-NLS-1$
	
	/** 
	 * markerId - the marker id that the framework should use when creating customized markers for this validator.
	 * This is an optional attribute. If it is not supplied than the standard validation marker type will be used.
	 */
	String markerId = "markerId"; //$NON-NLS-1$
	
	/** 
	 * sourceid - If this validator also serves as an as-you-type validator (also know as an ISourceValidator) 
	 * then it's source id is specified here, so that the two validators can be associated with one 
	 * another. By source id, we mean the id that is used in the org.eclipse.wst.sse.ui.sourceValidation 
	 * extension point.
	 */
	String sourceId = "sourceid"; //$NON-NLS-1$
	
	/** 
	 * version - the version of this definition. The attribute is a simple integer, and if not specified it
	 * is assumed to be 1. This allows the filter settings to be changed in the future.
	 */
	String version = "version";  //$NON-NLS-1$
	
	/** 
	 * include - a group of inclusion rules. At least one rule in this group needs to match in order for the resource
	 * to to considered to be validated.
	 */
	String include = "include"; //$NON-NLS-1$
	
	/** exclude - a group of exclusion rules. If any of these rules match the resource is not validated. */
	String exclude = "exclude"; //$NON-NLS-1$
	
	/** rules - a group of inclusion or exclusion rules. */
	String rules = "rules"; //$NON-NLS-1$
	
	/** Different types of rules for filtering validation. */
	interface Rule {
		
		/** projectNature - filter by project nature. */
		String projectNature = "projectNature"; //$NON-NLS-1$
		
		/** fileext - filter by file extension. */
		String fileext = "fileext"; //$NON-NLS-1$
		
		/** file - a file name, it can include path information as well. */
		String file = "file"; //$NON-NLS-1$
		
		/** facet - filter by facet id. */
		String facet = "facet"; //$NON-NLS-1$
		
		/** contentType - filter by content type. */
		String contentType = "contentType"; //$NON-NLS-1$
	}
	
	/** Rule attributes */
	interface RuleAttrib {
		
		/** caseSensitive - true or false. */
		String caseSensitive = "caseSensitive"; //$NON-NLS-1$
		
		/** 
		 * exactMatch - true or false, default is true. It is used to decide whether content types need to match
		 * exactly, or whether sub types should also be considered.
		 */
		String exactMatch = "exactMatch"; //$NON-NLS-1$
		
		/** ext - a file extension, for example "html". */
		String ext = "ext"; //$NON-NLS-1$
		
		/** id - an identifier. */
		String id = "id"; //$NON-NLS-1$
		
		/** name - a file name. */
		String name = "name"; //$NON-NLS-1$
		
		/** 
		 * type - the type of file to be matched:
		 * <ul>
		 * <li>folder - project relative folder name
		 * <li>file - simple file name
		 * <li>full - fully qualified, project relative file name
		 * </ul>
		 */
		String fileType = "type"; //$NON-NLS-1$
	}
	
	interface FileType {
		/** folder - project relative folder name */
		String folder = "folder"; //$NON-NLS-1$
		
		/** file - simple file name */
		String file = "file"; //$NON-NLS-1$
		
		/** full - fully qualified, project relative file name */
		String full = "full"; //$NON-NLS-1$
	}
	
	interface MessageCategory {
		/** messageCategory - name of the message category element. */
		String name = "messageCategory"; //$NON-NLS-1$
		
		/** id - simple id of the message. */
		String id = "id"; //$NON-NLS-1$
		
		/** label = human readable label of the message category. */
		String label = "label"; //$NON-NLS-1$
		
		/** severity - message severity, it must be one of error, warning or ignore. */
		String severity = "severity"; //$NON-NLS-1$
		
		String sevError = "error"; //$NON-NLS-1$
		String sevWarning = "warning"; //$NON-NLS-1$
		String sevIgnore = "ignore";  //$NON-NLS-1$
	}
	
	/** true */
	String True = "true"; //$NON-NLS-1$
	
	/** false */
	String False = "false"; //$NON-NLS-1$
}
