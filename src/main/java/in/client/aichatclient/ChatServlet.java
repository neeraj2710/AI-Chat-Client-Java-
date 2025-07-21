package in.client.aichatclient; // adjust to your package

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.util.*;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {
    private static final String API_KEY = "YOUR_API_KEY"; // <-- Use your key

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Just forward to JSP
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        List<JSONObject> chatHistory = (List<JSONObject>) session.getAttribute("chatHistory");
        if (chatHistory == null) {
            chatHistory = new ArrayList<>();
        }

        String userInput = request.getParameter("message");
        if (userInput!=null && !userInput.trim().isEmpty()) {
            // Add user message to history
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userInput);
            chatHistory.add(userMsg);

            // Send to Perplexity API
            try {
                URL url = new URL("https://api.perplexity.ai/chat/completions");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
                conn.setDoOutput(true);

                JSONObject payload = new JSONObject();
                payload.put("model", "sonar-pro");
                payload.put("messages", new JSONArray(chatHistory));

                OutputStream os = conn.getOutputStream();
                os.write(payload.toString().getBytes("UTF-8"));
                os.close();

                // Read response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder resp = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    resp.append(line.trim());
                }
                in.close();

                // Parse
                JSONObject jsonResponse = new JSONObject(resp.toString());
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices.length() > 0) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject aiMsg = firstChoice.getJSONObject("message");
                    // Add AI reply to history
                    chatHistory.add(aiMsg);
                } else {
                    //API Error, add notice
                    JSONObject errorMsg = new JSONObject();
                    errorMsg.put("role", "assistant");
                    errorMsg.put("content", "No reply found in API response.");
                    chatHistory.add(errorMsg);
                }
            } catch (Exception e) {
                JSONObject errMsg = new JSONObject();
                errMsg.put("role", "assistant");
                errMsg.put("content", "Error: "+e.getMessage());
                chatHistory.add(errMsg);
            }
        }
        // Save updated history and reload page
        session.setAttribute("chatHistory", chatHistory);
        response.sendRedirect("chat");
    }
}
