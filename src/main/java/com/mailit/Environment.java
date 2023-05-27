package com.mailit;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.mailit.utils.JsonUtils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import org.springframework.core.io.ClassPathResource;

public class Environment {

    /**
     * This variable is a List of String values that represents the URLs that are allowed to access the server.
     * This is used to implement Cross-Origin Resource Sharing (CORS) and prevent unauthorized access to the server.
     */
    public final List<String> ACCESS_CONTROL_ALLOW_ORIGIN_URL;

    /**
     * This variable enable the simplified user agent string that is used to identify the client or browser.
     */
    public final Boolean USER_AGENT_SIMPLIFIED;

    /**
     * This variable is a string that represents the password used to access /logs.
     */
    public final String LOGS_PASSWORD;

    /**
     * This variable is a string that represents the email account that will be used to send the email message.
     */
    public final String EMAIL_USERNAME;

    /**
     * This variable is a string that represents the password for the email account that will be used to send the email
     * message.
     */
    public final String EMAIL_PASSWORD;

    /**
     * This variable is a Map of String key-value pairs that represents a mapping from aliases to real email addresses.
     * This allows the sender to use a short, easy-to-remember alias.
     */
    public final Map<String, String> ALIAS;

    /**
     * This variable is a string that represents the CSS styles that should be applied to the email message.
     * The styles will be included inline in the email message, allowing the email to be displayed with the specified
     * styles without the need for the recipient to load external stylesheets.
     */
    public final String STYLE_CSS;

    public Environment(String file) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(file));
        JSONObject jo = (JSONObject) obj;

        List<String> access_control_allow_origin_url = JsonUtils.toList((JSONArray) jo.get("access_control_allow_origin_url"));
        Map<String, String> alias = JsonUtils.toMap((JSONObject) jo.get("alias"));

        InputStream css_resource = new ClassPathResource("static/css/style.css").getInputStream();

        Scanner scanner = new Scanner(css_resource).useDelimiter("\\A");
        String result = scanner.hasNext() ? scanner.next() : "";
        result = result.replace("\n", "");

        ACCESS_CONTROL_ALLOW_ORIGIN_URL = access_control_allow_origin_url;
        USER_AGENT_SIMPLIFIED = jo.get("user_agent_simplified") == null || (Boolean) jo.get("user_agent_simplified");
        EMAIL_USERNAME = (String) jo.get("email_username");
        EMAIL_PASSWORD = (String) jo.get("email_password");
        LOGS_PASSWORD = jo.get("user_agent_simplified") != null ? (String) jo.get("logs_password") : "admin";
        ALIAS = alias;
        STYLE_CSS = result;
    }

}
