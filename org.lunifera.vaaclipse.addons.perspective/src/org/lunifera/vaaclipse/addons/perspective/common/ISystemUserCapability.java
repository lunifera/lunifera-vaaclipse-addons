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
package org.lunifera.vaaclipse.addons.perspective.common;

public interface ISystemUserCapability {

	/**
	 * Returns true, if the user is allowed to save perspectives under the
	 * system user.
	 * 
	 * @param userId
	 *            - If null, then the system needs to use the current user.
	 * @return
	 */
	boolean hasCapability(String userId);

}
