
package org.generationcp.ibpworkbench.ui.programmethods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.generationcp.commons.hibernate.ManagerFactoryProvider;
import org.generationcp.commons.util.DateUtil;
import org.generationcp.ibpworkbench.SessionData;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * Created with IntelliJ IDEA. User: cyrus Date: 11/11/13 Time: 9:48 AM To change this template use File | Settings | File Templates.
 */

@Configurable
public class ProgramMethodsPresenter implements InitializingBean {

	private boolean isCropOnly;
	private Project project;
	private CropType cropType;
	private final ProgramMethodsView view;

	private static final Logger LOG = LoggerFactory.getLogger(ProgramMethodsPresenter.class);

	@Autowired
	private ManagerFactoryProvider managerFactoryProvider;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private SessionData sessionData;

	private GermplasmDataManager gerplasmDataManager;

	public ProgramMethodsPresenter(ProgramMethodsView view, Project project) {
		this.view = view;
		this.project = project;
	}

	public ProgramMethodsPresenter(ProgramMethodsView view, CropType cropType) {
		this.view = view;
		this.cropType = cropType;
		this.isCropOnly = true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.gerplasmDataManager = this.managerFactoryProvider.getManagerFactoryForProject(this.project).getGermplasmDataManager();
	}

	public void doMoveToSelectedMethod(Integer id) {
		Method selectedMethod = null;
		try {
			selectedMethod = this.gerplasmDataManager.getMethodByID(id);

			this.view.addRow(this.convertMethod(selectedMethod), false, 0);

		} catch (MiddlewareQueryException e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
		}

	}

	public Method getMethodByID(Integer id) {
		try {
			return this.gerplasmDataManager.getMethodByID(id);

		} catch (MiddlewareQueryException e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
		}
		return null;
	}

	public void doRemoveSelectedMethod(Integer id) {
		Method selectedMethod = null;
		try {
			selectedMethod = this.gerplasmDataManager.getMethodByID(id);

			this.view.addRow(this.convertMethod(selectedMethod), true, 0);

		} catch (MiddlewareQueryException e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
		}
	}

	public List<MethodView> getSavedProgramMethods() {
		if (this.cropType != null) {
			return new ArrayList<MethodView>();
		}

		List<Method> result = new ArrayList<Method>();
		try {
			List<ProgramFavorite> favorites = this.gerplasmDataManager.getProgramFavorites(FavoriteType.METHOD, this.project.getUniqueID());

			for (ProgramFavorite favorite : favorites) {
				Method m = this.gerplasmDataManager.getMethodByID(favorite.getEntityId());

				if (m != null) {
					result.add(m);
				}
			}

		} catch (MiddlewareQueryException e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
		}

		return this.convertFrom(result);
	}

	public Collection<MethodView> getFilteredResults(String mgroup, String mtype, String mname) {
		Map<Integer, MethodView> resultsMap = new LinkedHashMap<Integer, MethodView>();

		try {
			List<MethodView> result = this.convertFrom(this.gerplasmDataManager.getMethodsByGroupAndTypeAndName(mgroup, mtype, mname));

			for (MethodView method : result) {
				if (method.getUniqueID() == null || method.getUniqueID().equals(this.project.getUniqueID())) {
					resultsMap.put(method.getMid(), method);
				}
			}

		} catch (MiddlewareQueryException e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
		}

		return resultsMap.values();
	}

	public MethodView editBreedingMethod(MethodView method) {
		MethodView result = null;
		try {
			result = this.convertMethod(this.gerplasmDataManager.editMethod(method.copy()));
		} catch (MiddlewareQueryException e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
		}

		if (!this.sessionData.getUniqueBreedingMethods().contains(result.getMname())) {
			this.sessionData.getUniqueBreedingMethods().add(result.getMname());
			this.sessionData.getProjectBreedingMethodData().put(result.getMid(), result);
		}

		this.view.refreshTable();

		return this.convertMethod(result);
	}

	public boolean isExistingMethod(String methodName) {
		Method existingMethod;
		try {
			existingMethod = this.gerplasmDataManager.getMethodByName(methodName, this.project.getUniqueID());

			if (existingMethod.getMname() != null && existingMethod.getMname().length() > 0) {
				return true;
			}

		} catch (MiddlewareQueryException e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
		}

		return false;
	}

	public MethodView saveNewBreedingMethod(MethodView method) {

		if (!this.isExistingMethod(method.getMname())) {

			this.sessionData.getUniqueBreedingMethods().add(method.getMname());

			Integer nextKey = this.sessionData.getProjectBreedingMethodData().keySet().size() + 1;

			MethodView newBreedingMethod = new MethodView();

			newBreedingMethod.setMname(method.getMname());
			newBreedingMethod.setMdesc(method.getMdesc());
			newBreedingMethod.setMcode(method.getMcode());
			newBreedingMethod.setMgrp(method.getMgrp());
			newBreedingMethod.setMtype(method.getMtype());
			newBreedingMethod.setGeneq(method.getGeneq());

			newBreedingMethod.setMid(nextKey);

			this.sessionData.getProjectBreedingMethodData().put(nextKey, newBreedingMethod);

			ProgramMethodsPresenter.LOG.info(this.sessionData.getProjectBreedingMethodData().toString());

			if (this.sessionData.getUserData() != null) {
				newBreedingMethod.setUser(this.sessionData.getUserData().getUserid());
			}

			newBreedingMethod.setLmid(0);
			newBreedingMethod.setMattr(0);
			newBreedingMethod.setMprgn(0);
			newBreedingMethod.setReference(0);

			newBreedingMethod.setMdate(DateUtil.getCurrentDateAsIntegerValue());
			newBreedingMethod.setMfprg(0);

			// set programUUID
			newBreedingMethod.setUniqueID(this.project.getUniqueID());

			// ADD TO MIDDLEWARE LOCAL
			try {
				newBreedingMethod.setMid(this.gerplasmDataManager.addMethod(newBreedingMethod.copy()));
			} catch (Exception e) {
				ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
			}

			newBreedingMethod.setIsnew(true);

			this.view.addRow(newBreedingMethod, false, 0);

			return newBreedingMethod;
		}

		return method;
	}

	public boolean saveFavoriteBreedingMethod(Collection<MethodView> selectedMethodIds) {
		return ProgramMethodsPresenter.saveFavoriteBreedingMethod(selectedMethodIds, this.project, this.sessionData,
				this.workbenchDataManager, this.gerplasmDataManager);
	}

	public static boolean saveFavoriteBreedingMethod(Collection<MethodView> selectedMethodIds, Project project, SessionData sessionData,
			WorkbenchDataManager workbenchDataManager, GermplasmDataManager gdm) {
		List<ProgramFavorite> favorites = null;
		try {
			favorites = gdm.getProgramFavorites(ProgramFavorite.FavoriteType.METHOD, project.getUniqueID());

			// TODO: THIS IS A VERY UGLY CODE THAT WAS INHERITED IN THE OLD ProjectBreedingMethodsPanel Code, Replace the logic if possible

			for (Method m : selectedMethodIds) {
				boolean mExists = false;

				for (ProgramFavorite favorite : favorites) {
					if (favorite.getEntityId().equals(m.getMid())) {
						mExists = true;
					}
				}

				if (!mExists && sessionData.getUserData() != null) {
					workbenchDataManager.addProjectActivity(new ProjectActivity(project.getProjectId().intValue(), project,
							"Project Methods", String.format("Added a Breeding Method (%s) to the project", m.getMname()), sessionData
									.getUserData(), new Date()));
				}
			}
			// code block just adds a log activity, replace by just tracking newly added methods id so no need to fetch all methods from DB

			gdm.deleteProgramFavorites(favorites);

			// Repopulate the project methods table
			List<ProgramFavorite> list = new ArrayList<ProgramFavorite>();
			int mID = 0;

			for (Method m : selectedMethodIds) {
				ProgramFavorite favorite = new ProgramFavorite();
				if (m.getMid() < 1) {
					Method m2 = gdm.getMethodByID(m.getMid());

					if (m2 == null) {
						Method newMethod =
								new Method(m.getMid(), m.getMtype(), m.getMgrp(), m.getMcode(), m.getMname(), m.getMdesc(), 0, 0, 0, 0, 0,
										0, 0, m.getMdate(), project.getUniqueID());
						mID = gdm.addMethod(newMethod);
					} else {
						mID = m2.getMid();
					}
				} else {
					mID = m.getMid();
				}

				favorite.setEntityType(ProgramFavorite.FavoriteType.METHOD.getName());
				favorite.setEntityId(mID);
				favorite.setUniqueID(project.getUniqueID());
				list.add(favorite);
			}

			gdm.saveProgramFavorites(list);

		} catch (MiddlewareQueryException e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
			return false;
		}

		return true;
	}

	public MethodView convertMethod(Method method) {
		PropertyUtilsBean pub = new PropertyUtilsBean();
		MethodView methodView = new MethodView();
		try {
			pub.copyProperties(methodView, method);

		} catch (Exception e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
		}

		return methodView;
	}

	public List<MethodView> convertFrom(List<Method> list) {
		List<MethodView> result = new ArrayList<MethodView>();
		for (Method method : list) {
			MethodView methodView = new MethodView();

			PropertyUtilsBean pub = new PropertyUtilsBean();
			try {
				pub.copyProperties(methodView, method);

				result.add(methodView);
			} catch (Exception e) {
				ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
			}
		}
		return result;
	}

	public Collection<Method> convertTo(Collection<MethodView> list) {
		List<Method> result = new ArrayList<Method>();

		for (MethodView methodView : list) {
			result.add(methodView.copy());
		}
		return result;
	}

	public Map<Integer, String> getMethodClasses() {
		Map<Integer, String> methodClasses = new LinkedHashMap<Integer, String>();
		try {
			List<Term> terms = this.gerplasmDataManager.getMethodClasses();
			if (terms != null) {
				for (Term term : terms) {
					methodClasses.put(term.getId(), term.getName());
				}
			}
		} catch (MiddlewareQueryException e) {
			ProgramMethodsPresenter.LOG.error(e.getMessage(), e);
		}

		return methodClasses;
	}

	public void setGerplasmDataManager(GermplasmDataManager gerplasmDataManager) {
		this.gerplasmDataManager = gerplasmDataManager;
	}
}
