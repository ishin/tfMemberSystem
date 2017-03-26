$(document).ready(function(){

	$('#branch').validVal();

	$($('#tree11branchbranchwrap')).css({
		'left': $('#11branchbranch').position().left, 
		'top': $('#11branchbranch').position().top + 33, 
		'width': $('#11branchbranch').width() + 25
	});
	$($('#tree11branchmanagerwrap')).css({
		'left': $('#11branchmanager').position().left,
		'top': $('#11branchmanager').position().top + 33, 
		'width': $('#11branchmanager').width() + 25
	});
	$('#container').click(function(){
		if ($('.treewrap2').is(':visible')) {
			$('.treewrap2').hide();
		}
		return true;
	});
	$('.treeedit2').click(function(){
		var tw = $(this).parent().children('.treewrap2');
		if ($(tw).is(':visible')) {
			$(tw).hide();
		}
		else {
			$(tw).show();
		}
		return false;
	});
	$('.treewrap2').click(function(){
		return false;
	});
	$('#save11branch').click(function() {
		if ($( "#branch" ).triggerHandler( "submitForm" ) == false) return;

		var data = {
				branchparentid: $('#11branchbranchid').val(),
				branchname: $('#11branchname').val(),
				branchmanagerid: $('#11branchmanagerid').val(), 
				branchaddress: $('#11branchaddress').val(), 
				branchtelephone: $('#11branchtelephone').val(),
				branchwebsite: $('#11branchwebsite').val(),
				branchintro: $('#11branchintro').val(),
			};
		callajax('branch!saveBranch', data, cb_11_save_branch);
	});
	$('#11branchaddmember').click(function(){

		//权限
		if (has('rsgltj')) {
			fillmember = 2;
			$('#member').modal({
				backdrop: false,
				remote: '11_member.jsp'
			});
		}
		else {
			bootbox.alert({title:'提示', message:'您没有权限添加人员.', callback: function() {
				$('#container').css('width', document.body.clientWidth + 'px');	
			}});
		}
	});
});
function cb_11_save_branch(data) {
	if (data.branchid == '0') {
		bootbox.alert({'title':'提示', 'message':'部门名称已存在，请重新输入.', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');	
		}});
	}
	else {
		bootbox.alert({'title':'提示', 'message':'添加成功.', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');	
		}});
		callajax("branch!getOrganTree", "", cb_11_tree);
		if ($('#11branchcontinue').prop('checked') == false) {
			$('#branch').modal('hide');
		}
		$('#11branchname').val('');
		$('#11branchaddress').val('');
		$('#11branchtelephone').val('');
		$('#11branchwebsite').val('');
		$('#11branchintro').val('');
	}
}