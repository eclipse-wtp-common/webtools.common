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
/*
 * Created on Mar 31, 2003
 *
 */
package org.eclipse.wst.common.internal.emf.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.w3c.dom.Node;

/**
 * @author schacher
 */
public abstract class MultiObjectTranslator extends Translator {
	private static final Translator[] EMPTY_TRANSLATORS = new Translator[]{};

	/**
	 * @param domNameAndPath
	 * @param aFeature
	 * @param style
	 */
	public MultiObjectTranslator(String domNameAndPath, EStructuralFeature aFeature) {
		super(domNameAndPath, aFeature);
	}

	public abstract Translator getDelegateFor(EObject o);

	public abstract Translator getDelegateFor(String domName, String readAheadName);



	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#createEMFObject(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public EObject createEMFObject(String nodeName, String readAheadName) {
		return getDelegateFor(nodeName, readAheadName).createEMFObject(nodeName, readAheadName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#getChildren(java.lang.Object)
	 */
	@Override
	public Translator[] getChildren(Object o, int version) {
		if (o == null)
			return EMPTY_TRANSLATORS;
		return getDelegateFor((EObject) o).getChildren(o, version);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#getDOMName(java.lang.Object)
	 */
	@Override
	public String getDOMName(Object value) {
		return getDelegateFor((EObject) value).getDOMName(value);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.impl.Translator#isManagedByParent()
	 */
	@Override
	public boolean isManagedByParent() {
		return false;
	}

	@Override
	public boolean shouldIndentEndTag(Node node) {
		if (node.getNodeName().equals(getDOMPath())) {
			return super.shouldIndentEndTag(node);
		}
		Translator delegate = getDelegateFor(node.getNodeName(), null);
		if (delegate != null) {
			return delegate.shouldIndentEndTag(node);
		}
		return super.shouldIndentEndTag(node);
	}

}