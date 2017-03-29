var pagecnumber = 0;
var curpagec = 0;
var itemsperpagec = 2;
var itemtemplatec='<tr id="tgmid">'
	+ '<td class="ftd">creator</td>'
	+ '<td>name</td>'
	+ '<td>account</td></tr>';
$(document).ready(function() {
	
	$('#pagecfirst').click(function() {
		if (pagecnumber == 0) return;
		if (curpagec == 0) return;
		curpagec = 0;
		loadpagec();
	});
	$('#pagecprev').click(function() {
		if (pagecnumber == 0) return;
		if (curpagec == 0) return;
		curpagec--;
		loadpagec();
	});
	$('#pagecnext').click(function() {
		if (pagecnumber == 0) return;
		if (curpagec + 1 == pagecnumber) return;
		curpagec++;
		loadpagec();
	});
	$('#pageclast').click(function() {
		if (pagecnumber == 0) return;
		if (curpagec + 1 == pagecnumber) return;
		curpagec = pagecnumber - 1;
		loadpagec();
	});
});
function cb_13_creator_count(data) {
	pagecnumber = Math.ceil(data.id/itemsperpagec);
	if (pagecnumber > 0) {
		loadpagec();
	}
	else {
		$('#creatorlist').empty();
		$('#pageccurr').text('0/0');
		updatebrowse('2', pagecnumber, curpagec);
	}
}
function loadpagec() {
	$('#creatorlist').empty();
	$('#pageccurr').text((curpagec+1) + '/' + pagecnumber);
	var data = {id: grpid, page: curpagec, itemsperpage: itemsperpagec};
	callajax('grp!getMemberByGrp', data, cb_13_creator_fresh);
	updatebrowse('2', pagecnumber, curpagec);
}
function cb_13_creator_fresh(data) {
	var l = data.length;
	var i = 0;
	while(i < l) {
		var line = itemtemplatec
				.replace('gmid', data[i].gmid)
				.replace('name', data[i].name)
				.replace('account', data[i].account);
		if (data[i].iscreator == '1') {
			line = line.replace('creator', '<input class=mainpos value=创建人 readonly />');
		}
		else {
			line = line.replace('creator', '');
		}
		$('#creatorlist').append(line);
		i++;
	}
	$('#creatorlist tr').hover(function(){
		$(this).addClass('menuhover');
		if ($(this).find('td:first').html() == '') {
			$(this).find('td:first').html('<button class="makemain" style="width:94px" onclick="change(' + this.id.substr(1) + ')">设为创建人</button>');
		}
	},function(){
		$(this).removeClass('menuhover');
		if ($(this).find('td:first').find('button').length > 0) {
			$(this).find('td:first').html('');
		}
	});
}
function change(gmid) {
	callajax('grp!change', {id: grpid, gmid: gmid}, cb_13_change);
}
function cb_13_change(data) {
	$('.ftd').html('');
	$('#t' + data.id).find('td:first').html('创建人');
}