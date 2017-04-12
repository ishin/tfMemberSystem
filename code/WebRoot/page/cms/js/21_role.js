$(document).ready(function() {
	
	$('#role').validVal();
	console.log(111);

	callajax('appinfoconfig!getAppName','',fillAppSelect);

	$('#21_apptemplate').change(function() {
		console.log(222);
		var appID = $(this).val();
		var appName = $(this).find('option[value='+appID+']').html();
		var appId = $(this).find('option[value='+appID+']').val();
		callajax('limit!getRoleList', {appId:appId}, changeRoleSelect);
		callajax('limit!getLimitByRole', {roleid: 0,appname:appName}, cb_21_role_priv)
	});

	$('#21_roletemplate').change(function() {
		var appID = $('#21_apptemplate').val();
		var appName = $('#21_apptemplate').find('option[value='+appID+']').html();
		var roleName = $(this).val();
		callajax('limit!getLimitByRole', {roleid:roleName ,appname:appName}, cb_21_role_fresh)
	});
	$('#save21role').click(function() {
		if ($( "#role" ).triggerHandler( "submitForm" ) == false) return;
		var rolename = $('#21_rolename').val();
		var inps = $('#21_list').find('input');
		var i = inps.length;
		var data = '';
		while(i--) {
			if (inps[i].checked == true) {
				if (data != '') data += ',';
				data += inps[i].id.substr(2);
			}
		}
		var appsecretId = $('#21_apptemplate').val();
		callajax('limit!saveRolebyApp', {roleid:0,appsecretId:appsecretId,roleName: rolename, privs: data}, cb_21_role_save);
	});
});

function changeRoleSelect(data){
	var content = data.content;
	if(content){
		var sHTML = '<option value="0"></option>';

		for(var i = 0;i<content.length;i++){
			sHTML += '<option value="'+content[i].id+'">'+content[i].name+'</option>'
		}
		$('#21_roletemplate').html(sHTML);
	}else{
		$('#21_roletemplate').html('');
	}


}
function fillAppSelect(data){
	var content = data.content;
	var sHTML = '';
	for(var i = 0;i<content.length;i++){
		sHTML += '<option value="'+content[i].id+'">'+content[i].appName+'</option>'
	}
	$('#21_apptemplate').html(sHTML);
	console.log(content[0]);
	callajax('limit!getRoleList', {appId:content[0].id}, changeRoleSelect);
	callajax('limit!getLimitByRole', {roleid: 0,appname:content[0].appName}, cb_21_role_priv);

	//console.log('333')
	//$('#21_apptemplate').change();
}
function cb_21_role_fresh(data) {
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
							var gp;
							if (data[k].roleid > 0) {
								gp = '<div class="priv2d toleft">'
									+ '<img src="images/select-1.png" class="pgcd" />'
									+ '<input type="checkbox" id="pr' + data[k].privid + '" style="display:none" checked /> ' 
									+ data[k].privname + '</div>';
							}
							else {
								gp = '<div class="priv2d toleft">'
									+ '<img src="images/select-2.png" class="pgcd" />'
									+ '<input type="checkbox" id="pr' + data[k].privid + '" style="display:none" /> ' 
									+ data[k].privname + '</div>';
							}
							$(b).append(gp);
						}
					}
				}
			}
		}
	}	
};
function cb_21_role_save(data) {
	console.log('afteraddrole');
	if ($('#21rolecontinue').prop('checked') == false) {
		$('#role').modal('hide');
	}
	//$('#list21').append('<li class="prv21 toleft" style="width: 100%" id="r' + data.id + '">' + $('#21_rolename').val() + '</li>');
	//$('#list21').find('li:last-child').css('width', $('#list21').find('li:last-child').css('width').replace('px', '') - 10);
	//$('#21_rolename').val('');
	console.log(curpage);
	var curPage = $('.infotabi.tabactive');
	var bindPage = curPage.attr('bindpage')
	if(bindPage == 0||bindPage == '210'){
		var appName = '';
		var appId = '';
	}else{
		var appName = curPage.html();
		var appId = curPage.attr('appid');
	}
	callajax('limit!getRoleList', {appId: appId}, rollList);
}