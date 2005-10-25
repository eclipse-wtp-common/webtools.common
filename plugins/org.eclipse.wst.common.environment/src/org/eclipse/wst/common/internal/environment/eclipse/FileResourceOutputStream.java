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
package org.eclipse.wst.common.internal.environment.eclipse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.common.environment.IStatusHandler;



/**
* This class defines an output stream that writes to an
* {@link org.eclipse.core.resources.IFile IFile}.
*/
public class FileResourceOutputStream extends OutputStream
{

  IPath                 fFilePath;
  IStatusHandler         fStatusHandler;
  ByteArrayOutputStream fByteArrayOutputStream;
  boolean               fOpen;

  /**
  * Constructs an output stream that can be used to write to the
  * given <code>file</code>. The workspace is neither examined nor
  * altered during construction. All resource creation is deferred
  * until later in the stream's lifecycle, such as during writing
  * or closure.
  * <b>Note:</b> The preferred mechanism for creating a stream that
  * writes to an IFile is to call the static factory method
  * {@link FileResourceUtils#newFileOutputStream FileResourceUtils.newFileOutputStream()}
  * and work with the resulting <code>OutputStream</code>.
  * Direct construction of a FileResourceOutputStream is not recommended.
  * @param file The {@link org.eclipse.core.resources.IFile IFile}
  * handle of the file resource to create. The project implied by the
  * pathname of the file must already exist,
  * that is, this method cannot be used to create projects.
  * @param progressMonitor The progress monitor for the operation, or null.
  */
  public FileResourceOutputStream ( 
  			IPath           filePath, 
  			IStatusHandler   statusHandler
  			)
  {
    fFilePath = filePath;
    fStatusHandler = statusHandler;
    fByteArrayOutputStream = new ByteArrayOutputStream();
    fOpen = true;
  }

  /**
  * Closes the stream.
  * @throws IOException If an error occurs while closing the stream.
  * For example, if this stream was constructed with overwriteFile = false
  * and a file of the same name already exists, then an IOException will
  * be thrown either now or during an earlier {@link #write write}.
  */
  public void close ()
  throws IOException
  {
    if (!fOpen) return;
    fOpen = false;
    fByteArrayOutputStream.close();
    byte[] buffer = fByteArrayOutputStream.toByteArray();
    ByteArrayInputStream tempInputStream = new ByteArrayInputStream(buffer);
    try
    {
      FileResourceUtils.createFile(fFilePath, tempInputStream, fStatusHandler);
    }
    catch (CoreException e)
    {
      throw new IOException(e.getMessage());
    }
  }

  /**
  * Flushes the stream. This does not imply the File resource
  * will be created or become visible within the workbench.
  * @throws IOException If an error occurs. For example, if this
  * stream was constructed with overwriteFile = false and a file of the
  * same name already exists, then an IOException may be thrown at
  * this point.
  */
  public void flush ()
  throws IOException
  {
    fByteArrayOutputStream.flush();
  }

  /**
  * Writes all bytes from the given array to the stream.
  * @param b The array of bytes to write.
  * @throws IOException If an error occurs. For example, if this
  * stream was constructed with overwriteFile = false and a file of the
  * same name already exists, then an IOException may be thrown at
  * this point.
  */
  public void write ( byte[] b )
  throws IOException
  {
    fByteArrayOutputStream.write(b);
  }

  /**
  * Writes bytes from the given array beginning at some offset
  * and continuing for some number of bytes (len) to the stream.
  * @param b The array of bytes to write.
  * @param off The offset into the array to begin writing.
  * @param len The number of bytes to write.
  * @throws IOException If an error occurs. For example, if this
  * stream was constructed with overwriteFile = false and a file of the
  * same name already exists, then an IOException may be thrown at
  * this point.
  */
  public void write ( byte[] b, int off, int len )
  {
    fByteArrayOutputStream.write(b,off,len);
  }

  /**
  * Writes a single byte to the stream.
  * @param b The byte to write.
  * @throws IOException If an error occurs. For example, if this
  * stream was constructed with overwriteFile = false and a file of the
  * same name already exists, then an IOException may be thrown at
  * this point.
  */
  public void write ( int b )
  {
    fByteArrayOutputStream.write(b);
  }
}

