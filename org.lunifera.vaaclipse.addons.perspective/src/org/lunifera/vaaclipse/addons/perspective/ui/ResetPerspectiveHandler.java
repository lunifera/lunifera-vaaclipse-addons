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
package org.lunifera.vaaclipse.addons.perspective.ui;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.menu.MItem;
import org.lunifera.vaaclipse.addons.application.handler.AbstractHandler;
import org.semanticsoft.vaaclipse.publicapi.perspective.IPerspectiveHandler;

public class ResetPerspectiveHandler extends AbstractHandler {

	@Inject
	private IPerspectiveHandler handler;

	@Inject
	@Optional
	@Named("user")
	private String userId;

	@Execute
	public void execute(@Active MContext context,
			@Active MPerspective perspective, @Active MItem item) {
		handler.revertPerspective(perspective);
	}

	@CanExecute
	public boolean canExecute(@Active MPerspective perspective) {
		return handler.canRevertPerspective(perspective);
	}
}
