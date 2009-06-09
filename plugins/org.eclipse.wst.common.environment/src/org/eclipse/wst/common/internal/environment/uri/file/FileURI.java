/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.internal.environment.uri.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.environment.uri.IURI;
import org.eclipse.wst.common.environment.uri.URIException;
import org.eclipse.wst.common.environment.uri.IURIFilter;
import org.eclipse.wst.common.environment.uri.IURIScheme;
import org.eclipse.wst.common.internal.environment.relative.RelativeURI;


public class FileURI extends RelativeURI 
{
  private File file_ = null;
  
  public FileURI( String uri )
  {
    super( uri );
    
    if( getURIScheme().isValid( this ) )
    {
      file_ = new File( uri.substring( 5, uri.length() ) );
    }
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#asFile()
   */
  @Override
public File asFile() 
  {
    return file_;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#erase()
   */
  @Override
public void erase() throws URIException
  {
    deleteFile( file_ );
  }

  private void deleteFile( File file )
  {
    if( file.isDirectory() )
    {
      File[] children = file.listFiles();
      
      for( int index = 0; index < children.length; index++ )
      {
        File child = children[index];
        
        if( child.isDirectory() )
        {
          deleteFile( child );
        }
        else
        {
          child.delete();
        }
      }
    }
    
    file.delete();
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#getInputStream()
   */
  @Override
public InputStream getInputStream() throws URIException
  {
    FileInputStream stream = null;
    
    try
    {
      File parent = file_.getParentFile();
      parent.mkdirs();
      
      stream = new FileInputStream( file_ );  
    }
    catch( IOException exc )
    {
      throw new URIException( new Status( IStatus.ERROR, "id", 0, exc.getMessage(), exc ), this ); //$NON-NLS-1$
    }
    
    return stream;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#getOutputStream()
   */
  @Override
public OutputStream getOutputStream() throws URIException
  {
    FileOutputStream stream = null;
    
    try
    {
      File parent = file_.getParentFile();
      parent.mkdirs();
      
      stream = new FileOutputStream( file_ );  
    }
    catch( IOException exc )
    {
      throw new URIException( new Status( IStatus.ERROR, "id", 0, exc.getMessage(), exc ), this ); //$NON-NLS-1$
    }
    
    return stream;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#getURIScheme()
   */
  @Override
public IURIScheme getURIScheme()
  {
    return new FileScheme();
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isAvailableAsFile()
   */
  @Override
public boolean isAvailableAsFile()
  {
    return file_ != null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isAvailableAsURL()
   */
  @Override
public boolean isAvailableAsURL()
  {
    return file_ != null;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#asURL()
   */
  @Override
public URL asURL() throws URIException
  {
    URL url = null;
    
    try
    {
      url = file_ == null ? null : file_.toURL();
    }
    catch( MalformedURLException exc )
    {
      throw new URIException( new Status( IStatus.ERROR, "id", 0, exc.getMessage(), exc ), this ); //$NON-NLS-1$      
    }
    
    return url;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isLeaf()
   */
  @Override
public boolean isLeaf()
  {
    return file_.isFile();
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isPresent()
   */
  @Override
public boolean isPresent()
  {
    return file_.exists();
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isReadable()
   */
  @Override
public boolean isReadable()
  {
    return file_.canRead();
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isWritable()
   */
  @Override
public boolean isWritable()
  {
    return file_.canWrite();
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#list()
   */
  @Override
public IURI[] list() throws URIException
  {
    File[] children = file_.listFiles();
    IURI[]  URIs     = new IURI[0];
    
    if( children != null )
    {
      int   length   = children.length;
      
      URIs = new IURI[length];
    
      for( int index = 0; index < length; index++ )
      {
        URIs[index] = new FileURI( "file:" + children[index].getAbsolutePath() ); //$NON-NLS-1$
      }
    }
    
    return URIs;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#list(org.eclipse.env.uri.URIFilter)
   */
  @Override
public IURI[] list(IURIFilter uriFilter) throws URIException
  {
    File[]   children = file_.listFiles();
    int      length   = children == null ? 0 : children.length;
    Vector   URIs     = new Vector();
    
    if(children != null){
	    for( int index = 0; index < length; index++ )
	    {
	      IURI newURI = new FileURI( "file:" + children[index].getAbsolutePath() ); //$NON-NLS-1$
	      
	      if( uriFilter.accepts( newURI) )
	      {
	        URIs.add( newURI );
	      } 
	    }
    }
    return (IURI[])URIs.toArray( new IURI[0] );
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#rename(org.eclipse.env.uri.URI)
   */
  @Override
public void rename(IURI newURI) throws URIException
  {
    try
    {
      uri_ = newURI.toString();
      URL newURL = new URL( uri_ );
      file_.renameTo( new File( newURL.getPath() ) );
    }
    catch( MalformedURLException exc )
    {
      throw new URIException( new Status( IStatus.ERROR, "id", 0, exc.getMessage(), exc ), this ); //$NON-NLS-1$      
    }
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#touchFolder()
   */
  @Override
public void touchFolder() throws URIException
  {
    file_.mkdirs();
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#touchLeaf()
   */
  @Override
public void touchLeaf() throws URIException
  {
    try
    {
      // Ensure that the parent folders are created.
      File parent = file_.getParentFile();
      parent.mkdirs();
      
      file_.createNewFile();
    }
    catch( IOException exc )
    {
      throw new URIException( new Status( IStatus.ERROR, "id", 0, exc.getMessage(), exc ), this ); //$NON-NLS-1$      
    }
  }

  @Override
public boolean isRelative()
  {
    return false;
  }    
}
