<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<script src="js/110.js"></script>
<div class="infopanel11" id="110">
	<div class="info">
		<div class="infotitle">
			<div class="title">部门信息</div>
		</div>
		<form id="branchform">
		<input type="hidden" id="branchid" name="branchid">
		<div class="line11" style="padding-left: 28px;"><span style='color:red'>*</span>部门名称：
			<input type="text" id="branchname" class='required' name="branchname" >
		</div>
		<div class="line11" style="padding-left: 28px;"><span style='color:red'>*</span>部门领导：
			<input type="text" id="branchmanager" class='treeedit required' name="branchmanager" readonly class='treeedit' style='width: 280px'>
			<input class='treeedit xiala' id='xiala10' readonly></input>
			<input type="hidden" id="branchmanagerid" name="branchmanagerid">
			<span id="branchaddmember" style="color: rgb(255,162,0);font-size: 13px; cursor:pointer">
				<img src="images/addmember.png" style="padding: 0 5px 0 10px">添加人员
			</span>
			<div class='treewrap' id='tree110wrap' style='height: 270px; overflow: auto'>
				<div id='tree110' class='ztree'></div>
			</div>
		</div>
		<div class="line11">部门地址：
			<input type="text" id="branchaddress" name="branchaddress">
		</div>
		<div class="line11">部门电话：
			<input type="text" id="branchtelephone" name="branchtelephone">
		</div>
		<div class="line11">部门网址：
			<input type="text" id="branchwebsite" name="branchwebsite">
		</div>
		<div class="line11">部门传真：
			<input type="text" id="branchfax" name="branchfax">
		</div>
		<div class="line11" style="height: 240px;"><span style="vertical-align: top">部门简介：</span>
			<textarea id="branchintro" name="branchintro"></textarea>
		</div>
		</form>
		<div class="line11" style="margin-left: 95px">
			<button id="branchsave">保存</button>
		</div>
		<p>&nbsp;</p>
	</div>
</div>