/*******************************************************************************
 * Copyright (c) 2002, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.common.frameworks.internal.ui;

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
	@Override
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

	@Override
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
	@Override
	public boolean isContentCreated() {
		return wizard != null;
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizardNode#getExtent()
	 */
	@Override
	public Point getExtent() {
		return null;
	}

}