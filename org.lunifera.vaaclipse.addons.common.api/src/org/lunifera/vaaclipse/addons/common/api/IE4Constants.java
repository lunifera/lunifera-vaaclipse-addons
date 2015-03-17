package org.lunifera.vaaclipse.addons.common.api;

public interface IE4Constants {

	/**
	 * The instance of the ui action. For instance YExposedAction from ECView,
	 * or any other action for XWT,...
	 */
	public static final String PARAM_ACTION = "uiActionInstance";

	/**
	 * The class which should be used as key, to register the
	 * {@link #PARAM_ACTION} in the eclipse context.
	 */
	public static final String PARAM_ACTION_TYPE_KEY = "uiActionKeyType";

	/**
	 * The id of the save command.
	 */
	public static final String COMMAND_SAVE = "org.lunifera.vaaclipse.addons.application.command.save";
	// /**
	// * Contains the original action id from ECView or XWT or...
	// */
	// public static final String COMMAND_SAVE__ACTION_ID = COMMAND_SAVE + "."
	// + PARAM_ACTION_ID;
	/**
	 * The id of the delete command.
	 */
	public static final String COMMAND_DELETE = "org.lunifera.vaaclipse.addons.application.command.delete";
	// /**
	// * Contains the original action id from ECView or XWT or...
	// */
	// public static final String COMMAND_DELETE__ACTION_ID = COMMAND_DELETE +
	// "."
	// + PARAM_ACTION_ID;

	/**
	 * The id of the default part callback command.
	 */
	public static final String COMMAND_DEFAULT_PART_CALLBACK = "org.lunifera.vaaclipse.addons.application.command.default";
	// /**
	// * Contains the original action id from ECView or XWT or...
	// */
	// public static final String COMMAND_PART_CALLBACK__ACTION_ID =
	// COMMAND_PART_CALLBACK
	// + "." + PARAM_ACTION_ID;

	/**
	 * The id of the load command.
	 */
	public static final String COMMAND_LOAD = "org.lunifera.vaaclipse.addons.application.command.load";

	// /**
	// * Contains the original action id from ECView or XWT or...
	// */
	// public static final String COMMAND_LOAD__ACTION_ID = COMMAND_LOAD + "."
	// + PARAM_ACTION_ID;

	public static final String PROP_INPUT_VIEW_ID = "viewId";
	public static final String BUNDLECLASS_GENERIC_ECVIEW_VIEWPART = "bundleclass://org.lunifera.vaaclipse.addons.ecview/org.lunifera.vaaclipse.addons.ecview.views.GenericECViewView";
	
	public static final String ID__PARTSTACK__LEFT = "org.lunifera.vaaclipse.addons.application.partstack.left";
	public static final String ID__PARTSTACK__RIGHT = "org.lunifera.vaaclipse.addons.application.partstack.right";
	public static final String ID__PARTSTACK__MAIN = "org.lunifera.vaaclipse.addons.application.partstack.main";
	public static final String ID__PARTSTACK__BOTTOM = "org.lunifera.vaaclipse.addons.application.partstack.bottom";
	public static final String ID__PARTSTACK__MASTER_DETAIL__MASTER = "org.lunifera.vaaclipse.addons.application.partstack.materdetail.master";
	
	
	/**
	 * Used as property in OSGi services.
	 */
	public static final String APPLICATION_ID = "e4.application.id";

}
