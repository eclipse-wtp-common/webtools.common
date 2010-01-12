/*******************************************************************************
 * Copyright (c) 2009 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.flat;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.common.componentcore.internal.flat.FlatVirtualComponent.FlatComponentTaskModel;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

public class FilterResourceParticipant extends AbstractFlattenParticipant {
	public interface IExportableResourceFilter {
		public boolean accepts(IFlatResource resource);
	}
	
	public static class FilterExtensionsParticipant implements IExportableResourceFilter {
		private String[] bannedExtensions;
		public FilterExtensionsParticipant(String[] extensions) {
			this.bannedExtensions = extensions;
		}
		public boolean accepts(IFlatResource resource) {
			IFile ifile = (IFile)resource.getAdapter(IFile.class);
			String name = null;
			if( ifile != null ) {
				name = ifile.getName();
			} else {
				File f = (File)resource.getAdapter(File.class);
				if( f != null )
					name = f.getName();
			}
			if( name != null) {
				for( int i = 0; i < bannedExtensions.length; i++ ) {
					if( ifile.getName().endsWith(bannedExtensions[i]))
						return false;
				}
				return true;
			} 
			return false;
		}
	}
	
	public static FilterResourceParticipant createSuffixFilterParticipant(String[] strings) {
		return new FilterResourceParticipant(new FilterExtensionsParticipant(strings));
	}
	
	private IExportableResourceFilter filter;
	public FilterResourceParticipant(IExportableResourceFilter filter) {
		this.filter = filter;
	}
	
	public boolean shouldAddExportableFile(IVirtualComponent rootComponent,
			IVirtualComponent currentComponent, FlatComponentTaskModel dataModel,
			IFlatFile file) {
		return filter.accepts(file);
	}
}
