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
package org.lunifera.vaaclipse.addons.perspective.dialog;

import java.util.Locale;

import org.lunifera.ecview.core.common.context.II18nService;
import org.lunifera.runtime.web.vaadin.common.resource.IResourceProvider;
import org.lunifera.runtime.web.vaadin.components.dialogs.AbstractInputDialog;
import org.lunifera.runtime.web.vaadin.components.dialogs.IDialogI18nKeys;

import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Resource;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class SavePerspectiveDialog extends AbstractInputDialog {
	public static final String DIALOG_TITLE = "org.lunifera.dialogs.saveperspective.title";
	public static final String DIALOG_MESSAGE = "org.lunifera.dialogs.saveperspective.message";
	public static final String DIALOG_ICON = "org.lunifera.dialogs.saveperspective.image";
	public static final String DIALOG_DESCRIPTION = "org.lunifera.dialogs.saveperspective.description";

	public static final String DIALOG_OPTION__ACCEPT_CAPTION = "org.lunifera.dialogs.options.saveperspective";
	public static final String DIALOG_OPTION__ACCEPT_DESCRIPTION = "org.lunifera.dialogs.options.saveperspective.description";

	public static final String NAME_FIELD = "org.lunifera.dialogs.saveperspective.nameField";
	public static final String SYSTEM_USER_FIELD = "org.lunifera.dialogs.saveperspective.systemUserField";

	private II18nService i18nService;

	private TextField name;
	private CheckBox systemUser;
	private Data data;
	private BeanItem<Data> item;
	private boolean systemUserCapability;

	protected SavePerspectiveDialog(DialogConfig config, Data data,
			Option... options) {
		super(config, options);
		this.data = data;
	}

	@Override
	protected void fillForm(FormLayout customArea) {
		Locale locale = UI.getCurrent().getLocale();
		name = new TextField(i18nService.getValue(NAME_FIELD, locale));
		name.setNullRepresentation("");
		systemUser = new CheckBox(i18nService.getValue(SYSTEM_USER_FIELD,
				locale));
		systemUser.setVisible(systemUserCapability);

		customArea.addComponent(name);
		customArea.addComponent(systemUser);

		item = new BeanItem<Data>(data);
		name.setPropertyDataSource(item.getItemProperty("name"));
		systemUser.setPropertyDataSource(item.getItemProperty("systemUser"));

		name.focus();
	}

	public static void showDialog(II18nService service,
			boolean systemUserCapability, IResourceProvider resourceProvider,
			Runnable onAccept, Data data) {
		if (service == null) {
			throw new NullPointerException("Please pass an i18nService");
		}

		Locale locale = UI.getCurrent().getLocale();
		String dialogTitle = service.getValue(DIALOG_TITLE, locale);
		String dialogMessage = service.getValue(DIALOG_MESSAGE, locale);
		String dialogDescription = service.getValue(DIALOG_DESCRIPTION, locale);
		String dialogIcon = service.getValue(DIALOG_ICON, locale);
		String optionAcceptCaption = service.getValue(
				DIALOG_OPTION__ACCEPT_CAPTION, locale);
		String optionAcceptDescription = service.getValue(
				DIALOG_OPTION__ACCEPT_DESCRIPTION, locale);
		String optionCancelCaption = service.getValue(
				IDialogI18nKeys.DIALOG_OPTION__CANCEL_CAPTION, locale);
		String optionCancelDescription = service.getValue(
				IDialogI18nKeys.DIALOG_OPTION__CANCEL_DESCRIPTION, locale);
		String optionCancelIcon = service.getValue(
				IDialogI18nKeys.DIALOG_OPTION__CANCEL_ICON, locale);

		DialogConfig config = new DialogConfig(dialogTitle, dialogMessage,
				dialogDescription, createResource(dialogIcon, resourceProvider)) {
			@Override
			public void config(Window window) {
				super.config(window);
				window.setHeight("220px");
				window.setWidth("350px");
				window.center();
			}
		};

		SavePerspectiveDialog dialog = new SavePerspectiveDialog(config, data,
				new Option(optionCancelCaption, optionCancelDescription,
						createResource(optionCancelIcon, resourceProvider),
						null), new Option(optionAcceptCaption,
						optionAcceptDescription, null, onAccept));

		dialog.i18nService = service;
		dialog.systemUserCapability = systemUserCapability;
		dialog.open();
	}

	public static class Data {

		private String name;

		private boolean systemUser;

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the systemUser
		 */
		public boolean isSystemUser() {
			return systemUser;
		}

		/**
		 * @param systemUser
		 *            the systemUser to set
		 */
		public void setSystemUser(boolean systemUser) {
			this.systemUser = systemUser;
		}

	}

	public static class AcceptOption extends Option {

		public AcceptOption(String name, String description, Resource icon,
				Runnable runnable) {
			super(name, description, icon, runnable);
		}
	}
}
