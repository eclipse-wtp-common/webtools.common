package org.eclipse.jem.util.emf.workbench;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;


public interface FlexibleProjectResourceSet extends ProjectResourceSet {
	
	
	/**
	   * Returns the resource resolved by the URI.
	   * <p>
	   * A resource set is expected to implement the following strategy 
	   * in order to resolve the given URI to a resource.
	   * First it uses it's {@link #getURIConverter URI converter} to {@link URIConverter#normalize normalize} the URI 
	   * and then to compare it with the normalized URI of each resource;
	   * if it finds a match, 
	   * that resource becomes the result.
	   * Failing that,
	   * it {@link org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#delegatedGetResource delegates} 
	   * to allow the URI to be resolved elsewhere.
	   * For example, 
	   * the {@link org.eclipse.emf.ecore.EPackage.Registry#INSTANCE package registry}
	   * is used to {@link org.eclipse.emf.ecore.EPackage.Registry#getEPackage resolve} 
	   * the {@link org.eclipse.emf.ecore.EPackage namespace URI} of a package
	   * to the static instance of that package.
	   * So the important point is that an arbitrary implementation may resolve the URI to any resource,
	   * not necessarily to one contained by this particular resource set.
	   * If the delegation step fails to provide a result,
	   * and if <code>loadOnDemand</code> is <code>true</code>,
	   * a resource is {@link org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#demandCreateResource created} 
	   * and that resource becomes the result.
	   * If <code>loadOnDemand</code> is <code>true</code>
	   * and the result resource is not {@link Resource#isLoaded loaded}, 
	   * it will be {@link org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#demandLoad loaded} before it is returned.
	   * </p>
	   * @param uri the URI to resolve.
	   * @param loadOnDemand whether to create and load the resource, if it doesn't already exists.
	   * @param registeredFactory that is used to create resource if needed 
	   * @return the resource resolved by the URI, or <code>null</code> if there isn't one and it's not being demand loaded.
	   * @throws RuntimeException if a resource can't be demand created.
	   * @throws org.eclipse.emf.common.util.WrappedException if a problem occurs during demand load.
	   * @since 2.1
	   */
	
	Resource getResource(URI uri, boolean loadOnDemand, Factory registeredFactory);
	
	/**
	   * Creates a new resource, of the appropriate type, and returns it.
	   * <p>
	   * It delegates to the resource factory {@link #getResourceFactoryRegistry registry} 
	   * to determine the {@link Resource.Factory.Registry#getFactory correct} factory,
	   * and then it uses that factory to {@link Resource.Factory#createResource create} the resource
	   * and add it to the {@link #getResources contents}.
	   * If there is no registered factory, <code>null</code> will be returned;
	   * when running within Eclipse,
	   * a default XMI factory will be registered,
	   * and this will never return <code>null</code>.
	   * </p>
	   * @param uri the URI of the resource to create.
	   * @param resourceFactory 
	   * @return a new resource, or <code>null</code> if no factory is registered.
	   */
	
	public Resource createResource(URI uri, Resource.Factory resourceFactory);

}
