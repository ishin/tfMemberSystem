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
$(document).ready(function(){

//	$('.line21').css('width', document.body.clientWidth * 0.12 + 'px');
	$('.sidebar12').css('height', '1440px');
	
	showpage('210');
	
	if (has('qxglck')) {
		callajax('priv!getRoleList', '', cb_21_fresh);
	}
	
	$('#role').on('shown.bs.modal', function(e) {
		callajax('priv!getRoleList', '', cb_21_role_role);
		callajax('priv!getPrivByRole', {roleid: 0}, cb_21_role_priv)
	});

	$('#member').on('shown.bs.modal', function(e) {
		callajax("branch!getOrganTree", "", cb_21_member_tree);
	});

	$('body').on('click', '#list21 li', function() {
		$(this).parent().find('li').removeClass('prv21active');
		$('#sanjiao').remove();
		$(this).addClass('prv21active');
		$(this).after('<img id="sanjiao" src="images/roleselect.png" style="float:right" />');
		if (curpage == '212') {
			showpage('210');
		}
		currole = this.id.substr(1);
		load210();
		load211();
		load212();
	});
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
	$('#addrole').click(function(){
		
		//权限
		if (has('qxgltj')) {
			$('#role').modal({
				backdrop: false,
				remote: '21_role.jsp'
			});
		}
		else {
			bootbox.alert({'title':'提示','message':'您没有权限添加身份', callback: function() {
				$('#container').css('width', document.body.clientWidth + 'px');	
			}});
		}
	});
	$('#editmember').click(function(){
		if (currole == 1) {
			bootbox.alert({'title':'提示', 'message':'不能修改组织管理员.', callback: function() {
				$('#container').css('width', document.body.clientWidth + 'px');	
			}});
			return;
		}

		//权限
		if (has('qxglxg')) {
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
		}
		else {
			bootbox.alert({'title':'提示','message':'您没有权限新增/修改人员.', callback: function() {
				$('#container').css('width', document.body.clientWidth + 'px');	
			}});
		}
	});
	$('#editpriv').click(function() {
		if(currole == 1) {
			bootbox.alert({"title":"提示","message":"不能修改组织管理员.", callback: function() {
				$('#container').css('width', document.body.clientWidth + 'px');	
			}});
			return;
		}

		//权限
		if (has('qxglxg')) {
			if (currole == 0) {
				bootbox.alert({title:'提示', message:'请先选择身份.', callback: function() {
					$('#container').css('width', document.body.clientWidth + 'px');	
				}});
			}
			else {
				showpage("212");
			}
		}
		else {
			bootbox.alert({'title':'提示','message':'您没有权限修改权限.', callback: function() {
				$('#container').css('width', document.body.clientWidth + 'px');	
			}});
		}
	});
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
function cb_21_role_role(data) {
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
		$('#list210 tr:last-child')
			.append('<td>' + data[i].membername + '</td>')
			.append('<td>' + data[i].branchname + '</td>')
			.append('<td>' + data[i].positionname + '</td>')
			.append('<td><img src="images/delete-2.png" onclick="del210(' + data[i].memberroleid + ')"></img></td>');
	}
	$('#list210 tr').hover(function(){
		$(this).addClass('menuhover');
	},function(){
		$(this).removeClass('menuhover');
	});
}
function load211() {
	callajax('priv!getPrivByRole', {roleid: currole}, cb_211_fresh)
}
function cb_211_fresh(data) {
	$('#list211').empty();
	var i = data.length;
	while (i--) {
		if (data[i].parentid == 0) {
			$('#list211').append('<div class="line211">' + data[i].privname + '</div>');
			var j = data.length;
			var x = 0;
			while (j--) {
				if (data[j].parentid == data[i].privid) {
					if (x++ % 2 == 0)
						$('#list211').append('<div class="line211a"></div>');
					else
						$('#list211').append('<div class="line211b"></div>');
					var a = $('#list211').children().last();
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
function load212() {
	callajax('priv!getPrivByRole', {roleid: currole}, cb_212_fresh)
}
function cb_212_fresh(data) {
	$('#list212').empty();
	var i = data.length;
	while (i--) {
		if (data[i].parentid == 0) {
			$('#list212').append('<div class="line211">' + data[i].privname + '</div>');
			var j = data.length;
			var x = 0;
			while (j--) {
				if (data[j].parentid == data[i].privid) {
					if (x++ % 2 == 0)
						$('#list212').append('<div class="line211a"></div>');
					else
						$('#list212').append('<div class="line211b"></div>');
					var a = $('#list212').children().last();
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
function cb_21_fresh(data) {
	$('#list21').empty();
	var i = data.length;
	while (i--) {
		if (currole == 0) currole = data[i].id;
		$('#list21').append('<li class="prv21 toleft" style="width: 100%" id="r' + data[i].id + '">' + data[i].name + '</li>');
		$('#list21').find('li:last-child').css('width', $('#list21').find('li:last-child').css('width').replace('px', '') - 10);
	}
	$('#list21').find('li:first-child').addClass('prv21active');
	$('#list21').find('li:first-child').after('<img id="sanjiao" src="images/roleselect.png" style="float:right" />');
	load210();
	load211();
	load212();
}
function del210(id) {
	if (currole == 1) {
		bootbox.alert({'title':'提示', 'message':'不能删除组织管理员.', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');	
		}});
		return;
	}
	
	//权限
	if (has('qxglxg')) {
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
	}
	else {
		bootbox.alert({'title':'提示','message':'您没有权限删除人员', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');	
		}});
	}
}
function cb_210_del(data) {
	load210();
}
function save212() {
	var inps = $('#212').find('input');
	var i = inps.length;
	var data = '';
	while(i--) {
		if (inps[i].checked == true) {
			if (data != '') data += ',';
			data += inps[i].id.substr(1);
		}
	}
	callajax('priv!saveRole', {roleid: currole, privs: data}, cb_212_save);
}
function cb_212_save(data) {
	load211();
	load212();
	showpage('211');
}
function delrole() {
	if (currole == 1) {
		bootbox.alert({title:'提示', message:'不能删除组织管理员.', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');	
		}});
		return;
	}

	//权限
	if (has('qxglsc')) {
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
						callajax('priv!delRole', {roleid: currole}, cb_21_del);
					}
					$('#container').css('width', document.body.clientWidth + 'px');	
				}
			});
		}
	}
	else {
		bootbox.alert({'title':'提示','message':'您没有权限删除身份', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');	
		}});
	}
}
function cb_21_del(data) {
	var $a = $('#r' + currole);
	var $b = $a.prev();
	$a.remove();
	$('#sanjiao').remove();
	$b.addClass('prv21active');
	$b.after('<img id="sanjiao" src="images/roleselect.png" style="float:right" />');
	currole = $b[0].id.substr(1);
	load210();
	load211();
	load212();
}
function showpage(cp) {
	curpage = cp;
	$('#210').hide();
	$('#211').hide();
	$('#212').hide();
	$('#' + cp).show();
}
function stripicon(data) {
	var i = data.length;
	while (i--) {
		data[i].name = data[i].name.substr(iconlenth);
	}
	return data;
}