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
package org.lunifera.vaaclipse.addons.common.explorer;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerCategory;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerInfo;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerInfoManager;

public class ExplorerInfoManager implements IExplorerInfoManager {

	@Override
	public Iterable<IExplorerInfo> getExplorerInfo(IExplorerCategory parent,
			IEclipseContext context) {
		return null;
	}

}
