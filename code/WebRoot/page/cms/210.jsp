<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<div class="col2" id='210'>
	<div class="infotitle">
		<div class="infotab" style='font-size: 16px;'>
			<div class="infotabi tabactive" style='color:rgb(40,192,210);text-align:center;width:100px;padding-bottom:11px'>人员管理</div>
			<div class="infotabi" style='color:rgb(153,153,153);text-align:center;width:100px;cursor:pointer' onclick='showpage("211")'>所有权限</div>
		</div>
		<div class="infotabr" style='padding-top: 10px'>
			<button id="editmember" class='addedit' style='width:100px'>新增/修改人员</button>
		</div>
	</div>
	<div style="width:100%;padding-left:30px;">
		<table class="t210">
			<thead>
				<tr>
					<th width="32%">名称</th>
					<th width="30%">部门</th>
					<th width="30%">职位</th>
					<th width="8%"></th>
				</tr>
			</thead>
			<tbody id='list210'>
<!-- 			<tr>
					<td>A君</td>
					<td>UI部</td>
					<td>UI设计师</td>
					<td><button>删除</button></td>
 				</tr>
-->			</tbody>
		</table>
		<div style='margin: 30px'>
			<div class='toright leftspace15' id='pagelast'><img id='imglast1' src='images/lastpage_1.png' /></div>
			<div class='toright leftspace15' id='pagenext'><img id='imgnext1' src='images/next_1.png' /></div>
			<div class='toright leftspace15' id='pagecurr'>1/10</div>
			<div class='toright leftspace15' id='pageprev'><img id='imgback1' src='images/back_1.png' /></div>
			<div class='toright leftspace15' id='pagefirst'><img id='imgfirst1' src='images/firstpage_1.png' /></div>
		</div>
	</div>
</div>