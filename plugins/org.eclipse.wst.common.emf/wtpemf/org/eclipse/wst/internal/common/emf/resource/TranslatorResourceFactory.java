/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.internal.common.emf.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

public abstract class TranslatorResourceFactory extends ReferencedXMIFactoryImpl implements RendererFactory.Listener {

	protected RendererFactory rendererFactory;
	private boolean listeningForUpdates = false;

	/**
	 * Constructor for TranslatorResourceFactory.
	 */
	public TranslatorResourceFactory(RendererFactory aRendererFactory, boolean listeningForUpdates) {
		super();
		rendererFactory = aRendererFactory;
		setListeningForUpdates(listeningForUpdates);
	}

	/**
	 * Constructor for TranslatorResourceFactory.
	 */
	public TranslatorResourceFactory(RendererFactory aRendererFactory) {
		this(aRendererFactory, true);
	}

	public void setListeningForUpdates(boolean shouldBeListeningForUpdates) {

		if (this.listeningForUpdates ^ shouldBeListeningForUpdates) {
			if (!shouldBeListeningForUpdates)
				RendererFactory.NotificationEngine.removeListener(this);
			else
				RendererFactory.NotificationEngine.addListener(this);

			this.listeningForUpdates = shouldBeListeningForUpdates;
		}
	}

	public boolean isListeningForUpdates() {
		return this.listeningForUpdates;
	}

	public Resource doCreateResource(URI uri) {
		Renderer aRenderer = rendererFactory.createRenderer();
		return createResource(uri, aRenderer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.RendererFactory.Listener#updateRendererFactory(com.ibm.etools.emf2xml.RendererFactory)
	 */
	public void updateRendererFactory(RendererFactory newRendererFactory) {
		//System.out.println("Updating renderer factory");
		rendererFactory = newRendererFactory;
	}

	protected abstract TranslatorResource createResource(URI uri, Renderer aRenderer);

}