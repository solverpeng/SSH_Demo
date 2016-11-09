package com.solverpeng.ssh.dao;

import com.solverpeng.ssh.beans.Department;
import com.solverpeng.ssh.dao.base.BaseDao;

import java.util.List;

/**
 * @author solverpeng
 * @create 2016-11-08-16:53
 */
public class DepartmentDao extends BaseDao<Department, Integer> {
    public List<Department> getDepartmentList() {
        String hql = "from Department ";
        return getSession().createQuery(hql).list();
    }
}