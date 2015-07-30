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
package org.lunifera.vaaclipse.addons.common.status;

import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.lunifera.vaaclipse.addons.common.api.status.IStatusManager;
import org.osgi.service.component.annotations.Component;

@Component(service = org.eclipse.e4.core.contexts.IContextFunction.class, property = { "service.context.key=org.lunifera.vaaclipse.addons.common.api.status.IStatusManager" })
public class StatusManagerFactory extends ContextFunction {

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		StatusManager manager = ContextInjectionFactory.make(
				StatusManager.class, context);
		context.set(IStatusManager.class, manager);
		return manager;
	}
}
