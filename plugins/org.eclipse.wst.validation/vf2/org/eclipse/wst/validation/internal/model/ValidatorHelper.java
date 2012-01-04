/*******************************************************************************
 * Copyright (c) 2005, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.validation.internal.ValidatorMutable;

/**
 * Implement some common validator methods, that don't need to be part of the API.
 * 
 * @author karasiuk
 *
 */
public final class ValidatorHelper {
	
	/**
	 * Answer true if this validator already has an exclude filter.
	 */
	public static boolean hasExcludeGroup(ValidatorMutable v){
		for (FilterGroup group : v.getGroups())if (group.isExclude())return true;
		return false;		
	}
	
	/**
	 * Return all the Runtimes defined in the workspace.
	 */
	public static Object[] getRuntimes()
	{
		Iterator<IRuntime> runtimes = RuntimeManager.getRuntimes().iterator();
		List <IRuntime> list = new ArrayList<IRuntime>();
		
		while (runtimes.hasNext()) {
			IRuntime runtime1 = (IRuntime) runtimes.next();
			String id = runtime1.getProperty("id"); //$NON-NLS-1$
			
			if(id != null){
				list.add(runtime1);
			}
		}
		
		return list.toArray();
	}
	
	
	/**
	 * Return the ID of the given target Runtime.
	 * @param targetRuntime
	 * @return
	 */
	public static String getRuntimeID(Object targetRuntime)
	{
		IRuntime runtime = (IRuntime) targetRuntime;
		return runtime.getProperty("id"); //$NON-NLS-1$
	}
	
	/**
	 * Return the target runtime name of the given the ID
	 * @param id
	 * @return
	 */
	public static String getRuntimeName(String id)
	{
		IRuntime runtime = RuntimeManager.getRuntime(id);
		if(runtime != null)
			return runtime.getLocalizedName();
		
		return null;
	}
	
	/**
	 * Return the Name of the given target runtime.
	 * @param targetRuntime
	 * @return
	 */
	public static String getRuntimeName(Object targetRuntime)
	{
		IRuntime runtime = (IRuntime) targetRuntime;
		
		return runtime.getLocalizedName();
	}
}
