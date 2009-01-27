/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.model.IValidatorVisitor;

/**
 * Keep track of the disabled validators.
 * @author karasiuk
 *
 */
public final class DisabledValidatorManager implements IValChangedListener {
	
	private static final AtomicInteger _counter = new AtomicInteger();
	private static final int CacheSize = 5;
	
	private final Map<IResource, LRUSet> _map = Collections.synchronizedMap(new HashMap<IResource, LRUSet>(5));
	
	public static DisabledValidatorManager getDefault(){
		return Singleton.disabledValidatorManager;
	}
	
	private DisabledValidatorManager(){
		ValPrefManagerProject.addListener(this);
	}
	
	/*
	 * Although this is currently not called, it should be if this ever stops being a singleton.
	 */
	public void dispose(){
		ValPrefManagerProject.removeListener(this);
	}

	public Set<Validator> getDisabledValidatorsFor(IResource resource) {
		LRUSet set = _map.get(resource);
		if (set != null){
			set.counter = _counter.getAndIncrement();
			return set.validators;
		}
		
		DisabledValidationFinder dvf = new DisabledValidationFinder();
		Set<Validator> vset = dvf.findDisabledValidatorsFor(resource);
		insert(resource, vset);
		return vset;		
	}
	
	private synchronized void insert(IResource resource, Set<Validator> vset) {
		if (_map.size() >= CacheSize ){
			IResource oldest = null;
			int current = Integer.MAX_VALUE;
			for (Map.Entry<IResource, LRUSet> me : _map.entrySet()){
				if (me.getValue().counter < current){
					oldest = me.getKey();
					current = me.getValue().counter;
				}
			}
			_map.remove(oldest);
		}
		LRUSet set = new LRUSet();
		set.counter = _counter.getAndIncrement();
		set.validators = vset;
		_map.put(resource, set);		
	}


	private final static class LRUSet {
		int counter;
		Set<Validator> validators;
	}
	
	private final static class DisabledValidationFinder implements IValidatorVisitor {
		
		private Map<String, Validator> _validators;

		public void visit(Validator validator, IProject project, ValType valType, ValOperation operation,
				IProgressMonitor monitor) {
			
			_validators.remove(validator.getId());
			
		}
		
		public Set<Validator> findDisabledValidatorsFor(IResource resource) {
			ValManager vm = ValManager.getDefault();
			Validator[] vals = vm.getValidatorsCopy();
			_validators = new HashMap<String, Validator>(100);
			for (Validator v : vals)_validators.put(v.getId(), v);
						
			IProject project = resource.getProject();
			vm.accept(this, project, resource, ValType.Build, new ValOperation(), new NullProgressMonitor());
			Set<Validator> set = new HashSet<Validator>(_validators.size());
			set.addAll(_validators.values());
			return set;
		}
		
		
	}

	public void validatorsForProjectChanged(IProject project, boolean configSettingChanged) {
		_map.clear();
	}
	
	
	/**
	 * Store the singleton for the DisabledValidatorManager. This approach is used to avoid having to synchronize the
	 * DisabledValidatorManager.getDefault() method.
	 * 
	 * @author karasiuk
	 *
	 */
	private final static class Singleton {
		final static DisabledValidatorManager disabledValidatorManager = new DisabledValidatorManager();
	}


}
