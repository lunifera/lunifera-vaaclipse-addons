package org.lunifera.vaaclipse.addons.common.api.status;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.lunifera.runtime.common.validation.IStatus;

/**
 * The scope is responsible to collect IStatus objects for a single part.
 */
public interface IStatusScope {

	/**
	 * Returns the MPart this scope is attached to.
	 * 
	 * @return
	 */
	MPart getMPart();

	/**
	 * Adds a new status to the list of status.
	 * 
	 * @param status
	 */
	void addStatus(IStatus status);

	/**
	 * Removes the given status.
	 * 
	 * @param status
	 */
	void removeStatus(IStatus status);

	/**
	 * Removes all collected status.
	 */
	void clearStatus();

	/**
	 * Returns a list with all status objects.
	 * 
	 * @return
	 */
	List<IStatus> getAllStatus();

}
