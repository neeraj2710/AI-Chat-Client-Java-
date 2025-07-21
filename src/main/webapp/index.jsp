<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="org.json.JSONArray" %>
<html>
<head>
    <title>Java Perplexity Chat</title>
    <style>
        body { font-family: Arial; background: #f6f6f6; }
        #chatbox { background: #fff; max-width: 500px; margin: 40px auto; padding: 20px; border-radius: 8px; box-shadow: 0 0 8px #ccc;}
        .me {color: #005AC0;}
        .ai {color: #222; background: #eaeaea; padding:2px 6px; border-radius: 5px; }
        .msg {margin: 6px 0;}
    </style>
</head>
<body>
<div id="chatbox">
    <h2>PerplexityAI Java Chat</h2>
    <form method="post" action="chat">
        <input style="width:340px;" type="text" name="message" autofocus/>
        <input type="submit" value="Send"/>
    </form>
    <hr/>
    <div>
        <%
            List<JSONObject> chatHistory = (List<JSONObject>) session.getAttribute("chatHistory");
            if (chatHistory!=null) {
                for (JSONObject msg : chatHistory) {
                    String role = msg.getString("role");
                    String content = msg.getString("content");
        %>
        <div class="msg <%=role%>">
            <b><%=role.equals("user") ? "You" : "AI"%>:</b> <span><%=content.replaceAll("\n", "<br/>")%></span>
        </div>
        <%
                }
            }
        %>
    </div>
</div>
</body>
</html>
