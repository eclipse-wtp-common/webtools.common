/*******************************************************************************
 * Copyright (c) 2004, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
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
  
  @Override
public String toString()
  {
    return "platform"; //$NON-NLS-1$
  }
  
  /**
   */
  @Override
public boolean isHierarchical()
  {
    return true;
  }

  /**
   */
  @Override
public boolean isValid(IURI uri)
  {
    boolean result = true;
    
    if( uri == null ) return false;
    
    try
    {
      IURIScheme scheme = uri.getURIScheme();
      
      if( scheme.toString().equals( "relative") ) return scheme.isValid( uri ); //$NON-NLS-1$
        
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
  @Override
public IURI newURI(String uri) throws URIException
  {
    String newURI = null;
    
    if( uri != null && uri.startsWith( "platform:") ) //$NON-NLS-1$
    {
      // The platform has been specified so keep it as is.
      newURI = uri;
    }
    else if( uri == null || uri.indexOf( ":") != -1 ) //$NON-NLS-1$
    {
      // The platform uri is not allowed to contain some other protocol. 
      throw new URIException(
          new Status( IStatus.ERROR, "id", 0, //$NON-NLS-1$
              NLS.bind( Messages.MSG_INVALID_PLATFORM_URL,uri), null ) );
              
    }
    else if( uri.startsWith( "/") ) //$NON-NLS-1$
    {
      // The platform scheme has not been specified so we will add it.
      newURI = "platform:/resource" + uri; //$NON-NLS-1$
    }
    
    if( newURI == null )
    {
      return new RelativeURI( uri );
    }
    return new EclipseURI( newURI, environment_ );
  }

  /**
   */
  @Override
public IURI newURI(IURI uri) throws URIException
  {
    return newURI( uri == null ? null : uri.toString() );
  }

  /**
   */
  @Override
public IURI newURI(URL url) throws URIException
  {
    return newURI( url == null ? null : url.toString() );
  }

  /**
   */
  @Override
public IStatus validate(IURI uri)
  {
    IStatus status = null;
    
    try
    {
      IURIScheme scheme = uri.getURIScheme();
      
      if( scheme.toString().equals( "relative") ) return scheme.validate( uri ); //$NON-NLS-1$
      
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
          new Status( IStatus.ERROR, "id", 0, //$NON-NLS-1$
              NLS.bind( Messages.MSG_INVALID_PLATFORM_URL, uri ), exc ) );
    }
    
    if( url.getProtocol().equals("platform") )  //$NON-NLS-1$
    {
      String resourceURL = url.getFile();
      
      if (resourceURL.startsWith("/resource"))  //$NON-NLS-1$
      {
        resourceFile = resourceURL.substring(10);   // omit the "/resource" portion
      }
    }
    else 
    {
      throw new URIException(
          new Status( IStatus.ERROR, "id", 0, //$NON-NLS-1$
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
    return "platform:/resource" + absolutePath; //$NON-NLS-1$
  }
}
