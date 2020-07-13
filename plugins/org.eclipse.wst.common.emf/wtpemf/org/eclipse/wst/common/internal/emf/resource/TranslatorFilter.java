/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
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

import org.eclipse.emf.ecore.EObject;


/*
 * Translator filters can be used to differentiate between Object and Attribute Translators without
 * breaking them out into seperate data structures. The Filter will rely on the underlying data
 * structure used to store the children of the given Translator.
 * 
 * getNextChild() will return null when no other translators of the given genre are available.
 * 
 * The processing hints which are created and then passed to getNext*Translator() store information
 * that needs to be persisted between calls. The createProcessingHints() will create a properly
 * initialized array. The constants NEXT_START_HINT_INDX and MODE_HINT_INDX provide pointers into
 * the array for the NEXT_START position and the proper mode to operate in
 * (STANDARD_TRANSLATORS_MODE or
 *  
 */
public abstract class TranslatorFilter {

	protected final Translator translator;
	protected final int version;

	public static final int NEXT_START_HINT_INDX = 0;
	public static final int MODE_HINT_INDX = 1;

	public static final int STANDARD_TRANSLATORS_MODE = 0;
	public static final int VARIABLE_TRANSLATORS_MODE = 1;

	/*
	 * These TranslatorFilters are used in a stateless mode. Only their scanNextTranslator() methods
	 * will be invoked
	 */
	private static final TranslatorFilter objectTranslatorFilter = new ObjectTranslatorFilter(null, -1);
	private static final TranslatorFilter attributeTranslatorFilter = new AttributeTranslatorFilter(null, -1);


	protected int mode = STANDARD_TRANSLATORS_MODE;
	protected int index = -1;

	protected Translator cachedPeekAheadTranslator = null;

	public TranslatorFilter(Translator trans, int version) {
		this.translator = trans;
		this.version = version;
	}

	/**
	 * Calling peek twice will advance the current child
	 */
	public Translator peekNextChild(EObject target) {
		cachedPeekAheadTranslator = getNextChild(target);
		return cachedPeekAheadTranslator;
	}

	/**
	 * getNextChild() will return null when no other translators of the given genre are available.
	 */
	public Translator getNextChild(EObject target) {

		Translator result = null;
		if (cachedPeekAheadTranslator != null) {
			result = cachedPeekAheadTranslator;
			cachedPeekAheadTranslator = null;
			return result;
		}

		int found = 0;
		Translator children[] = null;
		switch (mode) {
			case STANDARD_TRANSLATORS_MODE :
				children = this.translator.getChildren(target, this.version);

				/* Look for the next Attribute Translator */
				found = scanNextTranslator(children, this.index);

				if (found >= 0) {
					/*
					 * If found, (1) update the result, (2) update the index so we can skip ahead on
					 * the next invocation (3) escape the VARIABLE_TRANSLATORS processing
					 */
					result = children[found];
					this.index = found;
					break;
				}
				/*
				 * Reset the index. DO NOT BREAK. Allow entry into VARIABLE_TRANSLATORS case
				 */
				this.index = -1;
				/*
				 * update the mode to VARIABLE_TRANSLATORS so we can skip to it directly next time
				 */
				this.mode = VARIABLE_TRANSLATORS_MODE;
			//$FALL-THROUGH$
			case VARIABLE_TRANSLATORS_MODE :
				children = this.translator.getVariableChildren(target, this.version);
				found = scanNextTranslator(children, this.index);
				if (found >= 0) {
					/*
					 * If found, (1) update the result, (2) update the index so we can skip ahead on
					 * the next invocation
					 */
					result = children[found];
					this.index = found;
				}

		}

		return result;
	}

	public static final int[] createProcessingHints() {
		return new int[]{-1, STANDARD_TRANSLATORS_MODE};
	}

	public static final Translator getNextAttributeTranslator(Translator translator, int startHint, int[] nextHint, EObject target, int version) {

		return TranslatorFilter.getNextChild(translator, startHint, nextHint, target, version, attributeTranslatorFilter);
	}

	public static final Translator getNextObjectTranslator(Translator translator, int startHint, int[] nextHint, EObject target, int version) {

		return TranslatorFilter.getNextChild(translator, startHint, nextHint, target, version, objectTranslatorFilter);
	}

	/**
	 * getNextChild() takes hints on where to begin in the children array of the given Translator.
	 * When it finds the translator, it will update the hints array with the start hint for the next
	 * invocation(hints[0]) and when necessary it will use update the mode (hints[1]) to either
	 * STANDARD_TRANSLATORS or VARIABLE_TRANSLATORS.
	 * 
	 * @param translator
	 * @param startHint
	 * @param hints
	 *            a two-element array: hints[0] will be updated with the next startHint and hints[1]
	 *            will be used to store the mode.
	 * @param target
	 * @param version
	 * @param translatorFilter
	 * @return
	 */
	public static final Translator getNextChild(Translator translator, int startHint, int[] hints, EObject target, int version, TranslatorFilter translatorFilter) {

		Translator result = null;
		
		int innerStartHint = startHint;
		int index = innerStartHint;
		Translator children[] = null;

		switch (hints[MODE_HINT_INDX]) {
			case STANDARD_TRANSLATORS_MODE :
				children = translator.getChildren(target, version);
				if (children != null && innerStartHint < children.length) {

					/* Look for the next Attribute Translator */
					index = translatorFilter.scanNextTranslator(children, index);

					if (index >= 0) {
						/*
						 * If found, (1) update the result, (2) update the index so we can skip
						 * ahead on the next invocation (3) escape the VARIABLE_TRANSLATORS
						 * processing
						 */
						result = children[index];
						break;
					}
					/*
					 * DO NOT BREAK we will default to VARIABLE TRANSLATORS MODE so we must reset
					 * the startHint appropriately
					 */
					innerStartHint = -1;
				}

			//$FALL-THROUGH$
			case VARIABLE_TRANSLATORS_MODE :
				hints[MODE_HINT_INDX] = VARIABLE_TRANSLATORS_MODE;
				/*
				 * Reset the index.
				 */
				index = innerStartHint;
				children = translator.getVariableChildren(target, version);
				if (children != null && children.length > 0 && innerStartHint < children.length) {
					index = translatorFilter.scanNextTranslator(children, index);
					result = (index >= 0) ? children[index] : null;
				}
		}

		hints[NEXT_START_HINT_INDX] = (result == null && children != null) ? children.length : index;

		return result;
	}


	public abstract int scanNextTranslator(Translator[] children, int start);

	/**
	 * @return
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return
	 */
	public int getMode() {
		return mode;
	}

}