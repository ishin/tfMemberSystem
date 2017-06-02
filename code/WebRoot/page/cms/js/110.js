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
		//if (has('rsgltj')) {
			fillmember = 1;
			$('#member').modal({
				backdrop: false,
				remote: '11_member.jsp'
			});
		//}
		//else {
		//	bootbox.alert({title:'提示', message:'您没有权限添加人员.', callback: function() {
		//		$('#container').css('width', document.body.clientWidth + 'px');
		//	}});
		//}
	});
	$('#branchsave').click(function(){
		if (curbranch == 0) {
			bootbox.alert({'title':'提示', 'message': '请先选择部门.', callback: function() {
				$('#container').css('width', document.body.clientWidth + 'px');	
			}});
			return;
		}
		
		if ($( "#110" ).triggerHandler( "submitForm" ) == false) return;

		var data = formtojson($('#branchform'));
		callajax('branch!saveBranch', data, cb_110_1);

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
	console.log(data);
	if(data.branchid==0){
		bootbox.alert({'title':'提示', 'message':'保存失败.', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');
		}});
	}else if(data.branchid==-1){
		bootbox.alert({'title':'提示', 'message':'每个成员最多兼职5个部门，请更换部门领导', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');
		}});
	}else{
		var selectNode = $('#tree11').find('.curSelectedNode').parent();
		window.selectNodeIdLeft = selectNode.attr('id');
		bootbox.alert({'title':'提示', 'message':'保存成功.', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');
		}});
		var treeObj = $.fn.zTree.getZTreeObj("tree11");
		var nodes = treeObj.getSelectedNodes();
		callajax("branch!getOrganTree", "", cb_11_tree);
		//if(window.selectNodeIdLeft){
		setTimeout(function(){
			var treeObj = $.fn.zTree.getZTreeObj("tree11");
			var node = treeObj.getNodeByTId(selectNodeIdLeft);
			treeObj.selectNode(node);
			window.selectNodeIdLeft='';
		},2000)

		//}
	}


}