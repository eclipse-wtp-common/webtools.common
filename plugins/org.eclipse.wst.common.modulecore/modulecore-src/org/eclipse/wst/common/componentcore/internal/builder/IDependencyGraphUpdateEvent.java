/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;

public interface IDependencyGraphUpdateEvent {

	public static int ADDED = 1;
	public static int REMOVED = 2;

	
	/**
	 * Returns a bitwise or of the reference change types.
	 * 
	 * @see {@link #ADDED} {@link #REMOVED}
	 * @return
	 */
	public abstract int getType();

	/**
	 * Returns the modification stamp for the last update change in the
	 * {@link IDependencyGraph} being notified by this event.
	 * 
	 * Note that updates to the {@link IDependencyGraph} may be queued so
	 * several are handled by a single event.
	 * 
	 * @see IDependencyGraph#getModStamp()
	 * 
	 * @return
	 */
	public abstract long getModStamp();

	/**
	 * The key contains the referenced component, the value contains the set of
	 * referencing components recently added. This map will only contain the
	 * changes since the last event was fired. If {@link #getType()} |
	 * {@link #ADDED} != {@link #ADDED} then this will be an empty map.
	 * 
	 * Note that the changes specified by this map do not necessarily reflect
	 * the current state of the {@link IDependencyGraph} because it is possible
	 * that additional changes have occurred since this event was fired.
	 * {@link IDependencyGraph#getReferencingComponents(IProject)} will return
	 * the current reference state.
	 * 
	 * @return
	 */
	public abstract Map<IProject, Set<IProject>> getAddedReferences();

	/**
	 * The key contains the referenced component, the value contains the set of
	 * referencing components recently removed. This map will only contain the
	 * changes since the last event was fired. If {@link #getType()} |
	 * {@link #REMOVED} != {@link #REMOVED} then this will be an empty map.
	 * 
	 * Note that the changes specified by this map do not necessarily reflect
	 * the current state of the {@link IDependencyGraph} because it is possible
	 * that additional changes have occurred since this event was fired.
	 * {@link IDependencyGraph#getReferencingComponents(IProject)} will return
	 * the current reference state.
	 * 
	 * @return
	 */
	public abstract Map<IProject, Set<IProject>> getRemovedReferences();

}
