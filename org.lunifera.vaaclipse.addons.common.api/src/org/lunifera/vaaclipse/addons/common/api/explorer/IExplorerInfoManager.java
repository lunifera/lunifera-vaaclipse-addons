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

import org.eclipse.e4.core.contexts.IEclipseContext;

/**
 * Manages the explorer info. Explorer infos are used to show applications,
 * processes,... to the user.
 */
public interface IExplorerInfoManager {

	/**
	 * Needs to return an iterable of explorer infos. These infos will be showed
	 * in the explorer view.
	 * 
	 * @param parent
	 * @param context
	 * @return
	 */
	Iterable<IExplorerInfo> getExplorerInfo(IExplorerCategory parent,
			IEclipseContext context);

}
