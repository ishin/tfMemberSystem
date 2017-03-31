<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<script src="js/111_reset.js" language="javascript"></script>
</head>
<body>
<div class="modal-body">
	<div class='h5px'></div>
	<div>
		<div class='dialogtitle'>
			<div class='toleft  dtitle'>重置成员密码</div>
			<div class='toright dclose' onclick="$('#reset').modal('hide');">×</div>
		</div>			
	</div>
	<div class='h50px'></div>
	<div>
		<div class='dialogtitle'>成员新密码：<span style='color:red'>*</span>
			<input type='text' id='newpassword' class='required' style='width: 261px'>
		</div>
	</div>
	<div class='h20px'></div>
	<div>
		<div class='dialogtitle'>
			<div style="float: left">新密码强度：</div>
			<div id='grade1' class='grade weak leftspace8'></div> 
			<div id='grade2' class='grade grade0 leftspace1'></div> 
			<div id='grade3' class='grade grade0 leftspace1'></div> 
		</div>
	</div>
	<div class='h50px'></div>
	<div class='h20px'></div>
	<div>
		<div class='dialogtitle'>
			<button class="toright leftspace15 cancel" onclick="$('#reset').modal('hide');">取消</button>
			<button class="toright leftspace15" id='save111reset'>确定</button>
		</div>
	</div>	
</div>
</body>
</html>