
package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.Installation;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class InstallationDAO extends GenericDAO<Installation, Long>{

    @SuppressWarnings("unchecked")
    public List<Installation> getByAdminId(Long id) throws MiddlewareQueryException {
        List<Installation> toreturn = new ArrayList<Installation>();
        try {
            Criteria criteria = getSession().createCriteria(Installation.class);
            criteria.add(Restrictions.eq("adminId", id));

            toreturn.addAll(criteria.list());
        } catch (HibernateException e) {
            logAndThrowException("Error with getByAdminId(id="+id+") query from Installation: " + e.getMessage(), e);
        }
        return toreturn;
    }

    public Installation getLatest(Database instance) throws MiddlewareQueryException {
        try {
            Long id = null;
            Criteria criteria = getSession().createCriteria(Installation.class);

            if (instance == Database.LOCAL) {
                criteria.setProjection(Projections.min("id"));
                id = (Long) criteria.uniqueResult();
            } else if (instance == Database.CENTRAL) {
                criteria.setProjection(Projections.max("id"));
                id = (Long) criteria.uniqueResult();
            }

            if (id != null) {
                return getById(id, false);
            }
        } catch (HibernateException e) {
            logAndThrowException("Error with getLatest(databaseInstance="+instance+") query from Installation: " + e.getMessage(), e);
        }
        return null;
    }

}
