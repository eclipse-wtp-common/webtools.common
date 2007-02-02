/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *******************************************************************************/
package org.eclipse.wst.common.uriresolver.internal.util;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


public class URIHelper
{                       
  protected static final String FILE_PROTOCOL = "file:";
  protected static final String PLATFORM_RESOURCE_PROTOCOL = "platform:/resource/";
  protected static final String PROTOCOL_PATTERN = ":"; 
  
  
  public static String ensureURIProtocolFormat(String uri) {
	  String protocol = getProtocol(uri);
	  if (protocol != null) {
		  if (protocol.equals(FILE_PROTOCOL)) {
			  return ensureFileURIProtocolFormat(uri);
		  }
	  }
	 return uri;
  }
  
  
  /**
   * This method takes a file URI in String format and ensures the protocol is followed by three slashes.
   * For example, files "file:D:/XXX", "file:/D:/XXX" and "file://D:/XXX" are corrected to:
   * "file:///D:/XXX".
   * If the input is not a file URI (does not start with "file:"), the String is returned unmodified.
   */
  public static String ensureFileURIProtocolFormat(String uri) {
      if (uri.startsWith(FILE_PROTOCOL) && !uri.startsWith(FILE_PROTOCOL + "///")) //$NON-NLS-1$
      {
      	if (uri.startsWith(FILE_PROTOCOL + "//")) {
      		uri = FILE_PROTOCOL + "/" + uri.substring(FILE_PROTOCOL.length()); //$NON-NLS-1$
      	} else if (uri.startsWith(FILE_PROTOCOL + "/")) {
      		uri = FILE_PROTOCOL + "//" + uri.substring(FILE_PROTOCOL.length()); //$NON-NLS-1$
      	} else {
      		uri = FILE_PROTOCOL + "///" + uri.substring(FILE_PROTOCOL.length()); //$NON-NLS-1$
      	}
      }
     return uri;
  }
  
  public static String normalize(String uri)
  {                           
    if (uri != null)
    {                      
      String protocol = getProtocol(uri);
      String file = uri;
           
      if (protocol != null)
      {               
        try
        {   
          // 
          URL url = new URL(uri); 
          // we use a 'Path' on the 'file' part of the url in order to normalize the '.' and '..' segments
          IPath path = new Path(url.getFile()); 
          URL url2 = new URL(url.getProtocol(), url.getHost(), url.getPort(), path.toString());
          uri = url2.toString();                               
        }                        
        catch (Exception e)
        {  
        }
      }   
      else
      {      
        IPath path = new Path(file);
        uri = path.toString();
      }
    }
    return uri;
  }


  /**
   * a 'null' rootLocation argument will causes uri that begins with a '/' to be treated as a workspace relative resource
   * (i.e. the string "platform:/resource" is prepended and the uri is resolved via the Platform object)
   */
  public static String normalize(String uri, String resourceLocation, String rootLocation)
  {
    String result = null;

    if (uri != null)
    { 
      // is the uri a url
      if (hasProtocol(uri))
      {                  
        if (isPlatformResourceProtocol(uri))
        {
          result = resolvePlatformUrl(uri);
        }
        else
        {
          result = uri;
        }
      }
   
      // is uri absolute
      //
      if (result == null)
      {
        if (uri.indexOf(":") != -1 || uri.startsWith("/") || uri.startsWith("\\"))
        {                   
          result = uri;
        }
      }
  
      // if uri is relative to the resourceLocation
      //
      if (result == null && resourceLocation != null)
      {          
        if (resourceLocation.endsWith("/"))
        {
			    result = resourceLocation + uri;
        }
		    else
        {
			    result = resourceLocation + "/../" + uri;
        }
      }
      
      if (result == null)
      {
        result = uri;
      }
  
      result = normalize(result);
    }

    //System.out.println("normalize(" + uri + ", " + resourceLocation + ", " + rootLocation + ") = " + result);
    return result;
  }


  public static boolean isURL(String uri)
  {
    return uri.indexOf(":/") > 2; // test that the index is > 2 so that C:/ is not considered a protocol
  }


  public static String getLastSegment(String uri)
  {
    String result = uri;
    int index = Math.max(uri.lastIndexOf("/"), uri.lastIndexOf("\\"));
    if (index != -1)
    {
      result = uri.substring(index + 1);
    }
    return result;
  }


  public static String getFileExtension(String uri)
  {
    String result = null;
    int dotIndex = getExtensionDotIndex(uri);
               
    if (dotIndex != -1)
    {
      result = uri.substring(dotIndex + 1);
    }

    return result;
  }


  public static String removeFileExtension(String uri)
  {
    String result = null;
    int dotIndex = getExtensionDotIndex(uri);

    if (dotIndex != -1)
    {
      result = uri.substring(0, dotIndex);
    }

    return result;
  }   
             

  // here we use the Platform to resolve a workspace relative path to an actual url
  //
  protected static String resolvePlatformUrl(String urlspec)
  {
    String result = null;
    try
    {                        
      urlspec = urlspec.replace('\\', '/'); 
      URL url = new URL(urlspec);
      URL resolvedURL = FileLocator.resolve(url);
      result = resolvedURL.toString();
    }
    catch (Exception e)
    {
    }
    return result;
  }


  protected static int getExtensionDotIndex(String uri)
  {
    int result = -1;
    int dotIndex = uri.lastIndexOf(".");
    int slashIndex = Math.max(uri.lastIndexOf("/"), uri.lastIndexOf("\\"));

    if (dotIndex != -1 && dotIndex > slashIndex)
    {
      result = dotIndex;
    }

    return result;
  }
  

  public static boolean isPlatformResourceProtocol(String uri)
  {                                                     
    return uri != null && uri.startsWith(PLATFORM_RESOURCE_PROTOCOL);
  }                                                   

  public static String removePlatformResourceProtocol(String uri)
  {  
    if (uri != null && uri.startsWith(PLATFORM_RESOURCE_PROTOCOL))
    {
      uri = uri.substring(PLATFORM_RESOURCE_PROTOCOL.length());
    }                                                          
    return uri;
  }            


  public static String prependPlatformResourceProtocol(String uri)
  {  
    if (uri != null && !uri.startsWith(PLATFORM_RESOURCE_PROTOCOL))
    {
      uri = PLATFORM_RESOURCE_PROTOCOL + uri;
    }                                                          
    return uri;
  } 
  

  public static String prependFileProtocol(String uri)
  {  
    if (uri != null && !uri.startsWith(FILE_PROTOCOL))
    {
      uri = FILE_PROTOCOL + uri;
    }                                                          
    return uri;
  } 
            
  public static boolean hasProtocol(String uri)
  {
    boolean result = false;     
    if (uri != null)
    {
      int index = uri.indexOf(PROTOCOL_PATTERN);
      if (index != -1 && index > 2) // assume protocol with be length 3 so that the'C' in 'C:/' is not interpreted as a protocol
      {
        result = true;
      }
    }
    return result;
  }     
                      

  public static boolean isAbsolute(String uri)
  {
    boolean result = false;     
    if (uri != null)
    {
      int index = uri.indexOf(PROTOCOL_PATTERN);
      if (index != -1 || uri.startsWith("/") || uri.startsWith("\\"))
      {
        result = true;
      }
    }
    return result;
  }


  public static String addImpliedFileProtocol(String uri)
  {  
    if (!hasProtocol(uri))
    {                           
      String prefix = FILE_PROTOCOL;
      prefix += uri.startsWith("/") ? "//" : "///";
      uri = prefix + uri;
    }
    return uri;
  }
             
  // todo... need to revisit this before we publicize it
  // 
  protected static String getProtocol(String uri)
  {  
    String result = null;     
    if (uri != null)
    {
      int index = uri.indexOf(PROTOCOL_PATTERN);
      if (index > 2) // assume protocol with be length 3 so that the'C' in 'C:/' is not interpreted as a protocol
      {
        result = uri.substring(0, index + PROTOCOL_PATTERN.length());
      }
    }
    return result;
  } 
 

  public static String removeProtocol(String uri)
  {
    String result = uri;     
    if (uri != null)
    {
      int index = uri.indexOf(PROTOCOL_PATTERN);
      if (index > 2)
      {
        result = result.substring(index + PROTOCOL_PATTERN.length());                 
      }
    }
    return result;
  } 


  protected static boolean isProtocolFileOrNull(String uri)
  {                                    
    String protocol = getProtocol(uri);   
    return protocol == null || protocol.equals(FILE_PROTOCOL);
  }  

                                           
  protected static boolean isMatchingProtocol(String uri1, String uri2)
  { 
    boolean result = false;  

    String protocol1 = getProtocol(uri1);
    String protocol2 = getProtocol(uri2);

    if (isProtocolFileOrNull(protocol1) && isProtocolFileOrNull(protocol2))
    {                                                                      
      result = true;
    } 
    else
    {
      result = protocol1 != null && protocol2 != null && protocol1.equals(protocol2);
    }             

    return result;
  }

  /**
   * warning... this method not fully tested yet
   */
  public static String getRelativeURI(String uri, String resourceLocation)
  {                                      
    String result = uri;  
    if (isMatchingProtocol(uri, resourceLocation)) 
    {
      result = getRelativeURI(new Path(removeProtocol(uri)),
                              new Path(removeProtocol(resourceLocation)));
    }            

    return result;
  }

  /**
   * warning... this method not fully tested yet
   */
  public static String getRelativeURI(IPath uri, IPath resourceLocation)
  {            
    String result = null;
    int nMatchingSegments = 0;       
    resourceLocation = resourceLocation.removeLastSegments(1);
    while (true)
    {                   
      String a = uri.segment(nMatchingSegments); 
      String b = resourceLocation.segment(nMatchingSegments); 
      if (a != null && b != null && a.equals(b))
      {
        nMatchingSegments++;
      }
      else
      {
        break;
      }
    }                 

    if (nMatchingSegments == 0)
    {
      result = uri.toOSString();
    }
    else
    {    
      result = "";   
      boolean isFirst = true;
      String[] segments = resourceLocation.segments();
      for (int i = nMatchingSegments; i < segments.length; i++)
      {  
        result += isFirst ? ".." : "/..";     
        if (isFirst)
        {
          isFirst = false;
        }        
      }
      // 
      segments = uri.segments();
      for (int i = nMatchingSegments; i < segments.length; i++)
      {                      
        result += isFirst ? segments[i] : ("/" + segments[i]);     
        if (isFirst)
        {
          isFirst = false;
        } 
      }
    }   
    return result;
  }


  public static String getPlatformURI(IResource resource)
  {                            
    String fullPath = resource.getFullPath().toString();
    if (fullPath.startsWith("/"))
    {
      fullPath = fullPath.substring(1);
    }
    return PLATFORM_RESOURCE_PROTOCOL + fullPath;
  }
  

  /**
   * This methods is used as a quick test to see if a uri can be resolved to an existing resource.   
   */
  public static boolean isReadableURI(String uri, boolean testRemoteURI)
  {  
    boolean result = true;  
    if (uri != null)
    {   
      try
      {                               
        uri = normalize(uri, null, null);
        if (isProtocolFileOrNull(uri))
        {
          uri = removeProtocol(uri);                            
          File file = new File(org.eclipse.wst.common.uriresolver.internal.URI.decode(uri));
          result = file.exists() && file.isFile();
        }
        else if (isPlatformResourceProtocol(uri))
        {
          // Note - If we are here, uri has been failed to resolve
          // relative to the Platform. See normalize() to find why.
          result = false;
        }
        else if (testRemoteURI)
        {
          URL url = new URL(uri);
          InputStream is = url.openConnection().getInputStream();
          is.close();
          // the uri is readable if we reach here.
          result = true;
        }
      }
      catch (Exception e)
      {
        result = false;
      }
    }
    else // uri is null
      result = false;

    return result;
  }  

  /**
   * return true if this is a valid uri
   */
  public static boolean isValidURI(String uri)
  {                       
    boolean result = false;
    try                                                              
    {
      new URI(uri);
      result = true;
    }
    catch (Exception e)
    {
    }               
    return result;
  }

  /**
   * returns an acceptable URI for a file path
   */
  public static String getURIForFilePath(String filePath)
  {
    String result = addImpliedFileProtocol(filePath);
    if (!isValidURI(result))
    {
    	try
    	{
        result = URIEncoder.encode(result, "UTF8");
    	}
    	catch(UnsupportedEncodingException e)
    	{
    		// Do nothing as long as UTF8 is used. This is supported.
    	}
    }
    return result;
  }
}
