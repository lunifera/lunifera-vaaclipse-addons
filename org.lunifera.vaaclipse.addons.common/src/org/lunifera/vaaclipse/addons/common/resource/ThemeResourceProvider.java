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
package org.lunifera.vaaclipse.addons.common.resource;

import org.lunifera.runtime.web.vaadin.common.resource.IResourceProvider;
import org.lunifera.vaaclipse.addons.common.api.ResourceUtil;

import com.vaadin.server.Resource;

/**
 * Vaaclipse specific resource provider. Service.ranking = 1000.
 */
public class ThemeResourceProvider implements IResourceProvider {

	@Override
	public Resource getResource(String resourcePath) {
		if(resourcePath == null || resourcePath.trim().equals("")){
			return null;
		}
		
		return ResourceUtil.getResource(resourcePath);
	}

}
