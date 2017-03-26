<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<jsp:include page="0.jsp" flush="true" />
<script src="page/admin/js/21.js"></script>
</head>
<body>
<div id='container'>

<div class="admheader">
	<ul class="admheadermenu">
		<!-- <li><a href="11.jsp">基本设置</a></li>-->
		<li><a href="<%=request.getContextPath() %>/system!organFrame">基本设置</a></li>
		<li class="active">高级设置</li>
	</ul>
    <ul class="admHeaderOper">
        <li class="admLeftIcon" id='idlogout'></li>
        <li class="admLeftIcon"></li>
    </ul>
</div>
<div class="menupanel12">
	<div id="jb" class="sidebar12">
		<div class="menu menuactive"><img src='page/admin/images/role.png' class='menuicon'>成员身份权限</div>
		<div class="menu" onclick='window.location.href="#";'><img src='page/admin/images/advanced.png' class='menuicon'>高级功能</div>
		<div class="menu" onclick='window.location.href="23.jsp";'><img src='page/admin/images/position.png' class='menuicon'>职务职位</div>
	</div>
</div>

<div class="infopanel21">
	<div class="info">
		<div class="infotitle">
			<div class="title">成员身份权限</div>
		</div>
		<div class="col21">
			<div class="col1">
				<div class="line21">
					<button id='addrole' class='cancel'><img src='page/admin/images/addicon.png' style='margin:-2px 5px 0 0' />添加身份</button>
					<button class='cancel' onclick='delrole()' style='width:64px;margin-left:5px'>删除</button>
				</div>
				<div class="line21" style="width:70%">
					<ul id='list21'>
<!-- 				<li>组织管理员</li>
						<li class="active">普通成员</li>
					</ul>
 -->			</div>
			</div>
<jsp:include page="210.jsp" flush="true" />
<jsp:include page="211.jsp" flush="true" />
<jsp:include page="212.jsp" flush="true" />
		</div>
		<p>&nbsp;</p>
	</div>
</div>

<div id="role" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="titleid" aria-hidden="true">
	<div class="modal-dialog" style='width: 900px'>
		<div class="modal-content" style='height: 650px'>
		</div>	
	</div>
</div>

<div id="member" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="titleid" aria-hidden="true">
	<div class="modal-dialog" style='width: 750px'>
		<div class="modal-content" style='height: 630px'>
		</div>
	</div>
</div>

</div>
</body>
</html>