/**
 * Copyright (c) 2011 - 2014, Lunifera GmbH (Gross Enzersdorf), Loetz KG (Heidelberg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * 		Florian Pirchner - Initial implementation
 */
package org.lunifera.vaaclipse.addons.ecview.e4context;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.lunifera.vaaclipse.addons.ecview.DynamicViewSupport;
import org.osgi.service.component.annotations.Component;

@Component(service = org.eclipse.e4.core.contexts.IContextFunction.class, property = { "service.context.key=org.lunifera.vaaclipse.addons.ecview.DynamicViewSupport" })
public class DynamicViewSupportContextFunctionFactory extends ContextFunction {

	public static final String ROOT_CONTEXT = "rootContext"; //$NON-NLS-1$

	public DynamicViewSupportContextFunctionFactory() {
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		MApplication application = context.get(MApplication.class);
		IEclipseContext rootContext = application.getContext();
		DynamicViewSupport support = rootContext
				.getLocal(DynamicViewSupport.class);
		if (support == null) {
			support = ContextInjectionFactory.make(DynamicViewSupport.class,
					rootContext);
			rootContext.set(DynamicViewSupport.class, support);
		}

		return support;
	}
}
