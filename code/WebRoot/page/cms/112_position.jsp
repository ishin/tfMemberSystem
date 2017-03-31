<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<script src="js/112_position.js" language="javascript"></script>
</head>
<body>
<div class="modal-body">
	<div class='h5px'></div>
	<div>
		<div class='dialogtitle'>
			<div id='title112position' class='toleft dtitle'>添加用户职位信息</div>
			<div class='toright dclose' onclick="$('#position').modal('hide');">×</div>
		</div>
	</div>
	<div class='h40px'></div>
	<div>
		<div class='dialogtitle searchbox'>
			<img src='images/boxsearch.png' class='searchimg' />
			<input type='text' id='search112position' class='searchinput' style='width:270px' placeholder='查找部门...'>
		</div>
	</div>
	<div>
		<div class='dialogtitle' style='margin-right: 7px'>
			<div class='treeopenwrap h400px' id='tree112positionwrap' style='margin-top: -1px;overflow: auto;'>
				<div id='tree112position' class='ztree'></div>
			</div>
		</div>
	</div>
	<div class='h20px'></div>
	<div>
		<div class='dialogtitle'>职务：
			<select id="select112position" class='toright w200px'>
				<option>部门</option>
			</select>
		</div>
	</div>
	<div class='h30px'></div>
	<div>
		<div class='dialogtitle'>
			<button onclick="$('#position').modal('hide');" class='leftspace15 toright cancel'>取消</button>
			<button id='save112position' class='leftspace15 toright'>确定</button>
		</div>
	</div>	
</div>
</body>
</html>