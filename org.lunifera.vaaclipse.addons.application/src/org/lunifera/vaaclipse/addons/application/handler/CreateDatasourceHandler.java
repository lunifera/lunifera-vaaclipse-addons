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
package org.lunifera.vaaclipse.addons.application.handler;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MItem;
import org.lunifera.runtime.common.datasource.IDataSourceService;
import org.lunifera.runtime.common.datasource.config.CommonDatasourceConfig;
import org.lunifera.vaaclipse.addons.common.api.di.Callback;

/**
 * Creates a new datasource for the given datasource configs.
 */
public class CreateDatasourceHandler extends AbstractHandler {

	@Execute
	public void execute(@Active MContext context, @Active MPart part,
			@Active MItem item, IDataSourceService dsService,
			CommonDatasourceConfig config) {

		dsService.createDataSource(config);

		final IEclipseContext pmContext = createCallbackContext(context, item);
		ContextInjectionFactory.invoke(part.getObject(), Callback.class,
				pmContext, null);
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}
}
