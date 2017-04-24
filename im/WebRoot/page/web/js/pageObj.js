"use strict";
/**
 * Created by gao_yn on 2017/2/3.
 */
/**
 * 获取历史记录分页切换
 */
function PageObj(){
    //var divObj,pageSize,actionCallback;
    var options,actionCallback;
    this.init.apply(this,arguments);
}
//分页 依内置的html DOM对象的枚举
PageObj.ActionType =
{
    prev        :   "1",    //上一页
    next        :   "2"  //下一页
    //first       :   "3",    //第一页
    //last        :   "4"    //最后一页
};
PageObj.prototype.init = function (options,actionCallback){
    this._DestObj = options.divObj;
    this.createHtml();
    this.conversationType = options.conversationtype;//类型
    this.hosFile=options.hosFile;//是否是文件
    this.targetId = options.targetId;//目标id
    //console.log(this.targetId);
    this._isLoading=false;
    this.searchStr =options.searchstr;
    this.hasmoreMessage=true;  //是否正在获取数据中
    this._pageNow=1;    //当前页
    this.dataBase=[];
    this._pageSize=options.pageSize || 20;  //每页笔数
    var actionType = PageObj.ActionType;
    this._SelfObj = {};
    this._ActionCallback = actionCallback;
    this.messageList=[];
    //设置预设的动作标签
    var objs = {};
    objs[actionType.prev] = '#spanPre';
    objs[actionType.next] = '#spanNext';
    //objs[actionType.first] = '#spanFirst';
    //objs[actionType.last] = '#spanLast';
    this._SelfObjString = objs;
    this._pageNum=0;    //页数
    //this._dataNum=options.pageCount; //总条数
    this.lastTime = 0;//最后时间
    //this.setDataNum(this._dataNum);
    this._refreshPageObj();
    this.getMoreMessage();
    this._setActionListener();
    this._matchValue();
   this.datechange();
};
PageObj.prototype.createHtml=function (){
    this._DestObj.empty();
    //this._m_sPageHtml='<section>\
    //<div class="example">\
    //<input type="text" id="calendar">\
    //</div>\
    //</section>\
    //<div class="infoDet-pageQuery">\
    //<i class="infoDet-firstPage allowClick" id="spanLast"></i>\
    //<i class="infoDet-prePage allowClick"  id="spanNext"></i>\
    //<i class="infoDet-nextPage" id="spanPre"></i>\
    //<i class="infoDet-lastPage"  id="spanFirst"></i>\
    //</div>';
    this._m_sPageHtml='<section>\
    <div class="example">\
    <strong class="dateIcon"></strong>\
    <input type="text" class="calendar">\
    </div>\
    </section>\
    <div class="infoDet-pageQuery">\
    <i class="infoDet-prePage allowClick"  id="spanNext"></i>\
    <i class="infoDet-nextPage" id="spanPre"></i>\
    </div>';
    this._DestObj.append(this._m_sPageHtml);
    this._DestObj.find('.calendar').asDatepicker({
        namespace: 'calendar',
        lang: 'zh',
        position:'top'
    });
};
PageObj.prototype.datechange=function(){
    var _self=this;
    this._DestObj.find('.calendar').off('blur');
    this._DestObj.find('.calendar').on('blur',function(){
        $('#calendar-api-show').asDatepicker('hide');
        _self.searchStr='';
        var aDateTime=_self._DestObj.find('.calendar').asDatepicker('getDate', 'yyyy/mm/dd');
        var sYear=aDateTime[0]+"  23:59:59";
        var date = new Date(sYear);
        var nSelTime = date.getTime();
        _self.lastTime=nSelTime;
        _self.dataBase=[];
        _self._pageNow=1;
        _self._pageNum=0;    //页数
        _self.hasmoreMessage=true;
        _self.getMoreMessage();
        //_self.setDataNum(_self._dataNum);
        _self._matchValue();
    });
};
/**
 * 获取页数
 */
PageObj.prototype.getMoreMessage=function(){
    var _self=this;
    if (this.searchStr) {
        RongIMLib.RongIMClient.getInstance().searchMessageByContent(RongIMLib.ConversationType[this.conversationType], this.targetId,this.searchStr, this.lastTime,this._pageSize,1,{
            onSuccess: function(data,count) {
                _self._pageNum= Math.ceil(count / _self._pageSize) || 0;
                if (_self._pageNow == _self._pageNum) {
                    _self.hasmoreMessage = false;
                }
                _self.messageList=data;
                for(var i=1;i<data.length+1;i++){
                    _self.dataBase.unshift(data[data.length-i]);
                }
                _self._CallbackProcess(0);
                _self.lastTime = (data[0] || {}).sentTime || 0;
                _self._matchValue();
            },
            onError:function(){

            }
        })
    }
    else {
        if(this.hosFile == 'RC:FileMsg'){
            RongIMLib.RongIMClient.getInstance().getHistoryMessages(RongIMLib.ConversationType[this.conversationType],this.targetId, this.lastTime, 20,{
                onSuccess: function(list,has) {
                    _self.hasmoreMessage = has;
                    var alist = list;
                    var end = alist.length - _self._pageSize;
                    alist.splice(0, end < 0 ? 0 : end);
                    _self.messageList=alist;
                    if(!has){
                        _self._pageNum=_self._pageNow;
                    }else{
                        _self._pageNum++;
                    }
                    for(var i=1;i<alist.length+1;i++){
                        _self.dataBase.unshift(alist[alist.length-i]);
                    }
                    _self._CallbackProcess(0);
                    _self.lastTime = (alist[0] || {}).sentTime || 0;
                },
                onError:function(){

                }
            },this.hosFile);
        }else{
            RongIMLib.RongIMClient.getInstance().getHistoryMessages(RongIMLib.ConversationType[this.conversationType],this.targetId, this.lastTime, 20,{
                onSuccess: function(list,has) {
                    _self.hasmoreMessage = has;
                    var alist = list;
                    var end = alist.length - _self._pageSize;
                    alist.splice(0, end < 0 ? 0 : end);
                    _self.messageList=alist;
                    if(!has){
                        _self._pageNum=_self._pageNow;
                    }else{
                        _self._pageNum++;
                    }
                    for(var i=1;i<alist.length+1;i++){
                        _self.dataBase.unshift(alist[alist.length-i]);
                    }
                    _self._CallbackProcess(0);
                    _self.lastTime = (alist[0] || {}).sentTime || 0;
                },
                onError:function(){

                }
            });
        }
    }
};
/**
 * 设置DOM Click事件监听
 * @private
 */
PageObj.prototype._setActionListener = function (){
    var _self=this;
    for(var key in this._SelfObj)
    {
        var obj = this._SelfObj[key];
        obj.click(function()
        {
            //if(_self._pageNum==1){
            //    return;
            //}
            //if(_self._isLoading)
            //   return;
            //_self._isLoading = true;

           // _self.hasmoreMessage = true;
            var type = null;
            var typeObj = null;
            //查找响应的点击对象
            for(var key in _self._SelfObj)
            {
                var obj = _self._SelfObj[key];
                if(this == obj[0])
                {
                    type = key;
                    typeObj = obj;
                    break;
                }
            }
            if(type && typeObj && _self._actionProcess(type))
            {
                _self._CallbackProcess(type);
            }
            else
            {
                _self._isLoading = false;
            }
        });
    }
};
/**
 * 运行点击动作回调
 * @param type
 * @private
 */
PageObj.prototype._CallbackProcess = function (type)
{
       // this._isLoading=true;
        var _self = this;
        this._isLoading=true;
        _self._ActionCallback.call(this,type,this.messageList,function()
        {
            _self._isLoading = false;
        });
}
/**
 * 重置显示的对象
 * @private
 */
PageObj.prototype._refreshPageObj = function ()
{
    for(var key in this._SelfObjString)
    {
        this._SelfObj[key]= this._DestObj.find(this._SelfObjString[key]);
    }
};
/**
 * 设置数据数
 * @param (number)num
 * @param (boolean)isCal
 */
PageObj.prototype.setDataNum = function (num,isCal)
{
    if(num)
    {
        this._dataNum = parseInt(num);
        if(isCal === undefined || isCal)
        {
            this._calculate();
        }
    }
};
/**
 * 计算分页数据
 * @private
 */
PageObj.prototype._calculate = function()
{
    if(this._dataNum)
    {
        var pagenum = this._dataNum  / this._pageSize;
        var fixPage = pagenum % 1;
        pagenum = parseInt(pagenum);
        pagenum += (fixPage > 0);
        this._pageNum = pagenum;
        if(this._pageNow > this._pageNum)
        {
            this._pageNow = 1;
        }
    }
    else
    {
        this._pageNow = 1;
    }
    this._matchValue();
};
/**
 * 将分页数据设置到画面上
 * @private
 */
PageObj.prototype._matchValue = function()
{
        var actionType = PageObj.ActionType;
        var bolfirst = (this._pageNow != 1);
        var bollast = (this._pageNow < this._pageNum) || this.hasmoreMessage;
        //var bolgoto = (this._pageNum>1);
        this._setHtmlLink(actionType.prev,bolfirst);
        //this._setHtmlLink(actionType.first,bolfirst);
        this._setHtmlLink(actionType.next,bollast);
        //this._setHtmlLink(actionType.last,bollast);
        //this._setHtmlLink(actionType.gotoBtn,bolgoto);
};
/**
 * 设置HTML连结
 * @param type
 * @param link
 * @private
 */
PageObj.prototype._setHtmlLink = function (type,link)
{
    var obj = this._SelfObj[type];
    if(obj)
    {
        if(link)
        {
            obj.addClass('allowClick');
        }
        else
        {
            obj.removeClass('allowClick');
        }
    }
};
/**
 * 分页动作处理
 * @param {number}type
 * @returns {boolean}
 * @private
 */
PageObj.prototype._actionProcess = function (type)
{

    var isChanged = false;
    var actionType = PageObj.ActionType;

    switch (type)
    {
        case actionType.prev:
        {
            if(this._pageNow > 1)
            {
                this.messageList = [];
                this._pageNow--;
                this.messageList = this.dataBase.slice(this.dataBase.length - (this._pageNow * this._pageSize), this.dataBase.length - ((this._pageNow - 1) * this._pageSize));
                isChanged = true;
            }
        }
            break;
        case actionType.next:
        {

            if(this._pageNow < this._pageNum  || this.hasmoreMessage)
            {
                    this._pageNow ++ ;
                    isChanged = true;
                    this.messageList = [];
                    if ((this._pageNow - 1) * this._pageSize < this.dataBase.length) {
                        var start = this.dataBase.length - (this._pageNow * this._pageSize);
                        this.messageList = this.dataBase.slice(start < 0 ? 0 : start, this.dataBase.length - ((this._pageNow - 1) * this._pageSize));
                    }
                    else {
                        this.getMoreMessage();
                    }
            }else{
                this._pageNum=this._pageNow;
            }
        }
            break;
        case actionType.first:
        {
            if(this._pageNow >1)
            {
                this._pageNow = 1;
                isChanged = true;
            }
        }
            break;
        case actionType.last:
        {
            if(this._pageNow < this._pageNum)
            {
                this._pageNow = this._pageNum;
                isChanged = true;
            }
        }
            break;
    }
    if(isChanged)
    {
        this._matchValue();
    }
    return isChanged;
}


