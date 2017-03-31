<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<script src="js/11_member.js" language="javascript"></script>
</head>
<body>
<div class="modal-body">
	<div>
		<div class='dialogtitle'>
			<div class='toleft dtitle'>添加人员</div>
			<div class='toright dclose' onclick="$('#member').modal('hide');">×</div>
		</div>
	</div>
	<div class='h40px'></div>
	<div>
		<div class='dialogtitle' style='margin-left: 10px'><span style='color:red'>*</span>成员帐号：	
			<input type='text' id='11memberaccount' name='11memberaccount' class='w270px toright rightspace120 required'>
		</div>
	</div>
	<div class='linespace'></div>
	<div>
		<div class='dialogtitle' style='margin-left: 10px'><span style='color:red'>*</span>手机号：	
			<input type='text' id='11membermobile' name='11membermobile' class='w270px toright rightspace120 required'>
		</div>
	</div>
	<div class='linespace'></div>
	<div>
		<div class='dialogtitle' style='margin-left: 10px'><span style='color:red'>*</span>姓名：
			<input type='text' id='11membername' name='11membername' class='w270px toright rightspace120 required'>
		</div>
	</div>
	<div class='linespace'></div>
	<div>
		<div class='dialogtitle' id='cyss' style='padding-right: 120px;'>成员所属：
			<input class='treeedit1 xiala toright' id='xiala11' readonly></input>
			<input type='text' id='11memberbranch' name='11memberbranch' class='treeedit1 w252px toright' readonly>
			<input type="hidden" id="11memberbranchid" name="11memberbranchid">
			<div class='treewrap1' id='tree11memberwrap' style='height: 270px; overflow: auto'>
				<div id='tree11member' class='ztree'></div>
			</div>
		</div>
	</div>
	<div class='linespace'></div>
	<div>
		<div class='dialogtitle'>性别：
			<select id="11membersex" name="11membersex" class='w270px toright rightspace120'>
				<option>性别</option>
			</select>
		</div>
	</div>
	<div class='linespace'></div>
	<div>
		<div class='dialogtitle'>职务：
			<select id="11memberpositionid" name="11memberpositionid"  class='w270px toright rightspace120'>
				<option>性别</option>
			</select>
		</div>
	</div>
	<div class='linespace'></div>
	<div>
		<div class='dialogtitle'>E-mail：
			<input type='text' id="11memberemail" name="11memberemail"  class='w270px toright rightspace120'>
		</div>
	</div>
	<div class='linespace'></div>
	<div>
		<div class='dialogtitle'>身份权限：
			<select id="11memberroleid" name="11memberroleid" class='w270px toright rightspace120'>
				<option>性别</option>
			</select>
		</div>
	</div>
	<div class='linespace'></div>
	<div>
		<div class='dialogtitle'>成员简介：
			<textarea id="11memberintro" name="11memberintro" class='toright w390px h130px'></textarea>
		</div>
	</div>
	<div class='h130px'></div>
	<div>
		<div class='dialogtitle'>
			<input id='11membercontinue' type="checkbox" class='toleft' style='margin: 1px 0 0'>&nbsp;&nbsp;继续添加下一个成员
			<button class='toright leftspace15 cancel' onclick="$('#member').modal('hide');">取消</button>
			<button class='toright leftspace15' id='save11member'>确定</button>
		</div>
	</div>	
	<div class='h10px'></div>
</div>
</body>
</html>