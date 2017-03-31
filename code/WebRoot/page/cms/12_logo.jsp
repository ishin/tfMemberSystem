<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %> 
<!doctype html>
<html>
<head>
<script src="js/12_logo.js" language="javascript"></script>
</head>
<body>

<div class="modal-body">
	<div class='h5px'></div>
	<div>
		<div class='dialogtitle'>
			<div class='toleft dtitle'>公司LOGO设置</div>
			<div class='toright dclose' onclick="$('#logod').modal('hide');">×</div>
		</div>			
	</div>
	<div class='h50px'></div>
	<div>
		<div class='dialogtitle'>选一张电脑里的图片作为公司LOGO：</div>
	</div>
	<div class='h20px'></div>
	<div>
		<div class='dialogtitle' style='line-height: 0'>
			<div style='display:none'>
 				<form method='post' id='fileform' enctype="multipart/form-data" target='target'>
					<input type="file" name="logofile"  id='logofile' />
				</form>
			</div>
			<input type='text' id='filename' style='width: 475px' />
			<button class='btnlogo' onclick='$("#logofile").click();'>浏览</button>
		</div>
	</div>
	<div class='h30px'></div>
	<div>
		<div class='dialogtitle'>支持JPG、PNG、BMP格式（推荐使用透明背景的PNG格式）</div>
	</div>
	<div>
		<div class='dialogtitle'>上传的图片尺寸不要超过255*255像素</div>
	</div>
	<div class='h30px'></div>
	<div>
		<div class='dialogtitle'>
			<button class="toright leftspace15 cancel" onclick="$('#logod').modal('hide');">取消</button>
			<button class="toright leftspace15" id='save12logo'>确定</button>
		</div>
	</div>	
</div>

<iframe name='target' style='display:none'></iframe>

</body>
</html>