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
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.fragment.MModelFragment;
import org.eclipse.e4.ui.model.fragment.MModelFragments;

/**
 * Handles the fragment for the system user. This fragment is used to persist
 * and load common settings for all users.
 */
public interface ISystemuserModelHandler {

	/**
	 * Persists the fragment.
	 * 
	 * @param mFragments
	 * @throws IOException
	 */
	public abstract void persistFragment(MModelFragments mFragments)
			throws IOException;

	/**
	 * Process the given fragment.
	 */
	public abstract void mergeFragment();

	/**
	 * Creates a new MModelFragment, configures it properly and adds it to the
	 * fragments container.
	 * 
	 * @param parent
	 * @param newPerspective
	 */
	public abstract void addPerspectiveFragment(
			MPerspectiveStack parent, MPerspective newPerspective);

	/**
	 * Removes the fragment for the given perspective from the container.
	 * 
	 * @param parentId
	 * @param perspective
	 * 
	 * @return
	 */
	public abstract void removePerspectiveFragment(String parentId,
			MPerspective perspective);

}