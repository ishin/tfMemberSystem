var fillmember = 0;
$(document).ready(function(){
	
	$('#110').validVal();

	//下拉相关
	$('#tree110wrap').css({
		'left': $('#branchmanager').offset().left, 
		'top': $('#branchmanager').offset().top + 33, 
		'width': $('#branchmanager').width() + 26
	});
	
	$('#branchaddmember').click(function() {
		
		//权限
		if (has('rsgltj')) {
			fillmember = 1;
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
	$('#branchsave').click(function(){
		if (curbranch == 0) {
			bootbox.alert({'title':'提示', 'message': '请先选择部门.', callback: function() {
				$('#container').css('width', document.body.clientWidth + 'px');	
			}});
			return;
		}
		
		if ($( "#110" ).triggerHandler( "submitForm" ) == false) return;

		//权限
		if (has('bmglxg')) {
			var data = formtojson($('#branchform'));
			callajax('branch!saveBranch', data, cb_110_1);
		}
		else {
			bootbox.alert({'title':'提示', 'message':'您没有权限修改部门信息.', callback: function() {
				$('#container').css('width', document.body.clientWidth + 'px');	
			}});
		}
	});
})
function cb_110(data) {
	loadbranch(data);
}
function loadbranch(data) {
	$('#branchid').val(curbranch);
	$('#branchname').val(data.name);
	$('#branchmanagerid').val(data.managerId);
	$('#branchmanager').val(data.manager);
	$('#branchaddress').val(data.address);
	$('#branchtelephone').val(data.telephone);
	$('#branchwebsite').val(data.website);
	$('#branchfax').val(data.fax);
	$('#branchintro').val(data.intro);
}
function cb_110_1(data) {
	bootbox.alert({'title':'提示', 'message':'保存成功.', callback: function() {
		$('#container').css('width', document.body.clientWidth + 'px');	
	}});
}