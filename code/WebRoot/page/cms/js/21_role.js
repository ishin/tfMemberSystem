$(document).ready(function() {
	
	$('#role').validVal();

	$('#21_roletemplate').change(function() {
		callajax('priv!getPrivByRole', {roleid: $(this).val().substr(2)}, cb_21_role_fresh)
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
		callajax('priv!saveRole', {rolename: rolename, privs: data}, cb_21_role_save);
	});
});
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
	if ($('#21rolecontinue').prop('checked') == false) {
		$('#role').modal('hide');
	}
	$('#list21').append('<li class="prv21 toleft" style="width: 100%" id="r' + data.id + '">' + $('#21_rolename').val() + '</li>');
	$('#list21').find('li:last-child').css('width', $('#list21').find('li:last-child').css('width').replace('px', '') - 10);
	$('#21_rolename').val('');
}