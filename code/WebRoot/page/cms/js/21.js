var curpage = '';
var currole = 0;
var pagenumber = 0;
var curpage = 0;
var itemsperpage = 10;
var membertemplate = '<div id="mmemberid" name="membername" class="member21">'
						+ '<div class="toleft">membername</div>'
						+ '<div class="toright" onclick="delmember(memberid)">'
						+ '<img src="images/delete-2.png" /></div>'
						+ '</div>';
var tabPageTemp =  '<div class="col2 collHide" id="211" style="display:none">'+
						'<div class="infotabr" style="padding-top: 10px;position: absolute;right: 0px;top: -50px;">'+
							'<button class="editpriv addedit" id="editpriv" style="width:100px">修改权限</button>'+
						'</div>'+
						'<div id="list211">'+
						'</div>'+
					'</div>';
var tabPageEditTemp = '<div class="col2 collHide" id="211edit" style="display:none">'+
						'<div class="infotabr" style="padding-top: 10px;position: absolute;right: 0px;top: -50px;">'+
							'<button id="editmember" class="addedit editmember" style="width:100px" onclick="">保存权限</button>'+
						'</div>'+
						'<div id="list211edit">'+
						'</div>'+
					'</div>'
	$(document).ready(function(){
	$('.infotab').delegate('.infotabi','click',function(){
		$('.infotabi').removeClass('tabactive');
		$(this).addClass('tabactive');
	})

	//if (has('qxglck')) {

		callajax('limit!getRoleList',{appId:''}, cb_21_fresh);
		callajax('appinfoconfig!getAppName','', showTab);
	//}
	
	$('#role').on('shown.bs.modal', function(e) {
		//callajax('limit!getRoleList', '', cb_21_role_role);
		//callajax('limit!getLimitByRole', {roleid: 0,appname:'IMS'}, cb_21_role_priv)
	});

	$('#member').on('shown.bs.modal', function(e) {
		callajax("branch!getOrganTree", "", cb_21_member_tree);
	});
	//切换角色
	$('body').on('click', '#list21 li', function() {
		$(this).parent().find('li').removeClass('prv21active');
		$('#sanjiao').remove();
		$(this).addClass('prv21active');
		$(this).after('<img id="sanjiao" src="images/roleselect.png" style="float:right" />');
		if (curpage == '211edit') {
			showpage('210');
		}
		var curPage = $('.infotab').find('.tabactive').attr('bindpage');
		var appName = $('.infotab').find('.tabactive').html();
		currole = this.id.substr(1);
		if(curPage!=210){
			//showpage(curPage);

			loadPage(curPage,appName);
			loadEditPage(curPage,appName)
		}else{
			load210();

		}

	});
	//切换多选的checkbox
	$('body').on('click', '.privgroup, .privgroupd', function() {
		if ($(this).prop('src').indexOf('1.png') > 0) {
			$(this).parent().parent().find('img').prop('src', 'images/select-2.png');
			$(this).parent().parent().find('input').prop('checked', false);
		}
		else {
			$(this).parent().parent().find('img').prop('src', 'images/select-1.png');
			$(this).parent().parent().find('input').prop('checked', true);
		}
	});
	//切换多选的checkbox
	$('body').on('click', '.pgc, .pgcd', function() {
		if ($(this).prop('src').indexOf('1.png') > 0) {
			$(this).parent().find('img').prop('src', 'images/select-2.png');
			$(this).parent().find('input').prop('checked', false);
		}
		else {
			$(this).parent().find('img').prop('src', 'images/select-1.png');
			$(this).parent().find('input').prop('checked', true);
		}
	});
	//点击添加角色
	$('#addrole').click(function(){

		//if (has('qxgltj')) {
			$('#role').modal({
				backdrop: false,
				remote: '21_role.jsp'
			});
		//}
		//else {
		//	bootbox.alert({'title':'提示','message':'您没有权限添加身份', callback: function() {
		//		$('#container').css('width', document.body.clientWidth + 'px');
		//	}});
		//}
	});
	//点击新增/修改人员
	$('#editmember').click(function(){
		//if (currole == 1) {
		//	bootbox.alert({'title':'提示', 'message':'不能修改组织管理员.', callback: function() {
		//		$('#container').css('width', document.body.clientWidth + 'px');
		//	}});
		//	return;
		//}

		//权限
		//if (has('qxglxg')) {
			if (currole == 0) {
				bootbox.alert({title:'提示', message:'请先选择身份.', callback: function() {
					$('#container').css('width', document.body.clientWidth + 'px');	
				}});
			}
			else {
				$('#member').modal({
					backdrop: false,
					remote: '21_member.jsp'
				});
			}
		//}
		//else {
		//	bootbox.alert({'title':'提示','message':'您没有权限新增/修改人员.', callback: function() {
		//		$('#container').css('width', document.body.clientWidth + 'px');
		//	}});
		//}
	});
	//点击保存修改权限
	$('.col21').on('click','.editmember',function(){
		var bindPage = $('.infotab .infotabi.tabactive').attr('bindpage');
		savaEditPage(bindPage)
	})

	//点击修改权限
	$('.col21').on('click','.editpriv',function(){
		var activeRole = $('.toleft.prv21active').length
		if(activeRole == 0||currole == 0) {
			bootbox.alert({"title":"提示","message":"请先选择角色！", callback: function() {
				$('#container').css('width', document.body.clientWidth + 'px');
			}});
			return;
		}

		//权限
		//if (has('qxglxg')) {
		var bindPage = $('.infotab .infotabi.tabactive').attr('bindpage');
		showpage(bindPage+'edit');
		//}
		//else {
		//	bootbox.alert({'title':'提示','message':'您没有权限修改权限.', callback: function() {
		//		$('#container').css('width', document.body.clientWidth + 'px');
		//	}});
		//}
	})

	//分页
	$('#pagefirst').click(function() {
		if (pagenumber == 0) return;
		if (curpage == 0) return;
		curpage = 0;
		load210page();
	});
	$('#pageprev').click(function() {
		if (pagenumber == 0) return;
		if (curpage == 0) return;
		curpage--;
		load210page();
	});
	$('#pagenext').click(function() {
		if (pagenumber == 0) return;
		if (curpage + 1 == pagenumber) return;
		curpage++;
		load210page();
	});
	$('#pagelast').click(function() {
		if (pagenumber == 0) return;
		if (curpage + 1 == pagenumber) return;
		curpage = pagenumber - 1;
		load210page();
	});
});
function cb_21_fresh(data) {
	data = data.content;
	initRoleList(data)
	load210();
}
function initRoleList(data){
	$('#list21').empty();
	if(data){
		var i = data.length;
		while (i--) {
			if (currole == 0) currole = data[i].id;
			$('#list21').append('<li class="prv21 toleft" style="width: 100%" id="r' + data[i].id + '">' + data[i].name + '</li>');
			$('#list21').find('li:last-child').css('width', $('#list21').find('li:last-child').css('width').replace('px', '') - 10);
		}

		$('#list21').find('#r'+currole).addClass('prv21active');
		$('#list21').find('#r'+currole).after('<img id="sanjiao" src="images/roleselect.png" style="float:right" />');
	}

}
function showTab(data){
	var pageNum = 211;
	var content = data.content;
	var sHTML = '';
	var contentLength = content.length;
	$('.infotitle .infotab').width(contentLength*100+100);
	for(var i = 0;i<contentLength;i++){
		sHTML='<div class="infotabi" appID="'+content[i].id+'" onclick="showpage('+pageNum+')" bindpage="'+pageNum+'">'+content[i].appName+'</div>';
		$('.infotab').append($(sHTML));

		$('.col21').append(tabPageTemp
				.replace(/211/g, pageNum)
		);
		$('.col21').append(tabPageEditTemp
				.replace(/211/g, pageNum)
		);
		//loadPage(pageNum,content[i]);
		//loadEditPage(pageNum,content[i]);

		pageNum++;
	}
	//$('.infotab').append($(sHTML));
	//以下是接口页面//获取角色权限


}

function cb_21_role_role(data) {
	data = data.content
	$('#21_roletemplate').empty();
	var i = data.length;
	while(i--) {
		$('#21_roletemplate').append("<option value='rt" + data[i].id + "'>" + data[i].name + "</option>");
	}
	$('#21_roletemplate').val('rt');
}
function cb_21_role_priv(data) {
	$('#21_list').empty();
	var i = data.length;
	while (i--) {
		if (data[i].parentid == 0) {
			$('#21_list').append('<div class="line211d">' + data[i].privname + '</div>');
			var j = data.length;
			var x = 0;
			while (j--) {
				if (data[j].parentid == data[i].privid) {
					if (x++ % 2 == 0)
						$('#21_list').append('<div class="line211ad"></div>');
					else
						$('#21_list').append('<div class="line211bd"></div>');
					var a = $('#21_list').children().last();
					var g = '<div class="line2111d">'
						+ '<img src="images/select-2.png" class="privgroupd pgcgd" />'
						+ '<input type="checkbox" id="pr' + data[j].privid + '" style="display:none" /> ' 
						+ data[j].privname + '</div>';
					$(a).append(g);
					$(a).append('<div class="line2112d"></div>');
					var b = $(a).children().last();
					var k = data.length;
					while (k--) {
						if (data[k].parentid == data[j].privid) {
							var gp = '<div class="priv2d toleft">'
								+ '<img src="images/select-2.png" class="pgcd" />'
								+ '<input type="checkbox" id="pr' + data[k].privid + '" style="display:none" /> ' 
								+ data[k].privname + '</div>';
							$(b).append(gp);
						}
					}
				}
			}
		}
	}	
}
function cb_21_member_tree(data) {
	$.fn.zTree.init($('#tree21member'), setting21, stripicon(data));
	var t = $.fn.zTree.getZTreeObj('tree21member');
	var ns = t.getNodesByParam('id', 1, null);
	t.expandNode(ns[0], true);
	var datas = JSON.stringify()
	$('#21_memberlist').empty();
	callajax('priv!getMemberByRole', {roleid: currole}, cb_21_member_check)
}
var setting21 = {
	view: {
		showLine: false,
		nameIsHTML: true,
		showIcon: false,
	},
	check: {
		autoCheckTrigger: true,
		chkboxType: { "Y": "ps", "N": "ps" },
		chkStyle: "checkbox",
		enable: true
	},
	data: {
		simpleData: {
			enable:true,
			idKey: "id",
			pIdKey: "pid",
			rootPId: null
		}
	},
	callback: {
		onClick: function(event, treeId, treeNode, clickFlag) {
			if (!treeNode.open) {
				$.fn.zTree.getZTreeObj(treeId).expandNode(treeNode, true);
			}
			else
				$.fn.zTree.getZTreeObj(treeId).expandNode(treeNode, false);
			if (treeNode.flag == 2) {
				
			}
		},
		onCheck: function(event, treeId, treeNode) {
			if (treeNode.flag < 2) return;
			var sellist = $('#21_memberlist').find('div.member21');
			var i = sellist.length;
			while(i--) {
				if ($(sellist[i]).prop('id') == 'm' + treeNode.id){
					if (treeNode.checked == false)
						$(sellist[i].remove());
					return;
				}
			}
			if (treeNode.checked) {
				$('#21_memberlist').append(membertemplate
						.replace(/memberid/g, treeNode.id)
						.replace(/membername/g, treeNode.name));
				$('.member21').hover(function() {
					$(this).addClass('menuhover');
				}, function() {
					$(this).removeClass('menuhover');
				});
			}
		}
	}
};
function cb_21_member_check(data) {
	var t = $.fn.zTree.getZTreeObj('tree21member');
	var j = data.length;
	while(j--) {
		var ns = t.getNodesByParam('id', data[j].memberid);
		var i = ns.length;
		while(i--) {
			t.checkNode(ns[i], true, false, true);
		}
	}
}
function load210() {
	curpage = 0;
	pagenumber = 0;
	callajax('priv!getMemberCountByRole', {roleid: currole}, cb_210_count);
}
function cb_210_count(data) {
	pagenumber = Math.ceil(data.count/itemsperpage);
	if (pagenumber > 0) {
		load210page();
	}
	else {
		$('#list210').empty();
		$('#pagecurr').text('0/0');
		updatebrowse('1', pagenumber, curpage);
	}
}
function load210page() {
	$('#list210').empty();
	$('#pagecurr').text((curpage+1) + '/' + pagenumber);
	var data = {roleid: currole, page: curpage, itemsperpage: itemsperpage};
	callajax('priv!getMemberByRole', data, cb_210_fresh)
	updatebrowse('1', pagenumber, curpage);
}
function cb_210_fresh(data) {
	var i = data.length;
	while(i--) {
		$('#list210').append('<tr></tr>');
		var positionname = data[i].positionname?data[i].positionname:'';
		$('#list210 tr:last-child')
			.append('<td>' + data[i].membername + '</td>')
			.append('<td>' + data[i].branchname + '</td>')
			.append('<td>' + positionname + '</td>')
			.append('<td><img src="images/delete-2.png" onclick="del210(' + data[i].memberroleid + ')"></img></td>');
	}
	$('#list210 tr').hover(function(){
		$(this).addClass('menuhover');
	},function(){
		$(this).removeClass('menuhover');
	});
}

function loadPage(pageNum,appName){
	callajax('limit!getLimitByRole', {roleid: currole,appname:appName}, function(cbData){
		//cb_211_fresh
		cbFreshPage(cbData,pageNum);
	})
}
function cbFreshPage(cbData,pageNum){
	var data = cbData;
	$('#list'+pageNum).empty();
	var i = data.length;
	while (i--) {
		if (data[i].parentid == 0) {
			$('#list'+pageNum).append('<div class="line211">' + data[i].privname + '</div>');
			var j = data.length;
			var x = 0;
			while (j--) {
				if (data[j].parentid == data[i].privid) {
					if (x++ % 2 == 0)
						$('#list'+pageNum).append('<div class="line211a"></div>');
					else
						$('#list'+pageNum).append('<div class="line211b"></div>');
					var a = $('#list'+pageNum).children().last();
					$(a).append('<div class="line2111">' + data[j].privname + '</div>');
					$(a).append('<div class="line2112"></div>');
					var b = $(a).children().last();
					var k = data.length;
					while (k--) {
						if (data[k].parentid == data[j].privid) {
							if (data[k].roleid == currole) {
								$(b).append('<div class="priv toleft"><img src="images/selected.png" style="margin-right: 5px" />' + data[k].privname + '</div>');
							}
							else {
								$(b).append('<div class="priv toleft">' + data[k].privname + '</div>');
							}
						}
					}
				}
			}
		}
	}
}


function loadEditPage(pageNum,appName){
	var data = {roleid:currole,appname:appName}
	callajax('limit!getLimitByRole', data, function(cbData){
		freshCbEditPage(cbData,pageNum)
	})
}
function freshCbEditPage(cbData,pageNum){
	var data = cbData;
	$('#list'+pageNum+'edit').empty();
	var i = data.length;
	while (i--) {
		if (data[i].parentid == 0) {
			$('#list'+pageNum+'edit').append('<div class="line211">' + data[i].privname + '</div>');
			var j = data.length;
			var x = 0;
			while (j--) {
				if (data[j].parentid == data[i].privid) {
					if (x++ % 2 == 0)
						$('#list'+pageNum+'edit').append('<div class="line211a"></div>');
					else
						$('#list'+pageNum+'edit').append('<div class="line211b"></div>');
					var a = $('#list'+pageNum+'edit').children().last();
					var g = '<div class="line2111">'
						+ '<img src="images/select-2.png" class="privgroup pgcg">'
						+ '<input type="checkbox" id="p' + data[j].privid + '" style="display:none" />'
						+ data[j].privname + '</div>';
					$(a).append(g);
					$(a).append('<div class="line2112"></div>');
					var b = $(a).children().last();
					var k = data.length;
					while (k--) {
						if (data[k].parentid == data[j].privid) {
							var gp;
							if (data[k].roleid == currole) {
								gp = '<div class="priv2 toleft">'
								+ '<img src="images/select-1.png" class="pgc">'
								+ '<input type="checkbox" id="p' + data[k].privid + '" style="display:none" checked />'
								+ data[k].privname + '</div>';
							}
							else {
								gp = '<div class="priv2 toleft">'
								+ '<img src="images/select-2.png" class="pgc">'
								+ '<input type="checkbox" id="p' + data[k].privid + '" style="display:none" />'
								+ data[k].privname + '</div>';
							}
							$(b).append(gp);
						}
					}
				}
			}
		}
	}
}

function loadSava(pageNum){
	var appName = $('.infotab .infotabi[bindpage='+pageNum+']').html();
	callajax('limit!getLimitByRole', {roleid: currole,appname:appName}, function(cb){
		cbEditFresh(cb,pageNum)
	})
}


function cbEditFresh(cbData,pageNum) {
	var data = cbData
	var curPage = $('#list'+pageNum+'edit')
	curPage.empty();
	var i = data.length;
	while (i--) {
		if (data[i].parentid == 0) {
			curPage.append('<div class="line211">' + data[i].privname + '</div>');
			var j = data.length;
			var x = 0;
			while (j--) {
				if (data[j].parentid == data[i].privid) {
					if (x++ % 2 == 0)
						curPage.append('<div class="line211a"></div>');
					else
						curPage.append('<div class="line211b"></div>');
					var a = curPage.children().last();
					var g = '<div class="line2111">'
						+ '<img src="images/select-2.png" class="privgroup pgcg">'
						+ '<input type="checkbox" id="p' + data[j].privid + '" style="display:none" />'
						+ data[j].privname + '</div>';
					$(a).append(g);
					$(a).append('<div class="line2112"></div>');
					var b = $(a).children().last();
					var k = data.length;
					while (k--) {
						if (data[k].parentid == data[j].privid) {
							var gp;
							if (data[k].roleid == currole) {
								gp = '<div class="priv2 toleft">'
								+ '<img src="images/select-1.png" class="pgc">'
								+ '<input type="checkbox" id="p' + data[k].privid + '" style="display:none" checked />'
								+ data[k].privname + '</div>';
							}
							else {
								gp = '<div class="priv2 toleft">'
								+ '<img src="images/select-2.png" class="pgc">'
								+ '<input type="checkbox" id="p' + data[k].privid + '" style="display:none" />'
								+ data[k].privname + '</div>';
							}
							$(b).append(gp);
						}
					}
				}
			}
		}
	}
}

//删除人员
function del210(id) {
	if (currole == 1) {
		bootbox.alert({'title':'提示', 'message':'不能删除组织管理员.', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');	
		}});
		return;
	}
	
	//权限
	//if (has('qxglxg')) {
		bootbox.confirm({
			title: '提示',
			message:'确定删除么 ？',
			callback: function(result) {
				if (result) {
					callajax('priv!delMemberRole', {id: id}, cb_210_del)
				}
				$('#container').css('width', document.body.clientWidth + 'px');
			}
		});
	//}
	//else {
	//	bootbox.alert({'title':'提示','message':'您没有权限删除人员', callback: function() {
	//		$('#container').css('width', document.body.clientWidth + 'px');
	//	}});
	//}
}
function cb_210_del(data) {
	load210();
}
//编辑后保存
function savaEditPage(pageNum){
	var inps = $('#'+pageNum+'edit').find('input');
	var i = inps.length;
	var data = '';
	while(i--) {
		if (inps[i].checked == true) {
			if (data != '') data += ',';
			data += inps[i].id.substr(1);
		}
	}
	var appName = $('.infotabi.tabactive').attr('appid');
	var roleName = $('.prv21active').html();
	callajax('limit!saveRolebyApp', {roleid: currole, privs: data,appsecretId:appName,roleName:roleName}, function(cb){
		cbEditSave(cb,pageNum);
	});
}
//编辑后保存的回调
function cbEditSave(cb,pageNum){
	var appName = $('.infotab .infotabi[bindpage='+pageNum+']').html();
	loadPage(pageNum,appName);
	loadSava(pageNum)
	//loadSavePage(pageNum);
	showpage(pageNum);
}

//删除角色
function delrole() {
	if (currole == 1) {
		bootbox.alert({title:'提示', message:'不能删除组织管理员.', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');	
		}});
		return;
	}

	//权限
	//if (has('qxglsc')) {
		if (currole == 0) {
			bootbox.alert({title:'提示', message:'请先选择身份.', callback: function() {
				$('#container').css('width', document.body.clientWidth + 'px');	
			}});
		}
		else {
			bootbox.confirm({
				title: '提示', 
				message:'确定删除么 ？',
				callback: function(result) {
					if (result) {
						callajax('limit!delRole', {roleId: currole}, cb_21_del);
					}
					$('#container').css('width', document.body.clientWidth + 'px');	
				}
			});
		}
	//}
	//else {
	//	bootbox.alert({'title':'提示','message':'您没有权限删除身份', callback: function() {
	//		$('#container').css('width', document.body.clientWidth + 'px');
	//	}});
	//}
}
//确定删除角色
function cb_21_del(data) {
	var $a = $('#r' + currole);
	var $b = $a.prev();
	$a.remove();
	$('#sanjiao').remove();
	$b.addClass('prv21active');
	$b.after('<img id="sanjiao" src="images/roleselect.png" style="float:right" />');
	currole = $b[0].id.substr(1);
	load210();
	//load211();
	//load211Save();
	////保存211页面的修改
	//load213();
	////load213Save();
    //
	//load214();
	////load214Save();

}

function showpage(cp) {
	curpage = cp;
	changeRoleList(curpage);
	if(cp!='210'){
		if(typeof (curpage)=='number'){
			var appName = $('.infotab .infotabi[bindpage='+curpage+']').html();
			loadPage(cp,appName);
			loadEditPage(cp,appName)
		}
		$('#editmember').hide();
	}else{
		$('#editmember').show();
		load210()
	}
	$('.collHide').hide();
	$('#' + cp).show();
	//if(curpage.indexOf('edit')!=-1){
	//curpage = curpage.replace(/edit/g,'');
	//}
	//if(typeof(curpage)=='number'){
	//	pageNum = curpage;
	//}else{
	//	var pageNum = curpage.replace('edit','')
	//}
	//var appName = $('.infotab .infotabi[bindpage='+pageNum+']').html();

	//loadPage(cp,appName);
	//loadEditPage(cp,appName);
}
function changeRoleList(curpage){
	if(curpage=='210'){
		appName = '';
	}else{
		if(typeof (curpage)=='number'){
			var appName = $('.infotab .infotabi[bindpage='+curpage+']').html();
			var appID = $('.infotab .infotabi[bindpage='+curpage+']').attr('appid')
		}else{
			var pageNum = curpage.replace('edit','')
			var appName = $('.infotab .infotabi[bindpage='+pageNum+']').html();
			var appID = $('.infotab .infotabi[bindpage='+pageNum+']').attr('appid')

		}
	}
	callajax('limit!getRoleList', {appId: appID}, rollList);
}
function rollList(data){
	var data = data.content
	initRoleList(data);
}
function stripicon(data) {
	var i = data.length;
	while (i--) {
		data[i].name = data[i].name.substr(iconlenth);
	}
	return data;
}


