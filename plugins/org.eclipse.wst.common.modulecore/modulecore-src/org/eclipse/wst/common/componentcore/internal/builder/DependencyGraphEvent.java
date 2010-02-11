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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;

public class DependencyGraphEvent implements IDependencyGraphUpdateEvent {

	private int type = 0;
	private long modStamp = 0;

	/**
	 * Both these maps are reverse reference maps as the ones are in
	 * {@link IDependencyGraph}
	 */
	private Map<IProject, Set<IProject>> addedReferences = null;
	private Map<IProject, Set<IProject>> removedReferences = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.wst.common.componentcore.internal.builder.
	 * IDependencyGraphUpdateEvent#getType()
	 */
	public int getType() {
		return type;
	}

	void setModStamp(long modStamp) {
		this.modStamp = modStamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.wst.common.componentcore.internal.builder.
	 * IDependencyGraphUpdateEvent#getModStamp()
	 */
	public long getModStamp() {
		return modStamp;
	}

	void addRefererence(IProject sourceProject, IProject targetProject) {
		if (sourceProject == null) {
			throw new NullPointerException("Source project must not be null.");
		}
		if (targetProject == null) {
			throw new NullPointerException("Target project must not be null.");
		}
		if (addedReferences == null) {
			type = type | ADDED;
			addedReferences = new HashMap<IProject, Set<IProject>>();
		}
		Set references = addedReferences.get(targetProject);
		if (references == null) {
			references = new HashSet<IProject>();
			addedReferences.put(targetProject, references);
		}
		references.add(sourceProject);
	}

	void removeReference(IProject sourceProject, IProject targetProject) {
		if (sourceProject == null) {
			throw new NullPointerException("Source project must not be null.");
		}
		if (targetProject == null) {
			throw new NullPointerException("Target project must not be null.");
		}
		if (removedReferences == null) {
			type = type | REMOVED;
			removedReferences = new HashMap<IProject, Set<IProject>>();
		}
		Set references = removedReferences.get(targetProject);
		if (references == null) {
			references = new HashSet<IProject>();
			removedReferences.put(targetProject, references);
		}
		references.add(sourceProject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.wst.common.componentcore.internal.builder.
	 * IDependencyGraphUpdateEvent#getAddedReferences()
	 */
	public Map<IProject, Set<IProject>> getAddedReferences() {
		if (addedReferences == null) {
			return Collections.EMPTY_MAP;
		}
		return addedReferences;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.wst.common.componentcore.internal.builder.
	 * IDependencyGraphUpdateEvent#getRemovedReferences()
	 */
	public Map<IProject, Set<IProject>> getRemovedReferences() {
		if (removedReferences == null) {
			return Collections.EMPTY_MAP;
		}
		return removedReferences;
	}

}
