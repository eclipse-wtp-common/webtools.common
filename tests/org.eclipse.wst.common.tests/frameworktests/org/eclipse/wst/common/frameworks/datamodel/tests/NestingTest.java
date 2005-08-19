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
package org.eclipse.wst.common.frameworks.datamodel.tests;

import java.util.Collection;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.WTPResourceHandler;

/**
 * @author jsholl
 * 
 * TODO To change the template for this generated type comment go to Window - Preferences - Java -
 * Code Style - Code Templates
 */
public class NestingTest extends TestCase {

	private IDataModel a;
	private IDataModel b;
	private IDataModel c;

	protected void setUp() throws Exception {
		super.setUp();
		a = DataModelFactory.createDataModel(new A());
		b = DataModelFactory.createDataModel(new B());
		c = DataModelFactory.createDataModel(new C());
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

	public void testInvalidNestedModel() {
		String NESTED_MODEL_NOT_LOCATED = WTPResourceHandler.getString("21"); //$NON-NLS-1$
		Exception ex = null;
		try {
			a.getNestedModel("foo");
		} catch (RuntimeException e) {
			ex = e;
		}
		Assert.assertNotNull(ex);
		Assert.assertTrue(ex.getMessage().startsWith(NESTED_MODEL_NOT_LOCATED));
		ex = null;
		try {
			a.getNestedModel(null);
		} catch (RuntimeException e) {
			ex = e;
		}
		Assert.assertNotNull(ex);
		Assert.assertTrue(ex.getMessage().startsWith(NESTED_MODEL_NOT_LOCATED));

		a.addNestedModel("b", b);
		ex = null;
		try {
			a.getNestedModel(null);
		} catch (RuntimeException e) {
			ex = e;
		}
		Assert.assertNotNull(ex);
		Assert.assertTrue(ex.getMessage().startsWith(NESTED_MODEL_NOT_LOCATED));

	}

	public void testIsNestedModel() {
		Assert.assertFalse(a.isNestedModel(""));
		Assert.assertFalse(a.isNestedModel(null));
		a.addNestedModel("b", b);
		Assert.assertTrue(a.isNestedModel("b"));
		Assert.assertFalse(a.isNestedModel("c"));
		a.addNestedModel("c", c);
		Assert.assertTrue(a.isNestedModel("c"));
	}

	public void testRemoveNonExistentModels() {
		Assert.assertNull(a.removeNestedModel("a"));
		Assert.assertNull(a.removeNestedModel(null));
	}

	public void testGetNestedAndGetNesting() {
		Assert.assertEquals(0, a.getNestedModels().size());
		Assert.assertEquals(0, b.getNestingModels().size());

		Assert.assertTrue(a.addNestedModel("b", b));
		Assert.assertEquals(1, a.getNestedModels().size());
		Assert.assertTrue(a.getNestedModels().contains(b));
		Assert.assertEquals(b, a.getNestedModel("b"));
		Assert.assertEquals(1, b.getNestingModels().size());
		Assert.assertTrue(b.getNestingModels().contains(a));

		Assert.assertTrue(a.addNestedModel("c", c));
		Assert.assertEquals(2, a.getNestedModels().size());
		Assert.assertTrue(a.getNestedModels().contains(c));
		Assert.assertEquals(c, a.getNestedModel("c"));
		Assert.assertEquals(1, c.getNestingModels().size());
		Assert.assertTrue(c.getNestingModels().contains(a));

		Assert.assertTrue(b.addNestedModel("c", c));
		Assert.assertEquals(1, b.getNestedModels().size());
		Assert.assertTrue(b.getNestedModels().contains(c));
		Assert.assertEquals(2, c.getNestingModels().size());
		Assert.assertTrue(c.getNestingModels().contains(b));

		Assert.assertEquals(b, a.removeNestedModel("b"));
		Assert.assertEquals(1, a.getNestedModels().size());
		Assert.assertEquals(0, b.getNestingModels().size());

		Assert.assertEquals(c, a.removeNestedModel("c"));
		Assert.assertEquals(0, a.getNestedModels().size());
		Assert.assertEquals(1, c.getNestingModels().size());
		Assert.assertTrue(c.getNestingModels().contains(b));

		Assert.assertEquals(c, b.removeNestedModel("c"));
		Assert.assertEquals(0, b.getNestedModels().size());
		Assert.assertEquals(0, c.getNestingModels().size());
	}

	public void testSelfNest() {
		Assert.assertFalse(a.addNestedModel("a", a));
		Assert.assertEquals(0, a.getNestedModels().size());
	}

	public void testDuplicateNest() {
		Assert.assertTrue(a.addNestedModel("b1", b));
		Assert.assertEquals(b, a.getNestedModel("b1"));
		Assert.assertFalse(a.addNestedModel("b2", b));
		Assert.assertEquals(b, a.getNestedModel("b1"));
		Assert.assertFalse(a.isNestedModel("b2"));
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
		Assert.assertFalse(a.isBaseProperty(B.P));
		Assert.assertTrue(a.isProperty(B.P));
		Assert.assertTrue(a.isNestedProperty(B.P));

		a.removeNestedModel("b"); // 2
		Assert.assertFalse(a.isBaseProperty(B.P));
		Assert.assertFalse(a.isProperty(B.P));
		Assert.assertFalse(a.isNestedProperty(B.P));

		a.addNestedModel("b", b); // 3
		Assert.assertFalse(a.isBaseProperty(B.P));
		Assert.assertTrue(a.isProperty(B.P));
		Assert.assertTrue(a.isNestedProperty(B.P));

		b.addNestedModel("c", c); // 4
		Assert.assertFalse(a.isBaseProperty(C.P));
		Assert.assertTrue(a.isProperty(C.P));
		Assert.assertTrue(a.isNestedProperty(C.P));

		b.removeNestedModel("c"); // 5
		Assert.assertFalse(a.isBaseProperty(C.P));
		Assert.assertFalse(a.isProperty(C.P));
		Assert.assertFalse(a.isNestedProperty(C.P));

		b.addNestedModel("c", c); // 6
		Assert.assertFalse(a.isBaseProperty(C.P));
		Assert.assertTrue(a.isProperty(C.P));
		Assert.assertTrue(a.isNestedProperty(C.P));
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
		Assert.assertFalse(a.isBaseProperty(B.P));
		Assert.assertTrue(a.isProperty(B.P));
		Assert.assertTrue(a.isNestedProperty(B.P));
		// check a's nested
		Assert.assertTrue(b == a.getNestedModel("b"));
		Collection nestedModelNames = a.getNestedModelNames();
		Assert.assertEquals(1, nestedModelNames.size());
		Assert.assertTrue(nestedModelNames.contains("b"));
		Collection baseProperties = a.getBaseProperties();
		Assert.assertEquals(3, baseProperties.size());
		Assert.assertTrue(baseProperties.contains(A.P));
		Collection nestedProperties = a.getNestedProperties();
		Assert.assertEquals(3, nestedProperties.size());
		Assert.assertTrue(nestedProperties.contains(B.P));
		Collection allProperties = a.getAllProperties();
		Assert.assertEquals(4, allProperties.size());
		Assert.assertTrue(allProperties.contains(A.P));
		Assert.assertTrue(allProperties.contains(B.P));


		b.addNestedModel("c", c); // 2
		Assert.assertFalse(a.isBaseProperty(C.P));
		Assert.assertTrue(a.isProperty(C.P));
		Assert.assertTrue(a.isNestedProperty(C.P));
		Assert.assertFalse(b.isBaseProperty(C.P));
		Assert.assertTrue(b.isProperty(C.P));
		Assert.assertTrue(b.isNestedProperty(C.P));
		// check a's nested
		Assert.assertTrue(b == a.getNestedModel("b"));
		nestedModelNames = a.getNestedModelNames();
		Assert.assertEquals(1, nestedModelNames.size());
		Assert.assertTrue(nestedModelNames.contains("b"));
		baseProperties = a.getBaseProperties();
		Assert.assertEquals(3, baseProperties.size());
		Assert.assertTrue(baseProperties.contains(A.P));
		nestedProperties = a.getNestedProperties();
		Assert.assertEquals(4, nestedProperties.size());
		Assert.assertTrue(nestedProperties.contains(B.P));
		Assert.assertTrue(nestedProperties.contains(C.P));
		allProperties = a.getAllProperties();
		Assert.assertEquals(5, allProperties.size());
		Assert.assertTrue(allProperties.contains(A.P));
		Assert.assertTrue(allProperties.contains(B.P));
		Assert.assertTrue(allProperties.contains(C.P));
		// check b's nested
		Assert.assertTrue(c == b.getNestedModel("c"));
		nestedModelNames = b.getNestedModelNames();
		Assert.assertEquals(1, nestedModelNames.size());
		Assert.assertTrue(nestedModelNames.contains("c"));
		baseProperties = b.getBaseProperties();
		Assert.assertEquals(3, baseProperties.size());
		Assert.assertTrue(baseProperties.contains(B.P));
		nestedProperties = b.getNestedProperties();
		Assert.assertEquals(3, nestedProperties.size());
		Assert.assertTrue(nestedProperties.contains(C.P));
		allProperties = b.getAllProperties();
		Assert.assertEquals(4, allProperties.size());
		Assert.assertTrue(allProperties.contains(B.P));
		Assert.assertTrue(allProperties.contains(C.P));

		b.removeNestedModel("c"); // 3
		Assert.assertFalse(a.isBaseProperty(C.P));
		Assert.assertFalse(a.isProperty(C.P));
		Assert.assertFalse(a.isNestedProperty(C.P));
		Assert.assertFalse(b.isBaseProperty(C.P));
		Assert.assertFalse(b.isProperty(C.P));
		Assert.assertFalse(b.isNestedProperty(C.P));
		// check a's nested
		Assert.assertTrue(b == a.getNestedModel("b"));
		nestedModelNames = a.getNestedModelNames();
		Assert.assertEquals(1, nestedModelNames.size());
		Assert.assertTrue(nestedModelNames.contains("b"));
		baseProperties = a.getBaseProperties();
		Assert.assertEquals(3, baseProperties.size());
		Assert.assertTrue(baseProperties.contains(A.P));
		nestedProperties = a.getNestedProperties();
		Assert.assertEquals(3, nestedProperties.size());
		Assert.assertTrue(nestedProperties.contains(B.P));
		allProperties = a.getAllProperties();
		Assert.assertEquals(4, allProperties.size());
		Assert.assertTrue(allProperties.contains(A.P));
		Assert.assertTrue(allProperties.contains(B.P));
		// check b's nested
		nestedModelNames = b.getNestedModelNames();
		Assert.assertEquals(0, nestedModelNames.size());
		baseProperties = b.getBaseProperties();
		Assert.assertEquals(3, baseProperties.size());
		Assert.assertTrue(baseProperties.contains(B.P));
		nestedProperties = b.getNestedProperties();
		Assert.assertEquals(2, nestedProperties.size());
		allProperties = b.getAllProperties();
		Assert.assertEquals(3, allProperties.size());
		Assert.assertTrue(allProperties.contains(B.P));

		a.removeNestedModel("b"); // 4
		Assert.assertFalse(a.isBaseProperty(B.P));
		Assert.assertFalse(a.isProperty(B.P));
		Assert.assertFalse(a.isNestedProperty(B.P));
		// check a's nested
		nestedModelNames = a.getNestedModelNames();
		Assert.assertEquals(0, nestedModelNames.size());
		baseProperties = a.getBaseProperties();
		Assert.assertEquals(3, baseProperties.size());
		Assert.assertTrue(baseProperties.contains(A.P));
		nestedProperties = a.getNestedProperties();
		Assert.assertEquals(2, nestedProperties.size());
		allProperties = a.getAllProperties();
		Assert.assertEquals(3, allProperties.size());
		Assert.assertTrue(allProperties.contains(A.P));
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
		IDataModel c2 = DataModelFactory.createDataModel(new C());
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
		IDataModel c2 = DataModelFactory.createDataModel(new C());
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


}