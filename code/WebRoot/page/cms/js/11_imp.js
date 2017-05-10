var tid;
var rep = 0;
var reps = 40;
var XLS = 'application/vnd.ms-excel';
var XLSX = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
var fields = ['email', 'telephone', 'position', 'manager', 'branch', 'sex', 'workno', 'name', 'mobile'];
var groups = ['good', 'well', 'bad'];
$(document).ready(function() {

	$('.downloadDemo').click(function() {
		//console.log('11111');
		if (window.Electron) {
			var url = $(this).attr('href');
			var localPath = window.Electron.chkFileExists(url);
			if(localPath){//本地有这个文件
				window.Electron.openFileDir(url);
				return false;
			}
		}
	})
	$('#imp2').on('dblclick', '.errimp', function() {
		console.log('111111');
		$(this).removeClass('errimp');
		$(this).prop('title', '');
		var text = ($(this).text() == '(需要填写)') ? '' : $(this).text();
		$(this).empty();
		$(this).append('<input class="editimp" value="" />');
		$(this).find('input').focus().val(text);
		
		$('.editimp').blur(function() {
			leave(this);
		});
		$('.editimp').keyup(function(e) {
			if (e.keyCode == 13) {
				leave(this);
			}
		});
	});
	$('#imp2').on('click', '.deltr', function() {
		var tb = $(this).parent().parent().parent();
		var group = tb.prop('id').substr(4);
		if (group != 'good') {
			$(this).parent().parent().remove();
			updatec();
		}
		else {
			var tdname = $(this).parent().parent().find('td[field=tdname]')[0];
			var name = $(tdname).text();
			$(this).parent().parent().remove();
			updategood(name);
		}
	});
	$('.result').click(function() {
		var img = $(this).find('img')[0];
		if ($(img).prop('src').indexOf('open') > 0) {
			$(img).prop('src','images/close.png');
			$(this).parent().next().hide();
		}
		else {
			$('#content').find('.result').find('img').prop('src', 'images/close.png');
			$('#content').find('.resultlist').hide();
			$(img).prop('src', 'images/open.png');
			$(this).parent().next().show();
		}
	});
	$('#imp1').on('change', '#impfile', function() {
		if (this.files.length > 0) {
			var t = this.files[0].type;
			if ( t == XLS || t == XLSX) {

				$('#impform').prop('action', path + 'admimp').submit();
				rep = 0;
				tid = setInterval('onimp()', 250);
			}
			else {
				bootbox.alert({
					'title': '提示',
					'message': '请选择XLS、XLSX格式文件.',
					callback: function() {
						$('#container').css('width', document.body.clientWidth + 'px');	
					},
				});
			}
			$('#impfile').val('');
//			$('#impform').empty();
//			$('#impform').append('<input type="file" name="impfile"  id="impfile" />');
		}
	});
});
function showlist(result) {
	$('#content').find('.result').find('img').prop('src', 'images/close.png');
	$('#content').find('.resultlist').hide();
	
	var tb = 'impl' + result;
	var resultlist = $('#' + tb).parent().parent().parent();
	$(resultlist).prev().find('img').prop('src', 'images/open.png');
	$(resultlist).show();
}
function leave(a) {
	var text = $(a).val();
	if (text == '(需要填写)') text = '';
	var td = $(a).parent();
	$(td).empty();
	if (text == '') {
		$(td).append('(需要填写)');
		$(td).addClass('errimp');
		$(td).prop('title', '双击修改');
	}
	else {
		$(td).append(text);
		onedit($(td).parent());
	}
}
function onedit(tr) {
	if (tr.find('.errimp').length > 0) return;
	var js = [];
	js[0] = tojson(tr);
	callajax('branch!impcheck', {'jtext': JSON.stringify(js)}, function(data) {
		if (data.status == 3) {
			bootbox.alert({'title': '提示', 'message': '数据库访问错误，请联系系统管理员.', callback: function() {
				$('#container').css('width', document.body.clientWidth + 'px');	
			}});
		}
		else {
			if (data['bad'].length == 1) {
				edittr(tr, data['bad'][0], 'bad');
			}
			else {
				$(tr).remove();
				adddata(data);
			}
			updatebad();
		}
	});
}
function updategood(name) {
	
	var trs = $('#implgood').find('tr');
	var i = trs.length;
	while(i-- > 0) {
		var tdmanager = $(trs[i]).find('td[field=tdmanager]')[0];
		if ($(tdmanager).text() == name) {
			
			var js = [];
			js[0] = tojson(trs[i]);
			callajax('branch!impcheck', {'jtext': JSON.stringify(js)}, function(data) {
				if (data.status == 3) {
					bootbox.alert({'title': '提示', 'message': '数据库访问错误，请联系系统管理员.', callback: function() {
						$('#container').css('width', document.body.clientWidth + 'px');	
					}});
				}
				else {
					if (data['bad'].length == 1) {
						movtrtobad(trs[i]);
					}
				}
			});
			
		}
	}
	
	updatec();
}
function movtrtobad(tr) {
	var td = $(tr).find('[field=tdmanager]')[0];
	$(td).addClass('errimp');
	$(td).prop('title', '双击修改');
	$('#implbad').append('<tr></tr>');
	$('#implbad').find('tr:last').append($(tr).html());
	$(tr).remove();
}
function updatebad() {
	
	while(true) {
		var f = false;
		var tds = $('#implbad').find('.errimp[field=tdmanager]');
		var i = tds.length;
		while(i-- > 0) {
			if (testmanager($(tds[i]).text()) == true) {
				movtrtogood($(tds[i]).parent());
				f = true;
				break;
			}
		}
		if (f == false) break;
	}
	updatec();
}
function movtrtogood(tr) {
	
	var td = $(tr).find('[field=tdmanager]')[0];
	$(td).removeClass('errimp');
	$(td).prop('title', '');
	$('#implgood').append('<tr></tr>');
	$('#implgood').find('tr:last').append($(tr).html());
	$(tr).remove();
}
function testmanager(manager) {
	var tds = $('#implgood').find('td[field=tdname]');
	var i = tds.length;
	while (i-- > 0) {
		if (manager==$(tds[i]).text()) return true;
	}
	return false;
}
function tojson(tr) {
	var tds = $(tr).find('td[field]');
	var i = tds.length;
	var json = {};
	while(i-- > 0) {
		json[$(tds[i]).attr('field').substr(2)] = $(tds[i]).text();
	}
	return json;
}
function onimp() {
	if (rep++ < reps) {
		var d = $(window.frames["imptarget"].document);
		if (d.children(0)[0].innerText != '') {
			var t = d.children(0)[0].innerText;
			$(d.children(0)[0]).empty();
			if (t.indexOf('status') > 0) {
				clearInterval(tid);
				var ret = $.parseJSON(t);
				if (ret.status == 1) {
					bootbox.alert({'title': '提示', 'message': '文件类型错误，请重新选择文件.', callback: function() {
						$('#container').css('width', document.body.clientWidth + 'px');	
					}});
				}
				else if (ret.status == 2) {
					bootbox.alert({'title': '提示', 'message': '文件读取错误，请联系系统管理员.', callback: function() {
						$('#container').css('width', document.body.clientWidth + 'px');	
					}});
				}
				else if (ret.status == 3) {
					bootbox.alert({'title': '提示', 'message': '数据库访问错误，请联系系统管理员.', callback: function() {
						$('#container').css('width', document.body.clientWidth + 'px');	
					}});
				}
				else if (ret.status == 4) {
					bootbox.alert({'title': '提示', 'message': '文件格式错误，请重新选择文件.', callback: function() {
						$('#container').css('width', document.body.clientWidth + 'px');	
					}});
				}
				else {
					showdata(ret);
					$('#imp1').hide();
					$('#imp2').show();
				}
			}
		}
	}
	else {
		clearInterval(tid);
	}
}
function showdata(data) {

	var x = groups.length;
	while (x-- > 0) {
		$('#impl' + groups[x]).empty();
	}
	
	adddata(data);
	
	if (data.bad.length > 0) {
		showlist('bad');
	}
	else {
		showlist('good');
	}
}
function adddata(data) {
	
	var i;
	var x = groups.length;
	while (x-- > 0) {
		i = 0;
		while(i < data[groups[x]].length) {
			addtr($('#impl' + groups[x]), data[groups[x]][i], groups[x]);
			i++;
		}
	}
	
	updatec();
}
function updatec() {
	
	var x = groups.length;
	while (x-- > 0) {
		$('#c' + groups[x]).text($('#impl' + groups[x]).find('tr').length);
	}	
}
function edittr(tr, data, group) {
	
	$(tr).after('<tr></tr>');
	var ntr = $(tr).next();
	var j = 9;
	while (j-- > 0) {
		$(ntr).append(gettd(fields[j], data[fields[j]], group));
	}
	$(ntr).append('<td><img class="deltr" src="images/delete.png" title="删除" /></td>');
	$(tr).remove();
}
function addtr(tb, data, group) {

	$(tb).append('<tr></tr>');
	var tr = $(tb).find('tr:last')[0];
	var j = 9;
	while (j-- > 0) {
		$(tr).append(gettd(fields[j], data[fields[j]], group));
	}
	$(tr).append('<td><img class="deltr" src="images/delete.png" title="删除" /></td>');
}
function gettd(field, data, group) {
	
	if (group != 'bad') {
		return '<td field="td' + field + '">' + data + '</td>';
	}
	
	var td;
	if (data == ''&&field!='sex'&&field!='position'&&field!='telephone'&&field!='email') {
		td = '<td field="td' + field + '" class="errimp" title="双击修改">(需要填写)</td>';
	}
	else if (data.indexOf('##') == 0) {
		td = '<td field="td' + field + '" class="errimp" title="双击修改">' + data.substr(2) + '</td>';
	}
	else {
		td = '<td field="td' + field + '">' + data + '</td>';
	}

	return td;
}
function closeimp() {
	$('#imp').modal('hide');
	$('#imp1').show();
	$('#imp2').hide();
	$('#imp3').hide();
}
function okimp() {

	var trs = $('#implgood').find('tr');
	
	if (trs.length < 1) {
		bootbox.alert({'title': '提示', 'message': '没有数据可以导入.', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');	
		}});
		return;
	}
	var i = 0;
	var js = [];
	while(i < trs.length) {
		js[i] = tojson(trs[i]);
		i++;
	}

	callajax('branch!impsave', {'jtext': JSON.stringify(js)}, function(data) {
		if (data.status == 0) {
			//alert(data.succeed);
			$('#succeed').text(data.succeed);
			$('#fail').text(data.fail);
			$('#imp2').hide();
			$('#imp3').show();
			callajax("branch!getOrganTree", "", cb_11_tree);
		}
		else {
			bootbox.alert({'title': '提示', 'message': '保存失败，请联系系统管理员.', callback: function() {
				$('#container').css('width', document.body.clientWidth + 'px');	
			}});
		}
	});
}