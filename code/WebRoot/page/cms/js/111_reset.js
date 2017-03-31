$(document).ready(function() {
	
	$('#reset').validVal();

	$('#newpassword').keyup(function(e) {
		
		var np = $('#newpassword').val();
		var grade = passwordGrade(np);
		clear();
		if (grade < 11) {
			$('#grade1').addClass('weak');
			$('#grade2').addClass('grade0');
			$('#grade3').addClass('grade0');
		}
		else if (grade < 21) {
			$('#grade1').addClass('good');
			$('#grade2').addClass('good');
			$('#grade3').addClass('grade0');
		}
		else if (grade < 41) {
			$('#grade1').addClass('strong');
			$('#grade2').addClass('strong');
			$('#grade3').addClass('strong');
		}
		else {
			$('#grade1').addClass('grade0');
			$('#grade2').addClass('grade0');
			$('#grade3').addClass('grade0');
		}
	});
	$('#save111reset').click(function() {
		if ($( "#reset" ).triggerHandler( "submitForm" ) == false) return;

		callajax('branch!reset', {memberid: curmember, newpassword: $('#newpassword').val()}, cb_111_reset);
	});
})
function cb_111_reset(date) {
	$('#reset').modal('hide');
	bootbox.alert({'title':'提示', 'message':'新密码已经通过短信通知对方.', callback: function() {
		$('#container').css('width', document.body.clientWidth + 'px');	
	}});
	
}
function clear() {
	$('#grade1').removeClass('grade0');
	$('#grade1').removeClass('weak');
	$('#grade1').removeClass('good');
	$('#grade1').removeClass('strong');
	$('#grade2').removeClass('grade0');
	$('#grade2').removeClass('weak');
	$('#grade2').removeClass('good');
	$('#grade2').removeClass('strong');
	$('#grade3').removeClass('grade0');
	$('#grade3').removeClass('weak');
	$('#grade3').removeClass('good');
	$('#grade3').removeClass('strong');
}
/*
0~10分：不合格（弱）
11~20分：一般
21~30分：中
31~40分：强
41~50分：安全
*/
function passwordGrade(pwd) {
    var score = 0;
    var regexArr = ['[0-9]', '[a-z]', '[A-Z]', '[\\W_]'];
    var repeatCount = 0;
    var prevChar = '';

    //check length
    var len = pwd.length;
    score += len > 18 ? 18 : len;

    //check type
    for (var i = 0, num = regexArr.length; i < num; i++) { if (eval('/' + regexArr[i] + '/').test(pwd)) score += 4; }

    //bonus point
    for (var i = 0, num = regexArr.length; i < num; i++) {
        if (pwd.match(eval('/' + regexArr[i] + '/g')) && pwd.match(eval('/' + regexArr[i] + '/g')).length >= 2) score += 2;
        if (pwd.match(eval('/' + regexArr[i] + '/g')) && pwd.match(eval('/' + regexArr[i] + '/g')).length >= 5) score += 2;
    }

    //deduction
    for (var i = 0, num = pwd.length; i < num; i++) {
        if (pwd.charAt(i) == prevChar) repeatCount++;
        else prevChar = pwd.charAt(i);
    }
    score -= repeatCount * 1;

    return score;

}
