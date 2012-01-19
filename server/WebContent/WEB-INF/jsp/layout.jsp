<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
	
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/style.css" />
	<title><tiles:insertAttribute name="title" ignore="true" /></title>
	
	<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
	
</head>
<body>
<div id="center">
	<div id="header"><tiles:insertAttribute name="header" /></div>
	<div id="main">
		<div id="nav"><tiles:insertAttribute name="nav" /></div>
		<div id="content"><tiles:insertAttribute name="body" /></div>
	</div>
	<div id="footer"><tiles:insertAttribute name="footer" /></div>
</div>
</body>
</html>

