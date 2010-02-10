package org.eclipse.wst.common.uriresolver.tests;

import org.eclipse.wst.common.uriresolver.internal.ExtensibleURIResolver;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {

	public static Test suite() {
		TestSuite testSuite = new TestSuite("All URI Resolver Tests");
		testSuite.addTestSuite(ExtensibleURIResolverTest.class);
		return testSuite;
	}
}
