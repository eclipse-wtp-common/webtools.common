/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;


import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.w3c.dom.Node;


public class LinkUpdaterTarget extends Object {

	public static final LinkUpdaterTarget INSTANCE = new LinkUpdaterTarget();

	/**
	 * Constructor for LinkUpdaterMultiTarget.
	 * 
	 * @param adapter
	 */
	private LinkUpdaterTarget() {
		super();
	}


	public void updateDOM(Translator map, Node node, EObject mofObject) {
		refreshSourceObjects(map, node, mofObject, true);
	}

	public void updateMOF(Translator map, Node node, EObject mofObject) {
		refreshSourceObjects(map, node, mofObject, false);
	}

	protected void refreshSourceObjects(Translator map, Node node, EObject mofObject, boolean domUpdate) {
		TranslatorPath[] paths = map.getTranslatorPaths();
		for (int i = 0; i < paths.length; i++) {
			TranslatorPath path = paths[i];
			List allSourceObjects = path.findObjects(mofObject);
			for (Iterator iter = allSourceObjects.iterator(); iter.hasNext();) {
				EObject curObject = (EObject) iter.next();
				EMF2DOMAdapter curAdapter = (EMF2DOMAdapter) EcoreUtil.getAdapter(curObject.eAdapters(), EMF2DOMAdapter.class);
				if (curAdapter != null) {
					if (domUpdate)
						curAdapter.updateDOM();
					else
						curAdapter.updateMOF();
				}
			}
		}
	}
}