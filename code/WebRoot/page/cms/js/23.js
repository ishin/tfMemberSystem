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
		$('#positionlist').append('<div class="pos" id="p' + data[i].id + '" title="' + data[i].name + '">' + subbyte(data[i].name, 10) + '<a href="#" onclick="del(' + data[i].id + ')">x</a></div>');
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
		$('#positionlist').append('<div class="pos" id="p' + data.id + '" title="' + data.name + '">' + subbyte(data.name, 10) + '&nbsp;&nbsp;<a href="#" onclick="del(' + data.id + ')">x</a></div>');
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
function subbyte(str, len) {
	if (bytelen(str) > len) {
		return subbytestr(str, 8) + '...';
	}
	return str;
}
function bytelen(str) {
	return str.replace(/[^\u0000-\u00ff]/g,"aa").length;
}
function subbytestr(str, len) 
{ 
    if(!str || !len) { return ''; } 
    //预期计数：中文2字节，英文1字节 
    var a = 0; 
    //循环计数 
    var i = 0; 
    //临时字串 
    var temp = ''; 
    for (i=0;i<str.length;i++) 
    { 
        if (str.charCodeAt(i)>255)  
        { 
            //按照预期计数增加2 
             a+=2; 
        } 
        else 
        { 
             a++; 
        } 
        //如果增加计数后长度大于限定长度，就直接返回临时字符串 
        if(a > len) { return temp; } 
        //将当前内容加到临时字符串 
         temp += str.charAt(i); 
    } 
    //如果全部是单字节字符，就直接返回源字符串 
    return str; 
} 