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
package org.eclipse.wst.common.internal.emf.resource;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.common.annotations.core.TagParseEventHandler;
import org.eclipse.wst.common.annotations.core.Token;


/**
 * @author mdelder
 *  
 */
public class AnnotatedCommentHandler implements TagParseEventHandler {

	private Map annotations;

	private Token annotationToken;

	/**
	 *  
	 */
	public AnnotatedCommentHandler() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.annotations.core.TagParseEventHandler#annotationTag(org.eclipse.wst.common.annotations.core.Token)
	 */
	public void annotationTag(Token tag) {
		this.annotationToken = tag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.annotations.core.TagParseEventHandler#endOfTag(int)
	 */
	public void endOfTag(int pos) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.annotations.core.TagParseEventHandler#attribute(org.eclipse.wst.common.annotations.core.Token,
	 *      int, org.eclipse.wst.common.annotations.core.Token)
	 */
	public void attribute(Token name, int equalsPosition, Token value) {
		if (value.getText() == null || value.getText().length() == 0)
			getAnnotations().put(this.annotationToken.getText(), name.getText());
		else
			getAnnotations().put(name.getText(), value.getText());
	}

	/**
	 * @return Returns the annotations.
	 */
	public Map getAnnotations() {
		if (annotations == null)
			annotations = new HashMap();
		return annotations;
	}
}