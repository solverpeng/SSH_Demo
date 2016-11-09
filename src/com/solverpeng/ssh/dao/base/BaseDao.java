package com.solverpeng.ssh.dao.base;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;

/**
 * @author solverpeng
 * @create 2016-11-08-16:53
 */
public class BaseDao<T, PK> extends HibernateDao<T, Serializable>{
    /*private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }*/
}
