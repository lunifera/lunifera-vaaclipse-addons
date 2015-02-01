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

public interface IExplorerCategory extends IExplorerInfo {

	/**
	 * Returns the parent category of this category or <code>null</code> if no
	 * parent category exists.
	 * 
	 * @return
	 */
	IExplorerCategory getParentCategory();

}
