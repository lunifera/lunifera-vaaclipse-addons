/**
 * Copyright (c) 2011 - 2015, Lunifera GmbH (Gross Enzersdorf), Loetz KG (Heidelberg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *         Florian Pirchner - Initial implementation
 */
package org.lunifera.vaaclipse.addons.common.api;

import org.semanticsoft.vaaclipse.publicapi.resources.BundleResource;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

/**
 * A util that deals with vaadin resources.
 */
public class ResourceUtil {

	/**
	 * Returns the proper resource for the given uri.
	 * 
	 * @param iconURI
	 * @return
	 */
	public static Resource getResource(String iconURI) {
		if (iconURI.startsWith("platform:/plugin/")) {
			return BundleResource.valueOf(iconURI);
		} else if (iconURI.startsWith("theme:/")) {
			return new ThemeResource(iconURI.replace("theme:/", ""));
		} else if (iconURI.startsWith("http")) {
			return new ExternalResource(iconURI);
		}

		return new ThemeResource(iconURI);
	}

}
