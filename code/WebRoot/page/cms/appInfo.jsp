<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<jsp:include page="0.jsp" flush="true" />
	<link rel="stylesheet" href="css/window.css"/>
	<script src="js/appInfo.js"></script>
	<script src="js/Paging.js"></script>
	<script src="js/window.js"></script>
</head>
<body>
<div class="dialogMask"></div>

<div id='container'>

<div class="admheader">
	<ul class="admheadermenu">
		<li>统一认证平台</li>
		<%--<li class="active">基本设置</li>--%>
		<%--<li><a href="21.jsp">高级设置</a></li>--%>
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
		<div class="menu menuactive"><img src='images/appInfo.png' class='menuicon'>应用信息配置</div>
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
			<span class="plusApp">+</span>
		</div>
		<div style="width:100%;padding:0 30px;">
			<table class="t112">
				<thead>
					<tr>
						<th width="10%">应用名称</th>
						<th width="10%">appid</th>
						<th width="10%">appSecret</th>
						<th width="20%">backurl</th>
						<th width="10%">状态</th>
						<th width="10%">操作人</th>
						<th width="15%">操作日期</th>
						<th width="15%">操作</th>

					</tr>
				</thead>
				<tbody id='grouplist'>
<!-- 				<tr>
						<td>4361784</td>
						<td>天坊test</td>
						<td>张宝宝</td>
						<td>2016-10-19</td>
						<td>
							<button>修改创建者</button>
							<button>解散群</button>
						</td>
					</tr>
 -->			</tbody>
			</table>
			<div class="paging">
			</div>
			<%--<div style='margin: 30px 0'>--%>
				<%--&lt;%&ndash;<div class='toright leftspace15' id='pagelast'><img src='images/lastpage_1.png' /></div>&ndash;%&gt;--%>
				<%--&lt;%&ndash;<div class='toright leftspace15' id='pagenext'><img src='images/next_1.png' /></div>&ndash;%&gt;--%>
				<%--&lt;%&ndash;<div class='toright leftspace15' id='pagecurr'>1/10</div>&ndash;%&gt;--%>
				<%--&lt;%&ndash;<div class='toright leftspace15' id='pageprev'><img src='images/back_1.png' /></div>&ndash;%&gt;--%>
				<%--&lt;%&ndash;<div class='toright leftspace15' id='pagefirst'><img src='images/firstpage_1.png' /></div>&ndash;%&gt;--%>
			<%--</div>--%>
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

<div class="dialogApp">
	<div class="dialogHeader">
		<p class="diaTitle">新增应用</p>
		<span class="closeX">&times;</span>
	</div>
	<div class="dialogBody">
		<ul>
			<li>
				<span>应用名称：</span><span class="necc">*</span>
				<input type="text" id="name"/>
			</li>
			<li>
				<span>appid：</span><span class="necc">*</span>
				<input type="text" id="appid"/>
			</li>
			<li>
				<span>appsecret：</span><span class="necc">*</span>
				<input type="text" id="appsecret"/>
			</li>
			<li>
				<span>backurl：</span><span class="necc">*</span>
				<input type="text" id="backurl"/>
			</li>
			<li id="isOpen">
				<span>是否开启：</span><span class="necc">*</span>
				<span class="radio chatLeftIcon dialogCheckBox CheckBoxChecked" value="1"></span>
				<span class="dialogGroupName">开启</span>
				<span class="radio chatLeftIcon dialogCheckBox" value="0"></span>
				<span class="dialogGroupName">停用</span>
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
