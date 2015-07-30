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
package org.lunifera.vaaclipse.addons.common.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.lunifera.runtime.common.validation.IStatus;
import org.lunifera.vaaclipse.addons.common.api.status.IStatusManager;
import org.lunifera.vaaclipse.addons.common.api.status.IStatusScope;

public class StatusManager implements IStatusManager {

	private final Map<MPart, IStatusScope> scopes = new HashMap<MPart, IStatusScope>();

	@Inject
	private IEventBroker eventBroker;

	private MPart activePart;

	@Override
	public List<IStatus> getAllStatus() {
		IStatusScope scope = getActiveScope();
		if (scope == null) {
			return Collections.emptyList();
		}

		return scope.getAllStatus();
	}

	/**
	 * Returns the scope of the currently active MPart.
	 * 
	 * @return
	 */
	public IStatusScope getActiveScope() {
		return scopes.get(activePart);
	}

	@Inject
	public void setActiveView(@Active MPart activePart) {
		this.activePart = activePart;
		ensureScope(this.activePart);

		eventBroker.post(ACTIVE_SCOPE_CHANGED_TOPIC, getActiveScope());
	}

	protected void ensureScope(MPart mPart) {
		if (activePart == null) {
			return;
		}

		if (!scopes.containsKey(activePart)) {
			IStatusScope scope = new StatusScope(activePart);
			scopes.put(activePart, scope);
		}
	}

	@Override
	public List<IStatusScope> getAllScopes() {
		return Collections.unmodifiableList(new ArrayList<IStatusScope>(scopes
				.values()));
	}

	@Override
	public IStatusScope getScopeFor(MPart mPart) {
		ensureScope(mPart);
		return scopes.get(mPart);
	}

	@PreDestroy
	protected void dispose() {
		scopes.clear();
		activePart = null;
	}
}
