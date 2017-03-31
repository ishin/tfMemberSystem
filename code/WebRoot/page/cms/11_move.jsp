<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<script src="js/11_move.js" language="javascript"></script>
</head>
<body>
<div class="modal-body">
	<div class='h5px'></div>
	<div>
		<div class='dialogtitle'>
			<div id='title11move' class='toleft dtitle'>移动到</div>
			<div class='toright dclose' onclick="$('#move').modal('hide');">×</div>
		</div>
	</div>
	<div class='h40px'></div>
	<div>
		<div class='dialogtitle searchbox'>
			<img src='images/boxsearch.png' class='searchimg' />
			<input type='text' id='search11move' class='searchinput' style='width:270px' placeholder='查找部门...'>
		</div>
	</div>
	<div>
		<div class='dialogtitle'>
			<div class='treeopenwrap h400px' id='tree11movewrap' style='margin-top: -1px;overflow:auto;'>
				<div id='tree11move' class='ztree'></div>
			</div>
		</div>
	</div>
	<div class='h30px'></div>
	<div>
		<div class='dialogtitle'>
			<button onclick="$('#move').modal('hide');" class='leftspace15 toright cancel'>取消</button>
			<button id='save11move' class='leftspace15 toright'>确定</button>
		</div>
	</div>	
</div>
</body>
</html>