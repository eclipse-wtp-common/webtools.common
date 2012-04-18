package org.eclipse.wst.common.tests.flatten;

import java.util.Properties;

import org.eclipse.wst.common.componentcore.internal.flat.AbstractFlattenParticipant;
import org.eclipse.wst.common.componentcore.internal.flat.IFlattenParticipant;
import org.eclipse.wst.common.componentcore.internal.flat.IFlattenParticipantProvider;

public class ExampleFlattenParticipantProvider implements
		IFlattenParticipantProvider {

	public static final String DUMMY_PARTICIPANT = "example1";
	public IFlattenParticipant findParticipant(String id, Properties props) {
		if( DUMMY_PARTICIPANT.equals(id)) {
			return new DummyFlattenParticipant();
		}
		return null;
	}
	
	public static class DummyFlattenParticipant extends AbstractFlattenParticipant {
		
	}

}
