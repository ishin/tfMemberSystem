<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<jsp:include page="0.jsp" flush="true" />
<script src="js/21.js"></script>
</head>
<body>
<div id='container'>

<div class="admheader">
	<ul class="admheadermenu">
		<li>统一认证平台</li>
		<%--<li><a href="11.jsp">基本设置</a></li>--%>
		<%--<li class="active">高级设置</li>--%>
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
		<div class="menu menuactive"><img src='images/limitInfo.png' class='menuicon'>授权管理配置</div>
		<div class="menu" onclick='window.location.href="appInfo.jsp";'><img src='images/appInfo.png' class='menuicon'>应用信息配置</div>
	</div>
</div>

<div class="infopanel21">
	<div class="info" style="overflow: scroll;">
		<div class="infotitle">
			<div class="title">成员身份权限</div>
		</div>
		<div class="col21">
			<div class="col1">
				<div class="line21">
					<button id='addrole' class='cancel'><img src='images/addicon.png' style='' />添加身份</button>
					<button class='cancel' onclick='delrole()' style='width:64px; margin-left:10px;'>删除</button>
				</div>
				<div class="line21" style="height: calc(100% - 84px);">
					<ul id='list21'>
<!-- 				<li>组织管理员</li>
						<li class="active">普通成员</li>
					</ul>
 -->			</div>
			</div>
			<div class="infotitle">
				<div class="infotab" style='font-size: 16px;'>
					<div class="infotabi tabactive" onclick='showpage("210")' bindpage='210'>人员管理</div>
					<%--<div class="infotabi" onclick='showpage("211")' bindpage='211'>IMS</div>--%>
					<%--<div class="infotabi" onclick='showpage("213")' bindpage='213'>OA</div>--%>
					<%--<div class="infotabi" onclick='showpage("214")' bindpage='214'>团餐SASS</div>--%>
				</div>
				<div class="infotabr" style='padding-top: 10px'>
					<button id="editmember" class="addedit" style='width:100px'>新增/修改人员</button>
				</div>
			</div>
			<jsp:include page="210.jsp" flush="true" />
<%--<jsp:include page="211.jsp" flush="true" />--%>
<%--<jsp:include page="211edit.jsp" flush="true" />--%>
<%--<jsp:include page="213.jsp" flush="true" />--%>
<%--<jsp:include page="213edit.jsp" flush="true" />--%>
<%--<jsp:include page="214.jsp" flush="true" />--%>
<%--<jsp:include page="214edit.jsp" flush="true" />--%>

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