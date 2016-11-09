package com.solverpeng.ssh.dao;

import com.solverpeng.ssh.beans.Employee;
import com.solverpeng.ssh.dao.base.BaseDao;

import java.util.List;

/**
 * @author solverpeng
 * @create 2016-11-08-15:48
 */
public class EmployeeDao extends BaseDao<Employee, Integer> {

    public List<Employee> getEmployeeList() {
        /*String hql = "from Employee e left outer join fetch e.dept";
        return this.getSession().createQuery(hql).list();*/
        List<Employee> all = getAll();
        return all;
    }

    public void saveOrUpdateEmployee(Employee employee) {
        this.getSession().saveOrUpdate(employee);
    }

    public void deleteEmployeeById(Integer id) {
        this.getSession().createQuery("delete from Employee where id = ?").setInteger(0, id).executeUpdate();
    }

    public Employee getEmployeeById(Integer id) {
        String hql = "from Employee e left outer join fetch e.dept where e.id = ?";
        return (Employee) this.getSession().createQuery(hql).setInteger(0, id).uniqueResult();
    }

    public long getEmployeeCountByEmployeeName(String employeeName) {
        String hql = "select count(id) from Employee e where e.employeeName like ?";
        return (Long) this.getSession().createQuery(hql).setString(0, "%"+ employeeName +"%").uniqueResult();

    }
}
