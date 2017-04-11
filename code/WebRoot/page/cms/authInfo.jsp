<%@ page language="java" contentType="text/html; charset=utf-8"    pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/html">
<head>
	<%--<meta charset="UTF-8">--%>
	<jsp:include page="0.jsp" flush="true" />
	<link rel="stylesheet" href="css/window.css"/>
	<script src="js/authInfo.js"></script>
	<script src="js/Paging.js"></script>
	<script src="js/window.js"></script>
</head>
<body>
	<div class="dialogMask"></div>

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
		<div class="menu menuactive"><img src='images/authInfo.png' class='menuicon'>权限信息配置</div>
		<div class="menu" onclick='window.location.href="21.jsp";'><img src='images/limitInfo.png' class='menuicon'>授权管理配置</div>
		<div class="menu" onclick='window.location.href="appInfo.jsp";'><img src='images/group.png' class='menuicon'>应用信息配置</div>
	</div>
</div>
<div class="infopanel12">
	<div class="info">
		<div class="infotitle">
			<div class="title">群组管理</div>
		</div>
        <div class="searchArea">
			<input type="text" class="searchInput" placeholder="请输入权限名称"/>
			<button class="searchBTN">搜索</button>
			<span class="plusAuth">+</span>
        </div>
		<div style="width:100%;padding:0 30px;">
			<table class="t112">
				<thead>
					<tr>
						<th width="20%">编号</th>
						<th width="20%">权限名称</th>
						<th width="20%">权限类别</th>
						<th width="20%">所属应用</th>
						<th width="20%">操作</th>
					</tr>
				</thead>
				<tbody id='grouplist'>
     				<tr>
						<%--<td>4361784</td>--%>
						<%--<td>天坊test</td>--%>
						<%--<td>张宝宝</td>--%>
						<%--<td>2016-10-19</td>--%>
						<%--<td>--%>
							<%--<button>修改创建者</button>--%>
							<%--<button>解散群</button>--%>
						<%--</td>--%>
					</tr>
    			</tbody>
			</table>
			<div class="paging">
			</div>
		<p>&nbsp;</p>
		<p>&nbsp;</p>
		</div>
	</div>
</div>

<div id="creator" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="titleid" aria-hidden="true">
	<div class="modal-dialog" style='width: 620px'>
		<div class="modal-content" style='height: 520px'>
		</div>
	</div>
</div>

</div>

<div class="dialogAuth">
	<div class="dialogHeader">
		<p class="diaTitle">新增权限</p>
		<span class="closeX">&times;</span>
	</div>
	<div class="dialogBody">
		<ul>
			<li>
				<span>权限名称：</span><span class="necc">*</span>
				<input type="text" id="name" necc="true"/>
			</li>
			<li>
				<span>所属应用：</span><span class="necc">*</span>
				<select name="category" class="category" id="app" necc="true">
					<%--<option value="1">1</option>--%>
					<%--<option value="2">2</option>--%>
					<%--<option value="3">3</option>--%>
					<%--<option value="4">4</option>--%>
					<%--<option value="5">5</option>--%>
				</select>
			</li>
			<li>
				<span>权限类别：</span><span class="necc">*</span>
				<!--<input type="text"/>-->
				<select name="category" class="category" id="parentId" necc="true">
					<%--<option value="1">1</option>--%>
					<%--<option value="2">2</option>--%>
					<%--<option value="3">3</option>--%>
					<%--<option value="4">4</option>--%>
					<%--<option value="5">5</option>--%>
				</select>
			</li>

		</ul>
	</div>
	<div class="dialogFooter">
		<button class="certainAdd">确定</button>
		<button class="canclaDia">取消</button>
	</div>
</div>

	
</body>
</html>
