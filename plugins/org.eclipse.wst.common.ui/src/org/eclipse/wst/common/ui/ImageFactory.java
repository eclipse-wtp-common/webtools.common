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
package org.eclipse.wst.common.ui;

import java.util.Hashtable;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.internal.ide.misc.OverlayIcon;
import org.eclipse.ui.internal.misc.OverlayComposite;

public class ImageFactory
{                                
  public static final String copyright = "(c) Copyright IBM Corporation 2000, 2002.";
  public static final int TOP_LEFT = 1;
  public static final int TOP_RIGHT = 2;
  public static final int BOTTOM_LEFT = 3;
  public static final int BOTTOM_RIGHT = 4;     

  protected static final int[][] OVERLAY_COORDINATE = {{0,0},{0,2},{2,0},{2,2}};

  protected Hashtable compositeImageTable = new Hashtable();

  public ImageFactory()
  {
  }

  public Image createCompositeImage(Image a, Image b)
  {
    String key = a + "*" + b;
    Image result = (Image)compositeImageTable.get(key);
    if (result == null)
    {
      OverlayComposite composite = new OverlayComposite(a.getImageData());
      composite.addForegroundImage(b.getImageData());
      result = composite.createImage();
      compositeImageTable.put(key, result);
    }
    return result;
  }

  public Image createCompositeImage(Image base, Image overlay, int overlayPosition)
  {                                                   
    String key = base + "*" + overlay + "*" + overlayPosition;
    Image result = (Image)compositeImageTable.get(key);
    if (result == null)
    {
      ImageDescriptor overlays[][] = new ImageDescriptor[3][3]; 
      int[] coord = OVERLAY_COORDINATE[overlayPosition];
      overlays[coord[1]][coord[0]] = new ImageBasedImageDescriptor(overlay);
      OverlayIcon icon = new OverlayIcon(new ImageBasedImageDescriptor(base), overlays, new Point(16, 16));
      result = icon.createImage();
      compositeImageTable.put(key, result);
    }
    return result;
  }  


  public static ImageDescriptor createImageDescriptorWrapper(Image image)
  {
    return new ImageBasedImageDescriptor(image);
  }


  protected static class ImageBasedImageDescriptor extends ImageDescriptor
  {
    protected Image image;

    public ImageBasedImageDescriptor(Image image)
    {
      this.image = image;
    }                    

    public ImageData getImageData()
    {
      return image.getImageData();
    }
  }  
}

