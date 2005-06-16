/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: EMFWorkbenchContextFactory.java,v $$
 *  $$Revision: 1.4 $$  $$Date: 2005/06/16 20:14:27 $$ 
 */
package org.eclipse.jem.internal.util.emf.workbench;

import java.util.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.jem.internal.util.emf.workbench.nls.EMFWorkbenchResourceHandler;
import org.eclipse.jem.util.RegistryReader;
import org.eclipse.jem.util.emf.workbench.*;
import org.eclipse.jem.util.emf.workbench.nature.EMFNature;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jem.util.plugin.JEMUtilPlugin;



public class EMFWorkbenchContextFactory  {
	public static final EMFWorkbenchContextFactory INSTANCE;
	
	static {
		INSTANCE = createFactoryInstance();
	}
	private final Class CONTRIBUTOR_CLASS = IEMFContextContributor.class;
	protected Map emfContextCache = new WeakHashMap();

	
	private static EMFWorkbenchContextFactory createFactoryInstance() {
		EMFWorkbenchContextFactory factory = createFactoryInstanceFromExtension();
		if (factory == null)
			factory = new EMFWorkbenchContextFactory();
		return factory;
	}
	
	private static EMFWorkbenchContextFactory createFactoryInstanceFromExtension() {
		final EMFWorkbenchContextFactory[] factoryHolder = new EMFWorkbenchContextFactory[1];
		RegistryReader reader = new RegistryReader(JEMUtilPlugin.ID, "internalWorkbenchContextFactory") { //$NON-NLS-1$
			public boolean readElement(IConfigurationElement element) {
				if (element.getName().equals("factoryClass")) //$NON-NLS-1$
					try {
						factoryHolder[0] = (EMFWorkbenchContextFactory)element.createExecutableExtension("name"); //$NON-NLS-1$
						return true;
					} catch (CoreException e) {
						Logger.getLogger().logError(e);
					}				
				return false;
			}
		};
		reader.readRegistry();
		return factoryHolder[0];
	}

	/**
	 * Constructor for EMFNatureFactory.
	 */
	protected EMFWorkbenchContextFactory() {
		super();

	}


	protected void cacheEMFContext(IProject aProject, EMFWorkbenchContextBase emfContext) {
		if (aProject != null && emfContext != null)
			emfContextCache.put(aProject, emfContext);
	}

	protected EMFWorkbenchContextBase getCachedEMFContext(IProject aProject) {
		if (aProject != null)
			return (EMFWorkbenchContextBase) emfContextCache.get(aProject);
		return null;
	}

	/**
	 * <code>aProject</code> is either being closed or deleted so we need to cleanup our cache.
	 */
	public void removeCachedProject(IProject aProject) {
		if (aProject != null) 
			emfContextCache.remove(aProject); 
		
	}
	/**
	 * Return a new or existing EMFNature on <code>aProject</code>. Allow the <code>contributor</code>
	 * to contribute to the new or existing nature prior to returning.
	 */
	public EMFWorkbenchContextBase createEMFContext(IProject aProject, IEMFContextContributor contributor) {
		if (aProject == null)
			throw new IllegalStateException("[EMFWorkbenchContextBase]" + EMFWorkbenchResourceHandler.getString("EMFWorkbenchContextFactory_UI_0")); //$NON-NLS-1$ //$NON-NLS-2$
		if (!aProject.isAccessible())
			throw new IllegalStateException("[EMFWorkbenchContextBase]" + EMFWorkbenchResourceHandler.getString("EMFWorkbenchContextFactory_UI_1", new Object[]{aProject.getName()})); //$NON-NLS-1$ //$NON-NLS-2$
		EMFWorkbenchContextBase context = getCachedEMFContext(aProject);
		boolean contributorFound = false;
		if (context == null) {
			context = primCreateEMFContext(aProject);
			cacheEMFContext(aProject, context);
			contributorFound = initializeEMFContextFromContributors(aProject, context, contributor);
		}
		if (contributor != null && context != null && !contributorFound)
			contributor.primaryContributeToContext(context);
		return context;
	}
	
	protected boolean initializeEMFContextFromContributors(IProject aProject, EMFWorkbenchContextBase emfContext, IEMFContextContributor contributor) {
		boolean contributorFound = false;
		if (aProject == null || emfContext == null)
			return contributorFound;
		List runtimes = EMFNature.getRegisteredRuntimes(aProject);
		for (int i = 0; i < runtimes.size(); i++) {
			IProjectNature nature = (IProjectNature) runtimes.get(i);
			if (nature != null && CONTRIBUTOR_CLASS.isInstance(nature)) {
				if (nature == contributor)
					contributorFound = true;
				((IEMFContextContributor) nature).primaryContributeToContext(emfContext);
			}
		}
		return contributorFound;
	}

	protected boolean isNatureEnabled(IProject aProject, String natureId) {
		try {
			return aProject.isNatureEnabled(natureId);
		} catch (CoreException e) {
			return false;
		}
	}

	protected String[] getNatureIds(IProject aProject) {
		try {
			if (aProject.isAccessible())
				return aProject.getDescription().getNatureIds();
		} catch (CoreException e) {
		}
		return null;
	}

	protected IProjectNature getNature(IProject aProject, String natureId) {
		try {
			return aProject.getNature(natureId);
		} catch (CoreException e) {
			return null;
		}
	}

	protected EMFWorkbenchContextBase primCreateEMFContext(IProject aProject) {
		return new EMFWorkbenchContextBase(aProject);
	}
	/**
	 * Return an existing EMFNature on <code>aProject</code>.
	 */
	public EMFWorkbenchContextBase getEMFContext(IProject aProject) {
		return getCachedEMFContext(aProject);
	}

	public ResourceSetWorkbenchSynchronizer createSynchronizer(ResourceSet aResourceSet, IProject aProject) {
		return new ResourceSetWorkbenchSynchronizer(aResourceSet, aProject);
	}

}
