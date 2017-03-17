<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<jsp:include page="0.jsp" flush="true" />
<script src="page/admin/js/11.js"></script>
</head>
<body>
<div id='container'>

<div class="admheader">
	<ul class="admheadermenu">
		<li class="active">基本设置</li>
		<!-- <li><a href="21.jsp">高级设置</a></li> -->
		<li><a href="<%=request.getContextPath() %>/system!highset">高级设置</a></li>
    </ul>
    <ul class="admHeaderOper">
        <li class="admLeftIcon" id='idlogout'></li>
        <li class="admLeftIcon"></li>
    </ul>
</div>
<div class="menupanel11">
	<div id="jb" class="sidebar11">
		<div class="menu menuactive"><img src='page/admin/images/struct.png' class='menuicon'>组织结构</div>
		<%-- <div class="menu" onclick='window.location.href="12.jsp";'><img src='page/admin/images/organinfo.png' class='menuicon'>组织信息</div> --%>
		<%--<div class="menu" onclick='window.location.href="13.jsp";'><img src='page/admin/images/group.png' class='menuicon'>群组管理</div> --%>
		<div class="menu" onclick='window.location.href="<%=request.getContextPath() %>/system!organInfo";'><img src='page/admin/images/organinfo.png' class='menuicon'>组织信息</div>
		<div class="menu" onclick='window.location.href="<%=request.getContextPath() %>/system!groupManager";'><img src='page/admin/images/group.png' class='menuicon'>群组管理</div>
	</div>
	<div class="organ">
		<div class="organline">
			<button class="btnadmin" id='zzgly'><img src='page/admin/images/superadmin.png' style='padding-right: 10px'>超级管理员</button>

            <div id='downadiv' class='btn-group dropdown toright'>
            	<a id='downa' class="btn  btn-sm dropdown-toggle" data-toggle="dropdown" href="#">
            	<img src="page/admin/images/addbutton.png">
            	</a>
            	<ul id="admadd" class="dropdown-menu pull-right" style="min-width: 350%">
            		<li class="admadd addbranch">添加部门</li>
            		<li class="admadd addmember">添加人员</li>
            		<li class="admadd addbatch" style="padding-bottom: 5px">批量导入</li>
            	</ul>
            </div>
			
		</div>
		<div class="organline" >
			<input type="text" class='organsearch' placeholder="搜索人员" id="search11" />
		</div>
		<div class="organline ztree" id="tree11"></div>
	</div>
</div>

<jsp:include page="110.jsp" flush="true" />
<jsp:include page="111.jsp" flush="true" />
<jsp:include page="112.jsp" flush="true" />

<div id="branch" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="titleid" aria-hidden="true">
	<div class="modal-dialog" style='width: 540px'>
		<div class="modal-content">
		</div>	
	</div>
</div>

<div id="member" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="titleid" aria-hidden="true">
	<div class="modal-dialog" style='width: 540px'>
		<div class="modal-content">
		</div>
	</div>
</div>

<div id="position" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="titleid" aria-hidden="true">
	<div class="modal-dialog" style="width: 380px">
		<div class="modal-content" style="height: 640px;">
		</div>
	</div>
</div>

<div id="move" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="titleid" aria-hidden="true">
	<div class="modal-dialog" style="width: 380px">
		<div class="modal-content" style="height: 580px;">
		</div>
	</div>
</div>

<div id="reset" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="titleid" aria-hidden="true">
	<div class="modal-dialog" style="width: 500px;">
		<div class="modal-content" style="height: 260px;">
		</div>
	</div>
</div>

<div id="imp" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="titleid" aria-hidden="true">
	<div class="modal-dialog" style="width: 680px;">
		<div class="modal-content" style="height: 530px;">
		</div>
	</div>
</div>

<div id='downbdiv' class='btn-group dropdown toright' style='position:absolute; top: 500px'>
	<a id='downb' class="btn  btn-sm dropdown-toggle" data-toggle="dropdown" href="#"></a>
	<ul id="admb" class="dropdown-menu pull-right"  style='min-width:80px;right:-90px'>
		<li class="admadd downmov">移动</li>
		<li class="admadd downdel" style="padding-bottom: 5px">删除</li>
	</ul>
</div>

</div>
</body>
</html>
