package org.lunifera.vaaclipse.addons.application.handler;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.lunifera.vaaclipse.addons.common.api.IE4Constants;
import org.lunifera.vaaclipse.addons.common.api.di.Callback;

/**
 * A callback action to the active part passing the executed commandId.
 */
public class ViewCallbackHandlerer extends AbstractHandler {
	@Execute
	public void execute(
			@Active MContext context,
			@Active MPart part,
			@Named(IE4Constants.COMMAND_PART_CALLBACK__ACTION_ID) String uiActionId) {

		ContextInjectionFactory.invoke(part.getObject(), Callback.class,
				createCallbackContext(context, uiActionId), null);
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}
}
