/*
 * Created on Mar 31, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.wst.common.frameworks.artifactedit.tests;



import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.common.tests.SimpleTestSuite;

public class ArtifactEditAPITests extends TestSuite {
	
	
	
	public static Test suite() {
		return new ArtifactEditAPITests();
	}

	public ArtifactEditAPITests() {
		super();
		addTest(new SimpleTestSuite(ArtifactEditTest.class));
	}

}
