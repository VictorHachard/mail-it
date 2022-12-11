package com.mailthis.validator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

// Lombok
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class EmailValidator implements Serializable {

    /**
     * This variable is a string that represents the email address of the sender and any replies to the message
     * should be sent to.
     */
    String replyTo;

    /**
     * This variable is a string that represents the personal name of the sender.
     */
    String fromName;

    /**
     * This variable is a string that represents the body of the email message.
     */
    String message;

    /**
     * This variable is a string that represents the subject of the email message.
     */
    String subject;

    /**
     * This variable is a string that represents the personal name of the application that send the message.
     */
    String fromApplication;

    /**
     * This variable is a string that represents a footer to be added to the email message.
     */
    String footer;

    /**
     * This variable is a boolean value that specifies whether a line break should be added to the email message by
     * replacing the newline character (\n) with the HTML line break tag (<br>).
     *
     * If true, the \n character in the message string will be replaced with <br>, which will cause the message
     * to be displayed with a line break in the email client.
     * If false, no replacement will be made and the message string will be displayed as-is.
     *
     * The default value is true.
     */
    Boolean replaceMessageBreak = true;

    /**
     * This variable is an array of MultipartFile objects that represent any files that are to be included as
     * attachments in the email message.
     * Each MultipartFile object in the array contains information about a single file, such as its name and contents.
     */
    MultipartFile[] files;

}
