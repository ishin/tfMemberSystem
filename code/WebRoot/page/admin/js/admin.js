//权限列表
var privs = '';
//应用根目录
var path = '';
$(document).ready(function(){
	
	// 取权限
	callajax('adm!getBase', '', cb_base);
	
	$('#idlogout').click(function() {
		window.location.href = path + 'system!logOut';
	});
	
	// 下拉相关
	$('#container').click(function(){
		if ($('.treewrap').is(':visible')) {
			$('.treewrap').hide();
		}
		return true;
	});
	$('.treeedit').click(function(){
		var tw = $(this).parent().children('.treewrap');
		if ($(tw).is(':visible')) {
			$(tw).hide();
		}
		else {
			$(tw).show();
		}
		return false;
	});
	$('.treewrap').click(function(){
		return false;
	});
	$('.menu').hover(function(){
		$(this).addClass('menuhover');
	},function(){
		$(this).removeClass('menuhover');
	});
})
function cb_base(data) {
	
	// 取权限失败返回登录界面
	if (data.id == 0) {
		window.location.href = path;
	}
	else {
		privs = data.privs;
	}
}
//判断是否有权限
function has(priv) {
	return (privs.indexOf(',' + priv + ',') > -1 ? true : false);
}
// 下拉相关
function treeplace(oedit, otree) {
	$(otree).css({
		'left': $(oedit).offset().left, 
		'top': $(oedit).offset().top + 20, 
		'width': $(oedit).width() + 4
	});
}

//ajax
function callajax(url, data, cb){
	$.ajax({
		type: "POST",
		url: path + url,
		data: data,
		datatype: 'json',
		async: false,
		success: function(msg){
			var ret = $.parseJSON(msg);
			cb(ret);
//			if (ret.status == 'ok') {
//				cb(ret.data);
//			}
//			else if (ret.status == 'bad'){
//				alert(ret.message);
//			}
//			else {
//				alert(msg);
//			}
		},
		error: function(msg){
			alert(msg.status + ', ' + msg.responseText);
		}
	});
}

function showdate(data) {
	if (data.length == 0) return '';
	return data.substr(0,4) + '-' + data.substr(4,2) + '-' + data.substr(6,2);
}
function dosearch(search, tree, nodes) {
	var i;
	if (nodes != null) {
		i = nodes.length;
		while (i--) {
			$('#' + nodes[i].tId + '_a').removeAttr('style');
		}
	}

	var text = $('#' + search).val();
	if (text == '') return;
	
	var t = $.fn.zTree.getZTreeObj(tree);
	t.expandAll(true);
	nodes = t.getNodesByParamFuzzy('name', text);
	i = nodes.length;
	while (i--) {
		$('#' + nodes[i].tId + '_a').attr('style', 'color: red');
		t.expandNode(nodes[i].getParentNode(), true);
	}
	
	return nodes;
}
function formtojson(form) {
	
	var astring = '{';
	var fa = $(form).serializeArray();
	var i = fa.length;
	while (i--) {
		var a = fa[i];
		if (astring != '{') astring += ',';
		astring += '"' + a.name + '":"' + a.value + '"';
	}
	astring += '}';
	return $.parseJSON(astring);
}