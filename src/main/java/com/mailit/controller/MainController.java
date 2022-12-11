package com.mailit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class MainController {

    @ResponseBody
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

}
