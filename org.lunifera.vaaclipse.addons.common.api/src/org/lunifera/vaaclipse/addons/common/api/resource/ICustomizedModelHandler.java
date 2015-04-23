package org.lunifera.vaaclipse.addons.common.api.resource;

import java.io.IOException;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.fragment.MModelFragment;
import org.eclipse.e4.ui.model.fragment.MModelFragments;

public interface ICustomizedModelHandler {

	public abstract void persistCustomized(MModelFragments mFragments)
			throws IOException;

	public abstract void persistCustomized(String userId,
			MModelFragments mFragments) throws IOException;

	public abstract MModelFragments loadCustomized() throws IOException;

	public abstract MModelFragments loadCustomized(String userId)
			throws IOException;

	/**
	 * Process the given fragments.
	 * 
	 * @param customizedFragments
	 */
	public abstract void mergeFragments(
			List<MModelFragments> fragmentsContainers);

	/**
	 * Process the given fragment.
	 * 
	 * @param fragmentsContainer
	 */
	public abstract void mergeFragment(MModelFragments fragmentsContainer);

	/**
	 * Returns the fragments for the given parentId and fragmentName. Or empty
	 * list if no fragment could be found.
	 * 
	 * @param parentId
	 * @param featureName
	 * @param fragmentsContainer
	 * @return
	 */
	public abstract List<MModelFragment> findFragments(String parentId,
			String featureName, MModelFragments fragmentsContainer);

	/**
	 * Returns the fragment for the given parentId and fragmentName. Or
	 * <code>null</code> if no fragment could be found.
	 * 
	 * @param parentId
	 * @param featureName
	 * @param fragmentsContainer
	 * @return
	 */
	public abstract MModelFragment createFragment(String parentId,
			String featureName);

	/**
	 * Creates a new MModelFragment, configures it properly and adds it to the
	 * fragments container.
	 * 
	 * @param parent
	 * @param newPerspective
	 * @return
	 */
	public abstract MModelFragment addPerspectiveFragment(
			MPerspectiveStack parent, MPerspective newPerspective,
			MModelFragments fragmentsContainer);

	/**
	 * Removes the given perspective from the container.
	 * 
	 * @param userId
	 * @param parentId
	 * @param perspective
	 * 
	 * @return
	 */
	public abstract void removePerspective(String userId, String parentId,
			MPerspective perspective);

	/**
	 * Removes the given perspective from the container.
	 * 
	 * @param parentId
	 * @param perspective
	 * @param fragmentsContainer
	 * 
	 * @return
	 */
	public abstract void removePerspective(String parentId,
			MPerspective perspective, MModelFragments fragmentsContainer);

	/**
	 * Creates a new MModelFragment and configures it properly.
	 * 
	 * @param parent
	 * @param newPerspective
	 * @return
	 */
	public abstract MModelFragment createPerspectiveFragment(
			MPerspectiveStack parent, MPerspective newPerspective);

	/**
	 * Tries to find a MModelFragment for the given perspective and parent id.
	 * 
	 * @param parent
	 * @param newPerspective
	 * @return
	 */
	public abstract MModelFragment findFragmentForPerspective(String parentId,
			MPerspective perspective, MModelFragments fragmentsContainer);

	/**
	 * Validates the customized fragments.
	 * 
	 * @param mFragments
	 * @return
	 */
	public abstract boolean validateCustomizedFragements(
			MModelFragments mFragments);

}