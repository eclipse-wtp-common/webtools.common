/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.core.util;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.*;
import org.eclipse.wst.common.core.search.SearchPlugin;


/**
 * Static utility class for UIContext determination.
 * 
 * @since 1.0.0
 */
public class UIContextDetermination {

	private static final int UNKNOWN = 100;

	public static final String HEADLESS_CONTEXT_LITERAL = "Headless"; //$NON-NLS-1$

	public static final String UI_CONTEXT_LITERAL = "UI"; //$NON-NLS-1$

	public static final int HEADLESS_CONTEXT = 102;

	public static final int UI_CONTEXT = 101;

	private static Map cachedExtensions = null;

	private static int currentContext = UNKNOWN;

	/**
	 * Do not use- this class should not be instantiated, but should be accessed via its static methods only.
	 */
	protected UIContextDetermination() {
	}

	/**
	 * Returns an instance of a given class based on the UI or Headless context.
	 * 
	 * @param key
	 * @return new class instance for the given key.
	 * @throws IllegalArgumentException
	 *             If the key is invalid (e.g. no extension is found for the key)
	 */
	public static Object createInstance(String key) {
		Object result = null;
		if (cachedExtensions == null)
			initExtensions();
		IConfigurationElement contextSensitiveClass = (IConfigurationElement) cachedExtensions.get(key);
		try {
			if (contextSensitiveClass != null)
				result = contextSensitiveClass
						.createExecutableExtension(UIContextDeterminationRegistryReader.UI_CONTEXT_SENSTIVE_CLASS_CLASSNAME_ATTR);
		} catch (CoreException e) {
			SearchPlugin.logError("Problem loading extension not found for key \"" + key + "\"."); //$NON-NLS-1$ //$NON-NLS-2$
			SearchPlugin.logError(e);
		}
		if (result == null)
			SearchPlugin.logError("Extension not found for key \"" + key + "\"."); //$NON-NLS-1$ //$NON-NLS-2$
		return result;
	}

	/**
	 * Returns the current context -- determines the value if necessary.
	 * 
	 * @return current context
	 * @see #HEADLESS_CONTEXT
	 * @see #UI_CONTEXT
	 */
	public static int getCurrentContext() {
		if (currentContext == UNKNOWN) {
			currentContext = HEADLESS_CONTEXT;
			new UITesterRegistryReader().readRegistry();
		}
		return currentContext;
	}

	/*
	 * Invokes the UIContextDeterminationRegistryReader to cache all of the extensions, if necessary.
	 *  
	 */
	private static void initExtensions() {
		if (cachedExtensions == null) {
			cachedExtensions = new HashMap();
			new UIContextDeterminationRegistryReader().readRegistry();
		}
	}

	/*
	 * Converts the input to one of UI_CONTEXT or HEADLESS_CONTEXT. Defaults to HEADLESS on invalid input
	 * 
	 * @param literal @return
	 */
	private static int convertLiteral(String literal) {
		return (UI_CONTEXT_LITERAL.equals(literal)) ? UI_CONTEXT : HEADLESS_CONTEXT;
	}

	/*
	 * Reads the registration of UI Context-sensitive class extensions and initializes the cache of the UIContextDetermination object.
	 * 
	 * @author mdelder
	 */
	private static class UIContextDeterminationRegistryReader extends RegistryReader {

		public static final String UI_CONTEXT_SENSTIVE_CLASS_ELEMENT = "uiContextSensitiveClass"; //$NON-NLS-1$

		public static final String UI_CONTEXT_SENSTIVE_CLASS_KEY_ATTR = "key"; //$NON-NLS-1$

		public static final String UI_CONTEXT_SENSTIVE_CLASS_CLASSNAME_ATTR = "className"; //$NON-NLS-1$

		public static final String UI_CONTEXT_SENSTIVE_CLASS_CONTEXT_ATTR = "context"; //$NON-NLS-1$

		public UIContextDeterminationRegistryReader() {
			super(SearchPlugin.PLUGIN_ID, SearchPlugin.UI_CONTEXT_EXTENSION_POINT);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jem.util.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
		 */
		public boolean readElement(IConfigurationElement element) {
			boolean result = false;
			if (element.getName().equals(UI_CONTEXT_SENSTIVE_CLASS_ELEMENT)) {

				String key = element.getAttribute(UI_CONTEXT_SENSTIVE_CLASS_KEY_ATTR);
				String context = element.getAttribute(UI_CONTEXT_SENSTIVE_CLASS_CONTEXT_ATTR);

				if (!cachedExtensions.containsKey(key) || getCurrentContext() == convertLiteral(context))
					cachedExtensions.put(key, element);
				result = true;
			}
			return result;
		}
	}

	/*
	 * Reads the uiTester extension and instantiate the any of the UITester classes it finds.
	 * 
	 * The implementation has the side effect that if multiple UITesters are registered, any of them can trip the currentContext into the UI_CONTEXT
	 * state.
	 * 
	 * @author mdelder
	 */
	private static class UITesterRegistryReader extends RegistryReader {

		public static final String UI_TESTER_ELEMENT = "uiTester"; //$NON-NLS-1$

		public static final String UI_TESTER_CLASSNAME_ATTR = "className"; //$NON-NLS-1$

		public UITesterRegistryReader() {
			super(SearchPlugin.PLUGIN_ID, SearchPlugin.UI_TESTER_EXTENSION_POINT);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.ibm.etools.emf.workbench.RegistryReader#readElement(org.eclipse.core.runtime.IConfigurationElement)
		 */
		public boolean readElement(IConfigurationElement element) {
			boolean result = false;
			if (element.getName().equals(UI_TESTER_ELEMENT)) {
				result = true;
				try {
					// Don't bother running tester it if we already processed one extension that returned true.
					if (currentContext != UI_CONTEXT && canCreateExecutableExtension(element)) {
						UITester tester = (UITester) element.createExecutableExtension(UI_TESTER_CLASSNAME_ATTR);
						if (tester.isCurrentContextUI())
							currentContext = UI_CONTEXT;
					}
				} catch (Exception t) {
					SearchPlugin.logWarning("UIContextDetermination is proceeding in HEADLESS mode"); //$NON-NLS-1$
				}
			}
			return result;
		}

		public void readRegistry() {
			super.readRegistry();
			IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.jem.util", extensionPointId);
			if (point == null)
				return;
			IConfigurationElement[] elements = point.getConfigurationElements();
			for (int i = 0; i < elements.length; i++) {
				boolean recognized = this.readElement(elements[i]);
				if (!recognized) {
					logError(elements[i], "Error processing extension: " + elements[i]); //$NON-NLS-1$
				}
			}
		}
	}
}
