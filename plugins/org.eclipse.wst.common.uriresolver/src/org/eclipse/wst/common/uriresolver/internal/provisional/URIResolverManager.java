package org.eclipse.wst.common.uriresolver.internal.provisional;

import org.eclipse.wst.common.uriresolver.internal.URIResolverPlugin;

/**
 * This class is used to obtain a URIResolver object.
 */
public class URIResolverManager
{
  /**
   * Returns a URIResolver object.
   */
  public static URIResolver getURIResolver()
  {
    return URIResolverPlugin.createResolver();
  }  
}
