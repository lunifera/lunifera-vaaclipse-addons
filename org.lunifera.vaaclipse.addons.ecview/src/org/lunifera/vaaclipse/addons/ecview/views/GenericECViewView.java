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

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.lunifera.dsl.dto.lib.services.IDTOService;
import org.lunifera.ecview.core.common.beans.ISlot;
import org.lunifera.ecview.core.common.context.ContextException;
import org.lunifera.ecview.core.common.context.II18nService;
import org.lunifera.ecview.core.common.context.IViewContext;
import org.lunifera.ecview.core.common.model.core.CoreModelPackage;
import org.lunifera.ecview.core.common.model.core.YBeanSlot;
import org.lunifera.ecview.core.common.model.core.YExposedAction;
import org.lunifera.ecview.core.common.model.core.YView;
import org.lunifera.runtime.common.annotations.DtoUtils;
import org.lunifera.runtime.common.event.IEventBroker;
import org.lunifera.runtime.common.state.ISharedStateContext;
import org.lunifera.runtime.common.state.SharedStateUnitOfWork;
import org.lunifera.runtime.web.ecview.presentation.vaadin.VaadinRenderer;
import org.lunifera.runtime.web.vaadin.common.resource.IResourceProvider;
import org.lunifera.runtime.web.vaadin.components.dialogs.AcceptDeleteDialog;
import org.lunifera.runtime.web.vaadin.components.dialogs.AcceptLoosingDataDialog;
import org.lunifera.runtime.web.vaadin.components.dialogs.AcceptReloadDialog;
import org.lunifera.vaaclipse.addons.common.api.IE4Constants;
import org.lunifera.vaaclipse.addons.common.api.di.Callback;
import org.lunifera.vaaclipse.addons.common.api.di.Delete;
import org.lunifera.vaaclipse.addons.common.api.di.Load;
import org.lunifera.vaaclipse.addons.common.api.di.Validate;
import org.lunifera.vaaclipse.addons.common.event.EventTopicNormalizer;
import org.lunifera.vaaclipse.addons.ecview.event.E4EventBrokerAdapter;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.semanticsoft.vaaclipse.publicapi.commands.IPartItemExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

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
	private II18nService i18nService;
	@Inject
	private IResourceProvider resourceProvider;
	@Inject
	private YView yView;
	@Inject
	private ISharedStateContext sharedState;
	@Inject
	private IDTOService<Object> dtoService;

	private ExposedActionsCallback exposedActionsCallback;
	private IViewContext viewContext;
	private HashMap<String, Set<YBeanSlot>> redirectedEventtopics;
	private Set<EventHandler> eventHandlers;

	public GenericECViewView() {
	}

	@PostConstruct
	public void setup() {

		if (yView == null) {
			Notification.show("View model is not available!",
					Notification.Type.ERROR_MESSAGE);
			return;
		}

		// VaadinObservables.activateRealm(UI.getCurrent());

		exposedActionsCallback = new ExposedActionsCallback();

		VerticalLayout layout = new VerticalLayout();
		parentLayout.addComponent(layout);
		layout.setSizeFull();

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
	public void save(final YExposedAction yAction) {
		final Object mainDto = viewContext.getBean(IViewContext.MAIN_BEAN_SLOT);
		boolean processedProperly = false;
		try {
			if (mainDto != null) {
				new SharedStateUnitOfWork<Object>() {
					@Override
					protected Object doExecute() {
						dtoService.update(mainDto);
						return null;
					}
				}.execute(sharedState);
				// in case of exception, it is still false
				processedProperly = true;
			} else {
			}
		} finally {
			if (processedProperly) {
				notifyExecuted(yAction);
			} else {
				notifyCanceled(yAction);
			}
		}
	}

	@Validate
	public void validate(final YExposedAction yAction) {
		if (yAction != null) {
			notifyExternalClicked(yAction);
		}
	}

	@Delete
	public void delete(final YExposedAction yAction) {
		final Object mainDto = viewContext.getBean(IViewContext.MAIN_BEAN_SLOT);
		if (mainDto != null) {
			AcceptDeleteDialog.showDialog(i18nService, resourceProvider,
					new Runnable() {
						@Override
						public void run() {
							new SharedStateUnitOfWork<Object>() {
								@Override
								protected Object doExecute() {
									boolean processed = false;
									try {
										dtoService.delete(mainDto);
										// in case of exception, it is not
										// changed
										processed = true;
									} finally {
										if (processed) {
											notifyExecuted(yAction);
										} else {
											notifyCanceled(yAction);
										}
									}
									return null;
								}
							}.execute(sharedState);
						}
					}, new ActionCanceledAdapter(yAction));
		} else {
			notifyCanceled(yAction);
		}
	}

	/**
	 * Notifies the action about cancel.
	 * 
	 * @param yAction
	 */
	protected void notifyCanceled(final YExposedAction yAction) {
		ActionCanceledAdapter.notify(yAction);
	}

	/**
	 * Notifies the action about executed.
	 * 
	 * @param yAction
	 */
	protected void notifyExecuted(final YExposedAction yAction) {
		ActionExecutedAdapter.notify(yAction);
	}

	/**
	 * Notifies the action about external clicked.
	 * 
	 * @param yAction
	 */
	protected void notifyExternalClicked(final YExposedAction yAction) {
		ActionExternalClickedAdapter.notify(yAction);
	}

	@Load
	public void reload(final YExposedAction yAction) {
		final Object mainDto = viewContext.getBean(IViewContext.MAIN_BEAN_SLOT);
		if (mainDto != null) {
			boolean isDirty = false;
			try {
				isDirty = DtoUtils.isDirty(mainDto);
			} catch (IllegalAccessException e) {
				// nothing to do
			}

			// if there is no dirty indicator, or the record is not dirty,
			// reload the data
			if (!isDirty) {
				new SharedStateUnitOfWork<Object>() {
					@Override
					protected Object doExecute() {
						boolean processed = false;
						try {
							dtoService.reload(mainDto);
							// in case of exception, it is not changed
							processed = true;
						} finally {
							if (processed) {
								notifyExecuted(yAction);
							} else {
								notifyCanceled(yAction);
							}
						}
						return null;
					}
				}.execute(sharedState);
			} else {
				AcceptReloadDialog.showDialog(i18nService, resourceProvider,
						new Runnable() {
							@Override
							public void run() {
								new SharedStateUnitOfWork<Object>() {
									@Override
									protected Object doExecute() {
										boolean processed = false;
										try {
											dtoService.reload(mainDto);
											// in case of exception, it is not
											// changed
											processed = true;
										} finally {
											if (processed) {
												notifyExecuted(yAction);
											} else {
												notifyCanceled(yAction);
											}
										}
										return null;
									}
								}.execute(sharedState);
							}
						}, new ActionCanceledAdapter(yAction));
			}
		} else {
			notifyCanceled(yAction);
		}
	}

	/**
	 * If a command was executed, the original actionId from the ECView view
	 * (YView) will be passed here.
	 * 
	 * @param actionId
	 */
	@Callback
	public void commandExecuted(YExposedAction yAction) {
		if (yAction != null) {
			// we are going to forward the execution of the action to the ecView
			// exposed action
			// final YExposedAction yAction = (YExposedAction) action;
			yAction.setExternalClickTime(new Date().getTime());

			// check if dto is dirty
			//
			boolean isDirty = false;
			final Object mainDto = viewContext
					.getBean(IViewContext.MAIN_BEAN_SLOT);
			if (mainDto != null) {
				try {
					isDirty = DtoUtils.isDirty(mainDto);
				} catch (IllegalAccessException e) {
					// nothing to do -> if there is not dirty flag, we ignore
					// this
				}
			}

			// to notify about executed or canceled state, first check if the
			// state is dirty
			if (isDirty && yAction.isCheckDirty()) {
				// show accept dialog
				AcceptLoosingDataDialog.showDialog(i18nService,
						resourceProvider, new ActionExecutedAdapter(yAction),
						new ActionCanceledAdapter(yAction));
			} else {
				notifyExecuted(yAction);
			}
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
			// if (yAction.getId().equals(IECViewConstants.ACTION__SAVE)) {
			// toolItem = createToolItem(yAction, IE4Constants.COMMAND_SAVE);
			// } else if
			// (yAction.getId().equals(IECViewConstants.ACTION__DELETE)) {
			// toolItem = createToolItem(yAction, IE4Constants.COMMAND_DELETE);
			// } else if (yAction.getId().equals(IECViewConstants.ACTION__LOAD))
			// {
			// toolItem = createToolItem(yAction, IE4Constants.COMMAND_LOAD);
			// } else
			if (yAction.getExternalCommandId() == null) {
				toolItem = createToolItem(yAction,
						IE4Constants.COMMAND_DEFAULT_PART_CALLBACK);
			} else if (yAction.getExternalCommandId() != null) {
				toolItem = createToolItem(yAction,
						yAction.getExternalCommandId());
			}

			if (toolItem != null) {
				mToolbar.getChildren().add(toolItem);
				// set default after rendering
				toolItem.setEnabled(yAction.isInitialEnabled());
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
		MCommand command = findCommand(mPart, commandId);
		if (command == null) {
			LOGGER.error("No action created for " + yAction.getId()
					+ " since command missing: " + commandId);
			return null;
		}
		MHandledToolItem toolItem = MMenuFactory.INSTANCE
				.createHandledToolItem();
		toolItem.setCommand(command);
		toolItem.setTooltip(i18nService.getValue(yAction.getLabelI18nKey(),
				Locale.getDefault()));
		toolItem.setIconURI(i18nService.getValue(yAction.getIcon(),
				Locale.getDefault()));
		toolItem.setVisible(true);
		// toolItem.setEnabled(yAction.isInitialEnabled());
		toolItem.setToBeRendered(true);
		toolItem.getTransientData().put(IE4Constants.PARAM_ACTION, yAction);
		toolItem.getTransientData().put(IE4Constants.PARAM_ACTION_TYPE_KEY,
				YExposedAction.class);

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
		List<MCommand> commands = modelService.findElements(
				eclipseContext.get(MApplication.class), id, MCommand.class,
				Collections.<String> emptyList());
		MCommand command = null;
		if (commands.size() > 0) {
			command = commands.get(0);
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

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Dispatches events from the event broker to the proper bean slots.
	 * 
	 * @param event
	 */
	protected void dispatchEventBrokerEvent(final Event event) {
		if (redirectedEventtopics == null) {
			return;
		}
		final String eventTopic = topicNormalizer.unwrapTopic(event.getTopic());
		if (!redirectedEventtopics.containsKey(eventTopic)) {
			return;
		}

		final Object newBean = event.getProperty(IEventBroker.DATA);

		// create a runnable processing the set operations
		Runnable doRunnable = new Runnable() {
			@Override
			public void run() {
				for (YBeanSlot yBeanSlot : redirectedEventtopics
						.get(eventTopic)) {
					final ISlot slot = viewContext.getBeanSlot(yBeanSlot
							.getName());

					// TODO workaround for databinding -> New instance may be
					// polymorphic brother of the current instance. And if
					// binding
					// the new instance, numeric field will not become unbound.
					// So
					// lets set a new instance of current set instance before
					// setting the new entry.
					Object oldValue = slot.getValue();
					if (oldValue != null) {
						Class<?> valueClass = oldValue.getClass();
						try {
							// now all fields will become unbound from the
							// current
							// instance
							slot.setValue(valueClass.newInstance());
						} catch (Exception e) {
							LOGGER.warn("Could not reset the value by {}",
									valueClass.getName());
						}
					}

					slot.setValue(newBean);
				}
			}
		};

		if (isBeanslotDirty(eventTopic, newBean)) {
			// show an accept loosing data dialog
			AcceptLoosingDataDialog.showDialog(i18nService, resourceProvider,
					doRunnable, null);
		} else {
			doRunnable.run();
		}

	}

	/**
	 * Returns true, if one of the bean slots addressed by the eventTopic is
	 * dirty.
	 * 
	 * @param eventTopic
	 * @return
	 */
	protected boolean isBeanslotDirty(String eventTopic, Object newBean) {
		boolean dirty = false;
		for (YBeanSlot yBeanSlot : redirectedEventtopics.get(eventTopic)) {
			final ISlot slot = viewContext.getBeanSlot(yBeanSlot.getName());
			Object currentBean = slot.getValue();
			if (currentBean != null && currentBean != newBean) {
				try {
					dirty = DtoUtils.isDirty(currentBean);
					if (dirty) {
						// dirty found and leave
						break;
					}
				} catch (IllegalAccessException e) {
					// if there is no dirty flag, we just ignore it
				}
			}
		}
		return dirty;
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
						YExposedAction yOther = (YExposedAction) item
								.getTransientData().get(
										IE4Constants.PARAM_ACTION);
						if (yOther != null
								&& yOther.getId().equals(yAction.getId())) {
							boolean newEnabled = msg.getNewBooleanValue();
							handledItem.setEnabled(newEnabled);
							break;
						}
					}
				} else if (msg.getFeature() == CoreModelPackage.Literals.YEXPOSED_ACTION__INTERNAL_CLICK_TIME) {
					YExposedAction yAction = (YExposedAction) msg.getNotifier();
					for (MToolBarElement item : mPart.getToolbar()
							.getChildren()) {
						if (!(item instanceof MHandledToolItem)) {
							continue;
						}
						YExposedAction yOther = (YExposedAction) item
								.getTransientData().get(
										IE4Constants.PARAM_ACTION);
						if (yOther != null
								&& yOther.getId().equals(yAction.getId())) {
							MHandledToolItem handledItem = (MHandledToolItem) item;
							IPartItemExecutionService service = mPart
									.getContext().get(
											IPartItemExecutionService.class);
							if (service != null
									&& service.canExecuteItem(handledItem)) {
								// notify the exposed action about the external
								// click
								yAction.setExternalClickTime(new Date()
										.getTime());
								service.executeItem(handledItem);
							} else {
								// notify the action about the cancel
								ActionCanceledAdapter.notify(yAction);
							}
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Notifies the action about a cancel.
	 */
	private static class ActionCanceledAdapter implements Runnable {
		private final YExposedAction action;

		public static void notify(YExposedAction action) {
			action.setCanceledNotificationTime(new Date().getTime());
		}

		public ActionCanceledAdapter(YExposedAction action) {
			super();
			this.action = action;
		}

		@Override
		public void run() {
			notify(action);
		}
	}

	/**
	 * Notifies the action about their proper execution.
	 */
	private static class ActionExecutedAdapter implements Runnable {
		private final YExposedAction action;

		public static void notify(YExposedAction action) {
			action.setExecutedNotificationTime(new Date().getTime());
		}

		public ActionExecutedAdapter(YExposedAction action) {
			super();
			this.action = action;
		}

		@Override
		public void run() {
			notify(action);
		}
	}

	/**
	 * Notifies the action about the external click.
	 */
	private static class ActionExternalClickedAdapter implements Runnable {
		private final YExposedAction action;

		public static void notify(YExposedAction action) {
			action.setExternalClickTime(new Date().getTime());
		}

		@SuppressWarnings("unused")
		public ActionExternalClickedAdapter(YExposedAction action) {
			super();
			this.action = action;
		}

		@Override
		public void run() {
			notify(action);
		}
	}

}
