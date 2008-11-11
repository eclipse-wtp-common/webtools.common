/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.internal.emf.resource.FileNameResourceFactoryRegistry;
import org.eclipse.wst.common.internal.emf.resource.ResourceFactoryDescriptor;
import org.eclipse.wst.common.internal.emf.utilities.DefaultOverridableResourceFactoryRegistry;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;
import org.eclipse.wst.common.internal.emfworkbench.edit.EMFWorkbenchEditContextFactory;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class WTPResourceFactoryRegistry extends FileNameResourceFactoryRegistry {

	public static final WTPResourceFactoryRegistry INSTANCE = new WTPResourceFactoryRegistry();
	 
	private final static boolean LOG_WARNINGS = false;
	
	
	private WTPResourceFactoryRegistry() {
		new ResourceFactoryRegistryReader().readRegistry();
	}
	
	public Resource.Factory delegatedGetFactory(URI uri) {
		if (WTPResourceFactoryRegistry.INSTANCE == this)
			return super.delegatedGetFactory(uri);
		return WTPResourceFactoryRegistry.INSTANCE.getFactory(uri);	
	}   

	public Resource.Factory getFactory(URI uri, IContentDescription description) {
		IProject componentProject = null;
		try {
			componentProject = StructureEdit.getContainingProject(uri);
		} catch (UnresolveableURIException e) {
			// don't do anything
		}
		ILock lock = EMFWorkbenchEditContextFactory.getProjectLockObject(componentProject);
		try{
			if(null != lock){
				lock.acquire();
			}
			synchronized(this){
				Resource.Factory resourceFactory = null;
				if(uri != null && uri.lastSegment() != null) {
					ResourceFactoryDescriptor descriptor = null;
					if(null == description){
						descriptor = getDescriptor(uri);
					} else {
						descriptor = getDescriptor(uri, description);
					}
					
					if(descriptor != null) {
						resourceFactory = getFactory(descriptor);	
					}	
				}
				if(resourceFactory == null)
					resourceFactory = super.getFactory(uri);
				return resourceFactory;
			}
		} finally{
			if(null != lock){
				lock.release();
			}
		} 
	}
	
	public Resource.Factory getFactory(URI uri) {
		return getFactory(uri, (IContentDescription)null);
	}	


	/**
	 * Register a file name representing the last segment of a URI with the corresponding
	 * Resource.Factory.
	 */
	public synchronized void registerLastFileSegment(String aSimpleFileName, Resource.Factory aFactory) { 
		
		if(LOG_WARNINGS) {
			/* the third entry in the array is this stack frame, we walk back from there. */
			StackTraceElement[] stackTrace = (new Exception()).getStackTrace();
			if(stackTrace.length > 4) {
				StringBuffer warningMessage = new StringBuffer("WTPResourceFactoryRegistry.registerLastFileSegment() was called explicitly from " + stackTrace[3]);
				warningMessage.append("\nThis happened around: \n");
				for (int i = 4; (i < stackTrace.length) && i < 8; i++) {
					warningMessage.append("\tnear ").append(stackTrace[i]).append('\n');
				}
				warningMessage.append(".\nClients should use the org.eclipse.wst.common.modulecore.resourceFactories extension point instead.");
				ModulecorePlugin.log(IStatus.INFO, 0, warningMessage.toString(), null);		
			}
		}
		
		super.registerLastFileSegment(aSimpleFileName, aFactory);
		
	}  
	private WTPResourceFactoryRegistryKey getKey(ResourceFactoryDescriptor descriptor) {
		WTPResourceFactoryRegistryKey key = new WTPResourceFactoryRegistryKey();
		key.shortName = descriptor.getShortSegment();
		key.type = descriptor.getContentType();
		key.isDefault = descriptor.isDefault();
		if(descriptor instanceof ConfigurationResourceFactoryDescriptor){
			ConfigurationResourceFactoryDescriptor configurationDescriptor = (ConfigurationResourceFactoryDescriptor)descriptor;
			key.factoryClassName = configurationDescriptor.getFactoryClassName();
			key.overridesFactoryClassName = configurationDescriptor.getOverridesFactoryClassName();
		}
		return key;
	}
	
	/**
	 * Declares a subclass to create Resource.Factory(ies) from an extension. 
	 */
	private class ConfigurationResourceFactoryDescriptor extends ResourceFactoryDescriptor  implements IResourceFactoryExtPtConstants {
		
		private String shortSegment;
		private IContentType contentType;
		private boolean isDefault = true;
		private String factoryClassName = null;
		private String overridesFactoryClassName = null;
		private final IConfigurationElement element; 
		
		public ConfigurationResourceFactoryDescriptor(IConfigurationElement ext) throws CoreException {
			Assert.isNotNull(ext);
			element = ext;
			init();
		} 
		
		public String getOverridesFactoryClassName() {
			return overridesFactoryClassName;
		}

		public String getFactoryClassName() {
			return factoryClassName;
		}

		private void init() throws CoreException {
			shortSegment = element.getAttribute(ATT_SHORT_SEGMENT);
			
			IConfigurationElement[] bindings = element.getChildren(TAG_CONTENTTYPE);
			if (bindings.length > 0) {
				String contentTypeId = null;
				contentTypeId = bindings[0].getAttribute(ATT_CONTENTTYPEID);			
				if (contentTypeId != null)
					contentType = Platform.getContentTypeManager().getContentType(contentTypeId);
			}
			
			if ((shortSegment == null || shortSegment.trim().length() == 0)
						&& contentType == null) {
				throw new CoreException(
							ModulecorePlugin.createErrorStatus(0, 
										"Either the shortSegment attribute or the contentType element of " //$NON-NLS-1$
										+ TAG_RESOURCE_FACTORY 
										+ " must be specified in " 
										+ element.getNamespaceIdentifier()
										+ ".  The shortSegment attribute is specified with a valid, non-null, " //$NON-NLS-1$
										+ "non-empty value, and the contentType element is specified with a " //$NON-NLS-1$
										+ "valid, non-null, non-empty contentTypeId." //$NON-NLS-1$
										, null));
			}
			
			if ("false".equals(element.getAttribute(ATT_ISDEFAULT)))
				isDefault = false;
				
            factoryClassName = element.getAttribute(ATT_CLASS);
			overridesFactoryClassName = element.getAttribute(ATT_OVERRIDES_FACTORY);				
		} 

		public boolean isEnabledFor(URI fileURI) {
			// Not sure where this is actually used, so not sure what the proper 
			// implementation should be, so simply checking the short segment for now
			if (fileURI != null && fileURI.lastSegment() != null && shortSegment != null) {
				return shortSegment.equals(fileURI.lastSegment());
			}
			return false;
		} 
		
		public Resource.Factory createFactory() {
			
			final Resource.Factory[] factory = new Resource.Factory[1];
			
			SafeRunner.run(new ISafeRunnable() {
				
				public void run() throws Exception {
					factory[0] = (Resource.Factory) element.createExecutableExtension(ATT_CLASS);					
				}
				
				public void handleException(Throwable exception) {
					ModulecorePlugin.log(ModulecorePlugin.createErrorStatus(0, exception.getMessage(), exception));					
				}
			});
			
			return factory[0] != null ? factory[0] : DefaultOverridableResourceFactoryRegistry.GLOBAL_FACTORY;
			
		}

		public String getShortSegment() {
			return shortSegment;
		}

		public IContentType getContentType() {
			return contentType;
		}

		public boolean isDefault() {
			return isDefault;
		}
		
		public int hashCode() {
			int hashCode = 0;
			if (getContentType() != null) {
				hashCode |= getContentType().hashCode();
			}
			if (getShortSegment() != null) {
				hashCode |= getShortSegment().hashCode();
			}
			return hashCode;
		}
		
		public boolean equals(Object o) {
			if (! (o instanceof ResourceFactoryDescriptor)) {
				return false;
			}
			ResourceFactoryDescriptor rfdo = (ResourceFactoryDescriptor) o;
			boolean equals = true;
			equals &= (getContentType() == null) ? rfdo.getContentType() == null :
				getContentType().equals(rfdo.getContentType());
			equals &= (getShortSegment() == null) ? rfdo.getShortSegment() == null :
				getShortSegment().equals(rfdo.getShortSegment());
			return equals;
		}
	}
	 
	
	private class ResourceFactoryRegistryReader extends RegistryReader implements IResourceFactoryExtPtConstants { 
 		
		public ResourceFactoryRegistryReader() {
			super(Platform.getPluginRegistry(), ModulecorePlugin.PLUGIN_ID, EXTPT_RESOURCE_FACTORIES);
		}

		public boolean readElement(final IConfigurationElement element) {
			
			if(element != null && TAG_RESOURCE_FACTORY.equals(element.getName())) {
				final boolean[] success = new boolean[] { true }; 
				SafeRunner.run(new ISafeRunnable() {
					
					public void run() throws Exception {
						addDescriptor(new ConfigurationResourceFactoryDescriptor(element));
					} 

					public void handleException(Throwable exception) {
						ModulecorePlugin.log(ModulecorePlugin.createErrorStatus(0, exception.getMessage(), exception));
						success[0] = false;
					}
				});				
				return success[0];
			} else {
				return false;
			}	
		}
	}
	private class WTPResourceFactoryRegistryKey { 
 		
		public String overridesFactoryClassName;
		public String factoryClassName;
		public String shortName;
		public IContentType type;
		public boolean isDefault = true;
		public WTPResourceFactoryRegistryKey() {
			super();
		}
		
		/**
		 * Sort in the following manner:
		 * First, sort by shortName, if shortName is null, then it comes last
		 * If shortNames are equal, then sort by isDefault
		 * If isDefault is also equal, then the one defining a factoryClassName wins
		 * If both have factoryClassNames, then check to see if one overrides the other via overridesFactoryClassName
		 * If neither override the other factory class, then sort by factoryClassname
		 * @param other
		 * @return
		 */
		public int compareTo(WTPResourceFactoryRegistryKey other){
			if(this == other){
				return 0;
			}
			if(shortName == null && other.shortName == null){
				return 0;
			} else if(shortName == null){
				return 1;
			} else if(other.shortName == null){
				return -1;
			}
			
			int shortNameCompare = this.shortName.compareTo(other.shortName);
			if(shortNameCompare != 0){
				return shortNameCompare;
			} else {
				if(this.isDefault != other.isDefault){
					if(this.isDefault){
						return -1;
					} else {
						return 1;
					}
				} else {
					if(this.factoryClassName == null && other.factoryClassName == null){
						return 0;
					} else if(other.factoryClassName == null){
						return -1;
					} else if (this.factoryClassName == null){
						return 1;
					} else if(other.factoryClassName.equals(this.overridesFactoryClassName)){
						return -1;
					} else if(this.factoryClassName.equals(other.overridesFactoryClassName)){
						return 1;
					} else {
						return this.factoryClassName.compareTo(other.factoryClassName);
					}	
				}
			}
		}
	}

	protected void addDescriptor(ResourceFactoryDescriptor descriptor) {
		getDescriptors().put(getKey(descriptor), descriptor);
	}
	
	private WTPResourceFactoryRegistryKey []  sortedDescriptors = null;
	
	private WTPResourceFactoryRegistryKey []  getSortedDescriptorKeys() {
		if(sortedDescriptors == null || sortedDescriptors.length != getDescriptors().size()){
			Set keys = getDescriptors().keySet();
			WTPResourceFactoryRegistryKey [] array = new WTPResourceFactoryRegistryKey [keys.size()];
			int count = 0;
			for (Iterator iterator = keys.iterator(); iterator.hasNext();count++) {
				WTPResourceFactoryRegistryKey key = (WTPResourceFactoryRegistryKey) iterator.next();
				array[count] = key;
			}
			Arrays.sort(array, new Comparator<WTPResourceFactoryRegistryKey>() {
				public int compare(WTPResourceFactoryRegistryKey key1,
						WTPResourceFactoryRegistryKey key2) {
					return key1.compareTo(key2);
				}
			});
			sortedDescriptors = array;
		}
		return sortedDescriptors;
	}

	protected synchronized ResourceFactoryDescriptor getDescriptor(URI uri, IContentDescription description) {
		WTPResourceFactoryRegistryKey [] keys = getSortedDescriptorKeys();
		ResourceFactoryDescriptor defaultDescriptor = null;
		
		// first check content type
		if (description != null) {
			for (WTPResourceFactoryRegistryKey key : keys) {
				ResourceFactoryDescriptor descriptor = (ResourceFactoryDescriptor) getDescriptors().get(key);
				
				if ((key.type != null) && (description.getContentType().equals(key.type))) {
					if ((defaultDescriptor == null) || (key.isDefault)) {
						defaultDescriptor = descriptor;
					}
				}
			}
		}
		
		// then check short name, overriding default if necessary
		for (WTPResourceFactoryRegistryKey key : keys) {
			ResourceFactoryDescriptor descriptor = (ResourceFactoryDescriptor) getDescriptors().get(key);
						
			if ((key.shortName != null) && (uri.lastSegment().equals(key.shortName))) {
				if ((defaultDescriptor == null) 
						|| ((description == null) && (key.isDefault))) {
					defaultDescriptor = descriptor;
				}
			}
		}
		
		return defaultDescriptor;
	}
	
	private URI newPlatformURI(URI aNewURI, IProject project) {
		
		if (project == null)
			return ModuleURIUtil.trimToDeployPathSegment(aNewURI);
		try {
			IVirtualComponent component = ComponentCore.createComponent(project);

			URI deployPathSegment = ModuleURIUtil.trimToDeployPathSegment(aNewURI);
			
			//IVirtualFile newFile = component.getFile(new Path(deployPathSegment.path()));			
			IVirtualFolder rootFolder = component.getRootFolder();
			IVirtualFile newFile = rootFolder.getFile(new Path(deployPathSegment.path()));
			
			return URI.createPlatformResourceURI(newFile.getWorkspaceRelativePath().toString());
			 
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private IContentDescription getDescriptionFromURI(URI uri) {
		String contentTypeIdentifier = ModuleURIUtil.getContentTypeName(uri);
		if (contentTypeIdentifier != null)
			return Platform.getContentTypeManager().getContentType(contentTypeIdentifier).getDefaultDescription();
		else
			return null;
		
	}

	protected synchronized ResourceFactoryDescriptor getDescriptor(URI uri) {
		IFile file = WorkbenchResourceHelper.getPlatformFile(uri);
		IContentDescription description = null;
		if (file != null && file.exists()) {
			try {
				description = file.getContentDescription();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (description == null) {//Check for optional embedded uri segment, then normalize
			description = getDescriptionFromURI(uri);
			try {
				if (description != null) {
					IProject componentProject = null;
					try {
						componentProject = StructureEdit.getContainingProject(uri);
					} catch (UnresolveableURIException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					uri = PlatformURLModuleConnection.resolve(uri);
					uri = newPlatformURI(uri,componentProject);
				} 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ResourceFactoryDescriptor defaultDesc = getDescriptor(uri, description);
		// Ok no content type match - go to super
		if (defaultDesc != null){
			return defaultDesc;
		}
		else{
			return super.getDescriptor(uri);
		}
	}
}
