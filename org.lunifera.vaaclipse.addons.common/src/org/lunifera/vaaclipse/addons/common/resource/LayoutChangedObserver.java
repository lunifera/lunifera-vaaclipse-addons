/**
 * Copyright (c) 2012 Committers of lunifera.org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Florian Pirchner - initial API and implementation
 */
package org.lunifera.vaaclipse.addons.common.resource;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.workbench.IModelResourceHandler;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.UIEvents.EventTags;
import org.lunifera.vaaclipse.addons.common.event.EventTopicNormalizer;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Observes changes in the layout and forces a resource save.
 */
public class LayoutChangedObserver implements EventHandler {

	private static final String ELEMENT_CONTAINER_SELECTED_ELEMENT_SET = "org/eclipse/e4/ui/model/ui/ElementContainer/selectedElement/SET";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LayoutChangedObserver.class);

	private static Set<String> TOPICS = new HashSet<String>();
	static {
		TOPICS.add(UIEvents.UIElement.TOPIC_CONTAINERDATA);
		TOPICS.add(UIEvents.ElementContainer.TOPIC_SELECTEDELEMENT);
	}

	@Inject
	private IEventBroker eventBroker;

	@Inject
	private EventTopicNormalizer topicNormalizer;

	@Inject
	private IModelResourceHandler resourceHandler;

	private Timer currentTimer;

	@PostConstruct
	public void setup() {
		for (String topic : TOPICS) {
			eventBroker.subscribe(topic, this);
		}
	}

	@Override
	public void handleEvent(Event event) {
		Object changedObj = event.getProperty(EventTags.ELEMENT);

		if (topicNormalizer.unwrapTopic(event.getTopic()).equals(
				ELEMENT_CONTAINER_SELECTED_ELEMENT_SET)) {
			if (!(changedObj instanceof MPerspectiveStack)) {
				return;
			}
		}

		synchronized (this) {
			// Wait for 500ms before saving. Most probably several events will
			// arrive in the next milli seconds
			if (currentTimer == null) {
				currentTimer = new Timer();
				currentTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						try {
							resourceHandler.save();
							LOGGER.debug("ApplicationModel-Resource saved.");
							resetTimer();
						} catch (IOException e) {
							LOGGER.warn("{}", e);
						}
					}
				}, 500);
				LOGGER.debug("Scheduled Model-Save-Timer.");
			} else {
				LOGGER.debug("Timer already active.");
			}
		}

	}

	protected void resetTimer() {
		synchronized (this) {
			currentTimer = null;
		}
	}

	public void dispose() {
		eventBroker.unsubscribe(this);
	}

}
