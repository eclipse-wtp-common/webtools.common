package org.eclipse.wst.validation;

import org.eclipse.core.resources.IResource;

/**
 * A resource that is dependent on another resource.
 * <p>
 * This is returned by the IDependencyIndex.
 * @author karasiuk
 *
 */
public class DependentResource {
	private IResource 	_resource;
	private Validator	_validator;
	
	public DependentResource(IResource resource, Validator validator){
		_resource = resource;
		_validator = validator;
	}
	
	public IResource getResource() {
		return _resource;
	}
	public Validator getValidator() {
		return _validator;
	}
}
