package com.mailit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@SpringBootApplication
public class MailItApplication {

    private static final Logger log = LoggerFactory.getLogger(MailItApplication.class);

    public static RunEnum runEnum = RunEnum.PRODUCTION;
    public static Environment environment;

    private static void logVersion() {
        try {
            String pomContent = new String(Files.readAllBytes(new File("pom.xml").toPath()));
            String versionStartTag = "<version>";
            String versionEndTag = "</version>";
            int startIndex = pomContent.indexOf("<artifactId>mail-it</artifactId>");
            if (startIndex != -1) {
                startIndex = pomContent.indexOf(versionStartTag, startIndex) + versionStartTag.length();
                int endIndex = pomContent.indexOf(versionEndTag, startIndex);
                log.info("mail-it version: " + pomContent.substring(startIndex, endIndex));
            }
        } catch (IOException e) {
            // pom.xml not available at runtime (e.g. in Docker), skip
        }
    }

    public static void main(String[] args) throws IOException {
        logVersion();

        String runMode = System.getenv("RUN_MODE");
        runEnum = "dev".equalsIgnoreCase(runMode) ? RunEnum.DEVELOPMENT : RunEnum.PRODUCTION;
        environment = new Environment();

        if (runEnum == RunEnum.PRODUCTION && environment.ACCESS_CONTROL_ALLOW_ORIGIN_URL.isEmpty()) {
            throw new IllegalArgumentException("In production the CORS_ALLOWED_ORIGINS env var cannot be empty");
        }

        log.info("Starting in " + runEnum.name() + " mode");
        SpringApplication.run(MailItApplication.class, args);
    }
}
