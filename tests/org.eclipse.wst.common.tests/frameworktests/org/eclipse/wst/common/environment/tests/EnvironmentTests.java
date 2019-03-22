/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.environment.tests;



import junit.framework.Test;
import junit.framework.TestSuite;

public class EnvironmentTests extends TestSuite 
{	
	
	public static Test suite() 
  {
		return new EnvironmentTests();
	}

  public EnvironmentTests()
  {
    super();
    
    addTest( EclipseSchemeTests.suite() );
    addTest( EclipseURITests.suite() );
    addTest( FileSchemeTests.suite() );
    addTest( FileURITests.suite() );
    addTest( StatusHandlerTests.suite() );
    addTest( LoggerTests.suite() );
  }
}
