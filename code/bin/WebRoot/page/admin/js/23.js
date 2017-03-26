$(document).ready(function() {
	
	$('.infopanel23').validVal();

//	$('.infopanel23').css('margin-left', (document.body.clientWidth * 0.87) * 0.29);
	$('.infopanel23').css('margin-left', ((document.body.clientWidth * 0.87 - 688) / 2 + 'px'));
	
	callajax('pos!getList', '', cb_23);
	
	$('#save23').click(function() {
		if ($( ".infopanel23" ).triggerHandler( "submitForm" ) == false) return;
		
		callajax('pos!save', {name: $('#posname').val()}, cb_23_save)
	});
});
function cb_23(data) {
	$('#positionlist').empty();
	var i = data.length;
	while (i--) {
		$('#positionlist').append('<div class="pos" id="p' + data[i].id + '" >' + data[i].name + '<a href="#" onclick="del(' + data[i].id + ')">x</a></div>');
	}
	star();
}
function del(id) {
	$('#p' + id).remove();
	callajax('pos!del', {id: id}, cb_23_del);
}
function cb_23_del(data) {
	star();
}
function cb_23_save(data) {
	if (data.id == 0) {
		bootbox.alert({
			'title': '提示',
			'message': '职务已存在.',
			callback: function(result) {
				$('#container').css('width', document.body.clientWidth + 'px');	
			},
		});
	}
	else {
		$('#positionlist').append('<div class="pos" id="p' + data.id + '" >' + data.name + '&nbsp;&nbsp;<a href="#" onclick="del(' + data.id + ')">x</a></div>');
		star();
	}
}
function star() {
	$('#star').css('height', (Math.ceil($('#positionlist').children().length / 4) * 50 + 200) + 'px');
	var infoh = $('.info').css('height').replace('px', '');
	var ih = parseInt(infoh) + 40;
	var h = document.body.clientHeight - 50;
	var v = h > ih ? h : ih;
	$('.sidebar12').css('height', v + 'px');
}