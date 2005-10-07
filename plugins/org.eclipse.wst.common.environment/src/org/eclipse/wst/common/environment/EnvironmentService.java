/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.environment;

import org.eclipse.wst.common.environment.uri.URIScheme;
import org.eclipse.wst.common.internal.environment.eclipse.ConsoleEclipseEnvironment;
import org.eclipse.wst.common.internal.environment.eclipse.EclipseLog;
import org.eclipse.wst.common.internal.environment.eclipse.EclipseScheme;
import org.eclipse.wst.common.internal.environment.uri.file.FileScheme;

public class EnvironmentService
{
  public static Environment getEclipseConsoleEnvironment()
  {
    return new ConsoleEclipseEnvironment(); 
  }  
  
  public static Log getEclipseLog()
  {
    return new EclipseLog();
  }
  
  public static URIScheme getEclipseScheme( Environment environment )
  {
    return new EclipseScheme( environment );
  }  
  
  public static URIScheme getFileScheme()
  {
    return new FileScheme();   
  }
}
