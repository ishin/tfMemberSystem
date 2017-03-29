var pagenumber = 0;
var curpage = 0;
var itemsperpage = 10;
var itemtemplate='<tr id="traid">'
	+'<td>code</td>'
	+'<td>name</td>'
	+'<td>classify</td>'
	+'<td>belong</td>'
	+'<td>'
		+'<button class="unifiedBTN" onclick="editAuth(aid)" style="margin-right: 20px"><img src="images/edit.png" /></button>'
		+'<button onclick="disAuth(aid)" class="unifiedBTN"><img src="images/delete.png" /></button>'
	+'</td></tr>';
var grpid = 0;
$(document).ready(function() {

	$('.plusAuth').click(function(){
		$('.dialogMask').show();
		$('.dialogAuth').show();
	})


	$('.closeX,.canclaDia').click(function(){
		$('.dialogMask').hide();
		$('.dialogAuth').hide();
	})


	$('.searchBTN').click(function(){

	})

	callajax('grp!getCount', '', fGroupCount);

});
function fGroupCount(data) {
	pagenumber = Math.ceil(data.id/itemsperpage);
	if (pagenumber > 0) {

		var newPaging = new Paging('.paging',
			{
				pageCount : pagenumber,
				current : 1,
				backFn : function(){
					loadpage(newPaging);
				}
			}
		)
		newPaging.init();
	}
	else {
		$('#grouplist').empty();
	}
}

function loadpage(newPaging) {
	$('#grouplist').empty();
	var authName = $('.searchInput').val();
	var data = {name:authName,pageindex: newPaging.args.current-1, pagesize: itemsperpage};
	callajax('limit!SearchPriv', data, fShowTable);
}
function fShowTable(data) {
	var i = data.length;

	for(var i = 0;i<data.length;i++){
		$('#grouplist').append(itemtemplate
				.replace('code', data[i].id)
				.replace('name', data[i].name)
				.replace('classify', data[i].category)
				.replace('belong', data[i].app)
				.replace(/aid/g, data[i].id)
		);
	}
	//while(i--) {
    //
    //
	//}
}
function editAuth(id){
	$('.dialogMask').show();
	$('.dialogAuth').show();
}
function disAuth(id){
	new Window().alert({
		title   : '删除权限',
		content : '是否删除此权限？',
		hasCloseBtn : true,
		hasImg : true,
		textForSureBtn : '确定',              //确定按钮
		textForcancleBtn : '取消',            //取消按钮
		handlerForCancle : null,
		handlerForSure : function(){
			//删除权限
			cancleRelation(account,memShip);
		}
	});
}
