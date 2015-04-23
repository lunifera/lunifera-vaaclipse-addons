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
package org.lunifera.vaaclipse.addons.common.api.resource;

import java.io.IOException;

import org.eclipse.e4.ui.model.fragment.MModelFragments;
import org.eclipse.e4.ui.workbench.IModelResourceHandler;

public interface ICustomizedModelResourceHandler extends IModelResourceHandler {

	public static final String SYSTEM_USER = "systemUser";

	/**
	 * Persists the given fragments for the given user.
	 * 
	 * @param userId
	 * @param mFragments
	 * @throws IOException
	 * @throws IllegalStateException
	 *             If the fragments are not configured properly
	 */
	void persistCustomized(String userId, MModelFragments mFragments)
			throws IOException;

	/**
	 * Loads the given fragments for the given user.
	 * 
	 * @param userId
	 * @return
	 * @throws IOException
	 */
	MModelFragments loadCustomized(String userId) throws IOException;
	
//	/**
//	 * Resets the model to its initial state discarding all changes.
//	 */
//	public void resetModel();

}
