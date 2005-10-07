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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * A URI represents a Univeral Resource Identifer.
 */
public interface URI
{
  /**
   * Returns the string form of the URI.
   * The resource need not exist.
   */
  public String toString ();

  /**
   * Returns the URIScheme for the scheme of this URI.
   * The resource need not exist.
   */
  public URIScheme getURIScheme ();

  /**
   * Returns true iff the resource identified by this URI exists.
   */
  public boolean isPresent ();

  /**
   * Returns true iff the resource identified by this URI exists
   * and is a leaf. Returns true always for non-hierarchical schemes.
   */
  public boolean isLeaf ();

  /**
   * Returns true iff the resource identified by this URI exists
   * and is readable. A readable leaf resource indicates that an
   * input stream can be opened on the resource. A readable folder
   * resource indicates that the children of the folder, if any,
   * can be determined via the list() or visit() methods.
   */
  public boolean isReadable ();

  /**
   * Returns true iff the resource identified by this URI exists
   * and is writable. A writable leaf resource indicates that an
   * output stream can be opened on the resource. A writable folder
   * resource indicates that new resources can be created in the folder.
   */
  public boolean isWritable ();

  /**
   * Returns true iff this URI belongs to a hierarchical scheme.
   * The resource need not exist.
   * this.isHierarchical() == this.getURIScheme().isHierarchical().
   */
  public boolean isHierarchical ();

  /**
   * Returns true iff this URI is a relative URI.
   * The resource need not exist.
   */
  public boolean isRelative ();

  /**
   * Returns the URI of the folder containing this URI.
   * The resource need not exist.
   */
  public URI parent () throws URIException;

  /**
   * Returns a new, normalized URI formed by appending the given
   * relative URI to this URI. Both URIs must be hierarchical.
   * The resulting URI will always be hierarchical. The given
   * URI must be relative. This URI can be relative or absolute.
   * the resulting URI will be equivalently relative or absolute.
   */
  public URI append ( URI relativeURI ) throws URIException;

  /**
   * Gets an InputStream for reading from the resource
   * identified by this leaf or non-hierarchical URI.
   */
  public InputStream getInputStream () throws URIException;

  /**
   * Gets an OutputStream for writing to the resource
   * identified by this leaf or non-hierarchical URI.
   */
  public OutputStream getOutputStream () throws URIException;

  /**
   * Creates a new, empty resource at the location identified by
   * the URI. On completion, this.isLeaf() == true. If a leaf
   * resource already exists, this method does nothing. If a
   * non-leaf resource already exists under this URI, creation
   * will fail and an exception will be thrown.
   */
  public void touchLeaf () throws URIException;

  /**
   * Creates a new folder resource at the location identified by
   * the URI. The scheme of the URI must be hierarchical.
   * On completion, this.isLeaf() == false. If a folder resource
   * already exists, this method does nothing. If a leaf resource
   * already exists under this URI, creation will fail and an
   * exception will be thrown.
   */
  public void touchFolder () throws URIException;

  /**
   * Erases the resource identified by this URI.
   */
  public void erase () throws URIException;

  /**
   * Renames or moves the resource identified by this URI
   * to the new URI.
   */
  public void rename ( URI newURI ) throws URIException;

  /**
   * Visits this resource and its decendants in pre-order fashion.
   * For each resource, whether a folder or a leaf, the given
   * URIVisitor's visit() method is called with the URI of the
   * resource. If URIVisitor.visit() returns false, the visiting
   * algorithm will "prune" the decendants of the resource and
   * carry on visiting the rest of the tree. If URIVisitor.visit()
   * returns true, the visiting algorithm will continue its walk
   * down the resource's descendants.
   */
  public void visit ( URIVisitor uriVisitor ) throws URIException;

  /**
   * As for visit(URIVisitor), except only resource URIs that are
   * accepted by the given filter are visited. If a folder resource
   * fails to be accepted by the filter, this will not prevent the
   * visiting algorithm from walking the resource's descendants.
   * It remains the job of the visitor to choose whether or not to
   * prune a branch by returning false or true.
   */
  public void visit ( URIVisitor uriVisitor, URIFilter uriFilter ) throws URIException;

  /**
   * Returns a list of URIs for the immediate children of the given
   * hierarchical, non-leaf URI. This method never returns null,
   * though it may return a zero length array.
   */
  public URI[] list () throws URIException;

  /**
   * As for list(), except only URIs that are accepted by the given
   * filter are returned in the array. This method never returns null,
   * though it may return a zero length array.
   */
  public URI[] list ( URIFilter uriFilter ) throws URIException;

  /**
   * Returns true if the asURL() method is fair game,
   * in other words, if this URI can be converted into a URL.
   */
  public boolean isAvailableAsURL ();

  /**
   * Returns a URL object for the resource under this URI.
   * There are many URIs and URISchemes for which this method
   * will fail and throw an exception. It should be used only
   * in cases where URIs are known to be representable as URLs.
   */
  public URL asURL () throws URIException;

  /**
   * Returns true if the asFile() method is fair game,
   * in other words, if this URI can be converted into a File.
   */
  public boolean isAvailableAsFile ();

  /**
   * Returns a File object for the resource under this URI.
   * There are many URIs and URISchemes for which this method
   * will fail and throw an exception. It should be used only
   * in cases where URIs are known to be backed by physical files.
   */
  public File asFile () throws URIException;

  /**
   * Returns the URI as a string.
   * Equivalent to toString().
   */
  public String asString ();
}
