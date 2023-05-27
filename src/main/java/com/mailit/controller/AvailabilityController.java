package com.mailit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class AvailabilityController {

    @ResponseBody
    @GetMapping("/availability")
    public String checkAvailability() {
        return "{\"result\": \"success\"," +
                "\"description\": \"Service is available\"" +
                "}";
    }

}
