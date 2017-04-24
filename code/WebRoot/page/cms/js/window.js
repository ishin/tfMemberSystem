/**
 * Created by zhu_jq on 2016/5/30.
 */
/**
 * Created by zhu_jq on 2016/5/26.
 */

function Window(){
    //config是个字典格式，这里（构造函数中）用于设置默认值
    this.config = {
        title : '系统消息',     //title
        width : 490,          //宽
        height: 270,          //高
        content: '',          //内容
        x:'50%',              //定位
        y:'50%',              //定位
        margin:'-135px 0px 0px -245px',     //定位
        hasCloseBtn : false,                //关闭按钮X
        hasMask : true,                     //遮罩
        hasImg : true,                      //图片
        isDraggable: true,
        dragHandle : null,
        textForSureBtn : '确定',              //确定按钮
        textForcancleBtn : '取消',            //取消按钮
        skinClassName : null,
        handlerForCancle : null,
        handlerForClose : null,
        handlerForSure : null,
        autoHide:false,
        //dragHandle:'window_header'
    };
};

Window.prototype = {
    //第三个参数，配置也是字典格式
    //将宽高、left:top位置作为第三个参数传递给alert方法
    alert  : function(config){
        var hasParent;
        //将两个字典进行比较，如果有同名的，后面的键名将替代同名的前面的
        var config = $.extend(this.config,config);
        //console.log(config);
        if(window.parent==window){
            hasParent = false;
        }else{
            hasParent = true;
        }

        var mask = null;
        $('.window_mask').empty().remove();
        if(config.hasMask){
            mask = $('<div class="window_mask" /*ondragover="allowDorp(event)" ondrop="drop(event)"*/></div>');
            hasParent?mask.prependTo($("body", parent.document)):mask.prependTo($("body"));
        }

        var hasInput = '';
        var inputText = '';
        if(config.hasInput){
            if(config.inputText){
                var inputText = config.inputText;
            }
            hasInput = '<input type="text" id="window_input" class="window_input" style="margin-top: 80px" value="'+inputText+'"/>';
        }
        var boundingBox_html = '';
        boundingBox_html += '<div class="window_boundingBox">';
        boundingBox_html += '<div class="window_header" id="window_header">'+config.title+'</div>';
        boundingBox_html += '<div class="window_body">'+config.content+hasInput+'</div>';
        boundingBox_html += '<div class="window_chooseArea"></div>';
        boundingBox_html += '<div class="window_footer">' +
        ''+
        '</div>';
        boundingBox_html += '</div>';
        var boundingBox = $(boundingBox_html);
        hasParent?boundingBox.appendTo($(".window_mask", parent.document)):boundingBox.appendTo($(".window_mask"));

        if(config.hasImg){
            var showImg = $('<div class="showImg"></div>');
            hasParent?$('.window_body', parent.document).prepend(showImg):$('.window_body').prepend(showImg);
        }else{
            var showDiv = $('<div class="iqs_defaultBox"></div>');
            hasParent?$('.window_body', parent.document).prepend(showDiv):$('.window_body').prepend(showDiv);
        }

        //设置弹窗的长宽和位置
        boundingBox.css({
            width : config.width + 'px',
            height: config.height+ 'px',
            left  : (config.x || (window.innerWidth - config.width) / 2 + 'px'),
            top   : (config.y || (window.innerHeight - config.height) / 2 + 'px'),
            margin: config.margin
            //,
            //paddingBottom: '20px'
        });

        //查看是否需要一个确定按钮
        if(config.textForSureBtn){
            var sureBtn = $('<input type="button" value="'+config.textForSureBtn+'" class="window_alertBtn" />');
            hasParent?$('.window_footer', parent.document).append(sureBtn):$('.window_footer').append(sureBtn);
            sureBtn.unbind('click');
            sureBtn.click(function(){
                config.handlerForSure && config.handlerForSure();
                mask && mask.remove();
                boundingBox.remove();
                //将遮罩一并删除掉
            });
        }

        //查看是否需要一个取消按钮
        if(config.textForcancleBtn){
            var cancleBtn = $('<input type="button" value="'+config.textForcancleBtn+'" class="window_cancleBtn" />');
            hasParent?$('.window_footer', parent.document).append(cancleBtn):$('.window_footer').append(cancleBtn);
            cancleBtn.unbind('click');
            cancleBtn.click(function(){
                config.handlerForCancle && config.handlerForCancle();
                boundingBox.remove();
                //将遮罩一并删除掉
                mask && mask.remove();
            });
        }

        //查看是否需要一个关闭按钮
        if(config.hasCloseBtn){
            var closeBtn = $('<span class="window_closeBtn">×</span>');
            closeBtn.appendTo(boundingBox);
            closeBtn.click(function(){
                config.handlerForClose && config.handlerForClose();
                boundingBox.remove();
                //将遮罩一并删除掉
                mask && mask.remove();
            });
        }

        //如果存在皮肤样式名，则在弹窗最外层添加一个class
        //skinClassName success warning error
        if(config.skinClassName){
            boundingBox.addClass(config.skinClassName);
        }
        //是否需要自动隐藏
        if(config.autoHide){
            setTimeout(function(){
                boundingBox.remove();
                mask && mask.remove();
            },1000);
        }


        if(config.dragHandle){
            //console.log(config.dragHandle);
            var Handle = parent.window.document.getElementById('window_header');
            //console.log(window);
            //console.log(parent.window);
            Handle.onmousedown = function(e){
                var This = this;
                var e = e||window.event;
                var mouseX = e.clientX;
                var mouseY = e.clientY;
                var leftX = This.getBoundingClientRect().top;
                var topY = This.getBoundingClientRect().left;
                var TitleY = parseInt(This.parentNode.style.marginTop.replace('px',''));
                var TitleX = parseInt(This.parentNode.style.marginLeft.replace('px',''));
                var x = mouseX-leftX;
                var y = mouseY-topY;
                //console.log(mouseY,mouseX);
                //console.log(TitleY,TitleX);
                parent.window.document.onmousemove = function(e){
                    var curTop = e.clientY;
                    var curLeft = e.clientX;
                    var parent = This.parentNode;
                    //console.log(curTop,curLeft);
                    parent.style.marginTop = TitleY+curTop-mouseY+'px';
                    parent.style.marginLeft = TitleX+curLeft-mouseX+'px';
                    //console.log(TitleY+curTop-mouseY,TitleX+curLeft-mouseX);
                }
                parent.window.document.onmouseup = function(){
                    parent.window.document.onmousedown = null;
                    parent.window.document.onmousemove = null
                }

            }
        }

    },
    confirm: function(){},
    prompt : function(){}
};

function fMouseDown(This){
    //console.log(This);
}

