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
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.lunifera.ecview.core.common.model.core.YView;
import org.lunifera.vaaclipse.addons.ecview.DynamicViewSupport;
import org.osgi.service.component.annotations.Component;

@Component(service = org.eclipse.e4.core.contexts.IContextFunction.class, property = { "service.context.key=org.lunifera.ecview.core.common.model.core.YView" })
public class YViewContextFunctionFactory extends ContextFunction {

	public YViewContextFunctionFactory() {
	}

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		MPart part = context.get(MPart.class);
		DynamicViewSupport viewSupport = context.get(DynamicViewSupport.class);
		YView yView = viewSupport.createPartContextForPersisted(part);
		return yView;
	}
}
