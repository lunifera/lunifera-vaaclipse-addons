package org.lunifera.vaaclipse.addons.application.handler;

import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MItem;
import org.lunifera.vaaclipse.addons.common.api.di.Load;

public class OpenBrowserHandler extends AbstractHandler {

	@Execute
	public void execute(@Active MContext context, @Active MPart part,
			@Active MItem item) {
		final IEclipseContext pmContext = createCallbackContext(context, item);
		ContextInjectionFactory.invoke(part.getObject(), Load.class, pmContext,
				null);

		// if (uiActionId != null && !uiActionId.equals("")) {
		// ContextInjectionFactory.invoke(part.getObject(), Callback.class,
		// pmContext, null);
		// }
	}

	@CanExecute
	public boolean canExecute() {
		return true;
	}
}
