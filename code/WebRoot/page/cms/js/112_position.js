var searchnodes112position = null;
$(document).ready(function(){

	//下拉相关
	treeplace($('#tree112position'), $('#tree112positionwrap'));
	
	$('#save112position').click(function(){
		var data = {branchmemberid: branchmemberid,
				memberid: curmember, 
				branchid: branch112position, 
				positionid: $('#select112position').val()};
		callajax('branch!savePosition', data, cb_112_position_save);
	});

	$('#search112position').keyup(function(e) {
		if (e.keyCode == 13) {
			searchnodes112position = dosearch('search112position', 'tree112position', searchnodes112position);
		}
	});
})
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