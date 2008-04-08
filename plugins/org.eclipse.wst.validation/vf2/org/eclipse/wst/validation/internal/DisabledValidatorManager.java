package org.eclipse.wst.validation.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
public class DisabledValidatorManager implements IValChangedListener {
	
	private static DisabledValidatorManager _me;
	private static int _counter;
	private static final int CacheSize = 5;
	
	private Map<IResource, LRUSet> _map = Collections.synchronizedMap(new HashMap<IResource, LRUSet>(5));
	
	public static DisabledValidatorManager getDefault(){
		DisabledValidatorManager me = _me;
		if (me != null)return me;
		me = new DisabledValidatorManager();
		_me = me;
		return me;
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
			set.counter = _counter++;
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
		set.counter = _counter++;
		set.validators = vset;
		_map.put(resource, set);		
	}


	private static class LRUSet {
		int counter;
		Set<Validator> validators;
	}
	
	private static class DisabledValidationFinder implements IValidatorVisitor {
		
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

}
