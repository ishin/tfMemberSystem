<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<script src="js/21_member.js" language="javascript"></script>
</head>
<body>
<div class="modal-body">
	<div class='h5px'></div>
	<div>
		<div class='dialogtitle'>
			<div id='title112position' class='toleft dtitle'>新增/修改人员</div>
			<div class='toright dclose' onclick="$('#member').modal('hide');">×</div>
		</div>
	</div>
	<div class='h40px'></div>
	<div style='height: 450px'>
		<div class='toleft' style='width: 48%;height: 450px'>
			<div>
				<div class='dialogtitle'>选择联系人：</div>
			</div>
			<div class='h10px'></div>
			<div>
				<div class='dialogtitle searchbox'>
					<img src='images/boxsearch.png' class='searchimg' />
					<input type='text' id='search21member1' class='searchinput' style='width:270px' placeholder='查找联系人...'>
				</div>
			</div>
			<div>
				<div class='dialogtitle'>
					<div class='treeopenwrap h400px' id='tree21memberwrap' style='overflow-y: auto; margin-top: -1px'>
						<div id='tree21member' class='ztree'></div>
					</div>
				</div>
			</div>
		</div>
		<div class='toright' style='width: 48%;height: 450px'>
			<div>
				<div class='dialogtitle'>已选联系人：</div>
			</div>
			<div class='h10px'></div>
			<div>
				<div class='dialogtitle searchbox'>
					<img src='images/boxsearch.png' class='searchimg' />
					<input type='text' id='search21member2' class='searchinput' style='width:270px' placeholder='查找联系人...'>
				</div>
			</div>
			<div>
				<div class='dialogtitle'>
					<div id='21_memberlist' class='memberlist_21'>
<!-- 					<div id='m1' name='xx' class='member21'>
							<div class='toleft'>xx</div>
							<div class='toright' onclick='delmember(memberid)'>删</div>
						</div>
 -->				</div>
				</div>
			</div>
		</div>
	</div>
	<div class='h60px'></div>
	<div>
		<div class='dialogtitle'>
			<button onclick="$('#member').modal('hide');" class='leftspace15 toright cancel'>取消</button>
			<button id='save21member' class='leftspace15 toright'>确定</button>
		</div>
	</div>	
</div>
</body>
</html>