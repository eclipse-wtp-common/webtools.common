package org.eclipse.wst.common.tests.flatten;

import java.util.Properties;

import org.eclipse.wst.common.componentcore.internal.flat.AbstractFlattenParticipant;
import org.eclipse.wst.common.componentcore.internal.flat.IFlattenParticipant;
import org.eclipse.wst.common.componentcore.internal.flat.IFlattenParticipantProvider;

public class ExampleFlattenParticipantProvider2 implements
		IFlattenParticipantProvider {

	public static final String DUMMY_PARTICIPANT = "example2";
	
	public static final String COMMON_KEY = "common";
	public IFlattenParticipant findParticipant(String id, Properties props) {
		if( DUMMY_PARTICIPANT.equals(id)) {
			return new Dummy2FlattenParticipant();
		}
		if( COMMON_KEY.equals(id))
			return new Dummy2FlattenParticipant();
		
		return null;
	}
	
	public static class Dummy2FlattenParticipant extends AbstractFlattenParticipant {
		
	}

}
