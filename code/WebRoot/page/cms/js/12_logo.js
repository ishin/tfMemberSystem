var tid;
var rep = 0;
var reps = 40;
$(document).ready(function() {
	
	$('#logofile').on('change',function(){
		if (this.files.length > 0) {
			var t = this.files[0].type;
			if ( t == 'image/jpeg' || t == 'image/png' || t == 'image/bmp') {
				$('#filename').val(this.files[0].name);
			}
			else {
				bootbox.alert({
					'title': '提示',
					'message': '请选择JPG、PNG、BMP格式文件.',
					callback: function() {
						$('#container').css('width', document.body.clientWidth + 'px');	
					},
				});
				$('#filename').val('');
			}
		}
		else {
			$('#filename').val('');
		}	
	});

	$('#save12logo').click(function() {
		if ($('#logofile')[0].files.length == 0) {
			bootbox.alert({
				'title': '提示',
				'message': '请先选择文件.',
				callback: function() {
					$('#container').css('width', document.body.clientWidth + 'px');	
				},
			});
		}
		else {
			$('#fileform').prop('action', path + 'admlogo').submit();
			$('#logod').modal('hide');
			rep = 0;
			tid = setInterval('freshlogo()', 250);
		}
	});
});
function freshlogo() {
	if (rep++ < reps) {
		var d = $(window.frames["target"].document);
		if (d.children(0)[0].innerText != '') {
			info.logo = d.children(0)[0].innerText;
			$('#logo').prop('src', imagedir + info.logo);
			clearInterval(tid);
		}
	}
	else {
		clearInterval(tid);
	}
}