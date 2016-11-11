package com.solverpeng.ssh.action;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import com.solverpeng.ssh.beans.Employee;
import com.solverpeng.ssh.orm.Page;
import com.solverpeng.ssh.orm.PropertyFilter;
import com.solverpeng.ssh.service.DepartmentService;
import com.solverpeng.ssh.service.EmployeeService;
import org.apache.struts2.interceptor.RequestAware;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author solverpeng
 * @create 2016-11-08-11:40
 */
public class EmployeeAction extends ActionSupport implements RequestAware, ModelDriven<Employee>, Preparable {

    private EmployeeService employeeService;

    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    private DepartmentService departmentService;

    public void setDepartmentService(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    private Employee model;

    private Integer id;

    public void setId(Integer id) {
        this.id = id;
    }

    private InputStream inputStream;

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public Employee getModel() {
        return this.model;
    }

    private Map<String, Object> requestMap;

    @Override
    public void setRequest(Map<String, Object> map) {
        this.requestMap = map;
    }

    public String list() {
        List<Employee> employeeList = employeeService.getEmployeeList();
        requestMap.put("employeeList", employeeList);
        return "list";
    }

    public void preparePageList() {
        this.model = new Employee();
    }

    public String pageList() {
        System.out.println(model);
        Page<Employee> page = new Page<>();
        List<PropertyFilter> filters = new ArrayList<>();
        employeeService.getEmployeePageList(page, filters);
        requestMap.put("departmentList", departmentService.getDepartmentList());
        return "page";
    }

    public void prepareInput() {
        if (id != null) {
            model = employeeService.getEmployeeById(id);
        }
    }

    public String input() {
        requestMap.put("departmentList", departmentService.getDepartmentList());
        return "input";
    }

    public void prepareSave() {
        if (id == null) {
            this.model = new Employee();
        } else {
            this.model = employeeService.getEmployeeById(id);
        }
    }

    public String save() {
        if (id == null) {
            model.setCreateTime(new Date());
        }
        employeeService.saveOrUpdateEmployee(model);
        return "save";
    }

    public String delete() {
        try {
            employeeService.deleteEmployeeById(id);
            inputStream = new ByteArrayInputStream("1".getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                inputStream = new ByteArrayInputStream("0".getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
        return SUCCESS;
    }

    private String employeeName;

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String validateEmployeeName() {
        try {
            long count = employeeService.getEmployeeCountByEmployeeName(employeeName);
            if (count > 0) {
                inputStream = new ByteArrayInputStream("0".getBytes("UTF-8"));
            } else if (count == 0) {
                inputStream = new ByteArrayInputStream("1".getBytes("UTF-8"));
            } else {
                inputStream = new ByteArrayInputStream("-1".getBytes("UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return SUCCESS;
    }

    @Override
    public void prepare() throws Exception {
    }
}
