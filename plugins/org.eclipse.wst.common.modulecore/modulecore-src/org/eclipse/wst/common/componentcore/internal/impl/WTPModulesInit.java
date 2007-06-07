/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.componentcore.internal.impl;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.wst.common.componentcore.internal.ComponentcoreFactory;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.internal.emf.resource.EMF2DOMRendererFactory;
import org.eclipse.wst.common.internal.emf.utilities.DOMUtilities;
import org.eclipse.wst.common.internal.emf.utilities.EncoderDecoderRegistry;
import org.eclipse.wst.common.internal.emf.utilities.ExtendedEcoreUtil;
import org.eclipse.wst.common.internal.emf.utilities.PasswordEncoderDecoder;


/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class WTPModulesInit {
 
	private static boolean isInitialized = false;
	
	public static void init() {
		init(true);
	}
	
	public static void init(boolean toPreregisterPackages) {
		if (!isInitialized) {
			isInitialized = true;
			setDefaultEncoderDecoder();
			DOMUtilities.setDefaultEntityResolver(WTPEntityResolver.INSTANCE); 
			initResourceFactories();
			
			//TODO: Remove this line after SED Adapter is restored.
			EMF2DOMRendererFactory.INSTANCE.setValidating(false);
		}
		if(toPreregisterPackages) 
			preregisterPackages();
	}
	
	private static void initResourceFactories() {
		WTPModulesResourceFactory.register();	
	}

	/** 
	 * If the currently defaulted encoder is the initial pass thru encoder,
	 * then register a Password encoder for security; otherwise if a more sophisticated
	 * encoder is already registered, then do nothing.
	 */
	private static void setDefaultEncoderDecoder() {
		EncoderDecoderRegistry reg = EncoderDecoderRegistry.getDefaultRegistry();
		if (reg.getDefaultEncoderDecoder() == EncoderDecoderRegistry.INITIAL_DEFAULT_ENCODER) {
			reg.setDefaultEncoderDecoder(new PasswordEncoderDecoder());
		}
	}
	
	private static void preregisterPackages() { 
		ExtendedEcoreUtil.preRegisterPackage("moduleCore.xmi", new EPackage.Descriptor() { //$NON-NLS-1$
			public EPackage getEPackage() {
				return ComponentcorePackage.eINSTANCE;
			}
			public EFactory getEFactory() {
				return ComponentcoreFactory.eINSTANCE;
			}
		});
	}
	
	
}
