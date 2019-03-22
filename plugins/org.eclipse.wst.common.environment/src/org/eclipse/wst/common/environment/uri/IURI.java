/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.environment.uri;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * A IURI represents a Univeral Resource Identifer.
 * 
 * @since 1.0
 */
public interface IURI
{
  /**
   * @return Returns the string form of the IURI.
   * The resource need not exist.
   */
  public String toString ();

  /**
   * @return Returns the IURIScheme for the scheme of this IURI.
   * The resource need not exist.
   */
  public IURIScheme getURIScheme ();

  /**
   * @return Returns true iff the resource identified by this IURI exists.
   */
  public boolean isPresent ();

  /**
   * @return Returns true iff the resource identified by this IURI exists
   * and is a leaf. Returns true always for non-hierarchical schemes.
   */
  public boolean isLeaf ();

  /**
   * @return Returns true iff the resource identified by this IURI exists
   * and is readable. A readable leaf resource indicates that an
   * input stream can be opened on the resource. A readable folder
   * resource indicates that the children of the folder, if any,
   * can be determined via the list() or visit() methods.
   */
  public boolean isReadable ();

  /**
   * @return Returns true iff the resource identified by this IURI exists
   * and is writable. A writable leaf resource indicates that an
   * output stream can be opened on the resource. A writable folder
   * resource indicates that new resources can be created in the folder.
   */
  public boolean isWritable ();

  /**
   * @return Returns true iff this IURI belongs to a hierarchical scheme.
   * The resource need not exist.
   * this.isHierarchical() == this.getURIScheme().isHierarchical().
   */
  public boolean isHierarchical ();

  /**
   * @return Returns true iff this IURI is a relative IURI.
   * The resource need not exist.
   */
  public boolean isRelative ();

  /**
   * @return Returns the IURI of the folder containing this IURI.
   * The resource need not exist.
   * @throws URIException if there is no parent URI.
   */
  public IURI parent () throws URIException;

  /**
   * @param relativeURI a relative URI.
   * @return Returns a new, normalized IURI formed by appending the given
   * relative IURI to this IURI. Both URIs must be hierarchical.
   * The resulting IURI will always be hierarchical. The given
   * IURI must be relative. This IURI can be relative or absolute.
   * the resulting IURI will be equivalently relative or absolute.
   * @throws URIException if relativeURI is not relative.
   */
  public IURI append ( IURI relativeURI ) throws URIException;

  /**
   * @return Gets an InputStream for reading from the resource
   * identified by this leaf or non-hierarchical IURI.
   * 
   * @throws URIException if a stream for this URI can not be created.
   */
  public InputStream getInputStream () throws URIException;

  /**
   * @return Gets an OutputStream for writing to the resource
   * identified by this leaf or non-hierarchical IURI.
   * @throws URIException if a stream for this URI can not be created.
   */
  public OutputStream getOutputStream () throws URIException;

  /**
   * Creates a new, empty resource at the location identified by
   * the IURI. On completion, this.isLeaf() == true. If a leaf
   * resource already exists, this method does nothing. If a
   * non-leaf resource already exists under this IURI, creation
   * will fail and an exception will be thrown.
   * @throws URIException if an error occurs touching this leaf resource.
   */
  public void touchLeaf () throws URIException;

  /**
   * Creates a new folder resource at the location identified by
   * the IURI. The scheme of the IURI must be hierarchical.
   * On completion, this.isLeaf() == false. If a folder resource
   * already exists, this method does nothing. If a leaf resource
   * already exists under this IURI, creation will fail and an
   * exception will be thrown.
   * @throws URIException if an error occurs touching this folder resource.
   */
  public void touchFolder () throws URIException;

  /**
   * Erases the resource identified by this IURI.
   * @throws URIException if an error occurs erasing this resource.
   */
  public void erase () throws URIException;

  /**
   * Renames or moves the resource identified by this IURI
   * to the new IURI.
   * @param newURI the new URI name for this URI.
   * @throws URIException if an error occurs renaming this resource.
   */
  public void rename ( IURI newURI ) throws URIException;

  /**
   * Visits this resource and its decendants in pre-order fashion.
   * For each resource, whether a folder or a leaf, the given
   * IURIVisitor's visit() method is called with the IURI of the
   * resource. If IURIVisitor.visit() returns false, the visiting
   * algorithm will "prune" the decendants of the resource and
   * carry on visiting the rest of the tree. If IURIVisitor.visit()
   * returns true, the visiting algorithm will continue its walk
   * down the resource's descendants.
   * @param uriVisitor the visitor that will be called for each resource visited.
   * @throws URIException if an error occurs visiting this resource and its children.
   */
  public void visit ( IURIVisitor uriVisitor ) throws URIException;

  /**
   * As for visit(IURIVisitor), except only resource URIs that are
   * accepted by the given filter are visited. If a folder resource
   * fails to be accepted by the filter, this will not prevent the
   * visiting algorithm from walking the resource's descendants.
   * It remains the job of the visitor to choose whether or not to
   * prune a branch by returning false or true.
   * @param uriVisitor the visitor that will be called for each resource visited.
   * @param uriFilter the resource filter.
   * @throws URIException if an error occurs visiting this resource and its children.
   */
  public void visit ( IURIVisitor uriVisitor, IURIFilter uriFilter ) throws URIException;

  /**
   * Returns a list of URIs for the immediate children of the given
   * hierarchical, non-leaf IURI. This method never returns null,
   * though it may return a zero length array.
   * @return returns the children for this URI.
   * @throws URIException if an error occurs locating the children for this URI.
   */
  public IURI[] list () throws URIException;

  /**
   * As for list(), except only URIs that are accepted by the given
   * filter are returned in the array. This method never returns null,
   * though it may return a zero length array.
   * @param uriFilter the child filter.
   * @return returns the filtered children for this URI.
   * @throws URIException if an error occurs locating the children for this URI.
   */
  public IURI[] list ( IURIFilter uriFilter ) throws URIException;

  /**
   * @return Returns true if the asURL() method is fair game,
   * in other words, if this IURI can be converted into a URL.
   */
  public boolean isAvailableAsURL ();

  /**
   * @return Returns a URL object for the resource under this IURI.
   * There are many URIs and URISchemes for which this method
   * will fail and throw an exception. It should be used only
   * in cases where URIs are known to be representable as URLs.
   * @throws URIException if an error occurs returning this URL.
   */
  public URL asURL () throws URIException;

  /**
   * @return Returns true if the asFile() method is fair game,
   * in other words, if this IURI can be converted into a File.
   */
  public boolean isAvailableAsFile ();

  /**
   * @return Returns a File object for the resource under this IURI.
   * There are many URIs and URISchemes for which this method
   * will fail and throw an exception. It should be used only
   * in cases where URIs are known to be backed by physical files.
   * @throws URIException if an error occurs returning this File.
   */
  public File asFile () throws URIException;

  /**
   * @return Returns the IURI as a string.
   * Equivalent to toString().
   */
  public String asString ();
}
