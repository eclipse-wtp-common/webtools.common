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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


public class URIResolverInput
{
  private String referenceType; 
  private String namespace;
  private String publicId;
  private String referenceURI;
  private String baseURI;
  
  public URIResolverInput(String baseURI, String referencedURI)
  {
    this.baseURI = baseURI;
    this.referenceURI = referencedURI;
  }
  
  /**
   *  Sets the public identifier. 
   */
  public void setPublicId(String publicId)
  {
    this.publicId = publicId;
  }

  /** 
   * Returns the public identifier or null if no public identifier has been specified. 
   */
  public String getPublicId()
  {
    return publicId;
  }
  
  /** 
   * Sets the value of the referenced URI.
   */
  public void setReferenceURI(String referencedURI)
  {
    this.referenceURI = referencedURI;
  }
   
  /** 
   * Returns the value of the referenced URI.
   */
  public String getReferenceURI()
  {
    return referenceURI;
  }
  
  /** 
   * Sets the base URI against which the literal SystemId is to be resolved.
   * The base URI must be a fully qualified URI with a valid protocol.
   */
  public void setBaseURI(String baseURI)
  {
    this.baseURI = baseURI;
  }
  
  /** 
   * Returns the base URI against which the referencedURI is to be resolved. 
   */
  public String getBaseURI()
  {
    return baseURI;
  }

  /** 
   * Sets the namespace of the referenced resource.
   * The namespace can be null. 
   */
  public void setNamespace(String namespace)
  {
    this.namespace = namespace;
  }
   
  /** 
   * Returns the namespace associated with the referenced resource or null if no namespace has been specified. 
   */
  public String getNamespace()
  {
    return namespace;
  }
  
  private IFile baseFile;    
  /** 
   * Returns the IFile that is associated with the specified base URI or null if 
   * no corresponding IFile exists.   
   */
  public IFile getBaseFile()
  {
    if (baseFile == null && baseURI != null)
    { 
      // TODO... handle 'platform:'
      //
      String pattern = "file:///";
      String filePath = new String(baseURI); 
      if (filePath.startsWith(pattern))
      {
        filePath = filePath.substring(pattern.length());
      }
      IPath path = new Path(filePath);
      baseFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);      
      return baseFile;
    }
    return baseFile;
  }

  public String getReferenceType()
  {
    return referenceType;
  }

  public void setReferenceType(String type)
  {
    this.referenceType = type;
  }
}
