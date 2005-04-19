/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.wst.common.componentcore.internal.builder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.wst.common.componentcore.internal.resources.ComponentHandle;

public class DependencyGraph {
	
	private static final DependencyGraph INSTANCE = new DependencyGraph();
	
	private final Map dependencies = new HashMap();
	
	public static DependencyGraph getInstance() {
		return INSTANCE;
	}

	public ComponentHandle[] getReferencingComponents(ComponentHandle target) {
		Set referencingComponents = internalGetReferencingComponents(target);
		return (ComponentHandle[]) referencingComponents.toArray(new ComponentHandle[referencingComponents.size()]);
	}
	
	public void addReference(ComponentHandle target, ComponentHandle referencingComponent) {
		internalGetReferencingComponents(target).add(referencingComponent);
	}
	
	public void removeReference(ComponentHandle target, ComponentHandle referencingComponent) {
		internalGetReferencingComponents(target).remove(referencingComponent);
		
	}
	
	protected Set internalGetReferencingComponents(ComponentHandle target) {
		Set referencingComponents = (Set) dependencies.get(target);
		if(referencingComponents == null) 
			dependencies.put(target, (referencingComponents = new HashSet()));
		return referencingComponents;
	}
}
