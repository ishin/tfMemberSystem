<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<script src="page/admin/js/11_branch.js" language="javascript"></script>
</head>
<body>
<div class="modal-body">
	<div>
		<div class='dialogtitle'>
			<div class='toleft dtitle'>添加部门</div>
			<div class='toright dclose' onclick="$('#branch').modal('hide');">×</div>
		</div>
	</div>
	<div class='h40px'></div>
	<div>
		<div class='dialogtitle' style='padding-right: 120px; margin-left: 10px'>
			<span style='color:red'>*</span>上级部门：
			<input class='treeedit2 xiala toright' id='xiala12' readonly></input>
			<input type='text' id='11branchbranch' name='11branchbranch' class='treeedit2 w252px toright required' readonly>
			<input type="hidden" id="11branchbranchid" name="11branchbranchid">
			<div class='treewrap2' id='tree11branchbranchwrap' style='height: 270px; overflow: auto'>
				<div id='tree11branchbranch' class='ztree'></div>
			</div>
		</div>
	</div>
	<div class='linespace'></div>
	<div>
		<div class='dialogtitle' style='margin-left: 10px'>
			<span style='color:red'>*</span>部门名称：	
			<input type='text' id='11branchname' name='11branchname' class='w270px toright rightspace120 required'>
		</div>
	</div>
	<div class='linespace'></div>
	<div>
		<div class='dialogtitle' style='padding-right: 40px; margin-left: 10px'>
			<span style='color:red'>*</span>部门领导：
			<span id="11branchaddmember" class='toright' style="color: rgb(255,162,0);font-size: 13px; cursor:pointer">
				<img src="page/admin/images/addmember.png" style="padding: 0 5px 0 10px">添加人员
			</span>
			<input class='treeedit2 xiala toright' id='xiala13' readonly></input>
			<input type='text' id='11branchmanager' name='11branchmanager' class='treeedit2 w252px toright required' readonly>
			<input type="hidden" id="11branchmanagerid" name="11branchmanagerid">
			<div class='treewrap2' id='tree11branchmanagerwrap' style='height: 270px; overflow: auto'>
				<div id='tree11branchmanager' class='ztree'></div>
			</div>
		</div>
	</div>
	<div class='linespace'></div>
	<div>
		<div class='dialogtitle'>部门地址：	
			<input type='text' id='11branchaddress' name='11branchaddress' class='w270px toright rightspace120'>
		</div>
	</div>
	<div class='linespace'></div>
	<div>
		<div class='dialogtitle'>部门电话：
			<input type='text' id="11branchtelephone" name="11branchtelephone"  class='w270px toright rightspace120'>
		</div>
	</div>
	<div class='linespace'></div>
	<div>
		<div class='dialogtitle'>部门网址：
			<input type='text' id="11branchwebsite" name="11branchwebsite"  class='w270px toright rightspace120'>
		</div>
	</div>
	<div class='linespace'></div>
	<div>
		<div class='dialogtitle'>部门简介：
			<textarea id="11branchintro" name="11branchintro" class='toright w390px h130px'></textarea>
		</div>
	</div>
	<div class='h130px'></div>
	<div>
		<div class='dialogtitle'>
			<input  id='11branchcontinue'type="checkbox" class='toleft' style='margin: 1px 0 0'>&nbsp;&nbsp;继续添加下一个部门
			<button class='toright leftspace15 cancel' onclick="$('#branch').modal('hide');">取消</button>
			<button class='toright leftspace15' id='save11branch'>确定</button>
		</div>
	</div>	
	<div class='h10px'></div>
</div>
</body>
</html>