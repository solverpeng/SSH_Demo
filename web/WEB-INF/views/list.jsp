<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>List Page</title>
    <script type="application/javascript" src="${pageContext.request.contextPath}/static/scripts/jquery1.42.min.js"></script>
    <script>
        $(function () {
            $(".delete").click(function () {
                var $tr = $(this).parents("tr");
                var idVal = $tr.find("td:eq(0)").text();
                var employeeNameVal = $tr.find("td:eq(1)").text();

                var flag = confirm("确定要删除"+ employeeNameVal +"吗？");
                if (flag) {
                    $.ajax({
                        url: "emp-delete",
                        data: {
                            id : idVal,
                            time : new Date()
                        },
                        success: function(data) {
                            if(data == 1) {
                                console.log("删除成功！");
                                $tr.remove();
                            } else {
                                console.log("删除失败！");
                            }
                        }
                    })
                }
                return false;
            });
        });
    </script>
</head>
<body>
    <center>
        <s:if test="#request.employeeList == null || #request.employeeList.size() == 0">
            没有员工列表
        </s:if>
        <s:else>
            <table border="1" cellpadding="10" cellspacing="0">
                <tr>
                    <td>ID</td>
                    <td>EMPLOYEE_NAME</td>
                    <td>EMAIL</td>
                    <td>BIRTH</td>
                    <td>CREATE_TIME</td>
                    <td>DEPARTMENT</td>
                    <td>DELETE</td>
                    <td>EDIT</td>
                </tr>
                <s:iterator value="#request.employeeList" var="employee">
                    <tr>
                        <td>${employee.id}</td>
                        <td>${employee.employeeName}</td>
                        <td>${employee.email}</td>
                        <td>${employee.birth}</td>
                        <td>
                            <s:date name="createTime" format="yyyy-MM-dd hh:mm:ss"/>
                        </td>
                        <td>${employee.dept.deptName}</td>
                        <td>
                            <a href="emp-delete" class="delete">删除</a>
                        </td>
                        <td>
                            <a href="emp-input?id=${employee.id}" class="edit">修改</a>
                        </td>
                    </tr>
                </s:iterator>
            </table>
        </s:else>
    </center>
</body>
</html>