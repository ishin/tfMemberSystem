var searchnodes11move = null;
$(document).ready(function(){

	$('#save11move').click(function(){
		
		var o = $.fn.zTree.getZTreeObj('tree11move');
		var nod = o.getSelectedNodes()[0];
		if (nod.id == movnode.pid) {
			$('#move').modal('hide');
			return;
		}
		
		var data = {id: movnode.id, pid: movnode.pid, toid: nod.id};
		callajax('branch!mov', data, cb_11_mov);
	});
	$('#search11move').keyup(function(e) {
		if (e.keyCode == 13) {
			searchnodes11move = dosearch('search11move', 'tree11move', searchnodes11move);
		}
	});
})
function cb_11_mov(data) {

	if (data.id == '0') {
		bootbox.alert({title:'提示', message:'部门不能移动到其子部门内.', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');	
		}});
	}
	else {
		$('#move').modal('hide');
		callajax("branch!getOrganTree", "", cb_11_tree);
		if (data.id > 10000) {
			callajax("branch!getMemberById", {'id': data.id}, cb_111_112);
		}
	}
}