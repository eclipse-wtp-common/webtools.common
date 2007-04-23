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

import java.net.URL;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.environment.uri.IURI;
import org.eclipse.wst.common.environment.uri.IURIScheme;
import org.eclipse.wst.common.environment.uri.URIException;
import org.eclipse.wst.common.internal.environment.eclipse.Messages;
import org.eclipse.wst.common.internal.environment.relative.RelativeScheme;
import org.eclipse.wst.common.internal.environment.relative.RelativeURI;


public class FileScheme extends RelativeScheme
{
  public String toString()
  {
    return "file";  
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URIScheme#isValid(org.eclipse.env.uri.URI)
   */
  public boolean isValid(IURI uri)
  { 
    boolean result = false;
    
    if( uri == null ) return false;
    
    IURIScheme scheme = uri.getURIScheme();
      
    if( scheme.toString().equals( "relative") ) return scheme.isValid( uri );
        
    String uriString = uri.toString();
      
    if( uriString != null && uriString.startsWith( "file:" ) )
    {
      result = true;
    }  
    
    return result;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URIScheme#newURI(java.lang.String)
   */
  public IURI newURI(String uri) throws URIException
  {
    String newURI = null;
    
    if( uri != null && uri.startsWith( "file:") )
    {
      // The file protocol has been specified so keep it as is.
      newURI = uri;
    }
    else if( uri == null || uri.indexOf( ":") != -1 )
    {
      // The file uri is not allowed to contain some other protocol. 
      throw new URIException(
          new Status( IStatus.ERROR, "id", 0,
              NLS.bind( Messages.MSG_INVALID_FILE_URL,uri), null ) );
              
    }
    else if( uri.startsWith( "/") )
    {
      // The file scheme has not been specified so we will add it.
      newURI = "file:" + uri;
    }
    
    if( newURI == null )
    {
      return new RelativeURI( uri );
    }
    else
    {
      return new FileURI( newURI );
    }
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URIScheme#newURI(org.eclipse.env.uri.URI)
   */
  public IURI newURI(IURI uri) throws URIException
  {
    return newURI( uri == null ? null : uri.toString() );
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URIScheme#newURI(java.net.URL)
   */
  public IURI newURI(URL url) throws URIException
  {
    return newURI( url == null ? null : url.toString() );
  }
}
