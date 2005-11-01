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
package org.eclipse.wst.common.environment.uri;

import java.net.URL;
import java.util.Hashtable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.internal.environment.eclipse.Messages;
import org.eclipse.wst.common.internal.environment.relative.RelativeScheme;

/**
 * 
 * This class provides a default implementation of the IURIFactory interface.
 *
 */
public class SimpleURIFactory implements IURIFactory
{
  private Hashtable    schemes_ = new Hashtable();  
  
  /**
   * Creates and returns a new IURI for the given string.
   */
  public IURI newURI(String uri) throws URIException
  {
    IURIScheme scheme = newURIScheme( uri, false );
    
    return scheme.newURI( uri );
  }

  /**
   * Creates and returns a new IURI for the given URL.
   */
  public IURI newURI(URL url) throws URIException
  {
    IURIScheme scheme = newURIScheme( url.toString(), false );
    
    return scheme.newURI( url );
  }

  /**
   * Creates and returns a new IURIScheme for the given scheme string.
   * If the string contains no colons, the entire string is interpretted
   * as the name of the scheme. If the string contains a colon, then the
   * substring up to but excluding the first colon is interpretted as the
   * name of the scheme, meaning the caller can pass in any IURI string in
   * order to get a IURIScheme object.
   */
  public IURIScheme newURIScheme(String schemeOrURI) throws URIException
  {
    return newURIScheme( schemeOrURI, true );
  }
  
  public void registerScheme( String protocol, IURIScheme scheme )
  {
    schemes_.put( protocol, scheme );
  }
  
  private IURIScheme newURIScheme(String schemeOrURI, boolean checkForScheme ) throws URIException
  {
    IURIScheme newScheme = null;
    
    if( schemeOrURI == null )
    {
      throw new URIException( 
              new Status( IStatus.ERROR, "id", 0, 
                 NLS.bind( Messages.MSG_NULL_ARG_SPECIFIED, "newURIScheme"), null ) );
    }
    
    int colon    = schemeOrURI.indexOf(':');
    int slash    = schemeOrURI.indexOf('/');
    
    // A protocol was specified.  Note: a colon appearing after a path is not
    // considered part of the protocol for this IURI.
    if( ( checkForScheme && colon == -1 ) ||
        ( colon != -1 && slash == -1) || 
        ( colon != -1 && colon < slash ) )
    {
      // If colon is -1 then we will treat the entire input parameter as the protocol.
      if( colon == -1 ) colon = schemeOrURI.length();
      
      String protocol = schemeOrURI.substring(0, colon );
      newScheme       = (IURIScheme)schemes_.get( protocol );
      
      if( newScheme == null )
      {
        throw new URIException( 
            new Status( IStatus.ERROR, "id", 0, 
                NLS.bind( Messages.MSG_SCHEME_NOT_FOUND, schemeOrURI ), null ) );
                
      }
    }
    else if( schemeOrURI.startsWith( "/") )
    {
      throw new URIException( 
          new Status( IStatus.ERROR, "id", 0,
              NLS.bind( Messages.MSG_ABSOLUTE_PATH_WITHOUT_SCHEME, schemeOrURI ), null ) );
      
    }
    else
    {
      newScheme = new RelativeScheme();
    }
    
    return newScheme;
  }
}
