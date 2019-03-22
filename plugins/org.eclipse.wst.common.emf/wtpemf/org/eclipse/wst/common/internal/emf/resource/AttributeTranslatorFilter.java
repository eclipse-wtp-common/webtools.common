/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
/*
 * Created on Sep 21, 2003
 *  
 */
package org.eclipse.wst.common.internal.emf.resource;


public final class AttributeTranslatorFilter extends TranslatorFilter {

	public AttributeTranslatorFilter() {
		super(null, -1);
	}

	public AttributeTranslatorFilter(Translator trans, int version) {
		super(trans, version);
	}

	@Override
	public final int scanNextTranslator(Translator[] children, int start) {
		int found = start + 1;
		for (; found < children.length; ++found) {
			if (children[found].isDOMAttribute())
				break;
		}
		found = (found < children.length) ? found : -1;
		return found;
	}
}