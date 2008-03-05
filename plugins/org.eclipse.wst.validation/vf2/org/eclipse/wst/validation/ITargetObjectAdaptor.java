package org.eclipse.wst.validation;

import org.eclipse.core.resources.IResource;

/**
 * This is used to convert an arbitrary target object in a validation message (like an EObject) to an
 * IResource. The IResource can then be used as the target for IMarkers.
 * @author karasiuk
 *
 */
public interface ITargetObjectAdaptor {
	
	/** 
	 * Map an object to it's associated resource. 
	 * 
	 * @return null if an appropriate resource can not be found.
	 */ 
	public IResource findResource(Object object);

}
