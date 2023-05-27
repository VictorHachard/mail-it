package com.mailit.controller;

import com.mailit.MailItApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
public class MainController {

    private static final Set<String> authenticatedSessions = new HashSet<>();

    @Value("${logging.file.name}")
    private String file_name;

    @GetMapping("/")
    public String index() {
        String style = "<style>" +
                "html {" +
                    "background-color: #181a1b !important;" +
                    "color: #e8e6e3 !important;" +
                "}" +
                ".center {" +
                    "height: 50%;" +
                    "width: 100%;" +
                    "display: flex;" +
                    "position: fixed;" +
                    "flex-direction: column;" +
                    "align-items: center;" +
                    "justify-content: center;" +
                "}" +
                "</style>";
        return style + "<div class='center'><h1>mail-it</h1></div>";
    }

    @GetMapping("/logs")
    public ResponseEntity<String> getLogs(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Check if the user is already authenticated
        HttpSession session = request.getSession();
        if (authenticatedSessions.contains(session.getId())) {
            // User is already authenticated, proceed with generating and returning the logs page
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);

            return new ResponseEntity<>(this.createLogPage(), headers, HttpStatus.OK);
        }

        // User is not authenticated, display the password form
        String message = StringUtils.isEmpty(request.getParameter("message")) ? "" : request.getParameter("message");
        String formHtml = "<html><head><style>body {font-family: Arial, sans-serif;}</style></head><body><div style=\"width: 300px; margin: 0 auto; padding: 20px; border: 1px solid #ccc; border-radius: 5px;\"><h2>Access Logs</h2>" + "<p style=\"color: red;\">"+ message +"</p>" + "<form method=\"POST\" action=\"/logs\"><label for=\"password\">Password:</label><br><input type=\"password\" id=\"password\" name=\"password\" style=\"width: 100%; padding: 8px 12px; margin-bottom: 10px;\"><br><button type=\"submit\" style=\"padding: 10px 15px; background-color: #4CAF50; color: white; border: none; border-radius: 4px; cursor: pointer;\">Submit</button></form></div></body></html>";
        return ResponseEntity.ok(formHtml);
    }

    @PostMapping("/logs")
    public ResponseEntity<String> submitPassword(@RequestParam("password") String enteredPassword, HttpServletRequest request) {
        if (StringUtils.isEmpty(enteredPassword) || !enteredPassword.equals(MailItApplication.environment.LOGS_PASSWORD)) {
            // Incorrect password, return unauthorized status
            String errorMessage = "Incorrect password";
            return ResponseEntity.status(HttpStatus.SEE_OTHER).header(HttpHeaders.LOCATION, "/logs?message=" + errorMessage).build();
        }

        // Password is correct, mark the session ID as authenticated
        HttpSession session = request.getSession();
        authenticatedSessions.add(session.getId());

        // Redirect the user back to the logs page
        return ResponseEntity.status(HttpStatus.SEE_OTHER).header(HttpHeaders.LOCATION, "/logs").build();
    }

    private String createLogPage() throws IOException {
        Path logFile = Paths.get(this.file_name);
        List<String> allLines = Files.readAllLines(logFile);
        int file_start = Math.max(0, allLines.size() - 500);
        List<String> last500Lines = allLines.subList(file_start, allLines.size());
        Collections.reverse(last500Lines);
        String logs = String.join(System.lineSeparator(), last500Lines);
        // Reverse the order of lines

        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><head><title>Logs</title><style>p {white-space: nowrap; margin: 0;}.DEBUG {color: #006400;}.INFO {color: #0000FF;}.WARN {color: #FFA500;}.ERROR {color: #FF0000;}</style></head><body>");

        htmlBuilder.append("<div><label>Search:</label><input type=\"text\" id=\"searchInput\"></div>");

        htmlBuilder.append("<div style=\"margin: 5px 0;\">");

        Pattern pattern = Pattern.compile("\\b(DEBUG|INFO|WARN|ERROR)\\b");
        int lastEnd = 0;

        String[] lines = logs.split("\n");
        for (String line : lines) {
            Matcher lineMatcher = pattern.matcher(line);
            StringBuilder lineHtml = new StringBuilder();
            while (lineMatcher.find()) {
                int start = lineMatcher.start();
                int end = lineMatcher.end();
                String level = lineMatcher.group(1);
                String colorClass = level.toUpperCase();
                lineHtml.append(line, lastEnd, start);
                lineHtml.append("<span class=\"").append(colorClass).append("\">[").append(level).append("]</span>");
                lastEnd = end;
            }
            lineHtml.append(line.substring(lastEnd));
            lastEnd = 0;
            htmlBuilder.append("<p>").append(lineHtml.toString()).append("</p>\n");
        }

        htmlBuilder.append("</div>");

        htmlBuilder.append("<script>\n" +
                "    const searchInput = document.getElementById('searchInput');\n" +
                "    const logs = document.querySelectorAll('div p');\n" +
                "    const clearBtn = document.getElementById('clear-logs');\n" +
                "\n" +
                "    searchInput.addEventListener('input', filterLogs);\n" +
                "\n" +
                "    function filterLogs() {\n" +
                "        const filterValue = searchInput.value.toLowerCase();\n" +
                "        logs.forEach(log => {\n" +
                "            const logText = log.innerText.toLowerCase();\n" +
                "            if (logText.includes(filterValue)) {\n" +
                "                log.style.display = 'block';\n" +
                "            } else {\n" +
                "                log.style.display = 'none';\n" +
                "            }\n" +
                "        });\n" +
                "    }\n" +
                "</script>");

        htmlBuilder.append("</body></html>");

        return htmlBuilder.toString();
    }

}
