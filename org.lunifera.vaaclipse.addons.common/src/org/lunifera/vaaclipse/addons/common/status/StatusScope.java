package org.lunifera.vaaclipse.addons.common.status;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.lunifera.runtime.common.validation.IStatus;
import org.lunifera.vaaclipse.addons.common.api.status.IStatusScope;

public class StatusScope implements IStatusScope {

	private final List<IStatus> statusList = new ArrayList<IStatus>(10);
	private final MPart part;

	public StatusScope(MPart part) {
		this.part = part;
	}

	@Override
	public MPart getMPart() {
		return part;
	}

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

	@Override
	public void removeStatus(IStatus status) {
		statusList.remove(status);
	}

}
