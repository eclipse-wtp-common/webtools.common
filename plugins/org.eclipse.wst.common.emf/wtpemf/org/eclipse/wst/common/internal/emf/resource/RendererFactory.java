/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/

 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.internal.emf.resource;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;


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

	@Override
	public String toString() {
		return "RendererFactory instance: " + getClass().getName(); //$NON-NLS-1$
	}

	public static class Notifier {

		private static final Notifier INSTANCE = new Notifier();

		// use a WeakHashMap for a weak HashSet
		private final Map resourceFactoryListeners = new WeakHashMap();

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
					synchronized (resourceFactoryListeners) {
						for (Iterator i = resourceFactoryListeners.keySet().iterator(); i.hasNext();) {
							Listener listener = (Listener) i.next();
							//System.out.println("Notifying Listener: " + listener);
							listener.updateRendererFactory(rendererFactory);
						}
					}
				}
			}
		}

		public void addListener(Listener l) {
			//System.out.println("Adding listener: " + l);
			synchronized (resourceFactoryListeners) {
				resourceFactoryListeners.put(l, null);
			}
		}

		public void removeListener(Listener listenerToRemove) {
			synchronized (resourceFactoryListeners) {
				resourceFactoryListeners.remove(listenerToRemove);
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