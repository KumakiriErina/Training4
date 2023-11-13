<%@page import="Training4.Omikuji"%>
<%@page import="dao.OmikujiDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
	//リクエストスコープから値を取得
	Omikuji omikujiDao = (Omikuji)request.getAttribute("omikuji");
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>占いの結果</title>
</head>
<body>
	<table border = "1">
		<tr>
			<th colspan="2">今日の運勢は<font color = "#ba55d3"><%= omikujiDao.getUnsei() %></font>です</th>
		</tr>
		<tr>
			<td>願い事</td><td><%= omikujiDao.getNegaigoto()%></td>
		</tr>
		<tr>
			<td>商い</td><td><%= omikujiDao.getAkinai()%></td>
		</tr>
		<tr>
			<td>学問</td><td><%= omikujiDao.getGakumon()%></td>
		</tr>
	</table>
</body>
</html>