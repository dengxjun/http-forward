<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
<style type="text/css">
	td{border:solid #add9c0; border-width:0px 1px 1px 0px; padding:10px 0px;}
	table{border:solid #add9c0; border-width:1px 0px 0px 1px;}
</style>
</head>
<body>
<div>
	<table >
		<tr>
			<td width="20%">Server Name</td>
			<td width="30%">Base URL</td>
			<td width="10%">Status</td>
			<td width="30%">Operation</td>
		</tr>
		<c:forEach items="${servers }" var="s">
			<tr>
				<td>${s.name }</td>
				<td>${s.baseUrl }</td>
				<td>${s.status }</td>
				<td>
					<c:if test="${s.status eq 'on' }">
						<a href="proxyServer?serverName=${s.name }&operation_type=off">stop</a>
					</c:if>
					<c:if test="${s.status eq 'off' }">
						<a href="proxyServer?serverName=${s.name }&operation_type=on" disabled>start</a>
					</c:if>
				</td>
			</tr>
		</c:forEach>
	</table>
</div>
</body>
</html>