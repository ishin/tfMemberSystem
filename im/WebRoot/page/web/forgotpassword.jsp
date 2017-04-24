<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/html">
<head lang="en">
    <meta charset="UTF-8">
    <title></title>
    <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/signin.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/window.css"/>

    <script src="<%=request.getContextPath() %>/page/web/js/jquery-2.1.1.min.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/signin.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/md5.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/window.js"></script>


    </head>
<body>
<div class="signin-bg">

    <div class="resetBox sealtalk-forgetpassword">
        <span style="position: absolute;right: 50px;top: 35px;"><a style="text-decoration: underline;color: white;" ui-sref="account.signin" href="#/account/signin">立即登录</a></span>
        <div class="resetInner">
            <div class="resetStep step1 ">
                <h2>重置密码</h2>
                <div class="step1Img"></div>
                <div name="formFogot" class="form-inline" step="resetStep-1">
                    <div class="">
                        <span class="textTitle">手机号码：</span>
                        <input type="text" id="phoneNum" name="phone" ng-model="user.phone" required="" phone-registered="" ng-disabled="codeloading" class="phoneNum ng-pristine ng-untouched ng-valid-phoneformat ng-invalid ng-invalid-required" placeholder="手机号" my-focus="">
                        <p class="error-block ng-hide" ng-show="(formFogot.phone.$dirty||formFogot.submitted)&amp;&amp;formFogot.phone.$error.required&amp;&amp;!formFogot.phone.$focused">不可以为空</p>
                        <p class="error-block ng-hide" ng-show="formFogot.phone.$dirty&amp;&amp;formFogot.phone.$error.phoneformat&amp;&amp;!formFogot.phone.$focused">手机号格式不正确</p>
                        <p class="error-block ng-hide" ng-show="formFogot.phone.$dirty&amp;&amp;formFogot.phone.$error.uniquephone&amp;&amp;!formFogot.phone.$focused">手机号未注册</p>
                        <p class="SendCheakCode">发送验证码</p>
                    </div>
                    <div class="" style="margin-top: 30px">
                        <div class="msgCode clearfix">
                            <span class="textTitle">验证码：</span>
                            <input type="text" name="code" ng-model="user.code" required="" class="msgCode ng-pristine ng-untouched ng-invalid ng-invalid-required" id="checkCode" placeholder="短信验证码" my-focus="">
                            <!--<send-code-button loading="codeloading" phone="user.phone" available="formFogot.phone.$dirty&amp;&amp;formFogot.phone.$valid" class="ng-isolate-scope"><span class="sendCode" ng-show="codeTime==0" ng-click="sendCode()"><a href="javascript:void 0">发送验证码</a></span><span class="sec ng-binding ng-hide" ng-show="codeTime>0">0 s</span></send-code-button>-->
                            <p class="error-block ng-hide" ng-show="(formFogot.code.$dirty||formFogot.submitted)&amp;&amp;formFogot.code.$error.required&amp;&amp;!formFogot.phone.$focused">不可以为空</p>
                        </div>
                    </div>
                    <p class="error-block ng-hide" ng-show="!formFogot.code.$error.required&amp;&amp;codeIsError&amp;&amp;!formFogot.phone.$focused">短信验证码错误</p>
                    <div class="button-wrapper form-group">
                        <button class="sign-button step1Btn" class="prevfun" onclick="fBackToSignin()">上一步</button>
                        <button class="sign-button step1Btn" class="nextfun" bindData="" onclick="fToStep2(this)">下一步</button>
                    </div>
                </div>
            </div>
            <div class=" resetStep step2 chatHide">
                <h2>重置密码</h2>
                <div class="step2Img"></div>
                <div name="formResetPwd" class="form-inline" step="resetStep-2" novalidate="novalidate">
                    <div class="">
                        <span class="textTitle">新密码：</span>
                        <input type="password" name="newpassword" class=" pwd"
                               placeholder="新密码（密码由 6-16 个字符组成）" id="newpassword">
                        <!--<p class="error-block" ng-show="(formResetPwd.newpassword.$dirty||formResetPwd.submitted)&&formResetPwd.newpassword.$invalid&&!formResetPwd.newpassword.$focused">6-16 位的字母、数字或 “_”</p>-->
                    </div>
                    <div class="" style="margin-top: 30px">
                        <span class="textTitle">确认新密码：</span>
                        <input type="password" name="repassword"class=" repwd" placeholder="再次输入" id="newpasswordCertain">
                        <!--<p class="error-block" ng-show="(formResetPwd.repassword.$dirty||formResetPwd.submitted)&&formResetPwd.repassword.$error.required&&!formResetPwd.repassword.$focused">不可以为空</p>-->
                        <!--<p class="error-block" ng-show="formResetPwd.repassword.$dirty&&formResetPwd.repassword.$error.pwmatch&&!formResetPwd.repassword.$focused">两次输入不一致</p>-->
                    </div>
                    <div class="button-wrapper form-group">
                        <!--<button class="sign-button step2Btn" ng-click="submit()" type="submit">完成</button>-->
                        <button class="sign-button step1Btn"  onclick="fToPrev(this)">上一步</button>

                        <button class="sign-button step1Btn"  onclick="fToStep3(this)">下一步</button>
                    </div>
                </div>
            </div>
            <div class="resetStep step3 chatHide">
                <h2>重置密码</h2>
                <div class="step3Img"></div>
                <div class="form-inline"step="resetStep-3" style="margin: 0 auto">


                    <div class="finishReset">
                        <a class="finishResetImg"></a>
                        <p class="finishResetText">密码修改成功！</p>
                    </div>
                    <div class="button-wrapper form-group">
                        <button class="sign-button step1Btn" onclick="fBackToSignin()">返回登录</button>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>

</body>
</html>