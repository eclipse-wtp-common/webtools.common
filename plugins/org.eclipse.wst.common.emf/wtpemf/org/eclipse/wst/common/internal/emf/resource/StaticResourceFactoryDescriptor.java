package org.eclipse.wst.common.internal.emf.resource;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Declares a subclass to create Resource.Factory(ies) from an extension. 
 */
class StaticResourceFactoryDescriptor extends ResourceFactoryDescriptor {
	
	private final String shortSegment;
	private final Resource.Factory factory;
	
	/**
	 * 
	 * @param shortSegment A non-null name of the file associated with the given factory
	 * @param factory A non-null Resource.Factory that can load files of the given name
	 */
	public StaticResourceFactoryDescriptor(String shortSegment, Resource.Factory factory) {
		Assert.isNotNull(shortSegment);
		Assert.isNotNull(factory);
		this.shortSegment = shortSegment;
		this.factory = factory;
	}  

	public boolean isEnabledFor(URI fileURI) {
		/* shortSegment must be non-null for the descriptor to be created, 
		 * a validation check in init() verifies this requirement */
		if(fileURI != null && fileURI.lastSegment() != null)
			return shortSegment.equals(fileURI.lastSegment());
		return false;
	} 
	
	public Resource.Factory createFactory() {
		 return factory;			
	}

	public String getShortSegment() {
		return shortSegment;
	}  
}
