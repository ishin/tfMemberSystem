<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<script src="page/admin/js/11_imp.js" language="javascript"></script>
</head>
<body>
<div class="modal-body">
	<div class='h5px'></div>
	<div>
		<div class='dialogtitle2'>
			<div class='toleft  dtitle'>批量导入成员</div>
			<div class='toright dclose' onclick="closeimp()">×</div>
		</div>			
	</div>
	<div class='h50px'></div>
	<div class='h5px'></div>
	
	<div id='imp1' style='display:'>
		<div class='dialogtitle2'>
			<img src='page/admin/images/1.png' />
		</div>
		<div class='dialogtitle2' style='margin: 95px 0 0 220px'>
			<span style='color: rgb(128,128,128); font-size: 12px'>1.请下载模板文档</span>
			<a href='../../upload/人员模板.xls' style='color:rgb(255,162,0)'>
			<img src='page/admin/images/Excel-icon.png' style='margin: 0 10px' />下载模板</a>
		</div>
		<div class='dialogtitle2' style='margin: 15px 0 0 220px;'>
			<span style='color: rgb(128,128,128); font-size: 12px'>2.编辑填写成员信息后再上传模板</span>
		</div>
		<div class='dialogtitle2' style='margin: 115px 0 0 220px'>
			<button style='width: 196px; height: 60px; font-size: 14px' onclick='$("#impfile").click();'>
				<img src='page/admin/images/upload.png' style='margin: 0 15px 0 0' />上传批量导入模板
			</button>
		</div>
		<div style='display:none'>
			<form method='post' id='impform' enctype="multipart/form-data" target='imptarget'>
				<input type="file" name="impfile"  id='impfile' />
			</form>
		</div>
		<iframe name='imptarget' style='display:none'></iframe>
	</div>

	<div id='imp2' style='display:none'>
		<div class='dialogtitle2'>
			<img src='page/admin/images/2.png' />
		</div>
		<div style='width:100%;height:320px;font-size: 12px' id='content'>
		<div class='dialogtitle2' style='margin-top:15px'>
			<span class='result'>
				<img src='page/admin/images/close.png' />
				<span style='padding: 0 0 0 5px;cursor:pointer'>格式错误：
					<span id='cbad' style='color: #fc7a8c;margin:0 5px 0 5px'>0</span>个
				</span>
			</span>
			<span style='color: #fc7a8c;padding: 0 0 0 135px'>(注：红色字段为出错项，请双击修改，如不修改，将无法被导入)</span>
		</div>
		<div class='dialogtitle2 resultlist' style='margin-top: 5px; display:none'>
			<div  style='width:100% ;height:180px;overflow: auto;'>
				<table style='width:940px'>
					<thead>
						<tr style='background-color: #eee'>
							<th width="100px">手机号</th>
							<th width="100px">姓名</th>
							<th width="100px">工号</th>
							<th width="100px">性别</th>
							<th width="100px">所属部门</th>
							<th width="100px">部门领导</th>
							<th width="100px">职务</th>
							<th width="100px">座机号</th>
							<th width="100px">邮箱</th>
							<th width="40px"></th>
						</tr>
					</thead>
					<tbody id='implbad'>
			 			<tr>
							<td field='tdmobile' class='errimp' title='双击修改'>123</td>
							<td field='tdname'>jjj</td>
							<td field='tdworkno'>222</td>
							<td field='tdsex'>男</td>
							<td field='tdbranch'>行政部</td>
							<td field='tdmanager'>333</td>
							<td field='tdposition'>UI设计师</td>
							<td field='tdtelephone'>444</td>
							<td field='tdemail'>jjj@mail</td>
							<td><img class='deltr' src='page/admin/images/delete.png' title='删除' /></td>
		 				</tr>
					</tbody>
				</table>
			</div>
		</div>
		<div class='dialogtitle2' style='margin-top:5px'>
			<span class='result'>
				<img src='page/admin/images/close.png' />
				<span style='padding: 0 0 0 5px;cursor:pointer'>格式正确：且已加入组织
					<span id='cwell' style='color: #ffba00;margin:0 5px 0 5px'>0</span>个
				</span>
			</span>
			<span style='color: #ffba00;padding: 0 0 0 290px'>(注：将不会重复导入)</span>
		</div>
		<div class='dialogtitle2 resultlist' style='margin-top: 5px;display:none'>
			<div  style='width:100% ;height:180px;overflow: auto;'>
				<table style='width:940px'>
					<thead>
						<tr style='background-color: #eee'>
							<th width="100px">手机号</th>
							<th width="100px">姓名</th>
							<th width="100px">工号</th>
							<th width="100px">性别</th>
							<th width="100px">所属部门</th>
							<th width="100px">部门领导</th>
							<th width="100px">职务</th>
							<th width="100px">座机号</th>
							<th width="100px">邮箱</th>
							<th width="40px"></th>
						</tr>
					</thead>
					<tbody id='implwell'>
			 			<tr>
							<td>123</td>
							<td>jjj</td>
							<td>222</td>
							<td>男</td>
							<td>行政部</td>
							<td>333</td>
							<td>UI设计师</td>
							<td>444</td>
							<td>jjj@mail</td>
							<td><img src='page/admin/images/delete.png' title='删除' /></td>
		 				</tr>
					</tbody>
				</table>
			</div>
		</div>
		<div class='dialogtitle2' style='margin-top:5px'>
			<span class='result'>
				<img src='page/admin/images/open.png' />
				<span style='padding: 0 0 0 5px;cursor:pointer'>格式正确：且未加入组织
					<span id='cgood' style='color: #40d4a3;margin:0 5px 0 5px'>0</span>个
				</span>
			</span>
			<span style='color: #40d4a3;padding: 0 0 0 122px'>(注：系统将自动为他们分配帐号，并发短信通知他们)</span>
		</div>
		<div class='dialogtitle2 resultlist' style='margin-top: 5px;display:'>
			<div  style='width:100% ;height:180px;overflow: auto;'>
				<table style='width:940px'>
					<thead>
						<tr style='background-color: #eee'>
							<th width="100px">手机号</th>
							<th width="100px">姓名</th>
							<th width="100px">工号</th>
							<th width="100px">性别</th>
							<th width="100px">所属部门</th>
							<th width="100px">部门领导</th>
							<th width="100px">职务</th>
							<th width="100px">座机号</th>
							<th width="100px">邮箱</th>
							<th width="40px"></th>
						</tr>
					</thead>
					<tbody id='implgood'>
			 			<tr>
							<td>123</td>
							<td>jjj</td>
							<td>222</td>
							<td>男</td>
							<td>行政部</td>
							<td>333</td>
							<td>UI设计师</td>
							<td>444</td>
							<td>jjj@mail</td>
							<td><img src='page/admin/images/delete.png' title='删除' /></td>
		 				</tr>
					</tbody>
				</table>
			</div>
		</div>
		</div>
		<div class='dialogtitle2'>
			<button class='toright' onclick='okimp()'>确认导入</button>
		</div>
	</div>

	<div id='imp3' style='display:none'>
		<div class='dialogtitle2'>
			<img src='page/admin/images/3.png' />
		</div>
		<div class='dialogtitle3' style='margin-top: 55px;'>
			<span style='font-size: 18px;margin-right: 5px'>操作完成</span>
			<a href='../../upload/导入成功.xls' style='font-size: 12px; color:rgb(255,162,0)'>保存表格到本地</a>
		</div>
		<div class='dialogtitle3' style='margin-bottom:30px;'>
		 	<span style='font-size: 12px;color: rgb(128,128,128)'>您可以去“组织结构”页面，拖动调整分支的层级位置!</span>
		</div>
		<div class='dialogtitle3'>
			<hr />
		</div>
		<div class='dialogtitle3' style='margin-top:50px'>
			<span style='font-size: 12px;color: rgb(51,51,51)'>
				导入成功：<span id='succeed' style='color:#40d4a3; margin: 0 5px 0 0'>4</span>位成员
			</span>
			<span style='font-size: 12px; color:rgb(179,179,179)'>账号和密码已经通过短信通知对方</span>
		</div>
		<div class='dialogtitle3'>
			<span style='font-size: 12px;color: rgb(51,51,51)'>
				导入失败：<span id='fail' style='color: #fc7a8c; margin: 0 5px 0 0'>0</span>位成员
			</span>
		</div>
		<div class='dialogtitle2' style='margin-top: 55px'>
			<button class='toright' onclick="closeimp()">关闭</button>
		</div>
		
	</div>

</div>
</body>
</html>