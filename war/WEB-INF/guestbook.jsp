<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>

<html>
    <body>

    <c:choose>
	   <c:when test="${nickname != null}">
		  <p>Hello, ${nickname}! (You can <a href="${logoutUrl}">sign
		  out</a>.)</p>
	   </c:when>

        <c:otherwise>
		  <p>Hello! <a href="${loginUrl}">Sign in</a> to include your name
		  with greetings you post.</p>
	   </c:otherwise>
    </c:choose>


    <form action="/sign" method="post">
        <div><textarea name="content" rows="3" cols="60"></textarea></div>
        <div><input type="submit" value="Post Greeting" /></div>
    </form>

    <c:choose>
        <c:when test="${fn:length(greetings) > 0}">
            <c:forEach var="greeting" items="${greetings}">
              <c:choose>
                <c:when test="${greeting.author.nickname != null}">
                  <p><b>${greeting.author.nickname}</b> wrote:</p>
                </c:when>
                <c:otherwise>
                  <p>An anonymous person wrote:</p>
                </c:otherwise>
               </c:choose>
                <blockquote>${greeting.content}</blockquote>
            </c:forEach>
        </c:when>

        <c:otherwise>
            There are no guestbook entries. You are unloved!
        </c:otherwise>
    </c:choose>


    <c:if test="${nextCursor != null}">
	   <a href="/view?page=${nextCursor}">Next Page</a>
    </c:if>

    </body>
</html>