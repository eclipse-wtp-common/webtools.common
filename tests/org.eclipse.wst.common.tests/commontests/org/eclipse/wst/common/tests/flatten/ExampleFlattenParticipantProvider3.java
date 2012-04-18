package org.eclipse.wst.common.tests.flatten;

import java.util.Properties;

import org.eclipse.wst.common.componentcore.internal.flat.AbstractFlattenParticipant;
import org.eclipse.wst.common.componentcore.internal.flat.IFlattenParticipant;
import org.eclipse.wst.common.componentcore.internal.flat.IFlattenParticipantProvider;

public class ExampleFlattenParticipantProvider3 implements
		IFlattenParticipantProvider {

	public static final String DUMMY_PARTICIPANT = "example3";
	public static final String COMMON_KEY = ExampleFlattenParticipantProvider2.COMMON_KEY;
	public IFlattenParticipant findParticipant(String id, Properties props) {
		if( DUMMY_PARTICIPANT.equals(id))
			return new Dummy3FlattenParticipant();
		if( COMMON_KEY.equals(id))
			return new Dummy3FlattenParticipant();
		return null;
	}
	
	public static class Dummy3FlattenParticipant extends AbstractFlattenParticipant {
		
	}

}
