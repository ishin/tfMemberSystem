<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!doctype html>
<html>
<head>
<jsp:include page="0.jsp" flush="true" />
<script src="js/23.js"></script>
</head>
<body>
	<c:if test="${SessionUser == null}">
	<script type="text/javascript">
	var origin = window.location.origin
	var pathName = window.location.pathname;
	var p = pathName.split("/");
	window.location.href=origin + "/" + p[1] + "/";
	</script>
	</c:if>
<div id='container'>

<div class="admheader">
	<ul class="admheadermenu">
		<li>统一认证平台</li>
	</ul>
	<ul class="admHeaderOper">
		<li class="admLeftIcon" id='idlogout'></li>
		<li class="admLeftIcon"></li>
	</ul>
</div>
<div class="menupanel12">
	<div id="jb" class="sidebar12">
	<div class="menu" onclick='window.location.href="12.jsp";'><img src='images/conpaney.png' class='menuicon'>公司信息配置</div>
	<div class="menu" onclick='window.location.href="11.jsp";'><img src='images/organize.png' class='menuicon'>组织结构配置</div>
	<div class="menu" onclick='window.location.href="authInfo.jsp";'><img src='images/authInfo.png' class='menuicon'>权限信息配置</div>
	<div class="menu" onclick='window.location.href="21.jsp";'><img src='images/limitInfo.png' class='menuicon'>授权管理配置</div>
	<div class="menu" onclick='window.location.href="appInfo.jsp";'><img src='images/appInfo.png' class='menuicon'>应用信息配置</div>
	<div class="menu menuactive"><img src='images/position.png' class='menuicon'>职务职位配置</div>
	</div>
</div>

<div class="infopanel23">
	<div class="info" style="height: 100%" id="star">
		<div class="infotitle">
			<div class="title">职务职位配置</div>
		</div>
		<div class="line23">
			职务职位：
			<input type="text" id="posname" class='required'>
			<button id="save23">添加</button>
		</div>
		<div class="line23" id='positionlist'>
<!-- 		<div class="pos">系统工程师&nbsp;&nbsp;<a href="#" onclick="delpos(1)">x</a></div>
			<div class="pos">系统工程师&nbsp;&nbsp;×</div>
 -->	</div>
	</div>
</div>

</div>
</body>
</html>
