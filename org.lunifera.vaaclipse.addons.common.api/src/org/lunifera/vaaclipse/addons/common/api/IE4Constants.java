/**
 * Copyright (c) 2011 - 2015, Lunifera GmbH (Gross Enzersdorf), Loetz KG (Heidelberg)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *         Florian Pirchner - Initial implementation
 */
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
	 * The id of the ext save command. (ext means that the enablement is controlled by the view)
	 */
	public static final String COMMAND_SAVE = "org.lunifera.vaaclipse.addons.application.command.extsave";

	/**
	 * The id of the ext delete command. (ext means that the enablement is controlled by the view)
	 */
	public static final String COMMAND_DELETE = "org.lunifera.vaaclipse.addons.application.command.extdelete";

	/**
	 * The id of the ext default part callback command. (ext means that the enablement is controlled by the view)
	 */
	public static final String COMMAND_DEFAULT_PART_CALLBACK = "org.lunifera.vaaclipse.addons.application.command.extdefault";

	/**
	 * The id of the ext load command. (ext means that the enablement is controlled by the view)
	 */
	public static final String COMMAND_LOAD = "org.lunifera.vaaclipse.addons.application.command.extload";

	public static final String PROP_INPUT_VIEW_ID = "viewId";
	public static final String BUNDLECLASS_GENERIC_ECVIEW_VIEWPART = "bundleclass://org.lunifera.vaaclipse.addons.ecview/org.lunifera.vaaclipse.addons.ecview.views.GenericECViewPart";
	
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
