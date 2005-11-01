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

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.environment.IEnvironment;
import org.eclipse.wst.common.environment.uri.IURI;
import org.eclipse.wst.common.environment.uri.IURIScheme;
import org.eclipse.wst.common.environment.uri.URIException;
import org.eclipse.wst.common.internal.environment.relative.RelativeURI;


public class EclipseScheme implements IURIScheme
{
  private IEnvironment      environment_;
  
  public EclipseScheme( IEnvironment environment )
  {
    environment_ = environment;
  }
  
  public String toString()
  {
    return "platform";  
  }
  
  /**
   */
  public boolean isHierarchical()
  {
    return true;
  }

  /**
   */
  public boolean isValid(IURI uri)
  {
    boolean result = true;
    
    if( uri == null ) return false;
    
    try
    {
      IURIScheme scheme = uri.getURIScheme();
      
      if( scheme.toString().equals( "relative") ) return scheme.isValid( uri );
        
      getPathFromPlatformURI( uri.toString() );
    }
    catch( URIException exc )
    {
      result = false;
    }
    
    return result;
  }

  /**
   */
  public IURI newURI(String uri) throws URIException
  {
    String newURI = null;
    
    if( uri != null && uri.startsWith( "platform:") )
    {
      // The platform has been specified so keep it as is.
      newURI = uri;
    }
    else if( uri == null || uri.indexOf( ":") != -1 )
    {
      // The platform uri is not allowed to contain some other protocol. 
      throw new URIException(
          new Status( IStatus.ERROR, "id", 0,
              NLS.bind( Messages.MSG_INVALID_PLATFORM_URL,uri), null ) );
              
    }
    else if( uri.startsWith( "/") )
    {
      // The platform scheme has not been specified so we will add it.
      newURI = "platform:/resource" + uri;
    }
    
    if( newURI == null )
    {
      return new RelativeURI( uri );
    }
    else
    {
      return new EclipseURI( newURI, environment_ );
    }
  }

  /**
   */
  public IURI newURI(IURI uri) throws URIException
  {
    return newURI( uri == null ? null : uri.toString() );
  }

  /**
   */
  public IURI newURI(URL url) throws URIException
  {
    return newURI( url == null ? null : url.toString() );
  }

  /**
   */
  public IStatus validate(IURI uri)
  {
    IStatus status = null;
    
    try
    {
      IURIScheme scheme = uri.getURIScheme();
      
      if( scheme.toString().equals( "relative") ) return scheme.validate( uri );
      
      getPathFromPlatformURI( uri.toString() );
      status = Status.OK_STATUS;
    }
    catch( URIException exc )
    {
      status = exc.getStatus(); 
    }
    
    return status;
  }
  
  /**
   * Gets the "platform:/resource" IPath given a url
   * 
   */
  public String getPathFromPlatformURI(String uri) throws URIException 
  {
    String resourceFile = null;
    URL    url          = null;
    
    try
    {
      url = new URL( uri ); 
    }
    catch( MalformedURLException exc )
    { 
      throw new URIException(
          new Status( IStatus.ERROR, "id", 0,
              NLS.bind( Messages.MSG_INVALID_PLATFORM_URL, uri ), exc ) );
    }
    
    if( url == null )
    {
      throw new URIException(
          new Status( IStatus.ERROR, "id", 0,
              NLS.bind( Messages.MSG_INVALID_PLATFORM_URL, uri ), null ) );
    }
    if( url.getProtocol().equals("platform") ) 
    {
      String resourceURL = url.getFile();
      
      if (resourceURL.startsWith("/resource")) 
      {
        resourceFile = resourceURL.substring(10);   // omit the "/resource" portion
      }
    }
    else 
    {
      throw new URIException(
          new Status( IStatus.ERROR, "id", 0,
                      NLS.bind( Messages.MSG_INVALID_PLATFORM_URL,url.getFile() ), null ) );
    }
    
    return resourceFile;
  }
  
  /**
   * 
   * @param absolutePath an absolute IPath
   * @return returns the platform IURI for this path.
   */
  public String getURLFromPath( IPath absolutePath )
  {
    return "platform:/resource" + absolutePath;
  }
}
