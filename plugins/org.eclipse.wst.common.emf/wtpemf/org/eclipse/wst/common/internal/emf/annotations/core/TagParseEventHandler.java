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
 * Created on Nov 11, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.internal.emf.annotations.core;


/**
 * Parser and interface for objects that want to receive parsing events. When parsing is started
 * through the <code>parse()</code> method, event methods are called for interesting features in
 * the parse. ( like a SAX ContentHandler )
 * 
 * @author Pat Kelley
 */
public interface TagParseEventHandler {

	/**
	 * Called when the annotation tag is encountered. This will always be the first piece of content
	 * encountered. Followed by a endOfTag( ) call when the end of the tag is reached.
	 */
	public void annotationTag(Token tag);

	/**
	 * Called when the entire annotation for a single tag has been parsed.
	 * 
	 * @param pos
	 *            Position in the stream of the end of the annotation.
	 */
	public void endOfTag(int pos);

	/**
	 * Called for every attribute setting encountered for an annotation tag.
	 * 
	 * @param name
	 *            Name of the attribute.
	 * @param equalsPosition
	 *            Source position of the equals sign, or -1 if no equals sign was found.
	 * @param value
	 *            Value of the attribute, with any quotes stripped off. Will be zero length token if
	 *            no attribute was found.
	 */
	public void attribute(Token name, int equalsPosition, Token value);
}