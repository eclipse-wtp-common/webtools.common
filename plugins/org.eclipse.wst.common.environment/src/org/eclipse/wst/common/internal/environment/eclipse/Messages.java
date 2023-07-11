/***************************************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.environment.eclipse;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
  private static final String BUNDLE_NAME = "org.eclipse.wst.common.internal.environment.eclipse.environment"; //$NON-NLS-1$
  
  public static String MSG_NULL_ARG_SPECIFIED;
  public static String MSG_SCHEME_NOT_FOUND;
  public static String MSG_ABSOLUTE_PATH_WITHOUT_SCHEME;
  public static String MSG_URI_NOT_RELATIVE; 
  public static String MSG_ERROR_UNEXPECTED_ERROR; 
  public static String MSG_INVALID_PLATFORM_URL; 
  public static String MSG_INVALID_FILE_URL;
  public static String MSG_ERROR_PATH_NOT_ABSOLUTE; 
  public static String MSG_ERROR_PATH_EMPTY; 
  public static String MSG_ERROR_PATH_NOT_FOLDER; 
  public static String MSG_ERROR_RESOURCE_NOT_FOLDER; 
  public static String MSG_ERROR_RESOURCE_NOT_FILE; 
  public static String MSG_ERROR_FOLDER_HAS_CHILDREN; 
  public static String MSG_ERROR_IO; 
  public static String LABEL_YES; 
  public static String LABEL_YES_TO_ALL; 
  public static String LABEL_CANCEL; 
  
  static
  {
    NLS.initializeMessages( BUNDLE_NAME, Messages.class );
  }
}
