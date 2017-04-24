<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/html">
<head lang="en">
<meta charset="UTF-8">
<title></title>
<link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/uploadImg/style.css"/>
<link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/bootstrap.min.css"/>
<link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/cropper.min.css"/>
</head>
<body>
	<h1>测试获取地图位置</h1>
	<form action="<%=request.getContextPath() %>/map!getPosition" method="post">
		userid: <input type="text" name="userid" />
		targetid: <input type="text" name="targetid" />
		type: <input type="text" name="type" />
		<input type="submit" value="test" />
	</form>
	<h1>测试上传非裁剪头像</h1>
	<form action="<%=request.getContextPath() %>/upload!uploadUserLogoNotCut" enctype="multipart/form-data" method="post">
		userId: <input name="userid" type="text" value="1" />
		Image: <input name="file" type="file" accept="image/gif, image/jpeg"/>
 		<input name="upload" type="submit" value="上传" />
	</form>
	<h1>测试app免登陆接</h1>
	<form action="<%=request.getContextPath() %>/system!freeLandingForApp" method="post">
		Token: <input type="text" name="token" value="" />
		<input type="submit" value="submit" />
	</form>
	<h1>测试Web端个人设置接口</h1>
	<form action="<%=request.getContextPath() %>/member!updateMemberInfoForWeb" method="post">
		userid: <input type="text" name="userid" value="" />
		position: <input type="text" name="position" value="" />
		fullname: <input type="text" name="fullname" value="" />
		sex: <input type="text" name="sex" value="" />
		email: <input type="text" name="email" value="" />
		phone: <input type="text" name="phone" value="" />
		sign: <input type="text" name="sign" value="" />
		<input type="submit" value="submit" />
	</form>
	<h1>测试对讲支持</h1>
	<form action="<%=request.getLocalAddr() %>:<%=request.getLocalPort() %>/rce/restapi/ptt" method="post">
		<input type="submit" value="submit" />
	</form>
	<h1>测试oauth2<h1>
	设置appId,secret,url<br />
	<form action="<%=request.getContextPath() %>/auth!setAppIDAndSecretAndUrl" method="post">
		<input type="text" name="appId" value="" />
		<input type="text" name="secret" value="" />
		<input type="text" name="url" value="http://localhost:8080/sealtalk/" />
		<input type="submit" value="submit" />
	</form><br />
	登陆按钮<br />
	
</body>
<html/>