package com.nix.dao.hiber;

import com.nix.dao.UserDao;
import com.nix.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("userDao")
@Transactional(propagation = Propagation.MANDATORY)
public class HUserDao implements UserDao {

    private static final Logger log = LoggerFactory.getLogger(HUserDao.class);

    private static final String DELETE_NAMED_QUERY = "User.delete";
    private static final String FIND_ALL_NAMED_QUERY = "User.findAll";
    private static final String FIND_BY_LOGIN_NAMED_QUERY = "User.findByLogin";
    private static final String FIND_BY_EMAIL_NAMED_QUERY = "User.findByEmail";
    private static final String FIND_BY_ID_NAMED_QUERY = "User.findById";

    private final SessionFactory sessionFactory;

    @Autowired
    public HUserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void create(User user) {
        log.trace("create with param: {}", user);
        sessionFactory.getCurrentSession().save(user);
    }

    @Override
    public void update(User user) {
        log.trace("update with param: {}", user);

        Session session = sessionFactory.getCurrentSession();
        if (session.find(User.class, user.getId()) != null) {
            session.merge(user);
            session.flush();
        }
    }

    @Override
    public void remove(User user) {
        log.trace("remove with param: {}", user);

        int removedRows = sessionFactory.getCurrentSession()
                .createNamedQuery(DELETE_NAMED_QUERY)
                .setParameter("login", user.getLogin())
                .executeUpdate();
        log.trace("rows removed {}", removedRows);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        log.trace("findAll invoked");

        return sessionFactory.getCurrentSession()
                .createNamedQuery(FIND_ALL_NAMED_QUERY, User.class)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public User findByLogin(String login) {
        log.trace("findByLogin with param: {}", login);

        return sessionFactory.getCurrentSession()
                .bySimpleNaturalId(User.class)
                .load(login);
    }

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        log.trace("findByEmail with param: {}", email);

        List<User> users = sessionFactory.getCurrentSession()
                .createNamedQuery(FIND_BY_EMAIL_NAMED_QUERY, User.class)
                .setParameter("email", email)
                .getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

}
