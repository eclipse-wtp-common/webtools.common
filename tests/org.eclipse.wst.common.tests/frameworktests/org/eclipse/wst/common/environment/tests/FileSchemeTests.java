/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.environment.tests;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.common.environment.EnvironmentService;
import org.eclipse.wst.common.environment.uri.IURI;
import org.eclipse.wst.common.environment.uri.IURIScheme;
import org.eclipse.wst.common.environment.uri.URIException;

public class FileSchemeTests extends TestCase
{
  private final String projectName_ = "TestProject";
  
  public FileSchemeTests(String name)
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
    return new TestSuite(FileSchemeTests.class);
  }

  protected static void runAll()
  {
    junit.textui.TestRunner.run(suite());
  }

  public static void runOne(String methodName)
  {
    TestSuite testSuite = new TestSuite();
    TestCase test = new FileSchemeTests(methodName);
    testSuite.addTest(test);
    junit.textui.TestRunner.run(testSuite);
  }
  
  /**
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
    
    // Create a test project in the workbench
    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    IProject       project       = workspaceRoot.getProject( projectName_ );
    
    if( project != null && project.exists() )
    {
      project.delete( true, null );
    }
    
    // Create the project
    IProjectDescription desc = workspaceRoot.getWorkspace().newProjectDescription( projectName_ );
    project.create( desc, null );
    project.open( null );
  }
  /**
   * @see TestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
    
    // Create a test project in the workbench
    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    IProject       project       = workspaceRoot.getProject( projectName_ );
    
    project.delete( true, null );
  }
  
  public static Test getTest()
  {
    return new FileSchemeTests("FileSchemeTests");
  }
  
  public void testBadURIs() 
  {
    IURIScheme scheme  = EnvironmentService.getFileScheme();
    
    try
    {
      IURI newURI = scheme.newURI( "bogus:/somefolder/somefile.txt");
      assertTrue( "Non file protocol should cause an exception", false );
      
      // This code should never run.
      newURI.asFile();
    }
    catch( URIException exc )
    {
    }   
    
    try
    {
      IURI newURI = scheme.newURI( ":/somefolder/somefile.txt");
      assertTrue( "Non file protocol should cause an exception", false );
      
      // This code should never run.
      newURI.asFile();
    }
    catch( URIException exc )
    {
    }       
 
  
    try
    {
      IURI newURI = scheme.newURI(  (String)null );
      assertTrue( "Non file protocol should cause an exception", false );
    
      // This code should never run.
      newURI.asFile();
    }
    catch( URIException exc )
    {
    }   
    
    try
    {
      IURI newURI = scheme.newURI( (URL)null );
      assertTrue( "Non file protocol should cause an exception", false );
    
      // This code should never run.
      newURI.asFile();
    }
    catch( URIException exc )
    {
    }  
    
    try
    {
      IURI newURI = scheme.newURI( (IURI)null );
      assertTrue( "Non file protocol should cause an exception", false );
    
      // This code should never run.
      newURI.asFile();
    }
    catch( URIException exc )
    {
    }       
  } 
  
  public void testNewURI()
  {
    IURIScheme scheme = EnvironmentService.getFileScheme() ;
    
    try
    {      
      IURI uri1 = scheme.newURI( "file:/tmp/myfile" );
      IURI uri2 = scheme.newURI( "relativedirectory/relativefile" );
      IURI uri3 = scheme.newURI( uri1 );
      IURI uri4 = scheme.newURI( uri2 );
      IURI uri5 = scheme.newURI( new URL( uri1.asString() ) );     
      
      File file1 = uri1.asFile();
      
      uri1.touchLeaf();
      
      assertTrue( "Is a file", file1.isFile() );
      assertTrue( "File names not the same", uri1.asString().equals( uri3.toString() ));
      assertTrue( "File names not the same", uri2.asString().equals( uri4.toString() ));
      assertTrue( "File names not the same", uri1.asString().equals( uri5.toString() ));
      assertTrue( "Protocol not file", uri1.getURIScheme().toString().equals("file"));
      assertTrue( "Protocol not file", uri3.getURIScheme().toString().equals("file"));
      assertTrue( "Protocol not file", uri5.getURIScheme().toString().equals("file"));
      assertTrue( "Protocol not relative", uri2.getURIScheme().toString().equals("relative"));
      assertTrue( "Protocol not relative", uri4.getURIScheme().toString().equals("relative"));
      
      uri1.erase();
    }
    catch( URIException exc )
    {
      assertTrue( "Exception throw:" + exc.getMessage(), false );
    }     
    catch( MalformedURLException exc )
    {
      assertTrue( "Exception throw:" + exc.getMessage(), false );      
    }
  }
  
  public void testHierarchical()
  {
    IURIScheme scheme = EnvironmentService.getFileScheme() ;
    
    assertTrue( "Not hierarchical", scheme.isHierarchical() == true );
  }
  
  public void testValidURIs()
  {
    try
    {
      IURIScheme scheme = EnvironmentService.getFileScheme();
      IURI       uri1   = scheme.newURI( "file:/tmp/somedir/somefile" );
      IURI       uri2   = scheme.newURI( "file:/tmp" );
      IURI       uri3   = scheme.newURI( "somerel" );
      IURI       uri4   = scheme.newURI( "somerel/somemorerel" );
      IURI       uri5   = scheme.newURI( "./somerel/somemore" );
    
      assertTrue( "URI not valid", scheme.isValid( uri1 ) );
      assertTrue( "URI not valid", scheme.isValid( uri2 ) );
      assertTrue( "URI not valid", scheme.isValid( uri3 ) );
      assertTrue( "URI not valid", scheme.isValid( uri4 ) );
      assertTrue( "URI not valid", scheme.isValid( uri5 ) );
      assertTrue( "URI has not valid status", scheme.validate( uri1 ).getSeverity() == IStatus.OK );
      assertTrue( "URI has not valid status", scheme.validate( uri2 ).getSeverity() == IStatus.OK );
      assertTrue( "URI has not valid status", scheme.validate( uri3 ).getSeverity() == IStatus.OK );
      assertTrue( "URI has not valid status", scheme.validate( uri4 ).getSeverity() == IStatus.OK );
      assertTrue( "URI has not valid status", scheme.validate( uri5 ).getSeverity() == IStatus.OK );
    }
    catch( URIException exc )
    {
      assertTrue( "Exception throw:" + exc.getMessage(), false );      
    }
  }
  
  public void testInvalidURIs()
  {
    try
    {
      IURIScheme eclipseScheme = EnvironmentService.getEclipseScheme();
      IURIScheme fileScheme    = EnvironmentService.getFileScheme();
      
      IURI       uri1   = eclipseScheme.newURI( "platform:/resource/somedir/somefile" );
    
      assertTrue( "URI valid", !fileScheme.isValid( uri1 ) );
      assertTrue( "URI has a valid status", fileScheme.validate( uri1 ).getSeverity() == IStatus.ERROR );
    }
    catch( URIException exc )
    {
      assertTrue( "Exception throw:" + exc.getMessage(), false );      
    }
  }
}
