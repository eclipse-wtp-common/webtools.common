/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.validation.internal.model;

/**
 * A mutable object that is used to initialize a GlobalPreference.
 * @author karasiuk
 *
 */
public class GlobalPreferencesValues {
	public boolean disableAllValidation = GlobalPreferences.DefaultSuspend;
	public boolean saveAutomatically = GlobalPreferences.DefaultAutoSave;
	public boolean confirmDialog = GlobalPreferences.DefaultConfirm;
	public boolean override = GlobalPreferences.DefaultOverride;
	
	/** The plug-in state time stamp. */
	public long	stateTimeStamp;
	
	/** The incoming version of the framework. This is used to determine if a migration is needed.*/
	public int		version;
	
	public GlobalPreferencesValues(){
		
	}

}
