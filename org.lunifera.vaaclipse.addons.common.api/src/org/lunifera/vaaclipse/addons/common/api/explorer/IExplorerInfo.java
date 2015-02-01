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
package org.lunifera.vaaclipse.addons.common.api.explorer;

import com.vaadin.server.Resource;

public interface IExplorerInfo {

	/**
	 * Returns the unique ID of the info.
	 * 
	 * @return
	 */
	String getId();

	/**
	 * Returns the category of the info.
	 * 
	 * @return
	 */
	IExplorerCategory getCategory();

	/**
	 * Returns the icon.
	 * 
	 * @return
	 */
	Resource getIcon();

	/**
	 * Returns the label key for i18n translations.
	 * 
	 * @return
	 */
	String getI18nLabelKey();

	/**
	 * Returns the description key for i18n translations.
	 * 
	 * @return
	 */
	String getI18nDescriptionKey();
}
