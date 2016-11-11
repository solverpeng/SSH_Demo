package com.solverpeng.ssh.dao;

import com.solverpeng.ssh.beans.Employee;
import com.solverpeng.ssh.dao.base.BaseDao;
import com.solverpeng.ssh.orm.Page;
import com.solverpeng.ssh.orm.PropertyFilter;

import java.util.*;

/**
 * @author solverpeng
 * @create 2016-11-08-15:48
 */
public class EmployeeDao extends BaseDao<Employee, Integer> {

    public List<Employee> getEmployeeList() {
        /* 原生 HQL
        String hql = "from Employee e left outer join fetch e.dept";
        return this.getSession().createQuery(hql).list();
        */
        return getAll();
    }

    public void saveOrUpdateEmployee(Employee employee) {
        this.getSession().saveOrUpdate(employee);
    }

    public void deleteEmployeeById(Integer id) {
        /* 原生 HQL
        this.getSession().createQuery("delete from Employee where id = ?").setInteger(0, id).executeUpdate();
         */
        delete(id);
    }

    public Employee getEmployeeById(Integer id) {
        /* 原生 HQL
        String hql = "from Employee e left outer join fetch e.dept where e.id = ?";
        return (Employee) this.getSession().createQuery(hql).setInteger(0, id).uniqueResult();
         */

        /* findUnique(List<PropertyFilter> filters)
        List<PropertyFilter> list = new ArrayList<>();
        PropertyFilter propertyFilter = new PropertyFilter("EQI_id", id);
        list.add(propertyFilter);
        return findUnique(list);
        */

        return findById(id);
    }

    public long getEmployeeCountByEmployeeName(String employeeName) {

        /*原生HQL
        String hql = "select count(id) from Employee e where e.employeeName like ?";
        return (Long) this.getSession().createQuery(hql).setString(0, "%"+ employeeName +"%").uniqueResult();
        */

        /*QBC
        Criteria criteria = getSession().createCriteria(Employee.class);
        criteria.add(Restrictions.like("employeeName", employeeName, MatchMode.ANYWHERE));
        return countCriteriaResult(criteria);
        */

        String str = "%" + employeeName + "%";

        /* HQL1
        String hql = "from Employee e where e.employeeName like ?";
        countHqlResult(hql, str);
        */

        // HQL2
        String hql2 = "from Employee e where e.employeeName like :employeeName";
        Map<String, Object> map = new HashMap<>();
        map.put("employeeName", str);
        return countHqlResult(hql2, map);
    }

    public Page<Employee> getEmployeePageList(Page<Employee> page, List<PropertyFilter> filters) {
        return findPage(page, filters);
    }

    public Page<Employee> getEmployeePageList(Page<Employee> page, Object...values) {
        String hql = "";
        return findPage(page, hql, values);
    }

    public Page<Employee> getEmployeePageList(Page<Employee> page, Map<String, Object> values) {
        String hql = "";
        return findPage(page, hql, values);
    }
}
