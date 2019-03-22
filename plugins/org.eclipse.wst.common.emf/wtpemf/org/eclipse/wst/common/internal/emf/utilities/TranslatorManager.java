/***************************************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.utilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.common.core.util.RegistryReader;
import org.eclipse.wst.common.internal.emf.plugin.EcoreUtilitiesPlugin;
import org.eclipse.wst.common.internal.emf.resource.Translator;

/**
 * This is the TranslatorManager class used by the TranslatorService in order to discover
 * and cache all of the extended Translators to be used by EMF2DOMAdapterImpl.
 */
public class TranslatorManager {
	
	/**
	 * The singleton TranslatorManager instance
	 */
	private static final TranslatorManager INSTANCE = new TranslatorManager();
	
	/**
	 * Cache of all the TranslatorDescriptors
	 */
	private final Set translators = new HashSet();
	
	/**
	 * Default static empty array used when no descriptors found
	 */
	private static final TranslatorDescriptor[] NO_EXTENDED_TRANSLATORS = new TranslatorDescriptor[0];
	
	/**
	 * Returns the singleton instance of the TranslatorManager
	 * @return TranslatorManager INSTANCE
	 */
	public static TranslatorManager getInstance() {
		return INSTANCE;
	}

	/**
	 * Private constructor
	 */
	private TranslatorManager() {
		new TranslatorRegistry().readRegistry();
	}
	
	/**
	 * Simple Registry Reader used by Translator Manager to recognize the Translator extensions
	 */
	private class TranslatorRegistry extends RegistryReader {
		
		/**
		 * The Translator element of the extension point.
		 */
		public static final String TRANSLATOR = "translator"; //$NON-NLS-1$
		
		/**
		 * Default constructor
		 */
		public TranslatorRegistry() {
			super(EcoreUtilitiesPlugin.ID, EcoreUtilitiesPlugin.TRANSLATOR_EXTENSTION_POINT);
		}

		/**
		 * Add the configuration element if it matchs the expected translator element name
		 */
		@Override
		public boolean readElement(IConfigurationElement element) {
			boolean result = false;
			if (TRANSLATOR.equals(element.getName())) {
				addTranslatorDescriptor(new TranslatorDescriptor(element));
					result = true;
			}
			return result;
		}
	}

	/**
	 * Describes a Translator extension point element
	 */
	public class TranslatorDescriptor {
	
		/**
		 * Qualified class name attribute
		 */
		public static final String CLASSNAME = "className"; //$NON-NLS-1$
		
		/**
		 * The config element for this Descriptor
		 */
		private final IConfigurationElement configElement;
		
		/**
		 * the cached qualified className value of this descriptor
		 */
		private String className;
	
		/**
		 * Constructor
		 * @param aConfigElement
		 */
		public TranslatorDescriptor(IConfigurationElement aConfigElement) {
			super();
			configElement = aConfigElement;
			className = configElement.getAttribute(CLASSNAME);
		}
		
		/**
		 * Retrieve the cached value of the qualified class name of the extended Translator
		 * @return String classname
		 */
		public String getClassName() {
			return className;
		}
	
		/**
		 * Create the actual Translator instance from the configuration element.
		 * @return Translator
		 */
		public Translator createTranslator() {
			Translator instance = null;
			try {
				instance = (Translator) configElement.createExecutableExtension(CLASSNAME);
			} catch (CoreException e) {
				EcoreUtilitiesPlugin.logError(e);
			}
			return instance;
		}
	}
	
	/**
	 * Add the TranslatorDescriptor to the cache of descriptor extensions.
	 * @param descriptor
	 */
	private void addTranslatorDescriptor(TranslatorDescriptor descriptor) {
		Assert.isNotNull(descriptor);
		translators.add(descriptor);
	}
	
	/**
	 * Find all the associated TranslatorDescriptors for the Traslator extensions defined.
	 * @return TranslatorDescriptor[]
	 */
	public TranslatorDescriptor[] findTranslators() {
		List result = new ArrayList();
		TranslatorDescriptor descriptor = null;
		for (Iterator translatorsItr = translators.iterator(); translatorsItr.hasNext();) {
			descriptor = (TranslatorDescriptor) translatorsItr.next();
			result.add(descriptor);
		}
		if (result.size() == 0) {
			return NO_EXTENDED_TRANSLATORS;
		}
		return (TranslatorDescriptor[]) result.toArray(new TranslatorDescriptor[result.size()]);
	}
}
