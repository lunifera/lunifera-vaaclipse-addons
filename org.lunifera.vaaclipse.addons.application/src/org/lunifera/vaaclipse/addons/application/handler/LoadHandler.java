package org.lunifera.vaaclipse.addons.application.handler;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.lunifera.vaaclipse.addons.common.api.IE4Constants;
import org.lunifera.vaaclipse.addons.common.api.di.Callback;

public class LoadHandler extends AbstractHandler {
	@Execute
	public void execute(
			@Named(IServiceConstants.ACTIVE_PART) MContext context,
			@Named(IServiceConstants.ACTIVE_PART) MPart part,
			@Optional @Named(IE4Constants.COMMAND_LOAD__ACTION_ID) String uiActionId) {

		if (uiActionId != null && !uiActionId.equals("")) {
			ContextInjectionFactory.invoke(part.getObject(), Callback.class,
					createCallbackContext(context, uiActionId), null);
		}
	}
}