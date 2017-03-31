<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<script src="js/21_role.js" language="javascript"></script>
</head>
<body>
<div class="modal-body">
	<div class='h5px'></div>
	<div>
		<div class='dialogtitle'>
			<div class='toleft dtitle'>添加身份</div>
			<div class='toright dclose' onclick="$('#role').modal('hide');">×</div>
		</div>
	</div>
	<div class='h40px'></div>
	<div>
		<div class='dialogtitle'><div class='toleft'>身份名称：</div>
			<input type='text' id='21_rolename' class='toleft required' style='width: 270px; margin-left: 20px' />
			<div class='toleft' style='margin-left: 117px'>身份模板：</div>
			<select id="21_roletemplate" class='toleft' style='width: 270px;margin-left: 20px'>
				<option>性别</option>
			</select>
		</div>
	</div>
	<div class='h60px'></div>
	<div>
		<div class='dialogtitle'>权限配置：
			<div id="21_list" class='toright' style='overflow-y: auto;border: 1px solid #ccc; width: 748px; height: 430px;'>
<!-- 			<div class="line211d">后台管理</div>
				<div class="line211ad">
					<div class="line2111d"><input type="checkbox" />人事管理</div>
					<div class="line2112d">
						<div class="priv2d toleft"><input type="checkbox" />查看</div>
						<div class="priv2d toleft"><input type="checkbox" />添加</div>
					</div>
				</div>
				<div class="line211bd">
					<div class="line2111d"><input type="checkbox" />部门管理</div>
					<div class="line2112d">
						<div class="priv2d toleft"><input type="checkbox" />查看</div>
						<div class="priv2d toleft"><input type="checkbox" />添加</div>
					</div>
				</div>
 -->		</div>
		</div>
	</div>
	<div style='height: 430px;'></div>
	<div>
		<div class='dialogtitle' >
			<input type="checkbox" class='toleft' id='21rolecontinue' style='margin: 1px 5px 0 0'>继续添加下一个身份
			<button class='toright leftspace15 cancel' onclick="$('#role').modal('hide');">取消</button>
			<button class='toright leftspace15' id='save21role'>确定</button>
		</div>
	</div>	
	<div class='h10px'></div>
</div>
</body>
</html>