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

/**
 * Collects status about errors and warnings.
 */
public interface IStatusManager {

	/**
	 * Adds a new status to the list of status.
	 * 
	 * @param status
	 */
	public abstract void addStatus(IStatus status);

	/**
	 * Removes all collected status.
	 */
	public abstract void clearStatus();

	/**
	 * Returns a list with all status.
	 * 
	 * @return
	 */
	public abstract List<IStatus> getAllStatus();

}