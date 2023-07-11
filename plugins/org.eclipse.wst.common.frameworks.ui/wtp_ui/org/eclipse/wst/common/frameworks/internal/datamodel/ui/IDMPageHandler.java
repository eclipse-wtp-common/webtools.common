/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel.ui;

public interface IDMPageHandler {

	/**
	 * return this as the page name if the expectedNextPageName or expectedPreviousPageName should
	 * be skipped
	 */
	public static final String SKIP_PAGE = "IDMExtendedPageHandler.SKIP_PAGE"; //$NON-NLS-1$

	/**
	 * prefix this string with the name of the page which occurs before the page that should be
	 * returned. E.G. suppose your page contributions know about pages A, B and C, and you want the
	 * to skip pages B and C, when going to the next page from A. To do this, return PAGE_AFTER+C.
	 * The framework will then ask the wizard for the page that normally comes after page C.
	 * PAGE_BEFORE works similarly.
	 */
	public static final String PAGE_AFTER = "IDMExtendedPageHandler.PAGE_AFTER"; //$NON-NLS-1$

	/**
	 * same as PAGE_AFTER, except for returing the page before.
	 */
	public static final String PAGE_BEFORE = "IDMExtendedPageHandler.PAGE_BEFORE"; //$NON-NLS-1$

	/**
	 * Return the name of the page that should be next
	 * 
	 * @param currentPageName
	 *            the page the wizard is currently on
	 * @param expectedNextPageName
	 *            the page that would normally be next
	 * @return
	 */
	public String getNextPage(String currentPageName, String expectedNextPageName);

	/**
	 * Return the name of the page that should be previous
	 * 
	 * @param currentPageName
	 *            the page the wizard is currently on
	 * @param expectedNextPageName
	 *            the page that would normally be previous
	 * @return
	 */
	public String getPreviousPage(String currentPageName, String expectedPreviousPageName);
}
