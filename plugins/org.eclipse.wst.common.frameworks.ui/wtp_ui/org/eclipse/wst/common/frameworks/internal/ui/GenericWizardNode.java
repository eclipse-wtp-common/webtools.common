package org.eclipse.wst.common.frameworks.internal.ui;

/*
 * Licensed Material - Property of IBM 
 * (C) Copyright IBM Corp. 2002 - All Rights Reserved. 
 * US Government Users Restricted Rights - Use, duplication or disclosure 
 * restricted by GSA ADP Schedule Contract with IBM Corp. 
 */

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.swt.graphics.Point;

/**
 * @version 1.0
 * @author
 */
public abstract class GenericWizardNode implements IWizardNode {

	protected IWizard wizard;

	/**
	 * Constructor for GenericWizardNode.
	 */
	public GenericWizardNode() {
		super();
	}

	/*
	 * @see IWizardNode#dispose()
	 */
	public void dispose() {
		if (wizard != null)
			wizard.dispose();
	}

	/*
	 * @see IWizardNode#getContents()
	 */
	public Point getContents() {
		return null;
	}

	public final IWizard getWizard() {
		if (wizard == null)
			wizard = createWizard();
		return wizard;
	}

	/**
	 * Subclasses must override to create the wizard
	 */
	protected abstract IWizard createWizard();

	/*
	 * @see IWizardNode#isContentCreated()
	 */
	public boolean isContentCreated() {
		return wizard != null;
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizardNode#getExtent()
	 */
	public Point getExtent() {
		return null;
	}

}