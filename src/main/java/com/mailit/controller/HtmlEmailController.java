package com.mailit.controller;

import com.mailit.MailItApplication;
import com.mailit.validator.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class HtmlEmailController {

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    private final Logger log = LoggerFactory.getLogger(HtmlEmailController.class);

    @Autowired
    private JavaMailSender emailSender;

    private Map<String, String> analyzeUserAgent(String userAgent) {
        Map<String, String> userInfo = new LinkedHashMap<>();

        // Extract browser information
        Pattern browserPattern = Pattern.compile(".*?(Chrome|Firefox|Safari).*?/(\\d+\\.\\d+).*");
        Matcher browserMatcher = browserPattern.matcher(userAgent);
        if (browserMatcher.matches()) {
            String browserName = browserMatcher.group(1);
            String browserVersion = browserMatcher.group(2);
            userInfo.put("browser", browserName);
            userInfo.put("version", browserVersion);
        }

        // Extract operating system information
        Pattern osPattern = Pattern.compile(".*?\\((.*?)\\).*");
        Matcher osMatcher = osPattern.matcher(userAgent);
        if (osMatcher.matches()) {
            String osInfo = osMatcher.group(1);
            userInfo.put("operating system", osInfo);
        }

        // Extract device information
        Pattern devicePattern = Pattern.compile(".*?\\((.*?)\\).*");
        Matcher deviceMatcher = devicePattern.matcher(userAgent);
        if (deviceMatcher.matches()) {
            String deviceInfo = deviceMatcher.group(1);
            userInfo.put("device", deviceInfo);
        }

        return userInfo;
    }

    private static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }

    private void checkRequired(EmailValidator validator) {
        if (validator.getReplyTo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "replyTo is required");
        }
        if (validator.getMessage() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message is required");
        }
    }

    private void checkEmail(Map<String, String> emailMap, EmailValidator validator) {
        for (String email : emailMap.values()) {
            if (!isValidEmailAddress(email)) {
                String msg = emailMap.size() > 1 ? "Destination emails are not valid" : "Destination email is not valid";
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
            }
        }
        if (!isValidEmailAddress(validator.getReplyTo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reply to email is not valid");
        }
    }

    /**
     *
     * The method sends an email message to the specified email address(es) using the provided email parameters.
     * It first performs some validation on the input parameters and the email addresses.
     * It then constructs the email message by combining the provided message with a footer and any attached files.
     * Finally, it sends the email message using the emailSender object, which is an instance of the JavaMailSender
     * interface.
     *
     * The method returns a JSON string containing the result of the email sending operation.
     *
     * @param validator An object containing the email parameters, constructed from the request parameters.
     * @param emailTo The email address(es) to send the email message to.
     * @param request The HTTP request object, used to access request headers.
     *
     * @return A JSON string containing the result of the email sending operation.
     *
     * @throws MessagingException If an error occurs while sending the email message.
     * @throws IOException If an error occurs while reading the attached files.
     */
    @ResponseBody
    @PostMapping("/{email}")
    public String sendSimpleMessage(@ModelAttribute EmailValidator validator,
                                    @PathVariable("email") String emailTo,
                                    HttpServletRequest request)
            throws MessagingException, IOException {
        emailTo = emailTo.replaceAll("\\s+","");

        log.info("Request received " + emailTo);

        // Required
        checkRequired(validator);

        Map<String, String> emailMap = new HashMap<>();
        for (String email: emailTo.split(",")) {
            emailMap.put(email, MailItApplication.environment.ALIAS.getOrDefault(email, email));
        }

        // Check Emails
        checkEmail(emailMap, validator);

        // Values
        String fromPersonal = validator.getFromApplication() != null ? "[" + validator.getFromApplication() + "]" : "[mail-it]";
        if (validator.getFromName() != null) {
            fromPersonal += " " + validator.getFromName();
        }
        String subject = validator.getSubject() != null ? validator.getSubject() : "No Subject";
        String htmlMessage = validator.getMessage();
        if (validator.getReplaceMessageBreak()) {
            htmlMessage = htmlMessage.replace("\n", "<br>");
        }
        String footer;
        if (validator.getFooter() != null) {
            footer = validator.getFooter();
        } else {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss XXX", Locale.getDefault());
            footer = "<div class='msg_footer'>" +
                        "<hr class='msg_footer_line'>" +
                         "<ul class='msg_footer_info'>" +
                             "<li><span>from: </span><em>" + validator.getReplyTo() + "</em></li>" +
                             "<li><span>date: </span><em>" + (df.format(new Date())) + "</em></li>";
            if (request.getHeader(HttpHeaders.ORIGIN) != null) {
                footer += "<li><span>domain: </span><em>" + request.getHeader(HttpHeaders.ORIGIN) + "</em></li>";
            }
            if (request.getHeader(HttpHeaders.USER_AGENT) != null) {
                if (MailItApplication.environment.USER_AGENT_SIMPLIFIED) {
                    StringBuilder userInfo = new StringBuilder();
                    Map<String, String> analysedUserInfo = this.analyzeUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
                    for (Map.Entry<String, String> entry : analysedUserInfo.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        userInfo.append("<li><span>").append(key).append(": </span><em>").append(value).append("</em></li>");
                    }
                    footer += userInfo;
                } else {
                    footer += "<li><span>user agent: </span><em>" + request.getHeader(HttpHeaders.USER_AGENT) + "</em></li>";
                }
            }
            footer += "</ul><p class='sent_with'>Sent with &#10084;</p></div>";
        }
        Map<String, ByteArrayDataSource> fileMap = new HashMap<>();
        if (validator.getFiles() != null) {
            for (MultipartFile file: validator.getFiles()) {
                fileMap.put(Objects.requireNonNull(file.getOriginalFilename()),
                        new ByteArrayDataSource(file.getInputStream(), "application/octet-stream"));
            }
        }

        List<String> res = new ArrayList<>();

        for (Map.Entry<String, String> to: emailMap.entrySet()) {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            String allMessage = "<div class='mailit_msg'>" + htmlMessage + footer + "</div>";
            helper.setText("<style>" + MailItApplication.environment.STYLE_CSS + "</style>" + allMessage, true);

            if (!fileMap.isEmpty()) {
                for (Map.Entry<String, ByteArrayDataSource> file: fileMap.entrySet()) {
                    helper.addAttachment(file.getKey(), file.getValue());
                }
            }

            helper.setTo(to.getValue());
            helper.setSubject(subject);
            helper.setReplyTo(validator.getReplyTo());
            if (fromPersonal.isEmpty()) {
                helper.setFrom("noreply@victorhachard.fr");
            } else {
                helper.setFrom("noreply@victorhachard.fr", fromPersonal);
            }

            this.emailSender.send(message);
            res.add("'" + to.getKey() + "'");  // Hack to have quote in the jsonRes
        }
        String jsonRes = "{\"result\": \"success\"," +
                "\"email_to_send_count\": " + emailMap.size() + "," +
                "\"email_sent_count\": " + res.size() + "," +
                "\"email_sent\": " + res.toString().replace("'", "\"") +
                "}";
        return jsonRes;
    }

}
