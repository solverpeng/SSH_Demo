package com.solverpeng.ssh.service;

import com.solverpeng.ssh.beans.Employee;
import com.solverpeng.ssh.dao.EmployeeDao;

import java.util.List;

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

}
