package com.solverpeng.ssh.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * @author solverpeng
 * @create 2016-11-08-16:53
 */
public abstract class BaseDao {
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }
}
