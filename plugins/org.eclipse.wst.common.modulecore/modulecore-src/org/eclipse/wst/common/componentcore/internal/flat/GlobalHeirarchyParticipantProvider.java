/*******************************************************************************
 * Copyright (c) 2013 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Red Hat - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.flat;

import java.util.Properties;

/**
 * This participant provider may be accessed by any module-core project
 * making use of the flat virtual component model. 
 * 
 * It may be directly from a FlatComponentDeployable via 
 * FlatComponentDepoyable.getParticipantIds() by adding 
 * GlobalHeirarchyParticipantProvider.GLOBAL_HEIRARCHY_PARTICIPANT
 * as one such id. 
 * 
 * For modules which prefer to customize the behavior based on whether
 * the flatten task is being called from a publish or from an export, 
 * they may use their own IFlattenParticipantProvider to return 
 * a GlobalHeirarchyParticipant directly. 
 * 
 */
public class GlobalHeirarchyParticipantProvider implements
		IFlattenParticipantProvider {
	public static final String GLOBAL_HEIRARCHY_PARTICIPANT = "globalHeirarchyParticipant";
	
	public IFlattenParticipant findParticipant(String id, Properties properties) {
		if( GLOBAL_HEIRARCHY_PARTICIPANT.equals(id)) {
			return new GlobalHeirarchyParticipant();
		}
		return null;
	}

}
