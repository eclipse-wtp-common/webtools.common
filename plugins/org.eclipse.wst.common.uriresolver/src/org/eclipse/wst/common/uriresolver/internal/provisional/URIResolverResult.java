/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.uriresolver.internal.provisional;


/**
 * This class represents a resolution result.  The resolution result consists of a logical URI
 * and a physical URI. The physical location represents the 'actual' location where the resource 
 * exists.  The physical location should be used to obtain an input stream.  The logical location 
 * represents the 'conceptual' location where a resource exists.  If while processing a document 
 * a  relative URI reference is encountered, the logical URI should be used to expand the relative 
 * URI reference.
 * <br/>
 * <br/>
 * The distinction between logical and physical URI becomes apparent when URI resolver extensions utilize resolution
 * strategies that resemble file caching.  A resource might be logically located at some web address (e.g. http://www.example.org/index/index.html)
 * but can be physically located in some local cache (e.g. file:///my-cache/092304.html").  When the cached file is processed
 * it's relative references need to be expanded relative to the logical location (as opposed to the physical location).
 */
public class URIResolverResult
{
  //TODO.. there have been requests to store additional information back via the URIResolverResult
  //
  private String logicalURI;
  private String physicalURI;
  
  public String getLogicalURI()
  {
    return logicalURI;
  }
  
  public void setLogicalURI(String logicalURI)
  {
    this.logicalURI = logicalURI;
  }

  public String getPhysicalURI()
  {
    return physicalURI;
  }

  public void setPhysicalURI(String physicalURI)
  {
    this.physicalURI = physicalURI;
  }
}
