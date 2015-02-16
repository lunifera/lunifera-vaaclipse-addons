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

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.lunifera.vaaclipse.addons.common.api.IE4Constants;
import org.lunifera.vaaclipse.addons.common.api.di.Callback;

public class SaveHandler extends AbstractHandler {

	@Execute
	public void execute(
			@Named(IServiceConstants.ACTIVE_PART) MContext context,
			@Named(IServiceConstants.ACTIVE_PART) MPart part,
			@Optional @Named(IE4Constants.COMMAND_SAVE__ACTION_ID) String uiActionId) {
		final IEclipseContext pmContext = context.getContext().createChild();
		ContextInjectionFactory.invoke(part, Persist.class, pmContext, null);

		if (uiActionId != null && !uiActionId.equals("")) {
			ContextInjectionFactory.invoke(part.getObject(), Callback.class,
					createCallbackContext(context, uiActionId), null);
		}
	}

	@CanExecute
	public boolean canExecute(
			@Named(IServiceConstants.ACTIVE_PART) MDirtyable dirtyable) {
		return dirtyable == null ? false : dirtyable.isDirty();
	}
}
