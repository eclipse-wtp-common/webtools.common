/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.environment.tests;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.environment.Choice;
import org.eclipse.wst.common.environment.EnvironmentService;
import org.eclipse.wst.common.environment.IEnvironment;
import org.eclipse.wst.common.environment.IStatusHandler;
import org.eclipse.wst.common.environment.StatusException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class StatusHandlerTests extends TestCase
{
  private IStatus error;
  private IStatus info;
  private IStatus warning;
  
  public StatusHandlerTests(String name)
  {
    super(name);
    
    error   = new Status( IStatus.ERROR, "id", 0, "error message", null );
    info    = new Status( IStatus.INFO, "id", 0, "info message", null );
    warning = new Status( IStatus.WARNING, "id", 0, "warning message", null );
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
    return new TestSuite(StatusHandlerTests.class);
  }

  protected static void runAll()
  {
    junit.textui.TestRunner.run(suite());
  }

  public static void runOne(String methodName)
  {
    TestSuite testSuite = new TestSuite();
    TestCase test = new StatusHandlerTests(methodName);
    System.out.println("Calling StatusHandlerTests."+methodName);
    testSuite.addTest(test);
    junit.textui.TestRunner.run(testSuite);
  }
  
  public void testEnvironmentStatusHandler()
  {
    IEnvironment   environment = EnvironmentService.getEclipseConsoleEnvironment();
    IStatusHandler handler     = environment.getStatusHandler();
    
    try
    {
      Choice choice1 = new Choice();
      Choice choice2 = new Choice( 'o', "Ok", "description" );
      
      StatusException someException = new StatusException( error );
      
      assertTrue( "Status not the same ", someException.getStatus() == error );
      
      handler.report( Status.OK_STATUS );
      handler.report( error );
      handler.report( warning );
      handler.report( info );
      choice1.setShortcut( 'c' );
      choice1.setLabel( "cancel" );
      choice1.setDescription( "cancel description" );
      
      Choice result = handler.report( error, null );
      
      assertTrue( "result not null ", result == null );
      
      result = handler.report( error, new Choice[0] );
      assertTrue( "result not null ", result == null );
      
      result = handler.report( error, new Choice[]{ choice2, choice1 } );
      assertTrue( "First choice shortcut not the same", result.getShortcut() == choice2.getShortcut());
      assertTrue( "First choice label not the same", result.getLabel().equals( choice2.getLabel()));
      assertTrue( "First choice description not the same", result.getDescription().equals( choice2.getDescription()));
      
      handler.reportError( error );
      handler.reportInfo( info );
      
    }
    catch( StatusException exc )
    {
      assertTrue( "unexpected exception:", false );
    }
  }
}
