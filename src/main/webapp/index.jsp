<%@page import="boardServer.user.UserDao"%>
<%@page import="boardServer.user.UserResponseDto"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
</head>
<jsp:include page="/header"></jsp:include>
<body>
	<section id="root">
	<%
	// 내장 객체의 유효기간 (Scope)
	pageContext.setAttribute("test", "page data"); // 페이지를 벗어나서 null
	request.setAttribute("test", "request data");
	session.setAttribute("test", "session data");
	application.setAttribute("test", "application data");
	
	// 페이지 이동처리 1)
	// 단순 페이지 이동 처리 (url의 변화 O)
	// response.sendRedirect("test.jsp");
	
	// 페이지 이동처리 2)
	// ㄴ request & response 객체를 전달하면서 이동 처리 (url의 변화 X)
	request.getRequestDispatcher("test1.jsp").forward(request, response);
	%>
	</section>
</body>
<jsp:include page="/footer"></jsp:include>
</html>