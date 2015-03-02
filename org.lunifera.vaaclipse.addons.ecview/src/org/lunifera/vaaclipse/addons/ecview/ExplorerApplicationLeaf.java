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
package org.lunifera.vaaclipse.addons.ecview;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.lunifera.vaaclipse.addons.common.api.explorer.AbstractExplorerInfo;
import org.lunifera.vaaclipse.addons.common.api.explorer.IExplorerLeaf;

@SuppressWarnings("restriction")
public class ExplorerApplicationLeaf extends AbstractExplorerInfo implements
		IExplorerLeaf {

	@Inject
	IEclipseContext context;

	public ExplorerApplicationLeaf() {
	}

	@Override
	public void execute(IEclipseContext context) {

		DynamicViewSupport viewSupport = ContextInjectionFactory.make(
				DynamicViewSupport.class, context);
		viewSupport
				.openNewGenericECViewView(
						getId(),
						getLabel(),
						getDescription(),
						"platform:/plugin/org.lunifera.vaaclipse.addons.application/images/package_explorer.png",
						context);

	}

}
