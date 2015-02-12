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

public interface IExplorerLeaf extends IExplorerInfo {

	/**
	 * Executes the info. This method may open a view or also do nothing.
	 * 
	 * @param eclipseContext
	 */
	void execute(IEclipseContext eclipseContext);
}
