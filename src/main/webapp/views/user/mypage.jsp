<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
</head>
<c:import url="/header" />
<body>
	<c:if test="${empty user}">
		<c:redirect url="login" />
	</c:if>
	<section id="root">
		<h1>${user.name}님 환영합니다!</h1>
		<button onclick="location.href='/updateUserForm'">회원정보 수정</button>
		<button onclick="location.href='//deleteUser'">회원정보 탈퇴</button>
	</section>
</body>
<c:import url="/footer" />
</html>