package com.solverpeng.ssh.dao;

import com.solverpeng.ssh.beans.Department;

import java.util.List;

/**
 * @author solverpeng
 * @create 2016-11-08-16:53
 */
public class DepartmentDao extends BaseDao{

    public List<Department> getDepartmentList() {
        String hql = "from Department ";
        return getSession().createQuery(hql).list();
    }
}