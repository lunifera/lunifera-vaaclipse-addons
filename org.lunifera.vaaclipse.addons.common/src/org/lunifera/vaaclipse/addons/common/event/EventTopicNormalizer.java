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
package org.lunifera.vaaclipse.addons.common.event;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;

/**
 * Wraps and unwraps the event topics.
 */
public class EventTopicNormalizer {

	@Inject
	@Named("e4ApplicationInstanceId")
	@Optional
	String applicationInstanceId;

	public String wrapTopic(String topic) {
		return applicationInstanceId + "/" + topic;
	}

	public String unwrapTopic(String topic) {
		return topic.replace(applicationInstanceId + "/", "");
	}
}
