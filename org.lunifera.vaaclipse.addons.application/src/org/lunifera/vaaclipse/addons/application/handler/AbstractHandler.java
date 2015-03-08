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
package org.lunifera.vaaclipse.addons.application.handler;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.menu.MItem;
import org.lunifera.vaaclipse.addons.common.api.IE4Constants;

public class AbstractHandler {

	public AbstractHandler() {
		super();
	}

	/**
	 * Creates a child context and puts the uiAction into the child context as
	 * key = {@link IE4Constants#PARAM_ACTION_TYPE_KEY} and value =
	 * {@link IE4Constants#PARAM_ACTION}.
	 * 
	 * @param context
	 * @param uiActionId
	 * @return
	 */
	protected IEclipseContext createCallbackContext(MContext context, MItem item) {
		Object action = item.getTransientData().get(IE4Constants.PARAM_ACTION);
		@SuppressWarnings("unchecked")
		Class<Object> actionTypeKey = (Class<Object>) item.getTransientData()
				.get(IE4Constants.PARAM_ACTION_TYPE_KEY);
		final IEclipseContext pmContext = context.getContext().createChild();
		pmContext.set(actionTypeKey, action);
		return pmContext;
	}

}