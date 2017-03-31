<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<script src="js/13_creator.js" language="javascript"></script>
</head>
<body>
<div class="modal-body">
	<div class='h5px'></div>
	<div>
		<div class='dialogtitle'>
			<div class='toleft dtitle'>修改创建者</div>
			<div class='toright dclose' onclick="$('#creator').modal('hide');">×</div>
		</div>
	</div>
	<div class='h40px'></div>
	<div style="width:100%;height: 380px;padding:0 15px;">
		<table class="t112">
			<thead>
				<tr">
					<th width="30%">群权限</th>
					<th width="30%">姓名</th>
					<th width="40%">帐号</th>
				</tr>
			</thead>
			<tbody id='creatorlist'>
<!-- 			<tr>
					<td>创建人</td>
					<td>xxx</td>
					<td>xxx</td>
				</tr>
				<tr>
					<td><button>设为创建人</button></td>
					<td>xxx</td>
					<td>xxx</td>
				</tr>
 -->		</tbody>
		</table>
		<div>
			<div class='toright leftspace15' id='pageclast'><img id='imglast2' src='images/lastpage_1.png' /></div>
			<div class='toright leftspace15' id='pagecnext'><img id='imgnext2' src='images/next_1.png' /></div>
			<div class='toright leftspace15' id='pageccurr'>1/10</div>
			<div class='toright leftspace15' id='pagecprev'><img id='imgback2' src='images/back_1.png' /></div>
			<div class='toright leftspace15' id='pagecfirst'><img id='imgfirst2' src='images/firstpage_1.png' /></div>
		</div>
	</div>
	<div>
		<div class='dialogtitle'>
			<button class='toright leftspace15 cancel' onclick="$('#creator').modal('hide');">取消</button>
			<button class='toright leftspace15' onclick="$('#creator').modal('hide');">确定</button>
		</div>
	</div>	
	<div class='h10px'></div>
</div>
</body>
</html>