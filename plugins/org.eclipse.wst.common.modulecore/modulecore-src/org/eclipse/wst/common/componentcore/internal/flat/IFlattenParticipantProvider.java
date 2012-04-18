/*******************************************************************************
 * Copyright (c) 2012 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.flat;

import java.util.Properties;

public interface IFlattenParticipantProvider {
	
	/**
	 * If this provider can find a participant for this specific
	 * id, it should do so now. Otherwise, return null. 
	 * 
	 * @param id A flatten participant id
	 * @param properties A list of properties to assist
	 * @return
	 */
	public IFlattenParticipant findParticipant(String id, Properties properties);
}
