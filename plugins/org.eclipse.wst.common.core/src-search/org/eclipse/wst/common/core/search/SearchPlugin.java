/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.core.search;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.core.search.internal.SearchParticipantRegistry;
import org.eclipse.wst.common.core.search.internal.SearchParticipantRegistryReader;
import org.eclipse.wst.common.core.search.pattern.SearchPattern;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 */
public class SearchPlugin extends Plugin implements ISearchOptions
{
	//the ID for this plugin (added automatically by logging quickfix)
	public static final String PLUGIN_ID = "org.eclipse.wst.common.core"; //$NON-NLS-1$

	/**
	 * UI Context extension point.
	 * 
	 * @since 1.0.0
	 */
	public static final String UI_CONTEXT_EXTENSION_POINT = "uiContextSensitiveClass"; //$NON-NLS-1$
	/**
	 * UITester element name.
	 * 
	 * @since 1.0.0
	 */
	public static final String UI_TESTER_EXTENSION_POINT = "uiTester"; //$NON-NLS-1$

	private SearchParticipantRegistry searchParticipantRegistry;

	// The shared instance.
	private static SearchPlugin plugin;

	/**
	 * The constructor.
	 */
	public SearchPlugin()
	{
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception
	{
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static SearchPlugin getDefault()
	{
		return plugin;
	}

	private SearchParticipantRegistry getSearchParticipantRegistry()
	{
		if (searchParticipantRegistry == null)
		{
			searchParticipantRegistry = new SearchParticipantRegistry();
			new SearchParticipantRegistryReader(searchParticipantRegistry)
					.readRegistry();
		}
		return searchParticipantRegistry;
	}


	public SearchParticipant getSearchParticipant(String id)
	{
		return getSearchParticipantRegistry().getSearchParticipant(id);
	}

	/**
	 * Returns the registered search participants that support the specified search
	 * pattern and options, loading and creating the search participants if necessary.
	 * @param pattern The pattern representing a search request
	 * @param searchOptions Map of options and values defining behavior of the search;
	 *         <code>null</code> if no options are specified;
	 *         some options and values are provided by {@link ISearchOptions}
	 * @return Array of search participants that support the specified search request 
	 */
	SearchParticipant[] loadSearchParticipants(SearchPattern pattern, Map searchOptions)
	{
		return getSearchParticipantRegistry().getParticipants(pattern, searchOptions);
	}

	public static IStatus createStatus(int severity, String message, Throwable exception) {
		return new Status(severity, PLUGIN_ID, message, exception);
	}

	public static IStatus createStatus(int severity, String message) {
		return createStatus(severity, message, null);
	}

	public static void logError(Throwable exception) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( createStatus(IStatus.ERROR, exception.getMessage(), exception));
	}

	public static void logError(CoreException exception) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( exception.getStatus() );
	}

	public static void logWarning(String message) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log(createStatus(IStatus.WARNING, message));
	}

	public static void logWarning(Throwable exception) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( createStatus(IStatus.WARNING, exception.getMessage(), exception));
	}

	public static void logError(String message) {
		Platform.getLog(Platform.getBundle(PLUGIN_ID)).log( createStatus(IStatus.ERROR, message));
	}
}
