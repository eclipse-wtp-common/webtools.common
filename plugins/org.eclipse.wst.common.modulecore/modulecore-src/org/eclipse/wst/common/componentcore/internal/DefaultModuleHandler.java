package org.eclipse.wst.common.componentcore.internal;

import java.util.List;

import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

public class DefaultModuleHandler implements IModuleHandler {

	public String getArchiveName(IVirtualComponent comp) {
		return comp.getName() + ".jar";
	}

	public List<IVirtualComponent> getFilteredListForAdd(IVirtualComponent sourceComponent, IVirtualComponent[] availableComponents) {
		// TODO Auto-generated method stub
		return null;
	}

}
