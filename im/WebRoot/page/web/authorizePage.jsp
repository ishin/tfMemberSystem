<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/html">
<head lang="en">
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, user-scalable=no">
    <title></title>
    <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/authorize.css"/>
    <script src="<%=request.getContextPath() %>/page/web/js/jquery-2.1.1.min.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/authorize.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/md5.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/config.js"></script>

    </head>
<body>
    <div class="authorize-header">
        <p class="phoneHide">IMS帐号安全登录</p>
        <p class="chatHide">IMS</p>
    </div>
    <div class="authorize-body">
        <div class="authorize-body-content">
            <div class="authorize-login">
                <h1 class="phoneHide">账户密码登录</h1>
                <h1 class="chatHide">请使用你的IMS帐号访问</h1>

                <%--<c:if test="${error != null && error != ''}">--%>
                    <%--<p class="login-tips phoneHide" style="visibility:visible;"><i>!</i>IMS授权失败请重新授权</p>--%>

                <%--</c:if>--%>
                <%--<c:if test="${error == null}">--%>
                    <%--<p class="login-tips phoneHide" style="visibility:hidden;"><i>!</i>&nbsp;</p>--%>
                <%--</c:if>--%>
    <%--<h2 class="phoneHide">推荐使用<span>快速安全登录</span></h2>--%>
                <p class="login-tips phoneHide" style="visibility:hidden;"><i>!</i>IMS授权失败请重新授权</p>
                <form class="authorize-submit" action="<%=request.getContextPath()%>/auth!reqAuthorizeOne" method="post">
                    <input placeholder="用户名" name="userName" class="authorize-user" type="text"/><label class="label-user" for="authorize-user"></label>
                    <input placeholder="密码" name="userPwd" class="authorize-psd" type="password"/><label class="label-psd" for="authorize-psd"></label>
                    <input value="授权并登录" class="authorize-signin" type="button" />
                </form>
                <a class="login-ap phoneHide">账号密码登录</a>

            </div>
            <div class="authorize-login-des">
                <h2><span>团餐Saas平台</span>将获得以下权限</h2>
                <div class="checkMem checkMem1">
                    <span class="chatLeftIcon dialogCheckBox CheckBoxChecked" onclick="return false"></span>
                    <span class="checkDes">获得你的姓名、头像、性别、职位</span>
                </div>
                <div class="checkMem checkMem2">
                    <span class="chatLeftIcon dialogCheckBox CheckBoxChecked" onclick="return false"></span>
                    <span class="checkDes">获得你的联系方式、所属公司、邮箱</span>
                </div>
                <span class="checkDescribe phoneHide">授权表明你已同意IMS登陆协议</span>
            </div>
        </div>
    </div>
</body>
</html>