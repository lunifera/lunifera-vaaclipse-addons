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
package org.lunifera.vaaclipse.addons.perspective.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.menu.MItem;
import org.eclipse.emf.ecore.EObject;
import org.lunifera.ecview.core.common.context.II18nService;
import org.lunifera.runtime.web.vaadin.common.resource.IResourceProvider;
import org.lunifera.vaaclipse.addons.application.handler.AbstractHandler;
import org.lunifera.vaaclipse.addons.common.api.resource.ICustomizedModelResourceHandler;
import org.lunifera.vaaclipse.addons.perspective.common.ISystemUserCapability;
import org.lunifera.vaaclipse.addons.perspective.dialog.SavePerspectiveDialog;
import org.semanticsoft.vaaclipse.publicapi.perspective.IPerspectiveHandler;

public class SavePerspectiveHandler extends AbstractHandler {

	@Inject
	private IPerspectiveHandler handler;

	@Inject
	private II18nService i18nService;

	@Inject
	private IResourceProvider resourceProvider;

	@Inject
	@Optional
	private ISystemUserCapability systemUserCapability;

	@Inject
	@Optional
	@Named("user")
	private String userId;

	@Execute
	public void execute(@Active MContext context,
			final @Active MPerspective perspective, @Active MItem item) {

		boolean systemUser = systemUserCapability != null ? systemUserCapability
				.hasCapability(userId) : true;

		final SavePerspectiveDialog.Data data = new SavePerspectiveDialog.Data();
		SavePerspectiveDialog.showDialog(i18nService, systemUser,
				resourceProvider, new Runnable() {
					@Override
					public void run() {
						String user = data.isSystemUser() ? ICustomizedModelResourceHandler.SYSTEM_USER
								: userId;
						MPerspective newPerspective = handler.clonePerspective(
								user, data.getName(), perspective);

						if (newPerspective != null) {
							// select the first perspective in the stack
							MPerspectiveStack stack = (MPerspectiveStack) ((EObject) perspective)
									.eContainer();
							stack.setSelectedElement(newPerspective);
						}
					}
				}, data);
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}
}
