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

import javax.inject.Named;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.lunifera.vaaclipse.addons.common.api.IE4Constants;
import org.lunifera.vaaclipse.addons.common.api.di.Callback;
import org.lunifera.vaaclipse.addons.common.api.di.Delete;

public class DeleteHandler extends AbstractHandler {
	@Execute
	public void execute(
			@Active MContext context,
			@Active MPart part,
			@Optional @Named(IE4Constants.COMMAND_DELETE__ACTION_ID) String uiActionId) {
		final IEclipseContext pmContext = context.getContext().createChild();
		ContextInjectionFactory.invoke(part.getObject(), Delete.class,
				pmContext, null);

		if (uiActionId != null && !uiActionId.equals("")) {
			ContextInjectionFactory.invoke(part.getObject(), Callback.class,
					createCallbackContext(context, uiActionId), null);
		}
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}
}
