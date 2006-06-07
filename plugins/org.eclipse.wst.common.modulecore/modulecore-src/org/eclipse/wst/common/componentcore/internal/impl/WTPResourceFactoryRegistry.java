/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.impl;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.wst.common.componentcore.internal.ModulecorePlugin;
import org.eclipse.wst.common.internal.emf.resource.FileNameResourceFactoryRegistry;
import org.eclipse.wst.common.internal.emf.resource.ResourceFactoryDescriptor;
import org.eclipse.wst.common.internal.emf.utilities.DefaultOverridableResourceFactoryRegistry;

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

	public synchronized Resource.Factory getFactory(URI uri) {
		
		Resource.Factory resourceFactory = null;
		if(uri != null && uri.lastSegment() != null) {
			ResourceFactoryDescriptor descriptor = getDescriptor(uri);
			
			if(descriptor != null) {
				resourceFactory = getFactory(descriptor);	
			}	
		}
		if(resourceFactory == null)
			resourceFactory = super.getFactory(uri);
		return resourceFactory; 
	}	


	/**
	 * Register a file name representing the last segment of a URI with the corresponding
	 * Resource.Factory.
	 */
	public synchronized void registerLastFileSegment(String aSimpleFileName, Resource.Factory aFactory) { 
		
		if(LOG_WARNINGS) {
			/* the third entry in the array is this stack frame, we walk back from there. */
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
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
	
	/**
	 * Declares a subclass to create Resource.Factory(ies) from an extension. 
	 */
	private class ConfigurationResourceFactoryDescriptor extends ResourceFactoryDescriptor  implements IResourceFactoryExtPtConstants {
		
		private String shortSegment;
		private final IConfigurationElement element; 
		
		public ConfigurationResourceFactoryDescriptor(IConfigurationElement ext) throws CoreException {
			Assert.isNotNull(ext);
			element = ext;
			init();
		} 
		
		private void init() throws CoreException {
			shortSegment = element.getAttribute(ATT_SHORT_SEGMENT);
			if(shortSegment == null || shortSegment.trim().length() == 0)
				throw new CoreException(
							ModulecorePlugin.createErrorStatus(0, 
										"The shortSegment attribute of " + TAG_RESOURCE_FACTORY + //$NON-NLS-1$ 
										" must specify a valid, non-null, non-empty value in " +   //$NON-NLS-1$
										element.getNamespaceIdentifier(), null));
			
		} 

		public boolean isEnabledFor(URI fileURI) {
			/* shortSegment must be non-null for the descriptor to be created, 
			 * a validation check in init() verifies this requirement */
			if(fileURI != null && fileURI.lastSegment() != null)
				return shortSegment.equals(fileURI.lastSegment());
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
}
