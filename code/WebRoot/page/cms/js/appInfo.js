var pagenumber = 0;
var curpage = 0;
var itemsperpage = 10;
var itemtemplate='<tr id="traid">'
	+'<td>name</td>'
	+'<td>appid</td>'
	+'<td>appsecret</td>'
	+'<td>backURL</td>'
	+'<td>states</td>'
	+'<td>operser</td>'
	+'<td>date</td>'
	+'<td>'
		+'<button class="unifiedBTN" onclick="editApp(aid)" style="margin-right: 20px"><img src="images/edit.png" /></button>'
		+'<button onclick="disApp(aid)" class="unifiedBTN"><img src="images/delete.png" /></button>'
	+'</td></tr>';
var grpid = 0;
$(document).ready(function() {

	$('.plusAuth').click(function(){
		$('.dialogMask').show();
		$('.dialogApp').show();
	})


	$('.closeX,.canclaDia').click(function(){
		$('.dialogMask').hide();
		$('.dialogApp').hide();
	})

	callajax('grp!getCount', '', cb_13_count);

});
function cb_13_count(data) {
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
function editApp(id){
	$('.dialogMask').show();
	$('.dialogApp').show();
}
function disApp(id){
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
function loadpage(newPaging) {
	var data = {page: newPaging.args.current-1, itemsperpage: itemsperpage};
	callajax('grp!getList', data, cb_13_fresh);
}
function cb_13_fresh(data) {
	$('#grouplist').empty();
	var i = data.length;
	while(i--) {
		$('#grouplist').append(itemtemplate
				.replace('code', data[i].code)
				.replace('name', data[i].name)
				.replace('member', data[i].member)
				.replace('date', showdate(data[i].date))
				.replace(/aid/g, data[i].id)
		);
	}

}