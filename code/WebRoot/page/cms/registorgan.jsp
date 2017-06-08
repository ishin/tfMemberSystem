<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/html">
	<head lang="en">
		<meta charset="UTF-8">
		<title>注册公司</title>
		<style type="text/css">
			#reg {
				padding: 20px;
				margin-top: 30px;
				margin-left: 30%;
				width: 350px;
				border: 1px outset black;
			}
			table {
				width: 350px;
				border: 0px;
				font-size: 14px;
				border-collapse: separate;   
				border-spacing: 10px;
			}
			table tr {
				width: 200px;
			}
			table select {
				width: 174px;
			}
			
			#button {
				background-color: #555555; 
				border: none;
				color: white;
				padding: 15px 42px;
				text-align: center;
				text-decoration: none;
				display: inline-block;
				font-size: 16px;
				margin: 4px 2px;
				cursor: pointer;
			}
		</style>
		<script src="<%=request.getContextPath()%>/page/cms/js/jquery-2.1.1.min.js"></script>
		<script src="<%=request.getContextPath()%>/page/cms/js/md5.js"></script>
	</head>
	<body>
		<div id="reg">
			<table>
				<form method="post" action="mulorgan!registOrgan" id="myform">
					<tr>
						<td>名称</td>
						<td><input type="text" name="name" id="name" value="" /></td>
						<td><span style="color:red;">*<span></td>
					</tr>
					<tr>
						<td>简称</td>
						<td><input type="text" name="shortname" id="shortname" value="" /></td>
						<td><span style="color:red;">*<span></td>
					</tr>
					<tr>
						<td>英文名称</td>
						<td><input type="text" name="englishname" id="englishname" value="" /></td>
						<td><span style="color:red;">*<span></td>
					</tr>
					<tr>
						<td>广告语</td>
						<td><input type="text" name="ad" id="ad" value="" /></td>
						<td></td>
					</tr>
					<tr>
						<td>省份</td>
						<td>
							<select name="provinceid" id="provinceid" onchange="selectCity()">
								<option selected>
									请选择
								</option>
							</select>
						</td>
						<td><span style="color:red;">*<span></td>
					</tr>
					<tr>
						<td>市</td>
						<td>
							<select name="cityid" id="cityid" onchange="selectDistric()"></select>
						</td>
						<td><span style="color:red;">*<span></td>
					</tr>
					<tr>
						<td>地区</td>
						<td>
							<select name="districtid" id="districtid"></select>
						</td>
						<td><span style="color:red;">*<span></td>
					</tr>
					<tr>
						<td>联系人</td>
						<td>
							<input type="text" name="contact" id="contact" value="" />
						</td>
						<td><span style="color:red;">*<span></td>
					</tr>
					<tr>
						<td>地址</td>
						<td>
							<input type="text" name="address" id="address" value="" />
						</td>
						<td><span style="color:red;">*<span></td>
					</tr>
					<tr>
						<td>电话</td>
						<td>
							<input type="text" name="telephone" id="telephone" value="" />
						</td>
						<td></td>
					</tr>
					<tr>
						<td>传真</td>
						<td>
							<input type="text" name="fax" id="fax" value="" />
						</td>
						<td></td>
					</tr>
					<tr>
						<td>Eamil</td>
						<td>
							<input type="text" name="email" id="email" value="" />
						</td>
						<td></td>
					</tr>
					<tr>
						<td>邮编</td>
						<td>
							<input type="text" name="postcode" id="postcode" value="" />
						</td>
						<td></td>
					</tr>
					<tr>
						<td>网址</td>
						<td>
							<input type="text" name="website" id="website" value="" />
						</td>
						<td></td>
					</tr>
					<tr>
						<td>企业性质</td>
						<td>
							<select name="inwardid" id="inwardid"></select>
						</td>
						<td><span style="color:red;">*<span></td>
					</tr>
					<tr>
						<td>主营行业</td>
						<td>
							<select name="industryid" id="industryid"
								onchange="selectSubtrid()">
								<option selected>
									请选择
								</option>
							</select>
						</td>
						<td><span style="color:red;">*<span></td>
					</tr>
					<tr>
						<td>子行业</td>
						<td>
							<select name="subdustryid" id="subdustryid"></select>
						</td>
						<td><span style="color:red;">*<span></td>
					</tr>
					<tr>
						<td>注册资金</td>
						<td>
							<input type="text" name="capital" id="capital" value="500000" />
						</td>
						<td></td>
					</tr>
					<tr>
						<td>成员个数</td>
						<td>
							<input type="text" name="membernumber" id="membernumber" value="100" />
						</td>
						<td></td>
					</tr>
					<tr>
						<td>计算机台数</td>
						<td><input type="text" name="computernumber" id="computernumber" value="300" /></td>
						<td></td>
					</tr>
					<tr>
						<td>介绍</td>
						<td><input type="text" name="intro" id="intro" value="" /></td>
						<td></td>
					</tr>
					<tr>
						<td>logo</td>
						<td><input type="text" name="logo" id="logo" value="" /></td>
						<td></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td><input type="button" value="submit" onclick=sub(); id="button" /></td>
						<td>&nbsp;</td>
					</tr>
					<input type="hidden" name="sign" id="sign" />
					<input type="hidden" name="timestamp" id="timestamp" />
				</form>
			</table>
		</div>
		<script type="text/javascript"><!--
			(function(){
				sendAjax('org!getProvince',{},function(data){
					var select = document.getElementById("provinceid");
					var obj = JSON.parse(data);
					appendSelect(select, obj);
				});
				sendAjax('org!getIndustry',{},function(data){
					var select = document.getElementById("industryid");
					var obj = JSON.parse(data);
					appendSelect(select, obj);
				});
				sendAjax('org!getInward',{},function(data){
					var select = document.getElementById("inwardid");
					var obj = JSON.parse(data);
					appendSelect(select, obj);
				});
			})()
			
			function selectCity() {
				delOption("cityid");
				var provinceid = document.getElementById("provinceid").value;
				sendAjax('org!getCity',{"provinceid": provinceid},function(data){
					var select = document.getElementById("cityid");
					var obj = JSON.parse(data);
					appendSelect(select, obj);
					var cityId = document.getElementById("cityid").options[0].value;
					selectDistric(cityId);
				});
			}
			
			function selectDistric(cityid) {
				delOption("districtid");
				var cityid = cityid || document.getElementById("cityid").value;
				sendAjax('org!getDistrict',{"cityid": cityid},function(data){
					var select = document.getElementById("districtid");
					var obj = JSON.parse(data);
					appendSelect(select, obj);
				});
			}
			
			function selectSubtrid() {
				delOption("subdustryid");
				var industryid = document.getElementById("industryid").value;
				sendAjax('org!getSubdustry',{"industryid": industryid},function(data){
					var select = document.getElementById("subdustryid");
					var obj = JSON.parse(data);
					appendSelect(select, obj);
				});
			}
			
			function delOption(name) {
				var selection = document.getElementById(name);
				for (var i = 0; i < selection.options.length; i++) {
				     selection.removeChild(selection.options[i]);
				};
			}
			
			function appendSelect(select, obj) {
				for(var i = 0; i < obj.length; i++) {
					var op=document.createElement("option"); 
					var t = obj[i];
					op.setAttribute("value", t .id);        
					op.appendChild(document.createTextNode(t.name));
					select.appendChild(op);          
				}
			}
		
			function sub() {
				if (judgeNull()) {
					subForm();
				}
			}
		
			function judgeNull() {
				var name = document.getElementById("name").value;
				var shortname = document.getElementById("shortname").value;
				var englishname = document.getElementById("englishname").value;
				var provinceid = document.getElementById("provinceid").value;
				var cityid = document.getElementById("cityid").value;
				var districtid = document.getElementById("districtid").value;
				var contact = document.getElementById("contact").value;
				var address = document.getElementById("address").value;
				var inwardid = document.getElementById("inwardid").value;
				var industryid = document.getElementById("industryid").value;
				var subdustryid = document.getElementById("subdustryid").value;
				
				if (!isNull(name)) {
					$("#name").css({"border":"1px sold, red"});
				}
				return isNull(name) && isNull(shortname) && isNull(englishname)
					&& isNull(provinceid) || isNull(cityid) && isNull(districtid)
					&& isNull(contact) && isNull(address) && isNull(inwardid) 
					&& isNull(industryid) && isNull(subdustryid); 
			}
			
			function isNull(str) {
				return !!str 
			}
			
			function subForm() {
				var timestamp = Math.floor(new Date().getTime() / 1000);
				var name = "name=" + document.getElementById("name").value;
				var shortname = "shortname=" + document.getElementById("shortname").value;
				var englishname = "englishname=" + document.getElementById("englishname").value;
				var ad = "ad=" + document.getElementById("ad").value;
				var provinceid = "provinceid=" + document.getElementById("provinceid").value;
				var cityid = "cityid=" + document.getElementById("cityid").value;
				var districtid = "districtid=" + document.getElementById("districtid").value;
				var contact = "contact=" + document.getElementById("contact").value;
				var address = "address=" + document.getElementById("address").value;
				var telephone = "telephone=" + document.getElementById("telephone").value;
				var fax = "fax=" + document.getElementById("fax").value;
				var email = "email=" + document.getElementById("email").value;
				var postcode = "postcode=" + document.getElementById("postcode").value;
				var website = "website=" + document.getElementById("website").value;
				var inwardid = "inwardid=" + document.getElementById("inwardid").value;
				var industryid = "industryid=" + document.getElementById("industryid").value;
				var subdustryid = "subdustryid=" + document.getElementById("subdustryid").value;
				var capital = "capital=" + document.getElementById("capital").value || 0;
				var membernumber = "membernumber=" + document.getElementById("membernumber").value || 0;
				var computernumber = "computernumber=" + document.getElementById("computernumber").value || 0;
				var intro = "intro=" + document.getElementById("intro").value;
				var logo = "logo=" + document.getElementById("logo").value;
		
				var str = name + shortname + englishname + ad + provinceid + cityid
						+ districtid + contact + address + telephone + fax + email
						+ postcode + website + inwardid + industryid + subdustryid
						+ capital + membernumber + computernumber + intro + logo;
		
				var afterSort = sort(str);
				console.log(afterSort);
				var key = "1234";
				console.log(key + afterSort + timestamp);
				var sign = md5(key + afterSort + timestamp);
				console.log(sign.toLowerCase());
				document.getElementById("timestamp").value = timestamp;
				document.getElementById("sign").value = sign.toLowerCase();
				document.getElementById("myform").submit();
			}
		
			function sort(str) {
				var arr = str.split("");
				return arr.sort().join("");
			}
			
			function sendAjax(url,data,callback){
			    $.ajax({
			        type: "GET",
			        url: url,
			        data:data,
			        success: function(data){
			            callback && callback(data);
			        }
			    })
			}
		
			function md5(string) {
			    var x = Array();
			    var k, AA, BB, CC, DD, a, b, c, d;
			    var S11 = 7, S12 = 12, S13 = 17, S14 = 22;
			    var S21 = 5, S22 = 9, S23 = 14, S24 = 20;
			    var S31 = 4, S32 = 11, S33 = 16, S34 = 23;
			    var S41 = 6, S42 = 10, S43 = 15, S44 = 21;
			    string = Utf8Encode(string);
			    x = ConvertToWordArray(string);
			    a = 0x67452301;
			    b = 0xEFCDAB89;
			    c = 0x98BADCFE;
			    d = 0x10325476;
			    for (k = 0; k < x.length; k += 16) {
			        AA = a;
			        BB = b;
			        CC = c;
			        DD = d;
			        a = FF(a, b, c, d, x[k + 0], S11, 0xD76AA478);
			        d = FF(d, a, b, c, x[k + 1], S12, 0xE8C7B756);
			        c = FF(c, d, a, b, x[k + 2], S13, 0x242070DB);
			        b = FF(b, c, d, a, x[k + 3], S14, 0xC1BDCEEE);
			        a = FF(a, b, c, d, x[k + 4], S11, 0xF57C0FAF);
			        d = FF(d, a, b, c, x[k + 5], S12, 0x4787C62A);
			        c = FF(c, d, a, b, x[k + 6], S13, 0xA8304613);
			        b = FF(b, c, d, a, x[k + 7], S14, 0xFD469501);
			        a = FF(a, b, c, d, x[k + 8], S11, 0x698098D8);
			        d = FF(d, a, b, c, x[k + 9], S12, 0x8B44F7AF);
			        c = FF(c, d, a, b, x[k + 10], S13, 0xFFFF5BB1);
			        b = FF(b, c, d, a, x[k + 11], S14, 0x895CD7BE);
			        a = FF(a, b, c, d, x[k + 12], S11, 0x6B901122);
			        d = FF(d, a, b, c, x[k + 13], S12, 0xFD987193);
			        c = FF(c, d, a, b, x[k + 14], S13, 0xA679438E);
			        b = FF(b, c, d, a, x[k + 15], S14, 0x49B40821);
			        a = GG(a, b, c, d, x[k + 1], S21, 0xF61E2562);
			        d = GG(d, a, b, c, x[k + 6], S22, 0xC040B340);
			        c = GG(c, d, a, b, x[k + 11], S23, 0x265E5A51);
			        b = GG(b, c, d, a, x[k + 0], S24, 0xE9B6C7AA);
			        a = GG(a, b, c, d, x[k + 5], S21, 0xD62F105D);
			        d = GG(d, a, b, c, x[k + 10], S22, 0x2441453);
			        c = GG(c, d, a, b, x[k + 15], S23, 0xD8A1E681);
			        b = GG(b, c, d, a, x[k + 4], S24, 0xE7D3FBC8);
			        a = GG(a, b, c, d, x[k + 9], S21, 0x21E1CDE6);
			        d = GG(d, a, b, c, x[k + 14], S22, 0xC33707D6);
			        c = GG(c, d, a, b, x[k + 3], S23, 0xF4D50D87);
			        b = GG(b, c, d, a, x[k + 8], S24, 0x455A14ED);
			        a = GG(a, b, c, d, x[k + 13], S21, 0xA9E3E905);
			        d = GG(d, a, b, c, x[k + 2], S22, 0xFCEFA3F8);
			        c = GG(c, d, a, b, x[k + 7], S23, 0x676F02D9);
			        b = GG(b, c, d, a, x[k + 12], S24, 0x8D2A4C8A);
			        a = HH(a, b, c, d, x[k + 5], S31, 0xFFFA3942);
			        d = HH(d, a, b, c, x[k + 8], S32, 0x8771F681);
			        c = HH(c, d, a, b, x[k + 11], S33, 0x6D9D6122);
			        b = HH(b, c, d, a, x[k + 14], S34, 0xFDE5380C);
			        a = HH(a, b, c, d, x[k + 1], S31, 0xA4BEEA44);
			        d = HH(d, a, b, c, x[k + 4], S32, 0x4BDECFA9);
			        c = HH(c, d, a, b, x[k + 7], S33, 0xF6BB4B60);
			        b = HH(b, c, d, a, x[k + 10], S34, 0xBEBFBC70);
			        a = HH(a, b, c, d, x[k + 13], S31, 0x289B7EC6);
			        d = HH(d, a, b, c, x[k + 0], S32, 0xEAA127FA);
			        c = HH(c, d, a, b, x[k + 3], S33, 0xD4EF3085);
			        b = HH(b, c, d, a, x[k + 6], S34, 0x4881D05);
			        a = HH(a, b, c, d, x[k + 9], S31, 0xD9D4D039);
			        d = HH(d, a, b, c, x[k + 12], S32, 0xE6DB99E5);
			        c = HH(c, d, a, b, x[k + 15], S33, 0x1FA27CF8);
			        b = HH(b, c, d, a, x[k + 2], S34, 0xC4AC5665);
			        a = II(a, b, c, d, x[k + 0], S41, 0xF4292244);
			        d = II(d, a, b, c, x[k + 7], S42, 0x432AFF97);
			        c = II(c, d, a, b, x[k + 14], S43, 0xAB9423A7);
			        b = II(b, c, d, a, x[k + 5], S44, 0xFC93A039);
			        a = II(a, b, c, d, x[k + 12], S41, 0x655B59C3);
			        d = II(d, a, b, c, x[k + 3], S42, 0x8F0CCC92);
			        c = II(c, d, a, b, x[k + 10], S43, 0xFFEFF47D);
			        b = II(b, c, d, a, x[k + 1], S44, 0x85845DD1);
			        a = II(a, b, c, d, x[k + 8], S41, 0x6FA87E4F);
			        d = II(d, a, b, c, x[k + 15], S42, 0xFE2CE6E0);
			        c = II(c, d, a, b, x[k + 6], S43, 0xA3014314);
			        b = II(b, c, d, a, x[k + 13], S44, 0x4E0811A1);
			        a = II(a, b, c, d, x[k + 4], S41, 0xF7537E82);
			        d = II(d, a, b, c, x[k + 11], S42, 0xBD3AF235);
			        c = II(c, d, a, b, x[k + 2], S43, 0x2AD7D2BB);
			        b = II(b, c, d, a, x[k + 9], S44, 0xEB86D391);
			        a = AddUnsigned(a, AA);
			        b = AddUnsigned(b, BB);
			        c = AddUnsigned(c, CC);
			        d = AddUnsigned(d, DD);
			    }
			    var temp = WordToHex(a) + WordToHex(b) + WordToHex(c) + WordToHex(d);
			    return temp.toUpperCase();
			}
			function RotateLeft(lValue, iShiftBits) {
			    return (lValue << iShiftBits) | (lValue >>> (32 - iShiftBits));
			}
			function AddUnsigned(lX, lY) {
			    var lX4, lY4, lX8, lY8, lResult;
			    lX8 = (lX & 0x80000000);
			    lY8 = (lY & 0x80000000);
			    lX4 = (lX & 0x40000000);
			    lY4 = (lY & 0x40000000);
			    lResult = (lX & 0x3FFFFFFF) + (lY & 0x3FFFFFFF);
			    if (lX4 & lY4) {
			        return (lResult ^ 0x80000000 ^ lX8 ^ lY8);
			    }
			    if (lX4 | lY4) {
			        if (lResult & 0x40000000) {
			            return (lResult ^ 0xC0000000 ^ lX8 ^ lY8);
			        } else {
			            return (lResult ^ 0x40000000 ^ lX8 ^ lY8);
			        }
			    } else {
			        return (lResult ^ lX8 ^ lY8);
			    }
			}
			function F(x, y, z) {
			    return (x & y) | ((~x) & z);
			}
			function G(x, y, z) {
			    return (x & z) | (y & (~z));
			}
			function H(x, y, z) {
			    return (x ^ y ^ z);
			}
			function I(x, y, z) {
			    return (y ^ (x | (~z)));
			}
			function FF(a, b, c, d, x, s, ac) {
			    a = AddUnsigned(a, AddUnsigned(AddUnsigned(F(b, c, d), x), ac));
			    return AddUnsigned(RotateLeft(a, s), b);
			}
			function GG(a, b, c, d, x, s, ac) {
			    a = AddUnsigned(a, AddUnsigned(AddUnsigned(G(b, c, d), x), ac));
			    return AddUnsigned(RotateLeft(a, s), b);
			}
			function HH(a, b, c, d, x, s, ac) {
			    a = AddUnsigned(a, AddUnsigned(AddUnsigned(H(b, c, d), x), ac));
			    return AddUnsigned(RotateLeft(a, s), b);
			}
			function II(a, b, c, d, x, s, ac) {
			    a = AddUnsigned(a, AddUnsigned(AddUnsigned(I(b, c, d), x), ac));
			    return AddUnsigned(RotateLeft(a, s), b);
			}
			function ConvertToWordArray(string) {
			    var lWordCount;
			    var lMessageLength = string.length;
			    var lNumberOfWords_temp1 = lMessageLength + 8;
			    var lNumberOfWords_temp2 = (lNumberOfWords_temp1 - (lNumberOfWords_temp1 % 64)) / 64;
			    var lNumberOfWords = (lNumberOfWords_temp2 + 1) * 16;
			    var lWordArray = Array(lNumberOfWords - 1);
			    var lBytePosition = 0;
			    var lByteCount = 0;
			    while (lByteCount < lMessageLength) {
			        lWordCount = (lByteCount - (lByteCount % 4)) / 4;
			        lBytePosition = (lByteCount % 4) * 8;
			        lWordArray[lWordCount] = (lWordArray[lWordCount] | (string.charCodeAt(lByteCount) << lBytePosition));
			        lByteCount++;
			    }
			    lWordCount = (lByteCount - (lByteCount % 4)) / 4;
			    lBytePosition = (lByteCount % 4) * 8;
			    lWordArray[lWordCount] = lWordArray[lWordCount] | (0x80 << lBytePosition);
			    lWordArray[lNumberOfWords - 2] = lMessageLength << 3;
			    lWordArray[lNumberOfWords - 1] = lMessageLength >>> 29;
			    return lWordArray;
			}
			function WordToHex(lValue) {
			    var WordToHexValue = "", WordToHexValue_temp = "", lByte, lCount;
			    for (lCount = 0; lCount <= 3; lCount++) {
			        lByte = (lValue >>> (lCount * 8)) & 255;
			        WordToHexValue_temp = "0" + lByte.toString(16);
			        WordToHexValue = WordToHexValue + WordToHexValue_temp.substr(WordToHexValue_temp.length - 2, 2);
			    }
			    return WordToHexValue;
			}
			function Utf8Encode(string) {
			    var utftext = "";
			    for (var n = 0; n < string.length; n++) {
			        var c = string.charCodeAt(n);
			        if (c < 128) {
			            utftext += String.fromCharCode(c);
			        } else if ((c > 127) && (c < 2048)) {
			            utftext += String.fromCharCode((c >> 6) | 192);
			            utftext += String.fromCharCode((c & 63) | 128);
			        } else {
			            utftext += String.fromCharCode((c >> 12) | 224);
			            utftext += String.fromCharCode(((c >> 6) & 63) | 128);
			            utftext += String.fromCharCode((c & 63) | 128);
			        }
			    }
			    return utftext;
			}
		</script>
	</body>
</html>