/***************************************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.environment.tests;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.environment.EnvironmentService;
import org.eclipse.wst.common.environment.IEnvironment;
import org.eclipse.wst.common.environment.uri.IURI;
import org.eclipse.wst.common.environment.uri.IURIFactory;
import org.eclipse.wst.common.environment.uri.IURIFilter;
import org.eclipse.wst.common.environment.uri.IURIScheme;
import org.eclipse.wst.common.environment.uri.IURIVisitor;
import org.eclipse.wst.common.environment.uri.URIException;

public class EclipseURITests extends TestCase
{
  private final String projectName_ = "TestProject";
  
  public EclipseURITests(String name)
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
    return new TestSuite(EclipseURITests.class);
  }

  protected static void runAll()
  {
    junit.textui.TestRunner.run(suite());
  }

  public static void runOne(String methodName)
  {
    TestSuite testSuite = new TestSuite();
    TestCase test = new EclipseURITests(methodName);
    System.out.println("Calling EclipseURITests."+methodName);
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
    return new EclipseURITests("EclipseURITests");
  }
  public void testAvailable() 
  {
    IEnvironment environment = EnvironmentService.getEclipseConsoleEnvironment();
    IURIFactory  factory     = environment.getURIFactory();
    
    System.out.println("EclipseURITests.testAvailable()");
    
    try
    {      
      IURI uri1 = factory.newURI( "platform:/resource/" + projectName_ +  "/myfile" );
      IURI uri2 = factory.newURI( "file:/tmp/somedirectory/somefile" );
      IURI uri3 = factory.newURI( "relativedirectory/relativefile" );
      
      assertTrue( "Not available as URL", uri1.isAvailableAsURL() );
      assertTrue( "Not available as URL", uri2.isAvailableAsURL() );
      assertTrue( "Available as URL", !uri3.isAvailableAsURL() );
      
      assertTrue( "Not available as File", uri1.isAvailableAsFile() );
      assertTrue( "Not available as File", uri2.isAvailableAsFile() );
      assertTrue( "Available as File", !uri3.isAvailableAsFile() );
      
      File file1 = uri1.asFile();
      File file2 = uri2.asFile();
      URL  url1  = uri1.asURL();
      URL  url2  = uri2.asURL();
      
      uri1.touchLeaf();
      uri2.touchLeaf();
      
      assertTrue( "Is a file", file1.isFile() );
      assertTrue( "Is a file", file2.isFile() );
      assertTrue( "Wrong protocol", url1.getProtocol().equals( "platform" ));
      assertTrue( "Wrong protocol", url2.getProtocol().equals( "file" ));
      assertTrue( uri1.asString().length() > 5 );
      assertTrue( uri2.asString().length() > 5 );
      assertTrue( uri1.toString().length() > 5 );
      assertTrue( uri2.toString().length() > 5 );
      uri1.erase();
      uri2.erase();
    }
    catch( URIException exc )
    {
      assertTrue( "Exception throw:" + exc.getMessage(), false );
    }
  }      
      
  public void testFactory() 
  {
    IEnvironment environment = EnvironmentService.getEclipseConsoleEnvironment();
    IURIFactory  factory     = environment.getURIFactory();
    
    System.out.println("EclipseURITests.testFactory()");
    try
    {
      factory.newURIScheme( null );
      assertTrue( "Null URI scheme should cause an exception", false );
    }
    catch( URIException exc )
    {
    }
    
    try
    {
      factory.newURIScheme( "bogusProtocol:/somefolder/somefile.txt" );
      assertTrue( "An undefined protocol should cause an exception", false );
    }
    catch( URIException exc )
    {
    }
    
    try
    {
      factory.newURIScheme( "/somefolder/somefile.txt" );
      assertTrue( "Absolute path with no protocol should cause an exception", false );
    }
    catch( URIException exc )
    {
    }
    
    try
    {
      IURIScheme scheme1 = factory.newURIScheme( "platform:/resource/myproj/myfile" );
      IURIScheme scheme2 = factory.newURIScheme( "file:/tmp/somedirectory/somefile" );
      IURIScheme scheme3 = factory.newURIScheme( "platform" );
      IURIScheme scheme4 = factory.newURIScheme( "file" );
      IURIScheme scheme5 = factory.newURI( "rel1/rel2" ).getURIScheme();
      
      IURI uri1 = factory.newURI( "platform:/resource/myproj/myfile" );
      IURI uri2 = factory.newURI( "file:/somedirectory/somefile" );
      IURI uri3 = factory.newURI( "relativedirectory/relativefile" );
      
      IURI uri4 = factory.newURI( new URL( "platform:/resource/myproj/myfile" ));
      IURI uri5 = factory.newURI( new URL( "file:/tmp/somedirectory/somefile" ) );
  
      
      assertTrue( "Scheme not platform protocol", scheme1.toString().equals("platform") );
      assertTrue( "Scheme not file protocol", scheme2.toString().equals("file") );
      assertTrue( "Scheme not platform", scheme3.toString().equals("platform") );
      assertTrue( "Scheme not file", scheme4.toString().equals("file") );
      assertTrue( "Scheme not relative", scheme5.toString().equals("relative") );
      
      assertTrue( "Scheme not platform protocol", uri1.getURIScheme().toString().equals("platform") );
      assertTrue( "Scheme not file protocol", uri2.getURIScheme().toString().equals("file") );
      assertTrue( "Scheme not relative", uri3.getURIScheme().toString().equals("relative") );
      
      assertTrue( "Scheme not platform protocol", uri4.getURIScheme().toString().equals("platform") );
      assertTrue( "Scheme not file protocol", uri5.getURIScheme().toString().equals("file") );
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
  
  public void testTouchFolder() 
  {
    IEnvironment environment = EnvironmentService.getEclipseConsoleEnvironment();
    IURIFactory  factory     = environment.getURIFactory();
    
    System.out.println("EclipseURITests.testTouchFolder()");
    try
    {
      IPath folderPath  = new Path( projectName_ ).append( "topFolder" ).makeAbsolute();
      IPath folderPath2 = folderPath.append( "space folder" );
      IURI   uri          = factory.newURI( getURLFromPath( folderPath ).toString() );
      IURI   uri2         = factory.newURI( getURLFromPath( folderPath2 ).toString() );
      
      assertTrue( "topFolder should not exist", !uri.isPresent() );
      
      uri.touchFolder();
   
      assertTrue( "topFolder should exist", uri.isPresent() );
      
      IPath nestedPath = folderPath.append( "level1" ).append( "level2" );
      IURI   nesteduri  = factory.newURI( getURLFromPath( nestedPath ).toString() );
      
      assertTrue( "nestedFolder should not exist", !nesteduri.isPresent() );
      
      nesteduri.touchFolder();
      
      assertTrue( "nestedFolder should exist", nesteduri.isPresent() );
      
      IURI child1 = uri2.append( factory.newURI( "child1" ) );
      IURI child2 = uri2.append( factory.newURI( "child2" ) );
      
      child1.touchLeaf();
      child2.touchLeaf();
      
      assertTrue ( "Child 1 in blank folder should exist ", child1.isPresent() );
      assertTrue ( "Child 2 in blank folder should exist ", child2.isPresent() );
     }
    catch( URIException exc )
    {
      assertTrue( "Exception throw:" + exc.getMessage(), false );
    }
  }
  
  public void testList() 
  {
    IEnvironment  environment = EnvironmentService.getEclipseConsoleEnvironment();
    IURIFactory   factory     = environment.getURIFactory();
    
    System.out.println("EclipseURITests.testList()");
    try
    {
      IPath folderPath0 = new Path( projectName_ ).append( "topFolder2" ).makeAbsolute();
      IPath folderPath1 = new Path( projectName_ ).append( "topFolder2" ).append( "leaf1" ).makeAbsolute();
      IPath folderPath2 = new Path( projectName_ ).append( "topFolder2" ).append( "leaf2" ).makeAbsolute();
      IPath folderPath3 = new Path( projectName_ ).append( "topFolder2" ).append( "leaf3" ).makeAbsolute();
      IURI   uri0        = factory.newURI( getURLFromPath( folderPath0 ).toString() );
      IURI   uri1        = factory.newURI( getURLFromPath( folderPath1 ).toString() );
      IURI   uri2        = factory.newURI( getURLFromPath( folderPath2 ).toString() );
      IURI   uri3        = factory.newURI( getURLFromPath( folderPath3 ).toString() );
      
      uri1.touchFolder();
      uri2.touchFolder();
      uri3.touchFolder();
      
      IURI[] babies = uri0.list();
          
      assertTrue( "folder path should have 3 children", babies.length == 3 );
      
      for( int index = 0; index < babies.length; index++ )
      {
        String  folderName = babies[index].toString();
        String  leafName   = folderName.substring( folderName.length() - 5, folderName.length() );
        boolean Ok         = leafName.equals( "leaf1") || leafName.equals( "leaf2" ) || leafName.equals( "leaf3" );
        assertTrue( "Wrong leaf baby uri:" + folderName + "," + leafName, Ok );
      }
      
      IURI[] babies2 = uri0.list( new IURIFilter()
                                 {
                                   public boolean accepts( IURI uri )
                                   {
                                     return !uri.toString().endsWith( "leaf2");
                                   }
                                 } );
      
      assertTrue( "folder path should have 2 filtered children", babies2.length == 2 );
      
      for( int index = 0; index < babies2.length; index++ )
       {
        String  folderName = babies2[index].toString();
        String  leafName   = folderName.substring( folderName.length() - 5, folderName.length() );
        boolean Ok         = leafName.equals( "leaf1") || leafName.equals( "leaf3" );
        
        assertTrue( "Wrong leaf baby uri:" + folderName + "," + leafName, Ok );
      }
    }
    catch( URIException exc )
    {
      assertTrue( "Exception throw:" + exc.getMessage(), false );
    }
  }
  
  public void testGetURIScheme()
  {
    IEnvironment environment = EnvironmentService.getEclipseConsoleEnvironment();
    IURIFactory  factory     = environment.getURIFactory();
   
    try
    { 
      IPath      folderPath0  = new Path( projectName_ ).append( "testGetScheme" ).makeAbsolute();
      IURI       uri0         = factory.newURI( getURLFromPath( folderPath0 ).toString() );
      IURIScheme scheme       = uri0.getURIScheme();
      
      assertTrue( "Scheme name is not platform", scheme.toString().equals( "platform" ) );
    }
    catch( URIException exc )
    {
      assertTrue( "Unexpected exception", false );
    }
  }
  
  public void testAppend() 
  {
    IEnvironment environment = EnvironmentService.getEclipseConsoleEnvironment();
    IURIFactory  factory     = environment.getURIFactory();
    
    System.out.println("EclipseURITests.testAppend()");
    try
    {
      IPath folderPath0  = new Path( projectName_ ).append( "topFolder3" ).makeAbsolute();
      IURI   uri0         = factory.newURI( getURLFromPath( folderPath0 ).toString() );
      IURI   uriRelative1 = factory.newURI( "level1");
      IURI   uriRelative2 = factory.newURI( "level2");
      
      IURI   newURI       = uri0.append( uriRelative1 ).append( uriRelative2 );
      
      assertTrue( "newURI should not exist", !newURI.isPresent() );
      
      newURI.touchFolder();
      
      assertTrue( "newURI should exist", newURI.isPresent() );
      assertTrue( "newURI should not be a leaf", !newURI.isLeaf() );
      
      // Ensure that append parameter is relative.
      try
      {
        IURI newURI2 = uri0.append( uri0 );  
        assertTrue( "Appending using a non-relative should throw and exception", true );
        
        // This code should never run.
        newURI2.asFile(); 
      }
      catch( URIException exc )
      {
      }
      
    }
    catch( URIException exc )
    {   
      assertTrue( "Exception throw:" + exc.getMessage(), false );
    }
  }
  
  public void testTouchLeaf()
  {
    IEnvironment environment = EnvironmentService.getEclipseConsoleEnvironment();
    IURIFactory  factory     = environment.getURIFactory();
    
    System.out.println("EclipseURITests.testTouchLeaf()");
    try
    {
      IPath folderPath0  = new Path( projectName_ ).append( "topFolder4" ).makeAbsolute();
      IURI   uri0         = factory.newURI( getURLFromPath( folderPath0 ).toString() );
      IURI   uriRelative1 = factory.newURI( "level1");
      IURI   uriRelative2 = factory.newURI( "leafFile.txt");
      IURI   newURI       = uri0.append( uriRelative1 ).append( uriRelative2 );
      
      assertTrue( "newURI should not exist", !newURI.isPresent() );
      
      newURI.touchLeaf();
      
      assertTrue( "newURI should exist", newURI.isPresent() );
      assertTrue( "newURI should be a leaf", newURI.isLeaf() );
      
      InputStream stream   = newURI.getInputStream();
      int         byteRead = 0;
      
      try
      {
        byteRead = stream.read(); // Returns a -1 if no bytes are read, which should be the case here. 
      }
      catch( IOException exc )
      {
        assertTrue( "Exception throw:" + exc.getMessage(), false );
      }
      finally
      {
        try
        {
          stream.close();
        }
        catch( IOException exc )
        {
          assertTrue( "Exception throw:" + exc.getMessage(), false );
        }
      }
      
      assertTrue( "There should be no bytes in this stream",  byteRead == -1 );
      
     }
    catch( URIException exc )
    {
      assertTrue( "Exception throw:" + exc.getMessage(), false );
    }     
  }
  
  public void testIOOperations()
  {
    IEnvironment environment = EnvironmentService.getEclipseConsoleEnvironment();
    IURIFactory  factory     = environment.getURIFactory();
    OutputStreamWriter       stream      = null;
    PrintWriter              writer      = null;
    InputStreamReader        inputStream = null;
    BufferedReader           reader      = null;
    
    System.out.println("EclipseURITests.testIOOperations()");
    try
    {
      IPath folderPath0  = new Path( projectName_ ).append( "topFolder5" ).makeAbsolute();
      IURI   uri0         = factory.newURI( getURLFromPath( folderPath0 ).toString() );
      IURI   uriRelative1 = factory.newURI( "level1");
      IURI   uriRelative2 = factory.newURI( "leafFile.txt");
      IURI   newURI       = uri0.append( uriRelative1 ).append( uriRelative2 );
      
      assertTrue( "newURI should not exist", !newURI.isPresent() );
      assertTrue( "newURI should not be readable", !newURI.isReadable() );
      assertTrue( "newURI should be writable", !newURI.isWritable() );
      
      stream = new OutputStreamWriter( newURI.getOutputStream() );
      writer = new PrintWriter( stream );
      
      writer.println( "This is line 1 of the file." ); 
      writer.println( "This is line 2 of the file." );
      writer.println( "This is the end of the file." );
      
      writer.close();
      stream.close();
      writer = null;
      stream = null;
     
      assertTrue( "newURI should be readable", newURI.isReadable() );
      assertTrue( "newURI should be writable", newURI.isWritable() );
      
      // Now read back this new file.
      inputStream = new InputStreamReader( newURI.getInputStream() );
      reader      = new BufferedReader( inputStream );
      
      assertTrue( "Bad first line of file", reader.readLine().equals("This is line 1 of the file.") ); 
      assertTrue( "Bad second line of file", reader.readLine().equals("This is line 2 of the file.") ); 
      assertTrue( "Bad last line of file", reader.readLine().equals("This is the end of the file.") );
      assertTrue( "Extra lines found in file", reader.readLine() == null );
      
      reader.close();
      
      //Now rename the folder.
      IURI level1   = uri0.append( uriRelative1 );
      IURI newLevel = uri0.append( factory.newURI("newLevel1") );
      IURI oldFile  = newLevel.append( uriRelative2 );
      IURI newFile  = newLevel.append( factory.newURI( "newFile" ) );
      
      assertTrue( "URI is not present", newURI.isPresent() );
      assertTrue( "URI is present", !newLevel.append(uriRelative2).isPresent() );
      
      level1.rename( newLevel );
      
      assertTrue( "URI is present", !newURI.isPresent() );
      assertTrue( "URI is not present", newLevel.append(uriRelative2).isPresent() );
      
      assertTrue( "URI is present", !newFile.isPresent() );
      assertTrue( "URI is not present", oldFile.isPresent() );
      
      oldFile.rename( newFile );
           
      assertTrue( "URI is not present", newFile.isPresent() );
      assertTrue( "URI is present", !oldFile.isPresent() );      
    }
    catch( URIException exc )
    {
      assertTrue( "Exception throw:" + exc.getMessage(), false );
    }
    catch( IOException exc )
    {
      assertTrue( "Exception throw:" + exc.getMessage(), false );
    }
    finally
    {
      if( stream != null )
      {
        try
        {
          writer.close();
          stream.close();
        }
        catch( IOException exc )
        {
          assertTrue( "Exception throw:" + exc.getMessage(), false );
        }
      }
      
      if( inputStream != null )
      {
        try
        {
          inputStream.close();
          reader.close();
        }
        catch( IOException exc )
        {
          assertTrue( "Exception throw:" + exc.getMessage(), false );
        }
      }
    }
    
  }
  
  public void testRelative()
  {
    IEnvironment environment = EnvironmentService.getEclipseConsoleEnvironment();
    IURIFactory  factory     = environment.getURIFactory();
    
    System.out.println("EclipseURITests.testRelative()");
    
    try
    {
      IPath folderPath0   = new Path( projectName_ ).append( "relativeProj" ).makeAbsolute();
      IURI   uri0         = factory.newURI( getURLFromPath( folderPath0 ).toString() );
      IURI   uriRelative1 = factory.newURI( "level1");
      IURI   level1URI    = uri0.append( uriRelative1 );
      
      assertTrue( "Uri is relative", !uri0.isRelative() );
      assertTrue( "Uri is not relative", uriRelative1.isRelative() );
      assertTrue( "Uri is relative", !level1URI.isRelative() );
      
      assertTrue( "Uri is not hierarchical", uri0.isHierarchical() );
      assertTrue( "Uri is not hierarchical", uriRelative1.isHierarchical() );
      assertTrue( "Uri is not hierarchical", level1URI.isHierarchical() ); 
    }
    catch( URIException exc)
    {
      assertTrue( "Exception throw:" + exc.getMessage(), false );
    }
  }
  
  public void testErase1()
  {
    IEnvironment environment = EnvironmentService.getEclipseConsoleEnvironment();
    IURIFactory  factory     = environment.getURIFactory();
    
    System.out.println("EclipseURITests.testErase1()");
    try
    {
      IPath folderPath0  = new Path( projectName_ ).append( "topFolder6" ).makeAbsolute();
      IURI   uri0         = factory.newURI( getURLFromPath( folderPath0 ).toString() );
      IURI   uriRelative1 = factory.newURI( "level1");
      IURI   uriRelative2 = factory.newURI( "leafFile.txt");
      IURI   newURI       = uri0.append( uriRelative1 ).append( uriRelative2 );
      IURI   level1URI    = uri0.append( uriRelative1 );
         
      assertTrue( "newURI should not exist", !newURI.isPresent() );
      
      newURI.touchLeaf();
      
      assertTrue( "newURI should exist", newURI.isPresent() );
      
      newURI.erase();
      
      assertTrue( "newURI should not exist", !newURI.isPresent() );
      
      assertTrue( "folder root should exist", uri0.isPresent() );
      assertTrue( "folder level1 should exist", level1URI.isPresent() );
      
      uri0.erase();
      
      assertTrue( "folder root should not exist", !uri0.isPresent() );
      assertTrue( "folder level1 should not exist", !level1URI.isPresent() );
      
    }
    catch( URIException exc)
    {
      assertTrue( "Exception throw:" + exc.getMessage(), false );
    }
  }
  
  
  public void testIsLeaf()
  {
    IEnvironment environment = EnvironmentService.getEclipseConsoleEnvironment();
    IURIFactory  factory     = environment.getURIFactory();
    
    System.out.println("EclipseURITests.testIsLeaf()");
    try
    {
      IPath folderPath0  = new Path( projectName_ ).append( "topFolder7" ).makeAbsolute();
      IURI   uri0         = factory.newURI( getURLFromPath( folderPath0 ).toString() );
      IURI   uriRelative1 = factory.newURI( "level1");
      IURI   uriRelative2 = factory.newURI( "leafFile.txt");
      IURI   newURI       = uri0.append( uriRelative1 ).append( uriRelative2 );
      IURI   parent       = newURI.parent();
      
      assertTrue( "newURI should not exist", !newURI.isPresent() );
      assertTrue( "parent should not exist", !parent.isPresent() );
      assertTrue( "newURI should not be a leaf yet", !newURI.isLeaf() );
      assertTrue( "parent should not be a leaf", !parent.isLeaf() );
      
      newURI.touchLeaf();
      
      assertTrue( "newURI should exist", newURI.isPresent() );
      assertTrue( "parent should exist", parent.isPresent() );
      assertTrue( "newURI should be a leaf", newURI.isLeaf() );
      assertTrue( "parent should not be a leaf", !parent.isLeaf() );
     }
    catch( URIException exc)
    {
      assertTrue( "Exception throw:" + exc.getMessage(), false );
    }
  }
  
  public void testVisit()
  {
    IEnvironment environment = EnvironmentService.getEclipseConsoleEnvironment();
    IURIFactory  factory     = environment.getURIFactory();
    IURIScheme   scheme      = EnvironmentService.getEclipseScheme();
    
    System.out.println("EclipseURITests.testVisit()");
    try
    {
      IPath folderPath0  = new Path( projectName_ ).append( "root" ).makeAbsolute();
      IURI   uri0         = factory.newURI( getURLFromPath( folderPath0 ).toString() );
      
      Hashtable table = buildTestEntries( scheme, uri0 );
      
      uri0.visit( new TestVisitor( table ) );      
      verifyTable( table );
      
      resetTableForTest2( table );
      uri0.visit( new TestVisitor( table ) );
      verifyTable( table );
      
      resetTable( table );
      uri0.visit( new TestVisitor( table ), new TestFilter( table ) );
      verifyTable( table );
      
      resetTableForTest3( table );
      uri0.visit( new TestVisitor( table ), new TestFilter( table ) );
      verifyTable( table );     
    }
    catch( URIException exc)
    {
      assertTrue( "Exception throw:" + exc.getMessage(), false );
    }
  }
 
  private class TestVisitor implements IURIVisitor
  {
    private Hashtable table_;
    
    public TestVisitor( Hashtable table)
    {
      table_ = table;  
    }
    
    public boolean visit( IURI uri )
    {
      NodeEntry entry = (NodeEntry)table_.get( getName( uri ) );
      entry.wasVisited_ = true;
      return !entry.stopTraversing_;
    } 
  }
  
  private class TestFilter implements IURIFilter
  {
    private Hashtable table_;
    
    public TestFilter( Hashtable table)
    {
      table_ = table;  
    }
    
    public boolean accepts( IURI uri )
    {
      NodeEntry entry = (NodeEntry)table_.get( getName( uri ) );
      return entry.visitNode_;
    } 
  }
  
  private Hashtable buildTestEntries( IURIScheme scheme, IURI rootURI )
  {
    Hashtable table = new Hashtable();
     
    try
    {
      IURI rootc1 = rootURI.append( scheme.newURI( "rootc1") );
      IURI rootc2 = rootURI.append( scheme.newURI( "rootc2") );
      IURI rootc3 = rootURI.append( scheme.newURI( "rootc3") );
      
      IURI rootc2c1 = rootc2.append( scheme.newURI( "rootc2c1") );
      IURI rootc2c2 = rootc2.append( scheme.newURI( "rootc2c2") );
      IURI rootc2c3 = rootc2.append( scheme.newURI( "rootc2c3") );
      
      IURI rootc2c2c1 = rootc2c2.append( scheme.newURI( "rootc2c2c1") );
      IURI rootc2c2c2 = rootc2c2.append( scheme.newURI( "rootc2c2c2") );
      
      IURI rootc3c1 = rootc3.append( scheme.newURI( "rootc3c1") );
      IURI rootc3c2 = rootc3.append( scheme.newURI( "rootc3c2") );
      
      // Now that the URI's are created we need to create physical folders
      // and files to represent them.
      rootc3c1.touchLeaf();
      rootc3c2.touchLeaf();    
      
      rootc2c2c1.touchLeaf();
      rootc2c2c2.touchLeaf();
      
      rootc2c1.touchLeaf();
      rootc2c3.touchLeaf();
      
      rootc1.touchFolder();
      
      // Now create the the table entries that will be used to visit
      // the URI nodes.
      table.put( "root", new NodeEntry() );
      table.put( "rootc1", new NodeEntry() );
      table.put( "rootc2", new NodeEntry() );
      table.put( "rootc3", new NodeEntry() );
      table.put( "rootc2c1", new NodeEntry() );
      table.put( "rootc2c2", new NodeEntry() );
      table.put( "rootc2c3", new NodeEntry() );
      table.put( "rootc2c2c1", new NodeEntry() );
      table.put( "rootc2c2c2", new NodeEntry() );
      table.put( "rootc3c1", new NodeEntry() );
      table.put( "rootc3c2", new NodeEntry() );
    }
    catch( URIException exc)
    {
      assertTrue( "Exception throw:" + exc.getMessage(), false );
    }  
      
    return table;
  }
  
  private String getName( IURI uri )
  { 
    String    fullName  = uri.toString();
    int       slash     = fullName.lastIndexOf( '/' );
    String    name      = fullName.substring( slash + 1, fullName.length() );
    
    return name;
  }
  
  private void verifyTable( Hashtable table )
  {
    Iterator entries = table.entrySet().iterator();
    
    while( entries.hasNext() )
    {
      Map.Entry entry = (Map.Entry)entries.next();
      
      String    key  = (String)entry.getKey();
      NodeEntry node = (NodeEntry)entry.getValue(); 
 
      assertTrue( "Bad table result for key:" + key + " visited=" + node.wasVisited_
                                                    + " shouldbe=" + node.shouldBeVisited_,
                  node.wasVisited_ == node.shouldBeVisited_ );
    }
  }
  
  private void resetTableForTest2( Hashtable table )
  {
    resetTable( table );
    
    NodeEntry rootc2 = (NodeEntry)table.get( "rootc2" );
    rootc2.stopTraversing_ = true;
    
    NodeEntry rootc2c1 = (NodeEntry)table.get( "rootc2c1" );
    rootc2c1.shouldBeVisited_ = false;
    
    NodeEntry rootc2c2 = (NodeEntry)table.get( "rootc2c2" );
    rootc2c2.shouldBeVisited_ = false;
    
    NodeEntry rootc2c3 = (NodeEntry)table.get( "rootc2c3" );
    rootc2c3.shouldBeVisited_ = false;
    
    NodeEntry rootc2c2c1 = (NodeEntry)table.get( "rootc2c2c1" );
    rootc2c2c1.shouldBeVisited_ = false;
    
    NodeEntry rootc2c2c2 = (NodeEntry)table.get( "rootc2c2c2" );
    rootc2c2c2.shouldBeVisited_ = false;   
  }
  
  private void resetTableForTest3( Hashtable table )
  {
    resetTable( table );  
    
    NodeEntry rootc3 = (NodeEntry)table.get( "rootc3" );
    rootc3.visitNode_ = false;
    rootc3.shouldBeVisited_ = false;
    
    NodeEntry rootc2c2 = (NodeEntry)table.get( "rootc2c2" );
    rootc2c2.visitNode_ = false;
    rootc2c2.shouldBeVisited_ = false;
    
    NodeEntry rootc3c1 = (NodeEntry)table.get( "rootc3c1" );
    rootc3c1.visitNode_ = false;
    rootc3c1.shouldBeVisited_ = false;
  }
  
  private void resetTable( Hashtable table )
  {
    Iterator entries = table.values().iterator();
    
    while( entries.hasNext() )
    {
      NodeEntry entry = (NodeEntry)entries.next();
      entry.wasVisited_ = false;
      entry.shouldBeVisited_=true;
      entry.stopTraversing_=false;
      entry.visitNode_=true;
    }
  }
  
  private String getURLFromPath( IPath absolutePath )
  {
    return "platform:/resource" + absolutePath;
  }
  
  private class NodeEntry
  {
    public NodeEntry()
    {
      this( true, false, true ); 
    }
    
    public NodeEntry( boolean visitNode, boolean stop, boolean shouldBeVisited )
    {
      visitNode_       = visitNode;
      stopTraversing_  = stop;
      shouldBeVisited_ = shouldBeVisited;
      wasVisited_      = false;
    }
    
    public boolean visitNode_;
    public boolean stopTraversing_;
    
    public boolean wasVisited_;
    public boolean shouldBeVisited_;
  }
}
