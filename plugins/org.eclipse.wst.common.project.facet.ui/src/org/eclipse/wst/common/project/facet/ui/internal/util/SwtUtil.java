/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.ui.internal.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class SwtUtil
{
    public static final int getPreferredWidth( final Control control )
    {
        return control.computeSize( SWT.DEFAULT, SWT.DEFAULT ).x;
    }

}
