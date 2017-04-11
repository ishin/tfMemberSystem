var pagenumber = 0;
var curpage = 0;
var itemsperpage = 10;
var itemtemplate='<tr id="traid">'
	+'<td>code</td>'
	+'<td>name</td>'
	+'<td>classify</td>'
	+'<td>belong</td>'
	+'<td>'
		+'<button class="unifiedBTN" onclick="editAuth(aid,this)" style="margin-right: 20px"><img src="images/edit.png" /></button>'
		+'<button onclick="disAuth(aid,this)" class="unifiedBTN"><img src="images/delete.png" /></button>'
	+'</td></tr>';
var grpid = 0;
$(document).ready(function() {

	$('#app').change(function(){
		var curId = $(this).val();
		var curApp = $(this).find('option[value='+curId+']').html();
		callajax('limit!getPrivNamebyclass',{appName:curApp},changeLimit);
	})

	fillAppSelect()
	$('.searchInput').off('focus');
	$('.searchInput').focus(function(){
		$(this).off('keypress');
		$(this).keypress(function(event) {
			if (event.which == 13) {
				$('.searchBTN').click();
			}
		})
	})


	$('.certainAdd').click(function(){
		var name = $('#name').val();
		var parentId = $('#parentId').val();
		var app = $('#app').val();
		//获取到所有必填项
		var allNecc = $(this).parents('.dialogAuth').find('[necc=true]');
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

		var text = $(this).parents('.dialogAuth').find('.diaTitle').text();
		if(text=='新增权限'){
			var data = {name:name,app:app,parentId:parentId}
			callajax('limit!AddPriv', data, afterAddPriv);
		}else if(text=='编辑权限'){
			var privId = $(this).parents('.dialogAuth').attr('bindid');
			var data = {name:name,app:app,parentId:parentId,privId:privId}
			callajax('limit!EditPriv', data, afterEditPriv);
		}

		$('.dialogMask').hide();
		$('.dialogAuth').hide();
	})

	$('.plusAuth').click(function(){
		$('.dialogMask').show();
		$('.dialogAuth').show();
		$('#name').val('');
		$('#parentId').find('option:nth-child(1)').attr("selected",true);
		//$('#app').val('');
		$('.dialogAuth').find('.diaTitle').html('新增权限');
	})


	$('.closeX,.canclaDia').click(function(){
		$('.dialogMask').hide();
		$('.dialogAuth').hide();
	})


	$('.searchBTN').click(function(){
		loadpage();
	})
	loadpage();
	//callajax('grp!getCount', '', loadpage);

});

function fillAppSelect(){
	callajax('appinfoconfig!getAppName','',fillApp);
}
function fillApp(data){
	var sHTML = '';
	var eApp = $('.dialogBody').find('#app')

	var content = data.content;
	for(var i = 0;i<content.length;i++){
		sHTML += '<option value="'+content[i].id+'">'+content[i].appName+'</option>'
	}
	eApp.append($(sHTML));
	$('#app').val(content[0].name);
}
function changeLimit(data,callback){
	console.log(data);
	var sHTML = '';
	var content = data.content;
	if(content){
		for(var i = 0;i<content.length;i++){
			var value =content[i].id;
			var name = content[i].name;
			sHTML += '<option value="'+value+'">'+name+'</option>';
		}
	}
	$('#parentId').html(sHTML);
	callback&&callback();
}
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
	var authName = $('.searchInput').val();

	if(pagenumber){
		var data = {name:authName,pageindex: pagenumber-1, pagesize: itemsperpage};
	}else{
		var data = {name:authName,pageindex: 0, pagesize: itemsperpage};
	}
	callajax('limit!SearchPriv', data, fShowTable);
}
//function fShowTableNew(data,pagenumber,authName){
//	if(data){
//		var datas = data.content;
//		var LocalData = JSON.stringify(datas);
//		window.localStorage.tableData = LocalData;
//		if(datas){
//			for(var i = 0;i<datas.length;i++){
//				$('#grouplist').append(itemtemplate
//						.replace('code', datas[i].id)
//						.replace('name', datas[i].name)
//						.replace('classify', datas[i].parent_name)
//						.replace('belong', datas[i].app)
//						.replace(/aid/g, datas[i].id)
//				);
//			}
//		}else{
//			pagenumber = pagenumber - 1
//			newPaging.args.pageCount--;
//			var data = {name:authName,pageindex:pagenumber, pagesize: itemsperpage};
//			callajax('limit!SearchPriv', data, function(cb){
//				fShowTableNew(cb,pagenumber)
//			});
//		}
//	}
//
//
//}
function fShowTable(data) {
	var i = data.length;
	var count = data.count;
	pagenumber = Math.ceil(count/itemsperpage);

	if($('.paging').find('.pageNum').length==0){
		window.newPaging = new Paging('.paging',
			{
				pageCount : pagenumber,
				current : 1,
				backFn : function(){
					loadpage(newPaging.args.current);
				}
			}
		)
	}else{
		if(data&&!count){
			newPaging.args.pageCount--;
			if(newPaging.args.pageCount<newPaging.args.current) {
				newPaging.args.current = newPaging.args.pageCount;
			}
			//重新fillPage并加载最后一页
			newPaging.fillHtml($('.paging'),newPaging.args);
			loadpage(newPaging.args.current);
		}else{
			if(newPaging.args.pageCount<pagenumber){
				newPaging.args.pageCount++;
				newPaging.fillHtml($('.paging'),newPaging.args);
			}
		}
	}

	var datas = data.content;
	var localData = JSON.stringify(datas);
	window.localStorage.tableData = localData
	for(var i = 0;i<datas.length;i++){
		$('#grouplist').append(itemtemplate
				.replace('code', datas[i].id)
				.replace('name', datas[i].name)
				.replace('classify', datas[i].parent_name)
				.replace('belong', datas[i].app)
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
function editAuth(id,curDom){
	var curList = findInList(id)
	var dialogAuth = $('.dialogAuth');

	$('.dialogMask').show();
	dialogAuth.show();
	dialogAuth.find('#name').val(curList.name);
	//dialogAuth.find('#parentId').val(curList.category);
	var options = dialogAuth.find('#app option');
	for(var i = 0;i<options.length;i++){
		if($(options[i]).html()==curList.app){
			var curVal = $(options[i]).attr('value');
			break;
		}

	}
	dialogAuth.find('#app').val(curVal);

	callajax('limit!getPrivNamebyclass',{appName:curList.app},function(cb){
		changeLimit(cb,function(){
			dialogAuth.find('#parentId').val(curList.parent_id);
		})
	});

	dialogAuth.attr('bindid',id);
	dialogAuth.find('.diaTitle').html('编辑权限');
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
			cancleRelation(id,function(){
				loadpage(newPaging.args.current)
			});
		}
	});
}
function cancleRelation(id,callback){
	callajax('limit!DelPriv', {privId:id}, function(){
		var authName = $('.searchInput').val();
		var data = {name:authName,pageindex: newPaging.args.current, pagesize: itemsperpage};
		callback&&callback()
	});

}
