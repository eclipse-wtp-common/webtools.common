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
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.environment.EnvironmentService;
import org.eclipse.wst.common.environment.IEnvironment;
import org.eclipse.wst.common.environment.ILog;
import org.eclipse.wst.common.environment.StatusException;

public class LoggerTests extends TestCase
{
  public LoggerTests(String name)
  {
    super(name);
  }
  
  public static void main(String[] args)
  {
    if (args.length == 0)
    {
      runAll();
    }
    else if (args.length == 1)
    {
      String methodToRun = args[0].trim();
      runOne(methodToRun);
    }
  }

  public static Test suite()
  {
    return new TestSuite(LoggerTests.class);
  }

  protected static void runAll()
  {
    junit.textui.TestRunner.run(suite());
  }

  public static void runOne(String methodName)
  {
    TestSuite testSuite = new TestSuite();
    TestCase test = new LoggerTests(methodName);
    System.out.println("Calling LoggerTests."+methodName);
    testSuite.addTest(test);
    junit.textui.TestRunner.run(testSuite);
  }
  
  public void testLogger()
  {
    IEnvironment environment = EnvironmentService.getEclipseConsoleEnvironment();
    ILog         logger      = environment.getLog();
    
	//assertTrue("Logging enabled", !logger.isEnabled());
	// We may or may not be called with the -debug option, 
	// so we can not test for it, but we can write an appropriate 
	// message, to help interpret results, if needed.
	if (logger.isEnabled()) {
		System.out.println(" Logging is enabled");
	} else {
		System.out.println(" Logging is is not enabled");			
	}
    assertTrue( "Logging feature enabled", !logger.isEnabled( "bad option" ) );
    
    logger.log( ILog.ERROR, 0, this, "test logger", Status.CANCEL_STATUS );
    logger.log( ILog.INFO, 1, this, "another method", "object" );
    logger.log( ILog.WARNING, 3, this, "one more method", new StatusException( Status.OK_STATUS ));
    logger.log( ILog.ERROR, "option1", 0, this, "test logger", Status.CANCEL_STATUS );
    logger.log( ILog.INFO, "option2", 1, this, "another method", "object" );
    logger.log( ILog.WARNING, "option3", 3, this, "one more method", new StatusException( Status.OK_STATUS ));
  }
}
