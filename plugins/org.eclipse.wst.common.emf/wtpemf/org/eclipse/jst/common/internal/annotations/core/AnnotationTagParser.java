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
package org.eclipse.jst.common.internal.annotations.core;

/**
 * @author Pat Kelley
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AnnotationTagParser {

	private TagParseEventHandler handler;

	private char[] input;

	int pos;

	int endOfLastGoodAttParse;

	public AnnotationTagParser(TagParseEventHandler tp) {
		if (tp == null) {
			throw new IllegalArgumentException(AnnotationsCoreResources.AnnotationTagParser_0); 
		}
		handler = tp;
	}

	private boolean eos() {
		return pos >= input.length;
	}

	private boolean isWS(char c) {
		return c == ' ' || c == '\n' || c == '\r' || c == '\t';
	}

	private void skipWS() {
		while (pos < input.length && (isWS(input[pos]) || input[pos] == '*')) {
			pos++;
		}
	}

	// Caller is expected to make sure the eos has not been reached.
	private char peek() {
		return input[pos];
	}

	// Caller is expected to check for EOS.
	private char nextChar() {
		return input[pos++];
	}

	private boolean isNextChar(char c) {
		if (eos())
			return false;
		return peek() == c;
	}

	private boolean isIDChar(char c) {
		return !isWS(c) && c != '=' && c != '@' && c != '\"';
	}

	private Token collectID() {
		StringBuffer b = new StringBuffer(16);
		Token t = new Token();

		t.setBeginning(pos);
		while (!eos() && isIDChar(peek())) {
			b.append(nextChar());
		}
		t.setEnd(pos - 1);
		t.setText(b.toString());
		return t;
	}

	private Token expectAttribName() {
		if (eos()) {
			return null;
		}
		int save = pos;

		Token retval = collectID();
		if (retval.length() == 0) {
			pos = save;
			return null;
		}
		return retval;
	}

	private Token expectTag() {
		if (eos()) {
			return null;
		}
		int savePos = pos;

		if (nextChar() != '@') {
			return null;
		}

		if (eos() || isWS(peek())) {
			return null;
		}

		Token retval = expectAttribName();

		if (retval.length() == 0) {
			pos = savePos + 1;
		}
		retval.setBeginning(savePos);

		// Save end of parse so we can pass it as the end of the parsed tag.
		endOfLastGoodAttParse = pos;
		return retval;
	}

	private Token expectQuotedValue() {
		skipWS();
		if (eos()) {
			return null;
		}

		Token tok = new Token();

		tok.setBeginning(pos);
		if (peek() != '\"') {
			return null;
		}
		nextChar();

		if (eos()) {
			return null;
		}

		StringBuffer b = new StringBuffer(64);

		while (!eos() && peek() != '\"') {
			b.append(nextChar());
		}
		if (!eos()) {
			nextChar();
		}

		tok.setEnd(pos - 1);
		tok.setText(b.toString());
		return tok;
	}

	private boolean expectAssign() {
		if (eos()) {
			return false;
		}

		if (nextChar() == '=') {
			return true;
		}
		pos--;
		return false;
	}

	private Token mkNullToken() {
		Token retval = new Token();

		retval.setBeginning(pos);
		retval.setEnd(pos - 1);
		retval.setText(""); //$NON-NLS-1$
		return retval;
	}

	private boolean parseNextAttribute() {
		skipWS();
		if (eos()) {
			return false;
		}
		Token key = collectID();

		if (key == null || key.length() == 0) {
			return false;
		}

		skipWS();
		if (eos()) {
			// Go ahead and report it, even though it is a partial attribute. (
			// we still fail here )
			handler.attribute(key, -1, mkNullToken());
			return false;
		}

		int eqPos = pos;

		if (!expectAssign()) {
			// Even though we won't parse this as a full attribute, go ahead and
			// call the handler with it. Some clients want to see partial
			// attributes.
			handler.attribute(key, -1, mkNullToken());
			return false;
		}
		skipWS();

		if (eos()) {
			// Same here - we fail on it, but we report it anyway
			handler.attribute(key, eqPos, mkNullToken());
			return false;
		}
		Token value = expectQuotedValue();

		if (value == null) {
			value = collectID();
			if (isNextChar('=')) {
				pos = value.getBeginning();
				value = mkNullToken();
			}
		}
		endOfLastGoodAttParse = pos;
		handler.attribute(key, eqPos, value);
		return true;
	}

	private void parseAttributes() {
		while (!eos() && parseNextAttribute()) {
			// loop while not end of string
		}
	}

	private void skipToTagChar() {
		while (!eos() && peek() != '@') {
			nextChar();
		}
	}

	public void setParserInput(char[] text) {
		input = text;
		pos = 0;
		endOfLastGoodAttParse = 0;
	}

	public void setParserInput(String text) {
		setParserInput(text.toCharArray());
	}

	public void parse() {
		while (!eos()) {
			skipToTagChar();
			Token tag = expectTag();
			if (tag == null) {
				break;
			}
			handler.annotationTag(tag);
			parseAttributes();
			handler.endOfTag(endOfLastGoodAttParse);
		}
	}

}