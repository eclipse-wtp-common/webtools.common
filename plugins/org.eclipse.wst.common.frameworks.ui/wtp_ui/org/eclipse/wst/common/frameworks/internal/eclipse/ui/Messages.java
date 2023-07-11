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
package org.eclipse.wst.common.frameworks.internal.eclipse.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
  private static final String BUNDLE_NAME = "org.eclipse.wst.common.frameworks.internal.eclipse.ui.environment"; //$NON-NLS-1$
  
  public static String TITLE_WARNING;
  public static String TITLE_ERROR;
  public static String TITLE_INFO;
  
  static
  {
    NLS.initializeMessages( BUNDLE_NAME, Messages.class );
  }
}
