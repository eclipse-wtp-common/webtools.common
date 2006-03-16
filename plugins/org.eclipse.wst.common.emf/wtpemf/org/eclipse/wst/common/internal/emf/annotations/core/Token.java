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
 * A string, and the range it was taken from in the source file. The range is inclusive. (ie, with
 * source "ABCD", the beginning and end for the Token "BC" would be (1,2) )
 * 
 * @author Pat Kelley
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Token {
	private String text;
	private int beginning;
	private int end;



	/**
	 * @return Position in original source of the first character of this token.
	 */
	public int getBeginning() {
		return beginning;
	}

	/**
	 * @return Position in the original source of the last character of this token.
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @return The token string.
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param i
	 *            A source position
	 */
	public void setBeginning(int i) {
		beginning = i;
	}

	/**
	 * @param i
	 *            A source position.
	 */
	public void setEnd(int i) {
		end = i;
	}

	/**
	 * @param string
	 */
	public void setText(String string) {
		text = string;
	}

	public int length() {
		return text.length();
	}

	/**
	 * Tests whether <code>srcPos</code> comes immediately after the last character in this token.
	 * 
	 * @param srcPos
	 *            A position in the original source the token came from.
	 * @return true if srcPos comes immediately after this token.
	 */
	public boolean immediatelyPrecedes(int srcPos) {
		return end + 1 == srcPos;
	}

	/**
	 * Tests whether srcPos is within the original source range range of the token.
	 * 
	 * @param srcPos
	 * @return
	 */
	public boolean contains(int srcPos) {
		return srcPos >= beginning && srcPos <= end;
	}
}