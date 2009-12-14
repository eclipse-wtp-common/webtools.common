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
package org.eclipse.wst.common.componentcore.export;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.common.componentcore.export.ExportModel.ExportTaskModel;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

public class ExportResourceFilterParticipant extends AbstractExportParticipant {
	public interface IExportableResourceFilter {
		public boolean accepts(IExportableResource resource);
	}
	
	public static class FilterExtensionsParticipant implements IExportableResourceFilter {
		private String[] bannedExtensions;
		public FilterExtensionsParticipant(String[] extensions) {
			this.bannedExtensions = extensions;
		}
		public boolean accepts(IExportableResource resource) {
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
	
	public static ExportResourceFilterParticipant createSuffixFilterParticipant(String[] strings) {
		return new ExportResourceFilterParticipant(new FilterExtensionsParticipant(strings));
	}
	
	private IExportableResourceFilter filter;
	public ExportResourceFilterParticipant(IExportableResourceFilter filter) {
		this.filter = filter;
	}
	
	public boolean shouldAddExportableFile(IVirtualComponent rootComponent,
			IVirtualComponent currentComponent, ExportTaskModel dataModel,
			IExportableFile file) {
		return filter.accepts(file);
	}
}
