/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $RCSfile: IMsgLogger.java,v $
 *  $Revision: 1.2 $  $Date: 2005/02/15 23:04:14 $ 
 */
package org.eclipse.jem.util.logger.proxyrender;

import org.eclipse.jem.util.logger.proxy.Logger;

/**
 * Interface for a message logger.
 * 
 * @since 1.0.0
 */
public interface IMsgLogger {
	public Logger getMsgLogger();
}
