package com.solverpeng.ssh.service;

import com.solverpeng.ssh.beans.Employee;
import com.solverpeng.ssh.dao.EmployeeDao;
import com.solverpeng.ssh.orm.Page;
import com.solverpeng.ssh.orm.PropertyFilter;

import java.util.List;
import java.util.Map;

/**
 * @author solverpeng
 * @create 2016-11-08-15:51
 */
public class EmployeeService {

    private EmployeeDao employeeDao;

    public void setEmployeeDao(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    public List<Employee> getEmployeeList() {
        return employeeDao.getEmployeeList();
    }

    public void saveOrUpdateEmployee(Employee employee) {
        employeeDao.saveOrUpdateEmployee(employee);
    }

    public void deleteEmployeeById(Integer id) {
        employeeDao.deleteEmployeeById(id);
    }

    public Employee getEmployeeById(Integer id) {
        return employeeDao.getEmployeeById(id);
    }

    public long getEmployeeCountByEmployeeName(String employeeName) {
        return employeeDao.getEmployeeCountByEmployeeName(employeeName);
    }

    public Page<Employee> getEmployeePageList(Page<Employee> page, List<PropertyFilter> filters) {
        return employeeDao.getEmployeePageList(page, filters);
    }

}
