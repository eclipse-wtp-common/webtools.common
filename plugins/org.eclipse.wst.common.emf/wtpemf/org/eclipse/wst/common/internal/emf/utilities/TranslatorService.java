/***************************************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.common.internal.emf.resource.Translator;
import org.eclipse.wst.common.internal.emf.utilities.TranslatorManager.TranslatorDescriptor;

/**
 * This is the service class to deliver API to use to retrieve Translator extensions from
 * the TranslatorManager and its RegistryReader.
 */
public class TranslatorService {

	/**
	 * Static Key value pair of descriptors as keys and Translator instances as values
	 */
	private static final Map translators = new HashMap();
	
	/**
	 * Static empty array used when no extensions found
	 */
	// never used
	//private static final Translator[] NO_TRANSLATORS = new Translator[0];
	
	/**
	 * Singleton instance of the Translator service
	 */
	private static final TranslatorService INSTANCE = new TranslatorService();
	
	/**
	 * Default constructor
	 */
	private TranslatorService() {
		super();
	}
	
	public static TranslatorService getInstance() {
		return INSTANCE;
	}
	
	/**
	 * This will return the associated extension point TranslatorDescriptor objects from the manager
	 * @return TranslatorDescriptor[]
	 */
	public TranslatorDescriptor[] getTranslatorDescriptors() {
		return TranslatorManager.getInstance().findTranslators();
	}
	
	/**
     * This retrieves the extended Translators using the extension point manager and descriptors
     * @return Translator[] (Note, the return value may contain null entries.)
     */
	public Translator[] getTranslators() {
		TranslatorDescriptor[] descriptors = getTranslatorDescriptors();
		Translator[] result = new Translator[descriptors.length];
		//The result index could differ from the descriptors index.
		int resultIndex = 0;
		for (int i=0; i<descriptors.length; i++) {
			Translator instance = getTranslator(descriptors[i]);
			if (instance!=null) {
				result[resultIndex] = instance;
				resultIndex++;
			}
		}
		return result;  
    }
	
	/**
	 * Retrieve the existing associated Translator instance for the descriptor, or create a new
	 * one and cache on the Set.
	 * 
	 * @param translatorDescriptor
	 * @return Translator associated with the descriptor
	 */
	public Translator getTranslator(TranslatorDescriptor translatorDescriptor) {
		Translator translator = (Translator) translators.get(translatorDescriptor);
		if (translator != null)
			return translator;
		
		synchronized (translators) {
			translator = translatorDescriptor.createTranslator();
			if (translator != null)
				translators.put(translatorDescriptor, translator);
		}
		return translator;
	}
}
