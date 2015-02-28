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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
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
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.lunifera.dsl.dto.lib.impl.DtoServiceAccess;
import org.lunifera.dsl.dto.lib.services.IDTOService;
import org.lunifera.ecview.core.common.beans.ISlot;
import org.lunifera.ecview.core.common.context.ContextException;
import org.lunifera.ecview.core.common.context.II18nService;
import org.lunifera.ecview.core.common.context.IViewContext;
import org.lunifera.ecview.core.common.model.core.CoreModelPackage;
import org.lunifera.ecview.core.common.model.core.YBeanSlot;
import org.lunifera.ecview.core.common.model.core.YExposedAction;
import org.lunifera.ecview.core.common.model.core.YView;
import org.lunifera.ecview.xtext.builder.participant.IECViewAddonsMetadataService;
import org.lunifera.runtime.common.event.IEventBroker;
import org.lunifera.runtime.common.state.ISharedStateContext;
import org.lunifera.runtime.common.state.ISharedStateContextProvider;
import org.lunifera.runtime.common.state.SharedStateUnitOfWork;
import org.lunifera.runtime.web.ecview.presentation.vaadin.VaadinRenderer;
import org.lunifera.runtime.web.vaadin.databinding.VaadinObservables;
import org.lunifera.vaaclipse.addons.common.api.IE4Constants;
import org.lunifera.vaaclipse.addons.common.api.di.Callback;
import org.lunifera.vaaclipse.addons.common.api.di.Delete;
import org.lunifera.vaaclipse.addons.common.event.EventTopicNormalizer;
import org.lunifera.vaaclipse.addons.ecview.IECViewConstants;
import org.lunifera.vaaclipse.addons.ecview.event.E4EventBrokerAdapter;
import org.lunifera.vaaclipse.addons.ecview.impl.Activator;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("restriction")
public class GenericECViewView {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(GenericECViewView.class);

	@Inject
	private IEclipseContext eclipseContext;
	@Inject
	private MPart mPart;
	@Inject
	private VerticalLayout parentLayout;
	@Inject
	private org.eclipse.e4.core.services.events.IEventBroker e4EventBroker;
	@Inject
	private EventTopicNormalizer topicNormalizer;
	@Inject
	private ISharedStateContextProvider sharedStateProvider;
	@Inject
	private II18nService i18nService;

	private String viewId;
	private ExposedActionsCallback exposedActionsCallback;
	private IViewContext viewContext;
	private IDTOService<Object> dtoService;
	private HashMap<String, Set<YBeanSlot>> redirectedEventtopics;
	private Set<EventHandler> eventHandlers;
	private ISharedStateContext sharedState;

	public GenericECViewView() {
	}

	@PostConstruct
	public void setup() {
		this.viewId = mPart.getPersistedState().get(
				IE4Constants.PROP_INPUT_VIEW_ID);

		VaadinObservables.activateRealm(UI.getCurrent());

		exposedActionsCallback = new ExposedActionsCallback();

		VerticalLayout layout = new VerticalLayout();
		parentLayout.addComponent(layout);
		layout.setSizeFull();

		YView yView = findViewModel();
		if (yView == null) {
			Notification.show(viewId + " could not be found!",
					Notification.Type.ERROR_MESSAGE);
			return;
		}

		// create shared state
		if (yView.getSharedStateGroup() != null
				&& !yView.getSharedStateGroup().trim().equals("")) {
			sharedState = sharedStateProvider.getContext(
					yView.getSharedStateGroup(), null);
		}

		YBeanSlot yBeanSlot = yView.getBeanSlot(IViewContext.MAIN_BEAN_SLOT);
		if (yBeanSlot != null) {
			dtoService = (IDTOService<Object>) DtoServiceAccess
					.getService(yBeanSlot.getValueType());
		}

		redirectEventTopics(yView);

		// render the Vaadin UI
		Map<String, Object> properties = new HashMap<String, Object>();

		// register services to be used
		Map<String, Object> services = new HashMap<String, Object>();
		services.put(
				org.lunifera.runtime.common.event.IEventBroker.class.getName(),
				new E4EventBrokerAdapter(e4EventBroker));
		if (sharedState != null) {
			services.put(ISharedStateContext.class.getName(), sharedState);
		}
		properties.put(IViewContext.PARAM_SERVICES, services);

		VaadinRenderer renderer = new VaadinRenderer();
		try {
			viewContext = renderer.render(layout, yView, properties);
		} catch (ContextException e) {
			e.printStackTrace();
		}

		preparePartToolbar(yView);
	}

	/**
	 * Remove defined event topics at bean slots. We pass them to custom
	 * handlers which will forward to the view. So we can avoid loosing changed
	 * data.
	 * 
	 * @param yView
	 */
	private void redirectEventTopics(YView yView) {

		VaadinObservables.activateRealm(parentLayout.getUI());

		// redirect event topics
		redirectedEventtopics = new HashMap<String, Set<YBeanSlot>>();
		for (YBeanSlot yBeanSlot : yView.getBeanSlots()) {
			String eventTopic = yBeanSlot.getEventTopic();
			if (eventTopic != null && !eventTopic.trim().equals("")) {
				eventTopic = eventTopic.trim();

				// events will be forwarded by this view
				yBeanSlot.setRedirectEvents(true);

				Set<YBeanSlot> yRedirSlots = redirectedEventtopics
						.get(eventTopic);
				if (yRedirSlots == null) {
					yRedirSlots = new HashSet<YBeanSlot>();
					redirectedEventtopics.put(eventTopic, yRedirSlots);
				}
				yRedirSlots.add(yBeanSlot);
			}
		}

		if (redirectedEventtopics.size() > 0) {
			eventHandlers = new HashSet<EventHandler>();
			// install event handler for each redirected topic
			for (String eventTopic : redirectedEventtopics.keySet()) {
				// install the event handler
				EventHandler handler = new EventHandler() {
					@Override
					public void handleEvent(Event event) {
						dispatchEventBrokerEvent(event);
					}
				};

				e4EventBroker.subscribe(eventTopic, handler);
				eventHandlers.add(handler);
			}
		}
	}

	@Persist
	public void save() {
		final Object mainDto = viewContext.getBean(IViewContext.MAIN_BEAN_SLOT);
		if (mainDto != null) {
			new SharedStateUnitOfWork<Object>() {
				@Override
				protected Object doExecute() {
					dtoService.update(mainDto);
					return null;
				}
			}.execute(sharedState);
		}
	}

	@Delete
	public void delete() {
		final Object mainDto = viewContext.getBean(IViewContext.MAIN_BEAN_SLOT);
		if (mainDto != null) {
			new SharedStateUnitOfWork<Object>() {
				@Override
				protected Object doExecute() {
					dtoService.delete(mainDto);
					return null;
				}
			}.execute(sharedState);
		}
	}

	/**
	 * If a command was executed, the original actionId from the ECView view
	 * (YView) will be passed here.
	 * 
	 * @param actionId
	 */
	@Callback
	public void commandExecuted(
			@Named(IE4Constants.PARAM_ACTION_ID) String actionId) {

		VaadinObservables.activateRealm(parentLayout.getUI());

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

			// register the exposed actions callback to handle enabled state
			yAction.eAdapters().add(exposedActionsCallback);

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

	/**
	 * Creates handled tool items for the given exposed action and the
	 * commandId.
	 * 
	 * @param yAction
	 * @param commandId
	 * @return
	 */
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
		toolItem.setTooltip(i18nService.getValue(yAction.getLabelI18nKey(),
				Locale.getDefault()));
		toolItem.setIconURI(i18nService.getValue(yAction.getIcon(),
				Locale.getDefault()));
		toolItem.setVisible(true);
		toolItem.setEnabled(yAction.isInitialEnabled());
		toolItem.setToBeRendered(true);
		toolItem.getTransientData().put(IE4Constants.PARAM_ACTION_ID,
				yAction.getId());
		// create the parameter that passes the original action id
		MParameter mParam = MCommandsFactory.INSTANCE.createParameter();
		mParam.setName(commandId + "." + IE4Constants.PARAM_ACTION_ID);
		mParam.setValue(yAction.getId());
		toolItem.getParameters().add(mParam);

		return toolItem;
	}

	/**
	 * Tries to find the command with the specified id.
	 * 
	 * @param mPart
	 * @param id
	 * @return
	 */
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
			if (redirectedEventtopics != null) {
				redirectedEventtopics.clear();
				redirectedEventtopics = null;
			}

			exposedActionsCallback = null;

			if (eventHandlers != null) {
				for (EventHandler handler : eventHandlers) {
					e4EventBroker.unsubscribe(handler);

				}
				eventHandlers.clear();
				eventHandlers = null;
			}

			viewContext.dispose();

			if (sharedState != null) {
				sharedStateProvider.unget(sharedState);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
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

	/**
	 * Dispatches events from the event broker to the proper bean slots.
	 * 
	 * @param event
	 */
	protected void dispatchEventBrokerEvent(Event event) {
		if (redirectedEventtopics == null) {
			return;
		}
		String eventTopic = topicNormalizer.unwrapTopic(event.getTopic());
		if (!redirectedEventtopics.containsKey(eventTopic)) {
			return;
		}
		for (YBeanSlot yBeanSlot : redirectedEventtopics.get(eventTopic)) {
			ISlot slot = viewContext.getBeanSlot(yBeanSlot.getName());
			slot.setValue(event.getProperty(IEventBroker.DATA));
		}
	}

	/**
	 * Forwards the enabled state to the e4 tool item.
	 */
	private class ExposedActionsCallback extends AdapterImpl {

		@Override
		public void notifyChanged(org.eclipse.emf.common.notify.Notification msg) {
			if (msg.getEventType() == org.eclipse.emf.common.notify.Notification.SET) {
				if (msg.getFeature() == CoreModelPackage.Literals.YENABLE__ENABLED) {

					YExposedAction yAction = (YExposedAction) msg.getNotifier();
					for (MToolBarElement item : mPart.getToolbar()
							.getChildren()) {
						if (!(item instanceof MHandledToolItem)) {
							continue;
						}
						MHandledToolItem handledItem = (MHandledToolItem) item;
						String id = (String) item.getTransientData().get(
								IE4Constants.PARAM_ACTION_ID);
						if (id != null && id.equals(yAction.getId())) {
							boolean newEnabled = msg.getNewBooleanValue();
							handledItem.setEnabled(newEnabled);
							break;
						}
					}
				}
			}
		}

	}
}
