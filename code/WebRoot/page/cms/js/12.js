var info = {};
var optiontemplate = '<option value="id">name</option>';
var imagedir = '../../images/';
$(document).ready(function() {
	
	$('.col12').validVal();
	
	var h = document.body.clientHeight > 1520 ? document.body.clientHeight : '1520';
	$('.sidebar12').css('height', h + 'px');

	$('#provinceid').change(function() {
		info.provinceid = this.value;
		info.cityid = 0;
		info.districtid = 0;
		$('#districtid').empty();
		callajax('org!getCity', {provinceid: this.value}, cb_12_city);
	});
	$('#cityid').change(function() {
		info.districtid = 0;
		callajax('org!getDistrict', {cityid: this.value}, cb_12_district);
	});
	$('#industryid').change(function() {
		info.industryid = this.value;
		info.subdustryid = 0;
		callajax('org!getSubdustry', {industryid: this.value}, cb_12_subdustry);
	});
	loadmeta();
	loaddata();
	$('#save12').click(function() {
		if ($( ".col12" ).triggerHandler( "submitForm" ) == false) return;

		//权限
		if (! has('zzxxglxg')) {
			bootbox.alert({'title':'提示','message':'您没有权限修改组织信息', callback: function() {
				$('#container').css('width', document.body.clientWidth + 'px');	
			}});
			return;
		}

		info.code =	$('#code').val();
		info.name =	$('#name').val();
		info.shortname =	$('#shortname').val();
		info.englishname =	$('#englishname').val();
		info.ad =	$('#ad').val();
		info.provinceid =	$('#provinceid').val();
		info.cityid =	$('#cityid').val();
		info.districtid =	$('#districtid').val();
		info.contact =	$('#contact').val();
		info.address =	$('#address').val();
		info.telephone =	$('#telephone').val();
		info.fax =	$('#fax').val();
		info.email =	$('#email').val();
		info.postcode =	$('#postcode').val();
		info.website =	$('#website').val();
		info.inwardid =	$('#inwardid').val();
		info.industryid =	$('#industryid').val();
		info.subdustryid =	$('#subdustryid').val();
		info.capital =	$('#capital').val();
		info.computernumber =	$('#computernumber').val();
		info.membernumber =	$('#membernumber').val();
		info.intro =	$('#intro').val();
		info.logo =	info.logo;

		callajax('org!save', info, cb_12_save);
	});
});
function cb_12_save(data) {
	bootbox.alert({'title':'提示','message': '保存成功.', callback: function() {
		$('#container').css('width', document.body.clientWidth + 'px');	
	}});
	complete();
}
function loadmeta() {
	callajax('org!getProvince', '', cb_12_province);
	callajax('org!getInward', '', cb_12_inward);
	callajax('org!getIndustry', '', cb_12_industry);
}
function loaddata() {

	// 权限
	if (has('zzxxglck')) {
		callajax('org!getInfo', '', cb_12_info);
	}
}
function cb_12_info(data) {
	info = data;
	$('#code').val(data.code);
	$('#name').val(data.name);
	$('#shortname').val(data.shortname);
	$('#englishname').val(data.englishname);
	$('#ad').val(data.ad);
	$('#provinceid').val(data.provinceid);
	$('#contact').val(data.contact);
	$('#address').val(data.address);
	$('#telephone').val(data.telephone);
	$('#fax').val(data.fax);
	$('#email').val(data.email);
	$('#postcode').val(data.postcode);
	$('#website').val(data.website);
	$('#inwardid').val(data.inwardid);
	$('#industryid').val(data.industryid);
	$('#capital').val(data.capital);
	$('#membernumber').val(data.membernumber);
	$('#computernumber').val(data.computernumber);
	$('#intro').val(data.intro);
	if (data.logo == '') {
		$('#logo').prop('src', imagedir + 'defaultlogo.png');
	}
	else {
		$('#logo').prop('src', imagedir + data.logo);
	}
	if (data.provinceid > 0) {
		callajax('org!getCity', {provinceid: data.provinceid}, cb_12_city);
	}
	if (data.industryid > 0) {
		callajax('org!getSubdustry', {industryid: data.industryid}, cb_12_subdustry);
	}
	complete();
 }
function cb_12_province(data) {
	load('provinceid', data);
}
function cb_12_city(data) {
	load('cityid', data);
	$('#cityid').val(info.cityid);
	if (info.cityid > 0) {
		callajax('org!getDistrict', {cityid: info.cityid}, cb_12_district);
	}
}
function cb_12_district(data) {
	load('districtid', data);
	$('#districtid').val(info.districtid);
}
function cb_12_inward(data) {
	load('inwardid', data);
}
function cb_12_industry(data) {
	load('industryid', data);
}
function cb_12_subdustry(data) {
	load('subdustryid', data);
	$('#subdustryid').val(info.subdustryid);
}
function load(id, data) {
	$('#' + id).empty();
	var i = data.length;
	while(i--) {
		$('#' + id).append(optiontemplate
				.replace('id', data[i].id)
				.replace('name', data[i].name));
	}
}
function edit() {

	//权限
	if (has('zzxxglxg')) {
		$('#logod').modal({
			backdrop: false,
			remote: '12_logo.jsp'
		});
		$('#filename').val('');
	}
	else {
		bootbox.alert({'title':'提示','message':'您没有权限修改组织LOGO', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');	
		}});
	}
	return false;
}
function del() {

	//权限
	if (has('zzxxglxg')) {
		info.logo = '';
		$('#logo').prop('src', imagedir + 'defaultlogo.png');
	}
	else {
		bootbox.alert({'title':'提示','message':'您没有权限删除组织LOGO', callback: function() {
			$('#container').css('width', document.body.clientWidth + 'px');	
		}});
	}
}
function complete() {
	var complete = 0;
	$.each(info,function(k,v) {
		if (v != '') complete++;
	});
	if (complete == 23) {
		complete = 100;
	}
	else if (complete > 8) {
		complete = 70;
	}
	else {
		complete = 30;
	}
	$('#complete').text(complete + '%');
}