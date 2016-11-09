<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>Create Page</title>
    <script src="${pageContext.request.contextPath}/static/scripts/jquery1.42.min.js"></script>
    <script>
        $(function () {
            $(":input[name='model.employeeName']").change(function () {
                var employeeNameVal = $.trim($(this).val());
                var $this = $(this);
                if(employeeNameVal) {
                    $this.nextAll("font").remove();
                    $.ajax({
                        url: "emp-validateEmployeeName",
                        data: {
                            employeeName: employeeNameVal,
                            time : new Date()
                        },
                        type: "post",
                        success: function (data) {
                            if(data == "1") {
                                $this.after("<font color='green'>EmployeeName可用!</font>");
                            } else if(data == "0") {
                                $this.after("<font color='red'>EmployeeName不可用!</font>");
                            } else {
                                alert("服务器错误！");
                            }
                        }
                    });
                } else {
                    alert("EmployeeName 不能为空!");
                    $(this).val("");
                    $(this).focus();
                }
            });
        })
    </script>
</head>
<body>
    <center>
        <s:fielderror fieldName="errorFiledName"/>
        <s:form action="emp-save" method="post">

            <s:if test="model.id != null">
                <s:textfield name="model.employeeName" label="employeeName" disabled="true"/>
                <s:hidden name="model.id"/>
            </s:if>
            <s:else>
                <s:textfield name="model.employeeName"  label="employeeName"/>
            </s:else>

            <s:textfield name="model.email" label="email"/>
            <s:textfield name="model.birth" label="birth"/>
            <s:select list="#request.departmentList" label="department" listKey="id" listValue="deptName"
                      name="model.dept.id"/>
            <s:submit/>
        </s:form>
    </center>
</body>
</html>
