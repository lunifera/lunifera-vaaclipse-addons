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
import org.lunifera.vaaclipse.addons.common.api.di.Delete;

/**
 * The enablement of this handler is controlled by the item itself.
 */
public class ExtDeleteHandler extends AbstractHandler {
	@Execute
	public void execute(@Active MContext context, @Active MPart part,
			@Active MItem item) {
		final IEclipseContext pmContext = createCallbackContext(context, item);
		ContextInjectionFactory.invoke(part.getObject(), Delete.class,
				pmContext, null);
	}

	@CanExecute
	public boolean canExecute(@Active MItem item) {
		// since enablement is controlled by external view
		return item.isEnabled();
	}
}
