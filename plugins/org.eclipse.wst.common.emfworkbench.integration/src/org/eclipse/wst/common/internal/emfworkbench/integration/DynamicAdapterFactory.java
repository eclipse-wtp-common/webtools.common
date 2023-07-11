/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Dec 1, 2003
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.internal.emfworkbench.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.wst.common.frameworks.internal.enablement.EnablementIdentifier;
import org.eclipse.wst.common.frameworks.internal.enablement.EnablementIdentifierEvent;
import org.eclipse.wst.common.frameworks.internal.enablement.EnablementManager;
import org.eclipse.wst.common.frameworks.internal.enablement.IEnablementIdentifier;
import org.eclipse.wst.common.frameworks.internal.enablement.IEnablementIdentifierListener;
import org.eclipse.wst.common.frameworks.internal.enablement.IEnablementManager;
import org.eclipse.wst.common.internal.emfworkbench.EMFWorkbenchEditResourceHandler;
import org.eclipse.wst.common.internal.emfworkbench.edit.AdapterFactoryDescriptor;
import org.eclipse.wst.common.internal.emfworkbench.edit.AdapterFactoryRegistry;
import org.eclipse.wst.common.internal.emfworkbench.edit.ExtendedComposedAdapterFactory;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DynamicAdapterFactory implements AdapterFactory, IChangeNotifier, IDisposable, INotifyChangedListener, IEnablementIdentifierListener {

	private static final AdapterFactory NULL_FACTORY = new AdapterFactoryImpl();

	private ResourceItemProviderAdapterFactory resourceItemProviderAdapterFactory = null;

	private String viewID;

	//Each of the two maps below are keyed by the project
	private Map adapterFactoriesByPackage;

	private Map adapterFactoriesByType;

	private Set allFactories;

	/**
	 * This is used to implement {@link IChangeNotifier}
	 */
	protected ChangeNotifier changeNotifier = new ChangeNotifier();

	private boolean respectingActivities = false;

	protected static final Object NO_PROJECT = "NO_PROJECT"; //$NON-NLS-1$

	/**
	 */
	public DynamicAdapterFactory(String viewID, boolean respectActivies) {
		this.viewID = viewID;
		this.respectingActivities = respectActivies;
		initialize();
	}

	/**
	 */
	public DynamicAdapterFactory(String viewID) {
		this.viewID = viewID;
		initialize();
	}

	private void initialize() {
		adapterFactoriesByPackage = new HashMap();
		adapterFactoriesByType = new HashMap();
		allFactories = new HashSet();
	}

	@Override
	public boolean isFactoryForType(Object type) {
		return getFactoryForType(type) != null;
	}

	private AdapterFactory getFactoryForType(Object type) {

		if (type == null)
			return null;

		if (type instanceof EObject)
			return getFactoryForType((EObject) type);

		Object aProject = ProjectUtilities.getProject(type);
		if (aProject == null)
			aProject = NO_PROJECT;
		AdapterFactory factory = getExistingFactoryForType(aProject, type);
		if (factory == NULL_FACTORY)
			return null;
		else if (factory == null) {
			factory = findFactoryForType(aProject, type);

			if (factory == null)
				addAdapterFactory(aProject, type, NULL_FACTORY);
			else
				addAdapterFactory(aProject, type, factory);
		}
		return factory;
	}

	private AdapterFactory getExistingFactoryForType(Object p, Object type) {
		if (p == null)
			return null;
		Map aMap = (Map) adapterFactoriesByType.get(p);
		if (aMap == null)
			return null;
		return (AdapterFactory) adapterFactoriesByType.get(type);
	}

	/*
	 * The factory was not cached by type, so we will search the cache of factories for the project
	 * and see if it can be found there
	 */
	private AdapterFactory findFactoryForType(Object project, Object type) {
		if (project == null)
			return null;
		Map aMap = (Map) adapterFactoriesByPackage.get(project);
		if (aMap == null)
			return null;
		Iterator iter = aMap.values().iterator();
		AdapterFactory factory = null;
		while (iter.hasNext()) {
			factory = (AdapterFactory) iter.next();
			if (factory.isFactoryForType(type))
				return factory;
		}

		// adapt the resource to its contents
		if (getResourceItemProviderAdapterFactory().isFactoryForType(type))
			return getResourceItemProviderAdapterFactory();
		return null;
	}

	private AdapterFactory getFactoryForType(EObject obj) {
		EClass eClass = obj.eClass();
		if (eClass == null)
			return null;

		EPackage ePackage = eClass.getEPackage();
		Object aProject = ProjectUtilities.getProject(obj);
		if (aProject == null)
			aProject = NO_PROJECT;
		AdapterFactory result = getFactoryForPackage(aProject, ePackage);

		if (result == null) {
			Set failedPackageSet = new HashSet();
			failedPackageSet.add(ePackage);
			Iterator supertypes = eClass.getEAllSuperTypes().iterator();
			while (supertypes.hasNext()) {
				eClass = (EClass) supertypes.next();
				if (eClass != null) {
					ePackage = eClass.getEPackage();
					if (failedPackageSet.add(ePackage)) {
						result = getFactoryForPackage(aProject, ePackage);
						if (result != null)
							break;
					}
				}
			}
		}
		return result;
	}

	private AdapterFactory getFactoryForPackage(Object aProject, EPackage aPackage) {
		if (aProject == null || aPackage == null)
			return null;
		AdapterFactory factory = getExistingFactoryForPackage(aProject, aPackage);
		if (factory == NULL_FACTORY)
			return null;
		else if (factory == null) {
			try {
				factory = createAdapterFactory(aProject, aPackage);

				if (factory == null)
					addAdapterFactory(aProject, aPackage, NULL_FACTORY);
				else
					addAdapterFactory(aProject, aPackage, factory);
			} catch (RuntimeException re) {
				EMFWorkbenchEditPlugin.logError(re);
			}
		}
		return factory;
	}

	private AdapterFactory getFactoryForTypes(Object p, List types) {
		Map aMap = (Map) adapterFactoriesByPackage.get(p);
		if (aMap == null)
			return adaptResourceTypes(types);

		Iterator factories = aMap.values().iterator();
		AdapterFactory factory = null;
		while (factories.hasNext()) {
			factory = (AdapterFactory) factories.next();
			if (isFactoryForAllTypes(factory, types)) {
				return factory;
			}
		}


		return null;
	}

	private AdapterFactory adaptResourceTypes(List types) {
		//      adapt the resource to its contents
		if (isFactoryForAllTypes(getResourceItemProviderAdapterFactory(), types))
			return getResourceItemProviderAdapterFactory();
		return null;
	}

	private void removeFactoryForTypes(Object p, List types) {
		Map aMap = (Map) adapterFactoriesByPackage.get(p);
		if (aMap == null)
			return;
		Iterator factories = aMap.values().iterator();
		AdapterFactory factory = null;
		while (factories.hasNext()) {
			factory = (AdapterFactory) factories.next();
			if (isFactoryForAllTypes(factory, types)) {
				aMap.remove(factory);
			}
		}

	}

	private boolean isFactoryForAllTypes(AdapterFactory factory, List types) {
		for (int i = 0; i < types.size(); i++) {
			if (!factory.isFactoryForType(types.get(i))) {
				return false;
			}
		}
		return true;

	}

	private AdapterFactory getExistingFactoryForPackage(Object p, EPackage aPackage) {
		if (p == null)
			return null;
		Map aMap = (Map) adapterFactoriesByPackage.get(p);
		if (aMap == null)
			return null;
		return (AdapterFactory) aMap.get(aPackage);
	}

	private void addAdapterFactory(Object p, EPackage aPackage, AdapterFactory adapterFactory) {
		Map aMap = getOrCreateMap(p, adapterFactoriesByPackage);

		aMap.put(aPackage, adapterFactory);

		if (adapterFactory == NULL_FACTORY)
			return;

		if (adapterFactory instanceof IChangeNotifier) {
			((IChangeNotifier) adapterFactory).addListener(this);
		}
		allFactories.add(adapterFactory);
	}

	private Map getOrCreateMap(Object p, Map container) {
		Map aMap = (Map) container.get(p);
		if (aMap == null) {
			aMap = new HashMap(10);
			container.put(p, aMap);
		}
		return aMap;
	}

	private void addAdapterFactory(Object p, Object type, AdapterFactory adapterFactory) {
		Map aMap = getOrCreateMap(p, adapterFactoriesByType);
		aMap.put(type, adapterFactory);

		if (adapterFactory == NULL_FACTORY)
			return;

		if (adapterFactory instanceof IChangeNotifier) {
			((IChangeNotifier) adapterFactory).addListener(this);
		}
		allFactories.add(adapterFactory);
	}

	@Override
	public Object adapt(Object target, Object type) {
		Object adapter = target;
		if (target instanceof Notifier) {
			adapter = adapt((Notifier) target, type);
		}

		if (!(type instanceof Class) || (((Class) type).isInstance(adapter))) {
			return adapter;
		}

		return null;
	}

	@Override
	public Adapter adapt(Notifier target, Object type) {
		Adapter result = null;

		if (target instanceof EObject)
			result = adapt((EObject) target, type);
		else {
			Object p = ProjectUtilities.getProject(target);
			if (p == null)
				p = NO_PROJECT;
			result = adapt(p, target, type, new HashSet(), target.getClass());

		}

		return result;
	}

	public Adapter adapt(EObject target, Object type) {

		EClass eClass = target.eClass();
		if (eClass == null)
			return null;

		EPackage ePackage = eClass.getEPackage();
		Adapter result = adapt(target, ePackage, type);

		if (result == null) {
			Set failedPackageSet = new HashSet();
			failedPackageSet.add(ePackage);
			Iterator supertypes = eClass.getEAllSuperTypes().iterator();
			while (supertypes.hasNext()) {
				eClass = (EClass) supertypes.next();
				if (eClass != null) {
					ePackage = eClass.getEPackage();
					if (failedPackageSet.add(ePackage)) {
						result = adapt(target, ePackage, type);
						if (result != null)
							break;
					}
				}
			}
		}
		return result;
	}

	private Adapter adapt(EObject target, EPackage ePackage, Object type) {
		Object aProject = ProjectUtilities.getProject(target);
		if (aProject == null)
			aProject = NO_PROJECT;
		AdapterFactory delegate = getFactoryForPackage(aProject, ePackage);
		if (delegate != null && delegate.isFactoryForType(type)) {
			return delegate.adapt(target, type);
		}

		return null;
	}

	/*
	 * Code borrowed from {@link ComposedAdapterFactory}
	 *  
	 */
	private Adapter adapt(Object p, Notifier target, Object type, Collection failedPackages, Class javaClass) {
		if (p == null)
			return null;

		Adapter result = null;

		Package javaPackage = javaClass.getPackage();
		if (failedPackages.add(javaPackage)) {
			List types = new ArrayList(2);
			types.add(javaPackage);
			if (type != null) {
				types.add(type);
			}

			/* when an error occurs, remove the delegate and try again */
			boolean attemptAdaptAgain = true;
			while (result == null && attemptAdaptAgain) {
				attemptAdaptAgain = false;

				AdapterFactory delegateAdapterFactory = getFactoryForTypes(p, types);
				if (delegateAdapterFactory != null) {
					try {
						result = delegateAdapterFactory.adapt(target, type);
					} catch (RuntimeException re) {
						EMFWorkbenchEditPlugin.logError(re);
						removeFactoryForTypes(p, types);
						attemptAdaptAgain = true;
					}
				}
			}
		}

		if (result == null) {
			Class superclass = javaClass.getSuperclass();
			if (superclass != null) {
				result = adapt(p, target, type, failedPackages, javaClass.getSuperclass());
			}
			if (result == null) {
				Class[] interfaces = javaClass.getInterfaces();
				for (int i = 0; i < interfaces.length; ++i) {
					result = adapt(p, target, type, failedPackages, interfaces[i]);
					if (result != null) {
						break;
					}
				}
			}
		}

		return result;
	}

	@Override
	public Adapter adaptNew(Notifier target, Object type) {

		AdapterFactory factory = getFactoryForType(target);

		if (factory != null)
			return factory.adaptNew(target, type);
		return null;
	}

	@Override
	public void adaptAllNew(Notifier target) {

		AdapterFactory factory = getFactoryForType(target);

		if (factory != null)
			factory.adaptAllNew(target);

	}

	@Override
	public void addListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.add(notifyChangedListener);
	}

	@Override
	public void removeListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.remove(notifyChangedListener);
	}

	@Override
	public void fireNotifyChanged(Notification notification) {
		if (changeNotifier == null || changeNotifier.isEmpty() || changeNotifier.get(0) == null)
			return;
		changeNotifier.fireNotifyChanged(notification);
	}

	@Override
	public void dispose() {
		Iterator iter = allFactories.iterator();
		Object factory = null;
		while (iter.hasNext()) {
			factory = iter.next();
			disposeFactory(factory);
		}
		for (Iterator itr = getEnablementIdentifiers().iterator(); itr.hasNext();) {
			((IEnablementIdentifier) itr.next()).removeIdentifierListener(this);
		}
		if (resourceItemProviderAdapterFactory != null)
			resourceItemProviderAdapterFactory.removeListener(this);
	}

	private void disposeFactory(Object factory) {
		if (factory instanceof IDisposable) {
			((IDisposable) factory).dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	@Override
	public void notifyChanged(Notification notification) {
		//Foward the notification on to all my listeners
		fireNotifyChanged(notification);
	}

	/**
	 * @param project
	 */
	private void removeAdapterFactories(Object project) {
		adapterFactoriesByType.remove(project);
		Map aMap = (Map) adapterFactoriesByPackage.remove(project);
		if (aMap == null)
			return;

		Iterator factories = aMap.values().iterator();
		Object adapterFactory;
		while (factories.hasNext()) {
			adapterFactory = factories.next();
			allFactories.remove(adapterFactory);
			disposeFactory(adapterFactory);
		}
	}

	private AdapterFactory createAdapterFactory(Object project, EPackage ePackage) {
		if (ePackage == null)
			throw new IllegalArgumentException(EMFWorkbenchEditResourceHandler.DynamicAdapterFactory_ERROR_0);

		return createAdapterFactory(project, AdapterFactoryRegistry.instance().getDescriptors(ePackage, viewID));

	}

	private AdapterFactory createAdapterFactory(Object project, List descriptors) {
		if (descriptors == null || descriptors.isEmpty())
			return null;

		AdapterFactory fact = null;
		IEnablementIdentifier identifier = null;
		AdapterFactoryDescriptor desc = null;
		if (descriptors.size() == 1) {
			desc = (AdapterFactoryDescriptor) descriptors.get(0);
			identifier = getIdentifier(project, desc);
			addListenerIfNecessary(identifier);
			if (project.equals(NO_PROJECT) || (identifier != null && identifier.isEnabled()))
				return desc.createInstance();
			return null;
		}
		List factories = new ArrayList(descriptors.size());
		for (int i = 0; i < descriptors.size(); i++) {
			desc = (AdapterFactoryDescriptor) descriptors.get(i);
			identifier = getIdentifier(project, desc);
			addListenerIfNecessary(identifier);
			if (project.equals(NO_PROJECT) || (identifier != null && identifier.isEnabled())) {
				fact = desc.createInstance();
				if (fact != null)
					factories.add(fact);
			}
		}
		if (factories.isEmpty())
			return null;

		return new ExtendedComposedAdapterFactory(factories);
	}

	/**
	 * @param project
	 * @param desc
	 * @return
	 */
	private IEnablementIdentifier getIdentifier(Object project, AdapterFactoryDescriptor desc) {
		IEnablementIdentifier identifier = null;
		if (isRespectingActivities() && project instanceof IProject)
			identifier = IEnablementManager.INSTANCE.getIdentifier(desc.getID(), (IProject) project);
		else if (project instanceof IProject)
			identifier = EnablementManager.INSTANCE.getIdentifier(desc.getID(), (IProject) project);
		return identifier;
	}

	/**
	 * @return
	 */
	private boolean isRespectingActivities() {
		return respectingActivities;
	}

	protected void addListenerIfNecessary(IEnablementIdentifier identifier) {
		if (identifier == null)
			return;
		identifier.addIdentifierListener(this);
		getEnablementIdentifiers().add(identifier);
	}

	protected boolean isListeningTo(IEnablementIdentifier identifier) {
		return getEnablementIdentifiers().contains(identifier);
	}

	/**
	 * @return Returns the enablementIdentifiers.
	 */
	protected Set getEnablementIdentifiers() {
		if (enablementIdentifiers == null)
			enablementIdentifiers = new HashSet();
		return enablementIdentifiers;
	}

	private Set enablementIdentifiers;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.enablement.IEnablementIdentifierListener#identifierChanged(org.eclipse.wst.common.frameworks.internal.enablement.EnablementIdentifierEvent)
	 */
	@Override
	public void identifierChanged(EnablementIdentifierEvent identifierEvent) {
		if (identifierEvent.hasEnabledChanged() || identifierEvent.hasFunctionGroupIdsChanged()) {
			Object project = ((EnablementIdentifier) identifierEvent.getIdentifier()).getProject();
			if (project != null) {
				removeAdapterFactories(project);
				/*
				 * final Notifier notifier = (Notifier) getCachedRoots().get(project);
				 */
				/* force a viewer refresh */
				notifyChanged(new NotificationImpl(Notification.ADD, null, null) {

					/*
					 * (non-Javadoc)
					 * 
					 * @see org.eclipse.emf.common.notify.impl.NotificationImpl#getNotifier()
					 */
					@Override
					public Object getNotifier() {
						return null; // notifier;
					}
				});
			}
			/* else replace entire structure */
		}
	}

	/**
	 * @return Returns the resourceItemProviderAdapterFactory.
	 */
	public ResourceItemProviderAdapterFactory getResourceItemProviderAdapterFactory() {
		if (resourceItemProviderAdapterFactory == null) {
			resourceItemProviderAdapterFactory = new ResourceItemProviderAdapterFactory();
			resourceItemProviderAdapterFactory.addListener(this);
		}
		return resourceItemProviderAdapterFactory;
	}
}
