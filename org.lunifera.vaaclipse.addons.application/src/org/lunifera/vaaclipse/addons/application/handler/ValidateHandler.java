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

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MItem;

import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

public class ValidateHandler extends AbstractHandler {

	@Execute
	public void execute(@Active MContext context, @Active MPart part,
			@Active VerticalLayout parent, @Active MItem item) {
		// final IEclipseContext pmContext = createCallbackContext(context,
		// item);
		// ContextInjectionFactory.invoke(part.getObject(), Validate.class,
		// pmContext, null);

		Notification.show("Not implemented yet.", Type.WARNING_MESSAGE);

	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}
}
