<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<jsp:include page="0.jsp" flush="true" />
<script src="page/admin/js/12.js"></script>
</head>
<body>
<div id='container'>

<div class="admheader">
	<ul class="admheadermenu">
		<li class="active">基本设置</li>
		<!-- <li><a href="21.jsp">高级设置</a></li>-->
		<li><a href="<%=request.getContextPath() %>/system!highset">高级设置</a></li>
	</ul>
    <ul class="admHeaderOper">
        <li class="admLeftIcon" id='idlogout'></li>
        <li class="admLeftIcon"></li>
    </ul>
</div>
<div class="menupanel12">
	<div id="jb" class="sidebar12">
		<!--<div class="menu" onclick='window.location.href="11.jsp";'><img src='page/admin/images/struct.png' class='menuicon'>组织结构</div>  -->
		<div class="menu" onclick='window.location.href="<%=request.getContextPath() %>/system!organFrame";'><img src='page/admin/images/struct.png' class='menuicon'>组织结构</div>
		<div class="menu menuactive"><img src='page/admin/images/organinfo.png' class='menuicon'>组织信息</div>
		<!-- <div class="menu" onclick='window.location.href="13.jsp";'><img src='page/admin/images/group.png' class='menuicon'>群组管理</div> -->
		<div class="menu" onclick='window.location.href="<%=request.getContextPath() %>/system!groupManager";'><img src='page/admin/images/group.png' class='menuicon'>群组管理</div>
	</div>
</div>
<div class="infopanel12">
	<div class="info">
		<div class="infotitle">
			<div class="title">组织信息</div>
		</div>
		<div class="col12">
			<div class="col1">
				<div class="line12a" style='font-size: 16px'>企业/机构名称</div>
				<div class="line12b">
					<input style='width: 80px;border:none' value='组织号码：' readonly/>
					<input type="text" id="code" style='background-color: #eee' disabled>
				</div>
				<div class="line12b" style='margin-left: 55px'><span style='color:red'>*</span>
					<input style='width: 80px;border:none;padding-left:0' value='组织全称：' readonly/>
					<input type="text" id="name" class='required' style='margin-left: -5px'>
					<span style="margin-left: 15px">填写后将显示在成员名片中</span>
				</div>
				<div class="line12b" style='margin-left: 55px'><span style='color:red'>*</span>
					<input style='width: 80px;border:none;padding-left:0' value='组织简称：' readonly/>
					<input type="text" id="shortname" class='required' style='margin-left: -5px'>
					<span style="margin-left: 15px">填写后将显示在客户端标题栏</span>
				</div>
				<div class="line12b">
					<input style='width: 80px;border:none' value='英文名称：' readonly/>
					<input type="text" id="englishname">
				</div>
				<div class="line12b">
					<input style='width: 80px;border:none' value='广告语：' readonly/>
					<input type="text" id="ad">
				</div>
				<div class="line12a" style='font-size: 16px'>联系方式</div>
				<div class="line12b" style='margin-left: 55px'><span style='color:red'>*</span>
					<input style='width: 80px;border:none;padding-left:0' value='所在城市：' readonly/>
					<select class="sel1" id="provinceid" class='required' style='margin-left: -5px'>
						<option value="id">上海</option>
					</select>
					<select class="sel1" id="cityid" class='required'>
						<option value="id">上海</option>
					</select>
					<select class="sel1" id="districtid" class='required'>
						<option value="id">上海</option>
					</select>
				</div>
				<div class="line12b" style='margin-left: 55px'><span style='color:red'>*</span>
					<input style='width: 80px;border:none;padding-left:0' value='联系人：' readonly/>
					<input type="text" id="contact" class='required' style='margin-left: -5px'>
				</div>
				<div class="line12b" style='margin-left: 55px'><span style='color:red'>*</span>
					<input style='width: 80px;border:none;padding-left:0' value='办公地址：' readonly/>
					<input type="text"  id="address" class='required' style="width:410px;margin-left: -5px">
					<span style="margin-left: 15px">填写后将显示在成员名片中</span>
				</div>
				<div class="line12b">
					<input style='width: 80px;border:none' value='电话：' readonly/>
					<input type="text" id="telephone">
				</div>
				<div class="line12b">
					<input style='width: 80px;border:none' value='传真：' readonly/>
					<input type="text" id="fax">
				</div>
				<div class="line12b">
					<input style='width: 80px;border:none' value='E-mail：' readonly/>
					<input type="text" id="email">
				</div>
				<div class="line12b">
					<input style='width: 80px;border:none' value='邮政编码：' readonly/>
					<input type="text" id="postcode">
				</div>
				<div class="line12b">
					<input style='width: 80px;border:none' value='网站：' readonly/>
					<input type="text" id="website">
				</div>
				<div class="line12a" style='font-size: 16px'>其他信息</div>
				<div class="line12b">
					<input style='width: 80px;border:none' value='组织性质：' readonly/>
					<select class="sel2" id="inwardid">
						<option value="id">上海</option>
					</select>
				</div>
				<div class="line12b">
					<input style='width: 80px;border:none' value='主营行业：' readonly/>
					<select class="sel2" id="industryid">
						<option value="id">上海</option>
					</select>
					<select class="sel2" id="subdustryid">
						<option value="id">上海</option>
					</select>
				</div>
				<div class="line12b">
					<input style='width: 80px;border:none' value='注册资本：' readonly/>
					<input type="text" id="capital"> <span style="margin-left: 15px">万</span>
				</div>
				<div class="line12b">
					<input style='width: 80px;border:none' value='成员人数：' readonly/>
					<select class="sel2" id="membernumber">
						<option value="1">10人以下</option>
						<option value="2">10人到100人</option>
						<option value="3">100人到500人</option>
						<option value="4">500人到1000人</option>
						<option value="5">1000人以上</option>
					</select>
				</div>
				<div class="line12b">
					<input style='width: 80px;border:none' value='电脑台数：' readonly/>
					<select class="sel2" id="computernumber">
						<option value="1">10台以下</option>
						<option value="2">10台到20台</option>
						<option value="3">20台到50台</option>
						<option value="4">50台到100台</option>
						<option value="5">100台以上</option>
					</select>
				</div>
				<div class="line12b" style="height: 200px;">
					<input style='width: 80px;border:none;vertical-align:top' value='组织简介：' readonly/>
					<textarea id="intro"></textarea>
				</div>
				<div class="line12b" style="padding: 20px 0 0 75px">
					<button id="save12">保存</button>
				</div>
				
			</div>
			<div class="col2">
				<div class="line12c toleft" style="height: 25px; line-height: 25px">信息完整度</div>
				<div id = 'complete' class="line12c toleft" style="height: 25px; line-height: 25px; width: 160px; background-color: #eee; margin-left: 10px; text-align: center">70%</div>
				<div style="padding: 10px 0 0 90px; clear:left;">
					<img width=150px height=150px id='logo'>
				</div>
				<div style="padding: 10px 0 0 135px">
					<a href="#" onclick="edit()" style='color:rgb(48,192,218)'>编辑</a>
					|
					<a href="#" onclick="del()"  style='color:rgb(48,192,218)'>删除</a>
				</div>
			</div>
		</div>
		<p>&nbsp;</p>
	</div>
</div>
<div id="logod" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="titleid" aria-hidden="true">
	<div class="modal-dialog" style="width: 600px;">
		<div class="modal-content" style="height: 350px;">
		</div>
	</div>
</div>

</div>
</body>
</html>