package org.eclipse.wst.common.framework;

/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */


import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Defines the API for coping with attempts to overwrite read-only files or save failures
 */
public interface ISaveHandler {
	public void access();

	public void handleSaveFailed(SaveFailedException ex, IProgressMonitor monitor);

	public void release();

	public boolean shouldContinueAndMakeFileEditable(IFile aFile);
}