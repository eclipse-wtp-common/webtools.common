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
package org.eclipse.wst.common.internal.environment.uri.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.environment.uri.RelativeURI;
import org.eclipse.wst.common.environment.uri.URI;
import org.eclipse.wst.common.environment.uri.URIException;
import org.eclipse.wst.common.environment.uri.URIFilter;
import org.eclipse.wst.common.environment.uri.URIScheme;


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
  public File asFile() 
  {
    return file_;
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#erase()
   */
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
      throw new URIException( new Status( IStatus.ERROR, "id", 0, exc.getMessage(), exc ), this );
    }
    
    return stream;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#getOutputStream()
   */
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
      throw new URIException( new Status( IStatus.ERROR, "id", 0, exc.getMessage(), exc ), this );
    }
    
    return stream;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#getURIScheme()
   */
  public URIScheme getURIScheme()
  {
    return new FileScheme();
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isAvailableAsFile()
   */
  public boolean isAvailableAsFile()
  {
    return file_ != null;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isAvailableAsURL()
   */
  public boolean isAvailableAsURL()
  {
    return true;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isLeaf()
   */
  public boolean isLeaf()
  {
    return file_.isFile();
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isPresent()
   */
  public boolean isPresent()
  {
    return file_.exists();
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isReadable()
   */
  public boolean isReadable()
  {
    return file_.canRead();
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#isWritable()
   */
  public boolean isWritable()
  {
    return file_.canWrite();
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#list()
   */
  public URI[] list() throws URIException
  {
    File[] children = file_.listFiles();
    URI[]  URIs     = new URI[0];
    
    if( children != null )
    {
      int   length   = children.length;
      
      URIs = new URI[length];
    
      for( int index = 0; index < length; index++ )
      {
        URIs[index] = new FileURI( "file:" + children[index].getAbsolutePath() );
      }
    }
    
    return URIs;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#list(org.eclipse.env.uri.URIFilter)
   */
  public URI[] list(URIFilter uriFilter) throws URIException
  {
    File[]   children = file_.listFiles();
    int      length   = children == null ? 0 : children.length;
    Vector   URIs     = new Vector();
    
    for( int index = 0; index < length; index++ )
    {
      URI newURI = new FileURI( "file:" + children[index].getAbsolutePath() );
      
      if( uriFilter.accepts( newURI) )
      {
        URIs.add( newURI );
      } 
    }
    
    return (URI[])URIs.toArray( new URI[0] );
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#rename(org.eclipse.env.uri.URI)
   */
  public void rename(URI newURI) throws URIException
  {
    uri_ = newURI.toString();
    file_.renameTo( new File( uri_ ) );
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#touchFolder()
   */
  public void touchFolder() throws URIException
  {
    file_.mkdirs();
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URI#touchLeaf()
   */
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
      throw new URIException( new Status( IStatus.ERROR, "id", 0, exc.getMessage(), exc ), this );      
    }
  }    
}
