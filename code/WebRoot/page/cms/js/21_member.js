var searchnodes21member = null;
var searchlist21member = null;
$(document).ready(function(){

	$('#save21member').click(function(){
		var nodes = $('#21_memberlist').children();
		var i = nodes.length;
		var memberlist = '';
		while (i--) {
			if (memberlist != '') memberlist += ',';
			memberlist += $(nodes[i]).prop('id').substr(1);
		}
		callajax('priv!saveRoleMember', {roleid: currole, memberlist: memberlist}, cb_21_member_save);
	});

	$('#search21member1').keyup(function(e) {
		if (e.keyCode == 13) {
			searchnodes21member = dosearch('search21member1', 'tree21member', searchnodes21member);
		}
	});
	$('#search21member2').keyup(function(e) {
		if (e.keyCode == 13) {
			searchlist21member = dosearchlist('search21member2', '21_memberlist', searchlist21member);
		}
	});
})
function cb_21_member_save(data) {
	load210();
	$('#member').modal('hide');
}
function dosearchlist(search, list, nodes) {
	var i;
	if (nodes != null) {
		i = nodes.length;
		while (i--) {
			$(nodes[i]).removeAttr('style');
		}
	}

	var text = $('#' + search).val();
	if (text == '') return;
	
	nodes = $('#21_memberlist').find('div[name*=' + text + ']')
	i = nodes.length;
	while (i--) {
		$(nodes[i]).attr('style', 'color: red');
	}
	
	return nodes;
}
function delmember(id) {
	var t = $.fn.zTree.getZTreeObj('tree21member');
	var ns = t.getNodesByParam('id', id);
	var i = ns.length;
	while(i--) {
		t.checkNode(ns[i], false, false, true);
		update_check_up(t, ns[i]);
	}
	$('#21_memberlist').find('#m' + id).remove();
}
function update_check_up(t, n) {
	var p = n.getParentNode();
	if (p == null) return;
	var cs = p.children;
	var i = cs.length;
	while (i-- > 0) {
		if (cs[i].checked == true) return;
	}
	t.checkNode(p, false, false, true);
	update_check_up(t, p);
}

function cb_112_position_save(data) {

	if (data.branchmemberid == '0') {
		bootbox.alert({'title':'提示', 'message':'职位已存在.', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');	
		}});
	}
	else {
		$('#position').modal('hide');
		update_112_position();
	}
}