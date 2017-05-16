/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.tests.flatten;

import junit.framework.TestCase;

import org.eclipse.wst.common.componentcore.internal.flat.FlattenParticipantModel;
import org.eclipse.wst.common.componentcore.internal.flat.IFlattenParticipant;


public class FlattenParticipantProviderTest extends TestCase {

    public FlattenParticipantProviderTest(String name) {
        super(name);
    }

    public void testNotFoundParticipantFound() {
    	assertNull(searchModel("NOT FOUND"));
    }

    public void testExample1ParticipantFound() {
    	IFlattenParticipant fp = searchModel(ExampleFlattenParticipantProvider.DUMMY_PARTICIPANT);
    	assertNotNull(fp);
    	assertTrue(fp.getClass().getName().endsWith("DummyFlattenParticipant"));
    }

    public void testExample2ParticipantFound() {
    	IFlattenParticipant fp = searchModel(ExampleFlattenParticipantProvider2.DUMMY_PARTICIPANT);
    	assertNotNull(fp);
    	assertTrue(fp.getClass().getName().endsWith("Dummy2FlattenParticipant"));
    }

    public void testExample3ParticipantFound() {
    	IFlattenParticipant fp = searchModel(ExampleFlattenParticipantProvider3.DUMMY_PARTICIPANT);
    	assertNotNull(fp);
    	assertTrue(fp.getClass().getName().endsWith("Dummy3FlattenParticipant"));
    }

    /**
     * This test ensures that the weighting is done properly.
     * providers with a higher weight should be consulted first.
     * Weight 10 should be asked before weight 0
     */
    public void testCommonParticipantFound() {
    	IFlattenParticipant fp = searchModel(ExampleFlattenParticipantProvider2.COMMON_KEY);
    	assertNotNull(fp);
    	assertTrue(fp.getClass().getName().endsWith("Dummy3FlattenParticipant"));
    }

    private IFlattenParticipant searchModel(String id) {
    	return FlattenParticipantModel.getDefault().getParticipant(id);
    }
}
