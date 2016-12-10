package com.nix.dao.hiber;

import com.nix.dao.RoleDao;
import com.nix.model.Role;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository("roleDao")
@Transactional(propagation = Propagation.MANDATORY)
public class HRoleDao implements RoleDao {

    private static final Logger log = LoggerFactory.getLogger(HRoleDao.class);
    private final SessionFactory sessionFactory;

    private static final String DELETE_QUERY = "delete Role where id=:id";
    private static final String FIND_BY_NAME_QUERY = "from Role r where lower(r.name)=:name";

    @Autowired
    public HRoleDao(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(Role role) {
        log.trace("create with param: {}", role);

        sessionFactory.getCurrentSession().save(role);
    }

    @Override
    public void update(Role role) {
        log.trace("update with param: {}", role);

        Session session = sessionFactory.getCurrentSession();
        if (session.find(Role.class, role.getId()) != null) {
            session.merge(role);
        }
    }

    @Override
    public void remove(Role role) {
        log.trace("remove with param: {}", role);

        int removedRows = sessionFactory.getCurrentSession()
                .createQuery(DELETE_QUERY)
                .setParameter("id", role.getId())
                .executeUpdate();
        log.trace("rows removed {}", removedRows);
    }

    @Override
    @Transactional(readOnly = true)
    public Role findByName(String name) {
        log.trace("findByName with param: {}", name);

        return sessionFactory.getCurrentSession()
                .createQuery(FIND_BY_NAME_QUERY, Role.class)
                .setParameter("name", name.toLowerCase())
                .uniqueResult();
    }
}
