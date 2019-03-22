/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

	private void setType(int newType){
		this.type = newType;
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
		//update removed references accordingly
		if(removedReferences != null){
			Set references = removedReferences.get(targetProject);
			if(references != null){
				if(references.remove(sourceProject)) {
					if(references.isEmpty()){
						removedReferences.remove(targetProject);
						if(removedReferences.isEmpty()){
							removedReferences = null;
							if((getType() & ADDED) == ADDED){
								setType(ADDED);
							} else {
								setType(0);
							}
						}
					}
					//there is no net change, so return.
					return;
				}
			}
		}
		
		if (addedReferences == null) {
			setType(getType() | ADDED);
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
		
		//updated added references accordingly
		if(addedReferences != null){
			Set references = addedReferences.get(targetProject);
			if(references != null){
				if(references.remove(sourceProject)){
					if(references.isEmpty()){
						addedReferences.remove(targetProject);
						if(addedReferences.isEmpty()){
							addedReferences = null;
							if((getType() & REMOVED) == REMOVED){
								setType(REMOVED);
							} else {
								setType(0);
							}
						}
					}
					//there is no net change, so return.
					return;
				}
			}
		}
		
		if (removedReferences == null) {
			setType(getType() | REMOVED);
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

	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer("Dependency Graph Event \n{\n getModStamp() = "+getModStamp()+"\n hashCode() = "+hashCode()+"\n");
		boolean added = (getType() & ADDED) == ADDED;
		if(added){
			buff.append(" ADDED:\n" );
			for(Iterator<Map.Entry<IProject, Set<IProject>>> iterator = getAddedReferences().entrySet().iterator(); iterator.hasNext();){
				Map.Entry<IProject, Set<IProject>> entry = iterator.next();
				buff.append("  " + entry.getKey().getName() + " -> {");
				for (Iterator<IProject> mappedProjects = entry.getValue().iterator(); mappedProjects.hasNext();) {
					buff.append(mappedProjects.next().getName());
					if (mappedProjects.hasNext()) {
						buff.append(", ");
					}
				}
				buff.append("}\n");
			}
		}
		boolean removed = (getType() & REMOVED) == REMOVED;
		if(removed){
			buff.append(" REMOVED:\n" );
			for(Iterator<Map.Entry<IProject, Set<IProject>>> iterator = getRemovedReferences().entrySet().iterator(); iterator.hasNext();){
				Map.Entry<IProject, Set<IProject>> entry = iterator.next();
				buff.append("  " + entry.getKey().getName() + " -> {");
				for (Iterator<IProject> mappedProjects = entry.getValue().iterator(); mappedProjects.hasNext();) {
					buff.append(mappedProjects.next().getName());
					if (mappedProjects.hasNext()) {
						buff.append(", ");
					}
				}
				buff.append("}\n");
			}
		}
		buff.append("}\n");
		return buff.toString();
	}
	
}
