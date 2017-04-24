
var aW = 800;
var aH = 800;
var oneW = 50;
var oneH = 100;
var marH = 40;
var marW = 40;
$(function(){
    var canvas = document.getElementById('bgCanvas');
    var ctx = canvas.getContext('2d');
    ctx.lineWidth = '1px'
    console.log(ctx)
    seeOrgnizeTree(ctx);
})

function seeOrgnizeTree(data,ctx){
    //var data =  [
    //    {
    //        kDepartName  : "董事长",
    //        kChildDepart :
    //
    //            [   {
    //                kDepartName  : "总经理000",
    //                kChildDepart : [
    //                    {
    //                        kDepartName  : "部门_02-01",
    //                        kChildDepart : [
    //                            {
    //                                kDepartName  : "部门_02-01",
    //                                kChildDepart : []
    //                            },
    //
    //                            {
    //                                kDepartName: "部门_02-02",
    //                                kChildDepart : []
    //                            }
    //                        ]
    //                    },
    //
    //                    {
    //                        kDepartName: "部门_02-02",
    //                        kChildDepart : [
    //                            {
    //                                kDepartName  : "部门_02-01",
    //                                kChildDepart : []
    //                            }
    //                        ]
    //                    },
    //                    {
    //                        kDepartName  : "部门_02-01",
    //                        kChildDepart : [
    //                            {
    //                                kDepartName: "部门_02-02",
    //                                kChildDepart : []
    //                            },
    //                            {
    //                                kDepartName  : "部门_02-01",
    //                                kChildDepart : []
    //                            }
    //                        ]
    //                    },
    //
    //                    {
    //                        kDepartName: "部门_02-02",
    //                        kChildDepart : []
    //                    },
    //                    {
    //                        kDepartName  : "部门_02-01",
    //                        kChildDepart : []
    //                    },
    //
    //                    {
    //                        kDepartName  : "部门_02-01",
    //                        kChildDepart : []
    //                    }
    //                ]
    //            },
    //                {
    //                    kDepartName  : "总经理000",
    //                    kChildDepart : [
    //                        {
    //                            kDepartName  : "部门_02-01",
    //                            kChildDepart : []
    //                        },
    //
    //                        {
    //                            kDepartName: "部门_02-02",
    //                            kChildDepart : [
    //                                {
    //                                    kDepartName: "部门_02-02",
    //                                    kChildDepart : []
    //                                },
    //                                {
    //                                    kDepartName: "部门_02-02",
    //                                    kChildDepart : []
    //                                },
    //                                {
    //                                    kDepartName: "部门_02-02",
    //                                    kChildDepart : [
    //                                        {
    //                                            kDepartName: "部门_02-02",
    //                                            kChildDepart : []
    //                                        },
    //                                        {
    //                                            kDepartName: "部门_02-02",
    //                                            kChildDepart : []
    //                                        }
    //                                    ]
    //                                },
    //                                {
    //                                    kDepartName: "部门_02-02",
    //                                    kChildDepart : []
    //                                }
    //                            ]
    //                        },
    //                        {
    //                            kDepartName: "部门_02-02",
    //                            kChildDepart : []
    //                        }
    //                    ]
    //                }
    //
    //            ]
    //    }
    //];
    var outerWidth = $('#organizeList').width();
    console.log('2',ctx);
    showOrganizeList(data,ctx);

}

function showOrganizeList(data,ctx){
    var sHTML = '';
    var i = '';
    var HTML = loop(data,sHTML,i);
    $('.bbb').append(HTML);
    var noChild = $('.NoChild');
    var levelarr = calaPosition(noChild,ctx);
    //console.log(levelarr)
    parentLevelLoop(levelarr,ctx);
}


function parentLevelLoop(levelarr,ctx){
    for(var i = 0;i<levelarr.length;i++){
        levelarr[i].pop();
    }
    levelarr = unique(levelarr);
    console.log(levelarr);
    for(var i = 0;i<levelarr.length;i++){
        if(levelarr[i].length>=0){
            calcMiddlePos(levelarr[i],ctx);

        }else{

            calcMiddlePos(levelarr[i],ctx);
            levelarr.splice(i,1);
        }

    }
    parentLevelLoop(levelarr,ctx);
}

function calcMiddlePos(arr,ctx){
    var deep = arr.length;
    var top = (deep-1)*(oneH+marH);

    //if(arr.length!=1){
    var level = arr.join('-');
    var curNode = $('[level='+level+']')
    var childCount = curNode.attr('childcount')-1;

    //Math.floor(childCount/2);
    var lastNum = Math.floor(childCount/2)
    console.log(lastNum)
    var firstChild = level+'-'+0;
    var middleChild = level+'-'+lastNum.toString();
    var lastChild = level+'-'+childCount.toString();
    console.log(firstChild,lastChild)
    //var left = $('[level='+middleChild+']').css('left');
    var leftPX = parseInt($('[level='+firstChild+']').css('left'))
    var rightPX = parseInt($('[level='+lastChild+']').css('left'))
    ctx.beginPath();
    ctx.moveTo(leftPX+oneW/2,top+2*oneH/2+marH/2);
    ctx.lineTo(rightPX+oneW/2,top+2*oneH/2+marH/2);
    ctx.stroke();
    var left = leftPX+(rightPX-leftPX)/2;
    ctx.beginPath();
    ctx.moveTo(left+oneW/2,top);
    ctx.lineTo(left+oneW/2,top-marH/2);
    ctx.stroke();
    ctx.beginPath();
    ctx.moveTo(left+oneW/2,top+oneH);
    ctx.lineTo(left+oneW/2,top+oneH+marH/2);
    ctx.stroke();
    console.log(left);
    curNode.css({left:left+'px',top:top+'px'});
    //}
}


function unique(arr){
    var res = [];
    var json = {};
    for(var i = 0; i < arr.length; i++){
        if(!json[arr[i]]){
            res.push(arr[i]);
            json[arr[i]] = 1;
        }
    }
    return res;
}

function calaPosition(noChild,ctx){
    var levelarr = [];
    for(var i = 0;i<noChild.length;i++){
        var $noChild = $(noChild[i]);
        var noChildLevel = $noChild.attr('level');
        var level = calcLevel(noChildLevel);
        levelarr.push(level);
        var deep = level.length;
        var left = i*(oneW+marW);
        var top = (deep-1)*(oneH+marH);
        ctx.beginPath();
        ctx.moveTo(left+oneW/2,top);
        ctx.lineTo(left+oneW/2,top-marH/2);
        ctx.stroke();
        $noChild.css({left:left+'px',top:top+'px'});
    }
    //console.log(levelarr);
    return levelarr;
}


function hasItem(parentLevelarr,parentLevel){
    var bhas = 0;
    for(i = 0;i<parentLevelarr.length;i++){
        if(parentLevel==parentLevelarr[i]){
            bhas = 1;
        }
    }
    return bhas;
}
function calcLevel(str){
    var arr = str.split('-');
    //console.log(arr)
    return arr;
}


function loop(aData,sHTML,level){
    var k = aData.length;
    var outerCName = '';
    var innerCName = '';
    //sHTML += '<div class="level'+level+'">'+sHTML+'</div>';
    for(var i = 0;i<aData.length;i++){
        var oData = aData[i];
        var hasChild = oData.kChildDepart.length==0?false:true;
        if(hasChild){
            var childCount = oData.kChildDepart.length;
        }
        var levelNum = level+i
        if(i == 0&&hasChild){

            var outer = leftHasChild(outerCName,levelNum,childCount)

        }else if(i == aData.length-1&&hasChild){

            var outer = rightHasChild(outerCName,levelNum,childCount)

        }else if(i == 0&&!hasChild){

            var outer = leftNoChild(outerCName,levelNum)

        }else if(i == aData.length-1&&!hasChild){

            var outer = rightNoChild(outerCName,levelNum);

        }else{
            if(oData.kChildDepart.length!=0){
                var outer = middleHasChild(outerCName,levelNum,childCount)
            }else{
                var outer = middleNoChild(outerCName,levelNum)
            }
        }

        sHTML += outer+'<p class="'+innerCName+'">'+oData.kDepartName+'</p></div>'

        if(hasChild){
            sHTML = loop(oData.kChildDepart,sHTML,levelNum+"-");
        }
    }

    return sHTML;

}

function leftNoChild(outerCName,level){
    var sHTML = '<div class="NoChild leftNoChild '+outerCName+'"level="'+level+'">';
    return sHTML
}
function rightNoChild(outerCName,level){
    var sHTML = '<div class="NoChild rightNoChild ' +outerCName+'"level="'+level+'">';
    return sHTML
}
function rightHasChild(outerCName,level,childCount){
    var sHTML = '<div class="rightHasChild '+outerCName+'"level="'+level+'" childCount="'+childCount+'">';
    return sHTML
}
function leftHasChild(outerCName,level,childCount){
    var sHTML = '<div class="leftHasChild '+outerCName+'"level="'+level+'"childCount="'+childCount+'">';
    return sHTML
}
function middleNoChild(outerCName,level){
    var sHTML = '<div class="NoChild middleNoChild '+outerCName+'"level="'+level+'">';
    return sHTML
}
function middleHasChild(outerCName,level,childCount){
    var sHTML = '<div class="middleHasChild '+outerCName+'"level="'+level+'"childCount="'+childCount+'">';
    return sHTML
}
