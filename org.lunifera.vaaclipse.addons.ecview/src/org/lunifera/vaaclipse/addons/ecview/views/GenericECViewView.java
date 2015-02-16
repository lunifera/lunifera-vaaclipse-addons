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
package org.lunifera.vaaclipse.addons.ecview.views;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.emf.ecore.EObject;
import org.lunifera.dsl.dto.lib.impl.DtoServiceAccess;
import org.lunifera.dsl.dto.lib.services.IDTOService;
import org.lunifera.ecview.core.common.context.ContextException;
import org.lunifera.ecview.core.common.context.IViewContext;
import org.lunifera.ecview.core.common.model.core.YBeanSlot;
import org.lunifera.ecview.core.common.model.core.YExposedAction;
import org.lunifera.ecview.core.common.model.core.YView;
import org.lunifera.ecview.xtext.builder.participant.IECViewAddonsMetadataService;
import org.lunifera.runtime.web.ecview.presentation.vaadin.VaadinRenderer;
import org.lunifera.vaaclipse.addons.common.api.IE4Constants;
import org.lunifera.vaaclipse.addons.common.api.di.Callback;
import org.lunifera.vaaclipse.addons.ecview.IECViewConstants;
import org.lunifera.vaaclipse.addons.ecview.event.E4EventBrokerAdapter;
import org.lunifera.vaaclipse.addons.ecview.impl.Activator;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("restriction")
public class GenericECViewView {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GenericECViewView.class);

	private final String viewId;
	private final IEclipseContext eclipseContext;
	private final VerticalLayout parent;

	private IViewContext viewContext;
	private IDTOService<?> dtoService;

	@Inject
	private org.eclipse.e4.core.services.events.IEventBroker e4EventBroker;

	private MPart mPart;

	@Inject
	public GenericECViewView(VerticalLayout parent,
			IEclipseContext eclipseContext, MPart mPart) {
		this.parent = parent;
		this.eclipseContext = eclipseContext;
		this.mPart = mPart;

		this.viewId = mPart.getPersistedState().get(
				IE4Constants.PROP_INPUT_VIEW_ID);

		VerticalLayout layout = new VerticalLayout();
		parent.addComponent(layout);
		layout.setSizeFull();

		YView yView = findViewModel();
		if (yView == null) {
			Notification.show(viewId + " could not be found!",
					Notification.Type.ERROR_MESSAGE);
			return;
		}

		YBeanSlot yBeanSlot = yView.getBeanSlot("main");
		if (yBeanSlot != null) {
			dtoService = DtoServiceAccess.getService(yBeanSlot.getValueType());
		}

		// render the Vaadin UI
		Map<String, Object> properties = new HashMap<String, Object>();
		Map<String, Object> services = new HashMap<String, Object>();
		services.put(
				org.lunifera.runtime.common.event.IEventBroker.class.getName(),
				new E4EventBrokerAdapter(e4EventBroker));
		properties.put(IViewContext.PARAM_SERVICES, services);

		VaadinRenderer renderer = new VaadinRenderer();
		try {
			viewContext = renderer.render(layout, yView, properties);
		} catch (ContextException e) {
			e.printStackTrace();
		}

		preparePartToolbar(yView);
	}

	@Callback
	public void commandExecuted(
			@Named(IE4Constants.PARAM_ACTION_ID) String actionId) {
		// find the action and forward it to the view
		EObject action = (EObject) viewContext.findModelElement(actionId);
		if (action instanceof YExposedAction) {
			YExposedAction yAction = (YExposedAction) action;
			yAction.setLastClickTime(new Date().getTime());
		}
	}

	/**
	 * Prepares the toolbar for the view.
	 * 
	 * @param yView
	 */
	private void preparePartToolbar(YView yView) {
		MToolBar mToolbar = mPart.getToolbar();
		for (YExposedAction yAction : yView.getExposedActions()) {

			MHandledToolItem toolItem = null;
			if (yAction.getId().equals(IECViewConstants.ACTION__SAVE)) {
				toolItem = createToolItem(yAction, IE4Constants.COMMAND_SAVE);
			} else if (yAction.getId().equals(IECViewConstants.ACTION__DELETE)) {
				toolItem = createToolItem(yAction, IE4Constants.COMMAND_DELETE);
			} else if (yAction.getId().equals(IECViewConstants.ACTION__LOAD)) {
				toolItem = createToolItem(yAction, IE4Constants.COMMAND_LOAD);
			} else {
				toolItem = createToolItem(yAction,
						IE4Constants.COMMAND_PART_CALLBACK);
			}

			if (toolItem != null) {
				mToolbar.getChildren().add(toolItem);
			}
		}
	}

	private MHandledToolItem createToolItem(YExposedAction yAction,
			String commandId) {
		MCommand mSaveCommand = findCommand(mPart, commandId);
		if (mSaveCommand == null) {
			LOGGER.error("No action created for " + yAction.getId()
					+ " since command missing: " + commandId);
			return null;
		}
		MHandledToolItem toolItem = MMenuFactory.INSTANCE
				.createHandledToolItem();
		toolItem.setCommand(mSaveCommand);
		toolItem.setLabel(yAction.getLabelI18nKey());
		toolItem.setVisible(true);
		toolItem.setToBeRendered(true);
		// create the parameter that passes the original action id
		MParameter mParam = MCommandsFactory.INSTANCE.createParameter();
		mParam.setName(commandId + "." + IE4Constants.PARAM_ACTION_ID);
		mParam.setValue(yAction.getId());
		toolItem.getParameters().add(mParam);

		return toolItem;
	}

	private MCommand findCommand(MPart mPart, String id) {
		EModelService modelService = eclipseContext.get(EModelService.class);
		List<MCommand> mSaveCommands = modelService.findElements(
				eclipseContext.get(MApplication.class), id, MCommand.class,
				Collections.<String> emptyList());
		MCommand command = null;
		if (mSaveCommands.size() > 0) {
			command = mSaveCommands.get(0);
		}
		return command;
	}

	@PreDestroy
	public void dispose() {
		try {
			viewContext.dispose();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Persist
	public void save() {

	}

	/**
	 * Tries to find the view model using the ecview addons service.
	 * 
	 * @return
	 */
	protected YView findViewModel() {
		ServiceTracker<IECViewAddonsMetadataService, IECViewAddonsMetadataService> tracker = new ServiceTracker<IECViewAddonsMetadataService, IECViewAddonsMetadataService>(
				Activator.getContext(), IECViewAddonsMetadataService.class,
				null);
		tracker.open();
		try {
			IECViewAddonsMetadataService uiService = tracker
					.waitForService(5000);
			return uiService.getViewMetadata(viewId);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			tracker.close();
		}
		return null;
	}
}
