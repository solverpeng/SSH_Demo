package com.solverpeng.ssh.beans;

import java.util.Date;

/**
 * @author solverpeng
 * @create 2016-11-08-16:26
 */
public class Employee {
    private Integer id;
    private String employeeName;
    private String email;
    private Date birth;
    private Date createTime;
    private Department dept;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Employee employee = (Employee) o;

        if (id != null ? !id.equals(employee.id) : employee.id != null) return false;
        if (employeeName != null ? !employeeName.equals(employee.employeeName) : employee.employeeName != null)
            return false;
        if (email != null ? !email.equals(employee.email) : employee.email != null) return false;
        if (birth != null ? !birth.equals(employee.birth) : employee.birth != null) return false;
        if (createTime != null ? !createTime.equals(employee.createTime) : employee.createTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (employeeName != null ? employeeName.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (birth != null ? birth.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        return result;
    }

    public Department getDept() {
        return dept;
    }

    public void setDept(Department dept) {
        this.dept = dept;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", employeeName='" + employeeName + '\'' +
                ", email='" + email + '\'' +
                ", birth=" + birth +
                ", createTime=" + createTime +
                ", dept=" + dept +
                '}';
    }
}
