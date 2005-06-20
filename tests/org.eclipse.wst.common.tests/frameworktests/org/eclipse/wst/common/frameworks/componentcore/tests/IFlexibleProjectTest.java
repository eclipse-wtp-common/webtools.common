package org.eclipse.wst.common.frameworks.componentcore.tests;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.componentcore.resources.IFlexibleProject;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;



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

		public boolean isFlexible() {
			// TODO Auto-generated method stub
			return false;
		}

		public void create(int theFlags, IProgressMonitor aMonitor) {
			// TODO Auto-generated method stub
		}
		
		public IVirtualComponent[] getComponentsOfType(String type) {
			// TODO Auto-generated method stub
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
