package com.solverpeng.ssh.service;

import com.solverpeng.ssh.beans.Department;
import com.solverpeng.ssh.dao.DepartmentDao;

import java.util.List;

/**
 * @author solverpeng
 * @create 2016-11-08-16:56
 */
public class DepartmentService {
    private DepartmentDao departmentDao;

    public void setDepartmentDao(DepartmentDao departmentDao) {
        this.departmentDao = departmentDao;
    }

    public List<Department> getDepartmentList() {
        return departmentDao.getDepartmentList();
    }
}
