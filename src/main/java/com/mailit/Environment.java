package com.mailit;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Environment {

    public final List<String> ACCESS_CONTROL_ALLOW_ORIGIN_URL;
    public final Boolean USER_AGENT_SIMPLIFIED;
    public final String EMAIL_USERNAME;
    public final String EMAIL_PASSWORD;
    public final Map<String, String> ALIAS;
    public final String STYLE_CSS;

    public Environment() throws IOException {
        String originsEnv = System.getenv("CORS_ALLOWED_ORIGINS");
        ACCESS_CONTROL_ALLOW_ORIGIN_URL = (originsEnv != null && !originsEnv.isBlank())
                ? Arrays.asList(originsEnv.split(","))
                : Collections.emptyList();

        String userAgentEnv = System.getenv("USER_AGENT_SIMPLIFIED");
        USER_AGENT_SIMPLIFIED = userAgentEnv == null || Boolean.parseBoolean(userAgentEnv);

        EMAIL_USERNAME = System.getenv("EMAIL_USERNAME");
        EMAIL_PASSWORD = System.getenv("EMAIL_PASSWORD");

        Map<String, String> alias = new HashMap<>();
        String aliasEnv = System.getenv("ALIAS");
        if (aliasEnv != null && !aliasEnv.isBlank()) {
            for (String pair : aliasEnv.split(",")) {
                String[] parts = pair.split("=", 2);
                if (parts.length == 2) {
                    alias.put(parts[0].trim(), parts[1].trim());
                }
            }
        }
        ALIAS = Collections.unmodifiableMap(alias);

        InputStream cssResource = new ClassPathResource("static/css/style.css").getInputStream();
        Scanner scanner = new Scanner(cssResource).useDelimiter("\\A");
        String result = scanner.hasNext() ? scanner.next() : "";
        scanner.close();
        STYLE_CSS = result.replace("\n", "");
    }
}
