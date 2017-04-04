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

	$('.searchInput').off('focus');
	$('.searchInput').focus(function(){
		$(this).off('keypress');
		$(this).keypress(function(event) {
			if (event.which == 13) {
				$('.searchBTN').click();
			}
		})
	})

	$('.dialogCheckBox').click(function(){
		$(this).parent().find('.dialogCheckBox').removeClass('CheckBoxChecked');
		$(this).addClass('CheckBoxChecked');
		//$(this).attr('necc','true')
	})


	$('.certainAdd').click(function(){
		var name = $('#name').val();
		var appid = $('#appid').val();
		var appsecret = $('#appsecret').val();
		var backurl = $('#backurl').val();
		var isOpen = $('#isOpen').find('.CheckBoxChecked').attr('value');

		//获取到所有必填项
		var allNecc = $(this).parents('.dialogApp').find('[necc=true]');
		if(allNecc.length!=0){
			for(var i = 0;i<allNecc.length;i++){
				if($(allNecc[i]).val()==''){
					new Window().alert({
						title   : '',
						content : '有必填项未填写！',
						hasCloseBtn : false,
						hasImg : true,
						textForSureBtn : false,
						textForcancleBtn : false,
						autoHide:true
					});
					return false;
					break;

				}
			}
		}

		var text = $(this).parents('.dialogApp').find('.diaTitle').text();
		var apptime = new Date().getTime();
		if(text=='新增应用'){
			var data = {appname:name,appId:appid,secert:appsecret,callbackurl:backurl,isopen:isOpen}
			callajax('appinfoconfig!updateAppInfo', data, afterAddPriv);
		}else if(text=='编辑应用'){
			var id = $(this).parents('.dialogApp').attr('bindid');
			var data = {id:id,appname:name,appId:appid,secert:appsecret,callbackurl:backurl,isopen:isOpen}
			callajax('appinfoconfig!EditApp', data, afterEditPriv);
		}
		$('.dialogMask').hide();
		$('.dialogApp').hide();
	})

	$('.plusApp').click(function(){
		$('.dialogMask').show();
		var dialogApp = $('.dialogApp');
		dialogApp.show();
		dialogApp.find('#name').val('');
		dialogApp.find('#appid').val('');
		dialogApp.find('#appsecret').val('');
		dialogApp.find('#backurl').val('');

		dialogApp.find('#isOpen .dialogCheckBox[value='+1+']').click();
		$('.dialogApp').find('.diaTitle').html('新增应用');
	})


	$('.closeX,.canclaDia').click(function(){
		$('.dialogMask').hide();
		$('.dialogApp').hide();
	})

	$('.searchBTN').click(function(){
		loadpage();
	})
	loadpage();

});
function afterAddPriv(data){
	console.log(data);
	loadpage(newPaging.args.current)
}
function afterEditPriv(data){
	loadpage(newPaging.args.current)
}
//function fGroupCount(data) {
//	loadpage();
//}

function loadpage(pagenumber) {
	$('#grouplist').empty();
	var appName = $('.searchInput').val();
	var accounts = localStorage.getItem('account');
	var oAccount = JSON.parse(accounts);
	var userId = oAccount.id;
	if(pagenumber){
		var data = {AppName:appName,pageindex: pagenumber-1, pagesize: itemsperpage,userId:userId};

		callajax('appinfoconfig!SearchAppName', data, fShowTableNew);
	}else{
		var data = {AppName:appName,pageindex: 0, pagesize: itemsperpage,userId:userId};

		callajax('appinfoconfig!SearchAppName', data, fShowTable);
	}
}
function fShowTableNew(data){
	var datas = data.content;
	var LocalData = JSON.stringify(datas);
	window.localStorage.tableData = LocalData;
	for(var i = 0;i<datas.length;i++){
		$('#grouplist').append(itemtemplate
				.replace('name', datas[i].appname)
				.replace('appid', datas[i].appId)
				.replace('appsecret', datas[i].secert)
				.replace('backURL', datas[i].callbackurl)
				.replace('states', datas[i].isopen)
				.replace('operser', datas[i].apptime)
				.replace('date', datas[i].apptime)
				.replace(/aid/g, datas[i].id)
		);
	}
}
function fShowTable(data) {

	var i = data.length;
	var count = data.count;
	pagenumber = Math.ceil(count/itemsperpage);
	window.newPaging = new Paging('.paging',
		{
			pageCount : pagenumber,
			current : 1,
			backFn : function(){
				loadpage(newPaging.args.current);
			}
		}
	)
	var datas = data.content;
	var localData = JSON.stringify(datas);
	window.localStorage.tableData = localData
	for(var i = 0;i<datas.length;i++){
		$('#grouplist').append(itemtemplate
				.replace('name', datas[i].appname)
				.replace('appid', datas[i].appId)
				.replace('appsecret', datas[i].secert)
				.replace('backURL', datas[i].callbackurl)
				.replace('states', datas[i].isopen)
				.replace('operser', datas[i].apptime)
				.replace('date', datas[i].apptime)
				.replace(/aid/g, datas[i].id)
		);
	}
}
function findInList(id){
	var tableData = localStorage.getItem('tableData');
	var finalData = '';
	var tableDatas = JSON.parse(tableData);
	for(var i = 0;i<tableDatas.length;i++){
		if(tableDatas[i].id == id){
			finalData = tableDatas[i];
		}
	}
	return finalData;
}
function editApp(id,curDom){
	var curList = findInList(id)
	var dialogApp = $('.dialogApp');

	$('.dialogMask').show();
	dialogApp.show();
	dialogApp.find('#name').val(curList.appname);
	dialogApp.find('#appid').val(curList.appId);
	dialogApp.find('#appsecret').val(curList.secert);
	dialogApp.find('#backurl').val(curList.callbackurl);
	var isOpenVal = curList.isopen;

	dialogApp.find('#isOpen .dialogCheckBox[value='+isOpenVal+']').addClass('CheckBoxChecked');

	dialogApp.attr('bindid',id);
	dialogApp.find('.diaTitle').html('编辑应用');
}
function disApp(id){
	new Window().alert({
		title   : '删除应用',
		content : '是否删除此应用？',
		hasCloseBtn : true,
		hasImg : true,
		textForSureBtn : '确定',              //确定按钮
		textForcancleBtn : '取消',            //取消按钮
		handlerForCancle : null,
		handlerForSure : function(){
			//删除权限
			cancleRelation(id,function(){
				loadpage(newPaging.args.current)
			});
		}
	});
}
function cancleRelation(id,callback){
	callajax('appinfoconfig!DelApp', {AppId:id}, function(){
		var appName = $('.searchInput').val();
		var data = {name:appName,pageindex: newPaging.args.current, pagesize: itemsperpage};
		callback&&callback()
	});

}
