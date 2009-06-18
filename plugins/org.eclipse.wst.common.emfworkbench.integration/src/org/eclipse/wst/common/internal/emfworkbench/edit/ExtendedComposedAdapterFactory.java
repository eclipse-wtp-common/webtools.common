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
/*
 * Created on Dec 3, 2003
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.internal.emfworkbench.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.wst.common.internal.emfworkbench.integration.EMFWorkbenchEditPlugin;

/**
 * @author schacher
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ExtendedComposedAdapterFactory extends ComposedAdapterFactory {

	/**
	 * @param adapterFactory
	 */
	public ExtendedComposedAdapterFactory(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * @param adapterFactories
	 */
	public ExtendedComposedAdapterFactory(AdapterFactory[] adapterFactories) {
		super(adapterFactories);
	}

	/**
	 * @param adapterFactories
	 */
	public ExtendedComposedAdapterFactory(Collection adapterFactories) {
		super(adapterFactories);
	}

	/*
	 * overrode from the super class, changed not to check supertypes of the EObject, because that
	 * will be handled by the DynamicAdapterFactory
	 * 
	 * @see org.eclipse.emf.common.notify.AdapterFactory#adapt(org.eclipse.emf.common.notify.Notifier,
	 *      java.lang.Object)
	 */
	@Override
	public Adapter adapt(Notifier target, Object type) {
		Adapter result = null;

		if (target instanceof EObject) {
			EObject eObject = (EObject) target;
			EClass eClass = eObject.eClass();
			if (eClass != null) {
				EPackage ePackage = eClass.getEPackage();
				Collection types = new ArrayList();
				types.add(ePackage);
				if (type != null) {
					types.add(type);
				}
				/* when an error occurs, remove the delegate and try again */
				boolean attemptAdaptAgain = true;
				while (result == null && attemptAdaptAgain) {
					attemptAdaptAgain = false;
					AdapterFactory delegateAdapterFactory = getFactoryForTypes(types);
					if (delegateAdapterFactory != null) {
						try {
							result = delegateAdapterFactory.adapt(target, type);
						} catch (RuntimeException re) {
							EMFWorkbenchEditPlugin.logError(re);
							adapterFactories.remove(delegateAdapterFactory);
							attemptAdaptAgain = true;
						}
					}
				}
			}
		} else {
			result = adapt(target, type, new HashSet(), target.getClass());
		}

		return result;
	}
}
