var pagenumber = 0;
var curpage = 0;
var itemsperpage = 10;
var itemtemplate='<tr id="traid">'
	+'<td>code</td>'
	+'<td>name</td>'
	+'<td>member</td>'
	+'<td>date</td>'
	+'<td>'
		+'<button onclick="create(aid)" class="btntable" style="margin-right:10px"><img src="images/edit-1.png" /> 修改创建者</button>'
		+'<button onclick="dismiss(aid)" class="btntable"><img src="images/delete.png" /> 解散群</button>'
	+'</td></tr>';
var grpid = 0;
$(document).ready(function() {
	
	itemsperpage = Math.floor((document.body.clientHeight - 300) / 40);
	var h = document.body.clientHeight - 50;
	$('.sidebar12').css('height', h + 'px');
	$('.menupanel12').css('width', (document.body.clientWidth - 22) * 0.13);

	//权限
	if (has('qzglck')) {
//		curpage = 0;
//		pagenumber = 0;
		callajax('grp!getCount', '', cb_13_count);
	}

	$('#pagefirst').click(function() {
		if (pagenumber == 0) return;
		if (curpage == 0) return;
		curpage = 0;
		loadpage();
	});
	$('#pageprev').click(function() {
		if (pagenumber == 0) return;
		if (curpage == 0) return;
		curpage--;
		loadpage();
	});
	$('#pagenext').click(function() {
		if (pagenumber == 0) return;
		if (curpage + 1 == pagenumber) return;
		curpage++;
		loadpage();
	});
	$('#pagelast').click(function() {
		if (pagenumber == 0) return;
		if (curpage + 1 == pagenumber) return;
		curpage = pagenumber - 1;
		loadpage();
	});

	$('#creator').on('shown.bs.modal', function(e) {
		curpagec = 0;
		pagecnumber = 0;
		callajax('grp!getMemberCountByGrp', {id: grpid}, cb_13_creator_count);
	});
});
function cb_13_count(data) {
	pagenumber = Math.ceil(data.id/itemsperpage);
	if (pagenumber > 0) {
		loadpage();
	}
	else {
		$('#grouplist').empty();
		$('#pagecurr').text('0/0');
		updatebrowse('1', pagenumber, curpage);
	}
}
function loadpage() {
	$('#grouplist').empty();
	$('#pagecurr').text((curpage+1) + '/' + pagenumber);
	var data = {page: curpage, itemsperpage: itemsperpage};
	callajax('grp!getList', data, cb_13_fresh);
	updatebrowse('1', pagenumber, curpage);
}
function cb_13_fresh(data) {
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
	$('#grouplist tr').hover(function() {
		$(this).addClass('menuhover');
	},function() {
		$(this).removeClass('menuhover')
	});
}
function create(id) {

	//权限
	if (has('qzglxg')) {
		grpid = id;
		$('#creator').modal({
			backdrop: false,
			remote: '13_creator.jsp'
		});
	}
	else {
		bootbox.alert({'title':'提示','message':'您没有权限修改创建者.', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');	
		}});
	}
}
function dismiss(id) {

	//权限
	if (has('qzgljs')) {
		bootbox.confirm({
			'title': '提示',
			'message': '确定解散群么？',
			callback: function(result) {
				if (!result) return;
				callajax('grp!dismiss', {id: id}, cb_13_dismiss);
				$('#container').css('width', document.body.clientWidth + 'px');	
			},
		});
	}
	else {
		bootbox.alert({'title':'提示','message':'您没有权限解散群组.', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');	
		}});
	}
}
function cb_13_dismiss(data) {
	$('#tr' + data.id).remove();
}