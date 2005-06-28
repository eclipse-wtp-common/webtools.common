/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *******************************************************************************/
package org.eclipse.wst.common.uriresolver.internal.provisional;

/**
 * The URIResolver is used to resolve URI references to resources.  A URIResolverInput
 * object is used store the information required to resolve a URI reference.  The URI resolver returns a  
 * URIResolverResult which provides the neccessary
 * information required to obtain and process the referenced resource.  The following example illustrates how the URIResolver is used. 
 * <br/>
 * <br/>
 * Let's say we're writing a program to render an HTML document located at the following URI
 * "http://www.example.org/myDocuments/exampleDocument.html". While parsing the document we ecounter a 
 * tag with a URL reference like this.
 *  
 * <pre>
 *   &lt;a href="../index.html"&gt;
 * </pre> 
 * 
 * At this point we would utilize the URIResolver to resolve this reference as shown here.
 * 
 * <pre>
 *   URIResolverInput resolverInput = new URIResolverInput("http://www.example.org/myDocuments/exampleDocument.html", 
 *                                                         "../index/index.html");
 *   URIResolverResult resolverResult = URIResolverManager.createResolver().resolve(resolverInput); 
 * </pre> 
 * 
 * If we inspect the content of the resolverResult we'll notice that the physicalURI is actually referencing some
 * cached location for the file (so behind the scenes one of the URIResolver extensions is providing caching support).
 * <pre>
 *    logicalURI="http://www.example.org/index/index.html" 
 *    physicalURI="file:///my-cache/092304.html"
 * </pre>
 * 
 * We now proceed to read the 'index' document using the physicalURI to obtain the stream.  Here's an example of how one might 
 * obtain a stream:
 * 
 * <pre>
 *   // here the phsyicalURI is used to open the stream (i.e. "file:///my-cache/092304.html")
 *   //
 *   InputStream stream = new URL(resolverResult.getPhysicalURI()).openStream();
 *   URIResolverResult resolverResult2 = URIResolverManager.createResolver().resolve(resolverInput); 
 * </pre>
 * Once we have obtained the stream, we proceed to parse the document and we encounter a resource reference like this...
 * 
 * <pre>
 *   &lt;image src="../myImage.jpg"/&gt;
 * </pre> 
 *
 * In order to retrieve the image, the URIResolver is invoked again using the logical location as the 
 * base URI parameter to the URIResolverInput as shown below.  
 *
 * <pre>
 *   // here the logicalURI is used to resolve relative references (i.e. "http://www.example.org/index/index.html")
 *   //
 *   URIResolverInput resolverInput2 = new URIResolverInput(resolverResult.getLogicalURI(), "../myImage.jpg");
 *   URIResolverResult resolverResult2 = URIResolverManager.createResolver().resolve(resolverInput); 
 * </pre>
 * 
 * In this example we've demonstrated how the URIResolver can be used to process multiple related documents.  
 * It's important to recognize that logical and physical locations of the URIResolverResult are significantly 
 * different and need to be understood by the URIResolver client.  The physical location is utilized to obtain a stream. 
 * The logical location is used to compute relative references encountered while parsing the stream.
 */
public interface URIResolver {
	
	/**
	 * @param baseLocation - the location of the resource that contains the uri 
	 * @param publicId - an optional public identifier (i.e. namespace name), or null if none
	 * @param systemId - an absolute or relative URI, or null if none 
	 * @return an absolute URI
   * 
   * @deprecated - clients should use URIResolverResult resolve(URIResolverInput input)
	 */
	public String resolve(String baseLocation, String publicId, String systemId);
  
  /*
   * @param input - a URIResolverInput that specifies the information of the resource reference 
   * @return a non-null URIResolverResult object 
   */  
  //TODO... enable this method for clients
  public URIResolverResult resolve(URIResolverInput input);
}
