package com.mailit;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class MailItApplication {

    private static final Logger log = LoggerFactory.getLogger(MailItApplication.class);

    /**
     * This variable is an instance of the RunEnum enum that represents the current mode of the program.
     * The RunEnum enum has two possible values: PRODUCTION and DEVELOPMENT.
     * If runEnum is set to PRODUCTION, the program is running in production mode.
     * If runEnum is set to DEVELOPMENT, the program is running in development mode.
     * The default value is PRODUCTION.
     */
    public static RunEnum runEnum = RunEnum.PRODUCTION;

    /**
     * This variable is an instance of the Environment class that represents the current runtime environment of the
     * program.
     */
    public static Environment environment;

    /**
     * args need to be like -run dev -file <path_to_config>
     * @param args String[]
     */
    public static void main(String[] args) throws IOException, ParseException {
        log.info("mail-it v1.0");

        runEnum = RunEnum.PRODUCTION;
        List<String> required_args = new ArrayList<>(Arrays.asList(
                "-file"
        ));
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("-run") && args[i + 1].equals("dev")) {
                runEnum = RunEnum.DEVELOPMENT;
            } else if (args[i].equals("-file") && args[i + 1] != null) {
                environment = new Environment(args[i + 1]);
            }
            required_args.remove(args[i]);
        }

        if (required_args.size() == 1) {
            throw new IllegalArgumentException("The following argument is missing " + required_args.toString());
        } else if (required_args.size() >= 1) {
            throw new IllegalArgumentException("The following arguments is missing " + required_args.toString());
        }

        if (runEnum == RunEnum.PRODUCTION && environment.ACCESS_CONTROL_ALLOW_ORIGIN_URL.isEmpty()) {
            throw new IllegalArgumentException("In production the access_control_allow_origin_url cannot be empty");
        }

        log.info("Starting in " + runEnum.name() + " mode");
        SpringApplication.run(MailItApplication.class, args);
    }

}
