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
 * Created on Mar 20, 2003
 *
 */
package org.eclipse.wst.common.internal.emf.resource;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Specialized translator for objects that contain simple mappings with no specialized behavior.
 */
public class GenericTranslator extends Translator {

	protected Translator[] children;

	/**
	 * @param domNameAndPath
	 * @param eClass
	 */
	public GenericTranslator(String domNameAndPath, EClass eClass) {
		super(domNameAndPath, eClass);
	}

	/**
	 * @param domNameAndPath
	 * @param aFeature
	 */
	public GenericTranslator(String domNameAndPath, EStructuralFeature aFeature) {
		super(domNameAndPath, aFeature);
	}

	/**
	 * @param domNameAndPath
	 * @param aFeature
	 * @param path
	 */
	public GenericTranslator(String domNameAndPath, EStructuralFeature aFeature, TranslatorPath path) {
		super(domNameAndPath, aFeature, path);
	}

	/**
	 * @param domNameAndPath
	 * @param aFeature
	 * @param paths
	 */
	public GenericTranslator(String domNameAndPath, EStructuralFeature aFeature, TranslatorPath[] paths) {
		super(domNameAndPath, aFeature, paths);
	}

	/**
	 * @param domNameAndPath
	 * @param aFeature
	 * @param eClass
	 */
	public GenericTranslator(String domNameAndPath, EStructuralFeature aFeature, EClass eClass) {
		super(domNameAndPath, aFeature, eClass);
	}

	/**
	 * @param domNameAndPath
	 * @param aFeature
	 * @param style
	 */
	public GenericTranslator(String domNameAndPath, EStructuralFeature aFeature, int style) {
		super(domNameAndPath, aFeature, style);
	}

	/**
	 * @return Translator[]
	 */
	public Translator[] getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 * 
	 * @param children
	 *            The children to set
	 */
	public void setChildren(Translator[] children) {
		this.children = children;
	}


	public static Translator appendChildren(GenericTranslator tran, Translator[] child) {
		Translator[] orgChild = tran.getChildren();
		Translator[] newChildren = (Translator[]) concat(orgChild, child);
		tran.setChildren(newChildren);
		return tran;
	}

	public static Translator appendChild(GenericTranslator tran, Translator child) {
		Object[] orgChild = tran.getChildren();
		Translator[] newChildren = (Translator[]) concat(orgChild, child);
		tran.setChildren(newChildren);
		return tran;
	}
}