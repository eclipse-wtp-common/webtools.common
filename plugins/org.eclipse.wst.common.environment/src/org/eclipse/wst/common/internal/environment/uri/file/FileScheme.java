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

import java.net.URL;

import org.eclipse.wst.common.environment.uri.RelativeScheme;
import org.eclipse.wst.common.environment.uri.RelativeURI;
import org.eclipse.wst.common.environment.uri.IURI;


public class FileScheme extends RelativeScheme 
{
  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URIScheme#isValid(org.eclipse.env.uri.URI)
   */
  public boolean isValid(IURI uri)
  {
    return uri.toString().startsWith( "file:" );
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URIScheme#newURI(java.lang.String)
   */
  public IURI newURI(String uri) 
  {
    String newURI = null;
    
    if( uri.startsWith( "file:") )
    {
      // The file protocol has been specified so keep it as is.
      newURI = uri;
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
  public IURI newURI(IURI uri) 
  {
    return new FileURI( uri.toString() );
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URIScheme#newURI(java.net.URL)
   */
  public IURI newURI(URL url) 
  {
    return new FileURI( url.toString() );
  }
}
