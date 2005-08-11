/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.operations.tests;

import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.operations.WTPDataModelBridgeProvider;
import org.eclipse.wst.common.frameworks.internal.operations.WTPOperationDataModel;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author jsholl
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class NestingTest extends TestCase {

	private A a;
	private B b;
	private C c;

	protected void setUp() throws Exception {
		super.setUp();
		a = new A();
		b = new B();
		c = new C();
	}

	protected void tearDown() throws Exception {
		if (null != a) {
			a.dispose();
			a = null;
		}
		if (null != b) {
			b.dispose();
			b = null;
		}

		if (null != c) {
			c.dispose();
			c = null;
		}
	}


	/**
	 * <code>
	 * 1    2   3    4     5    6
	 * A  | A | A  | A   | A  | A
	 *  B |   |  B |  B  |  B |  B
	 *                 C |    |   C
	 * </code>
	 */
	public void testIsPropertySimpleNesting0() {
		a.addNestedModel("b", b); // 1
		Assert.assertTrue(a.isProperty(B.P));

		a.removeNestedModel("b"); // 2
		Assert.assertFalse(a.isProperty(B.P));

		a.addNestedModel("b", b); // 3
		Assert.assertTrue(a.isProperty(B.P));

		b.addNestedModel("c", c); // 4
		Assert.assertTrue(a.isProperty(C.P));

		b.removeNestedModel("c"); // 5
		Assert.assertFalse(a.isProperty(C.P));

		b.addNestedModel("c", c); // 6
		Assert.assertTrue(a.isProperty(C.P));
	}

	/**
	 * <code>
	 * 1    2     3    4
	 * A  | A   | A  | A
	 *  B |  B  |  B |
	 *        C |    |
	 * </code>
	 */
	public void testIsPropertySimpleNesting1() {
		a.addNestedModel("b", b); // 1
		Assert.assertTrue(a.isProperty(B.P));

		b.addNestedModel("c", c); // 2
		Assert.assertTrue(a.isProperty(C.P));
		Assert.assertTrue(b.isProperty(C.P));

		b.removeNestedModel("c"); // 3
		Assert.assertFalse(a.isProperty(C.P));
		Assert.assertFalse(b.isProperty(C.P));

		a.removeNestedModel("b"); // 4
		Assert.assertFalse(a.isProperty(B.P));
	}

	/**
	 * <code>
	 * 1    2     3    4 
	 * A  | A   | A  | A
	 *    |  B  |    | 
	 * B  |   C | B  | B
	 *  C |     |  C | 
	 *          |    | C
	 * </code>
	 */
	public void testIsPropertySimpleNesting2() {
		b.addNestedModel("c", c); // 1
		Assert.assertTrue(b.isProperty(C.P));

		a.addNestedModel("b", b); // 2
		Assert.assertTrue(a.isProperty(B.P));
		Assert.assertTrue(a.isProperty(C.P));

		a.removeNestedModel("b"); // 3
		Assert.assertFalse(a.isProperty(B.P));
		Assert.assertFalse(a.isProperty(C.P));

		b.removeNestedModel("c"); // 4
		Assert.assertFalse(b.isProperty(C.P));
	}

	/**
	 * <code>
	 * 1    2     3      4     5
	 * A  | A   | A    | A   | A
	 *  B |  B  |  B   |  B  |  B
	 *        C |   C  |   C |
	 *          |   C2 |
	 * </code>
	 */
	public void testIsPropertyComplexNesting1() {
		a.addNestedModel("b", b); // 1
		b.addNestedModel("c", c); // 2
		Assert.assertTrue(a.isProperty(C.P));
		C c2 = new C();
		b.addNestedModel("c2", c2); // 3
		b.removeNestedModel("c2"); // 4
		Assert.assertTrue(b.isProperty(C.P));
		Assert.assertTrue(a.isProperty(C.P));
		b.removeNestedModel("c"); // 5
		Assert.assertFalse(b.isProperty(C.P));
		Assert.assertFalse(a.isProperty(C.P));
	}

	/**
	 * <code>
	 * 1    2     3     4     5      6      7
	 * A  | A   | A   | A   | A    | A    | A
	 *  B |  B  |  B  |  B  |  B   |  B   |  B
	 *        C |   C |   C |   C  |   C2 |
	 *             C2 |         C2 |
	 * </code>
	 */
	public void testIsPropertyComplexNesting2() {
		a.addNestedModel("b", b); // 1
		b.addNestedModel("c", c); // 2
		Assert.assertTrue(a.isProperty(C.P));
		C c2 = new C();
		a.addNestedModel("c2", c2); // 3
		a.removeNestedModel("c2"); // 4
		Assert.assertTrue(a.isProperty(C.P));
		b.addNestedModel("c2", c2); // 5
		Assert.assertTrue(a.isProperty(C.P));
		b.removeNestedModel("c"); // 6
		Assert.assertTrue(a.isProperty(C.P));
		b.removeNestedModel("c2"); // 7
		Assert.assertFalse(a.isProperty(C.P));
	}

	/**
	 * <code>
	 * 1     2             3      4             5     6
	 * A   | A           | B    | A          |  C   | A
	 *  B  |  B          |  C   |  B         |   A  | B
	 *   C |   C         |   A  |   C        |    B | C
	 *          A (loop) |      |    A(loop)
	 * </code>
	 */
	public void testIsPropertyComplexNesting3() {
		a.addNestedModel("b", b);
		b.addNestedModel("c", c); // 1
		Assert.assertTrue(a.isProperty(B.P));
		Assert.assertTrue(a.isProperty(C.P));
		Assert.assertFalse(b.isProperty(A.P));
		Assert.assertTrue(b.isProperty(C.P));
		Assert.assertFalse(c.isProperty(A.P));
		Assert.assertFalse(c.isProperty(B.P));
		Assert.assertFalse(a.isProperty("foo"));
		Assert.assertFalse(b.isProperty("foo"));
		Assert.assertFalse(c.isProperty("foo"));
		c.addNestedModel("a", a); // 2
		Assert.assertTrue(a.isProperty(B.P));
		Assert.assertTrue(a.isProperty(C.P));
		Assert.assertTrue(b.isProperty(A.P));
		Assert.assertTrue(b.isProperty(C.P));
		Assert.assertTrue(c.isProperty(A.P));
		Assert.assertTrue(c.isProperty(B.P));
		Assert.assertFalse(a.isProperty("foo"));
		Assert.assertFalse(b.isProperty("foo"));
		Assert.assertFalse(c.isProperty("foo"));
		a.removeNestedModel("b"); // 3
		Assert.assertFalse(a.isProperty(B.P));
		Assert.assertFalse(a.isProperty(C.P));
		Assert.assertTrue(b.isProperty(A.P));
		Assert.assertTrue(b.isProperty(C.P));
		Assert.assertTrue(c.isProperty(A.P));
		Assert.assertFalse(c.isProperty(B.P));
		Assert.assertFalse(a.isProperty("foo"));
		Assert.assertFalse(b.isProperty("foo"));
		Assert.assertFalse(c.isProperty("foo"));
		a.addNestedModel("b", b); // 4
		Assert.assertTrue(a.isProperty(B.P));
		Assert.assertTrue(a.isProperty(C.P));
		Assert.assertTrue(b.isProperty(A.P));
		Assert.assertTrue(b.isProperty(C.P));
		Assert.assertTrue(c.isProperty(A.P));
		Assert.assertTrue(c.isProperty(B.P));
		Assert.assertFalse(a.isProperty("foo"));
		Assert.assertFalse(b.isProperty("foo"));
		Assert.assertFalse(c.isProperty("foo"));
		b.removeNestedModel("c"); // 5
		Assert.assertTrue(a.isProperty(B.P));
		Assert.assertFalse(a.isProperty(C.P));
		Assert.assertFalse(b.isProperty(A.P));
		Assert.assertFalse(b.isProperty(C.P));
		Assert.assertTrue(c.isProperty(A.P));
		Assert.assertTrue(c.isProperty(B.P));
		Assert.assertFalse(a.isProperty("foo"));
		Assert.assertFalse(b.isProperty("foo"));
		Assert.assertFalse(c.isProperty("foo"));
		c.removeNestedModel("a");
		a.removeNestedModel("b");
		Assert.assertFalse(a.isProperty(B.P));
		Assert.assertFalse(a.isProperty(C.P));
		Assert.assertFalse(b.isProperty(A.P));
		Assert.assertFalse(b.isProperty(C.P));
		Assert.assertFalse(c.isProperty(A.P));
		Assert.assertFalse(c.isProperty(B.P));
		Assert.assertFalse(a.isProperty("foo"));
		Assert.assertFalse(b.isProperty("foo"));
		Assert.assertFalse(c.isProperty("foo"));
	}

	public void testSetGetProperty1() {
		// cylical
		a.addNestedModel("b", b);
		b.addNestedModel("c", c);
		c.addNestedModel("a", a);

		a.setProperty(A.P, "a");
		a.setProperty(B.P, "b");
		a.setProperty(C.P, "c");
		assertEquals("a", a.getProperty(A.P));
		assertEquals("a", b.getProperty(A.P));
		assertEquals("a", c.getProperty(A.P));
		assertEquals("b", a.getProperty(B.P));
		assertEquals("b", b.getProperty(B.P));
		assertEquals("b", c.getProperty(B.P));
		assertEquals("c", a.getProperty(C.P));
		assertEquals("c", b.getProperty(C.P));
		assertEquals("c", c.getProperty(C.P));

		b.setProperty(A.P, "aa");
		b.setProperty(B.P, "bb");
		b.setProperty(C.P, "cc");
		assertEquals("aa", a.getProperty(A.P));
		assertEquals("aa", b.getProperty(A.P));
		assertEquals("aa", c.getProperty(A.P));
		assertEquals("bb", a.getProperty(B.P));
		assertEquals("bb", b.getProperty(B.P));
		assertEquals("bb", c.getProperty(B.P));
		assertEquals("cc", a.getProperty(C.P));
		assertEquals("cc", b.getProperty(C.P));
		assertEquals("cc", c.getProperty(C.P));

		c.setProperty(A.P, "aaa");
		c.setProperty(B.P, "bbb");
		c.setProperty(C.P, "ccc");
		assertEquals("aaa", a.getProperty(A.P));
		assertEquals("aaa", b.getProperty(A.P));
		assertEquals("aaa", c.getProperty(A.P));
		assertEquals("bbb", a.getProperty(B.P));
		assertEquals("bbb", b.getProperty(B.P));
		assertEquals("bbb", c.getProperty(B.P));
		assertEquals("ccc", a.getProperty(C.P));
		assertEquals("ccc", b.getProperty(C.P));
		assertEquals("ccc", c.getProperty(C.P));
	}
	
	public void testNestingOlderUnderNew() throws Exception{
		IDataModel aDM = DataModelFactory.createDataModel(new org.eclipse.wst.common.frameworks.datamodel.tests.A());
		IDataModel bDM = DataModelFactory.createDataModel(new WTPDataModelBridgeProvider(){
			protected WTPOperationDataModel initWTPDataModel() {
				B b = new B();
				b.addNestedModel("c", new C());
				return b;
			}
		});
		aDM.addNestedModel("b", bDM);
		aDM.setProperty(org.eclipse.wst.common.frameworks.datamodel.tests.A.P, "aaa");
		aDM.setProperty(B.P, "bbb");
		aDM.setProperty(C.P, "ccc");
		
		
	}


}