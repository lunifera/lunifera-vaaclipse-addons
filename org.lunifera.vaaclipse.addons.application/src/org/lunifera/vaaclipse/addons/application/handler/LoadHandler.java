package org.lunifera.vaaclipse.addons.application.handler;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.lunifera.vaaclipse.addons.common.api.IE4Constants;
import org.lunifera.vaaclipse.addons.common.api.di.Callback;
import org.lunifera.vaaclipse.addons.common.api.di.Load;

public class LoadHandler extends AbstractHandler {

	@Execute
	public void execute(
			@Active MContext context,
			@Active MPart part,
			@Optional @Named(IE4Constants.COMMAND_LOAD__ACTION_ID) String uiActionId) {

		final IEclipseContext pmContext = createCallbackContext(context,
				uiActionId);
		ContextInjectionFactory.invoke(part.getObject(), Load.class, pmContext,
				null);

//		if (uiActionId != null && !uiActionId.equals("")) {
//			ContextInjectionFactory.invoke(part.getObject(), Callback.class,
//					pmContext, null);
//		}
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}
}
