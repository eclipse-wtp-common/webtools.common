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
package org.eclipse.wst.validation.internal.provisional.core;

/**
 * <p>
 * This interface is used, for incremental validation, to encapsulate the change 
 * status of a resource. IFileDelta contains the name of the file which has 
 * changed, and a flag which indicates the type of change which has occurred.
 * 
 * @since 1.0
 * </p>
 * [issue: LM - From speaking with Vijay the name IFileDelta doesn't really capture
 *  what this interface is for. Validation may occur of files, it may occur on resources,
 *  or it may occur on something more abstract simply known as an artifact. The delta part
 *  of the name also seems misleading as this interface doesn't provide the delta but
 *  rather provides the type of change. I suggest renaming this interface to IModifiedArtifact
 *  or something along those lines to better capture its use. ]
 */
public interface IFileDelta {
	public static final int ADDED = 1;   // the file has been added
	public static final int CHANGED = 2; // the file has been changed
	public static final int DELETED = 3; // the file has been deleted

	/**
	 * <p>
	 * Returns the flag which indicates the type of change which has occurred:
	 * IFileDelta.ADDED, IFileDelta.CHANGED, or IFileDelta.DELETED.
	 * </p>
	 * @return returns the delta type.
	 * @since WTP 1.0
	 * 
	 * [issue: LM - From my experience a validator simply needs to know what artifact needs
	 *  to be validated. Can you provide a use case where the type of change is needed for
	 *  validation? ]
	 */
	public int getDeltaType();

	/**
	 * <p>
	 * Returns the name of the eclipse resource file which has changed. The return value must not be 
	 * null or the empty string ("").
	 * </p>
	 * @return returns the file name.
	 * @since WTP 1.0
	 * 
	 * [issue: LM - Following my comments above this method should be renamed to getArtifactName or
	 *  something that follows along with the interface rename. ]
	 */
	public String getFileName();
}