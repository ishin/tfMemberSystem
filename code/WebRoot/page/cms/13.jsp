<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<jsp:include page="0.jsp" flush="true" />
<script src="js/13.js"></script>
</head>
<body>
<div id='container'>

<div class="admheader">
	<ul class="admheadermenu">
		<li class="active">基本设置</li>
		<li><a href="21.jsp">高级设置</a></li>
	</ul>
    <ul class="admHeaderOper">
        <li class="admLeftIcon" id='idlogout'></li>
        <li class="admLeftIcon"></li>
    </ul>
</div>
<div class="menupanel12">
	<div id="jb" class="sidebar12">
		<div class="menu" onclick='window.location.href="11.jsp";'><img src='images/struct.png' class='menuicon'>组织结构</div>
		<div class="menu" onclick='window.location.href="12.jsp";'><img src='images/organinfo.png' class='menuicon'>组织信息</div>
		<div class="menu menuactive"><img src='images/group.png' class='menuicon'>群组管理</div>
	</div>
</div>
<div class="infopanel12">
	<div class="info">
		<div class="infotitle">
			<div class="title">群组管理</div>
		</div>
		<div style="width:100%;padding:0 30px;">
			<table class="t112">
				<thead>
					<tr">
						<th width="15%">群号</th>
						<th width="25%">群名称</th>
						<th width="20%">创建者</th>
						<th width="15%">创建日期</th>
						<th width="25%">操作</th>
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
			<div style='margin: 30px 0'>
				<div class='toright leftspace15' id='pagelast'><img id='imglast1' src='images/lastpage_1.png' /></div>
				<div class='toright leftspace15' id='pagenext'><img id='imgnext1' src='images/next_1.png' /></div>
				<div class='toright leftspace15' id='pagecurr'>1/10</div>
				<div class='toright leftspace15' id='pageprev'><img id='imgback1' src='images/back_1.png' /></div>
				<div class='toright leftspace15' id='pagefirst'><img id='imgfirst1' src='images/firstpage_1.png' /></div>
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
</body>
</html>
