/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.common.uriresolver.internal.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;

/**
 *  This class is a modified version of java.lang.URLEncoder.
 */
public class URIEncoder 
{
  static BitSet dontNeedEncoding;
  static final int caseDiff = ('a' - 'A');
  static String dfltEncName = null;
  

  static 
  {            
	  dontNeedEncoding = new BitSet(256);
	  int i;
	  for (i = 'a'; i <= 'z'; i++) 
    {
	    dontNeedEncoding.set(i);
	  }
	  for (i = 'A'; i <= 'Z'; i++) 
    {
	    dontNeedEncoding.set(i);
	  }
	  for (i = '0'; i <= '9'; i++) 
    {
	    dontNeedEncoding.set(i);
	  }

	  //dontNeedEncoding.set(' '); // cs.. removed so that space character will be replaced by %20
	  dontNeedEncoding.set('-');
	  dontNeedEncoding.set('_');
	  dontNeedEncoding.set('.');
	  dontNeedEncoding.set('*');
	  dontNeedEncoding.set(':');   // cs.. added 
	  dontNeedEncoding.set('/');   // cs.. added so that slashes don't get encoded as %2F

  	// dfltEncName = (String)AccessController.doPrivileged(new GetPropertyAction("file.encoding"));
  	// As discussed with Sandy, we should encode URIs with UTF8
   dfltEncName = "UTF8";
    //System.out.println("dfltEncName " + dfltEncName);
   }

  /**
   * You can't call the constructor.
   */
  private URIEncoder() { }

  /**
   * Translates a string into <code>x-www-form-urlencoded</code>
   * format. This method uses the platform's default encoding
   * as the encoding scheme to obtain the bytes for unsafe characters.
   *
   * @param   s   <code>String</code> to be translated.
   * @deprecated The resulting string may vary depending on the platform's
   *             default encoding. Instead, use the encode(String,String)
   *             method to specify the encoding.
   * @return  the translated <code>String</code>.
   */
  public static String encode(String s) 
  {
	  String str = null;
	  try 
    {
	    str = encode(s, dfltEncName);
	  } 
    catch (UnsupportedEncodingException e) 
    {
	    // The system should always have the platform default
	  }
	  return str;
  }

  /**
   * Translates a string into <code>application/x-www-form-urlencoded</code>
   * format using a specific encoding scheme. This method uses the
   * supplied encoding scheme to obtain the bytes for unsafe
   * characters.
   * <p>
   * <em><strong>Note:</strong> The <a href=
   * "http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars">
   * World Wide Web Consortium Recommendation</a> states that
   * UTF-8 should be used. Not doing so may introduce
   * incompatibilites.</em>
   *
   * @param   s   <code>String</code> to be translated.
   * @param   enc   The name of a supported 
   *    <a href="../lang/package-summary.html#charenc">character
   *    encoding</a>.
   * @return  the translated <code>String</code>.
   * @exception  UnsupportedEncodingException
   *             If the named encoding is not supported
   * @see java.net.URLDecoder#decode(java.lang.String, java.lang.String)
   */
  public static String encode(String s, String enc) throws UnsupportedEncodingException 
  {
	  boolean needToChange = false;
	  boolean wroteUnencodedChar = false; 
	  int maxBytesPerChar = 10; // rather arbitrary limit, but safe for now
    StringBuffer out = new StringBuffer(s.length());
	  ByteArrayOutputStream buf = new ByteArrayOutputStream(maxBytesPerChar);
	  BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(buf, enc));

	  for (int i = 0; i < s.length(); i++) 
    {
	    int c = s.charAt(i);
	    //System.out.println("Examining character: " + c);
	    if (dontNeedEncoding.get(c))
      {
		   //if (c == ' ')
       //{
		   //  c = '+';
		   //  needToChange = true;
		   //}
		   //System.out.println("Storing: " + c);
		   out.append((char)c);
		   wroteUnencodedChar = true;
	    }
      else
      {
		    // convert to external encoding before hex conversion
		    try
        {
		      if (wroteUnencodedChar) 
          { // Fix for 4407610
		    	  writer = new BufferedWriter(new OutputStreamWriter(buf, enc));
			      wroteUnencodedChar = false;
		      }
		      writer.write(c);
		        
		      // If this character represents the start of a Unicode
		      // surrogate pair, then pass in two characters. It's not
		      // clear what should be done if a bytes reserved in the 
		      // surrogate pairs range occurs outside of a legal
		      // surrogate pair. For now, just treat it as if it were 
		      // any other character.
		      // 
		      if (c >= 0xD800 && c <= 0xDBFF) 
          {
			      //  System.out.println(Integer.toHexString(c) + " is high surrogate");			      
			      if ( (i+1) < s.length()) 
            {
			        int d = s.charAt(i+1);
			        // System.out.println("\tExamining " + Integer.toHexString(d));			      
			        if (d >= 0xDC00 && d <= 0xDFFF) 
              {
				        // System.out.println("\t" + Integer.toHexString(d) + " is low surrogate");				
				        writer.write(d);
				        i++;
			        }
			      }
		      }
		      writer.flush();
		    } 
        catch(IOException e) 
        {
		      buf.reset();
		      continue;
		    }
		    byte[] ba = buf.toByteArray();

		    for (int j = 0; j < ba.length; j++) 
        {
		      out.append('%');
		      char ch = Character.forDigit((ba[j] >> 4) & 0xF, 16);
		      // converting to use uppercase letter as part of
		      // the hex value if ch is a letter.
		      if (Character.isLetter(ch)) 
          {
			      ch -= caseDiff;
		      }
		      out.append(ch);
		      ch = Character.forDigit(ba[j] & 0xF, 16);
		      if (Character.isLetter(ch)) 
          {
			      ch -= caseDiff;
		      }
		      out.append(ch);
		    }
		    buf.reset();
		    needToChange = true;
	    }
	  }
	  return (needToChange? out.toString() : s);
  }
}
