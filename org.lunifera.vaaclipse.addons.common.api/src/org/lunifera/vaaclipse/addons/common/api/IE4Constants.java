package org.lunifera.vaaclipse.addons.common.api;

public interface IE4Constants {

	/**
	 * Every fired command may provide the original id from the UI action. For
	 * instance a button in ECView. This original value is passed by the command
	 * and finally associated with this parameter in the context.
	 */
	public static final String PARAM_ACTION_ID = "uiActionId";
	public static final String COMMAND_SAVE = "org.lunifera.vaaclipse.addons.application.command.save";
	/**
	 * Contains the original action id from ECView or XWT or...
	 */
	public static final String COMMAND_SAVE__ACTION_ID = COMMAND_SAVE + "."
			+ PARAM_ACTION_ID;
	public static final String COMMAND_DELETE = "org.lunifera.vaaclipse.addons.application.command.delete";
	/**
	 * Contains the original action id from ECView or XWT or...
	 */
	public static final String COMMAND_DELETE__ACTION_ID = COMMAND_DELETE + "."
			+ PARAM_ACTION_ID;
	public static final String COMMAND_PART_CALLBACK = "org.lunifera.vaaclipse.addons.application.command.partcallback";
	/**
	 * Contains the original action id from ECView or XWT or...
	 */
	public static final String COMMAND_PART_CALLBACK__ACTION_ID = COMMAND_PART_CALLBACK
			+ "." + PARAM_ACTION_ID;
	public static final String COMMAND_LOAD = "org.lunifera.vaaclipse.addons.application.command.save";
	public static final String COMMAND_LOAD__ACTION_ID = COMMAND_LOAD + "."
			+ PARAM_ACTION_ID;

	public static final String PROP_INPUT_VIEW_ID = "viewId";
	public static final String BUNDLECLASS_GENERIC_ECVIEW_VIEWPART = "bundleclass://org.lunifera.vaaclipse.addons.ecview/org.lunifera.vaaclipse.addons.ecview.views.GenericECViewView";
	public static final String ID__PARTSTACK__DYNAMIC_APPLICATIONS = "org.lunifera.vaaclipse.addons.application.partstack.areadefaultstack";

	/**
	 * Used as property in OSGi services.
	 */
	public static final String APPLICATION_ID = "e4.application.id";

}
