/**
 * Created by zhu_jq on 2017/3/21.
 */
function Paging(className,cfg){
    this.obj = $(className);
    var defaultCfg = {
        pageCount : 10,
        current : 1,
        backFn : function(){},
        pageTotle:5
    }
    this.args = $.extend(defaultCfg,cfg);
    this.init();
}



Paging.prototype.init = function(){
    this.fillHtml(this.obj,this.args);
    this.bindEvent(this.obj,this.args);
    //this.args.backFn();
}
Paging.prototype.fillHtml = function(obj,args){
    obj.empty();

    if(args.pageCount){
        obj.show();
        //上一页
        var prevPage = "<div class='' id='prevPage'><</div>"
        //中间五页
        var start = args.current-2;var end = args.current+2;
        if(args.current<3&&args.pageCount>=5){
            start = 1;end = 5;
        }else if(args.pageCount<5){
            start = 1;end = args.pageCount;
        }else if(args.current>args.pageCount-3){
            start = args.pageCount-4;end = args.pageCount;
        }
        var middlePage = '';
        for(var i = start;i<=end;i++){
            if(i==args.current){
                middlePage+="<div class='pageNum current' id=''>"+i+"</div>";
            }else{
                middlePage+="<div class='pageNum' id=''>"+i+"</div>";
            }
        }

        //下一页
        var nextPage = "<div class='' id='nextPage'>></div>"

        //搜索
        var searchPage = '<input class="" id="pagingJumpNum"/> <button class="pagingJump">跳转</button>'


        var sHTML = prevPage+middlePage+nextPage+searchPage;
        obj.html(sHTML);
    }else{
        obj.hide();
    }

}


Paging.prototype.bindEvent = function(obj,args){
    obj.undelegate("div.pageNum","click");
    var This = this;
    obj.delegate("div.pageNum","click",function(){
        console.log($(this).text());
        args.current = parseInt($(this).text());
        This.fillHtml(obj,{"current":args.current,"pageCount":args.pageCount});
        if(typeof(args.backFn)=="function"){
            args.backFn(args.current);
        }
    });
    //上一页
    obj.off("click","div#prevPage");
    obj.on("click","div#prevPage",function(){
        var currentNum = parseInt(obj.find("div.current").text())-1;
        console.log(args.current);
        if(currentNum<1){
            return
        }else{
            args.current = currentNum;
        }
        args.current = parseInt(obj.children("div.current").text())-1;
        This.fillHtml(obj,{"current":args.current,"pageCount":args.pageCount});
        if(typeof(args.backFn)=="function"){
            args.backFn(args.current);
        }
    });
    //下一页
    obj.off("click","div#nextPage")
    obj.on("click","div#nextPage",function(){
        var currentNum = parseInt(obj.find("div.current").text())+1;
        //console.log(args.current);
        if(currentNum>args.pageCount){
            return
        }else{
            args.current = currentNum;
        }
        console.log(args.current);
        //args.current = parseInt(obj.children("span.current").text())+1;
        This.fillHtml(obj,{"current":args.current,"pageCount":args.pageCount});
        if(typeof(args.backFn)=="function"){
            args.backFn(args.current);
        }
    });
    //跳转
    obj.off("click","button.pagingJump");
    obj.on("click","button.pagingJump",function(){
        var inputNum = $(this).prev().val();
        if(inputNum){//如果输入不为空
            var num = This.checkVal(inputNum,obj,args);
            if(num){
                num = parseInt(num);
                args.current = num;
                args.backFn(num);
                This.fillHtml(obj,args);
                $('input.inputNum').val('');
            }
        }
    })
}

Paging.prototype.checkVal = function(val,obj,args){
    if(val<=1){
        return 1;
    }
    else if(val>args.pageCount){
        return args.pageCount;
    }
    else if(val>1&&val<=args.pageCount){
        return val;
    }
    else{
        new Window().alert({
            title   : '消息',
            content : '请输入正确页码',
            hasCloseBtn : false,
            textForSureBtn : false,              //确定按钮
            textForcancleBtn : false,
            skinClassName:'winSkin_a warning',
            autoHide:true
        });
    }
}


Paging.prototype.setPageCount=function(npageCount,oTable,oPageobj){
    var tableID = oTable.cid+'_page';
    if(npageCount>0){
        $('#'+tableID).show();
        $('#'+tableID,parent.document).show();
        this.args.pageCount=npageCount;
        if(this.args.pageCount<this.args.current||oPageobj) {
            this.args.current = 1;
        }
        this.init();
    }else{
        var pagingID = oTable.cid+'_page';
        this.args.pageCount=npageCount;
        this.args.current=1;
        if($('#'+pagingID,parent.document).length==0){
            $('#'+pagingID).hide();
        }else{
            $('#'+pagingID,parent.document).hide();
        }
    }
}