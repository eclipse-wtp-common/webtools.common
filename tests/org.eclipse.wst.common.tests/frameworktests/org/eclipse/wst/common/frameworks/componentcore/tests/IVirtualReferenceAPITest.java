package org.eclipse.wst.common.frameworks.componentcore.tests;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualReference;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

public class IVirtualReferenceAPITest extends TestCase {

	public static void main(String[] args) {
	}

	public void testCreate() {
		IVirtualReference reference = new VirtualReference();
		reference.create(0,null);
		
	}

	public void testSetRuntimePath() {
		IVirtualReference reference = new VirtualReference();
		reference.setRuntimePath(new Path("/"));
	}

	public void testGetRuntimePath() {
		IVirtualReference reference = new VirtualReference();
		IPath path = reference.getRuntimePath();
	}

	public void testSetDependencyType() {
		IVirtualReference reference = new VirtualReference();
		int dependencyType = 0;
		reference.setDependencyType(dependencyType);
	}

	public void testGetDependencyType() {
		IVirtualReference reference = new VirtualReference();
		int dependencyType = 0;
		dependencyType = reference.getDependencyType();
	}

	public void testExists() {
		IVirtualReference reference = new VirtualReference();
		boolean exists = reference.exists();
	}

	public void testGetEnclosingComponent() {
		IVirtualReference reference = new VirtualReference();
		IVirtualComponent component = reference.getEnclosingComponent();
	}

	public void testGetReferencedComponent() {
		IVirtualReference reference = new VirtualReference();
		IVirtualComponent component = reference.getReferencedComponent();

	}

}
