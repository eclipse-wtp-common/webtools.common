/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


public abstract class RendererFactory {

	public abstract Renderer createRenderer();

	private static RendererFactory defaultRendererFactory;

	private static RendererFactoryDefaultHandler defaultHandler;

	private boolean validating = true;

	/**
	 * Interested parties can use the
	 */
	public final static Notifier NotificationEngine = Notifier.INSTANCE;

	/**
	 * @return
	 */
	public static RendererFactory getDefaultRendererFactory() {
		if (defaultRendererFactory == null)
			defaultRendererFactory = getDefaultHandler().getDefaultRendererFactory();
		return defaultRendererFactory;
	}

	/**
	 * @param factory
	 */
	public static void setDefaultRendererFactory(RendererFactory factory) {

		//System.out.println("\n\n***Setting factory: " + factory);
		NotificationEngine.notifyListeners(factory);
		defaultRendererFactory = factory;
	}

	public String toString() {
		return "RendererFactory instance: " + getClass().getName(); //$NON-NLS-1$
	}

	public static class Notifier {

		private static final Notifier INSTANCE = new Notifier();

		private final Collection resourceFactoryListeners = new ArrayList();

		private Notifier() {
		}

		public void notifyListeners(RendererFactory rendererFactory) {

			final int length = resourceFactoryListeners.size();
			//System.out.println("Notifying " + length + " listeners");

			if (length > 0) {
				/*
				 * Since the renderer factories are singletons, this reference check should always
				 * work
				 */
				if (rendererFactory != RendererFactory.getDefaultRendererFactory()) {
					WeakReference wref = null;
					Listener listener = null;
					synchronized (resourceFactoryListeners) {
						for (Iterator i = resourceFactoryListeners.iterator(); i.hasNext();) {
							wref = (WeakReference) i.next();
							listener = (Listener) wref.get();
							//System.out.println("Notifying Listener: " + listener);
							if (listener != null)
								listener.updateRendererFactory(rendererFactory);
							else
								i.remove();
						}
					}
				}
			}
		}

		public void addListener(Listener l) {
			//System.out.println("Adding listener: " + l);
			synchronized (resourceFactoryListeners) {
				resourceFactoryListeners.add(new WeakReference(l));
			}
		}

		public void removeListener(Listener listenerToRemove) {
			final int length = resourceFactoryListeners.size();
			if (length > 0) {
				WeakReference wref = null;
				Listener listener = null;
				synchronized (resourceFactoryListeners) {
					for (Iterator i = resourceFactoryListeners.iterator(); i.hasNext();) {
						wref = (WeakReference) i.next();
						listener = (Listener) wref.get();
						if (listener != null) {
							if (listener == listenerToRemove) {
								i.remove();
								break;
							}
						} else {
							i.remove();
						}
					}
				}
			}
		}

	}

	public interface Listener {
		void updateRendererFactory(RendererFactory newRendererFactory);
	}

	/**
	 * @return
	 */
	public boolean isValidating() {
		return validating;
	}

	/**
	 * @param b
	 */
	public void setValidating(boolean b) {
		validating = b;
	}

	/**
	 * @return
	 */
	public static RendererFactoryDefaultHandler getDefaultHandler() {
		if (defaultHandler == null)
			defaultHandler = EMF2DOMRendererFactoryDefaultHandler.INSTANCE;
		return defaultHandler;
	}

	/**
	 * @param handler
	 */
	public static void setDefaultHandler(RendererFactoryDefaultHandler handler) {
		defaultHandler = handler;
	}

}