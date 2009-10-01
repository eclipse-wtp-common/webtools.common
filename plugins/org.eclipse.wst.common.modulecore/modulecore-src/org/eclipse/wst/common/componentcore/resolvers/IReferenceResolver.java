package org.eclipse.wst.common.componentcore.resolvers;

import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

public interface IReferenceResolver {
	public boolean canResolve(IVirtualComponent context, ReferencedComponent referencedComponent);
	public IVirtualReference resolve(IVirtualComponent context, ReferencedComponent referencedComponent);
	public boolean canResolve(IVirtualReference reference);
	public ReferencedComponent resolve(IVirtualReference reference);
}
