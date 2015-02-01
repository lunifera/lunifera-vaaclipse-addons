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
package org.lunifera.vaaclipse.addons.common.status;

import java.util.ArrayList;
import java.util.List;

import org.lunifera.vaaclipse.addons.common.api.status.IStatus;
import org.lunifera.vaaclipse.addons.common.api.status.IStatusManager;

public class StatusManager implements IStatusManager {

	private final List<IStatus> statusList = new ArrayList<IStatus>(10);

	@Override
	public void addStatus(IStatus status) {
		statusList.add(status);
	}

	@Override
	public void clearStatus() {
		statusList.clear();
	}

	@Override
	public List<IStatus> getAllStatus() {
		return new ArrayList<IStatus>(statusList);
	}

}
