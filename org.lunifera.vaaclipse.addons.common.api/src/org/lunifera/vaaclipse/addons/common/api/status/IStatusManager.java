/**
 * Copyright (c) 2011 - 2014, Lunifera GmbH (Gross Enzersdorf)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * 		Florian Pirchner - Initial implementation
 */
package org.lunifera.vaaclipse.addons.common.api.status;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.lunifera.runtime.common.validation.IStatus;

/**
 * Collects status about errors and warnings. A status manager is attached to a
 * Vaaclipse session.
 */
public interface IStatusManager {

	/**
	 * An event is sent to this topic, if the active scope was changed.
	 */
	public static final String ACTIVE_SCOPE_CHANGED_TOPIC = "org/lunifera/vaaclipse/statusmanager/activescope/changed";

	/**
	 * Returns a immutable list with all scopes registered within the status
	 * manager.
	 * 
	 * @return
	 */
	List<IStatusScope> getAllScopes();

	/**
	 * Returns the scope of the active MPart.<br>
	 * Returns a DefaultScope if no MPart is active.
	 * 
	 * @return
	 */
	IStatusScope getActiveScope();

	/**
	 * Returns the scope of the given MPart.
	 * 
	 * @return
	 */
	IStatusScope getScopeFor(MPart mPart);

	/**
	 * Returns a immutable list with all status of the active MPart. <br>
	 * Returns <code>null</code> if no MPart is active.
	 * 
	 * @return
	 */
	public abstract List<IStatus> getAllStatus();

}