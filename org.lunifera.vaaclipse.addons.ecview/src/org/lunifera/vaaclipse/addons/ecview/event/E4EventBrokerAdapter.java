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
package org.lunifera.vaaclipse.addons.ecview.event;

import org.osgi.service.event.EventHandler;

/**
 * This adapter adapts from the e4 eventbroker to the ecview eventbroker. So the
 * e4 eventbroker can be used in ecview too.
 */
public class E4EventBrokerAdapter implements
		org.lunifera.runtime.common.event.IEventBroker {

	private org.eclipse.e4.core.services.events.IEventBroker e4EventBroker;

	public E4EventBrokerAdapter(
			org.eclipse.e4.core.services.events.IEventBroker e4EventBroker) {
		this.e4EventBroker = e4EventBroker;
	}

	@Override
	public boolean send(String topic, Object data) {
		return e4EventBroker.send(topic, data);
	}

	@Override
	public boolean post(String topic, Object data) {
		return e4EventBroker.post(topic, data);
	}

	@Override
	public boolean subscribe(String topic, EventHandler eventHandler) {
		return e4EventBroker.subscribe(topic, eventHandler);
	}

	@Override
	public boolean subscribe(String topic, String filter,
			EventHandler eventHandler, boolean headless) {
		return e4EventBroker.subscribe(topic, filter, eventHandler, headless);
	}

	@Override
	public boolean unsubscribe(EventHandler eventHandler) {
		return e4EventBroker.unsubscribe(eventHandler);
	}

}
