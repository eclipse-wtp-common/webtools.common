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
package org.eclipse.wst.common.internal.environment.relative;

import java.net.URL;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.environment.uri.IURI;
import org.eclipse.wst.common.environment.uri.IURIScheme;
import org.eclipse.wst.common.environment.uri.URIException;


public class RelativeScheme implements IURIScheme
{

  public String toString()
  {
    return "relative";  
  }
  
  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URIScheme#isHierarchical()
   */
  public boolean isHierarchical()
  {
    return true;
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URIScheme#isValid(org.eclipse.env.uri.URI)
   */
  public boolean isValid(IURI uri)
  {
    return !uri.toString().startsWith( "/" );
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URIScheme#newURI(java.lang.String)
   */
  public IURI newURI(String uri) throws URIException
  {
    return new RelativeURI( uri );
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URIScheme#newURI(org.eclipse.env.uri.URI)
   */
  public IURI newURI(IURI uri) throws URIException
  {
    return new RelativeURI( uri.toString() );
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URIScheme#newURI(java.net.URL)
   */
  public IURI newURI(URL url) throws URIException
  {
    return new RelativeURI( url.toString() );
  }

  /* (non-Javadoc)
   * @see org.eclipse.env.uri.URIScheme#validate(org.eclipse.env.uri.URI)
   */
  public IStatus validate(IURI uri)
  {
    IStatus result = null;
    
    if( isValid( uri ) )
    {
      result = Status.OK_STATUS;
    }
    else
    {
      result = new Status( IStatus.ERROR, "id", 0, "",null );      
    }
    
    return result;
  }
}
