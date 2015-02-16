package org.lunifera.vaaclipse.addons.application.handler;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.lunifera.vaaclipse.addons.common.api.IE4Constants;

public class AbstractHandler {

	public AbstractHandler() {
		super();
	}

	/**
	 * Creates a child context and puts the uiActionId into the child context as
	 * {@link IE4Constants#PARAM_ACTION_ID}.
	 * 
	 * @param context
	 * @param uiActionId
	 * @return
	 */
	protected IEclipseContext createCallbackContext(MContext context,
			String uiActionId) {
		final IEclipseContext pmContext = context.getContext().createChild();
		pmContext.set(IE4Constants.PARAM_ACTION_ID, uiActionId);
		return pmContext;
	}

}