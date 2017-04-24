<%@ page language="java" contentType="text/html; charset=utf-8"	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/html">
<head lang="en">
    <meta charset="UTF-8">
    <!--<meta name="viewport" content="width=device-width, initial-scale=1, minimum-scale=1, maximum-scale=1, user-scalable=no">-->
    <title></title>
    <!--<link rel="stylesheet" href="<%=request.getContextPath() %>/page/web/css/authorize.css"/>-->
    <script src="<%=request.getContextPath() %>/page/web/js/jquery-2.1.1.min.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/config.js"></script>
    <script src="<%=request.getContextPath() %>/page/web/js/deng.js"></script>
    <!--<script src="<%=request.getContextPath() %>/page/web/js/authorize.js"></script>-->
    <style>
        .jumoToAuth{
            width: 360px;
            height: 46px;
            background: #30c0da !important;
            color: white !important;
            margin-top: 50px;
            border: 1px solid #30c0da;
            border-radius: 4px;
            text-align: center;
        }
    </style>
</head>
<body>
    <input class="jumoToAuth" type="text" value="页面跳转到授权" onclick="jumoToAuth()"/>
</body>
<script>


</script>
</html>