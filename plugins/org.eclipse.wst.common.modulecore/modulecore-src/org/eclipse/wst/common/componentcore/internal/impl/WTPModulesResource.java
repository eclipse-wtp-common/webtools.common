/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.wst.common.componentcore.internal.impl;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.wst.common.componentcore.internal.util.WTPModulesTranslator;
import org.eclipse.wst.common.internal.emf.resource.Renderer;
import org.eclipse.wst.common.internal.emf.resource.Translator;
import org.eclipse.wst.common.internal.emf.resource.TranslatorResource;
import org.eclipse.wst.common.internal.emf.resource.TranslatorResourceImpl;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class WTPModulesResource extends TranslatorResourceImpl implements TranslatorResource {
	
	public WTPModulesResource(URI aURI, Renderer aRenderer) {
		super(aURI, aRenderer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emf.resource.TranslatorResourceImpl#getDefaultPublicId()
	 */
	protected String getDefaultPublicId() { 
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emf.resource.TranslatorResourceImpl#getDefaultSystemId()
	 */
	protected String getDefaultSystemId() { 
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emf.resource.TranslatorResourceImpl#getDefaultVersionID()
	 */
	protected int getDefaultVersionID() { 
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emf.resource.TranslatorResource#getDoctype()
	 */
	public String getDoctype() { 
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.internal.emf.resource.TranslatorResource#getRootTranslator()
	 */
	public Translator getRootTranslator() {
		return WTPModulesTranslator.INSTANCE;
	}
//	 
//	 
//	// temporary for debugging not to be released. 
//	protected void initializeContents() {
//		contents = new ResourceImpl.ContentsEList() {
//			public boolean add(Object object) {
//				renderer.prepareToAddContents();
//				return super.add(object);
//			}
//
//			public boolean addAll(Collection collection) {
//				renderer.prepareToAddContents();
//				return super.addAll(collection);
//			}
//		};
//	}
}
