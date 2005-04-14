package org.eclipse.wst.common.frameworks.componentcore.tests;

import org.eclipse.core.resources.IProject;
import org.eclipse.wst.common.componentcore.internal.resources.FlexibleProject;
import org.eclipse.wst.common.componentcore.resources.IFlexibleProject;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

import junit.framework.TestCase;



public class IFlexibleProjectTest extends TestCase {
	public class FlexProjectTest implements IFlexibleProject {
		public FlexProjectTest() {
		}

		public IVirtualComponent[] getComponents() {
			return null;
		}

		public IVirtualComponent getComponent(String aComponentName) {
			return null;
		}

		public IProject getProject() {
			return null;
		}
	}

	public void testGetComponents() {
		IFlexibleProject flex = new FlexProjectTest();
		flex.getComponents();
	}

	public void testGetComponent() {
		IFlexibleProject flex = new FlexProjectTest();
		flex.getComponent(null);
	}

	public void testGetProject() {
		IFlexibleProject flex = new FlexProjectTest();
		flex.getProject();
	}
}
