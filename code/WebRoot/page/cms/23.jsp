<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<jsp:include page="0.jsp" flush="true" />
<script src="js/23.js"></script>
</head>
<body>
<div id='container'>

<div class="admheader">
	<ul class="admheadermenu">
		<li><a href="11.jsp">基本设置</a></li>
		<li class="active">高级设置</li>
	</ul>
    <ul class="admHeaderOper">
        <li class="admLeftIcon" id='idlogout'></li>
        <li class="admLeftIcon"></li>
    </ul>
</div>
<div class="menupanel12">
	<div id="jb" class="sidebar12">
		<div class="menu" onclick='window.location.href="21.jsp";'><img src='images/role.png' class='menuicon'>成员身份权限</div>
		<div class="menu" onclick='window.location.href="#";'><img src='images/advanced.png' class='menuicon'>高级功能</div>
		<div class="menu menuactive"><img src='images/position.png' class='menuicon'>职务职位</div>
	</div>
</div>

<div class="infopanel23">
	<div class="info" style="height: 110%" id="star">
		<div class="infotitle">
			<div class="title">职务职位</div>
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
