<%@ page language="java" contentType="text/html; charset=utf-8"    pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/html">
<head lang="en">
    <meta charset="UTF-8">
    <meta name="description" content="">
    <%--<meta http-equiv="refresh" content="2;url=http://www.baidu.com">--%>
    <title></title>
    <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/normalize.css">
    <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/dateMain.css">
    <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/prism.css">
    <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/asDatepicker.css">
    <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/main.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/window.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/perfect-scrollbar.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/jquery.jOrgChart.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/OrgChart.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/backstageMg.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/clipImg.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/cropper.min.css"/>

    <script src="<%=request.getContextPath() %>/page/web/js/config.js"></script>

    <script src="<%=request.getContextPath() %>/page/web/js/RongIMLib.js"></script>

    <script src="<%=request.getContextPath() %>/page/web/js/jquery-2.1.1.min.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/jquery.mobile.custom.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/jquery.toc.min.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/prism.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/jquery-asDatepicker.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/language.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/fileDownload.js"></script>

    <%--七牛上传--%>
    <script src="<%=request.getContextPath() %>/page/web/js/qiniu/qiniu.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/upload.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/qiniu/init.js"></script>

    <script src="<%=request.getContextPath() %>/page/web/js/md5.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/main.js"></script>
    <%--<script src="http://cdn.ronghub.com/RongIMLib-2.2.4.min.js"></script>--%>
    <%--<script src="http://cdn.ronghub.com/RongEmoji-2.2.4.min.js"></script>--%>
        <%--<script src="http://cdn.ronghub.com/RongIMVoice-2.2.4.min.js"></script>--%>
        <script src="http://cdn.ronghub.com/RongEmoji-2.2.5.min.js"></script>
        <script src="http://cdn.ronghub.com/RongIMVoice-2.2.5.min.js"></script>
    <script src="http://webapi.amap.com/js/marker.js"></script>
    <script type="text/javascript" src="http://webapi.amap.com/maps?v=1.3&key=acafe737e6344c4ce19d101b9f3b1d03"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/getCurrentPos.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/jquery.mousewheel.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/perfect-scrollbar.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/rongyun.js"></script>

    <script src="<%=request.getContextPath() %>/page/web/js/window.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/seeOrgnizeTree.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/pageObj.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/backstageMg.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/Class.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/seeOrgnizeTree.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/dialogOper.js"></script>
    <%--<script src="<%=request.getContextPath() %>/page/web/js/creatGroup.js"></script>--%>
    <script src="<%=request.getContextPath() %>/page/web/js/message.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/conversation.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/organization.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/jquery-ui.min.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/jquery.jOrgChart.js"></script>

    <script src="<%=request.getContextPath() %>/page/web/js/bootstrap.min.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/cropper.min.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/clipImg.js"></script>
    </head>
    <%--${SessionPrivilege.map}--%>
    <%--${SessionUser.id}--%>
<body userid="${SessionUser.id}" token="${SessionUser.token}" limit="${SessionPrivilege.map}" >

	<!--
	权限设置判断
	<c:if test="${SessionPrivilege.map.rsglsc != null}">
	</c:if>
	-->
    <audio src="page/web/css/sound/reciveSound.mp3" id="systemSound_recive"  type="audio/wav">
    您的浏览器不支持 audio 标签。
    </audio>
    <div class="chatHeader">
    <ul class="chatHeaderMenu">
        <li class="active" bindPanel="news">消息</li>
        <li class="" bindPanel="orgnized">组织通讯录</li>
		<c:if test="${SessionPrivilege.map.htgl != null}">
            <li onclick='jumpToBack()'>后台管理</li>
            <%--<li onclick='window.location.href="<%=request.getContextPath() %>/page/admin/11.jsp"'>后台管理</li>--%>
		</c:if>
        <%--<li bindPanel="back">后台管理</li>   --%>
    </ul>
    <ul class="chatHeaderOper">
        <li class="chatLeftIcon"></li>
        <li class="chatLeftIcon"></li>
        <%--<li class="chatLeftIcon" class="" onclick="loginOA()">登录OA</li>--%>
    </ul>
</div>

<!--消息-->
<div class="chatContent chatBoxShadow news">
    <ul class="chatMenu">
        <li class="chatLeftIcon active" bindPanel="groupChatList"></li>
        <li class="chatLeftIcon" bindPanel="usualChatList"></li>
        <li class="chatLeftIcon" bindPanel="newsChatList"></li>
    </ul>
    <div class="newsTabContent">
        <div class="chatContent groupChatList">
            <div class="listCtrl myGroup">
                <span class="triOpen chatLeftIcon"></span>
                <span class="discrib">我的群组</span>
            </div>
            <ul class="groupChatListUl">
            </ul>
        </div>
        <div class="chatContent usualChatList chatHide">
            <div class="listCtrl myGroup">
                <span class="triOpen chatLeftIcon"></span>
                <span class="discrib">我的常用联系人</span>
            </div>
            <ul class="groupChatListUl">

            </ul>
        </div>
        <div class="chatContent newsChatList chatHide">
            <ul class="groupChatListUl usualChatListUl" style="margin: 6px 0px 35px 10px">
            </ul>
        </div>
    </div>
    <div class="footerPlus chatLeftIcon">
        <div class="operMenuList">
            <ul>
                <li class="chatLeftIcon">添加好友</li>
                <%--权限--%>
                <%--<li class="chatLeftIcon">发起聊天</li>--%>
                <c:if test="${SessionPrivilege.map.ltszfqgrlt != null}">
                    <li class="chatLeftIcon">发起聊天</li>
                </c:if>
                <c:if test="${SessionPrivilege.map.ltszfqgrlt == null}">
                    <li class="chatLeftIcon" displayLimit="false">发起聊天</li>
                </c:if>
                <c:if test="${SessionPrivilege.map.qzcjq != null}">
                    <li class="chatLeftIcon">创建群组</li>

                </c:if>
                <c:if test="${SessionPrivilege.map.qzcjq == null}">
                    <li class="chatLeftIcon" displayLimit="false">创建群组</li>

                </c:if>
            </ul>
            <div id="zhanwei"></div>
        </div>
    </div>

</div>

<!--组织通讯录-->
<div class="chatContent orgnized chatHide chatBoxShadow">
    <!--<div></div>-->
    <div class="serachMenber">
        <input class="searchInput chatLeftIcon">
        <span class="defaultText">搜索人员</span>
        </input>
    </div>
    <div class="organizationList">

    </div>
</div>

<!--后台管理-->
<div class="chatContent chatBoxShadow chatHide personalCenter">
    <ul class="backstageMg" id="backstageMgId">
		<%--<c:if test="${SessionPrivilege.map.grsz != null}">	  --%>
			<li class="clearfix" data-type="0">
				<em class="bMg-personalSet"></em>
				<span>个人设置</span>
			</li>
		<%--</c:if>--%>
        <li class="clearfix" data-type="1">
            <em class="bMg-systemSet"></em>
            <span>系统设置</span>
        </li>
        <li class="clearfix" data-type="2">
            <em class="bMg-changePw"></em>
            <span>修改密码</span>
        </li>
    </ul>
</div>

<!--聊天部分-->
<div class="chatBoxOuter">
    <div class="chatBox chatBoxShadow" style="position:relative" id="chatBox">
        <div id="map"></div>
        <div id="tip"></div>
    <!--地图-->
        <div class="orgNavClick groupMap chatHide" id="groupMap">
            <h3 class="perSetBox-title clearfix">
                <span>天坊产品部</span>
                <div class="messageRecord clearfix">
                    <b></b>
                </div>
            </h3>
            <div class="groupMapBox">
                <div id="container"></div>
                <div class="groupMapMember">
                    <ul>
                        <%--<li>--%>
                            <%--<img src="page/web/css/img/1.jpg">--%>
                        <%--</li>--%>
                        <%--<li>--%>
                            <%--<img src="page/web/css/img/1.jpg">--%>
                        <%--</li>--%>
                        <%--<li>--%>
                            <%--<img src="page/web/css/img/1.jpg">--%>
                        <%--</li>--%>
                        <%--<li>--%>
                            <%--<img src="page/web/css/img/1.jpg">--%>
                        <%--</li>--%>
                        <%--<li>--%>
                            <%--<img src="page/web/css/img/1.jpg">--%>
                        <%--</li>--%>
                    </ul>
                </div>
            </div>
        </div>
        <!--个人设置-->
    <%--<c:if test="${SessionPrivilege.map.grsz != null}">--%>
    <div class="perSetBox orgNavClick chatHide" id="personSettingId">
    </div>
    <%--</c:if>--%>
        <!--系统设置-->
        <div class="perSetBox orgNavClick chatHide" id="systemSet">
            <h3 class="perSetBox-title">系统设置</h3>

            <div class="systemSetVoice clearfix">
                <span>通知提示音：</span>
                <p class="clearfix systemVoiceBtn">
                    <i class="VoiceBtn_L" data-state="0"></i>
                    <i class="VoiceBtn_R" data-state="1"></i>
                </p>
            </div>
            <b class="systemSet-keep">保存</b>
        </div>
        <div class="perSetBox orgNavClick chatHide">
            <h3 class="perSetBox-title">修改密码</h3>
            <div class="changePassword">
                <div class="clearfix cp-oldPassword">
                    <span>旧密码：</span>
                    <input type="password" id="oldPassword">
                    <p class="oldPassworderror"></p>
                </div>
                <div class="clearfix">
                    <span>新密码：</span>
                    <input type="password" class="cp-newPassword" id="cp-newPasswordId">
        <ul class="cp-passwordSecurity clearfix">
        <li id="strength_L"></li>
        <li id="strength_M"></li>
        <li id="strength_H"></li>
        </ul>
                </div>
                <div class="clearfix cp-reNewPassword">
                    <span>重复密码：</span>
                    <input type="password" id="comparepwd">
                    <p class="retMewPw"></p>
                </div>
            </div>
            <b class="systemSet-keep" id="systemSet-savepsd">保存</b>
        </div>

        <!--系统消息历史记录-->
        <div class="mesContainerSys mesContainer orgNavClick chatHide" id="sysContainer">
            <h3 class="perSetBox-title clearfix">
                <span>张三</span>
            </h3>

            <div class="mr-chatContent mr-sysHistory">

            </div>
        </div>
        <!--geren消息记录-->
        <div class="mesContainerSelf mesContainer orgNavClick chatHide" id="perContainer">
            <h3 class="perSetBox-title clearfix">
                <span>张三</span>

                <div class="messageRecord clearfix">
                    <i class="mr-Location"></i>
                    <i class="mr-record" id="mr-record"></i>
                </div>
            </h3>
            <%--<div class="mr-chatview">--%>
            <%--</div>--%>
            <div class="mr-chateditBox">
                <div class="rongyun-emoji"></div>
                <div class="mr-expresFile clearfix">
                    <span class="showEmoji"></span>
                    <i></i>
                    <div class="upload-img">
                        <c:if test="${SessionPrivilege.map.ltszwjsc != null}">
                            <input type="file" class="comment-pic-upd upload_file" id="upload_file"/>
                        </c:if>
                    </div>
                </div>
                <pre id="message-content" contenteditable-directive
                contenteditable="true" contenteditable-dire="" ctrl-enter-keys=""
                atshow-dire=""  ctrlenter="false" placeholder="请输入文字..."
                 class="textarea" draggable="draggable"></pre>
                <strong class="sendMsgBTN">发送</strong>
            </div>
        </div>
        <!--群组消息记录-->
        <div class="mesContainerGroup mesContainer orgNavClick chatHide" id="groupContainer">
            <h3 class="perSetBox-title clearfix">
                <span>张三</span>
                <div class="messageRecord clearfix">
                    <i class="mr-Location"></i>
                    <i class="mr-record" id="groupRecord"></i>
                </div>
            </h3>

            <div class="mr-chateditBox">
                <div class="rongyun-emoji"></div>
                <div class="mr-expresFile clearfix">
                    <span class="showEmoji"></span>
                    <i></i>
                    <div class="upload-img">
                        <c:if test="${SessionPrivilege.map.ltszwjsc != null&&SessionPrivilege.map.ltszqzlt != null}">
                            <input type="file" class="comment-pic-upd upload_file" id="upload_file"/>
                        </c:if>
                    </div>
                </div>
                <%--<textarea placeholder="说点什么..." class="textarea"></textarea>--%>
                <pre id="message-content" contenteditable-directive
                contenteditable="true" contenteditable-dire="" ctrl-enter-keys=""
                atshow-dire=""  ctrlenter="false" placeholder="请输入文字..."
                class="textarea" draggable="draggable"></pre>
                <strong class="sendMsgBTN">发送</strong>
            </div>
        </div>
        <!--infoDet-page-->
        <div class="orgNavClick personalData  chatHide" id="personalData">
            <ul class="infoDetails clearfix" id="perInfo">
                <li class="active" data-type="d">资料</li>
                <li data-type="r">聊天记录</li>
                <li data-type="f">聊天文件</li>
            </ul>
            <div class="infoDetailsBox" id="infoDetailsBox">
                <div class="infoDetails-data">
                    <%--<div class="infoDet-personal clearfix">--%>
                        <%--<img src="page/web/css/img/1.jpg">--%>

                        <%--<div class="infoDet-text">--%>
                            <%--<p>张三</p>--%>

                            <%--<div class="clearfix">--%>
                                <%--<span class="infoDet-postInfo"></span>--%>
                                <%--<span class="infoDet-position"></span>--%>
                                <%--<span class="infoDet-addPer"></span>--%>
                            <%--</div>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                    <%--<ul class="infoDetList clearfix">--%>
                        <%--<li>--%>
                            <%--<span>手机：</span>--%>
                            <%--<b>123123123123</b>--%>
                        <%--</li>--%>
                        <%--<li>--%>
                            <%--<span>手机：</span>--%>
                            <%--<b>123123123123</b>--%>
                        <%--</li>--%>
                        <%--<li>--%>
                            <%--<span>手机：</span>--%>
                            <%--<b>123123123123</b>--%>
                        <%--</li>--%>
                        <%--<li>--%>
                            <%--<span>手机：</span>--%>
                            <%--<b>123123123123</b>--%>
                        <%--</li>--%>
                        <%--<li>--%>
                            <%--<span>手机：</span>--%>
                            <%--<b>123123123123</b>--%>
                        <%--</li>--%>
                        <%--<li>--%>
                            <%--<span>手机：</span>--%>
                            <%--<b>123123123123</b>--%>
                        <%--</li>--%>
                    <%--</ul>--%>
                </div>
                <div class="infoDet-chatRecord chatHide">
                    <div class="infoDet-search ">
                        <input type="text" placeholder="查找信息..."/>
                        <i class="searchHostoryInfo"></i>
                    </div>
                    <div class="infoDet-recordAll chatRecordSel">
                        <%--<p class="infoDet-timeRecord">2016-09-28</p>--%>
                        <%--<ul class="infoDet-contentDet">--%>
                            <%--<li class="infoDet-OthersSay">--%>
                                <%--<span>张三&nbsp&nbsp&nbsp17:10:10</span>--%>
                                <%--<p>11111111111</p>--%>
                            <%--</li>--%>
                            <%--<li class="infoDet-selfSay">--%>
                                <%--<span>张三&nbsp&nbsp&nbsp17:10:10</span>--%>

                                <%--<p>11111111111</p>--%>
                            <%--</li>--%>
                        <%--</ul>--%>
                    </div>
                    <div class="infoDet-page clearfix">
                        <%--<section>--%>
                            <%--<div class="example">--%>
                                <%--<input type="text" id="calendar">--%>
                            <%--</div>--%>
                        <%--</section>--%>
                        <%--<div class="infoDet-pageQuery">--%>
                            <%--<i class="infoDet-firstPage allowClick"></i>--%>
                            <%--<i class="infoDet-prePage allowClick"></i>--%>
                            <%--<i class="infoDet-nextPage"></i>--%>
                            <%--<i class="infoDet-lastPage"></i>--%>
                        <%--</div>--%>
                    </div>
                </div>
                <div class="infoDet-flieRecord chatHide">
                    <div class="infoDet-search">
                        <input type="text" placeholder="查找信息..."/>
                        <i></i>
                    </div>
                    <div class="infoDet-recordAll chatRecordSel">
                        <%--<ul class="chatFile">--%>
                            <%--<li class="chatFile-folder">--%>
        <%--<i></i>--%>
        <%--<p>--%>
        <%--<b>色彩搭配(188.mb)</b>--%>
        <%--<span>2016-11-18&nbsp&nbsp15:14&nbsp&nbsp张三</span>--%>
        <%--</p>--%>
        <%--<strong>打开</strong>--%>
        <%--<strong>打开文件夹</strong>--%>
        <%--</li>--%>
                            <%--<li class="chatFile-zipFile">--%>
                                <%--<i></i>--%>

                                <%--<p>--%>
                                    <%--<b>色彩搭配(188.mb)</b>--%>
                                    <%--<span>2016-11-18&nbsp&nbsp15:14&nbsp&nbsp张三</span>--%>
                                <%--</p>--%>
                                <%--<strong>打开</strong>--%>
                                <%--<strong>打开文件夹</strong>--%>
                            <%--</li>--%>
                            <%--<li class="chatFile-img">--%>
                                <%--<i></i>--%>

                                <%--<p>--%>
                                    <%--<b>色彩搭配(188.mb)</b>--%>
                                    <%--<span>2016-11-18&nbsp&nbsp15:14&nbsp&nbsp张三</span>--%>
                                <%--</p>--%>
                                <%--<strong>打开</strong>--%>
                                <%--<strong>打开文件夹</strong>--%>
                            <%--</li>--%>
                            <%--<li class="chatFile-unknown">--%>
                                <%--<i></i>--%>

                                <%--<p>--%>
                                    <%--<b>色彩搭配(188.mb)</b>--%>
                                    <%--<span>2016-11-18&nbsp&nbsp15:14&nbsp&nbsp张三</span>--%>
                                <%--</p>--%>
                                <%--<strong>打开</strong>--%>
                                <%--<strong>打开文件夹</strong>--%>
                            <%--</li>--%>
                        <%--</ul>--%>
                    </div>
                    <div class="infoDet-page">
                        <%--<span></span>--%>

                        <%--<div class="infoDet-pageQuery">--%>
                            <%--<i class="infoDet-firstPage"></i>--%>
                            <%--<i class="infoDet-prePage"></i>--%>
                            <%--<i class="infoDet-nextPage"></i>--%>
                            <%--<i class="infoDet-lastPage"></i>--%>
                        <%--</div>--%>
                    </div>
                </div>
            </div>
        </div>
        <!--群组资料-->
        <div class="orgNavClick BreadcrumbsOuter chatHide">
            <ul class="Breadcrumbs">
                <li><a href=""> 1111 </a> ></li>
                <li><a href=""> 2222 </a> ></li>
                <li><a href=""> 3333 </a> ></li>
            </ul>
            <a class="chatLeftIcon seeOrgnizeTree"></a>
        </div>
    <!-- 群组历史记录-->
    <div class="orgNavClick personalData chatHide" id="groupData" >
        <ul class="infoDetails clearfix" >
            <li class="active" data-type="d">资料</li>
            <li data-type="r">聊天记录</li>
            <li data-type="f">聊天文件</li>
        </ul>
        <div class="infoDetailsBox" id="groupDetailsBox">
            <div class="group-data">
                <ul class="groupInfo">
                    <%--<li class="groupInfo-name">--%>
                    <%--<span>群组名称：</span>--%>
                    <%--<b>天方产品部</b>--%>
                    <%--</li>--%>
                    <%--<li class="groupInfo-setTime">--%>
                    <%--<span>群组名称：</span>--%>
                    <%--<b>天方产品部</b>--%>
                    <%--</li>--%>
                    <%--<li class="groupInfo-Controller">--%>
                    <%--<span>群主/管理员：</span>--%>
                    <%--<img src="page/web/css/img/1.jpg">--%>
                    <%--</li>--%>
                    <%--<li class="groupInfo-disturb">--%>
                    <%--<span>消息免打扰：</span>--%>

                    <%--<p>--%>
                    <%--<i></i>--%>
                    <%--<i></i>--%>
                    <%--</p>--%>
                    <%--</li>--%>
                </ul>
                <div class="groupInfo-memberList">
                    <div class="groupInfo-number clearfix">
                    <span>成员(6)</span>

                    <p class="clearfix">
                    <i class="groupInfo-noChat" title="禁言"></i>
                    <i></i>
                    </p>
                    </div>
                    <ul class="groupInfo-memberAll" title="群成员管理">
                        <%--<li>--%>
                        <%--<img src="page/web/css/img/1.jpg">--%>

                        <%--<p>张三(产品经理)</p>--%>
                        <%--</li>--%>
                    </ul>
                </div>
            </div>
            <div class="infoDet-chatRecord chatHide">
                <div class="infoDet-search ">
                <input type="text" placeholder="查找信息..."/>
                <i class="searchHostoryInfo"></i>
             </div>
                <%--<div class="infoDet-search active">--%>
                <%--<input type="text" placeholder="查找信息..."/>--%>
                <%--<i></i>--%>
                <%--</div>--%>
                <div class="infoDet-recordAll chatRecordSel">
                <%--<p class="infoDet-timeRecord">2016-09-28</p>--%>
                <%--<ul class="infoDet-contentDet">--%>
                <%--<li class="infoDet-OthersSay">--%>
                <%--<span>张三&nbsp&nbsp&nbsp17:10:10</span>--%>
                <%--<p>11111111111</p>--%>
                <%--</li>--%>
                <%--<li class="infoDet-selfSay">--%>
                <%--<span>张三&nbsp&nbsp&nbsp17:10:10</span>--%>

                <%--<p>11111111111</p>--%>
                <%--</li>--%>
                <%--</ul>--%>
                </div>
                <div class="infoDet-page">
                <%--<span></span>--%>
                <%--<div class="infoDet-pageQuery">--%>
                <%--<i class="infoDet-firstPage allowClick"></i>--%>
                <%--<i class="infoDet-prePage allowClick"></i>--%>
                <%--<i class="infoDet-nextPage"></i>--%>
                <%--<i class="infoDet-lastPage"></i>--%>
                <%--</div>--%>
                </div>
            </div>
            <div class="infoDet-flieRecord chatHide">
    <div class="infoDet-search">
    <input type="text" placeholder="查找信息..."/>
    <i></i>
    </div>
    <div class="infoDet-recordAll chatRecordSel">
    <%--<ul class="chatFile">--%>
    <%--<li class="chatFile-folder">--%>
    <%--<i></i>--%>

    <%--<p>--%>
    <%--<b>色彩搭配(188.mb)</b>--%>
    <%--<span>2016-11-18&nbsp&nbsp15:14&nbsp&nbsp张三</span>--%>
    <%--</p>--%>
    <%--<strong>打开</strong>--%>
    <%--<strong>打开文件夹</strong>--%>
    <%--</li>--%>
    <%--<li class="chatFile-zipFile">--%>
    <%--<i></i>--%>

    <%--<p>--%>
    <%--<b>色彩搭配(188.mb)</b>--%>
    <%--<span>2016-11-18&nbsp&nbsp15:14&nbsp&nbsp张三</span>--%>
    <%--</p>--%>
    <%--<strong>打开</strong>--%>
    <%--<strong>打开文件夹</strong>--%>
    <%--</li>--%>
    <%--<li class="chatFile-img">--%>
    <%--<i></i>--%>

    <%--<p>--%>
    <%--<b>色彩搭配(188.mb)</b>--%>
    <%--<span>2016-11-18&nbsp&nbsp15:14&nbsp&nbsp张三</span>--%>
    <%--</p>--%>
    <%--<strong>打开</strong>--%>
    <%--<strong>打开文件夹</strong>--%>
    <%--</li>--%>
    <%--<li class="chatFile-unknown">--%>
    <%--<i></i>--%>

    <%--<p>--%>
    <%--<b>色彩搭配(188.mb)</b>--%>
    <%--<span>2016-11-18&nbsp&nbsp15:14&nbsp&nbsp张三</span>--%>
    <%--</p>--%>
    <%--<strong>打开</strong>--%>
    <%--<strong>打开文件夹</strong>--%>
    <%--</li>--%>
    <%--</ul>--%>
    </div>
    <div class="infoDet-page">
    <%--<span></span>--%>

    <%--<div class="infoDet-pageQuery">--%>
    <%--<i class="infoDet-firstPage"></i>--%>
    <%--<i class="infoDet-prePage"></i>--%>
    <%--<i class="infoDet-nextPage"></i>--%>
    <%--<i class="infoDet-lastPage"></i>--%>
    <%--</div>--%>
    </div>
    </div>
        </div>
    </div>
        <!--组织的层级导航-->

        <div class="orgNavClick orgNavClick1 chatHide" id="orgnizedLevel">
            <div class="orgNavTitle">成员</div>
            <%--<ul>--%>
                <%--<li>--%>
                    <%--&lt;%&ndash;<div class="showImgInfo">&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<img src="page/web/css/img/PersonImg.png" alt=""/>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="showPersonalInfo">&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<span>张三（产品总监）</span>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<ul class="personalOperaIcon">&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<li class="sendMsg"></li>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<li class="checkPosition"></li>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<li class="addConver"></li>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;</ul>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                <%--</li>--%>
            <%--</ul>--%>
            <div class="orgNavTitle">成员</div>
            <%--<ul>--%>

                <%--<li>--%>
                    <%--&lt;%&ndash;<div class="showImgInfo">&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<img src="page/web/css/img/PersonImg.png" alt=""/>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="showPersonalInfo">&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<span>张三（产品总监）</span>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<ul class="personalOperaIcon">&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<li class="sendMsg"></li>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<li class="checkPosition"></li>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<li class="addConver"></li>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;</ul>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                <%--</li>--%>
                <%--<li>--%>
                    <%--&lt;%&ndash;<div class="showImgInfo">&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<img src="page/web/css/img/PersonImg.png" alt=""/>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;<div class="showPersonalInfo">&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<span>张三（产品总监）</span>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;<ul class="personalOperaIcon">&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<li class="sendMsg"></li>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<li class="checkPosition"></li>&ndash;%&gt;--%>
                            <%--&lt;%&ndash;<li class="addConver"></li>&ndash;%&gt;--%>
                        <%--&lt;%&ndash;</ul>&ndash;%&gt;--%>
                    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                <%--</li>--%>
                <%--<li>--%>
                    <%--<div class="showImgInfo">--%>
                        <%--<img src="page/web/css/img/PersonImg.png" alt=""/>--%>
                    <%--</div>--%>
                    <%--<div class="showPersonalInfo">--%>
                        <%--<span>张三（产品总监）</span>--%>
                        <%--<ul class="personalOperaIcon">--%>
                            <%--<li class="sendMsg"></li>--%>
                            <%--<li class="checkPosition"></li>--%>
                            <%--<li class="addConver"></li>--%>
                        <%--</ul>--%>
                    <%--</div>--%>
                <%--</li>--%>
                <%--<li>--%>
                    <%--<div class="showImgInfo">--%>
                        <%--<img src="page/web/css/img/PersonImg.png" alt=""/>--%>
                    <%--</div>--%>
                    <%--<div class="showPersonalInfo">--%>
                        <%--<span>张三（产品总监）</span>--%>
                        <%--<ul class="personalOperaIcon">--%>
                            <%--<li class="sendMsg"></li>--%>
                            <%--<li class="checkPosition"></li>--%>
                            <%--<li class="addConver"></li>--%>
                        <%--</ul>--%>
                    <%--</div>--%>
                <%--</li>--%>
            <%--</ul>--%>
        </div>
    <div class="orgNavClick orgNavClick3 chatHide" id="organizeList">
        <div class="organizeListOuter" id="organizeListOuter">
        <!--<div class="topOuter"><p class="horizontal">董事长</p></div>-->
        </div>
    </div>
        <div class="orgNavClick orgNavClick2 chatHide" id="personalDetail ">
            <div class="personalDetailContent">
                <div class="selfImgInfo">
                    <img src="" alt=""/>

                    <%--<div>--%>
                        <%--<p>张三</p>--%>
                        <%--<ul class="selfImgOpera">--%>
                            <%--<li class="sendMsg"></li>--%>
                            <%--<li class="checkPosition"></li>--%>
                            <%--<li class="addConver"></li>--%>
                        <%--</ul>--%>
                    <%--</div>--%>
                <%--</div>--%>
                <div class="showPersonalInfo">
                    <%--<ul>--%>
                        <%--<li>--%>
                            <%--<div>aaaaa:</div>--%>
                            <%--<div>ddddd</div>--%>

                        <%--</li>--%>
                        <%--<li>--%>
                            <%--<div>aaaaa:</div>--%>
                            <%--<div>ddddd</div>--%>

                        <%--</li>--%>
                        <%--<li>--%>
                            <%--<div>aaaaa:</div>--%>
                            <%--<div>ddddd</div>--%>

                        <%--</li>--%>
                        <%--<li>--%>
                            <%--<div>aaaaa:</div>--%>
                            <%--<div>ddddd</div>--%>

                        <%--</li>--%>
                        <%--<li>--%>
                            <%--<div>aaaaa:</div>--%>
                            <%--<div>ddddd</div>--%>

                        <%--</li>--%>

                    <%--</ul>--%>
                </div>

            </div>
        </div>


    </div>
</div>
<div class="WindowMask">
    <div class="conversWindow groupConvers">
        <span class="dialogClose">×</span>

        <div class="dialogHeader">群组管理</div>
        <div class="dialogBody">
            <div class="contactListOuter">
                <p class="outerTitle">选择联系人：</p>

                <div class="contactBox" id="contactBox">
                    <input class="contactsSearch chatLeftIcon" placeholder="查找联系人..."/>
                    <%--<div class="contactSearchResult">没有搜索结果</div>--%>
                    <%--<ul class="contactSearchResult">--%>
                        <%--<li>sssssssssssssssssssssssss</li>--%>
                    <%--</ul>--%>
                    <div class="contactsList"></div>
                </div>
            </div>
            <div class="selectedContactOuter">
                <p class="outerTitle">已选择联系人 <em>(2/99)</em>：</p>

                <div class="contactBox">
                    <div class="selectedList">
                    <ul></ul>
                    </div>
                </div>
            </div>

        </div>
        <div class="dialogFooter">
            <input type="button" value="确定" class="manageSure">
            <input type="button" value="取消" class="manageCancle">
        </div>
    </div>
</div>
    <%--<div class="groupDataBox">--%>
        <%--<ul>--%>
            <%--<li>--%>
                <%--<span>群组名称:</span>--%>
                <%--<b>天方产品部</b>--%>
            <%--</li>--%>
            <%--<li>--%>
                <%--<span>创建时间:</span>--%>
                <%--<b>天方产品部</b>--%>
            <%--</li>--%>
            <%--<li>--%>
                <%--<span>群主/管理员:</span>--%>
               <%--<i>--%>
                    <%--<img src="page/web/css/img/1.jpg">--%>
                <%--</i>--%>
            <%--</li>--%>
        <%--</ul>--%>
    <%--</div>--%>
    <%--<iframe src="" frameborder="0" scrolling="no" class="chatHide" id="iqs_iframe" border="none"></iframe>--%>
    <div class="bMgMask chatHide"></div>
    <div class="container chatHide" id="crop-avatar">
            <div class="avatar-view">
                    <img src="" alt="Avatar">
            </div>
            <form class="avatar-form" action="<%=request.getContextPath() %>/upload!uploadUserLogo" enctype="multipart/form-data" method="post"  id="Uploader">
                <div class="bMg-cutPicture">
                    <h5 class="clearfix">
                        <span class="bMg-changPic">修改头像</span>
                        <i class="bMg-closeBtn" id="bMg-closeBtn"></i>
                    </h5>
                     <p class="bMg-selectImg">使用下列所选照片</p>
                    <div class="clearfix">
                        <div class="bMg-changImgSize">
                            <div class="bMg-ImgContainer chatHide bMg-cropImgBox">
                                <div class="avatar-wrapper"></div>
                                <div class="bMg-rotateDrag clearfix avatar-btns">
    <div class="btn-group bMg-leftRotate">
    <span class="" data-method="rotate" data-option="-90"  title="Rotate -90 degrees"></span>
    </div>
    <div class="btn-group">
    <i class="" data-method="rotate" data-option="90"  title="Rotate 90 degrees"></i>
    </div>
    <strong id="showGrid"></strong>
    </div>
                            </div>
                            <div class="bMg-ImgContainer bMg-cropImgSet">
                                <ul class="bMg-imgList">
                                </ul>
                            </div>
                            <p class="bMg-promptExp">* 选择上面中的一个做为你的照片，或点击"添加"来添加您电脑中的照片来作为您的照片...</p>
                        </div>
                        <div class="bMg-previewImg">
                            <div class="avatar-preview preview-lg">
    <img src="" class="bMg-selImg">
    </div>
                            <div class="bMg-gravityImg">
                        <span >
                            添加
                            <div class="avatar-upload">
                            <input class="avatar-src" name="avatar_src" type="hidden">
                            <input class="avatar-data" name="avatar_data" type="hidden">
                            <input class="avatar-input" id="avatarInput" name="avatar_file" type="file">
                            </div>
                    </span>
                <span class="bMg-delImg">删除</span>
            </div>
                        </div>
                    </div>
                    <div class="bMg-button clearfix bMg-confirm chatHide">
                        <b class="bMg-cancel">取消</b>
                        <button class="avatar-save btnSave" type="submit">保存</button>
                    </div>
                    <div class="bMg-button clearfix  bMg-preserve">
                        <b class="bMg-cancel">取消</b>
                        <b class="bMg-keepImg">保存</b>
                    </div>
                </div>
            </form>
    <!-- Loading state -->
    <%--<div class="loading" aria-label="Loading" role="img" tabindex="-1"></div>--%>
    </div>
    <div class="showAllHeadImg chatHide">
        <div class="bMg-cutPicture">
            <h5 class="clearfix">
                <span class="bMg-changPic">修改头像</span>
                <i class="bMg-closeBtn" id="bMg-closeBtn1"></i>
            </h5>
            <p class="bMg-selectImg">使用下列所选照片</p>
            <div class="clearfix">
                <div class="bMg-changImgSize">
            <div class="bMg-ImgContainer">
                <ul class="bMg-imgList">
                    <li class="active">
                        <img src=""/>
                    </li>
                    <li></li>
                </ul>
            </div>
            <p class="bMg-promptExp">* 选择上面中的一个做为你的照片，或点击"添加"来添加您电脑中的照片来作为您的照片...</p>
            </div>
                <div class="bMg-previewImg">
                    <div class="avatar-preview preview-lg">
                        <img src="" class="bMg-selImg">
                    </div>
                    <div class="bMg-gravityImg">
                        <span >
                            添加
                            <div class="avatar-upload">
                            <input class="avatar-src" name="avatar_src" type="hidden">
                            <input class="avatar-data" name="avatar_data" type="hidden">
                            <input class="avatar-input" id="avatarInput1" name="avatar_file" type="file">
                            </div>
                        </span>
                        <span>删除</span>
                    </div>
                 </div>
            </div>
            <div class="bMg-button clearfix">
                <b>取消</b>
                <b>确认</b>
            </div>
        </div>
    </div>
<%--修改头像部分--%>
<%--
<div class="bMgMask"></div>
--%>
<%--
<div class="bMg-cutPicture">--%>
    <%--<h5 class="clearfix">--%>
        <%--<span class="bMg-changPic">修改头像</span>--%>
        <%--<i class="bMg-closeBtn"></i>--%>
        <%--</h5>--%>
    <%--<p class="bMg-selectImg">使用下列所选照片</p>--%>
    <%--
    <div class="clearfix">--%>
        <%--
        <div class="bMg-changImgSize">--%>
            <%--
            <div class="bMg-ImgContainer">--%>
                <%--
                <canvas id="canvas_bg" width="200" height="200"></canvas>
                --%>
                <%--
                <canvas id="canvas" width="200" height="200"></canvas>
                --%>
                <%--
                <div class="bMg-rotateDrag clearfix">--%>
                    <%--<span></span>--%>
                    <%--<i></i>--%>
                    <%--<strong></strong>--%>
                    <%--
                </div>
                --%>
                <%--
            </div>
            --%>
            <%--
        </div>
        --%>
        <%--
        <div class="bMg-previewImg">--%>
            <%--
            <canvas id="big" width="80" height="80" radius="40"></canvas>
            --%>
            <%--&lt;%&ndash;<img src="css/img/1.jpg" class="bMg-selImg">&ndash;%&gt;--%>
            <%--
            <div class="bMg-gravityImg">--%>
                <%--<span>添加</span>--%>
                <%--<span>删除</span>--%>
                <%--
            </div>
            --%>
            <%--
        </div>
        --%>
        <%--
    </div>
    --%>
    <%--
    <div class="bMg-button clearfix">--%>
        <%--<b>取消</b>--%>
        <%--<b>保存</b>--%>
        <%--
    </div>
    </div>
    <%--修改头像部分--%>
    <%--<div class="bMgMask"></div>--%>
    <%--<div class="bMg-cutPicture">--%>
        <%--<h5 class="clearfix">--%>
            <%--<span class="bMg-changPic">修改头像</span>--%>
            <%--<i class="bMg-closeBtn"></i>--%>
        <%--</h5>--%>
        <%--<p class="bMg-selectImg">使用下列所选照片</p>--%>
        <%--<div class="clearfix">--%>
            <%--<div class="bMg-changImgSize">--%>
                <%--<div class="bMg-ImgContainer">--%>
                    <%--<canvas id="canvas_bg" width="200" height="200"></canvas>--%>
                    <%--<canvas id="canvas" width="200" height="200"></canvas>--%>
                    <%--<div class="bMg-rotateDrag clearfix">--%>
                        <%--<span></span>--%>
                        <%--<i></i>--%>
                        <%--<strong></strong>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</div>--%>
            <%--<div class="bMg-previewImg">--%>
            <%--<canvas id="big" width="80" height="80" radius="40"></canvas>--%>
                <%--&lt;%&ndash;<img src="css/img/1.jpg" class="bMg-selImg">&ndash;%&gt;--%>
                <%--<div class="bMg-gravityImg">--%>
                    <%--<span >添加</span>--%>
                    <%--<span>删除</span>--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</div>--%>
        <%--<div class="bMg-button clearfix">--%>
            <%--<b>取消</b>--%>
            <%--<b>保存</b>--%>
         <%--</div>--%>
    <%--</div>--%>




<div class="WindowMask2">
    <div class="conversWindow groupConvers"><!--//privateConvers-->
        <span class="dialogClose">×</span>
        <div class="dialogHeader">群组管理</div>
        <div class="dialogBody">
            <div class="transferInfo">
                <table class="transferInfoBox">
                    <thead>
                    <tr>
                        <th>群成员</th>
                        <th>职位</th>
                        <th>是否具有管理权限</th>
                        <th>群成员</th>
                    </tr>
                    </thead>
                    <tbody>
                    <%--<tr>--%>
                        <%--<td>--%>
                            <%--<img src="" alt=""/>王二虎--%>
                        <%--</td>--%>
                        <%--<td>产品经理</td>--%>
                        <%--<td>是</td>--%>
                        <%--<td class="operate"><span>转让群</span></td>--%>
                    <%--</tr>--%>
                    <%--<tr>--%>
                        <%--<td>--%>
                            <%--<img src="" alt=""/>王二虎--%>
                        <%--</td>--%>
                        <%--<td>产品经理</td>--%>
                        <%--<td>是</td>--%>
                        <%--<td class="operate"><span>转让群</span></td>--%>
                    <%--</tr>--%>
                    <%--<tr>--%>
                        <%--<td>--%>
                            <%--<img src="" alt=""/>王二虎--%>
                        <%--</td>--%>
                        <%--<td>产品经理</td>--%>
                        <%--<td>是</td>--%>
                        <%--<td class="operate"><span>转让群</span></td>--%>
                    <%--</tr>--%>
                    <%--<tr>--%>
                        <%--<td>--%>
                            <%--<img src="" alt=""/>王二虎--%>
                        <%--</td>--%>
                        <%--<td>产品经理</td>--%>
                        <%--<td>是</td>--%>
                        <%--<td class="operate"><span>转让群</span></td>--%>
                    <%--</tr>--%>
                    </tbody>
                </table>

            </div>
        </div>
        <div class="dialogFooter">
            <input type="button" value="确定" class="manageSure">
            <input type="button" value="取消" class="manageCancle">
        </div>
    </div>


</div>
        <div class="imgLoading"><i></i></div>
</body>
    <script src="<%=request.getContextPath() %>/page/web/js/uploadMethod.js"></script>

    </html>