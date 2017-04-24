
$(function(){
    $('#organizeListOuter').undelegate('.node','click');
    $('#organizeListOuter').delegate('.node','click',function(){
        var className = $(this).attr('class');
        var classArr = className.split(' ');
        deleteElement(classArr,'node');
        var className = classArr[0];
        var idName = classArr[1];
        var targetNode = $('.organizationList').find('ul li.'+className+'[id='+idName+']');

        $('.organizationList').find('li').removeClass('active');
        targetNode.addClass('active');
        if(targetNode.find('.groupCollspan').hasClass('groupCollspanC')){
            targetNode.find('.groupCollspan').click();
        }
        targetNode.click();

    })
})


function seeOrgnizeTree(){
    var data = localStorage.getItem('getBranchTree');
    var getBranchTree = JSON.parse(data);
    console.log(getBranchTree);
    var sHTML = '';
    var SHTML = loopTree(getBranchTree,sHTML,1);
    $('#organizeListOuter').html(SHTML);
    $('.orgNavClick').addClass('chatHide');
    $('.orgNavClick3').removeClass('chatHide');
    $('.BreadcrumbsOuter').removeClass('chatHide');
    //console.log(SHTML);
    $("#orgTree").jOrgChart({
        chartElement : '#organizeListOuter'
        //,
        //dragAndDrop  : true
    });


    console.log($('.jOrgChart table').width());
}

function loopTree(data,sHTML,level){
    if(level == 1){
        sHTML += '<ul id="orgTree" style="display:none">';
    }else{
        sHTML += '<ul>';

    }
    for(var i = 0;i<data.length;i++){
        var datas = data[i];
        var className = datas.flag==1?'member':'department';
        if(className=='department'){
            sHTML +='<li class="'+className+' '+datas.id+'" targetid="'+datas.id+'">'+btnShu(datas.name,8);
        }
        if(datas.hasChild.length!=0){//有子级
            sHTML =loopTree(datas.hasChild,sHTML,0)
        }else{
            sHTML+='</li>';
        }
    }
    sHTML+='</ul>';

    return sHTML;
}


function btnShu(txt1,length)
{
    //var txt1 = document.getElementById('txt1');
    //var txt2 = document.getElementById('txt2');
    //var txtLen = document.getElementById('txtLen');
    //var txtSplit = document.getElementById('txtSplit');
    var str = txt1;

    var txtHeight = length;
    var arrAll = new Array();
    var arr = new Array();
    var len = 0;
    for(var i=0; i<str.length; i++)
    {
        if(str.charAt(i) == '\n')
        {
            continue;
        }
        if(str.charAt(i) == '\r')
        {
            len = 0;
            arrAll.push(arr);
            arr = new Array();
            continue;
        }
        arr.push(str.charAt(i));
        len++;
        if(len == txtHeight || (i == str.length - 1))
        {
            len = 0;
            arrAll.push(arr);
            arr = new Array();
        }
    }

    var strOut = '';
    var j = 0;
    while(true)
    {
        for(var i=0; i<arrAll.length; i++)
        {
            if(j < arrAll[i].length)
            {
                var chr = arrAll[i][j];
                //（）［］〈〉《》‹›〕〔{}﹜﹛『』」「〖〗】【︵︶︷︸︿﹀︹︺︽︾_ˉ﹁﹂﹃﹄︻︼
                chr = rchar(chr, '（(', '︵');
                chr = rchar(chr, '）)', '︶');
                chr = rchar(chr, '{', '︷');
                chr = rchar(chr, '}', '︸');
                chr = rchar(chr, '［〖【[', '︻');
                chr = rchar(chr, '］〗】]', '︼');
                chr = rchar(chr, '《〈', '︽');
                chr = rchar(chr, '》〉', '︾');
                strOut += chr;
                if(chr.charCodeAt(0) < 255 || chr == '“')
                {
                    strOut += ' ';
                }
            }
            else
            {
                strOut += '  ';
            }
            strOut += ' ' +  ''+ ' ';
        }
        strOut += '<br />';

        j++;
        var bo = true;
        for(var i=0; i<arrAll.length; i++)
        {
            if(j < arrAll[i].length)
            {
                bo = false;
                break;
            }
        }
        if(bo)break;
    }
    return strOut
    //txt2.value = strOut;
}

function rchar(chr, oldChr, newChr)
{
    if(oldChr.indexOf(chr) != -1)
    {
        return newChr;
    }

    return chr;
}
