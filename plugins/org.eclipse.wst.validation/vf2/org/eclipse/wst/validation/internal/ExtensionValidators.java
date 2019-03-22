/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.validation.Validator;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;

/**
 * The validators that have been defined by the validator extension points.
 * @author karasiuk
 *
 */
public final class ExtensionValidators {
	private static ExtensionValidators _me;
	
	/** All the registered validators. The key is the validator id and the value is the validator. */
	private final Map<String, Validator> _map;
	
	public synchronized static ExtensionValidators instance(){
		if (_me == null){
			_me = new ExtensionValidators();
		}
		return _me;
	}

	private ExtensionValidators(){
		_map = new HashMap<String, Validator>(100);
		for (Validator v : ValidatorExtensionReader.getDefault().process()){
			_map.put(v.getId(), v);
		}		
	}
	
	/**
	 * Answer all the v2 validators that have been defined by the extension point. This is the real
	 * map (not a copy).
	 * 
	 * @return The key is the validator id and the value is the validator.
	 */
	public Map<String, Validator> getMapV2() {
		return _map;
	}

	/**
	 * Answer all the v2 validators that have been defined by the extension point. This is a copy of
	 * the map, but not a copy of the validators.
	 * 
	 * @return The key is the validator id and the value is the validator.
	 */
	public Map<String, Validator> getMapV2Copy() {
		HashMap<String, Validator> map = new HashMap<String, Validator>(_map.size());
		map.putAll(_map);
		return map;
	}
	
	
	/**
	 * Answer all the validators that have been defined by extension points. This includes all
	 * the v2 validators, and all of the v1 validators that have been defined just on this project.
	 * 
	 * @return The key is the validator id and the value is the validator.
	 */
	public Map<String, Validator> getMap(IProject project) {
		//TODO I may want to consider caching this, but then again if the v1 validators are going away
		// it may not be worth it.
		Map<String, Validator> map = new HashMap<String, Validator>();
		map.putAll(_map);
		
		for (Validator v : getV1Validators(project))map.put(v.getId(), v);
		
		return map;
	}
	
	/**
	 * Answer the v1 validators that have been defined just on this project.
	 * @param project
	 * @return
	 */
	List<Validator> getV1Validators(IProject project){
		List<Validator> list = new LinkedList<Validator>();
		try {
			ProjectConfiguration pc = new ProjectConfiguration(project);
			pc.resetToDefault();
			for (ValidatorMetaData vmd : pc.getValidators()){
				Validator v = Validator.create(vmd, pc, project);
				list.add(v);
			}
		}
		catch (InvocationTargetException e){
			ValidationPlugin.getPlugin().handleException(e);
		}

		return list;
	}

}
