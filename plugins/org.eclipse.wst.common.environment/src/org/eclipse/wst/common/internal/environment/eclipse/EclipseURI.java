/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.environment.eclipse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.environment.Environment;
import org.eclipse.wst.common.environment.uri.RelativeURI;
import org.eclipse.wst.common.environment.uri.URI;
import org.eclipse.wst.common.environment.uri.URIException;
import org.eclipse.wst.common.environment.uri.URIFilter;
import org.eclipse.wst.common.environment.uri.URIScheme;


public class EclipseURI extends RelativeURI
{
  private Environment      environment_;
  private EclipseScheme    scheme_;
  private File             file_;
  
  public EclipseURI( String uri, Environment environment )
  {
    super( uri );
    
    environment_  = environment;
    scheme_       = new EclipseScheme( environment );
    file_         = getFile();
  }
    
  /**
   * @see org.eclipse.env.uri.URI#erase()
   */
  public void erase() throws URIException
  {
    try 
    {
      IResource file = getResource();
      
      if( file != null && file.exists() ) 
      {
        if( file instanceof IFile )
        {
          FileResourceUtils.deleteFile( (IFile)file,
                                        environment_.getStatusHandler() );
        }
        else if( file instanceof IFolder )
        {
          FileResourceUtils.deleteFolder( (IFolder)file,
                                          environment_.getStatusHandler() );          
        }
      }
      
    }
    catch( Exception exc ) 
    {      
      throw new URIException( new Status( IStatus.ERROR, "id", 0, exc.getMessage(), exc ), this );
    }
  }

  /**
   * @see org.eclipse.env.uri.URI#getInputStream()
   */
  public InputStream getInputStream() throws URIException
  {
    try 
    {
      // If a class cast exception is throw it will be propogated as
      // a URIException.
      IFile file = (IFile)getResource();

      //call getContents on the eclipse File object
      if( file != null ) 
      {
        return file.getContents();
      }
    }
    catch( Throwable exc ) 
    {
      throw new URIException( new Status( IStatus.ERROR, "id", 0, exc.getMessage(), exc ), this );
    }
    
    return null;
  }

  /**
   * @see org.eclipse.env.uri.URI#getOutputStream()
   */
  public OutputStream getOutputStream() throws URIException
  {
    // Ensure that the parent folder exists.
    URI parent = parent();
    
    if( !parent.isPresent() )
    {
      parent().touchFolder();
    }
    
    return getOutStream();
  }

  /**
   * @see org.eclipse.env.uri.URI#getURIScheme()
   */
  public URIScheme getURIScheme()
  {
    return scheme_;
  }

  /**
   * @see org.eclipse.env.uri.URI#isLeaf()
   */
  public boolean isLeaf()
  {
    boolean result = false;
  
    try
    {
      IResource resource = getResource();
      
      if( resource != null && 
          resource.exists() && 
          resource.getType() == IResource.FILE )
      {  
        result = true;
      }
    }
    catch( URIException exc )
    {
      // This URI does not exist.
      result = false;
    }
   
    return result;
  }

  /**
   * @see org.eclipse.env.uri.URI#isPresent()
   */
  public boolean isPresent()
  {
    boolean result = false;
    
    try
    {
      IResource resource = getResource();
      
      if( resource != null && resource.exists() )
       {  
        result = true;
      }
    }
    catch( URIException exc )
    {
      // This URI does not exist.
      result = false;
    }
    
    return result;
  }

  /**
   * @see org.eclipse.env.uri.URI#isReadable()
   */
  public boolean isReadable()
  {
    boolean result = false;
    
    try
    {
      IResource resource = getResource();
      
      if( resource != null && resource.isAccessible() )
       {  
        result = true;
      }
    }
    catch( URIException exc )
    {
      // This URI does not exist.
      result = false;
    }
    
    return result;
   }

  /**
   * @see org.eclipse.env.uri.URI#isRelative()
   */
  public boolean isRelative()
  { 
    return false;
  }

  /**
   * @see org.eclipse.env.uri.URI#isWritable()
   */
  public boolean isWritable()
  {
    boolean result = false;
    
    try
    {
      IResource resource = getResource();
      
      if( resource != null && 
          resource.isAccessible() && 
          !resource.getResourceAttributes().isReadOnly() )
       {  
        result = true;
      }
    }
    catch( URIException exc )
    {
      // This URI does not exist.
      result = false;
    }
    
    return result;
  }

  /**
   * @see org.eclipse.env.uri.URI#list()
   */
  public URI[] list() throws URIException
  {
    IResource resource    = getResource();
    URI[]     uriChildren = new URI[0];
    
    if( resource.getType() == IResource.FOLDER )
    {
      IFolder     folder   = (IFolder)resource;
      
      try
      {
        IResource[] children    = folder.members();  
  
        uriChildren = new URI[children.length];
        
        for( int index = 0; index < children.length; index++ )
        {
          IPath path = children[index].getFullPath();
          uriChildren[index] = new EclipseURI( scheme_.getURLFromPath(path), environment_  );
        }
      }
      catch( CoreException exc )
      {
      }
    }
    
    return uriChildren;
  }

  /**
   * @see org.eclipse.env.uri.URI#list(org.eclipse.env.uri.URIFilter)
   */
  public URI[] list(URIFilter uriFilter) throws URIException
  {
    IResource resource = getResource();
    URI[]     result   = new URI[0];
    
    if( resource.getType() == IResource.FOLDER )
     {
      IFolder     folder   = (IFolder)resource;
      
      try
      {
        IResource[] children    = folder.members();       
        Vector      uriChildren = new Vector();
        
        for( int index = 0; index < children.length; index++ )
         {
          IPath path = children[index].getFullPath();
          URI   uri  = new EclipseURI( scheme_.getURLFromPath(path), environment_ );
          
          if( uriFilter.accepts( uri ) )
          {
            uriChildren.add( uri );
          }
        }
        
        result = (URI[])uriChildren.toArray( new URI[0] );
      }
      catch( CoreException exc )
      {
      }
    }
    
    return result;
  }
   
  /**
   * @see org.eclipse.env.uri.URI#rename(org.eclipse.env.uri.URI)
   */
  public void rename(URI newURI )
  {
    // TODO Auto-generated method stub

  }

  /**
   * @see org.eclipse.env.uri.URI#touchFolder()
   */
  public void touchFolder() throws URIException
  {
    IResource resource = getResource();
    
    if( resource != null )
    { 
      if( resource.getType() == IResource.FOLDER )
      {
        IFolder folder = (IFolder)resource;
        
        try
        {
          if( folder.members().length > 0 )
          {
            throw new URIException( 
                new Status( IStatus.ERROR, "id", 0, 
                    NLS.bind( Messages.MSG_ERROR_FOLDER_HAS_CHILDREN, folder.toString() ), null ),
                this );
								
          }
        }
        catch( CoreException exc )
        {        
          throw new URIException( new Status( IStatus.ERROR, "id", 0, exc.getMessage(), exc ), this );
        }
      }
      else
      {
         //??? Not sure what to do if touching a folder and the URI exists and it is not a folder.
      }
    }
    else
    {
      IPath newPath = new Path( scheme_.getPathFromPlatformURI( uri_ ) ).makeAbsolute();
      
      try
      {
        FileResourceUtils.makeFolderPath( newPath,
			  	                                environment_.getStatusHandler() );
      }
      catch( CoreException exc )
      {
        throw new URIException( new Status( IStatus.ERROR, "id", 0, exc.getMessage(), exc ), this );
      }
    }
  }

  /**
   * @see org.eclipse.env.uri.URI#touchLeaf()
   */
  public void touchLeaf() throws URIException
  {
    IResource resource = getResource();
    
    if( resource != null )
    {
      // The resource already exists so do nothing.
    }    
    else
    {
      // Get the parent for this leaf and create it if required.
      URI parent = parent();
      
      if( !parent.isPresent() )
      {  
        parent().touchFolder();
      }
      
      try
      {
        // Get an output stream to the specified file and immediately close it.
        // This should create a 0 byte file.
        getOutStream().close();
      }
      catch( IOException exc )
      {
        throw new URIException( new Status( IStatus.ERROR, "id", 0, exc.getMessage(), exc ), this );
      }
    }

  }

  private IResource getResource() throws URIException
  {    
    IPath  path             = new Path( scheme_.getPathFromPlatformURI(uri_) );
    String absolutePathname = path.makeAbsolute().toString();
   
    return FileResourceUtils.findResource(absolutePathname);
  }
  
  /**
   * @see org.eclipse.env.uri.URI#getOutputStream()
   */
  private OutputStream getOutStream() throws URIException
  {
    IPath        file   = new Path( scheme_.getPathFromPlatformURI( uri_ ) ).makeAbsolute();
    OutputStream stream = null;
    
    stream = FileResourceUtils.newFileOutputStream ( file,
 			                                               environment_.getStatusHandler() );    
    return stream;
  }
  
  /**
   * Returns a File object for the resource under this URI.
   * There are many URIs and URISchemes for which this method
   * will fail and throw an exception. It should be used only
   * in cases where URIs are known to be backed by physical files.
   */
  public File asFile ()
  {
  	return file_;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.wst.command.internal.provisional.env.core.uri.URI#isAvailableAsFile()
   */
  public boolean isAvailableAsFile() 
  {
  	return file_ != null;
  }
  
  private File getFile()
  {
    String platformRes = "platform:/resource";
    File   result      = null;
    
    if (uri_.startsWith(platformRes))
    {
      result = new File(ResourcesPlugin.getWorkspace().getRoot().getLocation().removeTrailingSeparator().append(uri_.substring(platformRes.length(), uri_.length())).toString());
    }
    
    return result;
  }
}
