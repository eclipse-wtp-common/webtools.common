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
import org.eclipse.wst.common.environment.Environment;
import org.eclipse.wst.common.environment.uri.RelativeURI;
import org.eclipse.wst.common.environment.uri.URI;
import org.eclipse.wst.common.environment.uri.URIException;
import org.eclipse.wst.common.environment.uri.URIScheme;


public class EclipseScheme implements URIScheme
{
  private Environment      environment_;
  
  public EclipseScheme( Environment environment )
  {
    environment_ = environment;
  }
  
  /**
   */
  public boolean isHierarchical()
  {
    return true;
  }

  /**
   */
  public boolean isValid(URI uri)
  {
    boolean result = true;
    
    if( uri == null ) return false;
    
    try
    {
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
  public URI newURI(String uri) throws URIException
  {
    String newURI = null;
    
    if( uri.startsWith( "platform:/resource") )
    {
      // The platform has been specified so keep it as is.
      newURI = uri;
    }
    else if( uri.indexOf( ":") != -1 )
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
  public URI newURI(URI uri) throws URIException
  {
    return newURI( uri.toString() );
  }

  /**
   */
  public URI newURI(URL url) throws URIException
  {
    return newURI( url.toString() );
  }

  /**
   */
  public IStatus validate(URI uri)
  {
    IStatus status = null;
    
    try
    {
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
   * @return returns the platform URI for this path.
   */
  public String getURLFromPath( IPath absolutePath )
  {
    return "platform:/resource" + absolutePath;
  }
}
