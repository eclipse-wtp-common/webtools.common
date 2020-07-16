/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Aug 20, 2003
 *
 */
package org.eclipse.wst.common.internal.emf.resource;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.common.internal.emf.utilities.Namespace;
import org.eclipse.wst.common.internal.emf.utilities.NamespaceAdapter;


/**
 * @author schacher
 */
public class DefaultTranslatorFactory implements VariableTranslatorFactory {

	public static final String XMLNS = "xmlns:"; //$NON-NLS-1$
	public static final DefaultTranslatorFactory INSTANCE = new DefaultTranslatorFactory();

	public DefaultTranslatorFactory() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.VariableTranslatorFactory#accepts(java.lang.String)
	 */
	@Override
	public boolean accepts(String domName) {
		return domName != null && domName.startsWith(XMLNS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.VariableTranslatorFactory#accepts(org.eclipse.emf.common.notify.Notification)
	 */
	@Override
	public boolean accepts(Notification notif) {
		return notif.getFeature() == NamespaceAdapter.NOTIFICATION_FEATURE;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.VariableTranslatorFactory#create(java.lang.String)
	 */
	@Override
	public Translator create(String domName) {
		return new NamespaceTranslator(domName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.VariableTranslatorFactory#create(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public List create(Notifier target) {
		if (!(target instanceof EObject))
			return null;
		List namespaces = NamespaceAdapter.getNamespaces((EObject) target);
		if (namespaces == null || namespaces.isEmpty())
			return null;
		List result = new ArrayList(namespaces.size());
		for (int i = 0; i < namespaces.size(); i++) {
			result.add(create((Namespace) namespaces.get(i)));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.VariableTranslatorFactory#create(org.eclipse.emf.common.notify.Notification)
	 */
	@Override
	public Translator create(Notification notif) {
		Namespace ns = (Namespace) notif.getNewValue();
		return create(ns);
	}

	private Translator create(Namespace ns) {
		return new NamespaceTranslator(XMLNS + ns.getPrefix());
	}



}
